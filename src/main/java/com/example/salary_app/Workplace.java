
package com.example.salary_app;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
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

    // 給与方式：HOURLY（時給制）または PER_LESSON（コマ制）
    private String payType = "HOURLY";

    private int regularWage;

    private int premiumWage;

    // 1コマあたりの料金
    private Integer lessonWage;

    public void setName(String name) {
        this.name = name;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public void setRegularWage(int regularWage) {
        this.regularWage = regularWage;
    }

    public void setPremiumWage(int premiumWage) {
        this.premiumWage = premiumWage;
    }

    public void setLessonWage(Integer lessonWage) {
        this.lessonWage = lessonWage;
    }

    public String getName() {
        return name;
    }

    public String getPayType() {
        return payType;
    }

    public int getRegularWage() {
        return regularWage;
    }

    public int getPremiumWage() {
        return premiumWage;
    }

    public Integer getLessonWage() {
        return lessonWage;
    }

    public Long getId() {
        return id;
    }
}
