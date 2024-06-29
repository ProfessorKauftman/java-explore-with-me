package ru.practicum.explore.services.admins;

import org.springframework.data.domain.Pageable;
import ru.practicum.explore.dto.user.NewUserRequest;
import ru.practicum.explore.dto.user.UserDto;

import java.util.Collection;
import java.util.List;

public interface AdminUserService {
    Collection<UserDto> getAllUsers(List<Long> ids, Pageable pageable);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}