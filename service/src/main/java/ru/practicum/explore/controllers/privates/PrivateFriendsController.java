package ru.practicum.explore.controllers.privates;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.friends.FriendEventsDto;
import ru.practicum.explore.dto.friends.FriendsDto;
import ru.practicum.explore.dto.user.UserShortDto;
import ru.practicum.explore.services.privates.PrivateFriendsService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/friends")
public class PrivateFriendsController {
    private final PrivateFriendsService privateFriendsService;

    @PostMapping("/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public FriendsDto addFriend(@PathVariable Long userId,
                                @PathVariable Long friendId) {
        return privateFriendsService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteFriend(@PathVariable Long userId,
                                               @PathVariable Long friendId) {

        privateFriendsService.deleteFriend(userId, friendId);

        return new ResponseEntity<>("Friend was deleted", HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserShortDto> getAllFriends(@PathVariable Long userId) {
        return privateFriendsService.getAllFriends(userId);
    }

    @GetMapping("/feed")
    @ResponseStatus(HttpStatus.OK)
    public Collection<FriendEventsDto> getFriendsEvents(@PathVariable Long userId) {
        return privateFriendsService.getFriendsEvents(userId);
    }
}
