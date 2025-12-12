
// package br.edu.lampi.infrareport;

// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.springframework.http.HttpStatus.BAD_REQUEST;
// import static org.springframework.http.HttpStatus.CREATED;
// import static org.springframework.http.HttpStatus.NO_CONTENT;
// import static org.springframework.http.HttpStatus.OK;

// import java.net.URISyntaxException;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Random;
// import java.util.Set;

// import br.edu.lampi.infrareport.controller.dto.call.*;
// import br.edu.lampi.infrareport.controller.dto.callImage.CallImageResponseDTO;

// import br.edu.lampi.infrareport.model.building.Building;
// import br.edu.lampi.infrareport.model.floor.Floor;
// import br.edu.lampi.infrareport.service.*;
// import org.json.JSONArray;
// import org.json.JSONException;
// import org.json.JSONObject;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInstance;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.http.*;
// import org.springframework.test.annotation.DirtiesContext;
// import org.springframework.test.context.transaction.TestTransaction;

// import br.edu.lampi.infrareport.service.exceptions.BadRequestException;
// import br.edu.lampi.infrareport.controller.dto.callComment.CallCommentRequestDTO;
// import br.edu.lampi.infrareport.controller.dto.callImage.CallImageRequestDTO;
// import br.edu.lampi.infrareport.controller.dto.callStatus.CallStatusRequestDTO;
// import br.edu.lampi.infrareport.controller.dto.category.CategoryRequestDto;
// import br.edu.lampi.infrareport.controller.dto.team.TeamNoIdDTO;
// import br.edu.lampi.infrareport.controller.dto.user.LoginRequestDTO;
// import br.edu.lampi.infrareport.controller.dto.user.LoginResponseDTO;
// import br.edu.lampi.infrareport.controller.dto.user.UserRequestDTO;
// import br.edu.lampi.infrareport.model.call.Call;
// import br.edu.lampi.infrareport.model.call.CallPriority;
// import br.edu.lampi.infrareport.model.callComment.CallComment;
// import br.edu.lampi.infrareport.model.callImage.CallImage;
// import br.edu.lampi.infrareport.model.callstatus.CallStatus;
// import br.edu.lampi.infrareport.model.category.Category;
// import br.edu.lampi.infrareport.model.team.Team;
// import br.edu.lampi.infrareport.model.user.User;
// import br.edu.lampi.infrareport.model.user.UserType;
// import br.edu.lampi.infrareport.repository.CallRepository;
// import br.edu.lampi.infrareport.repository.CallStatusRepository;
// import br.edu.lampi.infrareport.repository.TeamRepository;
// import br.edu.lampi.infrareport.service.CallCommentService;
// import br.edu.lampi.infrareport.service.CallImageService;
// import br.edu.lampi.infrareport.service.CallService;
// import br.edu.lampi.infrareport.service.CategoryService;
// import br.edu.lampi.infrareport.service.UserService;
// import jakarta.transaction.Transactional;

// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// public class CallFullTests extends InfrareportApplicationTests {
//     @Autowired
//     CallRepository callRepository;

//     @Autowired
//     CallStatusRepository callStatusRepository;


//     @Autowired
//     CallService callService;

//     @Autowired
//     UserService userService;

//     @Autowired
//     CategoryService categoryService;

//     @Autowired
//     BuildingService buildingService;

//     @Autowired
//     FloorService floorService;

//     @Autowired
//     CallCommentService callCommentService;

//     @Autowired
//     CallImageService callImageService;

//     @Autowired
//     TeamRepository teamRepository;

//     private final String CALL_URL = "/calls";

//     /**
//      * Teste para verificar Model de Call
//      */
//     @Test
//     @Transactional
//     public void model_instantiateCallAndCheckIfValid() {
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         Category category = new Category();
//         category.setName("Categoria");
//         Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call c = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         c.setId(11L);
//         c.setTitle("Titulo Titulo");
//         c.setDescription("Descricao Descricao Descricao");
//         c.setDateTime(instant);
//         c.setUser(savedUser);
//         c.setFloor(savedFloor);
//         c.setCategory(savedCategory);
//         c.setCallStatus(new CallStatus(44L, "Status Status"));
//         c.setTeam(new Team(55L, "Team Team"));
//         c.setPriority(CallPriority.High);
//         c.setActive(true);
//         c.setJustification("Justificativa Justificativa");

//         final String expected = "Call(id=11, title=Titulo Titulo, description=Descricao Descricao Descricao, dateTime="
//                 + instant
//                 + ", user=" + savedUser.toString()
//                 + ", category=" + savedCategory.toString()
//                 + ", floor=Floor(id=" + savedFloor.getId() + ", name=" + savedFloor.getName() + ", building=Building(id=" + savedBuilding.getId() + ", name=" + savedBuilding.getName() + ", floors=null))"
//                 + ", callStatus=CallStatus(id=44, callStatusName=Status Status)"
//                 + ", team=Team(id=55, name=Team Team)"
//                 + ", images=[]"
//                 + ", comments=[]"
//                 + ", priority=High"
//                 + ", active=true"
//                 + ", justification=Justificativa Justificativa)";
//         Assertions.assertEquals(expected, c.toString());
//     }

//     /**
//      * Teste para verificar custom query em Repository de Call
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void repository_exists_saveCallAndCallStatusCheckIfExistsByCallStatusId() {
//         // Configura o CallStatus
//         CallStatus callStatus = new CallStatus();
//         callStatus.setCallStatusName("Resolvido");
//         CallStatus savedCallStatus = callStatusRepository.save(callStatus);

//         // Verifica se a chamada existe antes de salvar
//         boolean existsBefore = callRepository.existsByCallStatusId(savedCallStatus.getId());

//         // Configura o usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(instant);
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);
//         call.setCallStatus(savedCallStatus);

//         // Salva a chamada
//         Call savedCall = callRepository.save(call);

//         // Verifica se a chamada existe após salvar
//         boolean existsAfter = callRepository.existsByCallStatusId(savedCallStatus.getId());

//         // Verificações
//         Assertions.assertNotNull(savedCall);
//         Assertions.assertNotNull(savedCall.getId());
//         Assertions.assertNotEquals("0", savedCall.getId());
//         Assertions.assertFalse(existsBefore);
//         Assertions.assertTrue(existsAfter);
//     }

//     /**
//      * Teste para verificar save em Service de Call
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_save_saveCallAndCheckIfValid() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(instant);
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);

//         // Cria o DTO da chamada
//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(
//                 call.getTitle(),
//                 call.getDescription(),
//                 call.getDateTime(),
//                 call.getUser().getId(),
//                 call.getFloor().getId()
//         );

//         // Salva a chamada
//         Call savedCall = callService.save(callUserRequestDTO);

