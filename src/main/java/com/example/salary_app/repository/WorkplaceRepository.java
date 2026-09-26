package com.example.salary_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.salary_app.Workplace;

public interface WorkplaceRepository extends JpaRepository<Workplace, Long> {
}