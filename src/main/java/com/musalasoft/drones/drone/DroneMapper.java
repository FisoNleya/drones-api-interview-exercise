package com.musalasoft.drones.drone;

import com.musalasoft.drones.drone.dtos.DroneUpdateRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DroneMapper {


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(DroneUpdateRequest updateRequest, @MappingTarget Drone drone);

}
