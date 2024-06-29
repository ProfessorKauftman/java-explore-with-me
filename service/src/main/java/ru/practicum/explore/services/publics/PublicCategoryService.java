package ru.practicum.explore.services.publics;

import org.springframework.data.domain.Pageable;
import ru.practicum.explore.dto.category.CategoryDto;

import java.util.Collection;

public interface PublicCategoryService {
    Collection<CategoryDto> getAllCategories(Pageable pageable);

    CategoryDto getCategory(Long catId);
}