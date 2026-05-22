package com.rayan.saasapp.controllers;

import com.rayan.saasapp.requests.CategoryRequest;
import com.rayan.saasapp.response.CategoryResponse;
import com.rayan.saasapp.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    final CategoryService service;

    @PostMapping
    public ResponseEntity<Void> createCategory(@Valid @RequestBody final CategoryRequest request) {
        this.service.create(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("{category-id}")
    public ResponseEntity<Void> updateCategory(@Valid @RequestBody final CategoryRequest request, @PathVariable("category-id") final String id) {
        this.service.update(id, request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("{category-id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable("category-id") final String id) {
        this.service.findById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok().body(this.service.findAll());
    }

    @DeleteMapping("{category-id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("category-id") final String id) {
        this.service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
