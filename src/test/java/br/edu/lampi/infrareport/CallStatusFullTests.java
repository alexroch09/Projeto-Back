package br.edu.lampi.infrareport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import br.edu.lampi.infrareport.model.building.Building;
import br.edu.lampi.infrareport.model.floor.Floor;
import br.edu.lampi.infrareport.service.*;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
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
import br.edu.lampi.infrareport.controller.dto.callStatus.CallStatusRequestDTO;
import br.edu.lampi.infrareport.controller.dto.callStatus.CallStatusResponseDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginRequestDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginResponseDTO;
import br.edu.lampi.infrareport.controller.dto.user.UserRequestDTO;
import br.edu.lampi.infrareport.model.call.Call;
import br.edu.lampi.infrareport.model.callstatus.CallStatus;
import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.model.user.UserType;
import br.edu.lampi.infrareport.repository.CallRepository;
import jakarta.transaction.Transactional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CallStatusFullTests extends InfrareportApplicationTests{
    @Autowired
    CallStatusService callStatusService;

    @Autowired
    CallRepository callRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @Autowired
    CallService callService;

    @Autowired
    BuildingService buildingService;

    @Autowired
    FloorService floorService;

    private final String CALL_STATUS_URL = "/call_status";

	/**
     * Teste para verificar Model de callStatus
     */
    @Test
    public void model_instantiateCallStatusAndCheckIfValid() {
        CallStatus callStatus = new CallStatus();
        callStatus.setId(1L);
        callStatus.setCallStatusName("Pending");
        Assertions.assertEquals(1L, callStatus.getId());
        Assertions.assertEquals("Pending", callStatus.getCallStatusName());
    }

    /**
     * Teste para verificar save em Service de callStatus
     */
    @Test
    @Transactional
    public void service_save_saveCalltatusAndCheckIfValid() {
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("In Progress");
        CallStatus savedCallStatus = callStatusService.save(callStatusRequest);
        Assertions.assertNotNull(savedCallStatus);
        Assertions.assertEquals(callStatusRequest.callStatusName(), savedCallStatus.getCallStatusName());
        Assertions.assertNotNull(savedCallStatus.getId());
        Assertions.assertNotEquals("0", savedCallStatus.getId());
    }

    /**
     * Teste para verificar se save joga exception ao tentar salvar um call status com um nome ja usado em Service de callStatus
     */
    @Test
    @Transactional
    public void service_save_throwsRuntimeExceptionIfCallStatusNameIsAlreadyInUse() {
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("In Progress");
        callStatusService.save(callStatusRequest);

        Assertions.assertThrows(RuntimeException.class, () -> callStatusService.save(callStatusRequest));
    }


    /**
     * Teste para verificar search em Service de call status
     */
    @Test
    @Transactional
    public void service_search_saveCallStatusAndGetCallStatusListThenVerifyIfEqualCreated() {
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("pendente 10");
        CallStatus savedCallStatus = callStatusService.save(callStatusRequest);

        List<CallStatus> returnedCallStatusList = callStatusService.search();

        Assertions.assertNotNull(returnedCallStatusList);
        Assertions.assertFalse(returnedCallStatusList.isEmpty());
        Assertions.assertEquals(savedCallStatus.getCallStatusName(), returnedCallStatusList.get(0).getCallStatusName());
        Assertions.assertEquals(savedCallStatus.getId(), returnedCallStatusList.get(0).getId());
    }
    /**
     * Teste para verificar se search em Service de call status retorna uma lista vazia quando nenhum call status for encontrado
     */
    @Test
    @Transactional
    public void service_search_returnsAnEmptyCallStatusListIfNoCallStatusWereFound() {
        List<CallStatus> returnedCallStatusList = callStatusService.search();

        Assertions.assertNotNull(returnedCallStatusList);
        Assertions.assertTrue(returnedCallStatusList.isEmpty());
    }

    /**
     * Teste para verificar searchById em Service de call status
     */
    @Test
    @Transactional
    public void service_searchById_saveCallStatusAndGetCreatedCallStatusByIdThenVerifyIfEqualCreated() {
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("Pending");
        CallStatus savedCallStatus = callStatusService.save(callStatusRequest);
        Long savedId = savedCallStatus.getId();

        CallStatus returnedCallStatus = callStatusService.searchById(savedId);

        Assertions.assertNotNull(returnedCallStatus);
        Assertions.assertEquals(savedCallStatus.getCallStatusName(), returnedCallStatus.getCallStatusName());
        Assertions.assertEquals(savedCallStatus.getId(), returnedCallStatus.getId());
    }
    /**
     * Teste para verificar se searchById em Service de call status joga runtimeException quando nenhum call status for encontrado pelo id
     */
    @Test
    @Transactional
    public void service_searchById_throwsRuntimeExceptionIfNoCallStatusWereFound() {
        Assertions.assertThrows(RuntimeException.class, () -> callStatusService.searchById(10000L));
    }

    /**
     * Teste para verificar delete and searchById em Service de call status
     */
    @Test
    @Transactional
    public void service_deleteAndSearchById_saveCallStatusAndDeleteCreatedCallStatusByIdThenVerifyIfExists() {
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("concluido 10");
        CallStatus savedCallStatus = callStatusService.save(callStatusRequest);
        Long savedId = savedCallStatus.getId();

        callStatusService.delete(savedId);

        Assertions.assertThrows(RuntimeException.class, () -> callStatusService.searchById(savedId));
    }
    /**
     * Teste para verificar se delete joga exception quando call status não for encontrado Service de call status
     */
    @Test
    @Transactional
    public void service_delete_throwRuntimeExceptionIfNoCallStatusWereFound() {
        Assertions.assertThrows(RuntimeException.class, () -> callStatusService.delete(100000L));
    }
    /**
     * Teste para verificar se delete joga exception quando call status estiver associada a uma call (chamada) Service de call status
     */
    @Test
    @Transactional
    public void service_delete_throwRuntimeExceptionIfCallStatusIsAssociatedToACall() {
        // Salvar CallStatus
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("concluido 20");
        CallStatus savedCallStatus = callStatusService.save(callStatusRequest);
        Long savedId = savedCallStatus.getId();

        // Criar e salvar usuário
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(List.of(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        // Configurar building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        // Criar e salvar chamada
        LocalDateTime instant = LocalDateTime.now();
        Call call = new Call();
        call.setTitle("Title Title");
        call.setDescription("Description Description");
        call.setUser(savedUser);
        call.setDateTime(instant);
        call.setCallStatus(savedCallStatus);
        call.setFloor(savedFloor);
        callRepository.save(call);

        // Verificar se RuntimeException é lançada ao tentar deletar CallStatus associado a uma chamada
        Assertions.assertThrows(RuntimeException.class, () -> callStatusService.delete(savedId));
    }

    /**
     * Teste para verificar update em Service de call status
     */
    @Test
    @Transactional
    public void service_update_saveCallStatusAndUpdateCallStatusThenVerifyIfValid() {
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("to do 10");
        CallStatus savedCallStatus = callStatusService.save(callStatusRequest);
        Long savedId = savedCallStatus.getId();

        CallStatusRequestDTO callStatusRequestToupdate = new CallStatusRequestDTO("in progress");
        CallStatus updatedCallStatus = callStatusService.update(savedId, callStatusRequestToupdate);

        Assertions.assertNotNull(updatedCallStatus);
        Assertions.assertEquals(savedId, updatedCallStatus.getId());
        Assertions.assertEquals(callStatusRequestToupdate.callStatusName(), updatedCallStatus.getCallStatusName());
    }
    /**
     * Teste para verificar se update joga exception se o call status não for encontrado em Service de call status
     */
    @Test
    @Transactional
    public void service_update_throwRuntimeExceptionIfNoCallStatusWereFound() {
        CallStatusRequestDTO callStatusRequestToupdate = new CallStatusRequestDTO("in progress");
        Assertions.assertThrows(RuntimeException.class, () -> callStatusService.update(100000L, callStatusRequestToupdate));
    }

    /**
     * Teste para verificar se update joga exception se o nome do call status ja estiver em uso  em Service de call status
     */
    @Test
    @Transactional
    public void service_update_throwRunimeExceptionIfCallStatusNameIsAlreadyInUse() {
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("to do 20");
        CallStatus savedCallStatus = callStatusService.save(callStatusRequest);
        CallStatusRequestDTO callStatusRequestToBeInConflict = new CallStatusRequestDTO("in progress");
        callStatusService.save(callStatusRequestToBeInConflict);
        Long savedId = savedCallStatus.getId();

        CallStatusRequestDTO callStatusRequestToUpdate = new CallStatusRequestDTO("in progress");
        Assertions.assertThrows(RuntimeException.class, () -> callStatusService.update(savedId, callStatusRequestToUpdate));
    }

     /**
     * Teste de requisição ao Controller para verificar se consegue criar um novo Call Status
     */
    @Test
    public void mockRestController_save_saveCallStatusAndVerifyIfPostResponseIsValidTeam() {
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("to do 30");
        HttpEntity<CallStatusRequestDTO> entity = new HttpEntity<CallStatusRequestDTO>(callStatusRequest, getAuthorizationTokenHeader());
        ResponseEntity<CallStatusResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_STATUS_URL, entity, CallStatusResponseDTO.class);

        Assertions.assertEquals(CREATED, postResponse.getStatusCode());
        Assertions.assertNotNull(postResponse);
        Assertions.assertNotEquals(0, postResponse.getBody().id());
        Assertions.assertEquals(callStatusRequest.callStatusName(), postResponse.getBody().callStatusName());
    }

    /**
     * Teste de requisição ao Controller para verificar se save retorna uma exception details ao criar um novo Call Status com um nome já em uso
     */
    @Test
    public void mockRestController_save_returnExceptionsDetailsWithConflictStatusIfCallStatusNameIsAlreadyInUse() {
        CallStatusRequestDTO callStatusRequestToBeInConflict = new CallStatusRequestDTO("to do");
        callStatusService.save(callStatusRequestToBeInConflict);

        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("to do");
        HttpEntity<CallStatusRequestDTO> entity = new HttpEntity<CallStatusRequestDTO>(callStatusRequest, getAuthorizationTokenHeader());

        ResponseEntity<ExceptionDetails> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_STATUS_URL, entity, ExceptionDetails.class);

        Assertions.assertEquals(CONFLICT, postResponse.getStatusCode());
        Assertions.assertNotNull(postResponse);
        Assertions.assertNotNull(postResponse.getBody());
        Assertions.assertEquals(CONFLICT.value(), postResponse.getBody().getStatus());
    }

    /**
     * Teste de requisição ao controller para verificar se consegue obter um call status que acabou de criar
     * @throws URISyntaxException
     */
    @Test
    public void mockRestController_searchById_createCallStatusAndSearchByIdThenVerifyIfEqualCreated() throws URISyntaxException {
        HttpHeaders headers = getAuthorizationTokenHeader();
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("em andamento 30s");

        HttpEntity<CallStatusRequestDTO> entity = new HttpEntity<CallStatusRequestDTO>(callStatusRequest, headers);
        ResponseEntity<CallStatusResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_STATUS_URL, entity, CallStatusResponseDTO.class);
        long id = postResponse.getBody().id();

        ResponseEntity<CallStatusResponseDTO> responseEntity = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + CALL_STATUS_URL + "/" +id)).headers(headers).build(), CallStatusResponseDTO.class);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());

        CallStatusResponseDTO returnedCallStatus = responseEntity.getBody();

        Assertions.assertEquals(id, returnedCallStatus.id());
        Assertions.assertEquals(callStatusRequest.callStatusName(), returnedCallStatus.callStatusName());
    }
    /**
     * Teste de requisição ao controller para verificar se retorna um exception details quando call status não encontrado
     * @throws URISyntaxException
     */
    @Test
    public void mockRestController_searchById_returnExceptionDetailsWithNotFoundStatus() throws URISyntaxException {
        ResponseEntity<ExceptionDetails> responseEntity = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + CALL_STATUS_URL + "/100000")).headers(getAuthorizationTokenHeader()).build(), ExceptionDetails.class);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());

        Assertions.assertEquals(NOT_FOUND.value(), responseEntity.getBody().getStatus());
    }

	/**
     * Teste de requisição no Controller para verificar se consegue consultar lista de call status.
     */
    @Test
    public void mockRestController_search_saveCallStatusAndSearchCallStatusListThenVerifyIfFirstCallStatusIdIsEqualsToSavedCallStatusId() throws Exception{
        HttpHeaders headers = getAuthorizationTokenHeader();
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("em andamento 40");
        HttpEntity<CallStatusRequestDTO> callStatusEntity = new HttpEntity<CallStatusRequestDTO>(callStatusRequest, headers);

        ResponseEntity<CallStatusResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_STATUS_URL, callStatusEntity, CallStatusResponseDTO.class);
        Long expectedId = postResponse.getBody().id();

        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + CALL_STATUS_URL,
        HttpMethod.GET, entity, String.class);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());

        JSONArray jsonArray = new JSONArray(response.getBody());
        Assertions.assertNotEquals(0, jsonArray.length());
        Assertions.assertEquals(expectedId, jsonArray.getJSONObject(0).getLong("id"));
        Assertions.assertEquals(callStatusRequest.callStatusName(), jsonArray.getJSONObject(0).getString("callStatusName"));
    }
	/**
     * Teste de requisição no Controller para verificar se consegue retornar uma lista vazia quando nenhum call status for encontrado.
     */
    @Test
    public void mockRestController_search_returnsAnEmptyCallStatusListifNoCallStatusWereFound() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, getAuthorizationTokenHeader());

        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + CALL_STATUS_URL,
        HttpMethod.GET, entity, String.class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());

        JSONArray jsonArray = new JSONArray(response.getBody());
        Assertions.assertEquals(0, jsonArray.length());
    }

    /**
     * Teste de requisição no controller para verificar se consegue atualizar um call status.

     */
    @Test
    public void mockRestController_update_saveCallStatusAndUpdateThenGetSearchAndVerifyIfUpdateWasValid() throws JsonMappingException, JsonProcessingException {
        HttpHeaders headers = getAuthorizationTokenHeader();
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("em andamento");
        CallStatusRequestDTO callStatusRequestUpdate = new CallStatusRequestDTO("concluido");
        HttpEntity<CallStatusRequestDTO> callSatusEntity = new HttpEntity<CallStatusRequestDTO>(callStatusRequest, headers);

        ResponseEntity<CallStatusResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_STATUS_URL, callSatusEntity, CallStatusResponseDTO.class);
        long expectedId = postResponse.getBody().id();

        HttpEntity<CallStatusRequestDTO> entity = new HttpEntity<CallStatusRequestDTO>(callStatusRequestUpdate, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(getRootUrl() + CALL_STATUS_URL + "/" + expectedId, HttpMethod.PUT, entity, String.class);

        assertEquals(OK, responseEntity.getStatusCode());

        CallStatusResponseDTO updatedCallStatus = objectMapper.readValue(responseEntity.getBody(), CallStatusResponseDTO.class);
        assertEquals(expectedId, updatedCallStatus.id());
        assertEquals(callStatusRequestUpdate.callStatusName(), updatedCallStatus.callStatusName());
    }
    /**
     * Teste de requisição no controller para verificar se retorna um exception details com status not found quando call status não for encontrada

     */
    @Test
    public void mockRestController_update_returnExceptionDetailsWithNotFoundStatusIfNoCallStatusWereFound() throws JsonMappingException, JsonProcessingException {
        CallStatusRequestDTO callStatusRequestUpdate = new CallStatusRequestDTO("concluido");
        HttpEntity<CallStatusRequestDTO> entity = new HttpEntity<CallStatusRequestDTO>(callStatusRequestUpdate, getAuthorizationTokenHeader());

        ResponseEntity<String> responseEntity = restTemplate.exchange(getRootUrl() + CALL_STATUS_URL + "/100000", HttpMethod.PUT, entity, String.class);

        assertEquals(NOT_FOUND, responseEntity.getStatusCode());

        ExceptionDetails exceptionDetails = objectMapper.readValue(responseEntity.getBody(), ExceptionDetails.class);
        Assertions.assertEquals(NOT_FOUND.value(), exceptionDetails.getStatus());
    }
    /**
     * Teste de requisição no controller para verificar se retorna um exception details com status conflict quando call status name ja esta em uso

     */
    @Test
    public void mockRestController_update_returnExceptionDetailsWithConflictStatusIfCallStatusNameIsAlreadyInUse() throws JsonMappingException, JsonProcessingException {
        HttpHeaders headers = getAuthorizationTokenHeader();
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("em andamento 50");
        CallStatusRequestDTO callStatusRequestUpdate = new CallStatusRequestDTO("concluido 40");

        HttpEntity<CallStatusRequestDTO> callSatusEntity = new HttpEntity<CallStatusRequestDTO>(callStatusRequestUpdate, headers);

        restTemplate.postForEntity(getRootUrl() + CALL_STATUS_URL, callSatusEntity, CallStatusResponseDTO.class);

        callSatusEntity = new HttpEntity<CallStatusRequestDTO>(callStatusRequest, headers);

        ResponseEntity<CallStatusResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_STATUS_URL, callSatusEntity, CallStatusResponseDTO.class);
        long expectedId = postResponse.getBody().id();

        HttpEntity<CallStatusRequestDTO> entity = new HttpEntity<>(callStatusRequestUpdate, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(getRootUrl() + CALL_STATUS_URL + "/" + expectedId, HttpMethod.PUT, entity, String.class);

        assertEquals(CONFLICT, responseEntity.getStatusCode());

        ExceptionDetails exceptionDetails = objectMapper.readValue(responseEntity.getBody(), ExceptionDetails.class);
        Assertions.assertEquals(CONFLICT.value(), exceptionDetails.getStatus());
    }

    /**
     * Teste de requisição no controller para verificar se consegue deletar um call status.
     * @throws URISyntaxException
     */
    @Test
    public void mockRestController_delete_saveCallStatusAndDeleteThenVerifyIfDontExistsAndReturnAnExceptionsDetailsWithNotFoundStatus() throws URISyntaxException {
        HttpHeaders headers = getAuthorizationTokenHeader();
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("em andamento 60");

        HttpEntity<CallStatusRequestDTO> callSatusEntity = new HttpEntity<CallStatusRequestDTO>(callStatusRequest, headers);

        ResponseEntity<CallStatusResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_STATUS_URL, callSatusEntity, CallStatusResponseDTO.class);
        Long expectedId = postResponse.getBody().id();

        callSatusEntity = new HttpEntity<CallStatusRequestDTO>(null, headers);

        restTemplate.exchange(getRootUrl() + CALL_STATUS_URL + "/" + expectedId, HttpMethod.DELETE, callSatusEntity, CallStatusResponseDTO.class);

        ResponseEntity<ExceptionDetails> responseEntity = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + CALL_STATUS_URL +"/" + expectedId)).headers(headers).build(), ExceptionDetails.class);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(NOT_FOUND, responseEntity.getStatusCode());
        Assertions.assertEquals(NOT_FOUND.value(), responseEntity.getBody().getStatus());
    }

    /**
     * Teste de requisição no controller para verificar se retorna exception details quando call status não for encontrada
     */
    @Test
    public void mockRestController_delete_returnExceptionDetailsWithNotFoundStatusIfNoCallStatusWereFound() {
        HttpEntity<CallStatusRequestDTO> callSatusEntity = new HttpEntity<CallStatusRequestDTO>(null, getAuthorizationTokenHeader());

        ResponseEntity<ExceptionDetails> response = restTemplate.exchange(getRootUrl() + CALL_STATUS_URL +"/100000", HttpMethod.DELETE, callSatusEntity, ExceptionDetails.class);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_FOUND.value(), response.getBody().getStatus());
    }

    /**
     * Teste de requisição no controller para verificar se retorna exception details quando tentar deletar um call status que esta associado a uma call
     */
    @Test
    public void mockRestController_delete_returnExceptionDetailsWithConflictStatusIfCallStatusIsAssociatedToACall() {
        // Criar e salvar CallStatus
        CallStatusRequestDTO callStatusRequest = new CallStatusRequestDTO("concluido 80");
        CallStatus savedCallStatus = callStatusService.save(callStatusRequest);
        Long savedId = savedCallStatus.getId();

        // Criar e salvar usuário
        User user = new User();
        user.setName("Usuario Usuario");
        user.setEmail("user@mail.c");
        user.setUserType(List.of(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        // Configurar building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        // Criar e salvar chamada
        Call call = new Call();
        LocalDateTime instant = LocalDateTime.now();
        call.setTitle("Titulo Titulo");
        call.setDescription("Descricao Descricao Descricao");
        call.setDateTime(instant);
        call.setUser(savedUser);
        call.setFloor(savedFloor);
        call.setCallStatus(savedCallStatus);
        callRepository.save(call);

        // Realizar a requisição de exclusão
        HttpEntity<CallStatusRequestDTO> callStatusEntity = new HttpEntity<>(null, getAuthorizationTokenHeader());
        ResponseEntity<ExceptionDetails> response = restTemplate.exchange(
                getRootUrl() + CALL_STATUS_URL + "/" + savedId, HttpMethod.DELETE, callStatusEntity, ExceptionDetails.class
        );

        // Verificar se o status de conflito é retornado
        assertEquals(CONFLICT, response.getStatusCode());
        assertEquals(CONFLICT.value(), response.getBody().getStatus());
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
