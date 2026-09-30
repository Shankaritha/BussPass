package com.example.buspass.repository;

import com.example.buspass.entity.PassApplication;
import com.example.buspass.entity.PassStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PassApplicationRepository
        extends JpaRepository<PassApplication, Long> {

    // Check whether a student already has an approved active pass
    boolean existsByStudentIdAndStatus(
            Long studentId,
            PassStatus status
    );

    // Find applications whose pass expires between two dates
    List<PassApplication> findByValidUntilBetween(
            LocalDate start,
            LocalDate end
    );

    // Find approved passes that have already expired
    List<PassApplication> findByStatusAndValidUntilBefore(
            PassStatus status,
            LocalDate date
    );

    // Get all applications of a particular student
    List<PassApplication> findByStudentId(
            Long studentId
    );
}