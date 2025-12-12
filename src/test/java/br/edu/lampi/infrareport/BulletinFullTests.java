package br.edu.lampi.infrareport;

import br.edu.lampi.infrareport.controller.dto.bulletin.BulletinRequestDTO;
import br.edu.lampi.infrareport.controller.dto.bulletin.BulletinResponseDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginRequestDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginResponseDTO;
import br.edu.lampi.infrareport.controller.dto.user.UserRequestDTO;
import br.edu.lampi.infrareport.model.bulletin.Bulletin;
import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.service.UserService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Objects;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BulletinFullTests extends InfrareportApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = getRootUrl() + "/bulletins";
    }

    private HttpHeaders getHeadersForAdmin(UserRequestDTO userRequest, String password) {
        User savedUser = userService.saveAdmin(userRequest);
        LoginRequestDTO loginRequest = new LoginRequestDTO(savedUser.getEmail(), password);
        ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginRequest, LoginResponseDTO.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + Objects.requireNonNull(loginResponse.getBody()).token());
        return headers;
    }

    private HttpHeaders getHeadersForUser(UserRequestDTO userRequest, String password) {
        User savedUser = userService.save(userRequest);
        LoginRequestDTO loginRequest = new LoginRequestDTO(savedUser.getEmail(), password);
        ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginRequest, LoginResponseDTO.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + Objects.requireNonNull(loginResponse.getBody()).token());
        return headers;
    }

    @Test
    void testCreateBulletinAsAdmin() {
        HttpHeaders headers = getHeadersForAdmin(new UserRequestDTO("admin", "admin123@gmail.com", "123456"), "123456");

        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(new BulletinRequestDTO("Test Title", "Test message"), headers);
        ResponseEntity<Bulletin> response = restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Test Title", response.getBody().getTitle());
        Assertions.assertEquals("Test message", response.getBody().getMessage());
    }

    @Test
    void testCreateBulletinAsCommonUser() {
        HttpHeaders headers = getHeadersForUser(new UserRequestDTO("user", "user@gmail.com", "123456"), "123456");

        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(new BulletinRequestDTO("Test Title", "Test message"), headers);
        ResponseEntity<Bulletin> response = restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testCreateBulletinAsUnauthenticatedUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(new BulletinRequestDTO("Test Title", "Test message"), headers);
        ResponseEntity<Bulletin> response = restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testGetBulletinByIdAsAdmin() {
        HttpHeaders headers = getHeadersForAdmin(new UserRequestDTO("admin", "admin123@gmail.com", "123456"), "123456");

        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(new BulletinRequestDTO("Test Title", "Test message"), headers);
        ResponseEntity<Bulletin> postResponse = restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        Long id = Objects.requireNonNull(postResponse.getBody()).getId();

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<Bulletin> getResponse = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.GET, getEntity, Bulletin.class);

        Assertions.assertNotNull(getResponse.getBody());
        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertEquals(id, getResponse.getBody().getId().longValue());
    }

    @Test
    void testGetBulletinByIdAsCommonUser() {
        HttpHeaders adminHeaders = getHeadersForAdmin(new UserRequestDTO("admin", "admin123@gmail.com", "123456"), "123456");

        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(new BulletinRequestDTO("Test Title", "Test message"), adminHeaders);
        ResponseEntity<Bulletin> postResponse = restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        Long id = Objects.requireNonNull(postResponse.getBody()).getId();

        HttpHeaders userHeaders = getHeadersForUser(new UserRequestDTO("user", "user@gmail.com", "123456"), "123456");
        HttpEntity<Void> getEntity = new HttpEntity<>(userHeaders);
        ResponseEntity<Bulletin> getResponse = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.GET, getEntity, Bulletin.class);

        Assertions.assertNotNull(getResponse.getBody());
        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertEquals(id, getResponse.getBody().getId().longValue());
    }

    @Test
    void testGetBulletinByIdAsUnauthenticatedUser() {
        HttpEntity<Void> getEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Bulletin> getResponse = restTemplate.exchange(baseUrl + "/" + 1L, HttpMethod.GET, getEntity, Bulletin.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, getResponse.getStatusCode());
    }

    @Test
    void testGetAllBulletinsAsAdmin() {
        HttpHeaders headers = getHeadersForAdmin(new UserRequestDTO("admin", "admin123@gmail.com", "123456"), "123456");

        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(new BulletinRequestDTO("Test Title", "Test message"), headers);
        restTemplate.postForEntity(baseUrl, entity, Bulletin.class);
        restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<RestResponsePage<BulletinResponseDTO>> response = restTemplate.exchange(
                baseUrl + "?page=0&size=10",
                HttpMethod.GET,
                getEntity,
                new ParameterizedTypeReference<>() {}
        );

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(2, response.getBody().getTotalElements());
    }

    @Test
    void testGetAllBulletinsAsCommonUser() {
        HttpHeaders adminHeaders = getHeadersForAdmin(new UserRequestDTO("admin", "admin123@gmail.com", "123456"), "123456");

        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(new BulletinRequestDTO("Test Title", "Test message"), adminHeaders);
        restTemplate.postForEntity(baseUrl, entity, Bulletin.class);
        restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        HttpHeaders userHeaders = getHeadersForUser(new UserRequestDTO("user", "user@gmail.com", "123456"), "123456");
        HttpEntity<Void> getEntity = new HttpEntity<>(userHeaders);
        ResponseEntity<RestResponsePage<BulletinResponseDTO>> response = restTemplate.exchange(
                baseUrl + "?page=0&size=10",
                HttpMethod.GET,
                getEntity,
                new ParameterizedTypeReference<>() {}
        );

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(2, response.getBody().getTotalElements());
    }

    @Test
    void testGetAllBulletinsAsUnauthenticatedUser() {
        HttpEntity<Void> getEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Bulletin[]> response = restTemplate.exchange(baseUrl, HttpMethod.GET, getEntity, Bulletin[].class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testUpdateBulletinAsAdmin() {
        HttpHeaders headers = getHeadersForAdmin(new UserRequestDTO("admin", "admin123@gmail.com", "123456"), "123456");

        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(new BulletinRequestDTO("Test Title", "Test message"), headers);
        ResponseEntity<Bulletin> postResponse = restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        Long id = Objects.requireNonNull(postResponse.getBody()).getId();

        BulletinRequestDTO updatedBulletinDTO = new BulletinRequestDTO("Updated Title", "Updated message");
        HttpEntity<BulletinRequestDTO> updateEntity = new HttpEntity<>(updatedBulletinDTO, headers);
        restTemplate.exchange(baseUrl + "/" + id, HttpMethod.PUT, updateEntity, Void.class);

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<Bulletin> getResponse = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.GET, getEntity, Bulletin.class);

        Assertions.assertNotNull(getResponse.getBody());
        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertEquals(id, getResponse.getBody().getId().longValue());
        Assertions.assertEquals("Updated Title", getResponse.getBody().getTitle());
        Assertions.assertEquals("Updated message", getResponse.getBody().getMessage());
    }

    @Test
    void testUpdateBulletinAsCommonUser() {
        HttpHeaders adminHeaders = getHeadersForAdmin(new UserRequestDTO("admin", "admin123@gmail.com", "123456"), "123456");

        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(new BulletinRequestDTO("Test Title", "Test message"), adminHeaders);
        ResponseEntity<Bulletin> postResponse = restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        Long id = Objects.requireNonNull(postResponse.getBody()).getId();

        HttpHeaders userHeaders = getHeadersForUser(new UserRequestDTO("user", "user@gmail.com", "123456"), "123456");
        BulletinRequestDTO updatedBulletinDTO = new BulletinRequestDTO("Updated Title", "Updated message");
        HttpEntity<BulletinRequestDTO> updateEntity = new HttpEntity<>(updatedBulletinDTO, userHeaders);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.PUT, updateEntity, String.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testUpdateBulletinAsUnauthenticatedUser() {
        BulletinRequestDTO updatedBulletinDTO = new BulletinRequestDTO("Updated Title", "Updated message");
        HttpEntity<BulletinRequestDTO> updateEntity = new HttpEntity<>(updatedBulletinDTO, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + 1L, HttpMethod.PUT, updateEntity, String.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testDeleteBulletinAsAdmin() {
        HttpHeaders headers = getHeadersForAdmin(new UserRequestDTO("admin", "admin123@gmail.com", "123456"), "123456");

        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(new BulletinRequestDTO("Test Title", "Test message"), headers);
        ResponseEntity<Bulletin> postResponse = restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        Long id = Objects.requireNonNull(postResponse.getBody()).getId();

        HttpEntity<Void> deleteEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.DELETE, deleteEntity, Void.class);

        Assertions.assertNull(deleteResponse.getBody());
        Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }

    @Test
    void testDeleteBulletinAsCommonUser() {
        HttpHeaders adminHeaders = getHeadersForAdmin(new UserRequestDTO("admin", "admin123@gmail.com", "123456"), "123456");

        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(new BulletinRequestDTO("Test Title", "Test message"), adminHeaders);
        ResponseEntity<Bulletin> postResponse = restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        Long id = Objects.requireNonNull(postResponse.getBody()).getId();

        HttpHeaders userHeaders = getHeadersForUser(new UserRequestDTO("user", "user@gmail.com", "123456"), "123456");
        HttpEntity<Void> deleteEntity = new HttpEntity<>(userHeaders);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.DELETE, deleteEntity, String.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testDeleteBulletinAsUnauthenticatedUser() {
        HttpEntity<Void> deleteEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/" + 1L, HttpMethod.DELETE, deleteEntity, Void.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testCreateBulletinWithErrorAsAdmin() {
        HttpHeaders headers = getHeadersForAdmin(new UserRequestDTO("admin", "admin123@gmail.com", "123456"), "123456");

        BulletinRequestDTO badRequest = new BulletinRequestDTO("", "");
        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(badRequest, headers);
        ResponseEntity<Bulletin> response = restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateBulletinWithErrorAsCommonUser() {
        HttpHeaders headers = getHeadersForUser(new UserRequestDTO("user", "user@gmail.com", "123456"), "123456");

        BulletinRequestDTO badRequest = new BulletinRequestDTO("", "");
        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(badRequest, headers);
        ResponseEntity<Bulletin> response = restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testCreateBulletinWithErrorAsUnauthenticatedUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        BulletinRequestDTO badRequest = new BulletinRequestDTO("", "");
        HttpEntity<BulletinRequestDTO> entity = new HttpEntity<>(badRequest, headers);
        ResponseEntity<Bulletin> response = restTemplate.postForEntity(baseUrl, entity, Bulletin.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testGetBulletinByIdNotFoundAsAdmin() {
        HttpHeaders headers = getHeadersForAdmin(new UserRequestDTO("admin", "admin123@gmail.com", "123456"), "123456");

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/9999", HttpMethod.GET, getEntity, String.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Bulletin not found with the given id."));
    }

    @Test
    void testGetBulletinByIdNotFoundAsCommonUser() {
        HttpHeaders headers = getHeadersForUser(new UserRequestDTO("user", "user@gmail.com", "123456"), "123456");

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/9999", HttpMethod.GET, getEntity, String.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Bulletin not found with the given id."));
    }

    @Test
    void testGetBulletinByIdNotFoundAsUnauthenticatedUser() {
        HttpEntity<Void> getEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/9999", HttpMethod.GET, getEntity, String.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testUpdateBulletinNotFoundAsAdmin() {
        HttpHeaders headers = getHeadersForAdmin(new UserRequestDTO("admin", "admin123@gmail.com", "123456"), "123456");

        BulletinRequestDTO updatedBulletinDTO = new BulletinRequestDTO("Non-existent Title", "No message");
        HttpEntity<BulletinRequestDTO> updateEntity = new HttpEntity<>(updatedBulletinDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/9999", HttpMethod.PUT, updateEntity, String.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Bulletin not found with the given id."));
    }

    @Test
    void testUpdateBulletinNotFoundAsCommonUser() {
        HttpHeaders headers = getHeadersForUser(new UserRequestDTO("user", "user@gmail.com", "123456"), "123456");

        BulletinRequestDTO updatedBulletinDTO = new BulletinRequestDTO("Non-existent Title", "No message");
        HttpEntity<BulletinRequestDTO> updateEntity = new HttpEntity<>(updatedBulletinDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/9999", HttpMethod.PUT, updateEntity, String.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testUpdateBulletinNotFoundAsUnauthenticatedUser() {
        BulletinRequestDTO updatedBulletinDTO = new BulletinRequestDTO("Non-existent Title", "No message");
        HttpEntity<BulletinRequestDTO> updateEntity = new HttpEntity<>(updatedBulletinDTO, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/9999", HttpMethod.PUT, updateEntity, String.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testDeleteBulletinNotFoundAsAdmin() {
        HttpHeaders headers = getHeadersForAdmin(new UserRequestDTO("admin", "admin123@gmail.com", "123456"), "123456");

        HttpEntity<Void> deleteEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/9999", HttpMethod.DELETE, deleteEntity, String.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Bulletin not found with the given id."));
    }

    @Test
    void testDeleteBulletinNotFoundAsCommonUser() {
        HttpHeaders headers = getHeadersForUser(new UserRequestDTO("user", "user@gmail.com", "123456"), "123456");

        HttpEntity<Void> deleteEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/9999", HttpMethod.DELETE, deleteEntity, Void.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testDeleteBulletinNotFoundAsUnauthenticatedUser() {
        HttpEntity<Void> deleteEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/9999", HttpMethod.DELETE, deleteEntity, Void.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @JsonIgnoreProperties({"pageable", "sort"})
    public static class RestResponsePage<T> extends PageImpl<T> {

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public RestResponsePage(@JsonProperty("content") List<T> content,
                                @JsonProperty("number") int number,
                                @JsonProperty("size") int size,
                                @JsonProperty("totalElements") Long totalElements,
                                @JsonProperty("last") boolean last,
                                @JsonProperty("totalPages") int totalPages,
                                @JsonProperty("sort") Sort sort,
                                @JsonProperty("first") boolean first,
                                @JsonProperty("numberOfElements") int numberOfElements) {
            super(content, Pageable.unpaged(), totalElements);
        }

        public RestResponsePage(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }

        public RestResponsePage(List<T> content) {
            super(content);
        }
    }
}