//         // Verificações
//         Assertions.assertNotNull(savedCall);
//         Assertions.assertNotNull(savedCall.getId());
//         Assertions.assertNotEquals("0", savedCall.getId());
//         Assertions.assertEquals(call.getDateTime(), savedCall.getDateTime());
//         Assertions.assertEquals(call.getTitle(), savedCall.getTitle());
//         Assertions.assertEquals(call.getDescription(), savedCall.getDescription());
//     }

//     /**
//      * Teste para verificar get em Service de Call
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_getById_saveCallAndGetCreatedCallByIdThenVerifyIfEqualCreated() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(instant);
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);

//         // Cria o DTO da chamada
//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(
//                 call.getTitle(),
//                 call.getDescription(),
//                 call.getDateTime(),
//                 call.getUser().getId(),
//                 call.getFloor().getId()
//         );

//         // Salva a chamada
//         Call savedCall = callService.save(callUserRequestDTO);
//         Long savedCallId = savedCall.getId();

//         // Recupera a chamada pelo ID
//         Call gotCall = callService.getById(savedCallId);

//         // Verificações
//         Assertions.assertNotNull(gotCall);
//         Assertions.assertNotNull(gotCall.getId());
//         Assertions.assertNotEquals("0", gotCall.getId());
//         Assertions.assertEquals(savedCall.getTitle(), gotCall.getTitle());
//         Assertions.assertEquals(savedCall.getDescription(), gotCall.getDescription());
//         Assertions.assertEquals(savedCall.getDateTime(), gotCall.getDateTime());
//         Assertions.assertEquals(savedCall.getFloor().getId(), gotCall.getFloor().getId());
//     }

//     /**
//      * Teste para verificar get all em Service de Call
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_getAll_saveCallAndGetAllThenVerifyIfCreatedIsInList() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(instant);
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);

//         // Cria o DTO da chamada
//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(
//                 call.getTitle(),
//                 call.getDescription(),
//                 call.getDateTime(),
//                 call.getUser().getId(),
//                 call.getFloor().getId()
//         );

//         // Salva a chamada
//         Call savedCall = callService.save(callUserRequestDTO);

//         // Recupera todas as chamadas
//         List<Call> gotCalls = callService.getAll();

//         // Verificações
//         Assertions.assertNotNull(gotCalls);
//         Assertions.assertNotEquals(0, gotCalls.size());
//         Assertions.assertTrue(gotCalls.contains(savedCall));
//     }


//     /**
//      * Teste para verificar delete and exists em Service de Call
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_deleteAndExists_saveCallAndDeleteCreatedCallByIdThenVerifyIfExists() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(instant);
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);

//         // Cria o DTO da chamada
//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(
//                 call.getTitle(),
//                 call.getDescription(),
//                 call.getDateTime(),
//                 call.getUser().getId(),
//                 call.getFloor().getId()
//         );

//         // Salva a chamada
//         Call savedCall = callService.save(callUserRequestDTO);
//         Long savedCallId = savedCall.getId();

//         // Verifica se a chamada existe antes de deletar
//         boolean existsBefore = callService.existsById(savedCallId);

//         // Deleta a chamada pelo ID
//         callService.deleteById(savedCallId);

//         // Verifica se a chamada existe após deletar
//         boolean existsAfter = callService.existsById(savedCallId);

//         // Verificações
//         Assertions.assertTrue(existsBefore);
//         Assertions.assertFalse(existsAfter);
//     }

//     /**
//      * Teste para verificar se delete em Service de Call gera a exception corretamente.
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_deleteAndError_saveCallAndCommentThenDeleteCreatedCallByIdThenVerifyException() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(instant);
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);

//         // Cria o DTO da chamada
//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(
//                 call.getTitle(),
//                 call.getDescription(),
//                 call.getDateTime(),
//                 call.getUser().getId(),
//                 call.getFloor().getId()
//         );

//         // Salva a chamada
//         Call savedCall = callService.save(callUserRequestDTO);
//         Long savedCallId = savedCall.getId();

//         // Configura e salva o comentário
//         CallComment comment = new CallComment();
//         comment.setUser(savedUser);
//         comment.setMessage("Mensagem Mensagem Mensagem");
//         comment.setDateTime(instant);
//         comment.setViewed(false);
//         comment.setCall(savedCall);
//         callCommentService.save(new CallCommentRequestDTO(
//                 comment.getUser().getId(),
//                 comment.getCall().getId(),
//                 comment.getDateTime(),
//                 comment.getMessage(),
//                 comment.getViewed()
//         ));

//         // Verifica se a exceção é lançada ao tentar deletar a chamada
//         Assertions.assertThrows(BadRequestException.class, () -> callService.deleteById(savedCallId));
//     }


//     /**
//      * Teste para verificar se delete em Service de Call gera a exception corretamente.
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_deleteAndError_saveCallAndImageThenDeleteCreatedCallByIdThenVerifyException() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(instant);
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);

//         // Cria o DTO da chamada
//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(
//                 call.getTitle(),
//                 call.getDescription(),
//                 call.getDateTime(),
//                 call.getUser().getId(),
//                 call.getFloor().getId()
//         );

//         // Salva a chamada
//         Call savedCall = callService.save(callUserRequestDTO);
//         Long savedCallId = savedCall.getId();

//         // Configura e salva a imagem
//         CallImage image = new CallImage();
//         image.setImagePath("path path path");
//         image.setCall(savedCall);
//         callImageService.save(new CallImageRequestDTO(
//                 image.getCall().getId(),
//                 image.getImagePath()
//         ));

//         // Verifica se a exceção é lançada ao tentar deletar a chamada
//         Assertions.assertThrows(BadRequestException.class, () -> callService.deleteById(savedCallId));
//     }


//     /**
//      * Teste para verificar update em Service de Call
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_updateById_saveCallAndUpdateCallByIdThenVerifyIfValid() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(instant);
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);

//         // Cria o DTO da chamada
//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(
//                 call.getTitle(),
//                 call.getDescription(),
//                 call.getDateTime(),
//                 call.getUser().getId(),
//                 call.getFloor().getId()
//         );

//         // Salva a chamada
//         Call savedCall = callService.save(callUserRequestDTO);
//         Long savedCallId = savedCall.getId();

//         // Novos dados para atualização
//         String newTitle = "Titulo Titulo Titulo";
//         String newDescription = "Descricao Descricao Descricao Descricao";
//         LocalDateTime newInstant = LocalDateTime.now().plusHours(1);
//         User newUser = new User();
//         newUser.setName("Usuario Usuario Usuario");
//         newUser.setEmail("user2@mail.c");
//         newUser.setUserType(List.of(UserType.ROLE_COMMON));
//         newUser.setPassword("passpasspass");
//         User savedNewUser = userService.save(new UserRequestDTO(newUser));

