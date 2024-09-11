package com.musalasoft.drones.drone;


import com.musalasoft.drones.drone.dtos.BatteryLevelResponse;
import com.musalasoft.drones.drone.dtos.DroneRequest;
import com.musalasoft.drones.drone.dtos.DroneUpdateRequest;
import com.musalasoft.drones.drone.dtos.LoadingRequest;
import com.musalasoft.drones.drone.enums.State;
import com.musalasoft.drones.exceptions.DataNotFoundException;
import com.musalasoft.drones.exceptions.DuplicateRecordException;
import com.musalasoft.drones.exceptions.InvalidRequestException;
import com.musalasoft.drones.medication.Medication;
import com.musalasoft.drones.medication.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DroneService {

    private final DroneRepository droneRepository;

    private final MedicationService medicationService;

    private final DroneMapper mapper;

    public Drone createDrone(DroneRequest droneRequest) {






        Optional<Drone> optionalDrone = droneRepository.findBySerialNumber(droneRequest.serialNumber());
        if (optionalDrone.isPresent()) {
            throw new DuplicateRecordException("Drone already exists with serial number : " + droneRequest.serialNumber());
        }

        return droneRepository.save(Drone.builder()
                .serialNumber(droneRequest.serialNumber())
                .model(droneRequest.model())
                .weightLimit(droneRequest.weightLimit())
                .batteryCapacity(droneRequest.batteryCapacity())
                .droneState(droneRequest.droneState())
                .build());
    }


    public Drone loadDrone(LoadingRequest loadingRequest, String serialNumber) {

        Drone drone = findAvailableDroneBySerialNumber(serialNumber);

        checkDroneBatteryCapacity(drone);
        Set<Medication> medications = medicationService.findAllAvailableByCodes(loadingRequest.medicationCodes());
        if (medications.isEmpty())
            throw new DataNotFoundException("Requested medications not found or has already been loaded");


        drone.setMedications(medications);
        drone.setDroneState(State.LOADED);

        checkWeight(drone, medications);


        drone = droneRepository.save(drone);
        medicationService.updateMedicationAvailability(medications, false);
        return drone;
    }


    public Drone unloadDrone(String serialNumber){
        Drone drone = findDroneBySerialNumber(serialNumber);
        Set<Medication> medications = drone.getMedications();

        if(Objects.nonNull(medications))
            medicationService.updateMedicationAvailability(medications, true);

        drone.setMedications(Collections.emptySet());
        drone.setDroneState(State.IDLE);
        return droneRepository.save(drone);
    }


    public Drone updateDroneProperties(String serialNumber, DroneUpdateRequest updateRequest){
        Drone drone = findDroneBySerialNumber(serialNumber);
        mapper.update(updateRequest, drone);
        return droneRepository.save(drone);
    }


    private void checkDroneBatteryCapacity(Drone drone) {
        if (drone.getBatteryCapacity() < 25)
            throw new InvalidRequestException("The selected drone battery level is below 25%, select another drone");
    }


    public void checkWeight(Drone drone, Set<Medication> medications) {

        int totalWeight = medications.stream().mapToInt(Medication::getWeight).sum();
        if (totalWeight > drone.getWeightLimit())
            throw new InvalidRequestException(
                    String.format("Total medication weight of requested items : %dg exceeds drone weight limit : %dg",
                            totalWeight, drone.getWeightLimit())
            );

    }

    public Set<Medication> getDroneMedicationItems(String serialNumber) {
        Drone drone = findDroneBySerialNumber(serialNumber);
        return drone.getMedications();
    }


    public Drone findAvailableDroneBySerialNumber(String serialNumber) {
        return droneRepository.findBySerialNumberAndDroneState(serialNumber, State.IDLE)
                .orElseThrow(() -> new DataNotFoundException(
                        "Drone not available for loading or does not exist, with serial number : " + serialNumber));
    }

    public List<Drone> findAllAvailableDrones() {
        return droneRepository.findAllByDroneState(State.IDLE);
    }

    public List<Drone> findAllDrones() {
        return droneRepository.findAll();
    }


    public Drone findDroneBySerialNumber(String serialNumber) {
        return droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new DataNotFoundException("Drone not found with serial number : " + serialNumber));
    }


    public BatteryLevelResponse getDroneBatteryLevel(String serialNumber) {
        Drone drone = findDroneBySerialNumber(serialNumber);
        return new BatteryLevelResponse(serialNumber, drone.getBatteryCapacity());
    }





}
