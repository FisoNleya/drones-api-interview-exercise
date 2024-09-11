package com.musalasoft.drones.medication;

import com.musalasoft.drones.medication.dtos.MedicationRequest;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/medication")
@RequiredArgsConstructor
public class MedicationController {


    private final MedicationService medicationService;

    @PostMapping("/registration")
    public ResponseEntity<Medication> addMedication(
            @RequestBody @Valid MedicationRequest request
    ) {
        return new ResponseEntity<>(medicationService.addMedication(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<Medication>> getAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy
    ) {
        return new ResponseEntity<>(medicationService.findAll(page, size, sortBy), HttpStatus.OK);
    }

    @DeleteMapping("{code}/available")
    public ResponseEntity<Void> delete(
            @PathVariable @Parameter(example = "MDC_32A") String code
    ) {
        medicationService.deleteAvailableMedication(code);
        return ResponseEntity.noContent().build();
    }

}
