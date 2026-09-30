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


    // =========================================================
    // CREATE NEW PASS APPLICATION
    // =========================================================

    public PassApplication createApplication(
            Long studentId,
            Long routeId,
            String photoReference) {

        // Find student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        // Find bus route
        BusRoute route = busRouteRepository.findById(routeId)
                .orElseThrow(() ->
                        new RuntimeException("Bus route not found"));

        // Check whether student already has an active/approved pass
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

        // Create application
        PassApplication application = new PassApplication();

        application.setApplicationDate(LocalDate.now());
        application.setPhotoReference(photoReference);
        application.setStatus(PassStatus.PENDING);
        application.setStudent(student);
        application.setBusRoute(route);

        return applicationRepository.save(application);
    }


    // =========================================================
    // GET ALL APPLICATIONS
    // =========================================================

    public List<PassApplication> getAllApplications() {

        return applicationRepository.findAll();
    }


    // =========================================================
    // GET APPLICATION BY ID
    // =========================================================

    public PassApplication getApplicationById(Long id) {

        return applicationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Application not found"
                        )
                );
    }


    // =========================================================
    // GET APPLICATIONS OF A PARTICULAR STUDENT
    // =========================================================

    public List<PassApplication> getApplicationsByStudent(
            Long studentId) {

        // Check whether student exists
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException(
                    "Student not found"
            );
        }

        return applicationRepository.findByStudentId(studentId);
    }


    // =========================================================
    // APPROVE APPLICATION
    // =========================================================

    public PassApplication approveApplication(Long id) {

        // Find application
        PassApplication application =
                applicationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"
                                )
                        );

        // Only PENDING applications can be approved
        if (application.getStatus() != PassStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending applications can be approved"
            );
        }

        // Check whether student already has an approved pass
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

        // Change status to APPROVED
        application.setStatus(PassStatus.APPROVED);

        // Generate unique pass number
        String passNumber =
                "BP-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase();

        application.setPassNumber(passNumber);

        // Set pass validity
        LocalDate today = LocalDate.now();

        application.setValidFrom(today);

        application.setValidUntil(
                today.plusMonths(6)
        );

        // Add admin remark
        application.setAdminRemark(
                "Application approved"
        );

        // Save updated application
        return applicationRepository.save(application);
    }


    // =========================================================
    // REJECT APPLICATION
    // =========================================================

    public PassApplication rejectApplication(
            Long id,
            String rejectionReason) {

        // Find application
        PassApplication application =
                applicationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"
                                )
                        );

        // Only PENDING applications can be rejected
        if (application.getStatus() != PassStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending applications can be rejected"
            );
        }

        // Rejection reason is mandatory
        if (rejectionReason == null ||
                rejectionReason.trim().isEmpty()) {

            throw new RuntimeException(
                    "Rejection reason is required"
            );
        }

        // Change status to REJECTED
        application.setStatus(PassStatus.REJECTED);

        // Store rejection reason
        application.setRejectionReason(
                rejectionReason.trim()
        );

        // Store admin remark
        application.setAdminRemark(
                "Application rejected"
        );

        // Save changes
        return applicationRepository.save(application);
    }


    // =========================================================
    // GET PASSES EXPIRING WITHIN NEXT 30 DAYS
    // =========================================================

    public List<PassApplication> getExpiringApplications() {

        LocalDate today = LocalDate.now();

        LocalDate next30Days =
                today.plusDays(30);

        return applicationRepository.findByValidUntilBetween(
                today,
                next30Days
        );
    }


    // =========================================================
    // UPDATE EXPIRED APPLICATIONS
    // =========================================================

    public void updateExpiredApplications() {

        LocalDate today = LocalDate.now();

        // Find approved passes whose validity has ended
        List<PassApplication> expiredApplications =
                applicationRepository
                        .findByStatusAndValidUntilBefore(
                                PassStatus.APPROVED,
                                today
                        );

        // Change their status to EXPIRED
        for (PassApplication application :
                expiredApplications) {

            application.setStatus(
                    PassStatus.EXPIRED
            );
        }

        // Save all updated applications
        applicationRepository.saveAll(
                expiredApplications
        );
    }
}