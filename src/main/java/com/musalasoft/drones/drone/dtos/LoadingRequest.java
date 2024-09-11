package com.musalasoft.drones.drone.dtos;


import java.util.Set;

public record LoadingRequest(
        Set<String> medicationCodes

) {
}