//         // Configura novo floor
//         Floor newFloor = new Floor();
//         newFloor.setName("Second Floor");
//         newFloor.setBuilding(savedBuilding);
//         Floor savedNewFloor = floorService.saveNewFloor(newFloor);

//         // Cria o DTO de atualização da chamada
//         CallUserRequestDTO updateCall = new CallUserRequestDTO(
//                 newTitle,
//                 newDescription,
//                 newInstant,
//                 savedNewUser.getId(),
//                 savedNewFloor.getId()
//         );

//         // Atualiza a chamada pelo ID
//         callService.updateById(savedCallId, updateCall);

//         // Recupera a chamada atualizada pelo ID
//         Call updatedCall = callService.getById(savedCallId);

//         // Verificações
//         Assertions.assertNotNull(updatedCall);
//         Assertions.assertEquals(savedCallId, updatedCall.getId());
//         Assertions.assertEquals(newTitle, updatedCall.getTitle());
//         Assertions.assertEquals(newDescription, updatedCall.getDescription());
//         Assertions.assertEquals(newInstant, updatedCall.getDateTime());
//         Assertions.assertEquals(savedNewUser, updatedCall.getUser());
//         Assertions.assertEquals(savedNewFloor, updatedCall.getFloor());
//     }

//     /**
//      * Teste para verificar searchFilter em Service de Call
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_searchFilter_saveCallAndSearchByFilterThenVerifyIfCreatedIsInList() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura callStatus, team e category
//         CallStatus callStatus = callStatusRepository.save(new CallStatus(new CallStatusRequestDTO("call status 1")));
//         Team team = teamRepository.save(new Team(new TeamNoIdDTO("team 1")));
//         Category category = categoryService.save(new CategoryRequestDto("category 1"));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(LocalDateTime.of(2021, 1, 4, 1, 8, 45, 97000000));
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);
//         call.setActive(true);
//         call.setCallStatus(callStatus);
//         call.setTeam(team);
//         call.setPriority(CallPriority.Urgent);
//         call.setCategory(category);

//         // Salva a chamada
//         call = callRepository.save(call);

//         // Cria o filtro da chamada
//         CallFilterDTO callFilter = new CallFilterDTO(
//                 LocalDateTime.of(2021, 1, 4, 1, 8, 45, 97000000),
//                 LocalDateTime.of(2024, 5, 4, 1, 8, 45, 97000000),
//                 Set.of(team.getId()),
//                 Set.of(category.getId()),
//                 Set.of(callStatus.getId()),
//                 Set.of(savedFloor.getId()),
//                 Set.of(call.getPriority()),
//                 true,
//                 Set.of(savedUser.getId()),
//                 true
//         );

//         // Busca as chamadas pelo filtro
//         List<Call> gotCalls = callService.searchFilter(callFilter, PageRequest.of(0, 10, Sort.Direction.ASC, "id")).toList();

//         // Verificações
//         Assertions.assertNotNull(gotCalls);
//         Assertions.assertFalse(gotCalls.isEmpty());
//         Assertions.assertEquals(call.toString(), gotCalls.get(0).toString());
//     }

//         /**
//      * Teste para verificar searchFilter em Service de Call: verificar se está retornando calls não classificadas
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_searchFilter_saveCallAndSearchByFilterUnClassifiedCallsThenVerifyIfCreatedIsInList() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura callStatus, team e category
//         Team team = teamRepository.save(new Team(new TeamNoIdDTO("team 1")));
//         Category category = categoryService.save(new CategoryRequestDto("category 1"));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(LocalDateTime.of(2021, 1, 4, 1, 8, 45, 97000000));
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);
//         call.setActive(true);
//         call.setTeam(team);
//         call.setPriority(CallPriority.Urgent);
//         call.setCategory(category);

//         // Salva a chamada
//         call = callRepository.save(call);

//         // Cria o filtro da chamada
//         CallFilterDTO callFilter = new CallFilterDTO(
//                 LocalDateTime.of(2021, 1, 4, 1, 8, 45, 97000000),
//                 LocalDateTime.of(2024, 5, 4, 1, 8, 45, 97000000),
//                 Set.of(team.getId()),
//                 Set.of(category.getId()),
//                 Set.of(),
//                 Set.of(savedFloor.getId()),
//                 Set.of(call.getPriority()),
//                 true,
//                 Set.of(savedUser.getId()),
//                 false
//         );

//         // Busca as chamadas pelo filtro
//         List<Call> gotCalls = callService.searchFilter(callFilter, PageRequest.of(0, 10, Sort.Direction.ASC, "id")).toList();

//         // Verificações
//         Assertions.assertNotNull(gotCalls);
//         Assertions.assertFalse(gotCalls.isEmpty());
//         Assertions.assertEquals(call.toString(), gotCalls.get(0).toString());
//     }

//     /**
//      * Teste para verificar generateCSV em Service de Call
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_generateCSV_saveCallAndGenerateCsvFileThenVerifyIfSavedCallIsInCsvFile() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura callStatus, team e category
//         CallStatus callStatus = callStatusRepository.save(new CallStatus(new CallStatusRequestDTO("call status 1")));
//         Team team = teamRepository.save(new Team(new TeamNoIdDTO("team 101")));
//         Category category = categoryService.save(new CategoryRequestDto("category 101"));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(LocalDateTime.of(2021, 1, 4, 1, 8, 45, 97000000));
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);
//         call.setActive(true);
//         call.setCallStatus(callStatus);
//         call.setTeam(team);
//         call.setPriority(CallPriority.Urgent);
//         call.setCategory(category);

//         // Salva a chamada
//         call = callRepository.save(call);

//         // Cria o filtro da chamada
//         CallFilterDTO callFilter = new CallFilterDTO(
//                 LocalDateTime.of(2021, 1, 4, 1, 8, 45, 97000000),
//                 LocalDateTime.of(2024, 5, 4, 1, 8, 45, 97000000),
//                 Set.of(team.getId()),
//                 Set.of(category.getId()),
//                 Set.of(callStatus.getId()),
//                 Set.of(savedFloor.getId()),
//                 Set.of(call.getPriority()),
//                 true,
//                 Set.of(savedUser.getId()),
//                 null
//         );

//         // Gera o CSV
//         byte[] csvBytes = callService.generateCSV(callFilter);

//         // Verificações
//         Assertions.assertNotNull(csvBytes);

//         String stringCsv = new String(csvBytes);

//         Assertions.assertTrue(stringCsv.contains(call.getId().toString()));
//         Assertions.assertTrue(stringCsv.contains(call.getTitle()));
//         Assertions.assertTrue(stringCsv.contains(call.getDescription()));
//         Assertions.assertTrue(stringCsv.contains(call.getDateTime().toString()));
//         Assertions.assertTrue(stringCsv.contains(savedFloor.getName()));
//         Assertions.assertTrue(stringCsv.contains(call.getPriority().name()));
//         Assertions.assertTrue(stringCsv.contains(call.getActive().toString()));
//         Assertions.assertTrue(stringCsv.contains(call.getTeam().getName()));
//         Assertions.assertTrue(stringCsv.contains(call.getCategory().getName()));
//         Assertions.assertTrue(stringCsv.contains(call.getCallStatus().getCallStatusName()));
//         Assertions.assertTrue(stringCsv.contains(call.getUser().getEmail()));
//     }

