package com.musalasoft.drones.drone;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.musalasoft.drones.drone.enums.Model;
import com.musalasoft.drones.drone.enums.State;
import com.musalasoft.drones.medication.Medication;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "drone")
public class Drone {

    @Id
    @GeneratedValue
    @Column(name = "drone_id")
    private Long  droneId;


    //Assuming serial number is unique
    @Column(unique = true, nullable = false)
    @NotEmpty
    @Size(max = 100 , min = 1)
    private String serialNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Model model;


    @Max(500)
    private int weightLimit;


    @Max(100)
    private int batteryCapacity;

    @Column(name = "drone_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private State droneState;

    @CreationTimestamp
    private LocalDateTime registeredAt;


    @OneToMany
    @JoinColumn(name = "drone_id")
    @JsonManagedReference
    @ToString.Exclude
    private Set<Medication> medications;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Drone drone = (Drone) o;
        return getDroneId() != null && Objects.equals(getDroneId(), drone.getDroneId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
