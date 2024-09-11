package com.musalasoft.drones.drone;

import com.musalasoft.drones.drone.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DroneRepository extends JpaRepository<Drone, Long> {

    Optional<Drone> findBySerialNumber(String serialNumber);
    Optional<Drone> findBySerialNumberAndDroneState(String serialNumber, State droneState);
    List<Drone> findAllByDroneState(State droneState);

}