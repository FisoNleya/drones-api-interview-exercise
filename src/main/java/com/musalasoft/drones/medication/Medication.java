package com.musalasoft.drones.medication;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.musalasoft.drones.drone.Drone;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medication")
public class Medication {

    @Id
    @GeneratedValue
    @Column(name = "medication_id")
    private Long  medicationId;

    @Column(name = "medication_name")
    @Pattern(regexp = "[a-zA-Z0-9-_]+", message = "Medication name must contain only letters, numbers, ‘-‘, ‘_’")
    @NotEmpty
    private String name;

    @Min(1)
    @Max(500)
    private int weight;

    private boolean available;


    //Assuming medication code is unique per this case
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "[A-Z0-9_]+", message = "Medication code must contain only upper case letters, underscore and numbers")
    @NotEmpty
    private String code;

    @Column(name = "image_url",columnDefinition = "TEXT")
    private String imageUrl;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drone_id", insertable = false, updatable = false)
    @ToString.Exclude
    @JsonBackReference
    public Drone drone;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Medication that = (Medication) o;
        return getMedicationId() != null && Objects.equals(getMedicationId(), that.getMedicationId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