//     /**
//      * Teste para verificar se deactivate em Service de Call gera a exception corretamente.
//      */
//     @Test
//     @Transactional
//     public void service_deactivateCall_deactivateExistingCallAndVerify() {
//         int seed = new Random().nextInt();
//         User user = userService.save(new UserRequestDTO("Usuario Usuario", "user" + seed + "@mail.com", "passpass"));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Cria e salva a chamada
//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(
//                 "Call Title",
//                 "Description of the Call",
//                 LocalDateTime.now(),
//                 user.getId(),
//                 savedFloor.getId()
//         );
//         Call savedCall = callService.save(callUserRequestDTO);

//         // Desativa a chamada com justificativa
//         String justification = "Temporary closure";
//         callService.deactivateById(savedCall.getId(), justification);

//         // Recupera a chamada desativada
//         Call deactivatedCall = callService.getById(savedCall.getId());

//         // Verificações
//         Assertions.assertNotNull(deactivatedCall);
//         Assertions.assertFalse(deactivatedCall.getActive());
//         Assertions.assertEquals(justification, deactivatedCall.getJustification());
//     }


//     /**
//      * Teste para verificar classifyCall em service de Call
//      */
//     @SuppressWarnings("null")
//     @Test
//     public void service_classifyCall_saveCallAndClassifyCallThenVerifyIfCallWasClassifiedCorrectly() throws JSONException {
//         // Criando usuário admin
//         UserRequestDTO userRequest = new UserRequestDTO("Zoro", "zoro1234@gmail.com", "12345");
//         User savedUser = userService.saveAdmin(userRequest);

//         // Configura callStatus, team e category
//         CallStatus callStatus = callStatusRepository.save(new CallStatus(new CallStatusRequestDTO("call status 4")));
//         Team team = teamRepository.save(new Team(new TeamNoIdDTO("team 4")));
//         Category category = categoryService.save(new CategoryRequestDto("category 4"));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         call.setTitle("Titulo");
//         call.setDescription("Descricao");
//         call.setDateTime(LocalDateTime.of(2022, 1, 4, 1, 8, 45, 97000000));
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);
//         call.setActive(false);

//         // Salva a chamada
//         call = callRepository.save(call);

//         // Classifica a chamada
//         ClassifyCallDTO classifyCallDTO = new ClassifyCallDTO(team.getId(), category.getId(), callStatus.getId(), CallPriority.Urgent);
//         this.callService.classifyCall(call.getId(), classifyCallDTO);

//         // Recupera a chamada classificada
//         Call classifiedCall = this.callService.getById(call.getId());

//         // Verificações
//         Assertions.assertNotNull(classifiedCall);
//         Assertions.assertEquals(call.getId(), classifiedCall.getId());
//         Assertions.assertEquals(call.getDescription(), classifiedCall.getDescription());
//         Assertions.assertEquals(savedFloor.getId(), classifiedCall.getFloor().getId());
//         Assertions.assertEquals(call.getTitle(), classifiedCall.getTitle());
//         Assertions.assertEquals(call.getDateTime(), classifiedCall.getDateTime());
//         Assertions.assertEquals(call.getUser().getId(), classifiedCall.getUser().getId());
//         Assertions.assertEquals(category.getId(), classifiedCall.getCategory().getId());
//         Assertions.assertEquals(category.getName(), classifiedCall.getCategory().getName());
//         Assertions.assertEquals(team.getId(), classifiedCall.getTeam().getId());
//         Assertions.assertEquals(team.getName(), classifiedCall.getTeam().getName());
//         Assertions.assertEquals(callStatus.getId(), classifiedCall.getCallStatus().getId());
//         Assertions.assertEquals(callStatus.getCallStatusName(), classifiedCall.getCallStatus().getCallStatusName());
//         Assertions.assertEquals(CallPriority.Urgent, classifiedCall.getPriority());
//     }

//     @Test
//     @Transactional
//     public void service_searchCallsByUserId_saveCallsAndSearchByUserId() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Cria e salva a primeira chamada
//         Call call = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(instant);
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(
//                 call.getTitle(),
//                 call.getDescription(),
//                 call.getDateTime(),
//                 call.getUser().getId(),
//                 call.getFloor().getId()
//         );

//         Call savedCall1 = callService.save(callUserRequestDTO);

//         // Cria e salva a segunda chamada
//         call = new Call();
//         instant = LocalDateTime.now();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(instant);
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);

//         callUserRequestDTO = new CallUserRequestDTO(
//                 call.getTitle(),
//                 call.getDescription(),
//                 call.getDateTime(),
//                 call.getUser().getId(),
//                 call.getFloor().getId()
//         );

//         Call savedCall2 = callService.save(callUserRequestDTO);

//         // Adiciona comentários às chamadas
//         CallComment comment = new CallComment();
//         comment.setUser(savedUser);
//         comment.setCall(savedCall1);
//         comment.setMessage("Mensagem Mensagem Mensagem");
//         comment.setDateTime(instant);
//         comment.setViewed(false);

//         CallCommentRequestDTO callCommentRequestDTO = new CallCommentRequestDTO(
//                 comment.getUser().getId(),
//                 comment.getCall().getId(),
//                 comment.getDateTime(),
//                 comment.getMessage(),
//                 comment.getViewed()
//         );
//         CallComment savedCallComment1 = callCommentService.save(callCommentRequestDTO);

//         comment = new CallComment();
//         comment.setUser(savedUser);
//         comment.setCall(savedCall2);
//         comment.setMessage("Mensagem Mensagem Mensagem");
//         comment.setDateTime(instant);
//         comment.setViewed(false);

//         callCommentRequestDTO = new CallCommentRequestDTO(
//                 comment.getUser().getId(),
//                 comment.getCall().getId(),
//                 comment.getDateTime(),
//                 comment.getMessage(),
//                 comment.getViewed()
//         );
//         CallComment savedCallComment2 = callCommentService.save(callCommentRequestDTO);

//         comment = new CallComment();
//         comment.setUser(savedUser);
//         comment.setCall(savedCall2);
//         comment.setMessage("Mensagem Mensagem Mensagem");
//         comment.setDateTime(instant);
//         comment.setViewed(true);

