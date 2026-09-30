package com.example.buspass.service;

import com.example.buspass.entity.PassStatus;
import com.example.buspass.entity.Student;
import com.example.buspass.repository.PassApplicationRepository;
import com.example.buspass.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    private final PassApplicationRepository applicationRepository;

    private final Path uploadDirectory =
            Paths.get("uploads/students");

    public Student createStudent(
            Student student,
            MultipartFile photo) {

        // ==========================================
        // 1. FIND ALL STUDENTS WITH SAME NAME + EMAIL
        // ==========================================

        List<Student> existingStudents =
                studentRepository
                        .findAllByNameIgnoreCaseAndEmailIgnoreCase(
                                student.getName().trim(),
                                student.getEmail().trim()
                        );

        // ==========================================
        // 2. CHECK WHETHER ANY OF THEM HAS ACTIVE PASS
        // ==========================================

        for (Student existingStudent : existingStudents) {

            boolean hasActivePass =
                    applicationRepository.existsByStudentIdAndStatus(
                            existingStudent.getId(),
                            PassStatus.APPROVED
                    );

            if (hasActivePass) {

                throw new RuntimeException(
                        "Student already has an active bus pass"
                );
            }
        }

        // ==========================================
        // 3. IF STUDENT EXISTS BUT HAS NO ACTIVE PASS
        //    USE THE EXISTING STUDENT
        // ==========================================

        if (!existingStudents.isEmpty()) {

            return existingStudents.get(0);
        }

        // ==========================================
        // 4. CREATE NEW STUDENT
        // ==========================================

        try {

            Files.createDirectories(uploadDirectory);

            // ==========================================
            // 5. SAVE PHOTO
            // ==========================================

            if (photo != null && !photo.isEmpty()) {

                String originalName =
                        photo.getOriginalFilename();

                String extension = "";

                if (originalName != null &&
                        originalName.contains(".")) {

                    extension = originalName.substring(
                            originalName.lastIndexOf(".")
                    );
                }

                String fileName =
                        UUID.randomUUID() + extension;

                Path filePath =
                        uploadDirectory.resolve(fileName);

                Files.copy(
                        photo.getInputStream(),
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING
                );

                student.setPhotoReference(
                        "uploads/students/" + fileName
                );
            }

            // ==========================================
            // 6. SAVE NEW STUDENT
            // ==========================================

            return studentRepository.save(student);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to save student photo"
            );
        }
    }

    // ==========================================
    // GET ALL STUDENTS
    // ==========================================

    public List<Student> getAllStudents() {

        return studentRepository.findAll();
    }

    // ==========================================
    // GET STUDENT BY ID
    // ==========================================

    public Student getStudentById(Long id) {

        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Student not found"
                        ));
    }
}