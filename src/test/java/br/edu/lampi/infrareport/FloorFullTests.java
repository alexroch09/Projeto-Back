package br.edu.lampi.infrareport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.ArrayList;

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
import br.edu.lampi.infrareport.controller.dto.floor.FloorRequestDTO;
import br.edu.lampi.infrareport.controller.dto.floor.FloorResponseDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginRequestDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginResponseDTO;
import br.edu.lampi.infrareport.model.building.Building;
import br.edu.lampi.infrareport.model.floor.Floor;
import br.edu.lampi.infrareport.service.BuildingService;
import br.edu.lampi.infrareport.service.FloorService;
import br.edu.lampi.infrareport.service.exceptions.ConflictException;
import jakarta.transaction.Transactional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FloorFullTests extends InfrareportApplicationTests {
    @Autowired
    private FloorService floorService;

    @Autowired
    private BuildingService buildingService;

    @Test
    public void model_instantiateFloorAndCheckIfValid() {
        Floor f = new Floor();
        f.setId(23L);
        f.setName("floor 1");
        Assertions.assertEquals(
                "Floor(id=23, name=floor 1, building=null)", f.toString());
    }

    @Test
    @Transactional
    public void service_save_saveFloorAndCheckIfValid() {
        
        Building building = new Building();
        building.setName("Example Building");
        building.setFloors(new ArrayList<>());
        Building savedBuilding = buildingService.saveNewBuilding(building);
    
        Floor floor = new Floor();
        floor.setName("Example Floor");
        floor.setBuilding(savedBuilding);
    
        Floor savedFloor = floorService.saveNewFloor(floor);
    
        Assertions.assertNotNull(savedFloor);
        Assertions.assertEquals("Example Floor", savedFloor.getName());
        Assertions.assertNotNull(savedFloor.getBuilding());
        Assertions.assertNotNull(savedFloor.getId());
        Assertions.assertNotEquals(0, savedFloor.getId());
    }

    @Test
    @Transactional
    public void service_save_saveFloorWhenFloorNameIsAlreadyInUseByOtherFloorAtOtherBuildingAndCheckIfValid() {
        
        Building building = new Building();
        building.setName("Example Building");
        building.setFloors(new ArrayList<>());
        Building savedBuilding = buildingService.saveNewBuilding(building);
    
        Floor floor = new Floor();
        floor.setName("Example Floor");
        floor.setBuilding(savedBuilding);
    
        floorService.saveNewFloor(floor);

        Building OtherBuilding = new Building();
        OtherBuilding.setName("other Example Building");
        OtherBuilding.setFloors(new ArrayList<>());
        Building savedOtherBuilding = buildingService.saveNewBuilding(OtherBuilding);
    
        Floor otherFloor = new Floor();
        otherFloor.setName("Example Floor");
        otherFloor.setBuilding(savedOtherBuilding);
    
        Floor savedFloor = floorService.saveNewFloor(otherFloor);
    
        Assertions.assertNotNull(savedFloor);
        Assertions.assertEquals("Example Floor", savedFloor.getName());
        Assertions.assertNotNull(savedFloor.getBuilding());
        Assertions.assertNotNull(savedFloor.getId());
        Assertions.assertNotEquals(0, savedFloor.getId());
    }

    @Test
    @Transactional
    public void service_save_throwExceptionIfNameExists() {
        Building building = new Building();
        building.setName("Example Building");
        building.setId(1L);
        building.setFloors(new ArrayList<>());
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor existingFloor = new Floor();
        existingFloor.setName("Existing Floor");
        existingFloor.setBuilding(savedBuilding);

        floorService.saveNewFloor(existingFloor);

        Floor newFloor = new Floor();
        newFloor.setName("Existing Floor");
        newFloor.setBuilding(building);

        Assertions.assertThrows(ConflictException.class, () -> {
            floorService.saveNewFloor(newFloor);
        });
    }

    @Test
    public void service_save_throwExceptionIfEntityIsNull() {
        Floor newFloor = null;
        Assertions.assertThrows(RuntimeException.class, () -> {
            floorService.saveNewFloor(newFloor);
        });
    }

    @Test
    @Transactional
    public void service_save_throwExceptionIfFieldIsNull() {
        Floor floor = new Floor();
        Assertions.assertThrows(RuntimeException.class, () -> {
            floorService.saveNewFloor(floor);
        });
    }

    @Test
    @Transactional
    public void service_getById_saveFloorAndGetCreatedFloorByIdThenVerifyIfEqualCreated() {
        Building building = new Building();
        building.setName("Example Building");
        building.setId(1L);
        building.setFloors(new ArrayList<>());
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor newFloor = new Floor();
        newFloor.setName("Example Floor");
        newFloor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(newFloor);
        Long savedId = savedFloor.getId();

        Floor gotFloor = floorService.getFloorByID(savedId);

        Assertions.assertNotNull(gotFloor);
        Assertions.assertEquals(newFloor.getName(), gotFloor.getName());
        Assertions.assertEquals(newFloor.getId(), gotFloor.getId());
    }

    @Test
    @Transactional
    public void service_deleteAndExists_saveFloorAndDeleteCreatedFloorByIdThenVerifyIfExists() {
        Building building = new Building();
        building.setName("Example Building");
        building.setId(1L);
        building.setFloors(new ArrayList<>());
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor newFloor = new Floor();
        newFloor.setName("Example Floor");
        newFloor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(newFloor);
        Long savedId = savedFloor.getId();

        floorService.deleteFloorById(savedId);
        boolean exists = floorService.existById(savedId);

        Assertions.assertFalse(exists);
    }

    @Test
    @Transactional
    public void service_updateById_saveFloorAndUpdateFloorByIdThenVerifyIfValid() {
        Building building = new Building();
        building.setName("Example Building");
        building.setId(1L);
        building.setFloors(new ArrayList<>());
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor newFloor = new Floor();
        newFloor.setName("Example Floor");
        newFloor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(newFloor);
        Long savedId = savedFloor.getId();

        FloorRequestDTO updateFloor = new FloorRequestDTO("Updated Floor", savedBuilding.getId());
        floorService.updateFloorById(savedId, updateFloor);

        Floor updatedFloor = floorService.getFloorByID(savedId);
        Assertions.assertNotNull(updatedFloor);
        Assertions.assertEquals(updateFloor.name(), updatedFloor.getName());
    }

    @Test
    @SuppressWarnings("null")
    public void mockRestController_create_createFloorAndVerifyIfPostResponseIsValidFloor() {
        Building building = new Building();
        building.setName("Example Building");
        building.setId(1L);
        building.setFloors(new ArrayList<>());
        Building savedBuilding = buildingService.saveNewBuilding(building);

        FloorRequestDTO floorDto = new FloorRequestDTO("Example Floor", savedBuilding.getId());
        HttpEntity<FloorRequestDTO> entity = new HttpEntity<FloorRequestDTO>(floorDto, getAuthorizationTokenHeader());
        ResponseEntity<FloorResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/floor",
        entity, FloorResponseDTO.class);
        
        Assertions.assertNotNull(postResponse);
        Assertions.assertNotEquals(0, postResponse.getBody().id());
        Assertions.assertEquals("Example Floor", postResponse.getBody().name());
    }

    @Test
    @SuppressWarnings("null")
    public void mockRestController_create_createFloorWhenFloorNameIsAlreadyInUseByOtherFloorAtOtherBuildingAndVerifyIfPostResponseIsValidFloor() {
        HttpHeaders headers = getAuthorizationTokenHeader();
        Building building = new Building();
        building.setName("Example Building");
        building.setFloors(new ArrayList<>());
        building = buildingService.saveNewBuilding(building);

        FloorRequestDTO floorDto = new FloorRequestDTO("Example Floor", building.getId());
        HttpEntity<FloorRequestDTO> entity = new HttpEntity<FloorRequestDTO>(floorDto, headers);
        restTemplate.postForEntity(getRootUrl() + "/floor", entity, FloorResponseDTO.class);

        Building otherBuilding = new Building();
        otherBuilding.setName("Other Example Building");
        otherBuilding.setFloors(new ArrayList<>());
        otherBuilding = buildingService.saveNewBuilding(otherBuilding);

        floorDto = new FloorRequestDTO("Example Floor", otherBuilding.getId());
        entity = new HttpEntity<FloorRequestDTO>(floorDto, headers);
        ResponseEntity<FloorResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/floor",
        entity, FloorResponseDTO.class);
        
        Assertions.assertNotNull(postResponse);
        Assertions.assertNotEquals(0, postResponse.getBody().id());
        Assertions.assertEquals("Example Floor", postResponse.getBody().name());
    }

    @Test
    @SuppressWarnings("null")
    public void mockRestController_get_createFloorAndGetCreatedFloorByIdThenVerifyIfEqualCreated() {
        Building building = new Building();
        building.setName("Example Building");
        building.setId(1L);
        building.setFloors(new ArrayList<>());
        Building savedBuilding = buildingService.saveNewBuilding(building);

        FloorRequestDTO floorDto = new FloorRequestDTO("Example Floor", savedBuilding.getId());
        HttpEntity<FloorRequestDTO> entity = new HttpEntity<FloorRequestDTO>(floorDto, getAuthorizationTokenHeader());
        ResponseEntity<FloorResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/floor",
        entity, FloorResponseDTO.class);
        long id = postResponse.getBody().id();

        ResponseEntity<FloorResponseDTO> gotFloorDto = restTemplate.exchange(getRootUrl() + "/floor/" + id, HttpMethod.GET, entity,
                FloorResponseDTO.class);

        Assertions.assertNotNull(gotFloorDto);
        Assertions.assertEquals("Example Floor", gotFloorDto.getBody().name());
    }

    @Test
    @SuppressWarnings("null")
    public void mockRestController_getAll_createFloorAndGetListOfFloorsThenVerifyIfBiggerIdEqualCreated()
            throws Exception {
                Building building = new Building();
                building.setName("Example Building");
                building.setId(1L);
                building.setFloors(new ArrayList<>());
                Building savedBuilding = buildingService.saveNewBuilding(building);

        FloorRequestDTO floorDto = new FloorRequestDTO("Example Floor", savedBuilding.getId());
        HttpEntity<FloorRequestDTO> entity = new HttpEntity<FloorRequestDTO>(floorDto, getAuthorizationTokenHeader());

        ResponseEntity<FloorResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/floor",
        entity, FloorResponseDTO.class);

        Assertions.assertNotNull(postResponse, "The post response should not be null");
        Assertions.assertNotNull(postResponse.getBody(), "The body of the post response should not be null");
        Assertions.assertNotNull(postResponse.getBody().id(), "The ID of the created floor should not be null ");

        long expectedId = postResponse.getBody().id();

        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/floor?sort=id,desc",
                HttpMethod.GET, entity, String.class);

        Assertions.assertNotNull(response.getBody(), "The body of the Get response should not be null");

        JSONObject obj = new JSONObject(response.getBody());
        Assertions.assertNotEquals(0, obj.getInt("numberOfElements"), "The number of elements should not be zero");

        JSONArray listContent = obj.getJSONArray("content");

        long lastId = listContent.getJSONObject(0).getLong("id");
        String lastName = listContent.getJSONObject(0).getString("name");

        Assertions.assertEquals(expectedId, lastId, "The expected ID must correspond to the last ID returned");
        Assertions.assertEquals("Example Floor", lastName, "The name of the floor must be 'Example Floor'");
    }

    @Test
    @SuppressWarnings("null")
    public void mockRestController_update_createFloorAndUpdateThenGetAndVerifyIfUpdateWasValid() {
        Building building = new Building();
        building.setName("Example Building");
        building.setId(1L);
        building.setFloors(new ArrayList<>());
        Building savedBuilding = buildingService.saveNewBuilding(building);

        FloorRequestDTO floorDto = new FloorRequestDTO("Example Floor", savedBuilding.getId());
        HttpEntity<FloorRequestDTO> entity = new HttpEntity<FloorRequestDTO>(floorDto, getAuthorizationTokenHeader());
        ResponseEntity<FloorResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/floor",
        entity, FloorResponseDTO.class);

        long expectedId = postResponse.getBody().id();

        entity = new HttpEntity<FloorRequestDTO>(new FloorRequestDTO("Updated Floor", savedBuilding.getId()), getAuthorizationTokenHeader());
        restTemplate.put(getRootUrl() + "/floor/" + expectedId, entity);

        entity = new HttpEntity<FloorRequestDTO>(null, getAuthorizationTokenHeader());

        ResponseEntity<FloorResponseDTO> updatedFloorDto = restTemplate.exchange(getRootUrl() + "/floor/" + expectedId, HttpMethod.GET, entity,
                FloorResponseDTO.class);

        Assertions.assertNotNull(updatedFloorDto);
        Assertions.assertEquals("Updated Floor", updatedFloorDto.getBody().name());
    }

    @Test
    @SuppressWarnings("null")
    public void mockRestController_delete_createFloorAndDeleteThenVerifyIfDoesNotExist() {
        Building building = new Building();
        building.setName("Example Building");
        building.setId(1L);
        building.setFloors(new ArrayList<>());
        Building savedBuilding = buildingService.saveNewBuilding(building);

        FloorRequestDTO floorDto = new FloorRequestDTO("Example Floor", savedBuilding.getId());
        HttpEntity<FloorRequestDTO> entity = new HttpEntity<FloorRequestDTO>(floorDto, getAuthorizationTokenHeader());
        ResponseEntity<FloorResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/floor",
        entity, FloorResponseDTO.class);

        long expectedId = postResponse.getBody().id();

        restTemplate.exchange(getRootUrl() + "/floor/" + expectedId,  HttpMethod.DELETE, entity, Void.class);

        ResponseEntity<ExceptionDetails> exceptionDetails = restTemplate.exchange(getRootUrl() + "/floor/" + expectedId, HttpMethod.GET, entity, ExceptionDetails.class);

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
