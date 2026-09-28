package com.example.buspass.service;

import com.example.buspass.entity.Student;
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

    private final Path uploadDirectory =
            Paths.get("uploads/students");

    public Student createStudent(
            Student student,
            MultipartFile photo) {

        try {

            // Create upload directory if it doesn't exist
            Files.createDirectories(uploadDirectory);

            if (photo != null && !photo.isEmpty()) {

                String originalName = photo.getOriginalFilename();

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

            return studentRepository.save(student);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to save student photo"
            );
        }
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {

        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Student not found"
                        ));
    }
}