package com.musalasoft.drones.drone.dtos;


import com.musalasoft.drones.drone.enums.Model;
import com.musalasoft.drones.drone.enums.State;
import com.musalasoft.drones.medication.Medication;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroneResponse {

    private Long  droneId;
    private String serialNumber;
    private Model model;
    private int weightLimit;
    private int batteryCapacity;
    private State droneState;
    private Set<Medication> medications;


}
