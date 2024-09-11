package com.musalasoft.drones.events;


import com.musalasoft.drones.drone.Drone;
import com.musalasoft.drones.drone.DroneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventLogService {


    private final DroneService droneService;
    private final EventLogRepository eventLogRepository;

    @Async
    @Scheduled(fixedRate = 1000 * 10 * 10)
    public void logBatteryLevel() {

        List<Drone> drones = droneService.findAllDrones();

        drones.forEach(drone -> {

            if (drone.getBatteryCapacity() > 25)
                log.info("Drone (serial number : {} ) battery percentage now at {} percent ",
                        drone.getSerialNumber(), drone.getBatteryCapacity());
            else
                log.warn("Drone (serial number : {} ) battery percentage now critical at {} percent, drone can no longer  be loaded",
                        drone.getSerialNumber(), drone.getBatteryCapacity());

            eventLogRepository.save(EventLog.builder()
                    .batterCapacityAtLogTime(drone.getBatteryCapacity())
                    .drone(drone)
                    .build());

        });

    }


    public Page<EventLog> findAll(int page, int size , String sort){

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        return eventLogRepository.findAll(pageable);
    }


}
