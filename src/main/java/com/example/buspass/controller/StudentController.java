package com.example.buspass.controller;

import com.example.buspass.entity.Student;
import com.example.buspass.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping(
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Student> createStudent(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("collegeName") String collegeName,
            @RequestParam("photo") MultipartFile photo) {

        if (photo == null || photo.isEmpty()) {
            throw new RuntimeException("Student photo is required");
        }

        Student student = new Student();

        student.setName(name);
        student.setEmail(email);
        student.setPhone(phone);
        student.setCollegeName(collegeName);

        Student savedStudent =
                studentService.createStudent(student, photo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedStudent);
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }
}