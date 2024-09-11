package com.musalasoft.drones.drone.dtos;

import com.musalasoft.drones.drone.enums.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record DroneUpdateRequest(
                                 Model model,

                                 @Schema(description = "Drone weight limit in grammes", defaultValue = "500")
                                 @Min(0)
                                 @Max(500)
                                 Integer weightLimit,

                                 @Schema(defaultValue = "40")
                                 @Min(0)
                                 @Max(100)
                                 Integer batteryCapacity

                                 ) {
}
