package com.example.buspass.controller;

import com.example.buspass.dto.PassApplicationRequest;
import com.example.buspass.entity.PassApplication;
import com.example.buspass.service.PassApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class PassApplicationController {

    private final PassApplicationService applicationService;


    // =========================================================
    // CREATE APPLICATION
    // =========================================================

    @PostMapping
    public ResponseEntity<PassApplication> createApplication(
            @Valid @RequestBody PassApplicationRequest request) {

        PassApplication application =
                applicationService.createApplication(
                        request.getStudentId(),
                        request.getRouteId(),
                        request.getPhotoReference()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(application);
    }


    // =========================================================
    // GET ALL APPLICATIONS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<PassApplication>> getAllApplications() {

        return ResponseEntity.ok(
                applicationService.getAllApplications()
        );
    }


    // =========================================================
    // GET APPLICATION BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<PassApplication> getApplication(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService.getApplicationById(id)
        );
    }


    // =========================================================
    // GET APPLICATIONS BY STUDENT
    // =========================================================

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PassApplication>>
    getStudentApplications(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                applicationService
                        .getApplicationsByStudent(studentId)
        );
    }


    // =========================================================
    // APPROVE APPLICATION
    // =========================================================

    @PutMapping("/{id}/approve")
    public ResponseEntity<PassApplication> approveApplication(
            @PathVariable Long id) {

        PassApplication application =
                applicationService.approveApplication(id);

        return ResponseEntity.ok(application);
    }


    // =========================================================
    // REJECT APPLICATION
    // =========================================================

    @PutMapping("/{id}/reject")
    public ResponseEntity<PassApplication> rejectApplication(
            @PathVariable Long id,
            @RequestParam String reason) {

        PassApplication application =
                applicationService.rejectApplication(
                        id,
                        reason
                );

        return ResponseEntity.ok(application);
    }


    // =========================================================
    // GET EXPIRING APPLICATIONS
    // =========================================================

    @GetMapping("/expiring")
    public ResponseEntity<List<PassApplication>>
    getExpiringApplications() {

        return ResponseEntity.ok(
                applicationService.getExpiringApplications()
        );
    }


    // =========================================================
    // UPDATE EXPIRED APPLICATIONS
    // =========================================================

    @PutMapping("/update-expired")
    public ResponseEntity<String>
    updateExpiredApplications() {

        applicationService.updateExpiredApplications();

        return ResponseEntity.ok(
                "Expired applications updated successfully"
        );
    }
}