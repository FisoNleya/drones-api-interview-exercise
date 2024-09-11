package com.musalasoft.drones.medication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

    Set<Medication> findByCodeInAndAvailable(Set<String> medicationCodes, boolean available);

    Optional<Medication> findMedicationByCode(String medicationCode);

    Optional<Medication> findMedicationByCodeAndAvailable(String medicationCode, boolean available);


}