package com.example.buspass.service;

import com.example.buspass.entity.BusRoute;
import com.example.buspass.entity.PassApplication;
import com.example.buspass.entity.PassStatus;
import com.example.buspass.entity.Student;
import com.example.buspass.repository.BusRouteRepository;
import com.example.buspass.repository.PassApplicationRepository;
import com.example.buspass.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassApplicationService {

    private final PassApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final BusRouteRepository busRouteRepository;

    // Create a new pass application
    public PassApplication createApplication(
            Long studentId,
            Long routeId,
            String photoReference) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        BusRoute route = busRouteRepository.findById(routeId)
                .orElseThrow(() ->
                        new RuntimeException("Bus route not found"));

        // Check whether student already has an approved pass
        boolean alreadyHasPass =
                applicationRepository.existsByStudentIdAndStatus(
                        studentId,
                        PassStatus.APPROVED
                );

        if (alreadyHasPass) {
            throw new RuntimeException(
                    "Student already has an active bus pass"
            );
        }

        PassApplication application = new PassApplication();

        application.setApplicationDate(LocalDate.now());
        application.setPhotoReference(photoReference);
        application.setStatus(PassStatus.PENDING);
        application.setStudent(student);
        application.setBusRoute(route);

        return applicationRepository.save(application);
    }

    // Get all applications
    public List<PassApplication> getAllApplications() {
        return applicationRepository.findAll();
    }

    // Get application by ID
    public PassApplication getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Application not found"));
    }

    // Get applications of a particular student
    public List<PassApplication> getApplicationsByStudent(Long studentId) {
        return applicationRepository.findAll()
                .stream()
                .filter(application ->
                        application.getStudent().getId().equals(studentId))
                .toList();
    }

    // Approve application
    public PassApplication approveApplication(Long id) {

        PassApplication application = getApplicationById(id);

        if (application.getStatus() != PassStatus.PENDING) {
            throw new RuntimeException(
                    "Only pending applications can be approved"
            );
        }

        // Double-check active pass
        boolean alreadyHasPass =
                applicationRepository.existsByStudentIdAndStatus(
                        application.getStudent().getId(),
                        PassStatus.APPROVED
                );

        if (alreadyHasPass) {
            throw new RuntimeException(
                    "Student already has an approved pass"
            );
        }

        application.setStatus(PassStatus.APPROVED);

        application.setPassNumber(
                "BP-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        application.setValidFrom(LocalDate.now());

        application.setValidUntil(
                LocalDate.now().plusMonths(6)
        );

        application.setAdminRemark(
                "Application approved"
        );

        return applicationRepository.save(application);
    }

    // Reject application
    public PassApplication rejectApplication(
            Long id,
            String rejectionReason) {

        PassApplication application = getApplicationById(id);

        if (application.getStatus() != PassStatus.PENDING) {
            throw new RuntimeException(
                    "Only pending applications can be rejected"
            );
        }

        if (rejectionReason == null ||
                rejectionReason.trim().isEmpty()) {

            throw new RuntimeException(
                    "Rejection reason is required"
            );
        }

        application.setStatus(PassStatus.REJECTED);
        application.setRejectionReason(rejectionReason);
        application.setAdminRemark("Application rejected");

        return applicationRepository.save(application);
    }

    // Find passes expiring within a date range
    public List<PassApplication> getExpiringApplications() {

        LocalDate today = LocalDate.now();

        LocalDate next30Days = today.plusDays(30);

        return applicationRepository.findByValidUntilBetween(
                today,
                next30Days
        );
    }
    public void updateExpiredApplications() {

        LocalDate today = LocalDate.now();

        List<PassApplication> expiredApplications =
                applicationRepository.findByStatusAndValidUntilBefore(
                        PassStatus.APPROVED,
                        today
                );

        for (PassApplication application : expiredApplications) {
            application.setStatus(PassStatus.EXPIRED);
        }

        applicationRepository.saveAll(expiredApplications);
    }
}