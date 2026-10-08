package com.example.salary_app.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;

import org.springframework.stereotype.Service;

@Service
public class WageCalculator {

    private final HolidayService holidayService;

    public WageCalculator(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

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

        if (endTime.isBefore(startTime)
                || endTime.equals(startTime)) {
                return 0;
        }

        LocalTime premiumStart = LocalTime.of(17, 0);

        // 17時より前に働いた時間
        long regularMinutes;

        if (endTime.isBefore(premiumStart)
                || endTime.equals(premiumStart)) {

                regularMinutes = java.time.Duration.between(
                        startTime,
                        endTime
                ).toMinutes();

        } else if (startTime.isBefore(premiumStart)) {

                regularMinutes = java.time.Duration.between(
                        startTime,
                        premiumStart
                ).toMinutes();

        } else {

                regularMinutes = 0;
        }

        // 休憩時間は通常時給の時間帯から差し引く
        regularMinutes = Math.max(
                0,
                regularMinutes - breakMinutes
        );

        // 17時以降の勤務時間
        long premiumMinutes = 0;

        if (endTime.isAfter(premiumStart)
                && !startTime.isAfter(premiumStart)) {

                premiumMinutes = java.time.Duration.between(
                        premiumStart,
                        endTime
                ).toMinutes();

        } else if (!startTime.isBefore(premiumStart)) {

                premiumMinutes = java.time.Duration.between(
                        startTime,
                        endTime
                ).toMinutes();
        }

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
                || dayOfWeek == DayOfWeek.SUNDAY
                || holidayService.isJapaneseHoliday(workDate)) {

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