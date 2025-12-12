package br.edu.lampi.infrareport.controller;

import br.edu.lampi.infrareport.controller.dto.category.CategoryRequestDto;
import br.edu.lampi.infrareport.controller.dto.category.CategoryResponseDTO;
import br.edu.lampi.infrareport.model.category.Category;
import br.edu.lampi.infrareport.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @Operation(summary = "return saved categoryDto", tags = "Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Saved success"),
            @ApiResponse(responseCode = "203", description = "No content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing data to authenticate", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized, unauthorized resource", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    public ResponseEntity<CategoryResponseDTO> save(@RequestBody CategoryRequestDto categoryDto, UriComponentsBuilder uriComponentsBuilder) {
        Category savedCategory = categoryService.save(categoryDto);
        URI uri = uriComponentsBuilder.path("/category/{id}").buildAndExpand(savedCategory.getId()).toUri();
        return ResponseEntity.created(uri).body(new CategoryResponseDTO(savedCategory.getId(), savedCategory.getName()));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "return CategoryDto filtered by id", tags = "Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return a category filtered"),
            @ApiResponse(responseCode = "203", description = "No content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing data to authenticate", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized, unauthorized resource", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable("id") Long id) {
        Category category = categoryService.getById(id);
        return new ResponseEntity<>(new CategoryResponseDTO(category.getId(), category.getName()), HttpStatusCode.valueOf(200));
    }

    @GetMapping
    @Operation(summary = "return a list of saved categories", tags = "Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return all categories", content = @Content(mediaType = "application/json", array =
            @ArraySchema(schema = @Schema(implementation = CategoryRequestDto.class)))),
            @ApiResponse(responseCode = "203", description = "No content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing data to authenticate", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized, unauthorized resource", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    public List<CategoryResponseDTO> getAll() {
        return categoryService.getAll()
                .stream()
                .map(res -> new CategoryResponseDTO(res.getId(), res.getName()))
                .toList();
    }

    @Operation(summary = "update data for a category", tags = "Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entity updated successfully", content = @Content),
            @ApiResponse(responseCode = "203", description = "No content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing data to authenticate", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized, unauthorized resource", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity update(@PathVariable("id") Long id, @Valid @RequestBody CategoryRequestDto categoryDto) {
        Category updateCategory = categoryService.update(id, categoryDto);
        return new ResponseEntity<>(updateCategory, HttpStatusCode.valueOf(204));
    }

    @Operation(summary = "Delete a category by id", tags = "Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully delete a category", content = @Content),
            @ApiResponse(responseCode = "203", description = "No content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing data to authenticate", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized, unauthorized resource", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
