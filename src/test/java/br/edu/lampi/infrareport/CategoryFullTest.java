package br.edu.lampi.infrareport;

import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.google.protobuf.Method;

import br.edu.lampi.infrareport.config.ExceptionDetails;
import br.edu.lampi.infrareport.controller.dto.category.CategoryRequestDto;
import br.edu.lampi.infrareport.controller.dto.category.CategoryResponseDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginRequestDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginResponseDTO;
import br.edu.lampi.infrareport.controller.dto.user.UserRequestDTO;
import br.edu.lampi.infrareport.model.category.Category;
import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.service.CategoryService;
import br.edu.lampi.infrareport.service.UserService;
import br.edu.lampi.infrareport.service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CategoryFullTest extends InfrareportApplicationTests {

    @Autowired
    CategoryService categoryService;

    @Autowired
    UserService userService;

    private final static String CATEGORY_URL = "/category";


    @Test
    @Transactional
    void service_shouldReturnCategorySaved() {
        CategoryRequestDto requestCategoryDto = exampleOfCategoryDto();

        Category savedCategory = categoryService.save(requestCategoryDto);

        Assertions.assertNotNull(savedCategory);
        Assertions.assertEquals(requestCategoryDto.name(), savedCategory.getName());
    }

    @Test
    @Transactional
    void service_shouldReturnCategoryWithId() {
        CategoryRequestDto expectCategory = exampleOfCategoryDto();

        Category savedCategory = categoryService.save(expectCategory);

        Category returnCategory = categoryService.getById(savedCategory.getId());

        Assertions.assertEquals(expectCategory.name(), returnCategory.getName());
    }

    @Test
    @Transactional
    void service_shouldThrowRunTimeExceptionWhenIdIsNull() {
        Assertions.assertThrows(RuntimeException.class, () -> categoryService.getById(null));
    }

    @Test
    @Transactional
    void service_shouldThrowRunTimeExceptionWhenCategoryIsNotFound() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.getById(1L));
    }

    @Test
    @Transactional
    void service_shouldReturnAllCategories() {
        CategoryRequestDto expectCategoryDto = exampleOfCategoryDto();

        categoryService.save(expectCategoryDto);
        List<Category> responseCategoriesList = categoryService.getAll();

        Assertions.assertFalse(responseCategoriesList.isEmpty());
        Assertions.assertEquals(1, responseCategoriesList.size());
        Assertions.assertEquals(exampleOfCategory().getId(), responseCategoriesList.get(0).getId());
        Assertions.assertEquals(exampleOfCategory().getName(), responseCategoriesList.get(0).getName());

    }

    @Test
    @Transactional
    void service_shouldReturnEmptyListCategories() {
        List<Category> responseCategoriesList = categoryService.getAll();

        Assertions.assertTrue( responseCategoriesList.isEmpty());
    }

    @Test
    @Transactional
    void service_shouldReturnUpdatedCategoryInMethodUpdate() {
        CategoryRequestDto requestCategoryDto = exampleOfCategoryDto();
        Category savedCategory = categoryService.save(requestCategoryDto);

        Category resultCategory = categoryService.update(savedCategory.getId(), new CategoryRequestDto(savedCategory.getName()));

        Assertions.assertEquals(savedCategory.getName(), resultCategory.getName());
    }

    @Test
    @Transactional
    void service_shouldReturnUpdatedCategoryWhenCategoryIsNotFoundInMethodUpdate() {

        Category expectCategoryDto = exampleOfCategory(12L, "Test");

        Category resultCategory = categoryService.update(expectCategoryDto.getId(), exampleOfCategoryDto());

        Assertions.assertNotNull(resultCategory);
        Assertions.assertNotEquals(expectCategoryDto.getId(), resultCategory.getId());
        Assertions.assertEquals(expectCategoryDto.getName(), resultCategory.getName());
    }

    @Test
    @Transactional
    void service_shouldDeleteCategory() {
        Category savedcategory = categoryService.save(exampleOfCategoryDto());
        categoryService.delete(savedcategory.getId());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryService.getById(savedcategory.getId()));
    }

    @Test
    void controller_shouldReturnResponseEntityOfSavedCategoryWithHttpStatusOkWhenCategoryIsSave() {
        CategoryRequestDto expectedCategoryDto = exampleOfCategoryDto("Teste");
        HttpHeaders header = getAuthorizationTokenHeader();
        HttpEntity<CategoryRequestDto> entity = new HttpEntity<CategoryRequestDto>(expectedCategoryDto, header);

        ResponseEntity<CategoryResponseDTO> responseEntityCategory = restTemplate.postForEntity(getRootUrl() + CATEGORY_URL, 
            entity, CategoryResponseDTO.class);

        Assertions.assertNotNull(responseEntityCategory);
        Assertions.assertEquals(HttpStatus.CREATED, responseEntityCategory.getStatusCode());
        Assertions.assertEquals(expectedCategoryDto.name(), responseEntityCategory.getBody().getName());
    }

    @Test
    void controller_shouldThrowRunTimeExceptionWhenNameIsAlready() {
        CategoryRequestDto expectedCategoryDto = exampleOfCategoryDto("teste");
        HttpHeaders header = getAuthorizationTokenHeader();
        

        Category savedCategory = categoryService.save(expectedCategoryDto);

        HttpEntity<CategoryRequestDto> entity = new HttpEntity<CategoryRequestDto>( new CategoryRequestDto(savedCategory.getName()), header);

        ResponseEntity<CategoryResponseDTO> categoryResponseEntity = restTemplate.postForEntity(getRootUrl() + CATEGORY_URL,
                entity, CategoryResponseDTO.class);

        Assertions.assertEquals(HttpStatus.CONFLICT, categoryResponseEntity.getStatusCode());
    }

    @Test
    void controller_shouldReturnCategoryGetById() {
        CategoryRequestDto savedCategoryDto = exampleOfCategoryDto();
        HttpHeaders header = getAuthorizationTokenHeader();
        HttpEntity<CategoryRequestDto> entity = new HttpEntity<CategoryRequestDto>(savedCategoryDto, header);

        ResponseEntity<CategoryResponseDTO> categoryResponseEntity = restTemplate.postForEntity(getRootUrl() + CATEGORY_URL,
        entity, CategoryResponseDTO.class);

        Long id = Objects.requireNonNull(categoryResponseEntity.getBody()).getId();

        ResponseEntity<CategoryRequestDto> categoryDtoResponseEntity = restTemplate.exchange(getRootUrl() + CATEGORY_URL + "/" + id, HttpMethod.GET, entity,
                CategoryRequestDto.class);

        Assertions.assertNotNull(categoryResponseEntity);

        Assertions.assertEquals(categoryResponseEntity.getBody().getName(), categoryDtoResponseEntity.getBody().name());
    }

    @Test
    void controller_shouldThrowRunTimeExceptionWhenCategoryDoesExistWithId() {
        HttpHeaders header = getAuthorizationTokenHeader();
        HttpEntity<ExceptionDetails> entity = new HttpEntity<ExceptionDetails>(null, header);
        ResponseEntity<ExceptionDetails> exceptionDetails = restTemplate.exchange(getRootUrl() + CATEGORY_URL + "/1000000",  HttpMethod.GET, entity,
                ExceptionDetails.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), exceptionDetails.getBody().getStatus());
    }

    @Test
    void controller_shouldReturnAllCategories() throws Exception {
        HttpHeaders header = getAuthorizationTokenHeader();
        Category expectedCategory = exampleOfCategory();

        categoryService.save(exampleOfCategoryDto());

        HttpEntity<String> entity = new HttpEntity<String>(null, header);

        ResponseEntity<String> responseCategoriesList = restTemplate.exchange(getRootUrl() + CATEGORY_URL,
                HttpMethod.GET, entity, String.class);

        JSONArray listCategory = new JSONArray(responseCategoriesList.getBody());
        Assertions.assertNotNull(responseCategoriesList.getBody());
        Assertions.assertEquals(expectedCategory.getId(), listCategory.getJSONObject(0).getLong("id"));
        Assertions.assertEquals(expectedCategory.getName(), listCategory.getJSONObject(0).getString("name"));
    }

    @Test
    void controller_shouldReturnEmptyListCategories() throws Exception {
        HttpHeaders header = getAuthorizationTokenHeader();
        HttpEntity<String> entity = new HttpEntity<String>(null, header);
        ResponseEntity<String> responseCategoriesList = restTemplate.exchange(getRootUrl() + CATEGORY_URL,
                HttpMethod.GET, entity, String.class);

        JSONArray jsonArray = new JSONArray(responseCategoriesList.getBody());
        Assertions.assertEquals(0, jsonArray.length());

    }

    @Test
    void controller_shouldReturnResponseEntityOfUpdatedCategoryWithHttpStatusOKInMethodUpdate() {
        HttpHeaders header = getAuthorizationTokenHeader();
        CategoryRequestDto requestCategoryDto = exampleOfCategoryDto();

        Category savedCategory = categoryService.save(requestCategoryDto);

        HttpEntity<CategoryRequestDto> entity = new HttpEntity<>(requestCategoryDto, header);

        ResponseEntity<String> categoryResponseEntity = restTemplate.exchange(
                getRootUrl() + CATEGORY_URL + "/" + savedCategory.getId(), HttpMethod.PUT, entity, String.class);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, categoryResponseEntity.getStatusCode());
    }

    @Test
    void controller_shouldThrowRunTimeExceptionWhenIdIsDifferentInRelationToTheSavedName() {
        HttpHeaders header = getAuthorizationTokenHeader();
        CategoryRequestDto requestCategoryDto = exampleOfCategoryDto(4L, "Teste");

        categoryService.save(exampleOfCategoryDto(2L, "Teste"));

        HttpEntity<CategoryRequestDto> entity = new HttpEntity<>(requestCategoryDto, header);

        ResponseEntity<String> categoryResponseEntity = restTemplate.exchange(getRootUrl() + CATEGORY_URL + "/" + "4",
                HttpMethod.PUT, entity, String.class);
        Assertions.assertEquals(HttpStatus.CONFLICT, categoryResponseEntity.getStatusCode());
    }

    @Test
    void controller_shouldDeleteCategoryById() {
        HttpHeaders header = getAuthorizationTokenHeader();
        HttpEntity<Void> entity = new HttpEntity<Void>(null, header);
        CategoryRequestDto requestCategoryDto = exampleOfCategoryDto();

        Category savedCategory = categoryService.save(requestCategoryDto);

        restTemplate.exchange(getRootUrl() + CATEGORY_URL + "/" + savedCategory.getId(), HttpMethod.DELETE, entity, Void.class);

        ResponseEntity<ExceptionDetails> exeption = restTemplate.exchange(getRootUrl() + CATEGORY_URL + "/" + savedCategory.getId(),  HttpMethod.DELETE, entity, ExceptionDetails.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), exeption.getBody().getStatus());
    }

    @Test
    void controller_shouldThrowRunTimeExceptionWhenCategoryIsNotExistInMethodDelete() {
        HttpHeaders header = getAuthorizationTokenHeader();
        HttpEntity<ExceptionDetails> entity = new HttpEntity<ExceptionDetails>(null, header);
        ResponseEntity<ExceptionDetails> exeption = restTemplate.exchange(getRootUrl() + CATEGORY_URL + "/" + 100000, HttpMethod.DELETE, entity, ExceptionDetails.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), exeption.getBody().getStatus());
    }

    private Category exampleOfCategory() {
        Category category = new Category();

        category.setId(1L);
        category.setName("Test");

        return category;
    }

    private Category exampleOfCategory(Long id, String name) {
        Category category = new Category();

        category.setId(id);
        category.setName(name);

        return category;
    }

    private Category exampleOfCategory(String name) {
        Category category = new Category();

        category.setId(1L);
        category.setName(name);

        return category;
    }

    private CategoryRequestDto exampleOfCategoryDto() {

        return new CategoryRequestDto("Test");
    }

    private CategoryRequestDto exampleOfCategoryDto(Long id, String name) {

        return new CategoryRequestDto(name);
    }

    private CategoryRequestDto exampleOfCategoryDto(String name) {

        return new CategoryRequestDto(name);
    }

    private HttpHeaders getAuthorizationTokenHeader(){
        /* Realizando login com o admin criado no metodo run da classe principal: */
        LoginRequestDTO loginRequest = new LoginRequestDTO("admin@gmail.com", "123456");
        ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como admin: */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+loginResponse.getBody().token());
		return headers;
	}
}
