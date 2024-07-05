package ru.practicum.explore.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explore.dto.friends.FriendsDto;
import ru.practicum.explore.models.Friends;

@UtilityClass
public class FriendMapper {

    public FriendsDto mapToFriendsDto(Friends friends) {
        return new FriendsDto(
                friends.getId(),
                friends.getUser(),
                friends.getFriend(),
                friends.getFriendshipStat());
    }
}