//         callCommentRequestDTO = new CallCommentRequestDTO(
//                 comment.getUser().getId(),
//                 comment.getCall().getId(),
//                 comment.getDateTime(),
//                 comment.getMessage(),
//                 comment.getViewed()
//         );
//         CallComment savedCallComment3 = callCommentService.save(callCommentRequestDTO);

//         // Adiciona imagem à primeira chamada
//         CallImage image = new CallImage();
//         image.setImagePath("path path path");
//         image.setCall(savedCall1);

//         CallImageRequestDTO callImageRequestDTO = new CallImageRequestDTO(
//                 image.getCall().getId(),
//                 image.getImagePath()
//         );
//         CallImage imageSaved = callImageService.save(callImageRequestDTO);

//         savedCall1.setComments(List.of(savedCallComment1));
//         savedCall2.setComments(List.of(savedCallComment2, savedCallComment3));
//         savedCall1.setImages(List.of(imageSaved));

//         // Busca as chamadas por ID do usuário
//         Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
//         List<CallByUserResponseDTO> listCalls = callService.searchCallsByUserId(savedUser.getId(), pageable).map(CallByUserResponseDTO::new).toList();

//         // Verificações
//         Assertions.assertEquals(listCalls.get(0).id(), savedCall1.getId());
//         Assertions.assertNull(listCalls.get(0).callStatus());
//         Assertions.assertEquals(listCalls.get(0).dateTime(), savedCall1.getDateTime());
//         Assertions.assertEquals(listCalls.get(0).description(), savedCall1.getDescription());
//         Assertions.assertEquals(listCalls.get(0).title(), savedCall1.getTitle());
//         Assertions.assertEquals(listCalls.get(0).active(), savedCall1.getActive());
//         Assertions.assertEquals(listCalls.get(0).callImages(), new CallImageResponseDTO(imageSaved));
//         Assertions.assertEquals(listCalls.get(0).justification(), savedCall1.getJustification());
//         Assertions.assertEquals(listCalls.get(0).numberUnseenComments(), 1);
//         Assertions.assertEquals(listCalls.get(1).id(), savedCall2.getId());
//         Assertions.assertNull(listCalls.get(1).callStatus());
//         Assertions.assertEquals(listCalls.get(1).dateTime(), savedCall2.getDateTime());
//         Assertions.assertEquals(listCalls.get(1).description(), savedCall2.getDescription());
//         Assertions.assertEquals(listCalls.get(1).title(), savedCall2.getTitle());
//         Assertions.assertEquals(listCalls.get(1).active(), savedCall2.getActive());
//         Assertions.assertNull(listCalls.get(1).callImages());
//         Assertions.assertEquals(listCalls.get(1).justification(), savedCall2.getJustification());
//         Assertions.assertEquals(listCalls.get(1).numberUnseenComments(), 1);
//     }

//     /**
//      * Teste de requisição ao Controller para verificar se consegue criar um novo Call
//      */
//     @SuppressWarnings("null")
//     @Test
//     public void mockRestController_save_saveCallAndVerifyIfPostResponseIsValid() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(instant);
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);

//         // Cria o DTO da chamada
//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(
//                 call.getTitle(),
//                 call.getDescription(),
//                 call.getDateTime(),
//                 call.getUser().getId(),
//                 call.getFloor().getId()
//         );

//         // Cria a entidade HTTP
//         HttpEntity<CallUserRequestDTO> entity = new HttpEntity<>(callUserRequestDTO, getAuthorizationTokenHeader());

//         // Envia a requisição POST
//         ResponseEntity<CallUserResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_URL, entity, CallUserResponseDTO.class);

//         // Verificações
//         Assertions.assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
//         Assertions.assertNotNull(postResponse);
//         Assertions.assertNotEquals(0, postResponse.getBody().id());
//         Assertions.assertEquals(callUserRequestDTO.title(), postResponse.getBody().title());
//     }

//     /**
//      * Teste de requisição ao Controller para verificar se falha um novo Call incompleto
//      */
//     @SuppressWarnings("null")
//     @Test
//     public void mockRestController_save_saveCallWithNullTitleAndVerifyIfError() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call) sem título
//         Call call = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(instant);
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);

//         // Cria o DTO da chamada com título nulo
//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(
//                 null, // Título nulo
//                 call.getDescription(),
//                 call.getDateTime(),
//                 call.getUser().getId(),
//                 call.getFloor().getId()
//         );

//         // Cria a entidade HTTP
//         HttpEntity<CallUserRequestDTO> entity = new HttpEntity<>(callUserRequestDTO, getAuthorizationTokenHeader());

//         // Envia a requisição POST
//         ResponseEntity<CallUserResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_URL, entity, CallUserResponseDTO.class);

//         // Verificações
//         Assertions.assertEquals(HttpStatus.BAD_REQUEST, postResponse.getStatusCode());
//     }


//     /**
//      * Teste de requisição ao controller para verificar se consegue obter um call que acabou de criar
//      */
//     @SuppressWarnings("null")
//     @Test
//     public void mockRestController_searchById_createCallAndSearchByIdThenVerifyIfEqualCreated() {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         call.setTitle("Titulo Titulo");
//         call.setDescription("Descricao Descricao Descricao");
//         call.setDateTime(instant);
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);

//         // Cria o DTO da chamada
//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(
//                 call.getTitle(),
//                 call.getDescription(),
//                 call.getDateTime(),
//                 call.getUser().getId(),
//                 call.getFloor().getId()
//         );

//         // Cria a entidade HTTP
//         HttpEntity<CallUserRequestDTO> entity = new HttpEntity<>(callUserRequestDTO, getAuthorizationTokenHeader());

//         // Envia a requisição POST para criar a chamada
//         ResponseEntity<CallUserResponseDTO> postResponse = restTemplate.postForEntity(getRootUrl() + CALL_URL, entity, CallUserResponseDTO.class);
//         long id = postResponse.getBody().id();

//         // Envia a requisição GET para buscar a chamada pelo ID
//         ResponseEntity<CallUserResponseDTO> returnedCall = restTemplate.exchange(getRootUrl() + CALL_URL + "/" + id, HttpMethod.GET, entity, CallUserResponseDTO.class);

//         // Verificações
//         Assertions.assertNotNull(returnedCall);
//         Assertions.assertEquals(callUserRequestDTO.title(), returnedCall.getBody().title());
//     }


//     /**
//      * Teste para verificar searchFilter em controller de Call
//      */
//     @SuppressWarnings("null")
//     @Test
//     public void mockRestController_searchFilter_saveCallAndSearchByFilterThenVerifyIfCreatedIsInList() throws JSONException {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura callStatus, team e category
//         CallStatus callStatus = callStatusRepository.save(new CallStatus(new CallStatusRequestDTO("call status 2")));
//         Team team = teamRepository.save(new Team(new TeamNoIdDTO("team 2")));
//         Category category = categoryService.save(new CategoryRequestDto("category 2"));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         call.setTitle("Titulo");
//         call.setDescription("Descricao");
//         call.setDateTime(LocalDateTime.of(2022, 1, 4, 1, 8, 45, 97000000));
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);
//         call.setActive(false);
//         call.setCallStatus(callStatus);
//         call.setTeam(team);
//         call.setPriority(CallPriority.Urgent);
//         call.setCategory(category);

