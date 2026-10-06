package com.example.salary_app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

public class WageCalculatorTest {

    @Test
    void 平日の通常勤務を計算できる() {

        WageCalculator wageCalculator = new WageCalculator(
                new HolidayService()
        );

        long wage = wageCalculator.calculateWeekdayWage(
                LocalTime.of(10, 0),
                LocalTime.of(16, 0),
                30,
                1200,
                1250
        );

        assertEquals(6600, wage);
    }

    @Test
    void 平日の17時以降は割増時給になる() {

        WageCalculator wageCalculator = new WageCalculator(
                new HolidayService()
        );

        long wage = wageCalculator.calculateWeekdayWage(
                LocalTime.of(17, 0),
                LocalTime.of(20, 0),
                0,
                1200,
                1250
        );

        assertEquals(3750, wage);
    }

    @Test
    void 平日17時をまたぐ勤務を計算できる() {

        WageCalculator wageCalculator = new WageCalculator(
                new HolidayService()
        );

        long wage = wageCalculator.calculateWeekdayWage(
                LocalTime.of(16, 0),
                LocalTime.of(18, 0),
                0,
                1200,
                1250
        );

        assertEquals(2450, wage);
    }

    @Test
    void 土曜日は全時間が割増時給になる() {

        WageCalculator wageCalculator = new WageCalculator(
                new HolidayService()
        );

        long wage = wageCalculator.calculateWageByDate(
                LocalDate.of(2026, 10, 3),
                LocalTime.of(10, 0),
                LocalTime.of(16, 0),
                30,
                1200,
                1250
        );

        assertEquals(6875, wage);
    }

    @Test
    void 祝日は全時間が割増時給になる() {

        WageCalculator wageCalculator = new WageCalculator(
                new HolidayService()
        );

        long wage = wageCalculator.calculateWageByDate(
                LocalDate.of(2026, 10, 12),
                LocalTime.of(10, 0),
                LocalTime.of(16, 0),
                0,
                1200,
                1250
        );

        assertEquals(7500, wage);
    }

    @Test
    void 日曜日は全時間が割増時給になる() {

        WageCalculator wageCalculator = new WageCalculator(
                new HolidayService()
        );

        long wage = wageCalculator.calculateWageByDate(
                LocalDate.of(2026, 10, 4),
                LocalTime.of(10, 0),
                LocalTime.of(16, 0),
                30,
                1200,
                1250
        );

        assertEquals(6875, wage);
    }
}