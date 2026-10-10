
package com.example.salary_app.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

@Service
public class WageCalculator {

    /**
     * 勤務時間を分単位で計算する
     */
    public int calculateWorkingMinutes(
            LocalTime startTime,
            LocalTime endTime,
            int breakMinutes) {

        if (startTime == null || endTime == null) {
            return 0;
        }

        int start = startTime.getHour() * 60 + startTime.getMinute();
        int end = endTime.getHour() * 60 + endTime.getMinute();

        if (end <= start) {
            return 0;
        }

        return Math.max(0, end - start - breakMinutes);
    }

    /**
     * 時給と勤務時間から給与を計算する
     */
    public long calculateWage(
            LocalTime startTime,
            LocalTime endTime,
            int breakMinutes,
            int hourlyWage) {

        int minutes = calculateWorkingMinutes(
                startTime, endTime, breakMinutes);

        return (long) minutes * hourlyWage / 60;
    }

    /**
     * 平日について、17時より前と17時以降の給与を計算する
     */
    public long calculateWeekdayWage(
            LocalTime startTime,
            LocalTime endTime,
            int breakMinutes,
            int regularWage,
            int premiumWage) {

        if (startTime == null || endTime == null || !endTime.isAfter(startTime)) {
            return 0;
        }

        LocalTime premiumStart = LocalTime.of(17, 0);

        long regularMinutes = 0;
        long premiumMinutes = 0;

        if (startTime.isBefore(premiumStart)) {
            LocalTime regularEnd =
                    endTime.isBefore(premiumStart) ? endTime : premiumStart;

            regularMinutes = Math.max(
                    0,
                    java.time.Duration.between(startTime, regularEnd).toMinutes());
        }

        if (endTime.isAfter(premiumStart)) {
            LocalTime premiumBegin =
                    startTime.isAfter(premiumStart) ? startTime : premiumStart;

            premiumMinutes = Math.max(
                    0,
                    java.time.Duration.between(premiumBegin, endTime).toMinutes());
        }

        long totalMinutes = regularMinutes + premiumMinutes;
        long actualBreak = Math.min(Math.max(0, breakMinutes), totalMinutes);

        // 休憩時間は17時より前の勤務時間から優先して差し引く
        long regularBreak = Math.min(regularMinutes, actualBreak);
        long premiumBreak = actualBreak - regularBreak;

        regularMinutes -= regularBreak;
        premiumMinutes -= premiumBreak;

        return (regularMinutes * regularWage
                + premiumMinutes * premiumWage) / 60;
    }

    /**
     * 土日・祝日は全時間を割増時給、それ以外は17時を境に計算する
     */
    
    public long calculateWageByDate(
            LocalDate workDate,
            LocalTime startTime,
            LocalTime endTime,
            int breakMinutes,
            int regularWage,
            int premiumWage) {

        if (workDate == null || startTime == null || endTime == null) {
            return 0;
        }

        boolean weekend =
                workDate.getDayOfWeek() == java.time.DayOfWeek.SATURDAY
                || workDate.getDayOfWeek() == java.time.DayOfWeek.SUNDAY;

        if (weekend) {
            return calculateWage(
                    startTime, endTime, breakMinutes, premiumWage);
        }

        return calculateWeekdayWage(
                startTime, endTime, breakMinutes,
                regularWage, premiumWage);
    }

}
