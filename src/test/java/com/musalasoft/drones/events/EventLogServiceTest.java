package com.musalasoft.drones.events;

import com.musalasoft.drones.drone.Drone;
import com.musalasoft.drones.drone.DroneService;
import com.musalasoft.drones.drone.enums.Model;
import com.musalasoft.drones.drone.enums.State;
import com.musalasoft.drones.medication.Medication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventLogServiceTest {

    @Mock
    private DroneService mockDroneService;
    @Mock
    private EventLogRepository mockEventLogRepository;

    private EventLogService eventLogServiceUnderTest;

    @BeforeEach
    void setUp() {
        eventLogServiceUnderTest = new EventLogService(mockDroneService, mockEventLogRepository);
    }

    @Test
    void testLogBatteryLevel() {
        // Setup
        // Configure DroneService.findAllDrones(...).
        final List<Drone> drones = List.of(new Drone(0L, "DR_55B", Model.LIGHT_WEIGHT, 500, 25, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Set.of(new Medication(0L, "name", 0, false, "MDC_33A", "imageUrl", null))));
        when(mockDroneService.findAllDrones()).thenReturn(drones);

        // Configure EventLogRepository.save(...).
        final EventLog eventLog = new EventLog(0L, new Drone(0L, "DR_55B", Model.LIGHT_WEIGHT, 500, 25, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Set.of(new Medication(0L, "name", 0, false, "MDC_33A", "imageUrl", null))), 0,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        when(mockEventLogRepository.save(any(EventLog.class))).thenReturn(eventLog);

        // Run the test
        eventLogServiceUnderTest.logBatteryLevel();

        // Verify the results
        verify(mockEventLogRepository).save(any(EventLog.class));
    }

    @Test
    void testLogBatteryLevel_CapacityGreaterThan25() {
        // Setup
        // Configure DroneService.findAllDrones(...).
        final List<Drone> drones = List.of(new Drone(0L, "DR_55B", Model.LIGHT_WEIGHT, 500, 30, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Set.of(new Medication(0L, "name", 0, false, "MDC_33A", "imageUrl", null))));
        when(mockDroneService.findAllDrones()).thenReturn(drones);

        // Configure EventLogRepository.save(...).
        final EventLog eventLog = new EventLog(0L, new Drone(0L, "DR_55B", Model.LIGHT_WEIGHT, 500, 25, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Set.of(new Medication(0L, "name", 0, false, "MDC_33A", "imageUrl", null))), 0,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        when(mockEventLogRepository.save(any(EventLog.class))).thenReturn(eventLog);

        // Run the test
        eventLogServiceUnderTest.logBatteryLevel();

        // Verify the results
        verify(mockEventLogRepository).save(any(EventLog.class));
    }


    @Test
    void testFindAll() {
        // Setup
        // Configure EventLogRepository.findAll(...).
        final Page<EventLog> eventLogs = new PageImpl<>(List.of(new EventLog(0L, new Drone(0L, "DR_55B", Model.LIGHT_WEIGHT, 500, 25, State.IDLE,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Set.of(new Medication(0L, "name", 0, false, "MDC_33A", "imageUrl", null))), 0,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0))));
        when(mockEventLogRepository.findAll(any(Pageable.class))).thenReturn(eventLogs);

        // Run the test
        final Page<EventLog> result = eventLogServiceUnderTest.findAll(0, 10, "sort");

        // Verify the result
        assertThat(result.getContent()).isEqualTo(eventLogs.getContent());

    }

}
