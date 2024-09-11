package com.musalasoft.drones.events;

import com.musalasoft.drones.drone.Drone;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_log")
public class EventLog {

    @Id
    @GeneratedValue
    @Column(name = "log_id")
    private Long  logId;


    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "drone_id",
            nullable = false,
            referencedColumnName = "drone_id",
            foreignKey = @ForeignKey(name = "drone_id_fk"))
    private Drone drone;

    private int batterCapacityAtLogTime;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EventLog eventLog = (EventLog) o;
        return getLogId() != null && Objects.equals(getLogId(), eventLog.getLogId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
