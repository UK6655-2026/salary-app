package com.example.salary_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.salary_app.Salary;

public interface SalaryRepository extends JpaRepository<Salary, Long> {

    Optional<Salary> findByWorkplaceIdAndSalaryYearAndSalaryMonth(
            Long workplaceId,
            int salaryYear,
            int salaryMonth
    );
}