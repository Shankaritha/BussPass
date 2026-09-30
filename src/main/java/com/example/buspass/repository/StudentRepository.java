package com.example.buspass.repository;

import com.example.buspass.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository
        extends JpaRepository<Student, Long> {

    List<Student> findAllByNameIgnoreCaseAndEmailIgnoreCase(
            String name,
            String email
    );
}