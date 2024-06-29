package ru.practicum.explore.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explore.models.EndpointHit;
import ru.practicum.explore.models.HitEntity;

import java.time.LocalDateTime;

import static ru.practicum.explore.handler.ErrorHandlerStatistic.FORMATTER;

@UtilityClass
public class HitEntityMapper {

    public HitEntity mapToHit(EndpointHit endpointHit) {
        return new HitEntity(endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                LocalDateTime.parse(endpointHit.getTimestamp(), FORMATTER));
    }

    public EndpointHit mapToEndpointHit(HitEntity hitEntity) {
        return new EndpointHit(hitEntity.getId(),
                hitEntity.getApp(),
                hitEntity.getUri(),
                hitEntity.getIp(),
                hitEntity.getDates().format(FORMATTER));
    }
}