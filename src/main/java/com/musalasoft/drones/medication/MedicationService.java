package com.musalasoft.drones.medication;

import com.musalasoft.drones.exceptions.DataNotFoundException;
import com.musalasoft.drones.exceptions.DuplicateRecordException;
import com.musalasoft.drones.medication.dtos.MedicationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MedicationService {


    private final MedicationRepository medicationRepository;


    public Medication addMedication(MedicationRequest request) {

        Optional<Medication> optionalDrone = medicationRepository.findMedicationByCode(request.code());
        if (optionalDrone.isPresent()) {
            throw new DuplicateRecordException("Medication already exists with this code : " + request.code());
        }

        return medicationRepository.save(Medication.builder()
                .name(request.name())
                .weight(request.weight())
                .available(true)
                .code(request.code())
                .imageUrl(request.imageUrl())
                .build());
    }



    public Page<Medication> findAll(int page, int size , String sort){

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        return medicationRepository.findAll(pageable);
    }


    public Set<Medication> findAllAvailableByCodes(Set<String> medicCodes){
        return medicationRepository.findByCodeInAndAvailable(medicCodes, true);
    }


    public void updateMedicationAvailability(Set<Medication> medications, boolean isAvailable){
        medications.parallelStream().forEach(medication -> medication.setAvailable(isAvailable));
        medicationRepository.saveAll(medications);
    }


    public Medication findAvailableMedication(String medicationCode){
        return medicationRepository.findMedicationByCodeAndAvailable(medicationCode, true)
                .orElseThrow(
                        ()-> new DataNotFoundException("Medication not available or does not exist with code : "+ medicationCode));
    }

    public void deleteAvailableMedication(String medicationCode){

        Medication medication = findAvailableMedication(medicationCode);
        medicationRepository.delete(medication);
    }

}
