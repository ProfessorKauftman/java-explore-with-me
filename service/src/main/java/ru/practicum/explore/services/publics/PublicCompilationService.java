package ru.practicum.explore.services.publics;

import org.springframework.data.domain.Pageable;
import ru.practicum.explore.dto.compilation.CompilationDto;

import java.util.Collection;

public interface PublicCompilationService {
    Collection<CompilationDto> getAllCompilations(Boolean pinned, Pageable pageable);

    CompilationDto getCompilation(Long compId);
}