package com.musalasoft.drones.medication.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record MedicationRequest(

         @Schema(description = "Medication name must contain only letters, numbers, ‘-‘, ‘_’", defaultValue = "GRAND-PA_32A")
         @Pattern(regexp = "[a-zA-Z0-9-_]+", message = "Medication name must contain only letters, numbers, ‘-‘, ‘_’")
         @NotEmpty(message = "Please specify medication name")
         String name,

         @Min(1)
         @Max(500)
         @NotNull
         @Schema(description = "Drone weight limit in grammes", defaultValue = "100")
         Integer weight,

         @Schema(description = "Medication code must contain only upper case letters, underscore and numbers", defaultValue = "MDC_32A")
         @Pattern(regexp = "[A-Z0-9_]+", message = "Medication code must contain only upper case letters, underscore and numbers")
         @NotEmpty(message = "Code cannot be left blank")
         String code,

         @Schema(defaultValue = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS6p0ZWRCEldPVn3tkhAJYPAETG-2pQCeyUTUGoyw3uGQ&s")
         String imageUrl
) {

}
