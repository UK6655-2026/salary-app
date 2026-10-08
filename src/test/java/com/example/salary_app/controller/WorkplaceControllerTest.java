package com.example.salary_app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.example.salary_app.repository.WorkplaceRepository;

public class WorkplaceControllerTest {

    @Test
    void 通常時給が0以下なら登録しない() {

        WorkplaceRepository workplaceRepository = null;

        WorkplaceController controller =
                new WorkplaceController(workplaceRepository);

        String result = controller.registerWorkplace(
                "テスト職場",
                0,
                1250
        );

        assertEquals("redirect:/workplaces", result);
    }

    @Test
    void 割増時給が0以下なら登録しない() {

        WorkplaceRepository workplaceRepository = null;

        WorkplaceController controller =
                new WorkplaceController(workplaceRepository);

        String result = controller.registerWorkplace(
                "テスト職場",
                1200,
                0
        );

        assertEquals("redirect:/workplaces", result);
    }

    @Test
    void 編集時に通常時給が0以下なら更新しない() {

        WorkplaceRepository workplaceRepository = null;

        WorkplaceController controller =
                new WorkplaceController(workplaceRepository);

        String result = controller.updateWorkplace(
                1L,
                "テスト職場",
                0,
                1250
        );

        assertEquals("redirect:/workplaces/edit/1", result);
    }

    @Test
    void 編集時に割増時給が0以下なら更新しない() {

        WorkplaceRepository workplaceRepository = null;

        WorkplaceController controller =
                new WorkplaceController(workplaceRepository);

        String result = controller.updateWorkplace(
                1L,
                "テスト職場",
                1200,
                0
        );

        assertEquals("redirect:/workplaces/edit/1", result);
    }
}