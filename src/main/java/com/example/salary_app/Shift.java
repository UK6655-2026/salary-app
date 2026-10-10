
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

    // 給与方式：時給制またはコマ制
    private String payType = "HOURLY";

    private LocalTime startTime;

    private LocalTime endTime;

    private int breakMinutes;

    private int regularWage;

    private int premiumWage;

    // コマ制で働いたコマ数
    private Integer lessonCount;

    public Long getId() {
        return id;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public Long getWorkplaceId() {
        return workplaceId;
    }

    public String getPayType() {
        return payType;
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

    public Integer getLessonCount() {
        return lessonCount;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public void setWorkplaceId(Long workplaceId) {
        this.workplaceId = workplaceId;
    }

    public void setPayType(String payType) {
        this.payType = payType;
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

    public void setLessonCount(Integer lessonCount) {
        this.lessonCount = lessonCount;
    }
}