//         // Salva a chamada
//         callRepository.save(call);

//         // Cria o filtro da chamada
//         CallFilterDTO callFilter = new CallFilterDTO(
//                 LocalDateTime.of(2021, 1, 4, 1, 8, 45, 97000000),
//                 LocalDateTime.of(2024, 5, 4, 1, 8, 45, 97000000),
//                 Set.of(team.getId()),
//                 Set.of(category.getId()),
//                 Set.of(callStatus.getId()),
//                 Set.of(savedFloor.getId()),
//                 Set.of(call.getPriority()),
//                 false,
//                 Set.of(savedUser.getId()),
//                 true
//         );

//         // Cria a entidade HTTP
//         HttpEntity<CallFilterDTO> requestEntity = new HttpEntity<>(callFilter, getAuthorizationTokenHeader());

//         // Envia a requisição POST para buscar as chamadas pelo filtro
//         ResponseEntity<String> response = restTemplate.exchange(
//                 getRootUrl() + CALL_URL + "/search_by_filter?sort=id,desc&page=0&size=10",
//                 HttpMethod.POST,
//                 requestEntity,
//                 String.class
//         );

//         // Verificações
//         Assertions.assertNotNull(response);
//         Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

//         JSONArray jsonArray = new JSONObject(response.getBody()).getJSONArray("content");

//         Assertions.assertNotNull(jsonArray);
//         Assertions.assertNotEquals(0, jsonArray.length());
//         Assertions.assertEquals(call.getId().toString(), jsonArray.getJSONObject(0).getString("id"));
//         Assertions.assertEquals(call.getDescription(), jsonArray.getJSONObject(0).getString("description"));
//         Assertions.assertEquals(savedFloor.getName(), jsonArray.getJSONObject(0).getString("floor"));
//         Assertions.assertEquals(call.getTitle(), jsonArray.getJSONObject(0).getString("title"));
//         Assertions.assertEquals(call.getDateTime().toString(), jsonArray.getJSONObject(0).getString("dateTime"));
//         Assertions.assertEquals(call.getUser().getName(), jsonArray.getJSONObject(0).getString("userName"));
//     }

//     /**
//      * Teste para verificar searchByIdAndReturnDetails no controller de Call
//      */
//     @Test
//     @Transactional
//     public void mockRestController_searchByIdAndReturnDetails_saveCallAndSearchByIdAndReturnDetails() throws URISyntaxException {
//         // Cria o admin
//         UserRequestDTO adminRequest = new UserRequestDTO("sanji", "sanji123@gmail.com", "12345");
//         User savedAdmin = userService.saveAdmin(adminRequest);
//         TestTransaction.flagForCommit();
//         TestTransaction.end();
//         TestTransaction.start();

//         // Realiza login com admin
//         LoginRequestDTO loginAdminRequest = new LoginRequestDTO(savedAdmin.getEmail(), "12345");
//         ResponseEntity<LoginResponseDTO> loginAdminResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginAdminRequest, LoginResponseDTO.class);

//         HttpHeaders adminHeaders = new HttpHeaders();
//         adminHeaders.setContentType(MediaType.APPLICATION_JSON);
//         adminHeaders.set("Authorization", "Bearer " + loginAdminResponse.getBody().token());

//         // Configura usuário comum
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura callStatus, team e category
//         CallStatus callStatus = callStatusRepository.save(new CallStatus(new CallStatusRequestDTO("call status 2")));
//         Team team = teamRepository.save(new Team(new TeamNoIdDTO("team 2")));
//         Category category = categoryService.save(new CategoryRequestDto("category 2"));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         call.setTitle("Titulo");
//         call.setDescription("Descricao");
//         call.setDateTime(LocalDateTime.of(2022, 1, 4, 1, 8, 45, 97000000));
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);
//         call.setActive(false);
//         call.setCallStatus(callStatus);
//         call.setTeam(team);
//         call.setPriority(CallPriority.Urgent);
//         call.setCategory(category);

//         Call savedCall = callRepository.save(call);

//         // Adiciona imagem e comentário à chamada
//         CallImage savedImage = callImageService.save(new CallImageRequestDTO(savedCall.getId(), "path path"));
//         CallComment savedComment = callCommentService.save(
//                 new CallCommentRequestDTO(
//                         savedUser.getId(),
//                         savedCall.getId(),
//                         LocalDateTime.of(2022, 1, 4, 1, 8, 45, 97000000),
//                         "message message",
//                         false
//                 ));
//         savedCall.setImages(new ArrayList<>(List.of(savedImage)));
//         savedCall.setComments(new ArrayList<>(List.of(savedComment)));
//         callRepository.save(savedCall);

//         TestTransaction.flagForCommit();
//         TestTransaction.end();
//         TestTransaction.start();

//         // Realiza a requisição GET
//         HttpEntity<Void> entity = new HttpEntity<>(null, adminHeaders);
//         ResponseEntity<CallUserWithDetailsResponseDTO> response = restTemplate.exchange(
//                 getRootUrl() + CALL_URL + "/details/" + savedCall.getId(),
//                 HttpMethod.GET, entity,
//                 CallUserWithDetailsResponseDTO.class
//         );

//         // Verificações
//         Assertions.assertNotNull(response);
//         Assertions.assertNotNull(response.getBody());
//         Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

//         Assertions.assertEquals(savedCall.getId(), response.getBody().callId());
//         Assertions.assertEquals(savedCall.getCallStatus().getId(), response.getBody().callStatusID());
//         Assertions.assertEquals(savedCall.getUser().getId(), response.getBody().userID());
//         Assertions.assertEquals(savedCall.getActive(), response.getBody().active());
//         Assertions.assertEquals(savedCall.getTeam().getId(), response.getBody().teamID());
//         Assertions.assertEquals(savedCall.getTitle(), response.getBody().title());
//         Assertions.assertEquals(savedCall.getFloor().getId(), response.getBody().floor().getId());
//         Assertions.assertEquals(savedCall.getCategory().getId(), response.getBody().categoryID());
//         Assertions.assertEquals(savedCall.getPriority(), response.getBody().callPriority());
//         Assertions.assertEquals(savedCall.getJustification(), response.getBody().justification());
//         Assertions.assertEquals(savedCall.getDateTime(), response.getBody().dateTime());
//         Assertions.assertEquals(savedCall.getImages().get(0).getId(), response.getBody().imagesID().get(0));
//         Assertions.assertEquals(savedCall.getComments().get(0).getId(), response.getBody().commentsID().get(0));
//     }

