package com.example.buspass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "pass_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate applicationDate;

    private String photoReference;

    @Enumerated(EnumType.STRING)
    private PassStatus status;

    private String adminRemark;

    private String rejectionReason;

    private String passNumber;

    private LocalDate validFrom;

    private LocalDate validUntil;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private BusRoute busRoute;
}