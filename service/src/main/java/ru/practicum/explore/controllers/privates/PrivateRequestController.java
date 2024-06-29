package ru.practicum.explore.controllers.privates;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.event_request.ParticipationRequestDto;
import ru.practicum.explore.exceptions.InvalidRequestException;
import ru.practicum.explore.services.privates.PrivateRequestService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users/{requesterId}/requests")
public class PrivateRequestController {
    private final PrivateRequestService privateRequestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ParticipationRequestDto> getAllRequests(@PathVariable Long requesterId) {

        return privateRequestService.getAllRequests(requesterId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long requesterId,
                                                 @RequestParam(required = false) Long eventId) {
        if (eventId == null) {
            throw new InvalidRequestException("The event does not exist");
        }

        return privateRequestService.createRequest(requesterId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable Long requesterId,
                                                 @PathVariable Long requestId) {
        return privateRequestService.cancelRequest(requesterId, requestId);
    }
}
