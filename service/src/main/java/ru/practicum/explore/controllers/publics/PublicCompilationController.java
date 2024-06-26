package ru.practicum.explore.controllers.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.services.publics.PublicCompilationService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {
    private final PublicCompilationService publicCompilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(from / size, size);

        return publicCompilationService.getAllCompilations(pinned, pageable);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilation(@PathVariable Long compId) {
        return publicCompilationService.getCompilation(compId);
    }

}
