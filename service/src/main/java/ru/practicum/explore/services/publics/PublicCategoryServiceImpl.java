package ru.practicum.explore.services.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.mappers.CategoryMapper;
import ru.practicum.explore.models.Category;
import ru.practicum.explore.repositories.CategoryRepository;
import ru.practicum.explore.component.DataSearcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicCategoryServiceImpl implements PublicCategoryService {

    private final CategoryRepository categoryRepository;
    private final DataSearcher dataSearcher;

    @Override
    public Collection<CategoryDto> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);

        if (categories.getContent().isEmpty()) {
            return new ArrayList<>();
        }

        return categories.getContent().stream()
                .map(CategoryMapper::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = dataSearcher.findCategoryById(catId);

        return CategoryMapper.mapToCategoryDto(category);
    }
}