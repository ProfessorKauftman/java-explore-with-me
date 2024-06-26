package ru.practicum.explore.controllers.privates;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.EventShortDto;
import ru.practicum.explore.dto.event.NewEventDto;
import ru.practicum.explore.dto.event.UpdateEventUserRequest;
import ru.practicum.explore.dto.event_request.EventRequestStatusUpdateRequest;
import ru.practicum.explore.dto.event_request.EventRequestStatusUpdateResult;
import ru.practicum.explore.dto.event_request.ParticipationRequestDto;
import ru.practicum.explore.services.privates.PrivateEventService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{initiatorId}/events")
public class PrivateEventController {
    private final PrivateEventService privateEventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventShortDto> getAllEvents(@PathVariable Long initiatorId,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        return privateEventService.getAllEvents(initiatorId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long initiatorId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        return privateEventService.createEvent(initiatorId, newEventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable Long initiatorId,
                                 @PathVariable Long eventId) {
        return privateEventService.getEvent(initiatorId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable Long initiatorId,
                                    @PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return privateEventService.updateEvent(initiatorId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ParticipationRequestDto> getEventRequests(@PathVariable Long initiatorId,
                                                                @PathVariable Long eventId) {
        return privateEventService.getEventRequests(initiatorId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long initiatorId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return privateEventService.updateRequestStatus(initiatorId, eventId, eventRequestStatusUpdateRequest);
    }


}
