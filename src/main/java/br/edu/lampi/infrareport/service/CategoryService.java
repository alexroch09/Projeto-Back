package br.edu.lampi.infrareport.service;

import br.edu.lampi.infrareport.controller.dto.category.CategoryRequestDto;
import br.edu.lampi.infrareport.model.category.Category;
import br.edu.lampi.infrareport.repository.CategoryRepository;

import br.edu.lampi.infrareport.service.exceptions.ConflictException;
import br.edu.lampi.infrareport.service.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category save(CategoryRequestDto categoryDto) {
        if(categoryRepository.findByName(categoryDto.name()) != null){
            throw new ConflictException("category name already in use");
        }

        Category category = new Category();

        category.setName(categoryDto.name());

        return categoryRepository.save(category);

    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.orElseThrow(() -> new ResourceNotFoundException("category not found by the given id"));
    }

    @Transactional
    public Category update(Long id, CategoryRequestDto categoryDto) {
        Category resultCategory = categoryRepository.findByName(categoryDto.name());

        if (resultCategory != null && !resultCategory.getId().equals(id)) {
            throw new ConflictException("category name already in use");
        }

        Category category = categoryRepository.findById(id).orElse(null);

        if (category != null) {
            category.setName(categoryDto.name());

            return categoryRepository.save(category);
        }
        return save(categoryDto);
    }

    public void delete(Long id) {
        Category resultCategory = getById(id);

        categoryRepository.delete(resultCategory);
    }
}
