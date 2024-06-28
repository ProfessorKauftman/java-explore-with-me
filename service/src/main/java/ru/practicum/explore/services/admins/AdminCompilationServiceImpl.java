package ru.practicum.explore.services.admins;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.dto.compilation.NewCompilationDto;
import ru.practicum.explore.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explore.mappers.CompilationMapper;
import ru.practicum.explore.models.Compilation;
import ru.practicum.explore.models.Event;
import ru.practicum.explore.repositories.CompilationRepository;
import ru.practicum.explore.repositories.EventRepository;
import ru.practicum.explore.component.DataSearcher;
import ru.practicum.explore.utility.UtilityMergeProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final DataSearcher dataSearcher;

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.mapToCompilation(newCompilationDto);
        compilation.setEvents(getEvents(newCompilationDto.getEvents()));

        return CompilationMapper.mapToCompilationDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        dataSearcher.findCompilationById(compId);
        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationToUpdate = dataSearcher.findCompilationById(compId);

        UtilityMergeProperty.copyProperties(updateCompilationRequest, compilationToUpdate);
        compilationToUpdate.setEvents(getEvents(updateCompilationRequest.getEvents()));

        return CompilationMapper.mapToCompilationDto(compilationRepository.save(compilationToUpdate));
    }

    private Collection<Event> getEvents(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return new ArrayList<>();
        }

        return eventRepository.getAllByIdIn(eventIds);
    }
}