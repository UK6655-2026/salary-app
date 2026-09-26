package com.example.salary_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.salary_app.Workplace;
import com.example.salary_app.repository.WorkplaceRepository;

@Controller
public class WorkplaceController {

    private final WorkplaceRepository workplaceRepository;

    public WorkplaceController(WorkplaceRepository workplaceRepository) {
        this.workplaceRepository = workplaceRepository;
    }

    @GetMapping("/workplaces")
    public String workplaces(Model model) {
        model.addAttribute("workplaces", workplaceRepository.findAll());
        return "workplaces";
    }

    @PostMapping("/workplaces")
    public String registerWorkplace(
            @RequestParam String name,
            @RequestParam int regularWage,
            @RequestParam int premiumWage) {

        Workplace workplace = new Workplace();

        workplace.setName(name);
        workplace.setRegularWage(regularWage);
        workplace.setPremiumWage(premiumWage);

        workplaceRepository.save(workplace);

        return "redirect:/workplaces";
    }

    @PostMapping("/workplaces/delete/{id}")
    public String deleteWorkplace(@PathVariable Long id) {
        workplaceRepository.deleteById(id);

        return "redirect:/workplaces";
    }

    @GetMapping("/workplaces/edit/{id}")
    public String editWorkplace(@PathVariable Long id, Model model) {

        Workplace workplace = workplaceRepository.findById(id).orElseThrow();

        model.addAttribute("workplace", workplace);

        return "workplace-edit";
    }

    @PostMapping("/workplaces/edit")
    public String updateWorkplace(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam int regularWage,
            @RequestParam int premiumWage) {

        Workplace workplace = workplaceRepository.findById(id).orElseThrow();

        workplace.setName(name);
        workplace.setRegularWage(regularWage);
        workplace.setPremiumWage(premiumWage);

        workplaceRepository.save(workplace);

        return "redirect:/workplaces";
    }
}