//         /**
//      * Teste para verificar searchByIdAndReturnDetails no controller de Call quando as entidades relacionadas a call estão nulas
//      */
//     @Test
//     public void mockRestController_searchByIdAndReturnDetails_saveCallAndSearchByIdAndReturnDetailsWithSomeDetailsNull() throws URISyntaxException {
//         /* Realizando login com admin: */
//         LoginRequestDTO loginAdminRequest = new LoginRequestDTO("admin@gmail.com", "123456");

//         ResponseEntity<LoginResponseDTO> loginAdminResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginAdminRequest, LoginResponseDTO.class);

//         assertNotNull(loginAdminResponse.getBody());

//         HttpHeaders adminHeaders = new HttpHeaders();
//         adminHeaders.setContentType(MediaType.APPLICATION_JSON);
//         adminHeaders.set("Authorization", "Bearer " + loginAdminResponse.getBody().token());

//         // Criar e salvar usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configurar building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Criar e salvar chamada com alguns detalhes nulos
//         Call call = new Call();
//         call.setTitle("Titulo");
//         call.setDescription("Descricao");
//         call.setDateTime(LocalDateTime.of(2022, 1, 4, 1, 8, 45, 97000000));
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);
//         call.setActive(false);
//         call.setPriority(CallPriority.Urgent);

//         Call savedCall = callRepository.save(call);

//         HttpEntity<Void> entity = new HttpEntity<Void>(null, adminHeaders);

//         ResponseEntity<CallUserWithDetailsResponseDTO> response = restTemplate.exchange(
//                 getRootUrl() + CALL_URL + "/details/" + savedCall.getId(),
//                 HttpMethod.GET, entity, CallUserWithDetailsResponseDTO.class);

//         Assertions.assertNotNull(response, "The response must not be null when request was successful");
//         Assertions.assertNotNull(response.getBody(), "The response body must not be null when request was successful");
//         Assertions.assertEquals(OK, response.getStatusCode(), "The response status code must be ok when request was successful");

//         Assertions.assertEquals(savedCall.getId(), response.getBody().callId(), "The response call id must be the same as the saved call");
//         Assertions.assertNull(response.getBody().callStatusID(), "The call status must be null when it is not defined.");
//         Assertions.assertEquals(savedCall.getUser().getId(), response.getBody().userID(), "The response call user id must be the same as the saved call user id");
//         Assertions.assertEquals(savedCall.getActive(), response.getBody().active(), "The response call active must be the same as the saved call");
//         Assertions.assertNull(response.getBody().teamID(), "The response call team id must be null when it is not defined.");
//         Assertions.assertEquals(savedCall.getTitle(), response.getBody().title(), "The response call title must be the same as the saved call");
//         Assertions.assertEquals(savedCall.getFloor().getId(), response.getBody().floor().getId(), "The response call floor must be the same as the saved call floor id");
//         Assertions.assertNull(response.getBody().categoryID(), "The response call category id must be null when it is not defined.");
//         Assertions.assertEquals(savedCall.getPriority(), response.getBody().callPriority(), "The response call priority must be the same as the saved call");
//         Assertions.assertNull(response.getBody().justification(), "The response call justification must be null when it is not defined.");
//         Assertions.assertEquals(savedCall.getDateTime(), response.getBody().dateTime(), "The response call datetime must be the same as the saved call datetime");
//         Assertions.assertTrue(response.getBody().imagesID().isEmpty(), "The response image list must be empty when there are no images associated with the call");
//         Assertions.assertTrue(response.getBody().commentsID().isEmpty(), "The response comment list must be empty when there are no comments associated with the call");
//     }

//     /**
//      * Teste para verificar generateCSV em controller de Call
//      */
//     @SuppressWarnings("null")
//     @Test
//     public void mockRestController_generateCSV_saveCallAndGenerateCsvFileThenVerifyIfSavedCallIsInCsvFile() throws JSONException {
//         // Configura usuário
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         // Configura callStatus, team e category
//         CallStatus callStatus = callStatusRepository.save(new CallStatus(new CallStatusRequestDTO("call status 2")));
//         Team team = teamRepository.save(new Team(new TeamNoIdDTO("team 102")));
//         Category category = categoryService.save(new CategoryRequestDto("category 102"));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         // Configura a chamada (Call)
//         Call call = new Call();
//         call.setTitle("Titulo");
//         call.setDescription("Descricao");
//         call.setDateTime(LocalDateTime.of(2022, 1, 4, 1, 8, 45, 97000000));
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);
//         call.setActive(false);
//         call.setCallStatus(callStatus);
//         call.setTeam(team);
//         call.setPriority(CallPriority.Urgent);
//         call.setCategory(category);

//         call = callRepository.save(call);

//         // Cria o filtro da chamada
//         CallFilterDTO callFilter = new CallFilterDTO(
//                 LocalDateTime.of(2021, 1, 4, 1, 8, 45, 97000000),
//                 LocalDateTime.of(2024, 5, 4, 1, 8, 45, 97000000),
//                 Set.of(team.getId()),
//                 Set.of(category.getId()),
//                 Set.of(callStatus.getId()),
//                 Set.of(savedFloor.getId()),
//                 Set.of(call.getPriority()),
//                 false,
//                 Set.of(savedUser.getId()),
//                 true
//         );

//         // Cria a entidade HTTP
//         HttpEntity<CallFilterDTO> requestEntity = new HttpEntity<>(callFilter, getAuthorizationTokenHeader());

//         // Envia a requisição POST para gerar o arquivo CSV
//         ResponseEntity<byte[]> response = restTemplate.exchange(
//                 getRootUrl() + CALL_URL + "/csv_file",
//                 HttpMethod.POST,
//                 requestEntity,
//                 byte[].class
//         );

//         // Verificações
//         Assertions.assertNotNull(response, "Response is null");
//         Assertions.assertNotNull(response.getBody(), "Response body is null");
//         Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP status is not OK");

//         byte[] csvBytes = response.getBody();
//         String stringCsv = new String(csvBytes);

//         Assertions.assertTrue(stringCsv.contains(call.getId().toString()), "CSV does not contain call ID");
//         Assertions.assertTrue(stringCsv.contains(call.getTitle()), "CSV does not contain call title");
//         Assertions.assertTrue(stringCsv.contains(call.getDescription()), "CSV does not contain call description");
//         Assertions.assertTrue(stringCsv.contains(call.getDateTime().toString()), "CSV does not contain call dateTime");
//         Assertions.assertTrue(stringCsv.contains(savedFloor.getName()), "CSV does not contain floor name");
//         Assertions.assertTrue(stringCsv.contains(call.getPriority().name()), "CSV does not contain call priority");
//         Assertions.assertTrue(stringCsv.contains(call.getActive().toString()), "CSV does not contain call active status");
//         Assertions.assertTrue(stringCsv.contains(call.getTeam().getName()), "CSV does not contain team name");
//         Assertions.assertTrue(stringCsv.contains(call.getCategory().getName()), "CSV does not contain category name");
//         Assertions.assertTrue(stringCsv.contains(call.getCallStatus().getCallStatusName()), "CSV does not contain call status name");
//         Assertions.assertTrue(stringCsv.contains(call.getUser().getEmail()), "CSV does not contain user email");
//     }


