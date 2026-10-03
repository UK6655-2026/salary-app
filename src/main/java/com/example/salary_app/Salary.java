package com.example.salary_app;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long workplaceId;

    private int salaryYear;

    private int salaryMonth;

    private int actualAmount;

    public Long getId() {
        return id;
    }

    public Long getWorkplaceId() {
        return workplaceId;
    }

    public int getSalaryYear() {
        return salaryYear;
    }

    public int getSalaryMonth() {
        return salaryMonth;
    }

    public int getActualAmount() {
        return actualAmount;
    }

    public void setWorkplaceId(Long workplaceId) {
        this.workplaceId = workplaceId;
    }

    public void setSalaryYear(int salaryYear) {
        this.salaryYear = salaryYear;
    }

    public void setSalaryMonth(int salaryMonth) {
        this.salaryMonth = salaryMonth;
    }

    public void setActualAmount(int actualAmount) {
        this.actualAmount = actualAmount;
    }
}