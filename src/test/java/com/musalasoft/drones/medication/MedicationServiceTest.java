package com.musalasoft.drones.medication;

import com.musalasoft.drones.drone.Drone;
import com.musalasoft.drones.drone.enums.Model;
import com.musalasoft.drones.drone.enums.State;
import com.musalasoft.drones.exceptions.DataNotFoundException;
import com.musalasoft.drones.exceptions.DuplicateRecordException;
import com.musalasoft.drones.medication.dtos.MedicationRequest;
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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.atomicIntegerFieldUpdater;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicationServiceTest {

    @Mock
    private MedicationRepository mockMedicationRepository;

    private MedicationService medicationServiceUnderTest;

    @BeforeEach
    void setUp() {
        medicationServiceUnderTest = new MedicationService(mockMedicationRepository);
    }

    @Test
    void testAddMedication() {
        // Setup
        final MedicationRequest request = new MedicationRequest("name", 500, "MDC_33A", "imageUrl");

        // Configure MedicationRepository.findMedicationByCode(...).
        final Optional<Medication> medication = Optional.of(new Medication(1L, "name", 500, false, "MDC_33A", "imageUrl",
                new Drone(1L, "MDC_32A", Model.LIGHT_WEIGHT, 0, 0, State.IDLE,
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0), Set.of())));
        when(mockMedicationRepository.findMedicationByCode("MDC_33A")).thenReturn(medication);


        // Run the test
        // Verify the results
        assertThatThrownBy(()->medicationServiceUnderTest.addMedication(request)).isInstanceOf(DuplicateRecordException.class);

    }

    @Test
    void testAddMedication_MedicationRepositoryFindMedicationByCodeReturnsAbsent() {
        // Setup
        final MedicationRequest request = new MedicationRequest("name", 0, "MDC_33A", "imageUrl");
        final Medication expectedResult = new Medication(1L, "name", 500, false, "MDC_33A", "imageUrl",null);
        when(mockMedicationRepository.findMedicationByCode("MDC_33A")).thenReturn(Optional.empty());

        // Configure MedicationRepository.save(...).
        final Medication medication = new Medication(1L, "name", 500, false, "MDC_33A", "imageUrl",null);
        when(mockMedicationRepository.save(any(Medication.class))).thenReturn(medication);

        // Run the test
        final Medication result = medicationServiceUnderTest.addMedication(request);


        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindAll() {
        // Setup
        // Configure MedicationRepository.findAll(...).
        final Page<Medication> medications = new PageImpl<>(
                List.of(new Medication(1L, "name", 0, false, "MDC_33A", "imageUrl",
                        null)));
        when(mockMedicationRepository.findAll(any(Pageable.class))).thenReturn(medications);

        // Run the test
        final Page<Medication> result = medicationServiceUnderTest.findAll(0, 10, "sort");

        // Verify the results
        assertThat(result.getContent()).isEqualTo(medications.getContent());
    }

    @Test
    void testFindAll_MedicationRepositoryReturnsNoItems() {
        // Setup
        when(mockMedicationRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final Page<Medication> result = medicationServiceUnderTest.findAll(0, 10, "sort");

        // Verify the results
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void testFindAllAvailableByCodes() {
        // Setup
        final Set<Medication> expectedResult = Set.of(new Medication(1L, "name", 300,
                true, "MDC_33A", "imageUrl",null));

        // Configure MedicationRepository.findByCodeInAndAvailable(...).
        final Set<Medication> medications = Set.of(new Medication(1L, "name", 300,
                true, "MDC_33A", "imageUrl",null));
        when(mockMedicationRepository.findByCodeInAndAvailable(Set.of("MDC_33A"), true)).thenReturn(medications);

        // Run the test
        final Set<Medication> result = medicationServiceUnderTest.findAllAvailableByCodes(Set.of("MDC_33A"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindAllAvailableByCodes_MedicationRepositoryReturnsNoItems() {
        // Setup
        when(mockMedicationRepository.findByCodeInAndAvailable(Set.of("MDC_33A"), true))
                .thenReturn(Collections.emptySet());

        // Run the test
        final Set<Medication> result = medicationServiceUnderTest.findAllAvailableByCodes(Set.of("MDC_33A"));

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptySet());
    }

    @Test
    void testUpdateMedicationAvailability() {
        // Setup
        final Set<Medication> medications = Set.of(new Medication(1L, "name", 300, false, "MDC_33A", "imageUrl",
                new Drone(1L, "MDC_32A", Model.LIGHT_WEIGHT, 300, 59, State.IDLE,
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0), Set.of())));

        // Configure MedicationRepository.saveAll(...).
        when(mockMedicationRepository.saveAll(any(Set.class))).thenReturn(List.of(medications));

        // Run the test
        medicationServiceUnderTest.updateMedicationAvailability(medications, false);

        // Verify the results
        verify(mockMedicationRepository).saveAll(any(Set.class));
    }

    @Test
    void testFindAvailableMedication() {
        // Setup
        final Medication expectedResult = new Medication(1L, "name", 300, false, "MDC_33A", "imageUrl",null);

        // Configure MedicationRepository.findMedicationByCodeAndAvailable(...).
        final Optional<Medication> medication = Optional.of(new Medication(1L, "name", 300, false, "MDC_33A", "imageUrl",null));
        when(mockMedicationRepository.findMedicationByCodeAndAvailable("MDC_33A", true)).thenReturn(medication);

        // Run the test
        final Medication result = medicationServiceUnderTest.findAvailableMedication("MDC_33A");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindAvailableMedication_MedicationRepositoryReturnsAbsent() {
        // Setup
        when(mockMedicationRepository.findMedicationByCodeAndAvailable("MDC_33A", true))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> medicationServiceUnderTest.findAvailableMedication("MDC_33A"))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void testDeleteAvailableMedication() {
        // Setup
        // Configure MedicationRepository.findMedicationByCodeAndAvailable(...).
        final Optional<Medication> medication = Optional.of(
                new Medication(1L, "name", 0, false, "MDC_33A", "imageUrl", null));
        when(mockMedicationRepository.findMedicationByCodeAndAvailable("MDC_33A", true)).thenReturn(medication);

        // Run the test
        medicationServiceUnderTest.deleteAvailableMedication("MDC_33A");

        // Verify the results
        verify(mockMedicationRepository).delete(any(Medication.class));
    }

    @Test
    void testDeleteAvailableMedication_MedicationRepositoryFindMedicationByCodeAndAvailableReturnsAbsent() {
        // Setup
        when(mockMedicationRepository.findMedicationByCodeAndAvailable("MDC_33A", true))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> medicationServiceUnderTest.deleteAvailableMedication("MDC_33A"))
                .isInstanceOf(DataNotFoundException.class);
    }
}
