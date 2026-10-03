package com.example.salary_app.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.salary_app.repository.WorkplaceRepository;
import com.example.salary_app.Shift;
import com.example.salary_app.Workplace;
import com.example.salary_app.repository.ShiftRepository;
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
    public String shifts(Model model) {

        var shifts = shiftRepository.findAll();

        for (Shift shift : shifts) {

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

        model.addAttribute("shifts", shifts);
        model.addAttribute("workplaces", workplaceRepository.findAll());

        return "shifts";
    }

    @PostMapping("/shifts")
    public String registerShift(
            @RequestParam Long workplaceId,
            @RequestParam LocalDate workDate,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            @RequestParam int breakMinutes) {

        Shift shift = new Shift();

        shift.setWorkDate(workDate);
        shift.setWorkplaceId(workplaceId);
        shift.setStartTime(startTime);
        shift.setEndTime(endTime);
        shift.setBreakMinutes(breakMinutes);

        Workplace workplace = workplaceRepository.findById(workplaceId).orElseThrow();

        shift.setRegularWage(workplace.getRegularWage());
        shift.setPremiumWage(workplace.getPremiumWage());

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
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            @RequestParam int breakMinutes) {

        Shift shift = shiftRepository.findById(id).orElseThrow();

        shift.setWorkplaceId(workplaceId);
        shift.setWorkDate(workDate);
        shift.setStartTime(startTime);
        shift.setEndTime(endTime);
        shift.setBreakMinutes(breakMinutes);

        Workplace workplace = workplaceRepository.findById(workplaceId).orElseThrow();

        shift.setRegularWage(workplace.getRegularWage());
        shift.setPremiumWage(workplace.getPremiumWage());

        shiftRepository.save(shift);

        return "redirect:/shifts";
    }
}