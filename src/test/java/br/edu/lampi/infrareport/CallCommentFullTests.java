package br.edu.lampi.infrareport;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import br.edu.lampi.infrareport.config.ExceptionDetails;
import br.edu.lampi.infrareport.controller.dto.call.CallUserRequestDTO;
import br.edu.lampi.infrareport.controller.dto.callComment.CallCommentResponseDTO;

import br.edu.lampi.infrareport.model.building.Building;
import br.edu.lampi.infrareport.model.floor.Floor;
import br.edu.lampi.infrareport.service.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import br.edu.lampi.infrareport.controller.dto.callComment.CallCommentRequestDTO;
import br.edu.lampi.infrareport.controller.dto.category.CategoryRequestDto;
import br.edu.lampi.infrareport.controller.dto.user.LoginRequestDTO;
import br.edu.lampi.infrareport.controller.dto.user.LoginResponseDTO;
import br.edu.lampi.infrareport.controller.dto.user.UserRequestDTO;
import br.edu.lampi.infrareport.model.call.Call;
import br.edu.lampi.infrareport.model.call.CallPriority;
import br.edu.lampi.infrareport.model.callComment.CallComment;
import br.edu.lampi.infrareport.model.category.Category;
import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.model.user.UserType;
import br.edu.lampi.infrareport.repository.CallRepository;
import br.edu.lampi.infrareport.service.exceptions.BadRequestException;
import br.edu.lampi.infrareport.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TestTransaction;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CallCommentFullTests extends InfrareportApplicationTests {
    @Autowired
    CallRepository callRepo;

    @Autowired
    CallCommentService callCommentService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    UserService userService;

    @Autowired
    CallService callService;

    @Autowired
    BuildingService buildingService;

    @Autowired
    FloorService floorService;

    @Autowired
    EntityManager entityManager;

    private final String CALL_COMMENT_URL = "/comment";

    /**
     * Teste para verificar Model de CallComment
     */
    @Test
    public void model_instantiateCallCommentAndCheckIfValid() {
        CallComment c = new CallComment();
        LocalDateTime instant = LocalDateTime.now();
        c.setId(13L);
        c.setDateTime(instant);
        c.setMessage("Mensagem Mensagem Mensagem");
        c.setViewed(false);
        final String expected = "CallComment(id=13, user=null, call=null, dateTime=" + instant + ", message=Mensagem Mensagem Mensagem, viewed=false)";
        Assertions.assertEquals(expected, c.toString());
    }

    /**
     * Teste para verificar save em Service de CallComment
     */
    @SuppressWarnings("null")
    @Test
    @Transactional
    public void service_save_saveCallCommentAndCheckIfValid() {
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        Category category = new Category();
        category.setName("Categoria");
        Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

        // Configura building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        Call call = new Call();
        LocalDateTime instant = LocalDateTime.now();
        call.setTitle("Titulo Titulo");
        call.setDescription("Descricao Descricao Descricao");
        call.setDateTime(instant);
        call.setUser(savedUser);
        call.setFloor(savedFloor);
        call.setCategory(savedCategory);
        call.setPriority(CallPriority.High);
        call.setActive(true);
        call.setJustification("Justificativa Justificativa");

        CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(call.getTitle(), call.getDescription(), call.getDateTime(), call.getUser().getId(), call.getFloor().getId());
        Call savedCall = callService.save(callUserRequestDTO);

        CallComment comment = new CallComment();
        comment.setUser(savedUser);
        comment.setCall(savedCall);
        comment.setMessage("Mensagem Mensagem Mensagem");
        comment.setDateTime(instant);
        comment.setViewed(false);

        CallCommentRequestDTO callCommentRequestDTO = new CallCommentRequestDTO(comment.getUser().getId(), comment.getCall().getId(), comment.getDateTime(), comment.getMessage(), comment.getViewed());
        CallComment savedCallComment = callCommentService.save(callCommentRequestDTO);

        Assertions.assertNotNull(savedCallComment);
        Assertions.assertNotNull(savedCallComment.getId());
        Assertions.assertNotEquals("0", savedCallComment.getId());
        Assertions.assertEquals(comment.getDateTime(), savedCallComment.getDateTime());
        Assertions.assertEquals(comment.getMessage(), savedCallComment.getMessage());
    }


    /**
     * Teste para verificar save falha de save no Service de CallImage
     */
    @SuppressWarnings("null")
    @Test
    @Transactional
    public void service_save_saveCallCommentWithouCallAndCheckIfFailed() {
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));

        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        CallComment comment = new CallComment();
        LocalDateTime instant = LocalDateTime.now();
        comment.setUser(savedUser);
        comment.setMessage("Mensagem Mensagem Mensagem");
        comment.setDateTime(instant);
        comment.setViewed(false);

        CallCommentRequestDTO callCommentRequestDTO = new CallCommentRequestDTO(comment.getUser().getId(), null, comment.getDateTime(), comment.getMessage(), comment.getViewed());

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            @SuppressWarnings("unused")
            CallComment savedComment = callCommentService.save(callCommentRequestDTO);
        });

        Assertions.assertEquals("The given call id must not be null", thrown.getMessage());
    }

    /**
     * Teste para verificar get em Service de CallComment
     */
    @SuppressWarnings("null")
    @Test
    @Transactional
    public void service_getById_saveCallCommentAndGetCreatedCallCommentByIdThenVerifyIfEqualCreated() {
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        Category category = new Category();
        category.setName("Categoria");
        Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

        // Configura building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setCategory(savedCategory);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
        Call savedCall = callService.save(callUserRequestDTO);

        CallComment comment = new CallComment();
        comment.setUser(savedUser);
        comment.setCall(savedCall);
        comment.setMessage("Mensagem Mensagem Mensagem");
        comment.setDateTime(instant);
        comment.setViewed(false);

        CallCommentRequestDTO callCommentRequestDTO = new CallCommentRequestDTO(comment.getUser().getId(), comment.getCall().getId(), comment.getDateTime(), comment.getMessage(), comment.getViewed());
        CallComment savedCallComment = callCommentService.save(callCommentRequestDTO);
        Long savedCallCommentId = savedCallComment.getId();

        CallComment gotCallComment = callCommentService.getById(savedCallCommentId);

        Assertions.assertNotNull(gotCallComment);
        Assertions.assertEquals(savedCallComment.getMessage(), gotCallComment.getMessage());
        Assertions.assertEquals(savedCallComment.getDateTime(), gotCallComment.getDateTime());
        Assertions.assertEquals(savedCallComment.getCall().getId(), gotCallComment.getCall().getId());
        Assertions.assertNotNull(gotCallComment.getId());
        Assertions.assertNotEquals("0", gotCallComment.getId());
    }


    /**
     * Teste para verificar get em Service de CallComment
     */
    @SuppressWarnings("null")
    @Test
    @Transactional
    public void service_getByCallId_saveCallCommentAndGetCreatedByCallIdThenVerifyIfEqualCreated() {
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        Category category = new Category();
        category.setName("Categoria");
        Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

        // Configura building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setCategory(savedCategory);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
        Call savedCall = callService.save(callUserRequestDTO);

        CallComment comment = new CallComment();
        comment.setUser(savedUser);
        comment.setCall(savedCall);
        comment.setMessage("Mensagem Mensagem Mensagem");
        comment.setDateTime(instant);
        comment.setViewed(false);

        CallCommentRequestDTO callCommentRequestDTO = new CallCommentRequestDTO(comment.getUser().getId(), comment.getCall().getId(), comment.getDateTime(), comment.getMessage(), comment.getViewed());
        CallComment savedCallComment = callCommentService.save(callCommentRequestDTO);
        Long savedCallId = savedCall.getId();

        List<CallComment> gotCallCommentList = callCommentService.getByCallId(savedCallId);

        Assertions.assertNotNull(gotCallCommentList);
        Assertions.assertFalse(gotCallCommentList.isEmpty());
        Assertions.assertEquals(savedCallComment.getMessage(), gotCallCommentList.get(0).getMessage());
    }


    /**
     * Teste para verificar get all em Service de CallComment
     */
    @SuppressWarnings("null")
    @Test
    @Transactional
    public void service_getAll_saveCallCommentAndGetAllThenVerifyIfCreatedIsInList() {
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        Category category = new Category();
        category.setName("Categoria");
        Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

        // Configura building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setCategory(savedCategory);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
        Call savedCall = callService.save(callUserRequestDTO);

        CallComment comment = new CallComment();
        comment.setUser(savedUser);
        comment.setCall(savedCall);
        comment.setMessage("Mensagem Mensagem Mensagem");
        comment.setDateTime(instant);
        comment.setViewed(false);

        CallCommentRequestDTO callCommentRequestDTO = new CallCommentRequestDTO(comment.getUser().getId(), comment.getCall().getId(), comment.getDateTime(), comment.getMessage(), comment.getViewed());
        CallComment savedCallComment = callCommentService.save(callCommentRequestDTO);

        List<CallComment> gotCallComments = callCommentService.getAll();

        Assertions.assertNotNull(gotCallComments);
        Assertions.assertNotEquals(0, gotCallComments.size());
        Assertions.assertTrue(gotCallComments.contains(savedCallComment));
    }


    /**
     * Teste para verificar delete and exists em Service de CallComment
     */
    @SuppressWarnings("null")
    @Test
    @Transactional
    public void service_deleteAndExists_saveCallCommentAndDeleteCreatedCallCommentByIdThenVerifyIfExists() {
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        Category category = new Category();
        category.setName("Categoria");
        Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

        // Configura building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setCategory(savedCategory);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
        Call savedCall = callService.save(callUserRequestDTO);

        CallComment comment = new CallComment();
        comment.setUser(savedUser);
        comment.setCall(savedCall);
        comment.setMessage("Mensagem Mensagem Mensagem");
        comment.setDateTime(instant);
        comment.setViewed(false);

        CallCommentRequestDTO callCommentRequestDTO = new CallCommentRequestDTO(comment.getUser().getId(), comment.getCall().getId(), comment.getDateTime(), comment.getMessage(), comment.getViewed());
        CallComment savedCallComment = callCommentService.save(callCommentRequestDTO);
        Long savedCallCommentId = savedCallComment.getId();

        boolean existsBefore = callCommentService.existsById(savedCallCommentId);

        callCommentService.deleteById(savedCallCommentId);

        boolean existsAfter = callCommentService.existsById(savedCallCommentId);

        Assertions.assertTrue(existsBefore);
        Assertions.assertFalse(existsAfter);
    }

    /**
     * Teste para verificar update em Service de CallComment
     */
    @SuppressWarnings("null")
    @Test
    @Transactional
    public void service_updateById_saveCallCommentAndUpdateCallCommentByIdThenVerifyIfValid() {
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        Category category = new Category();
        category.setName("Categoria");
        Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

        // Configura building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setCategory(savedCategory);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
        Call savedCall = callService.save(callUserRequestDTO);

        CallComment comment = new CallComment();
        comment.setUser(savedUser);
        comment.setCall(savedCall);
        comment.setMessage("Mensagem Mensagem Mensagem");
        comment.setDateTime(instant);
        comment.setViewed(false);

        CallCommentRequestDTO callCommentRequestDTO = new CallCommentRequestDTO(comment.getUser().getId(), comment.getCall().getId(), comment.getDateTime(), comment.getMessage(), comment.getViewed());
        CallComment savedCallComment = callCommentService.save(callCommentRequestDTO);
        Long savedCallCommentId = savedCallComment.getId();

        LocalDateTime newInstant = LocalDateTime.now().plusHours(1);
        String newMessage = "Mensagem Mensagem Mensagem Mensagem Mensagem Mensagem";
        boolean newViewed = true;
        CallCommentRequestDTO updateCallComment = new CallCommentRequestDTO(comment.getUser().getId(), comment.getCall().getId(), newInstant, newMessage, newViewed);
        callCommentService.updateById(savedCallCommentId, updateCallComment);

        CallComment updatedCallComment = callCommentService.getById(savedCallCommentId);
        Assertions.assertNotNull(updatedCallComment);
        Assertions.assertEquals(newInstant, updatedCallComment.getDateTime());
        Assertions.assertEquals(newMessage, updatedCallComment.getMessage());
        Assertions.assertEquals(newViewed, updatedCallComment.getViewed());
    }


        /**
     * Teste para verificar addViewedStatusToCallComment em Service de CallComment
     */

    @SuppressWarnings("null")
    @Test
    @Transactional
    public void service_addViewedStatusToCallComment_saveCallCommentAndSetTrueForCallCommentViewedStatusByCallIdThenVerifyIfValid() {
        // Criar e salvar usuário
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
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
        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        Call savedCall = callRepo.save(c);

        // Criar e salvar comentário da chamada
        CallCommentRequestDTO callCommentRequestDTO = new CallCommentRequestDTO(savedUser.getId(), savedCall.getId(), instant, "Mensagem Mensagem Mensagem", false);
        CallComment savedCallComment = callCommentService.save(callCommentRequestDTO);

        // Adicionar comentário à chamada e salvar
        savedCall.setComments(new ArrayList<>(List.of(savedCallComment)));
        savedCall = callRepo.save(savedCall);

        // Marcar comentário como visualizado
        this.callCommentService.addViewedStatusToCallCommentByCallId(savedCall.getId());

        // Recuperar comentário atualizado
        CallComment returnedCallComment = this.callCommentService.getById(savedCallComment.getId());

        // Verificações
        Assertions.assertNotNull(returnedCallComment, "the returned call comment must be not null when successful");
        Assertions.assertEquals(savedCallComment.getId(), returnedCallComment.getId(), "the returned call comment id must be the same as the saved call comment when successful");
        Assertions.assertEquals(callCommentRequestDTO.message(), returnedCallComment.getMessage(), "the returned call comment message must be the same as the saved call comment when successful");
        Assertions.assertTrue(returnedCallComment.getViewed(), "the returned call comment viewed status must be true when successful");
    }

    /**
     * Teste para verificar se addViewedStatusToCallComment em Service de CallComment joga exception quando call nao encontrada
     */

    @SuppressWarnings("null")
    @Test
    @Transactional
    public void service_addViewedStatusToCallComment_saveCallCommentAndThrowsResourceNotFoundExceptionWhenCallNotFoundById() {
        // Criar e salvar usuário
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
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
        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        Call savedCall = callRepo.save(c);

        // Criar e salvar comentário da chamada
        CallCommentRequestDTO callCommentRequestDTO = new CallCommentRequestDTO(savedUser.getId(), savedCall.getId(), instant, "Mensagem Mensagem Mensagem", false);
        CallComment savedCallComment = callCommentService.save(callCommentRequestDTO);

        // Adicionar comentário à chamada e salvar
        savedCall.setComments(new ArrayList<>(List.of(savedCallComment)));

        // Verificar se a exceção é lançada quando a chamada não é encontrada
        Assertions.assertThrows(ResourceNotFoundException.class, () -> this.callCommentService.addViewedStatusToCallCommentByCallId(1000000L),
                "it must throw an exception when the call is not found by id");

        // Recuperar comentário para verificação adicional
        CallComment returnedCallComment = this.callCommentService.getById(savedCallComment.getId());

        // Verificações
        Assertions.assertNotNull(returnedCallComment, "the returned call comment must be not null when an exception is thrown");
        Assertions.assertEquals(savedCallComment.getId(), returnedCallComment.getId(), "the returned call comment id must be the same as the saved call comment when an exception is thrown");
        Assertions.assertEquals(callCommentRequestDTO.message(), returnedCallComment.getMessage(), "the returned call comment message must be the same as the saved call comment when an exception is thrown");
        Assertions.assertFalse(returnedCallComment.getViewed(), "the returned call comment viewed status must be false when an exception is thrown");
    }


    /**
     * Teste para verificar o Controller de pesquisar por id do CallComment
     */
    @Test
    public void controller_searchById_createCallCommentAndSearchByYourId() throws URISyntaxException {
        HttpHeaders headers = getAuthorizationTokenHeader();
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        Category category = new Category();
        category.setName("Categoria");
        Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

        // Configura building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setCategory(savedCategory);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
        Call savedCall = callService.save(callUserRequestDTO);

        CallComment comment = new CallComment();
        comment.setUser(savedUser);
        comment.setCall(savedCall);
        comment.setMessage("Mensagem Mensagem Mensagem");
        comment.setDateTime(instant);
        comment.setViewed(false);

        CallCommentRequestDTO callCommentRequest = new CallCommentRequestDTO(comment.getUser().getId(), comment.getCall().getId(), comment.getDateTime(), comment.getMessage(), comment.getViewed());
        HttpEntity<CallCommentRequestDTO> entity = new HttpEntity<>(callCommentRequest, headers);

        ResponseEntity<CallCommentResponseDTO> savedCallComment = restTemplate.postForEntity(getRootUrl() + CALL_COMMENT_URL, entity, CallCommentResponseDTO.class);

        Assertions.assertNotNull(savedCallComment);

        Long id = savedCallComment.getBody().call_id();

        ResponseEntity<CallCommentResponseDTO> searchResultCallComment = restTemplate.exchange(
                RequestEntity.get(new URI(getRootUrl() + CALL_COMMENT_URL + "/" + id)).headers(headers).build(),
                CallCommentResponseDTO.class
        );

        CallCommentResponseDTO returnedComment = searchResultCallComment.getBody();

        Assertions.assertEquals(savedCallComment.getBody().id(), returnedComment.id());
        Assertions.assertEquals(savedCallComment.getBody().call_id(), returnedComment.call_id());
        Assertions.assertEquals(savedCallComment.getBody().dateTime().truncatedTo(ChronoUnit.SECONDS), returnedComment.dateTime().truncatedTo(ChronoUnit.SECONDS));
        Assertions.assertEquals(savedCallComment.getBody().message(), returnedComment.message());
        Assertions.assertEquals(savedCallComment.getBody().viewed(), returnedComment.viewed());
    }

    /**
     * Teste para verificar o Controller de pesquisar por id do CallComment
     */
    @Test
    public void controller_searchById_shouldThrowRunTimeExceptionWhenNotFoundCallCommentWithId() throws URISyntaxException {

        ResponseEntity<ExceptionDetails> exceptionCallComment = restTemplate.exchange(RequestEntity.get(new URI(getRootUrl() + CALL_COMMENT_URL + "/" + 1L)).headers(getAuthorizationTokenHeader()).build(), ExceptionDetails.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), exceptionCallComment.getBody().getStatus());
    }
    /**
     * Teste para verificar o Controller de retornar todos os CallComment
     * @throws JSONException
     */
    @Test
    @Transactional
    public void controller_searchAll_returnCallCommentList() throws JSONException {
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        HttpHeaders headers = getAuthorizationTokenHeader();

        Category category = new Category();
        category.setName("Categoria");
        Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

        // Configura building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setCategory(savedCategory);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
        Call savedCall = callService.save(callUserRequestDTO);

        CallComment comment = new CallComment();
        comment.setUser(savedUser);
        comment.setCall(savedCall);
        comment.setMessage("Mensagem Mensagem Mensagem");
        comment.setDateTime(instant);
        comment.setViewed(false);

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
        CallCommentRequestDTO callCommentRequest = new CallCommentRequestDTO(comment.getUser().getId(), comment.getCall().getId(), comment.getDateTime(), comment.getMessage(), comment.getViewed());
        HttpEntity<CallCommentRequestDTO> entityToSave = new HttpEntity<>(callCommentRequest, headers);

        ResponseEntity<CallCommentResponseDTO> savedCallComment = restTemplate.postForEntity(getRootUrl() + CALL_COMMENT_URL, entityToSave, CallCommentResponseDTO.class);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseCallCommentList = restTemplate.exchange(getRootUrl() + CALL_COMMENT_URL, HttpMethod.GET, entity, String.class);

        JSONArray listCallComment = new JSONArray(responseCallCommentList.getBody());

        Assertions.assertNotNull(responseCallCommentList.getBody());
        Assertions.assertEquals(OK, responseCallCommentList.getStatusCode());
        Assertions.assertFalse(responseCallCommentList.getBody().isEmpty());
        Assertions.assertEquals(savedCallComment.getBody().id(), listCallComment.getJSONObject(0).getLong("id"));
        Assertions.assertEquals(savedCallComment.getBody().call_id(), listCallComment.getJSONObject(0).getLong("call_id"));
        Assertions.assertEquals(savedCallComment.getBody().userId(), listCallComment.getJSONObject(0).getLong("userId"));
        Assertions.assertEquals(savedCallComment.getBody().message(), listCallComment.getJSONObject(0).getString("message"));
        Assertions.assertEquals(savedCallComment.getBody().viewed(), listCallComment.getJSONObject(0).getBoolean("viewed"));
        Assertions.assertEquals(savedCallComment.getBody().dateTime().toString().split("\\.")[0], listCallComment.getJSONObject(0).getString("dateTime").split("\\.")[0]);
    }


    /**
     * Teste para verificar o Controller de retornar todos os CallComment
     * @throws JSONException
     */
    @Test
    public void controller_searchAll_returnEmptyCallCommentList() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, getAuthorizationTokenHeader());

        ResponseEntity<String> responseCallCommentList = restTemplate.exchange(getRootUrl() + CALL_COMMENT_URL,
                HttpMethod.GET, entity, String.class);

        JSONArray listCallCommentEmpty = new JSONArray(responseCallCommentList.getBody());
        Assertions.assertEquals(0, listCallCommentEmpty.length());
    }
    /**
     * Teste para verificar o Controller de pesquisar por id da Call do CallComment
     * @throws JSONException
     */
    @Test
    public void controller_searchByIdCall_createCallAndCallCommentAndSearchByIdCall() throws JSONException {
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        Category category = new Category();
        category.setName("Categoria");
        Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

        // Configura building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setCategory(savedCategory);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
        Call savedCall = callService.save(callUserRequestDTO);

        CallComment comment = new CallComment();
        comment.setUser(savedUser);
        comment.setCall(savedCall);
        comment.setMessage("Mensagem Mensagem Mensagem");
        comment.setDateTime(instant);
        comment.setViewed(false);

        CallCommentRequestDTO callCommentRequestDTO = new CallCommentRequestDTO(comment.getUser().getId(), comment.getCall().getId(), comment.getDateTime(), comment.getMessage(), comment.getViewed());
        HttpEntity<CallCommentRequestDTO> entity = new HttpEntity<>(callCommentRequestDTO, getAuthorizationTokenHeader());

        ResponseEntity<CallCommentResponseDTO> savedCallComment = restTemplate.postForEntity(getRootUrl() + CALL_COMMENT_URL, entity, CallCommentResponseDTO.class);

        Assertions.assertNotNull(savedCallComment);

        Long id = savedCall.getId();

        ResponseEntity<String> responseCallCommentList = restTemplate.exchange(getRootUrl() + CALL_COMMENT_URL + "/call/" + id, HttpMethod.GET, entity, String.class);

        JSONArray listCallComment = new JSONArray(responseCallCommentList.getBody());
        Assertions.assertNotNull(responseCallCommentList.getBody());

        Assertions.assertEquals(savedCallComment.getBody().id(), listCallComment.getJSONObject(0).getLong("id"));
        Assertions.assertEquals(savedCallComment.getBody().call_id(), listCallComment.getJSONObject(0).getLong("call_id"));
        Assertions.assertEquals(savedCallComment.getBody().userId(), listCallComment.getJSONObject(0).getLong("userId"));
        Assertions.assertEquals(savedCallComment.getBody().message(), listCallComment.getJSONObject(0).getString("message"));
        Assertions.assertEquals(savedCallComment.getBody().viewed(), listCallComment.getJSONObject(0).getBoolean("viewed"));
        Assertions.assertEquals(savedCallComment.getBody().dateTime().toString().split("\\.")[0], listCallComment.getJSONObject(0).getString("dateTime").split("\\.")[0]);
    }

    /**
     * Teste para verificar o Controller de pesquisar por id da Call do CallComment
     * @throws JSONException
     */
    @Test
    public void controller_searchByIdCall_shouldReturnEmptyListCallCommentWhenIdCallIsInvalid() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<String>(null, getAuthorizationTokenHeader());

        ResponseEntity<String> responseCallCommentList = restTemplate.exchange(getRootUrl() + CALL_COMMENT_URL + "/call/" + 1L,
                HttpMethod.GET, entity, String.class);

        JSONArray listCallCommentEmpty = new JSONArray(responseCallCommentList.getBody());
        Assertions.assertEquals(0, listCallCommentEmpty.length());
    }
    /**
     * Teste para verificar o Controller de criar o CallComment
     */
    @Test
    public void controller_createCallComment_shouldReturnCallComment() {
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        Category category = new Category();
        category.setName("Categoria");
        Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

        // Configura building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setCategory(savedCategory);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
        Call savedCall = callService.save(callUserRequestDTO);

        CallComment comment = new CallComment();
        comment.setUser(savedUser);
        comment.setCall(savedCall);
        comment.setMessage("Mensagem Mensagem Mensagem");
        comment.setDateTime(instant);
        comment.setViewed(false);

        CallCommentRequestDTO callCommentRequest = new CallCommentRequestDTO(comment.getUser().getId(), comment.getCall().getId(), comment.getDateTime(), comment.getMessage(), comment.getViewed());

        HttpEntity<CallCommentRequestDTO> entity = new HttpEntity<>(callCommentRequest, getAuthorizationTokenHeader());

        ResponseEntity<CallCommentResponseDTO> savedCallComment = restTemplate.postForEntity(getRootUrl() + CALL_COMMENT_URL, entity, CallCommentResponseDTO.class);

        Assertions.assertNotNull(savedCallComment.getBody());
        Assertions.assertEquals(callCommentRequest.userId(), savedCallComment.getBody().userId());
        Assertions.assertEquals(callCommentRequest.message(), savedCallComment.getBody().message());
        Assertions.assertEquals(callCommentRequest.viewed(), savedCallComment.getBody().viewed());
        Assertions.assertEquals(callCommentRequest.dateTime(), savedCallComment.getBody().dateTime());
    }


    /**
     * Teste para verificar o Controller de alterar dados do CallComment
     * @throws JSONException
     */
    @Test
    @Transactional
    public void controller_updateById_createCallCommentAndChangeTheDataLater() throws JSONException {
        HttpHeaders headers = getAuthorizationTokenHeader();
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        Category category = new Category();
        category.setName("Categoria");
        Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

        // Configura building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setCategory(savedCategory);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
        Call savedCall = callService.save(callUserRequestDTO);

        CallComment comment = new CallComment();
        comment.setUser(savedUser);
        comment.setCall(savedCall);
        comment.setMessage("Mensagem Mensagem Mensagem");
        comment.setDateTime(instant);
        comment.setViewed(false);

        CallCommentRequestDTO callCommentRequest = new CallCommentRequestDTO(comment.getUser().getId(), savedCall.getId(), comment.getDateTime(), comment.getMessage(), comment.getViewed());
        HttpEntity<CallCommentRequestDTO> entity = new HttpEntity<>(callCommentRequest, headers);

        Assertions.assertFalse(this.callRepo.findById(savedCall.getId()).isEmpty());
        CallComment savedCallComment = this.callCommentService.save(callCommentRequest);

        CallCommentRequestDTO requestUpdateDto = new CallCommentRequestDTO(savedCallComment.getUser().getId(), savedCallComment.getCall().getId(), savedCallComment.getDateTime(), "Update update update", savedCallComment.getViewed());

        entity = new HttpEntity<>(requestUpdateDto, headers);

        ResponseEntity<CallCommentResponseDTO> callCommentResponseUpdate = restTemplate.exchange(
                getRootUrl() + CALL_COMMENT_URL + "/" + savedCallComment.getId(), HttpMethod.PUT, entity, CallCommentResponseDTO.class);

        Assertions.assertNotNull(callCommentResponseUpdate);
        Assertions.assertNotEquals(savedCallComment.getMessage(), callCommentResponseUpdate.getBody().message());
    }

    /**
     * Teste para verificar o Controller de deletar o CallComment
     */
    @Test
    public void controller_deleteById_CallCommentDeletedWithId() {
        HttpHeaders headers = getAuthorizationTokenHeader();
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
        user.setPassword("passpass");
        User savedUser = userService.save(new UserRequestDTO(user));

        Category category = new Category();
        category.setName("Categoria");
        Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

        // Configura building e floor
        Building building = new Building();
        building.setName("Main Building");
        Building savedBuilding = buildingService.saveNewBuilding(building);

        Floor floor = new Floor();
        floor.setName("First Floor");
        floor.setBuilding(savedBuilding);
        Floor savedFloor = floorService.saveNewFloor(floor);

        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setCategory(savedCategory);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
        Call savedCall = callService.save(callUserRequestDTO);

        CallComment comment = new CallComment();
        comment.setUser(savedUser);
        comment.setCall(savedCall);
        comment.setMessage("Mensagem Mensagem Mensagem");
        comment.setDateTime(instant);
        comment.setViewed(false);

        CallCommentRequestDTO callCommentRequest = new CallCommentRequestDTO(comment.getUser().getId(), comment.getCall().getId(), comment.getDateTime(), comment.getMessage(), comment.getViewed());
        HttpEntity<CallCommentRequestDTO> entity = new HttpEntity<>(callCommentRequest, headers);

        ResponseEntity<CallCommentResponseDTO> savedCallComment = restTemplate.postForEntity(getRootUrl() + CALL_COMMENT_URL, entity, CallCommentResponseDTO.class);

        entity = new HttpEntity<>(null, headers);

        restTemplate.exchange(getRootUrl() + CALL_COMMENT_URL + "/" + savedCallComment.getBody().id(), HttpMethod.DELETE, entity, Void.class);

        Long id = savedCallComment.getBody().id();

        HttpEntity<CallCommentRequestDTO> callCommentEntity = new HttpEntity<>(null, headers);

        assertNotNull(id);
        ResponseEntity<ExceptionDetails> exception = restTemplate.exchange(getRootUrl() + CALL_COMMENT_URL + "/" + id, HttpMethod.DELETE, callCommentEntity, ExceptionDetails.class);

        Assertions.assertEquals(NOT_FOUND.value(), exception.getBody().getStatus());
    }

    /**
     * Teste para verificar o Controller de deletar o CallComment
     */
    @Test
    public void controller_deleteById_shouldThrowRuntimeExceptionWhenCallCommentNotFound(){
        HttpEntity<ExceptionDetails> entity = new HttpEntity<ExceptionDetails>(null, getAuthorizationTokenHeader());

        ResponseEntity<ExceptionDetails> exception = restTemplate.exchange(getRootUrl() + CALL_COMMENT_URL + "/" + 1000000L,
                HttpMethod.DELETE, entity, ExceptionDetails.class);

        Assertions.assertEquals(NOT_FOUND.value(), exception.getBody().getStatus());

    }

            /**
     * Teste para verificar addViewedStatusToCallComment em Controller de CallComment
     */

    @SuppressWarnings("null")
    @Test
    @Transactional
    public void controller_addViewedStatusToCallComment_saveCallCommentAndSetTrueForCallCommentViewedStatusByCallIdThenVerifyIfValid() {
        // Criar e salvar usuário
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
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
        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        Call savedCall = callRepo.save(c);

        // Criar e salvar comentário da chamada
        CallCommentRequestDTO callCommentRequestDTO = new CallCommentRequestDTO(savedUser.getId(), savedCall.getId(), instant, "Mensagem Mensagem Mensagem", false);
        CallComment savedCallComment = callCommentService.save(callCommentRequestDTO);

        // Adicionar comentário à chamada e salvar
        savedCall.setComments(new ArrayList<>(List.of(savedCallComment)));
        savedCall = callRepo.save(savedCall);

        // Finalizar a transação atual para garantir que os dados estejam persistidos
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        // Enviar a requisição para atualizar o status de visualização do comentário
        HttpEntity<Void> entity = new HttpEntity<>(getAuthorizationTokenHeader());
        this.restTemplate.exchange(
                getRootUrl() + CALL_COMMENT_URL + "/call/" + savedCall.getId() + "/messages/viewed",
                HttpMethod.PUT, entity, Void.class
        );

        // Recuperar o comentário atualizado
        CallComment returnedCallComment = this.callCommentService.getById(savedCallComment.getId());

        // Verificações
        Assertions.assertNotNull(returnedCallComment, "the returned call comment must be not null when successful");
        Assertions.assertEquals(savedCallComment.getId(), returnedCallComment.getId(), "the returned call comment id must be the same as the saved call comment when successful");
        Assertions.assertEquals(callCommentRequestDTO.message(), returnedCallComment.getMessage(), "the returned call comment message must be the same as the saved call comment when successful");
        Assertions.assertTrue(returnedCallComment.getViewed(), "the returned call comment viewed status must be true when successful");
    }


    /**
      * Teste para verificar se addViewedStatusToCallComment em Controller de CallComment joga exception quando call nao encontrada
      */

    @SuppressWarnings("null")
    @Test
    @Transactional
    public void controller_addViewedStatusToCallComment_saveCallCommentAndThrowsResourceNotFoundExceptionWhenCallNotFoundById() {
        // Criar e salvar usuário
        User user = new User();
        user.setName("Usuario Usuario");
        int seed = new Random().nextInt();
        user.setEmail("user" + seed + "@mail.c");
        user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
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
        Call c = new Call();
        LocalDateTime instant = LocalDateTime.now();
        c.setTitle("Titulo Titulo");
        c.setDescription("Descricao Descricao Descricao");
        c.setDateTime(instant);
        c.setUser(savedUser);
        c.setFloor(savedFloor);
        c.setPriority(CallPriority.High);
        c.setActive(true);
        c.setJustification("Justificativa Justificativa");

        Call savedCall = callRepo.save(c);

        // Criar e salvar comentário da chamada
        CallCommentRequestDTO callCommentRequestDTO = new CallCommentRequestDTO(savedUser.getId(), savedCall.getId(), instant, "Mensagem Mensagem Mensagem", false);
        CallComment savedCallComment = callCommentService.save(callCommentRequestDTO);

        // Adicionar comentário à chamada e salvar
        savedCall.setComments(new ArrayList<>(List.of(savedCallComment)));

        // Finalizar a transação atual para garantir que os dados estejam persistidos
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        // Enviar a requisição para atualizar o status de visualização do comentário com um ID inválido
        Long invalidCallId = 1000000L; // ID inválido
        HttpEntity<Void> entity = new HttpEntity<>(getAuthorizationTokenHeader());
        ResponseEntity<ExceptionDetails> responseEntity = restTemplate.exchange(
                getRootUrl() + CALL_COMMENT_URL + "/call/" + invalidCallId + "/messages/viewed",
                HttpMethod.PUT, entity, ExceptionDetails.class
        );

        // Verificações da resposta
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(NOT_FOUND.value(), responseEntity.getBody().getStatus());

        // Verificações do comentário original
        CallComment returnedCallComment = this.callCommentService.getById(savedCallComment.getId());

        Assertions.assertNotNull(returnedCallComment, "the returned call comment must be not null when throw exception");
        Assertions.assertEquals(savedCallComment.getId(), returnedCallComment.getId(), "the returned call comment id must be the same as the saved call comment when throw exception");
        Assertions.assertEquals(callCommentRequestDTO.message(), returnedCallComment.getMessage(), "the returned call comment message must be the same as the saved call comment when throw exception");
        Assertions.assertFalse(returnedCallComment.getViewed(), "the returned call comment viewed status must be false when throw exception");
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