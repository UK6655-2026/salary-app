package com.example.salary_app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.example.salary_app.repository.ShiftRepository;
import com.example.salary_app.repository.WorkplaceRepository;
import com.example.salary_app.service.WageCalculator;

public class ShiftControllerTest {

    @Test
    void 開始時刻より終了時刻が前なら登録しない() {

        ShiftRepository shiftRepository = null;
        WorkplaceRepository workplaceRepository = null;
        WageCalculator wageCalculator = null;

        ShiftController controller = new ShiftController(
                shiftRepository,
                workplaceRepository,
                wageCalculator
        );

        String result = controller.updateShift(
                1L,
                1L,
                LocalDate.of(2026, 10, 8),
                LocalTime.of(18, 0),
                LocalTime.of(17, 0),
                0
        );

        assertEquals("redirect:/shifts", result);
    }

    @Test
    void 休憩時間が勤務時間以上なら登録しない() {

        ShiftRepository shiftRepository = null;
        WorkplaceRepository workplaceRepository = null;
        WageCalculator wageCalculator = null;

        ShiftController controller = new ShiftController(
                shiftRepository,
                workplaceRepository,
                wageCalculator
        );

        String result = controller.updateShift(
                1L,
                1L,
                LocalDate.of(2026, 10, 8),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                120
        );

        assertEquals("redirect:/shifts", result);
    }

    @Test
    void 休憩時間がマイナスなら登録しない() {

        ShiftRepository shiftRepository = null;
        WorkplaceRepository workplaceRepository = null;
        WageCalculator wageCalculator = null;

        ShiftController controller = new ShiftController(
                shiftRepository,
                workplaceRepository,
                wageCalculator
        );

        String result = controller.updateShift(
                1L,
                1L,
                LocalDate.of(2026, 10, 8),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                -10
        );

        assertEquals("redirect:/shifts", result);
    }
}