package ru.practicum.explore.dto.friends;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.models.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendsDto {
    private Long id;
    private User user;
    private User friend;
    private Boolean friendshipStat;
}
