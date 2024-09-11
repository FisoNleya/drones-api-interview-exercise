package com.musalasoft.drones.drone;

import com.musalasoft.drones.drone.dtos.BatteryLevelResponse;
import com.musalasoft.drones.drone.dtos.DroneRequest;
import com.musalasoft.drones.drone.dtos.DroneUpdateRequest;
import com.musalasoft.drones.drone.dtos.LoadingRequest;
import com.musalasoft.drones.drone.enums.Model;
import com.musalasoft.drones.drone.enums.State;
import com.musalasoft.drones.exceptions.DataNotFoundException;
import com.musalasoft.drones.exceptions.DuplicateRecordException;
import com.musalasoft.drones.exceptions.InvalidRequestException;
import com.musalasoft.drones.medication.Medication;
import com.musalasoft.drones.medication.MedicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DroneServiceTest {

    @Mock
    private DroneRepository mockDroneRepository;
    @Mock
    private MedicationService mockMedicationService;
    @Mock
    private DroneMapper mockMapper;

    private DroneService droneServiceUnderTest;

    @BeforeEach
    void setUp() {
        droneServiceUnderTest = new DroneService(mockDroneRepository, mockMedicationService, mockMapper);
    }

    @Test
    void testCreateDrone() {
        // Setup
        final DroneRequest droneRequest = new DroneRequest("DR-25", Model.LIGHT_WEIGHT, 300, 50, State.IDLE);
        final Drone expectedResult = new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 300, 50, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0), null);

        // Configure DroneRepository.findBySerialNumber(...).
        final Optional<Drone> optionalDrone = Optional.of(
                new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 300, 0, State.IDLE,
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0), null));
        when(mockDroneRepository.findBySerialNumber("DR-25")).thenReturn(optionalDrone);


        // Verify the results
        assertThatThrownBy(() -> droneServiceUnderTest.createDrone( droneRequest))
                .isInstanceOf(DuplicateRecordException.class);
    }

    @Test
    void testCreateDrone_DroneRepositoryFindBySerialNumberReturnsAbsent() {
        // Setup
        final DroneRequest droneRequest = new DroneRequest("DR-25", Model.LIGHT_WEIGHT, 300, 50, State.IDLE);
        final Drone expectedResult = new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 300, 50, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0), null);
        when(mockDroneRepository.findBySerialNumber("DR-25")).thenReturn(Optional.empty());

        // Configure DroneRepository.save(...).
        final Drone drone = new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 300, 50, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0), null);

        when(mockDroneRepository.save(any(Drone.class))).thenReturn(drone);

        // Run the test
        final Drone result = droneServiceUnderTest.createDrone(droneRequest);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testLoadDrone() {
        // Setup
        final LoadingRequest loadingRequest = new LoadingRequest(Set.of("MDC_32A"));
        final Drone expectedResult = new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 300, 50, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Set.of(new Medication(1L, "GRAND-PA_32A", 0, false, "MDC_32A",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS6p0ZWRCEldPVn3tkhAJYPAETG-2pQCeyUTUGoyw3uGQ&s", null)));

        // Configure DroneRepository.findBySerialNumberAndDroneState(...).
        final Optional<Drone> optionalDrone = Optional.of(
                new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 300, 50, State.IDLE,
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0), null));
        when(mockDroneRepository.findBySerialNumberAndDroneState("DR-25", State.IDLE)).thenReturn(optionalDrone);

        // Configure MedicationService.findAllAvailableByCodes(...).
        final Set<Medication> medications =  Set.of(new Medication(1L, "GRAND-PA_32A", 0, false, "MDC_32A",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS6p0ZWRCEldPVn3tkhAJYPAETG-2pQCeyUTUGoyw3uGQ&s", null));
        when(mockMedicationService.findAllAvailableByCodes(Set.of("MDC_32A"))).thenReturn(medications);

        // Configure DroneRepository.save(...).
        final Drone drone = new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 300, 50, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Set.of(new Medication(1L, "GRAND-PA_32A", 0, false, "MDC_32A",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS6p0ZWRCEldPVn3tkhAJYPAETG-2pQCeyUTUGoyw3uGQ&s", null)));
        when(mockDroneRepository.save(any(Drone.class))).thenReturn(drone);

        // Run the test
        final Drone result = droneServiceUnderTest.loadDrone(loadingRequest, "DR-25");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockMedicationService).updateMedicationAvailability(any(Set.class), any(Boolean.class));
    }

    @Test
    void testLoadDrone_DroneBatteryBelow25() {
        // Setup
        final LoadingRequest loadingRequest = new LoadingRequest(Set.of("MDC_32A"));

        // Configure DroneRepository.findBySerialNumberAndDroneState(...).
        final Optional<Drone> optionalDrone = Optional.of(
                new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 300, 22, State.IDLE,
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0), null));
        when(mockDroneRepository.findBySerialNumberAndDroneState("DR-25", State.IDLE)).thenReturn(optionalDrone);


        // Run the test
        // Verify the results
        assertThatThrownBy(() -> droneServiceUnderTest.loadDrone(loadingRequest, "DR-25"))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void testLoadDrone_DroneRepositoryFindBySerialNumberAndDroneStateReturnsAbsent() {
        // Setup
        final LoadingRequest loadingRequest = new LoadingRequest(Set.of("MDC_32A"));
        when(mockDroneRepository.findBySerialNumberAndDroneState("DR-25", State.IDLE))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> droneServiceUnderTest.loadDrone(loadingRequest, "DR-25"))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void testLoadDrone_MedicationServiceFindAllAvailableByCodesReturnsNoItems() {
        // Setup
        final LoadingRequest loadingRequest = new LoadingRequest(Set.of("MDC_32A"));

        // Configure DroneRepository.findBySerialNumberAndDroneState(...).
        final Optional<Drone> optionalDrone = Optional.of(
                new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 300, 50, State.IDLE,
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                        Set.of(new Medication(1L, "GRAND-PA_32A", 0, false, "MDC_32A",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS6p0ZWRCEldPVn3tkhAJYPAETG-2pQCeyUTUGoyw3uGQ&s", null))));
        when(mockDroneRepository.findBySerialNumberAndDroneState("DR-25", State.IDLE)).thenReturn(optionalDrone);

        when(mockMedicationService.findAllAvailableByCodes(any(Set.class))).thenReturn(Collections.emptySet());

        // Run the test
        assertThatThrownBy(() -> droneServiceUnderTest.loadDrone(loadingRequest, "DR-25"))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void testUnloadDrone() {
        // Setup
        final Drone expectedResult = new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 0, 0, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Collections.emptySet());

        // Configure DroneRepository.findBySerialNumber(...).
        final Optional<Drone> optionalDrone = Optional.of(
                new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 300, 50, State.IDLE,
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                        Set.of(new Medication(1L, "GRAND-PA_32A", 0, false, "MDC_32A",
                                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS6p0ZWRCEldPVn3tkhAJYPAETG-2pQCeyUTUGoyw3uGQ&s", null))));
        when(mockDroneRepository.findBySerialNumber("DR-25")).thenReturn(optionalDrone);

        // Configure DroneRepository.save(...).
        final Drone drone = new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 0, 0, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Collections.emptySet());
        when(mockDroneRepository.save(any(Drone.class))).thenReturn(drone);

        // Run the test
        final Drone result = droneServiceUnderTest.unloadDrone("DR-25");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockMedicationService).updateMedicationAvailability(
                any(Set.class), any(Boolean.class));
    }

    @Test
    void testUnloadDrone_DroneRepositoryFindBySerialNumberReturnsAbsent() {
        // Setup
        when(mockDroneRepository.findBySerialNumber("DR-25")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> droneServiceUnderTest.unloadDrone("DR-25"))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void testUpdateDroneProperties() {
        // Setup
        final DroneUpdateRequest updateRequest = new DroneUpdateRequest(Model.LIGHT_WEIGHT, 300, 50);
        final Drone expectedResult = new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 300, 50, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),null);

        // Configure DroneRepository.findBySerialNumber(...).
        final Optional<Drone> optionalDrone = Optional.of(
                new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 300, 50, State.IDLE,
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0),null));
        when(mockDroneRepository.findBySerialNumber("DR-25")).thenReturn(optionalDrone);

        // Configure DroneRepository.save(...).
        final Drone drone = new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 300, 50, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Set.of(new Medication(1L, "name", 0, false, "MDC_32A", "imageUrl", null)));
        when(mockDroneRepository.save(any(Drone.class))).thenReturn(drone);

        // Run the test
        final Drone result = droneServiceUnderTest.updateDroneProperties("DR-25", updateRequest);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockMapper).update(any(DroneUpdateRequest.class), any(Drone.class));
    }

    @Test
    void testUpdateDroneProperties_DroneRepositoryFindBySerialNumberReturnsAbsent() {
        // Setup
        final DroneUpdateRequest updateRequest = new DroneUpdateRequest(Model.LIGHT_WEIGHT, 0, 0);
        when(mockDroneRepository.findBySerialNumber("DR-25")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
                () -> droneServiceUnderTest.updateDroneProperties("DR-25", updateRequest))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void testCheckWeight_AboveMaxWeight() {
        // Setup
        final Drone drone = new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 400, 0, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0), null);
        final Set<Medication> medications = Set.of(new Medication(1L, "name", 300, false, "MDC_32A", "imageUrl",
                null),
                new Medication(2L, "name", 300, false, "MDC_33A", "imageUrl",null));

        // Run the test
        // Verify the results
        assertThatThrownBy(
                () -> droneServiceUnderTest.checkWeight(drone, medications))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void testGetDroneMedicationItems() {
        // Setup
        final Set<Medication> expectedResult = Set.of(new Medication(1L, "name", 0, false, "MDC_32A", "imageUrl",
                new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 0, 0, State.IDLE,
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0), Set.of())));

        // Configure DroneRepository.findBySerialNumber(...).
        final Optional<Drone> optionalDrone = Optional.of(
                new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 0, 0, State.IDLE,
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                        Set.of(new Medication(1L, "name", 0, false, "MDC_32A", "imageUrl", null))));
        when(mockDroneRepository.findBySerialNumber("DR-25")).thenReturn(optionalDrone);

        // Run the test
        final Set<Medication> result = droneServiceUnderTest.getDroneMedicationItems("DR-25");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetDroneMedicationItems_DroneRepositoryReturnsAbsent() {
        // Setup
        when(mockDroneRepository.findBySerialNumber("DR-25")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> droneServiceUnderTest.getDroneMedicationItems("DR-25"))
                .isInstanceOf(DataNotFoundException.class);
    }


    @Test
    void testFindAllAvailableDrones() {
        // Setup
        final List<Drone> expectedResult = List.of(new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 50, 50, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Set.of(new Medication(1L, "name", 0, false, "MDC_32A", "imageUrl", null))));

        // Configure DroneRepository.findAllByDroneState(...).
        final List<Drone> drones = List.of(new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 50, 50, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Set.of(new Medication(1L, "name", 0, false, "MDC_32A", "imageUrl", null))));
        when(mockDroneRepository.findAllByDroneState(State.IDLE)).thenReturn(drones);

        // Run the test
        final List<Drone> result = droneServiceUnderTest.findAllAvailableDrones();

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindAllAvailableDrones_DroneRepositoryReturnsNoItems() {
        // Setup
        when(mockDroneRepository.findAllByDroneState(State.IDLE)).thenReturn(Collections.emptyList());

        // Run the test
        final List<Drone> result = droneServiceUnderTest.findAllAvailableDrones();

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testFindAllDrones() {
        // Setup
        final List<Drone> expectedResult = List.of(new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 500, 35, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Set.of(new Medication(1L, "name", 0, false, "MDC_32A", "imageUrl", null))));

        // Configure DroneRepository.findAll(...).
        final List<Drone> drones = List.of(new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 500, 50, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Set.of(new Medication(1L, "name", 0, false, "MDC_32A", "imageUrl", null))));
        when(mockDroneRepository.findAll()).thenReturn(drones);

        // Run the test
        final List<Drone> result = droneServiceUnderTest.findAllDrones();

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindAllDrones_DroneRepositoryReturnsNoItems() {
        // Setup
        when(mockDroneRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final List<Drone> result = droneServiceUnderTest.findAllDrones();

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }


    @Test
    void testGetDroneBatteryLevel() {
        // Setup
        final BatteryLevelResponse expectedResult = new BatteryLevelResponse("DR-25", 35);

        // Configure DroneRepository.findBySerialNumber(...).
        final Optional<Drone> optionalDrone = Optional.of(
                new Drone(1L, "DR-25", Model.LIGHT_WEIGHT, 500, 35, State.IDLE,
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                        Set.of(new Medication(1L, "name", 0, false, "MDC_32A", "imageUrl", null))));
        when(mockDroneRepository.findBySerialNumber("DR-25")).thenReturn(optionalDrone);

        // Run the test
        final BatteryLevelResponse result = droneServiceUnderTest.getDroneBatteryLevel("DR-25");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetDroneBatteryLevel_DroneRepositoryReturnsAbsent() {
        // Setup
        when(mockDroneRepository.findBySerialNumber("DR-25")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> droneServiceUnderTest.getDroneBatteryLevel("DR-25"))
                .isInstanceOf(DataNotFoundException.class);
    }


}
