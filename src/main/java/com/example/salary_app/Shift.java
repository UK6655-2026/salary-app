package com.example.salary_app;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate workDate;

    private Long workplaceId;

    private LocalTime startTime;

    private LocalTime endTime;

    private int breakMinutes;

    private int regularWage;

    private int premiumWage;

    public Long getId() {
        return id;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public Long getWorkplaceId() {
        return workplaceId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getBreakMinutes() {
        return breakMinutes;
    }

    public int getRegularWage() {
        return regularWage;
    }

    public int getPremiumWage() {
        return premiumWage;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public void setWorkplaceId(Long workplaceId) {
        this.workplaceId = workplaceId;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setBreakMinutes(int breakMinutes) {
        this.breakMinutes = breakMinutes;
    }

    public void setRegularWage(int regularWage) {
        this.regularWage = regularWage;
    }

    public void setPremiumWage(int premiumWage) {
        this.premiumWage = premiumWage;
    }
}