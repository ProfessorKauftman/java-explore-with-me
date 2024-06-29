package ru.practicum.explore.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.exceptions.InvalidRequestException;
import ru.practicum.explore.mappers.HitEntityMapper;
import ru.practicum.explore.models.EndpointHit;
import ru.practicum.explore.models.HitEntity;
import ru.practicum.explore.models.ViewStats;
import ru.practicum.explore.repositories.EndpointHitRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.explore.handler.ErrorHandlerStatistic.FORMATTER;

@Service
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;

    @Transactional
    @Override
    public EndpointHit create(EndpointHit endpointHit) {
        HitEntity hit = HitEntityMapper.mapToHit(endpointHit);

        return HitEntityMapper.mapToEndpointHit(endpointHitRepository.save(hit));
    }


    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> getAll(String startDate, String endDate, List<String> uris, boolean unique) {
        LocalDateTime start = LocalDateTime.parse(decode(startDate), FORMATTER);
        LocalDateTime end = LocalDateTime.parse(decode(endDate), FORMATTER);

        if (start.isAfter(end)) {
            throw new InvalidRequestException("The dates in the range are incorrect");
        }

        if (uris == null && !unique) {
            return endpointHitRepository.getAllStats(start, end);
        }

        if (!unique) {
            return endpointHitRepository.getStatsWithUris(start, end, uris);
        }

        if (uris == null && unique) {
            return endpointHitRepository.getStatsWithUniqueViews(start, end);
        }

        return endpointHitRepository.getStatsWithUrisAndUniqueViews(start, end, uris);

    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}