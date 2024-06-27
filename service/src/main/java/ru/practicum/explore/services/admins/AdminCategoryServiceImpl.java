package ru.practicum.explore.services.admins;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.dto.category.NewCategoryDto;
import ru.practicum.explore.exceptions.MismatchException;
import ru.practicum.explore.mappers.CategoryMapper;
import ru.practicum.explore.models.Category;
import ru.practicum.explore.models.Event;
import ru.practicum.explore.repositories.CategoryRepository;
import ru.practicum.explore.repositories.EventRepository;
import ru.practicum.explore.component.DataSearcher;
import ru.practicum.explore.utility.UtilityMergeProperty;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final DataSearcher dataSearcher;

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.mapToCategory(newCategoryDto);

        return CategoryMapper.mapToCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long catId) {
        dataSearcher.findCategoryById(catId);

        Collection<Event> eventsByCategory = eventRepository.getAllByCategoryId(catId);

        if (!eventsByCategory.isEmpty()) {
            throw new MismatchException("The category isn't empty");
        }

        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category categoryToUpdate = dataSearcher.findCategoryById(catId);

        UtilityMergeProperty.copyProperties(categoryDto, categoryToUpdate);

        return CategoryMapper.mapToCategoryDto(categoryRepository.save(categoryToUpdate));
    }
}