package com.example.salary_app.service;

import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

@Service
public class WageCalculator {

    public long calculateWorkingMinutes(
            LocalTime startTime,
            LocalTime endTime,
            int breakMinutes) {

        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();

        return minutes - breakMinutes;
    }

    public long calculateWage(
        LocalTime startTime,
        LocalTime endTime,
        int breakMinutes,
        int hourlyWage) {

        long workingMinutes = calculateWorkingMinutes(
                startTime,
                endTime,
                breakMinutes
        );

        return workingMinutes * hourlyWage / 60;
    }

    public long calculateWeekdayWage(
        LocalTime startTime,
        LocalTime endTime,
        int breakMinutes,
        int regularWage,
        int premiumWage) {

        long totalMinutes = calculateWorkingMinutes(
                startTime,
                endTime,
                breakMinutes
        );

        if (endTime.isBefore(LocalTime.of(17, 0))
                || endTime.equals(LocalTime.of(17, 0))) {

            return totalMinutes * regularWage / 60;
        }

        if (startTime.isAfter(LocalTime.of(17, 0))
                || startTime.equals(LocalTime.of(17, 0))) {

            return totalMinutes * premiumWage / 60;
        }

        long regularMinutes = java.time.Duration.between(
                startTime,
                LocalTime.of(17, 0)
        ).toMinutes();

        long premiumMinutes = java.time.Duration.between(
                LocalTime.of(17, 0),
                endTime
        ).toMinutes();

        return (regularMinutes * regularWage / 60)
                + (premiumMinutes * premiumWage / 60);
    }

    public long calculateWageByDate(
        LocalDate workDate,
        LocalTime startTime,
        LocalTime endTime,
        int breakMinutes,
        int regularWage,
        int premiumWage) {

        long workingMinutes = calculateWorkingMinutes(
                startTime,
                endTime,
                breakMinutes
        );

        DayOfWeek dayOfWeek = workDate.getDayOfWeek();

        if (dayOfWeek == DayOfWeek.SATURDAY
                || dayOfWeek == DayOfWeek.SUNDAY) {

            return workingMinutes * premiumWage / 60;
        }

        return calculateWeekdayWage(
                startTime,
                endTime,
                breakMinutes,
                regularWage,
                premiumWage
        );
    }
}