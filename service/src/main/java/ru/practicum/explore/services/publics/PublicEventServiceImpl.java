package ru.practicum.explore.services.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.StatsClient;
import ru.practicum.explore.component.DataSearcher;
import ru.practicum.explore.dto.EventsParamsReader;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.EventShortDto;
import ru.practicum.explore.enums.EventLifecycle;
import ru.practicum.explore.exceptions.InvalidRequestException;
import ru.practicum.explore.exceptions.NotFoundException;
import ru.practicum.explore.mappers.EventMapper;
import ru.practicum.explore.models.EndpointHit;
import ru.practicum.explore.models.Event;
import ru.practicum.explore.models.ViewStats;
import ru.practicum.explore.repositories.EventRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.explore.handler.ErrorHandlerService.FORMATTER;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final DataSearcher dataSearcher;
    private final StatsClient statsClient;

    @Override
    public Collection<EventShortDto> getAllEvents(EventsParamsReader params, Pageable pageable) {
        if (params.getRangeStart() != null && params.getRangeEnd() != null) {
            if (params.getRangeStart().isAfter(params.getRangeEnd())) {
                throw new InvalidRequestException("The dates in the range are incorrect!");
            }
        }

        Collection<Event> events = eventRepository.getEventsByUserParameters(
                params.getText(),
                params.getCategories(),
                params.getPaid(),
                params.getRangeStart(),
                params.getRangeEnd(),
                pageable
        ).getContent();

        if (params.getOnlyAvailable()) {
            events = events.stream()
                    .filter(event -> event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        statsClient.create(new EndpointHit("ewm-main-service",
                params.getRequest().getRequestURI(),
                params.getRequest().getRemoteAddr(),
                LocalDateTime.now().format(FORMATTER)));

        return events.stream()
                .map(EventMapper::mapToEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        Event event = dataSearcher.findEventById(eventId);

        if (!event.getState().equals(EventLifecycle.PUBLISHED.toString())) {
            throw new NotFoundException("The event has to be published");
        }

        statsClient.create(new EndpointHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now().format(FORMATTER)));
        event.setViews(countViews(request));
        eventRepository.save(event);

        return EventMapper.mapToEventFullDto(event);

    }

    private long countViews(HttpServletRequest request) {
        List<ViewStats> stats = statsClient.getAll(
                LocalDateTime.now().minusYears(10).format(FORMATTER),
                LocalDateTime.now().plusHours(1).format(FORMATTER),
                List.of(request.getRequestURI()),
                true);

        return !stats.isEmpty() ? stats.get(0).getHits() : 0;
    }
}