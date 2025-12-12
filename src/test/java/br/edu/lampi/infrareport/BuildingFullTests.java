package br.edu.lampi.infrareport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import br.edu.lampi.infrareport.config.ExceptionDetails;
import br.edu.lampi.infrareport.controller.dto.building.BuildingRequestDTO;
import br.edu.lampi.infrareport.controller.dto.building.BuildingResponseDTO;
import br.edu.lampi.infrareport.controller.dto.building.BuildingUpdateDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginRequestDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginResponseDTO;
import br.edu.lampi.infrareport.model.building.Building;
import br.edu.lampi.infrareport.service.BuildingService;
import br.edu.lampi.infrareport.service.exceptions.ConflictException;
import jakarta.transaction.Transactional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BuildingFullTests extends InfrareportApplicationTests {
    @Autowired
    private BuildingService buildingService;

    @Test
    public void model_instantiateBuildingAndCheckIfValid() {
        Building b = new Building();
        b.setId(23L);
        b.setName("bloco 1");
        Assertions.assertEquals(
                "Building(id=23, name=bloco 1, floors=null)", b.toString());
    }

    @Test
    @Transactional
    public void service_save_saveBuildingAndCheckIfValid() {
        Building building = new Building();
        building.setName("Example Building");
        building.setFloors(new ArrayList<>());

        Building savedBuilding = buildingService.saveNewBuilding(building);

        Assertions.assertNotNull(savedBuilding);
        Assertions.assertEquals("Example Building", savedBuilding.getName());
        Assertions.assertNotNull(savedBuilding.getFloors());
        Assertions.assertEquals(0, savedBuilding.getFloors().size());
        Assertions.assertNotNull(savedBuilding.getId());
        Assertions.assertNotEquals(0, savedBuilding.getId());
    }

    @Test
    @Transactional
    public void service_save_throwExceptionIfNameExists() {
        Building existingBuilding = new Building();
        existingBuilding.setName("Existing Building");
        existingBuilding.setFloors(new ArrayList<>());

        buildingService.saveNewBuilding(existingBuilding);

        Building newBuilding = new Building();
        newBuilding.setName("Existing Building");

        Assertions.assertThrows(ConflictException.class, () -> {
            buildingService.saveNewBuilding(newBuilding);
        });
    }


    @Test
    @Transactional
    public void service_getById_saveBuildingAndGetCreatedBuildingByIdThenVerifyIfEqualCreated() {
        Building newBuilding = new Building();
        newBuilding.setName("Example Building");
        newBuilding.setFloors(new ArrayList<>());
        Building savedBuilding = buildingService.saveNewBuilding(newBuilding);
        Long savedId = savedBuilding.getId();

        Building gotBuilding = buildingService.getBuildingByID(savedId);

        Assertions.assertNotNull(gotBuilding);
        Assertions.assertEquals(newBuilding.getName(), gotBuilding.getName());
        Assertions.assertEquals(newBuilding.getId(), gotBuilding.getId());
    }

    @Test
    @Transactional
    public void service_deleteAndExists_saveBuildingAndDeleteCreatedBuildingByIdThenVerifyIfExists() {
        Building newBuilding = new Building();
        newBuilding.setName("Example Building");
        newBuilding.setFloors(new ArrayList<>());
        Building savedBuilding = buildingService.saveNewBuilding(newBuilding);
        Long savedId = savedBuilding.getId();

        buildingService.deleteBuildingById(savedId);
        boolean exists = buildingService.existById(savedId);

        Assertions.assertFalse(exists);
    }

    @Test
    @Transactional
    public void service_updateById_saveBuildingAndUpdateBuildingByIdThenVerifyIfValid() {
        Building newBuilding = new Building();
        newBuilding.setName("Example Building");
        newBuilding.setFloors(new ArrayList<>());
        Building savedBuilding = buildingService.saveNewBuilding(newBuilding);
        Long savedId = savedBuilding.getId();

        BuildingUpdateDTO updateBuilding = new BuildingUpdateDTO("New Example Building", new ArrayList<>());
        buildingService.updateBuildingById(savedId, updateBuilding);

        Building updatedBuilding = buildingService.getBuildingByID(savedId);
        Assertions.assertNotNull(updatedBuilding);
        Assertions.assertEquals(updateBuilding.name(), updatedBuilding.getName());

    }

    @Test
    @SuppressWarnings("null")
    public void mockRestController_create_createBuildingAndVerifyIfPostResponseIsValidBuilding() {
        BuildingRequestDTO buildingDto = new BuildingRequestDTO("Example Building", new HashSet<>());
        HttpEntity<BuildingRequestDTO> entity = new HttpEntity<BuildingRequestDTO>(buildingDto, getAuthorizationTokenHeader());
        ResponseEntity<BuildingResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/building", entity,
                BuildingResponseDTO.class);

        Assertions.assertNotNull(postResponse);
        Assertions.assertNotEquals(0, postResponse.getBody().id());
        Assertions.assertEquals("Example Building", postResponse.getBody().name());
    }

    @Test
    @SuppressWarnings("null")
    public void mockRestController_get_createBuildingAndGetCreatedBuildingByIdThenVerifyIfEqualCreated() {
        BuildingRequestDTO buildingDto = new BuildingRequestDTO("Example Building", new HashSet<>());
        HttpEntity<BuildingRequestDTO> entity = new HttpEntity<BuildingRequestDTO>(buildingDto, getAuthorizationTokenHeader());
        ResponseEntity<BuildingResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/building",
        entity,
                BuildingResponseDTO.class);
        long id = postResponse.getBody().id();
        ResponseEntity<BuildingResponseDTO> gotBuildingDto = restTemplate.exchange(getRootUrl() + "/building/" + id, HttpMethod.GET, entity,
                BuildingResponseDTO.class);

        Assertions.assertNotNull(gotBuildingDto);
        Assertions.assertEquals("Example Building", gotBuildingDto.getBody().name());
    }

    @Test
    @SuppressWarnings("null")
    public void mockRestController_getAll_createBuildingAndGetListOfBuildingsThenVerifyIfBiggerIdEqualCreated()
            throws Exception {
        BuildingRequestDTO buildingDto = new BuildingRequestDTO("Example Building", new HashSet<>());
        HttpEntity<BuildingRequestDTO> entity = new HttpEntity<BuildingRequestDTO>(buildingDto, getAuthorizationTokenHeader());
        ResponseEntity<BuildingResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/building",
        entity,
                BuildingResponseDTO.class);

        Assertions.assertNotNull(postResponse, "The post response should not be null");
        Assertions.assertNotNull(postResponse.getBody(), "The body of the post response should not be null");
        Assertions.assertNotNull(postResponse.getBody().id(), "The ID of the created building should not be null ");

        long expectedId = postResponse.getBody().id();

        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/building?sort=id,desc",
                HttpMethod.GET, entity, String.class);

        Assertions.assertNotNull(response.getBody(), "The body of the Get response should not be null");

        JSONObject obj = new JSONObject(response.getBody());
        Assertions.assertNotEquals(0, obj.getInt("numberOfElements"), "The number of elements should not be zero");

        JSONArray listContent = obj.getJSONArray("content");

        long lastId = listContent.getJSONObject(0).getLong("id");
        String lastName = listContent.getJSONObject(0).getString("name");

        Assertions.assertEquals(expectedId, lastId, "The expected ID must correspond to the last ID returned");
        Assertions.assertEquals("Example Building", lastName, "The name of the building must be 'Example Building'");
    }

    @Test
    @SuppressWarnings("null")
    public void mockRestController_update_createBuildingAndUpdateThenGetAndVerifyIfUpdateWasValid() {
        HttpHeaders headers = getAuthorizationTokenHeader();
        BuildingRequestDTO buildingDto = new BuildingRequestDTO("Example Building", new HashSet<>());
        HttpEntity<BuildingRequestDTO> entity = new HttpEntity<BuildingRequestDTO>(buildingDto, headers);
        ResponseEntity<BuildingResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/building",
        entity,
                BuildingResponseDTO.class);
        long expectedId = postResponse.getBody().id();

        Building t = new Building(buildingDto);
        t.setName("Updated Building");

        HttpEntity<BuildingUpdateDTO> entityToUpdate = new HttpEntity<BuildingUpdateDTO>(new BuildingUpdateDTO(t.getName(), new ArrayList<>()), headers);
        restTemplate.put(getRootUrl() + "/building/" + expectedId, entityToUpdate);

        ResponseEntity<BuildingResponseDTO> updatedBuildingDto = restTemplate.exchange(getRootUrl() + "/building/" + expectedId, HttpMethod.GET, entity,
                BuildingResponseDTO.class);

        Assertions.assertNotNull(updatedBuildingDto);
        Assertions.assertEquals("Updated Building", updatedBuildingDto.getBody().name());
    }

    @Test
    @SuppressWarnings("null")
    public void mockRestController_delete_createBuildingAndDeleteThenVerifyIfDoesNotExist() {
        BuildingRequestDTO buildingDto = new BuildingRequestDTO("Example Building", new HashSet<>());
        HttpEntity<BuildingRequestDTO> entity = new HttpEntity<BuildingRequestDTO>(buildingDto, getAuthorizationTokenHeader());
        ResponseEntity<BuildingResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/building", entity,
                BuildingResponseDTO.class);
        long expectedId = postResponse.getBody().id();

        restTemplate.exchange(getRootUrl() + "/building/" + expectedId, HttpMethod.DELETE, entity, Void.class);
        
        ResponseEntity<ExceptionDetails> exceptionDetails = restTemplate.exchange(getRootUrl() + "/building/" + expectedId, HttpMethod.GET, entity, ExceptionDetails.class);

        assertNotNull(exceptionDetails);
        assertEquals(NOT_FOUND.value(), exceptionDetails.getBody().getStatus());
    }

    @Test
    @SuppressWarnings("null")
    public void mockRestController_delete_returnExceptionDetailsWithNotFoundStatusIfBuildingNotFound() {
        HttpEntity<Void> entity = new HttpEntity<Void>(null, getAuthorizationTokenHeader());
        ResponseEntity<ExceptionDetails> exceptionDetails = restTemplate.exchange(getRootUrl() + "/building/" + 1000000, HttpMethod.GET, entity, ExceptionDetails.class);

        assertNotNull(exceptionDetails);
        assertEquals(NOT_FOUND.value(), exceptionDetails.getBody().getStatus());
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