//     /**
//      * Test de requisição ao Controller para desativar um Call
//      */
//     @Test
//     public void mockRestController_deactivateCall_deactivateExistingCallAndVerify() {
//         User user = userService.save(new UserRequestDTO("Usuario Usuario", "user@mail.com", "passpass"));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call call = new Call();
//         call.setTitle("Call Title");
//         call.setDescription("Description of the Call");
//         call.setDateTime(LocalDateTime.now());
//         call.setUser(user);
//         call.setFloor(savedFloor);
//         call.setActive(true);

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(call.getTitle(), call.getDescription(), call.getDateTime(), call.getUser().getId(), call.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         String justification = "Temporary closure";
//         CallDeactivationDTO deactivationDTO = new CallDeactivationDTO(justification);

//         HttpEntity<CallDeactivationDTO> entity = new HttpEntity<>(deactivationDTO, getAuthorizationTokenHeader());
//         ResponseEntity<Void> response = restTemplate.exchange(getRootUrl() + CALL_URL + "/deactivate/" + savedCall.getId(), HttpMethod.POST, entity, Void.class);

//         Call deactivatedCall = callService.getById(savedCall.getId());

//         Assertions.assertNotNull(deactivatedCall);
//         Assertions.assertFalse(deactivatedCall.getActive());
//         Assertions.assertEquals(justification, deactivatedCall.getJustification());
//         Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//         Assertions.assertNull(response.getBody());
//     }

//     @Test
//     public void mockRestController_deactivateCallWithoutJustification_useDefaultJustificationAndVerify() {
//         int seed = new Random().nextInt();
//         User user = userService.save(new UserRequestDTO("Usuario Usuario", "user" + seed + "@mail.com", "passpass"));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call call = new Call();
//         call.setTitle("Call Title");
//         call.setDescription("Description of the Call");
//         call.setDateTime(LocalDateTime.now());
//         call.setUser(user);
//         call.setFloor(savedFloor);
//         call.setActive(true);

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(call.getTitle(), call.getDescription(), call.getDateTime(), call.getUser().getId(), call.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         HttpEntity<CallDeactivationDTO> entity = new HttpEntity<>(new CallDeactivationDTO(null), getAuthorizationTokenHeader());
//         ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + CALL_URL + "/deactivate/" + savedCall.getId(), HttpMethod.POST, entity, String.class);

//         Call deactivatedCall = callService.getById(savedCall.getId());

//         Assertions.assertNotNull(deactivatedCall);
//         Assertions.assertFalse(deactivatedCall.getActive());
//         Assertions.assertEquals("Chamada deletada pelo administrador", deactivatedCall.getJustification());
//         Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//         Assertions.assertNull(response.getBody());
//     }

//     /**
//     * Teste para verificar classifyCall em controller de Call
//     */
//     @SuppressWarnings("null")
//     @Test
//     public void mockRestController_classifyCall_saveCallAndClassifyCallThenVerifyIfCallWasClassifiedCorrectly() throws JSONException {
//         /* Criando usuário admin: */
//         UserRequestDTO userRequest = new UserRequestDTO("Zoro", "zoro2@gmail.com", "12345");
//         User savedUser = userService.saveAdmin(userRequest);

//         CallStatus callStatus = callStatusRepository.save(new CallStatus(new CallStatusRequestDTO("call status 2")));
//         Team team = teamRepository.save(new Team(new TeamNoIdDTO("team 2")));
//         Category category = categoryService.save(new CategoryRequestDto("category 2"));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call call = new Call();
//         call.setTitle("Titulo");
//         call.setDescription("Descricao");
//         call.setDateTime(LocalDateTime.of(2022, 1, 4, 1, 8, 45, 97000000));
//         call.setUser(savedUser);
//         call.setFloor(savedFloor);
//         call.setActive(false);

//         call = callRepository.save(call);

//         ClassifyCallDTO classifyCallDTO = new ClassifyCallDTO(team.getId(), category.getId(), callStatus.getId(), CallPriority.Urgent);

//         HttpEntity<ClassifyCallDTO> requestEntity = new HttpEntity<>(classifyCallDTO, getAuthorizationTokenHeader());

//         ResponseEntity<Void> response = restTemplate.exchange(
//                 getRootUrl() + CALL_URL + "/" + call.getId(),
//                 HttpMethod.PUT,
//                 requestEntity,
//                 Void.class
//         );

//         Assertions.assertNotNull(response);
//         Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//         Assertions.assertNull(response.getBody());

//         Call classifiedCall = this.callService.getById(call.getId());

//         Assertions.assertNotNull(classifiedCall);
//         Assertions.assertEquals(call.getId(), classifiedCall.getId());
//         Assertions.assertEquals(call.getDescription(), classifiedCall.getDescription());
//         Assertions.assertEquals(call.getFloor().getId(), classifiedCall.getFloor().getId());
//         Assertions.assertEquals(call.getTitle(), classifiedCall.getTitle());
//         Assertions.assertEquals(call.getDateTime(), classifiedCall.getDateTime());
//         Assertions.assertEquals(call.getUser().getId(), classifiedCall.getUser().getId());
//         Assertions.assertEquals(category.getId(), classifiedCall.getCategory().getId());
//         Assertions.assertEquals(category.getName(), classifiedCall.getCategory().getName());
//         Assertions.assertEquals(team.getId(), classifiedCall.getTeam().getId());
//         Assertions.assertEquals(team.getName(), classifiedCall.getTeam().getName());
//         Assertions.assertEquals(callStatus.getId(), classifiedCall.getCallStatus().getId());
//         Assertions.assertEquals(callStatus.getCallStatusName(), classifiedCall.getCallStatus().getCallStatusName());
//         Assertions.assertEquals(CallPriority.Urgent, classifiedCall.getPriority());
//     }

//     private HttpHeaders getAuthorizationTokenHeader(){
//         /* Realizando login com o admin criado no metodo run da classe principal: */
//         LoginRequestDTO loginRequest = new LoginRequestDTO("admin@gmail.com", "123456");
//         ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity(getRootUrl() + "/login", loginRequest, LoginResponseDTO.class);

//         /* Criando header com token JWT para próximas requests como admin: */
//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_JSON);
//         headers.set("Authorization", "Bearer "+loginResponse.getBody().token());
// 		return headers;
// 	}
// }
