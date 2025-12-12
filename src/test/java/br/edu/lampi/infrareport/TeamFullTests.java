package br.edu.lampi.infrareport;

import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import br.edu.lampi.infrareport.controller.dto.team.TeamDTO;
import br.edu.lampi.infrareport.controller.dto.team.TeamNoIdDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginRequestDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginResponseDTO;
import br.edu.lampi.infrareport.controller.dto.user.UserRequestDTO;
import br.edu.lampi.infrareport.model.team.Team;
import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.service.TeamService;
import br.edu.lampi.infrareport.service.UserService;
import jakarta.transaction.Transactional;

public class TeamFullTests extends InfrareportApplicationTests {
    @Autowired
    TeamService tService;

    @Autowired
    UserService userService;

	/**
     * Teste para verificar Model de Team
     */
    @Test
    public void model_instantiateTeamAndCheckIfValid() {
        Team t = new Team();
        t.setId(23L);
        t.setName("Piratas de Foxy");
        Assertions.assertEquals("Team(id=23, name=Piratas de Foxy)", t.toString());
    }

    /**
     * Teste para verificar save em Service de Team
     */
    @Test
    @Transactional
    public void service_save_saveTeamAndCheckIfValid() {
        Team t = new Team();
        t.setName("Piratas de Kid");
        Team saved = tService.saveTeam(t);
        Assertions.assertNotNull(saved);
        Assertions.assertEquals("Piratas de Kid", saved.getName());
        Assertions.assertNotNull(saved.getId());
        Assertions.assertNotEquals("0", saved.getId());
    }

    /**
     * Teste para verificar get em Service de Team
     */
    @Test
    @Transactional
    public void service_getById_saveTeamAndGetCreatedTeamByIdThenVerifyIfEqualCreated() {
        Team newTeam = new Team();
        newTeam.setName("Piratas Bellamy");
        Team savedTeam = tService.saveTeam(newTeam);
        Long savedId = savedTeam.getId();

        Team gotTeam = tService.getTeamsById(savedId);

        Assertions.assertNotNull(gotTeam);
        Assertions.assertEquals(newTeam.getName(), gotTeam.getName());
        Assertions.assertEquals(newTeam.getId(), gotTeam.getId());
    }

    /**
     * Teste para verificar delete and exists em Service de Team
     */
    @Test
    @Transactional
    public void service_deleteAndExists_saveTeamAndDeleteCreatedTeamByIdThenVerifyIfExists() {
        Team newTeam = new Team();
        newTeam.setName("Piratas Arlong");
        Team savedTeam = tService.saveTeam(newTeam);
        Long savedId = savedTeam.getId();

        tService.deleteTeamById(savedId);
        boolean exists = tService.existsById(savedId);

        Assertions.assertEquals(false, exists);
    }

    /**
     * Teste para verificar update em Service de Team
     */
    @Test
    @Transactional
    public void service_updateById_saveTeamAndUpdateTeamByIdThenVerifyIfValid() {
        Team newTeam = new Team();
        newTeam.setName("Piratas Big Mom");
        Team savedTeam = tService.saveTeam(newTeam);
        Long savedId = savedTeam.getId();

        TeamNoIdDTO updateTeam = new TeamNoIdDTO("Piratas de Big Mom");
        tService.updateTeamById(savedId, updateTeam);

        Team updatedTeam = tService.getTeamsById(savedId); 
        Assertions.assertNotNull(updatedTeam);
        Assertions.assertEquals(updateTeam.name(), updatedTeam.getName());
    }

    /**
     * Teste de requisição ao Controller para verificar se consegue criar uma nova equipe como admin
     */
    @Test
    @SuppressWarnings("null")
    public void mockRestController_create_createTeamAsAdminAndVerifyIfPostResponseIsValidTeam() {
        /* Criando usuário admin: */
        UserRequestDTO userRequest = new UserRequestDTO("Zoro", "zoro@gmail.com", "12345");
        User savedUser = userService.saveAdmin(userRequest);

        /* Realizando login com admin: */
        LoginRequestDTO loginRequest = new LoginRequestDTO(savedUser.getEmail(), "12345");
        ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como admin: */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+loginResponse.getBody().token());

        /* Criando novo Team como admin: */
        TeamNoIdDTO teamNoIdDTO = new TeamNoIdDTO("Piratas do Chapéu de Palha");
        HttpEntity<TeamNoIdDTO> entity = new HttpEntity<TeamNoIdDTO>(teamNoIdDTO, headers);
        ResponseEntity<TeamDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/teams", entity, TeamDTO.class);

