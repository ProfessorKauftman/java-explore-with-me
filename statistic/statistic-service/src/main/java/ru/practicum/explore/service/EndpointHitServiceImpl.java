package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.mapper.HitEntityMapper;
import ru.practicum.explore.model.EndpointHit;
import ru.practicum.explore.model.ViewStats;
import ru.practicum.explore.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional
public class EndpointHitServiceImpl implements EndpointHitService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    public void create(EndpointHit endpointHit) {
        endpointHitRepository.save(HitEntityMapper.mapToHit(endpointHit));
    }

    @Override
    public Collection<ViewStats> readAll(LocalDateTime start, LocalDateTime end, Collection<String> uris,
                                         boolean unique) {

        if (uris == null && !unique) {
            return endpointHitRepository.readAllStats(start, end);
        } else if (!unique) {
            return endpointHitRepository.readStatsWithUris(start, end, uris);
        } else if (uris == null && unique) {
            return endpointHitRepository.readStatsWithUniqueViews(start, end);
        } else {
            return endpointHitRepository.readStatsWithUrisAndUniqueViews(start, end, uris);
        }
    }
}
