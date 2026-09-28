package com.example.buspass.controller;

import com.example.buspass.entity.PassApplication;
import com.example.buspass.service.PassApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class PassApplicationController {

    private final PassApplicationService applicationService;

    // Create application
    @PostMapping
    public PassApplication createApplication(
            @RequestParam Long studentId,
            @RequestParam Long routeId,
            @RequestParam(required = false) String photoReference) {

        return applicationService.createApplication(
                studentId,
                routeId,
                photoReference
        );
    }

    // Get all applications
    @GetMapping
    public List<PassApplication> getAllApplications() {
        return applicationService.getAllApplications();
    }

    // Get application by ID
    @GetMapping("/{id}")
    public PassApplication getApplication(
            @PathVariable Long id) {

        return applicationService.getApplicationById(id);
    }

    // Get applications of a student
    @GetMapping("/student/{studentId}")
    public List<PassApplication> getStudentApplications(
            @PathVariable Long studentId) {

        return applicationService
                .getApplicationsByStudent(studentId);
    }

    // Approve
    @PutMapping("/{id}/approve")
    public PassApplication approveApplication(
            @PathVariable Long id) {

        return applicationService.approveApplication(id);
    }

    // Reject
    @PutMapping("/{id}/reject")
    public PassApplication rejectApplication(
            @PathVariable Long id,
            @RequestParam String reason) {

        return applicationService.rejectApplication(
                id,
                reason
        );
    }

    // Expiring passes
    @GetMapping("/expiring")
    public List<PassApplication> getExpiringApplications() {
        return applicationService.getExpiringApplications();
    }
}