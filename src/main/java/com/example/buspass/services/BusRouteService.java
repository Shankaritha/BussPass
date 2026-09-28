package com.example.buspass.service;

import com.example.buspass.entity.BusRoute;
import com.example.buspass.repository.BusRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusRouteService {

    private final BusRouteRepository busRouteRepository;

    public BusRoute createRoute(BusRoute route) {
        return busRouteRepository.save(route);
    }

    public List<BusRoute> getAllRoutes() {
        return busRouteRepository.findAll();
    }

    public BusRoute getRouteById(Long id) {
        return busRouteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Bus route not found"));
    }
}