package com.vivero.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.ProductDTOs.*;
import com.vivero.entity.Category;
import com.vivero.exception.ResourceNotFoundException;
import com.vivero.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    @Transactional
    public void initDefaultCategories() {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(Category.builder().name("Grass Natural").description("Mantas de grass en rollos por metro cuadrado (m²)").iconName("Sprout").active(true).build());
            categoryRepository.save(Category.builder().name("Plantas Ornamentales").description("Plantas de interior y exterior por unidades").iconName("Flower2").active(true).build());
            categoryRepository.save(Category.builder().name("Árboles y Palmeras").description("Árboles para jardín y palmeras decorativas").iconName("Trees").active(true).build());
            categoryRepository.save(Category.builder().name("Accesorios e Insumos").description("Tierra preparada, macetas y abonos").iconName("Package").active(true).build());
        }
    }

    public List<CategoryDTO> getAllCategories() {
        if (categoryRepository.count() == 0) {
            initDefaultCategories();
        }
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> getActiveCategories() {
        if (categoryRepository.count() == 0) {
            initDefaultCategories();
        }
        return categoryRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO createCategory(CreateCategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .iconName(request.getIconName() != null ? request.getIconName() : "FolderTree")
                .active(true)
                .build();
        return mapToDTO(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CreateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        category.setName(request.getName());
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getIconName() != null) category.setIconName(request.getIconName());

        return mapToDTO(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDTO toggleCategoryStatus(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        category.setActive(!Boolean.TRUE.equals(category.getActive()));
        return mapToDTO(categoryRepository.save(category));
    }

    private CategoryDTO mapToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .iconName(category.getIconName())
                .active(category.getActive())
                .build();
    }
}