        /* Verificando resultados: */
        Assertions.assertNotNull(postResponse);
        Assertions.assertNotEquals(0, postResponse.getBody().id());
        Assertions.assertEquals("Piratas do Chapéu de Palha", postResponse.getBody().name());
    }

    /**
     * Teste de requisição ao Controller para verificar se consegue criar uma nova equipe como usuário comum.
     * Deve resultar em não autorizado.
     */
    @Test
    @SuppressWarnings("null")
    public void mockRestController_create_createTeamAsCommonAndVerifyIfUnauthorized() {
        /* Criando usuário comum: */
        UserRequestDTO userRequest = new UserRequestDTO("Nami", "nami@gmail.com", "12345");
        User savedUser = userService.save(userRequest);

        /* Realizando login com usuário comum: */
        LoginRequestDTO loginRequest = new LoginRequestDTO(savedUser.getEmail(), "12345");
        ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como usuário comum: */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+loginResponse.getBody().token());

        /* Criando novo Team como usuário comum: */
        TeamNoIdDTO teamNoIdDTO = new TeamNoIdDTO("Piratas do Chapéu de Palha");
        HttpEntity<TeamNoIdDTO> entity = new HttpEntity<TeamNoIdDTO>(teamNoIdDTO, headers);
        ResponseEntity<TeamDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/teams", entity, TeamDTO.class);

        /* Verificando resultados: */
        Assertions.assertNotNull(postResponse);
        Assertions.assertEquals(postResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);
        Assertions.assertEquals(postResponse.getStatusCode().value(), HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * Teste de requisição ao controller para verificar se consegue obter um time que acabou de criar.
     * Deve ser capaz de criar e consultar como admin.
     * @throws URISyntaxException 
     */
    @Test
    @SuppressWarnings("null")
    public void mockRestController_get_createTeamAndGetCreatedAsAdminTeamByIdThenVerifyIfEqualCreated() throws URISyntaxException {
        /* Criando usuário admin: */
        UserRequestDTO adminRequest = new UserRequestDTO("Usopp", "usopp@gmail.com", "12345");
        User savedAdmin = userService.saveAdmin(adminRequest);

        /* Realizando login com admin: */
        LoginRequestDTO loginAdminRequest = new LoginRequestDTO(savedAdmin.getEmail(), "12345");
        ResponseEntity<LoginResponseDTO> loginAdminResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginAdminRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como admin: */
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.set("Authorization", "Bearer "+loginAdminResponse.getBody().token());

        /* Criando novo Team como admin: */
        TeamNoIdDTO teamNoIdDTO = new TeamNoIdDTO("Piratas Heart");
        HttpEntity<TeamNoIdDTO> entity = new HttpEntity<TeamNoIdDTO>(teamNoIdDTO, adminHeaders);
        ResponseEntity<TeamDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/teams", entity, TeamDTO.class);
        long id = postResponse.getBody().id();

        /* Consultando team por id, usando credenciais de admin: */
        ResponseEntity<TeamDTO> gotTeam = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + "/teams/"+id)).headers(adminHeaders).build(), TeamDTO.class);
        
        /* Verificando resultados: */
        Assertions.assertNotNull(gotTeam);
        Assertions.assertEquals("Piratas Heart", gotTeam.getBody().name());
    }

    /**
     * Teste de requisição ao controller para verificar se consegue obter um time que acabou de criar.
     * Deve ser capaz de criar como admin e consultar como usuário comum.
     * @throws URISyntaxException 
     */
    @Test
    @SuppressWarnings("null")
    public void mockRestController_get_createTeamAndGetCreatedAsCommonTeamByIdThenVerifyIfEqualCreated() throws URISyntaxException {
        /* Criando usuário admin: */
        UserRequestDTO adminRequest = new UserRequestDTO("sanji", "sanji@gmail.com", "12345");
        User savedAdmin = userService.saveAdmin(adminRequest);

        /* Realizando login com admin: */
        LoginRequestDTO loginAdminRequest = new LoginRequestDTO(savedAdmin.getEmail(), "12345");
        ResponseEntity<LoginResponseDTO> loginAdminResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginAdminRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como admin: */
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.set("Authorization", "Bearer "+loginAdminResponse.getBody().token());

        /* Criando novo Team como admin: */
        TeamNoIdDTO teamNoIdDTO = new TeamNoIdDTO("Piratas Heart");
        HttpEntity<TeamNoIdDTO> entity = new HttpEntity<TeamNoIdDTO>(teamNoIdDTO, adminHeaders);
        ResponseEntity<TeamDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/teams", entity, TeamDTO.class);
        long id = postResponse.getBody().id();

        /* Criando usuário comum: */
        UserRequestDTO commonRequest = new UserRequestDTO("Brook", "brook@gmail.com", "54321");
        User savedCommon = userService.save(commonRequest);

        /* Fazendo login como usuário comum: */
        LoginRequestDTO loginCommonRequest = new LoginRequestDTO(savedCommon.getEmail(), "54321");
        ResponseEntity<LoginResponseDTO> loginCommonResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginCommonRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como usuário comum: */
        HttpHeaders commonHeaders = new HttpHeaders();
        commonHeaders.setContentType(MediaType.APPLICATION_JSON);
        commonHeaders.set("Authorization", "Bearer "+loginCommonResponse.getBody().token());

        /* Consultando team por id, usando credenciais de usuário comum: */
        ResponseEntity<TeamDTO> gotTeam = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + "/teams/"+id)).headers(commonHeaders).build(), TeamDTO.class);

        /* Verificando resultados: */
        Assertions.assertNotNull(gotTeam);
        Assertions.assertEquals("Piratas Heart", gotTeam.getBody().name());
    }

	/**
     * Teste de requisição no Controller para verificar se consegue consultar lista de teams.
     * Deve ser capaz de criar e consultar como admin.
     * O resultado esperado é uma lista paginável. 
     * No teste a lista é ordenada por ID.
     * O maior ID deve corresponder ao time que acabou de ser adicionado.
     */
    @Test
    @SuppressWarnings("null")
    public void mockRestController_getAll_createTeamAndGetListOfTeamsAsAdminThenVerifyIfBiggerIdEqualCreated() throws Exception{
        /* Criando usuário admin: */
        UserRequestDTO adminRequest = new UserRequestDTO("Luffy", "luffy@gmail.com", "12345");
        User savedAdmin = userService.saveAdmin(adminRequest);

        /* Realizando login com admin: */
        LoginRequestDTO loginAdminRequest = new LoginRequestDTO(savedAdmin.getEmail(), "12345");
        ResponseEntity<LoginResponseDTO> loginAdminResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginAdminRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como admin: */
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.set("Authorization", "Bearer "+loginAdminResponse.getBody().token());
        
        /* Criando novo Team como admin: */
        TeamNoIdDTO teamNoIdDTO = new TeamNoIdDTO("Piratas do Sol");
        HttpEntity<TeamNoIdDTO> teamEntity = new HttpEntity<TeamNoIdDTO>(teamNoIdDTO, adminHeaders);
        ResponseEntity<TeamDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/teams", teamEntity, TeamDTO.class);
        long expectedId = postResponse.getBody().id();

        /* Consultando lista de Teams como admin: */
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, adminHeaders);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/teams?sort=id,desc",
                                                                HttpMethod.GET, requestEntity, String.class);

        /* Verificando resultados na lista consultada: */                                          
        Assertions.assertNotNull(response.getBody());
        JSONObject obj = new JSONObject(response.getBody());
        Assertions.assertNotEquals(0, obj.getInt("numberOfElements"));
        JSONArray listContent = obj.getJSONArray("content");
        long lastId = listContent.getJSONObject(0).getLong("id");
        String lastName = listContent.getJSONObject(0).getString("name");
        Assertions.assertEquals(expectedId, lastId);
        Assertions.assertEquals("Piratas do Sol", lastName);
    }

    /**
     * Teste de requisição no Controller para verificar se consegue consultar lista de teams.
     * Deve ser capaz de criar como admin e consultar como usuário comum.
     * O resultado esperado é uma lista paginavel. 
     * No teste a lista é ordenada por ID.
     * O maior ID deve corresponder ao time que acabou de ser adicionado.
     */
    @Test
    @SuppressWarnings("null")
    public void mockRestController_getAll_createTeamAndGetListOfTeamsAsCommonThenVerifyIfBiggerIdEqualCreated() throws Exception{
        /* Criando usuário admin: */
        UserRequestDTO adminRequest = new UserRequestDTO("Chopper", "chopper@gmail.com", "12345");
        User savedAdmin = userService.saveAdmin(adminRequest);

        /* Realizando login com admin: */
        LoginRequestDTO loginAdminRequest = new LoginRequestDTO(savedAdmin.getEmail(), "12345");
        ResponseEntity<LoginResponseDTO> loginAdminResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginAdminRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como admin: */
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.set("Authorization", "Bearer "+loginAdminResponse.getBody().token());
        
        /* Criando novo Team como admin: */
        TeamNoIdDTO teamNoIdDTO = new TeamNoIdDTO("Piratas do Sol");
        HttpEntity<TeamNoIdDTO> teamEntity = new HttpEntity<TeamNoIdDTO>(teamNoIdDTO, adminHeaders);
        ResponseEntity<TeamDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/teams", teamEntity, TeamDTO.class);
        long expectedId = postResponse.getBody().id();

        /* Criando usuário comum: */
        UserRequestDTO commonRequest = new UserRequestDTO("Jimbe", "jimbe@gmail.com", "54321");
        User savedCommon = userService.save(commonRequest);

        /* Fazendo login como usuário comum: */
        LoginRequestDTO loginCommonRequest = new LoginRequestDTO(savedCommon.getEmail(), "54321");
        ResponseEntity<LoginResponseDTO> loginCommonResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginCommonRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como usuário comum: */
        HttpHeaders commonHeaders = new HttpHeaders();
        commonHeaders.setContentType(MediaType.APPLICATION_JSON);
        commonHeaders.set("Authorization", "Bearer "+loginCommonResponse.getBody().token());

        /* Consultando lista de Teams como usuário comum: */
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, commonHeaders);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/teams?sort=id,desc",
                                                                HttpMethod.GET, requestEntity, String.class);

        /* Verificando resultados na lista consultada: */
        Assertions.assertNotNull(response.getBody());
        JSONObject obj = new JSONObject(response.getBody());
        Assertions.assertNotEquals(0, obj.getInt("numberOfElements"));
        JSONArray listContent = obj.getJSONArray("content");
        long lastId = listContent.getJSONObject(0).getLong("id");
        String lastName = listContent.getJSONObject(0).getString("name");
        Assertions.assertEquals(expectedId, lastId);
        Assertions.assertEquals("Piratas do Sol", lastName);
    }

    /**
     * Teste de requisição no controller para verificar se consegue atualizar um team como admin.
     * @throws URISyntaxException 
     */
    @Test
    @SuppressWarnings("null")
    public void mockRestController_update_createTeamAndUpdateAsAdminThenGetAndVerifyIfUpdateWasValid() throws URISyntaxException {
        /* Criando usuário admin: */
        UserRequestDTO adminRequest = new UserRequestDTO("Robin", "robin@gmail.com", "12345");
        User savedAdmin = userService.saveAdmin(adminRequest);

        /* Realizando login com admin: */
        LoginRequestDTO loginAdminRequest = new LoginRequestDTO(savedAdmin.getEmail(), "12345");
        ResponseEntity<LoginResponseDTO> loginAdminResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginAdminRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como admin: */
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.set("Authorization", "Bearer "+loginAdminResponse.getBody().token());

        /* Criando novo Team como admin: */
        TeamNoIdDTO teamNoIdDTO = new TeamNoIdDTO("Piratas do Barba Branca");
        HttpEntity<TeamNoIdDTO> teamEntity = new HttpEntity<TeamNoIdDTO>(teamNoIdDTO, adminHeaders);
        ResponseEntity<TeamDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/teams", teamEntity, TeamDTO.class);
        long expectedId = postResponse.getBody().id();

        /* Consultando o Team por id com as credenciais de admin: */
        ResponseEntity<TeamDTO> gotTeam = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + "/teams/"+expectedId)).headers(adminHeaders).build(), TeamDTO.class);
        
        /* Alterando os atributos do team consultado: */
        Team t = new Team(gotTeam.getBody());
        t.setName("Antigos Piratas do Barba Branca");

        /* Enviando a atualização de Team como admin: */
        HttpEntity<TeamDTO> teamUpdateEntity = new HttpEntity<TeamDTO>(new TeamDTO(t), adminHeaders);
        ResponseEntity<TeamDTO> putResponse = restTemplate.exchange(new URI(getRootUrl() + "/teams/"+expectedId), HttpMethod.PUT, teamUpdateEntity, TeamDTO.class);

        /* Verificando resultados: */
        ResponseEntity<TeamDTO> updatedTD = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + "/teams/"+expectedId)).headers(adminHeaders).build(), TeamDTO.class);
        Team updatedT = new Team(updatedTD.getBody());
        Assertions.assertEquals(putResponse.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(updatedT);
        Assertions.assertEquals("Antigos Piratas do Barba Branca", updatedT.getName());
    }

    /**
     * Teste de requisição no controller para verificar se consegue atualizar um team como usuário comum.
     * Deve resultar em não autorizado.
     * @throws URISyntaxException 
     */
    @Test
    @SuppressWarnings("null")
    public void mockRestController_update_createTeamAndUpdateAsCommonThenVerifyIfUnauthorized() throws URISyntaxException {
        /* Criando usuário admin: */
        UserRequestDTO adminRequest = new UserRequestDTO("Law", "law@gmail.com", "12345");
        User savedAdmin = userService.saveAdmin(adminRequest);

        /* Realizando login com admin: */
        LoginRequestDTO loginAdminRequest = new LoginRequestDTO(savedAdmin.getEmail(), "12345");
        ResponseEntity<LoginResponseDTO> loginAdminResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginAdminRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como admin: */
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.set("Authorization", "Bearer "+loginAdminResponse.getBody().token());

        /* Criando novo Team como admin: */
        TeamNoIdDTO teamNoIdDTO = new TeamNoIdDTO("Piratas do Barba Branca");
        HttpEntity<TeamNoIdDTO> teamEntity = new HttpEntity<TeamNoIdDTO>(teamNoIdDTO, adminHeaders);
        ResponseEntity<TeamDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/teams", teamEntity, TeamDTO.class);
        long expectedId = postResponse.getBody().id();

        /* Criando usuário comum: */
        UserRequestDTO commonRequest = new UserRequestDTO("Pedro", "pedro@gmail.com", "54321");
        User savedCommon = userService.save(commonRequest);

        /* Realizando login como usuário comum: */
        LoginRequestDTO loginCommonRequest = new LoginRequestDTO(savedCommon.getEmail(), "54321");
        ResponseEntity<LoginResponseDTO> loginCommonResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginCommonRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como usuário comum: */ 
        HttpHeaders commonHeaders = new HttpHeaders();
        commonHeaders.setContentType(MediaType.APPLICATION_JSON);
        commonHeaders.set("Authorization", "Bearer "+loginCommonResponse.getBody().token());

        /* Consultando o Team por id com as credenciais de usuário comum: */
        ResponseEntity<TeamDTO> gotTeam = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + "/teams/"+expectedId)).headers(commonHeaders).build(), TeamDTO.class);
        
        /* Alterando os atributos do Team consultado: */
        Team t = new Team(gotTeam.getBody());
        t.setName("Antigos Piratas do Barba Branca");

        /* Enviando a atualização de Team como usuário comum: */
        HttpEntity<TeamDTO> teamUpdateEntity = new HttpEntity<TeamDTO>(new TeamDTO(t), commonHeaders);
        ResponseEntity<TeamDTO> putResponse = restTemplate.exchange(new URI(getRootUrl() + "/teams/"+expectedId), HttpMethod.PUT, teamUpdateEntity, TeamDTO.class);
        
        /* Verificando resultados: */
        Assertions.assertNotNull(putResponse);
        Assertions.assertEquals(putResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);
        Assertions.assertEquals(putResponse.getStatusCode().value(), HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * Teste de requisição no controller para verificar se consegue deletar um team como admin.
     * @throws URISyntaxException 
     */
    @Test
    @SuppressWarnings("null")
    public void mockRestController_delete_createTeamAndDeleteAsAdminThenVerifyIfDontExists() throws URISyntaxException {
        /* Criando usuário admin: */
        UserRequestDTO adminRequest = new UserRequestDTO("Vivi", "vivi@gmail.com", "12345");
        User savedAdmin = userService.saveAdmin(adminRequest);

        /* Realizando login com admin: */
        LoginRequestDTO loginAdminRequest = new LoginRequestDTO(savedAdmin.getEmail(), "12345");
        ResponseEntity<LoginResponseDTO> loginAdminResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginAdminRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como admin: */
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.set("Authorization", "Bearer "+loginAdminResponse.getBody().token());
        
        /* Criando novo Team como admin: */
        TeamNoIdDTO teamNoIdDTO = new TeamNoIdDTO("Baroque Works");
        HttpEntity<TeamNoIdDTO> teamEntity = new HttpEntity<TeamNoIdDTO>(teamNoIdDTO, adminHeaders);
        ResponseEntity<TeamDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/teams", teamEntity, TeamDTO.class);
        long expectedId = postResponse.getBody().id();

        /* Consultando o Team por id com as credenciais de admin: */
        ResponseEntity<TeamDTO> gotTeam = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + "/teams/"+expectedId)).headers(adminHeaders).build(), TeamDTO.class);
        Assertions.assertNotNull(gotTeam.getBody());
        
        /* Deletando o Team por id com as credenciais de admin: */
        HttpEntity<TeamDTO> teamDeleteEntity = new HttpEntity<TeamDTO>(null, adminHeaders);
        ResponseEntity<TeamDTO> deleteResponse = restTemplate.exchange(new URI(getRootUrl() + "/teams/"+expectedId), HttpMethod.DELETE, teamDeleteEntity, TeamDTO.class);
        
        /* Verificando resultados: */
        Assertions.assertEquals(deleteResponse.getStatusCode(), HttpStatus.OK);
        try {
            restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + "/teams/"+expectedId)).headers(adminHeaders).build(), TeamDTO.class);
        } catch (final HttpClientErrorException e) {
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Teste de requisição no controller para verificar se consegue deletar um team como usuário comum.
     * Deve resultar em não autorizado.
     * @throws URISyntaxException 
     */
    @Test
    @SuppressWarnings("null")
    public void mockRestController_delete_createTeamAndDeleteAsCommonThenVerifyIfUnauthorized() throws URISyntaxException {
        /* Criando usuário admin: */
        UserRequestDTO adminRequest = new UserRequestDTO("Ace", "ace@gmail.com", "12345");
        User savedAdmin = userService.saveAdmin(adminRequest);

        /* Realizando login com admin: */
        LoginRequestDTO loginAdminRequest = new LoginRequestDTO(savedAdmin.getEmail(), "12345");
        ResponseEntity<LoginResponseDTO> loginAdminResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginAdminRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como admin: */
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.set("Authorization", "Bearer "+loginAdminResponse.getBody().token());
        
        /* Criando novo Team como admin: */
        TeamNoIdDTO teamNoIdDTO = new TeamNoIdDTO("Baroque Works");
        HttpEntity<TeamNoIdDTO> teamEntity = new HttpEntity<TeamNoIdDTO>(teamNoIdDTO, adminHeaders);
        ResponseEntity<TeamDTO> postResponse = restTemplate.postForEntity(getRootUrl() + "/teams", teamEntity, TeamDTO.class);
        long expectedId = postResponse.getBody().id();

        /* Criando usuário comum: */
        UserRequestDTO commonRequest = new UserRequestDTO("Carrot", "carrot@gmail.com", "54321");
        User savedCommon = userService.save(commonRequest);

        /* Realizando login como usuário comum: */
        LoginRequestDTO loginCommonRequest = new LoginRequestDTO(savedCommon.getEmail(), "54321");
        ResponseEntity<LoginResponseDTO> loginCommonResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginCommonRequest, LoginResponseDTO.class);

        /* Criando header com token JWT para próximas requests como usuário comum: */
        HttpHeaders commonHeaders = new HttpHeaders();
        commonHeaders.setContentType(MediaType.APPLICATION_JSON);
        commonHeaders.set("Authorization", "Bearer "+loginCommonResponse.getBody().token());

        /* Consultando o Team por id com as credenciais de usuário comum: */
        ResponseEntity<TeamDTO> gotTeam = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + "/teams/"+expectedId)).headers(commonHeaders).build(), TeamDTO.class);
        Assertions.assertNotNull(gotTeam.getBody());

        /* Deletando o Team por id com as credenciais de usuário comum: */
        HttpEntity<TeamDTO> teamDeleteEntity = new HttpEntity<TeamDTO>(null, commonHeaders);
        ResponseEntity<TeamDTO> deleteResponse = restTemplate.exchange(new URI(getRootUrl() + "/teams/"+expectedId), HttpMethod.DELETE, teamDeleteEntity, TeamDTO.class);
        
        /* Verificando resultados: */
        Assertions.assertEquals(deleteResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);
        ResponseEntity<TeamDTO> newGotTeam = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + "/teams/"+expectedId)).headers(adminHeaders).build(), TeamDTO.class);
        Assertions.assertNotNull(newGotTeam.getBody());
        
    }

}
