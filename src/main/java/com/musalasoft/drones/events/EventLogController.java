package com.musalasoft.drones.events;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/eventlog")
@RequiredArgsConstructor
public class EventLogController {


    private final EventLogService eventLogService;

    @GetMapping
    public ResponseEntity<Page<EventLog>> getAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        return new ResponseEntity<>(eventLogService.findAll(page, size, sortBy), HttpStatus.OK);
    }


}
