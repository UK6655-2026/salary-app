
package com.example.salary_app.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.salary_app.Shift;
import com.example.salary_app.Workplace;
import com.example.salary_app.repository.ShiftRepository;
import com.example.salary_app.repository.WorkplaceRepository;
import com.example.salary_app.service.WageCalculator;

@Controller
public class ShiftController {

    private final ShiftRepository shiftRepository;
    private final WorkplaceRepository workplaceRepository;
    private final WageCalculator wageCalculator;

    public ShiftController(
            ShiftRepository shiftRepository,
            WorkplaceRepository workplaceRepository,
            WageCalculator wageCalculator) {

        this.shiftRepository = shiftRepository;
        this.workplaceRepository = workplaceRepository;
        this.wageCalculator = wageCalculator;
    }

    @GetMapping("/shifts")
    public String shifts(
            @RequestParam(defaultValue = "2026") int year,
            @RequestParam(defaultValue = "10") int month,
            @RequestParam(required = false) Integer day,
            Model model) {

        LocalDate firstDay = LocalDate.of(year, month, 1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = firstDay.lengthOfMonth();

        LocalDate currentMonth = LocalDate.of(year, month, 1);
        LocalDate previousMonth = currentMonth.minusMonths(1);
        LocalDate nextMonth = currentMonth.plusMonths(1);

        model.addAttribute("previousYear", previousMonth.getYear());
        model.addAttribute("previousMonth", previousMonth.getMonthValue());
        model.addAttribute("nextYear", nextMonth.getYear());
        model.addAttribute("nextMonth", nextMonth.getMonthValue());

        List<Shift> allShifts = shiftRepository.findAll();

        allShifts.sort(
                Comparator.comparing(Shift::getWorkDate)
                        .thenComparing(
                                shift -> shift.getStartTime() == null
                                        ? LocalTime.MIN
                                        : shift.getStartTime()
                        )
        );

        Map<Long, String> workplaceNames = new HashMap<>();
        Map<Long, Workplace> workplaceMap = new HashMap<>();

        List<Workplace> workplaces = workplaceRepository.findAll();

        for (Workplace workplace : workplaces) {
            workplaceNames.put(workplace.getId(), workplace.getName());
            workplaceMap.put(workplace.getId(), workplace);
        }

        List<Shift> shifts = allShifts;

        if (day != null) {
            LocalDate selectedDate = LocalDate.of(year, month, day);

            shifts = allShifts.stream()
                    .filter(shift -> shift.getWorkDate().equals(selectedDate))
                    .toList();
        }

        for (Shift shift : shifts) {
            if ("PER_LESSON".equals(shift.getPayType())) {
                long wage = (long) workplaceMap
                        .get(shift.getWorkplaceId())
                        .getLessonWage()
                        * (shift.getLessonCount() == null ? 0 : shift.getLessonCount());

                System.out.println(
                        "勤務日：" + shift.getWorkDate()
                        + " コマ数：" + shift.getLessonCount()
                        + " 給料：" + wage + "円"
                );
            } else if (shift.getStartTime() != null
                    && shift.getEndTime() != null) {

                long workingMinutes = wageCalculator.calculateWorkingMinutes(
                        shift.getStartTime(),
                        shift.getEndTime(),
                        shift.getBreakMinutes()
                );

                long wage = wageCalculator.calculateWageByDate(
                        shift.getWorkDate(),
                        shift.getStartTime(),
                        shift.getEndTime(),
                        shift.getBreakMinutes(),
                        shift.getRegularWage(),
                        shift.getPremiumWage()
                );

                System.out.println(
                        "勤務日：" + shift.getWorkDate()
                        + " 実働：" + workingMinutes + "分"
                        + " 給料：" + wage + "円"
                );
            }
        }

        Map<Integer, String> shiftTimes = new HashMap<>();

        for (Shift shift : allShifts) {
            if (shift.getWorkDate().getYear() == year
                    && shift.getWorkDate().getMonthValue() == month) {

                int dayNumber = shift.getWorkDate().getDayOfMonth();
                Workplace workplace = workplaceMap.get(shift.getWorkplaceId());

                String time;

                if ("PER_LESSON".equals(shift.getPayType())) {
                    time = workplace.getName()
                            + " "
                            + (shift.getLessonCount() == null
                                    ? 0
                                    : shift.getLessonCount())
                            + "コマ";
                } else {
                    time = workplace.getName()
                            + " "
                            + shift.getStartTime()
                            + "〜"
                            + shift.getEndTime();
                }

                shiftTimes.put(
                        dayNumber,
                        shiftTimes.getOrDefault(dayNumber, "")
                                + time
                                + "<br>"
                );
            }
        }

        List<Integer> shiftDays = allShifts.stream()
                .filter(shift -> shift.getWorkDate().getYear() == year)
                .filter(shift -> shift.getWorkDate().getMonthValue() == month)
                .map(shift -> shift.getWorkDate().getDayOfMonth())
                .toList();

        model.addAttribute("shifts", shifts);
        model.addAttribute("workplaceNames", workplaceNames);
        model.addAttribute("workplaces", workplaces);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("daysInMonth", daysInMonth);
        model.addAttribute("startDayOfWeek", startDayOfWeek);
        model.addAttribute("shiftDays", shiftDays);
        model.addAttribute("shiftTimes", shiftTimes);
        model.addAttribute(
                "selectedDate",
                day != null ? LocalDate.of(year, month, day) : null
        );

        return "shifts";
    }

    @PostMapping("/shifts")
    public String registerShift(
            @RequestParam Long workplaceId,
            @RequestParam LocalDate workDate,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime,
            @RequestParam(defaultValue = "0") int breakMinutes,
            @RequestParam(required = false) Integer lessonCount) {

        Workplace workplace = workplaceRepository
                .findById(workplaceId)
                .orElseThrow();

        Shift shift = new Shift();
        shift.setWorkplaceId(workplaceId);
        shift.setWorkDate(workDate);
        shift.setPayType(workplace.getPayType());

        if ("PER_LESSON".equals(workplace.getPayType())) {
            if (lessonCount == null || lessonCount <= 0) {
                return "redirect:/shifts";
            }

            shift.setLessonCount(lessonCount);
            shift.setStartTime(null);
            shift.setEndTime(null);
            shift.setBreakMinutes(0);
            shift.setRegularWage(0);
            shift.setPremiumWage(0);
        } else {
            if (startTime == null || endTime == null
                    || !endTime.isAfter(startTime)) {
                return "redirect:/shifts";
            }

            long workingMinutes = java.time.Duration.between(
                    startTime,
                    endTime
            ).toMinutes();

            if (breakMinutes < 0 || breakMinutes >= workingMinutes) {
                return "redirect:/shifts";
            }

            shift.setStartTime(startTime);
            shift.setEndTime(endTime);
            shift.setBreakMinutes(breakMinutes);
            shift.setRegularWage(workplace.getRegularWage());
            shift.setPremiumWage(workplace.getPremiumWage());
            shift.setLessonCount(null);
        }

        shiftRepository.save(shift);
        return "redirect:/shifts";
    }

    @PostMapping("/shifts/delete/{id}")
    public String deleteShift(@PathVariable Long id) {
        shiftRepository.deleteById(id);
        return "redirect:/shifts";
    }

    @GetMapping("/shifts/edit/{id}")
    public String editShift(@PathVariable Long id, Model model) {
        Shift shift = shiftRepository.findById(id).orElseThrow();

        model.addAttribute("shift", shift);
        model.addAttribute("workplaces", workplaceRepository.findAll());

        return "shift-edit";
    }

    @PostMapping("/shifts/edit")
    public String updateShift(
            @RequestParam Long id,
            @RequestParam Long workplaceId,
            @RequestParam LocalDate workDate,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime,
            @RequestParam(defaultValue = "0") int breakMinutes,
            @RequestParam(required = false) Integer lessonCount) {

        Workplace workplace = workplaceRepository
                .findById(workplaceId)
                .orElseThrow();

        Shift shift = shiftRepository.findById(id).orElseThrow();

        shift.setWorkplaceId(workplaceId);
        shift.setWorkDate(workDate);
        shift.setPayType(workplace.getPayType());

        if ("PER_LESSON".equals(workplace.getPayType())) {
            if (lessonCount == null || lessonCount <= 0) {
                return "redirect:/shifts/edit/" + id;
            }

            shift.setLessonCount(lessonCount);
            shift.setStartTime(null);
            shift.setEndTime(null);
            shift.setBreakMinutes(0);
            shift.setRegularWage(0);
            shift.setPremiumWage(0);
        } else {
            if (startTime == null || endTime == null
                    || !endTime.isAfter(startTime)) {
                return "redirect:/shifts/edit/" + id;
            }

            long workingMinutes = java.time.Duration.between(
                    startTime,
                    endTime
            ).toMinutes();

            if (breakMinutes < 0 || breakMinutes >= workingMinutes) {
                return "redirect:/shifts/edit/" + id;
            }

            shift.setStartTime(startTime);
            shift.setEndTime(endTime);
            shift.setBreakMinutes(breakMinutes);
            shift.setRegularWage(workplace.getRegularWage());
            shift.setPremiumWage(workplace.getPremiumWage());
            shift.setLessonCount(null);
        }

        shiftRepository.save(shift);
        return "redirect:/shifts";
    }
}
