package com.example.salary_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.salary_app.Shift;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
}