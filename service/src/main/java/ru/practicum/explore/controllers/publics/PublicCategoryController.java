package ru.practicum.explore.controllers.publics;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.category.CategoryDto;
import ru.practicum.explore.services.publics.PublicCategoryService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoryController {
    private final PublicCategoryService publicCategoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        return publicCategoryService.getAllCategories(pageable);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getEvent(@PathVariable Long catId) {
        return publicCategoryService.getCategory(catId);
    }
}
