package com.example.salary_app.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.salary_app.Salary;
import com.example.salary_app.Shift;
import com.example.salary_app.Workplace;
import com.example.salary_app.repository.SalaryRepository;
import com.example.salary_app.repository.ShiftRepository;
import com.example.salary_app.repository.WorkplaceRepository;
import com.example.salary_app.service.WageCalculator;

@Controller
public class SalaryController {

    private final ShiftRepository shiftRepository;
    private final WorkplaceRepository workplaceRepository;
    private final SalaryRepository salaryRepository;
    private final WageCalculator wageCalculator;

    public SalaryController(
            ShiftRepository shiftRepository,
            WorkplaceRepository workplaceRepository,
            SalaryRepository salaryRepository,
            WageCalculator wageCalculator) {

        this.shiftRepository = shiftRepository;
        this.workplaceRepository = workplaceRepository;
        this.salaryRepository = salaryRepository;
        this.wageCalculator = wageCalculator;
    }

    @GetMapping("/salary")
    public String salary(
            @RequestParam(defaultValue = "2026") int year,
            @RequestParam(defaultValue = "10") int month,
            Model model) {

        // 存在しない年月が指定された場合はエラーにする
        if (month < 1 || month > 12) {
            return "redirect:/salary";
        }

        List<Shift> shifts = shiftRepository.findAll();
        List<Workplace> workplaces = workplaceRepository.findAll();

        long totalMinutes = 0;
        long totalWage = 0;
        long totalActualAmount = 0;

        Map<Long, Long> workplaceMinutes = new HashMap<>();
        Map<Long, Long> workplaceWages = new HashMap<>();
        Map<Long, Long> workplaceLessons = new HashMap<>();

        for (Shift shift : shifts) {

            LocalDate workDate = shift.getWorkDate();

            if (workDate == null
                    || workDate.getYear() != year
                    || workDate.getMonthValue() != month) {
                continue;
            }

            Long workplaceId = shift.getWorkplaceId();

            long workingMinutes = 0;
            long wage = 0;

            if ("PER_LESSON".equals(shift.getPayType())) {

                // コマ制：コマ数 × 1コマあたりの給料
                int lessonCount = shift.getLessonCount() == null
                        ? 0
                        : shift.getLessonCount();

                wage = (long) shift.getPremiumWage() * 0
                        + (long) lessonCount * getLessonWage(
                                workplaceId, workplaces);

                workplaceLessons.put(
                        workplaceId,
                        workplaceLessons.getOrDefault(workplaceId, 0L)
                                + lessonCount
                );

            } else if (shift.getStartTime() != null
                    && shift.getEndTime() != null) {

                // 時給制：従来どおり勤務時間と時給から計算
                workingMinutes = wageCalculator.calculateWorkingMinutes(
                        shift.getStartTime(),
                        shift.getEndTime(),
                        shift.getBreakMinutes()
                );

                wage = wageCalculator.calculateWageByDate(
                        workDate,
                        shift.getStartTime(),
                        shift.getEndTime(),
                        shift.getBreakMinutes(),
                        shift.getRegularWage(),
                        shift.getPremiumWage()
                );
            }

            totalMinutes += workingMinutes;
            totalWage += wage;

            workplaceMinutes.put(
                    workplaceId,
                    workplaceMinutes.getOrDefault(workplaceId, 0L)
                            + workingMinutes
            );

            workplaceWages.put(
                    workplaceId,
                    workplaceWages.getOrDefault(workplaceId, 0L)
                            + wage
            );
        }

        List<Map<String, Object>> workplaceSummaries = new ArrayList<>();

        for (Workplace workplace : workplaces) {

            Map<String, Object> summary = new HashMap<>();

            summary.put("name", workplace.getName());
            summary.put("id", workplace.getId());
            summary.put(
                    "minutes",
                    workplaceMinutes.getOrDefault(workplace.getId(), 0L)
            );
            summary.put(
                    "wage",
                    workplaceWages.getOrDefault(workplace.getId(), 0L)
            );
            summary.put(
                    "lessons",
                    workplaceLessons.getOrDefault(workplace.getId(), 0L)
            );
            summary.put("payType", workplace.getPayType());

            Salary salary = salaryRepository
                    .findByWorkplaceIdAndSalaryYearAndSalaryMonth(
                            workplace.getId(),
                            year,
                            month
                    )
                    .orElse(null);

            if (salary != null) {
                summary.put("actualAmount", salary.getActualAmount());
                totalActualAmount += salary.getActualAmount();
            } else {
                summary.put("actualAmount", null);
            }

            workplaceSummaries.add(summary);
        }

        LocalDate currentMonth = LocalDate.of(year, month, 1);
        LocalDate previousMonth = currentMonth.minusMonths(1);
        LocalDate nextMonth = currentMonth.plusMonths(1);

        model.addAttribute("previousYear", previousMonth.getYear());
        model.addAttribute("previousMonth", previousMonth.getMonthValue());
        model.addAttribute("nextYear", nextMonth.getYear());
        model.addAttribute("nextMonth", nextMonth.getMonthValue());

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("totalMinutes", totalMinutes);
        model.addAttribute("totalWage", totalWage);
        model.addAttribute("workplaceSummaries", workplaceSummaries);
        model.addAttribute("totalActualAmount", totalActualAmount);

        return "salary";
    }

    private int getLessonWage(
            Long workplaceId,
            List<Workplace> workplaces) {

        for (Workplace workplace : workplaces) {
            if (workplace.getId().equals(workplaceId)) {
                Integer lessonWage = workplace.getLessonWage();
                return lessonWage == null ? 0 : lessonWage;
            }
        }

        return 0;
    }

    @PostMapping("/salary/actual")
    public String saveActualSalary(
            @RequestParam Long workplaceId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int actualAmount) {

        if (actualAmount < 0 || month < 1 || month > 12) {
            return "redirect:/salary?year=" + year + "&month=" + month;
        }

        Salary salary = salaryRepository
                .findByWorkplaceIdAndSalaryYearAndSalaryMonth(
                        workplaceId,
                        year,
                        month
                )
                .orElseGet(Salary::new);

        salary.setWorkplaceId(workplaceId);
        salary.setSalaryYear(year);
        salary.setSalaryMonth(month);
        salary.setActualAmount(actualAmount);

        salaryRepository.save(salary);

        return "redirect:/salary?year=" + year + "&month=" + month;
    }
}