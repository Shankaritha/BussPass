package com.example.buspass.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bus_routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String routeName;

    private String boardingPoint;

    private String destination;
}