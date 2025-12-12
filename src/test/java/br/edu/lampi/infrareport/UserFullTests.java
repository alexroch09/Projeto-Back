package br.edu.lampi.infrareport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.lampi.infrareport.config.ExceptionDetails;
import br.edu.lampi.infrareport.controller.dto.user.LoginRequestDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginResponseDTO;
import br.edu.lampi.infrareport.controller.dto.user.UserRequestDTO;
import br.edu.lampi.infrareport.controller.dto.user.UserResponseDTO;
import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.model.user.UserType;
import br.edu.lampi.infrareport.repository.UserRepository;
import br.edu.lampi.infrareport.service.UserService;
import jakarta.transaction.Transactional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserFullTests extends InfrareportApplicationTests{
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final String CALL_USER_URL = "/user";

    //private HttpHeaders headers;

    @Test
    public void model_instantiateUserAndCheckIfValid() {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setUserType(List.of(UserType.ROLE_COMMON));
        user.setEmail("john@gmail.com");
        user.setPassword("12345");

        assertEquals(1L, user.getId());
        assertEquals("John", user.getName());
        assertEquals(List.of(UserType.ROLE_COMMON), user.getUserType());
        assertEquals("john@gmail.com", user.getEmail());
        assertEquals("12345", user.getPassword());
    }

    @Test
    @Transactional
    public void service_save_saveUserAndCheckIfValid() {
        UserRequestDTO userRequest = new UserRequestDTO("John", "john@gmail.com", "12345");
        User savedUser = userService.save(userRequest);

        Assertions.assertNotNull(savedUser);
        assertEquals(userRequest.name(), savedUser.getName());
        assertEquals(userRequest.email(), savedUser.getEmail());
        Assertions.assertNotEquals(userRequest.password(), savedUser.getPassword());
        Assertions.assertNotNull(savedUser.getId());
        Assertions.assertNotEquals("0", savedUser.getId());
    }

    @Test
    @Transactional
    public void service_save_throwsRuntimeExceptionIfEmailIsAlreadyInUse() {
        UserRequestDTO userRequest = new UserRequestDTO("John", "john@gmail.com", "12345");
        userService.save(userRequest);

        Assertions.assertThrows(RuntimeException.class, () -> userService.save(userRequest));
    }

    @Test
    @Transactional
    public void service_searchById_saveUserAndGetCreatedUserByIdThenVerifyIfEqualCreated() {
        UserRequestDTO userRequest = new UserRequestDTO("John", "john@gmail.com", "12345");
        User savedUser = userService.save(userRequest);
        Long savedId = savedUser.getId();

        User returnedUser = userService.searchById(savedId);

        Assertions.assertNotNull(returnedUser);
        assertEquals(savedUser.getName(), returnedUser.getName());
        assertEquals(savedUser.getUserType(), returnedUser.getUserType());
        assertEquals(savedUser.getEmail(), returnedUser.getEmail());
        assertEquals(savedUser.getPassword(), returnedUser.getPassword());
        assertEquals(savedUser.getId(), returnedUser.getId());
    }

    @Test
    @Transactional
    public void service_searchById_throwsRuntimeExceptionIfNoUserWereFound() {
        Assertions.assertThrows(RuntimeException.class, () -> userService.searchById(10000L));
    }

    @Test
    @Transactional
    public void service_deleteAndSearchById_saveUserAndDeleteCreatedUserByIdThenVerifyIfExists() {
        UserRequestDTO userRequest = new UserRequestDTO("John", "john@gmail.com", "12345");
        User savedUser = userService.save(userRequest);
        Long savedId = savedUser.getId();

        userService.delete(savedId);

        User deactivatedUser = userRepository.findById(savedId).orElse(null);
        Assertions.assertNotNull(deactivatedUser);
        Assertions.assertFalse(deactivatedUser.isEnabled());
    }

    @Test
    @Transactional
    public void service_delete_throwRuntimeExceptionIfNoStatusWereFound() {
        Assertions.assertThrows(RuntimeException.class, () -> userService.delete(10000L));
    }

    @Test
    @Transactional
    public void service_update_saveUserAndUpdateUserThenVerifyIfValid() {
        UserRequestDTO userRequest = new UserRequestDTO("John", "john@gmail.com", "12345");
        User savedUser = userService.save(userRequest);
        Long savedId = savedUser.getId();

        UserRequestDTO userRequestToUpdate = new UserRequestDTO("John Doyle", "john@gmail.com", "12345");
        User updatedUser = userService.update(savedId, userRequestToUpdate);

        Assertions.assertNotNull(updatedUser);
        assertEquals(savedId, updatedUser.getId());
        assertEquals(userRequestToUpdate.name(), updatedUser.getName());
        assertEquals(userRequestToUpdate.email(), updatedUser.getEmail());
        Assertions.assertNotEquals(userRequestToUpdate.password(), updatedUser.getPassword());
    }

    @Test
    @Transactional
    public void service_update_throwRuntimeExceptionIfNoUserWereFound() {
        UserRequestDTO userRequestToUpdate = new UserRequestDTO("John Doyle", "john@gmail.com", "12345");
        Assertions.assertThrows(RuntimeException.class, () -> userService.update(100000L, userRequestToUpdate));
    }

    @Test
    @Transactional
    public void service_update_throwRuntimeExceptionIfUserEmailIsAlreadyInUse() {
        UserRequestDTO userRequest = new UserRequestDTO("John", "john@gmail.com", "12345");
        User savedUser = userService.save(userRequest);

        UserRequestDTO userRequestToByInConflict = new UserRequestDTO("John Doyle", "johndoyle@gmail.com", "12345");
        userService.save(userRequestToByInConflict);
        Long savedId = savedUser.getId();

        UserRequestDTO userRequestToUpdate = new UserRequestDTO("John", "johndoyle@gmail.com", "12345");
        Assertions.assertThrows(RuntimeException.class, () -> userService.update(savedId, userRequestToUpdate));
    }

    @Test
    public void mockRestController_save_saveUserAndVerifyIfPostResponseIsValidTeam() {
        UserRequestDTO userRequest = new UserRequestDTO("John", "john1@gmail.com", "12345");
        ResponseEntity<UserResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_USER_URL, userRequest, UserResponseDTO.class);
        assertEquals(CREATED, postResponse.getStatusCode());
        Assertions.assertNotNull(postResponse);
        Assertions.assertNotEquals(0, postResponse.getBody().id());
        assertEquals(userRequest.name(), postResponse.getBody().name());
        assertEquals(userRequest.email(), postResponse.getBody().email());
    }

    @Test
    public void mockRestController_searchById_createUserAndSearchByIdThenVerifyIfEqualCreated() throws URISyntaxException {
        HttpHeaders headers = getAuthorizationTokenHeader();
        UserRequestDTO userRequest = new UserRequestDTO("John", "john2@gmail.com", "12345");

        HttpEntity<UserRequestDTO> entity = new HttpEntity<UserRequestDTO>(userRequest, headers);
        ResponseEntity<UserResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_USER_URL, entity, UserResponseDTO.class);
        long id = postResponse.getBody().id();

        ResponseEntity<UserResponseDTO> returnedUser = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + CALL_USER_URL + "/" + id)).headers(headers).build(), UserResponseDTO.class);
        Assertions.assertNotNull(returnedUser);
        assertEquals(userRequest.name(), returnedUser.getBody().name());
        assertEquals(userRequest.email(), returnedUser.getBody().email());
    }

    @Test
    public void mockRestController_searchById_returnExceptionDetailsWithNotFoundUser() throws URISyntaxException {
        ResponseEntity<ExceptionDetails> exceptionDetails = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + CALL_USER_URL + "/" + 100000L)).headers(getAuthorizationTokenHeader()).build(), ExceptionDetails.class);

        assertEquals(NOT_FOUND.value(), exceptionDetails.getBody().getStatus());
    }

    @Test
    public void mockRestController_update_saveUserAndUpdateThenGetSearchAndVerifyIfUpdateWasValid() throws JsonMappingException, JsonProcessingException {
        UserRequestDTO userRequest = new UserRequestDTO("John", "john3@gmail.com", "12345");
        UserRequestDTO userRequestToUpdate = new UserRequestDTO("John Doyle", "johndoyle@gmail.com", "12345");
        ResponseEntity<UserResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_USER_URL, userRequest, UserResponseDTO.class);
        long expectedId = postResponse.getBody().id();

        HttpEntity<UserRequestDTO> entity = new HttpEntity<>(userRequestToUpdate, getAuthorizationTokenHeader());

        ResponseEntity<String> responseEntity = restTemplate.exchange(getRootUrl() + CALL_USER_URL + "/" + expectedId, HttpMethod.PUT, entity, String.class);

        assertEquals(OK, responseEntity.getStatusCode());

        UserResponseDTO updatedUser = objectMapper.readValue(responseEntity.getBody(), UserResponseDTO.class);
        assertEquals(expectedId, updatedUser.id());
        assertEquals(userRequestToUpdate.name(), updatedUser.name());
    }

    @Test
    public void mockRestController_update_returnExceptionDetailsWithNotFoundStatusIfNoUserWereFound() throws JsonMappingException, JsonProcessingException {
        UserRequestDTO userRequestToUpdate = new UserRequestDTO("John Doyle", "johndoyle4@gmail.com", "12345");
        HttpEntity<UserRequestDTO> entity = new HttpEntity<>(userRequestToUpdate, getAuthorizationTokenHeader());

        ResponseEntity<String> responseEntity = restTemplate.exchange(getRootUrl() + CALL_USER_URL + "/10000", HttpMethod.PUT, entity, String.class);

        assertEquals(NOT_FOUND, responseEntity.getStatusCode());

        ExceptionDetails exceptionDetails = objectMapper.readValue(responseEntity.getBody(), ExceptionDetails.class);
        Assertions.assertEquals(NOT_FOUND.value(), exceptionDetails.getStatus());
    }

    @Test
    public void mockRestController_update_returnExceptionDetailsWithConflictStatusIfUserEmailIsAlreadyInUse() throws JsonMappingException, JsonProcessingException {
        HttpHeaders headers = getAuthorizationTokenHeader();
        UserRequestDTO userRequest = new UserRequestDTO("John", "john5@gmail.com", "12345");
        UserRequestDTO userRequestToUpdate = new UserRequestDTO("John Doyle", "johndoyle6@gmail.com", "12345");
        
        HttpEntity<UserRequestDTO> entity = new HttpEntity<>(userRequestToUpdate, headers);
        restTemplate.postForEntity(getRootUrl() + CALL_USER_URL, entity, UserResponseDTO.class);

        entity = new HttpEntity<>(userRequest, headers);
        ResponseEntity<UserResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_USER_URL, entity, UserResponseDTO.class);
        long expectedId = postResponse.getBody().id();

        entity = new HttpEntity<>(userRequestToUpdate, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(getRootUrl() + CALL_USER_URL + "/" + expectedId, HttpMethod.PUT, entity, String.class);

        assertEquals(CONFLICT, responseEntity.getStatusCode());

        ExceptionDetails exceptionDetails = objectMapper.readValue(responseEntity.getBody(), ExceptionDetails.class);
        Assertions.assertEquals(CONFLICT.value(), exceptionDetails.getStatus());
    }

    @Test
    public void mockRestController_delete_saveUSerAndDeleteThenVerifyIfDontExistsAndReturnAnExceptionsDetailsWithNotFoundStatus() throws URISyntaxException {
        HttpHeaders headers = this.getAuthorizationTokenHeader();
        UserRequestDTO userRequest = new UserRequestDTO("John", "john7@gmail.com", "12345");
        HttpEntity<UserRequestDTO> entity = new HttpEntity<>(userRequest, headers);
        ResponseEntity<UserResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_USER_URL, entity, UserResponseDTO.class);
        Long expectedId = postResponse.getBody().id();

        restTemplate.exchange(getRootUrl() + CALL_USER_URL + "/" + expectedId, HttpMethod.DELETE, entity, Void.class);

        ResponseEntity<ExceptionDetails> exceptionDetails = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + CALL_USER_URL + "/" + 1000L)).headers(headers).build(),ExceptionDetails.class);

        Assertions.assertEquals(NOT_FOUND.value(), exceptionDetails.getBody().getStatus());
    }

    @Test
    public void mockRestController_delete_returnExceptionDetailsWithNotFoundStatusIfNoUserWereFound() {
        HttpEntity<UserRequestDTO> entity = new HttpEntity<>(null, getAuthorizationTokenHeader());

        ResponseEntity<ExceptionDetails> response = restTemplate.exchange(getRootUrl() + CALL_USER_URL +"/100000", HttpMethod.DELETE, entity, ExceptionDetails.class);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_FOUND.value(), response.getBody().getStatus());
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
