package com.musalasoft.drones.drone;


import com.musalasoft.drones.drone.dtos.BatteryLevelResponse;
import com.musalasoft.drones.drone.dtos.DroneRequest;
import com.musalasoft.drones.drone.dtos.DroneUpdateRequest;
import com.musalasoft.drones.drone.dtos.LoadingRequest;
import com.musalasoft.drones.medication.Medication;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/drones")
@RequiredArgsConstructor
public class DroneController {


    private final DroneService droneService;

    @PostMapping("/registration")
    public ResponseEntity<Drone> register(
            @RequestBody @Valid DroneRequest request
    ) {
        return new ResponseEntity<>(droneService.createDrone(request), HttpStatus.CREATED);
    }


    @PutMapping("/{serialNumber}/loading")
    public ResponseEntity<Drone> load(
            @PathVariable @Parameter(example = "DR-25") String serialNumber,
            @RequestBody @Valid LoadingRequest request
    ) {
        return new ResponseEntity<>(droneService.loadDrone(request, serialNumber), HttpStatus.OK);
    }

    @PutMapping("/{serialNumber}/unloading")
    public ResponseEntity<Drone> unLoad(
             @PathVariable @Parameter(example = "DR-25") String serialNumber
    ) {
        return new ResponseEntity<>(droneService.unloadDrone(serialNumber), HttpStatus.OK);
    }


    @PutMapping("/{serialNumber}")
    public ResponseEntity<Drone> update(
            @PathVariable @Parameter(example = "DR-25") String serialNumber,
            @RequestBody @Valid DroneUpdateRequest request
    ) {
        return new ResponseEntity<>(droneService.updateDroneProperties(serialNumber, request), HttpStatus.OK);
    }


    @GetMapping("/{serialNumber}/medications")
    public ResponseEntity<Set<Medication>> getDroneMedications(
             @PathVariable @Parameter(example = "DR-25") String serialNumber
    ) {
        return new ResponseEntity<>(droneService.getDroneMedicationItems(serialNumber), HttpStatus.OK);
    }


    @GetMapping("/available")
    public ResponseEntity<List<Drone>> getAllAvailableDrones(
    ) {
        return new ResponseEntity<>(droneService.findAllAvailableDrones(), HttpStatus.OK);
    }


    @GetMapping("/{serialNumber}/battery-level")
    public ResponseEntity<BatteryLevelResponse> getDroneBatteryLevel(
             @PathVariable @Parameter(example = "DR-25") String serialNumber
    ) {
        return new ResponseEntity<>(droneService.getDroneBatteryLevel(serialNumber), HttpStatus.OK);
    }




}
