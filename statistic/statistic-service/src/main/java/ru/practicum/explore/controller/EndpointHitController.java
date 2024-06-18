package ru.practicum.explore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.model.EndpointHit;
import ru.practicum.explore.model.ViewStats;
import ru.practicum.explore.service.EndpointHitService;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class EndpointHitController {

    private static final String DATE = "yyyy-MM-dd HH:mm:ss";
    private final EndpointHitService endpointHitService;

    @PostMapping("/hit")
    public ResponseEntity<String> create(@RequestBody EndpointHit endpointHit) {
        endpointHitService.create(endpointHit);
        return new ResponseEntity<>("Information saved", HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public Collection<ViewStats> readAll(@RequestParam @DateTimeFormat(pattern = DATE) LocalDateTime start,
                                         @RequestParam @DateTimeFormat(pattern = DATE) LocalDateTime end,
                                         @RequestParam(required = false) Collection<String> uris,
                                         @RequestParam(defaultValue = "false") Boolean unique) {
        return endpointHitService.readAll(start, end, uris, unique);
    }
}
