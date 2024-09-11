package com.musalasoft.drones.drone.enums;

public enum Model {

    LIGHT_WEIGHT("Lightweight"),
    MIDDLE_WEIGHT("Middleweight"),
    CRUSER_WEIGHT("Cruiserweight"),
    HEAVY_WEIGHT("Heavyweight");


    private String description;

    private Model(String description){
        this.description = description;
    }

}
