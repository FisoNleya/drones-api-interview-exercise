package com.musalasoft.drones.drone.dtos;

import com.musalasoft.drones.drone.enums.Model;
import com.musalasoft.drones.drone.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record DroneRequest(

        @Schema(defaultValue = "DR-25")
        @NotEmpty(message = "Please specify serial number")
        @Size(max = 100 , min = 1)
        String serialNumber,

        @NotNull
        Model model,

        @Schema(description = "Drone weight limit in grammes", defaultValue = "500")
        @Min(0)
        @Max(500)
        @NotNull
        Integer weightLimit,

        @Schema(defaultValue = "40")
        @Min(0)
        @Max(100)
        @NotNull
        Integer batteryCapacity,

        @NotNull
        State droneState
) {
}
