package com.example.salary_app;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Workplace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int regularWage;

    private int premiumWage;

    public void setName(String name) {
        this.name = name;
    }

    public void setRegularWage(int regularWage) {
        this.regularWage = regularWage;
    }

    public void setPremiumWage(int premiumWage) {
        this.premiumWage = premiumWage;
    }

    public String getName() {
        return name;
    }

    public int getRegularWage() {
        return regularWage;
    }

    public int getPremiumWage() {
        return premiumWage;
    }

    public Long getId() {
        return id;
    }
}

