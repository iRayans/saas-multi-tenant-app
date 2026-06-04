package com.rayan.saasapp.services.impl;

import com.rayan.saasapp.common.PageResponse;
import com.rayan.saasapp.entites.Category;
import com.rayan.saasapp.excpetions.DuplicateResourceException;
import com.rayan.saasapp.mappers.CategoryMapper;
import com.rayan.saasapp.repositories.CategoryRepository;
import com.rayan.saasapp.requests.CategoryRequest;
import com.rayan.saasapp.response.CategoryResponse;
import com.rayan.saasapp.services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    @Override
    public void create(CategoryRequest request) {
        checkIfCategoryExistsByName(request.getName());
        final Category entity = this.mapper.toEntity(request);
        this.categoryRepository.save(entity);
    }

    @Override
    public void update(String id, CategoryRequest request) {
        final Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            log.error("Category not found");
            throw new EntityNotFoundException("Category not found");
        }
        final Category category = existingCategory.get();

        // Check if the name already exists.
        if (!category.getName().equalsIgnoreCase(request.getName())) {
            checkIfCategoryExistsByName(request.getName());
        }
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        categoryRepository.save(category);
    }

    @Override
    public void delete(String id) {
        final Category category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found"));
        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponse findById(String id) {
        return this.categoryRepository.findById(id)
                .map(this.mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    @Override
    public PageResponse<CategoryResponse> findAll(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Category> categoryPage = this.categoryRepository.findAll(pageRequest);
        final Page<CategoryResponse> categoryResponsePage = categoryPage.map(category -> mapper.toResponse(category));
        return PageResponse.of(categoryResponsePage);
    }


    private void checkIfCategoryExistsByName(final String name) {
        final Optional<Category> category = this.categoryRepository.findByNameIgnoreCase(name);
        if (category.isPresent()) {
            log.debug("Category already exists");
            throw new DuplicateResourceException("Category already exists");
        }
    }
}
