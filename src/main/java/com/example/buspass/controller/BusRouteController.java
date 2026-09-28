package com.example.buspass.controller;

import com.example.buspass.entity.BusRoute;
import com.example.buspass.service.BusRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class BusRouteController {

    private final BusRouteService busRouteService;

    @PostMapping
    public BusRoute createRoute(@RequestBody BusRoute route) {
        return busRouteService.createRoute(route);
    }

    @GetMapping
    public List<BusRoute> getAllRoutes() {
        return busRouteService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public BusRoute getRoute(@PathVariable Long id) {
        return busRouteService.getRouteById(id);
    }
}