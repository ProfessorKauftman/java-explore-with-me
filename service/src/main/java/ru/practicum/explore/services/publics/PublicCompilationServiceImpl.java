package ru.practicum.explore.services.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.mappers.CompilationMapper;
import ru.practicum.explore.models.Compilation;
import ru.practicum.explore.repositories.CompilationRepository;
import ru.practicum.explore.component.DataSearcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;
    private final DataSearcher dataSearcher;

    @Transactional(readOnly = true)
    @Override
    public Collection<CompilationDto> getAllCompilations(Boolean pinned, Pageable pageable) {
        Collection<Compilation> compilations = (pinned != null) ?
                compilationRepository.getAllByPinned(pinned, pageable).getContent() :
                compilationRepository.findAll(pageable).getContent();

        if (compilations.isEmpty()) {
            return new ArrayList<>();
        }

        return compilations.stream()
                .map(CompilationMapper::mapToCompilationDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = dataSearcher.findCompilationById(compId);

        return CompilationMapper.mapToCompilationDto(compilation);
    }
}