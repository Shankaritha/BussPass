package com.example.buspass.controller;
import com.example.buspass.dto.BusRouteRequest;
import jakarta.validation.Valid;
import com.example.buspass.entity.BusRoute;
import com.example.buspass.service.BusRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class BusRouteController {

    private final BusRouteService busRouteService;

    @PostMapping
    public ResponseEntity<BusRoute> createRoute(
            @Valid @RequestBody BusRouteRequest request) {

        BusRoute route = new BusRoute();

        route.setRouteName(request.getRouteName());
        route.setBoardingPoint(request.getBoardingPoint());
        route.setDestination(request.getDestination());

        BusRoute savedRoute = busRouteService.createRoute(route);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedRoute);
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