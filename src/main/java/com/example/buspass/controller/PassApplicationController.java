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

    @GetMapping
    public List<PassApplication> getAllApplications() {
        return applicationService.getAllApplications();
    }

    @GetMapping("/{id}")
    public PassApplication getApplication(
            @PathVariable Long id) {

        return applicationService.getApplicationById(id);
    }

    @GetMapping("/student/{studentId}")
    public List<PassApplication> getStudentApplications(
            @PathVariable Long studentId) {

        return applicationService
                .getApplicationsByStudent(studentId);
    }

    @PutMapping("/{id}/approve")
    public PassApplication approveApplication(
            @PathVariable Long id) {

        return applicationService.approveApplication(id);
    }

    @PutMapping("/{id}/reject")
    public PassApplication rejectApplication(
            @PathVariable Long id,
            @RequestParam String reason) {

        return applicationService.rejectApplication(
                id,
                reason
        );
    }

    @GetMapping("/expiring")
    public List<PassApplication> getExpiringApplications() {
        return applicationService.getExpiringApplications();
    }

    @PutMapping("/update-expired")
    public ResponseEntity<String> updateExpiredApplications() {

        applicationService.updateExpiredApplications();

        return ResponseEntity.ok(
                "Expired applications updated successfully"
        );
    }
}