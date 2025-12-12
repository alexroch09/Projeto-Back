// package br.edu.lampi.infrareport;

// import java.time.LocalDateTime;
// import java.util.Collections;
// import java.util.List;
// import java.util.Objects;
// import java.util.Random;

// import br.edu.lampi.infrareport.controller.dto.call.CallUserRequestDTO;
// import br.edu.lampi.infrareport.model.building.Building;
// import br.edu.lampi.infrareport.model.floor.Floor;
// import br.edu.lampi.infrareport.service.*;
// import org.json.JSONArray;
// import org.json.JSONException;

// import br.edu.lampi.infrareport.config.ExceptionDetails;
// import br.edu.lampi.infrareport.controller.dto.callImage.CallImageResponseDTO;

// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInstance;
// import org.springframework.beans.factory.annotation.Autowired;

// import br.edu.lampi.infrareport.controller.dto.callImage.CallImageRequestDTO;
// import br.edu.lampi.infrareport.controller.dto.category.CategoryRequestDto;
// import br.edu.lampi.infrareport.controller.dto.user.LoginRequestDTO;
// import br.edu.lampi.infrareport.controller.dto.user.LoginResponseDTO;
// import br.edu.lampi.infrareport.controller.dto.user.UserRequestDTO;
// import br.edu.lampi.infrareport.model.call.Call;
// import br.edu.lampi.infrareport.model.call.CallPriority;
// import br.edu.lampi.infrareport.model.callImage.CallImage;
// import br.edu.lampi.infrareport.model.category.Category;
// import br.edu.lampi.infrareport.model.user.User;
// import br.edu.lampi.infrareport.model.user.UserType;
// import br.edu.lampi.infrareport.repository.CallRepository;
// import br.edu.lampi.infrareport.service.exceptions.BadRequestException;
// import jakarta.transaction.Transactional;

// import org.springframework.http.*;
// import org.springframework.test.annotation.DirtiesContext;

// import static org.springframework.http.HttpStatus.NOT_FOUND;

// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// public class CallImageFullTests extends InfrareportApplicationTests {
//     @Autowired
//     CallRepository callRepo;

//     @Autowired
//     CallImageService callImageService;

//     @Autowired
//     UserService userService;

//     @Autowired
//     CallService callService;

//     @Autowired
//     BuildingService buildingService;

//     @Autowired
//     FloorService floorService;

//     @Autowired
//     CategoryService categoryService;

//     private final static String CALL_IMAGE_URL = "/image";

//     /**
//      * Teste para verificar Model de CallImage
//      */
//     @Test
//     public void model_instantiateCallImageAndCheckIfValid() {
//         CallImage i = new CallImage();
//         i.setId(23L);
//         i.setImagePath("https://ifce.edu.br/prpi/documentos-1/semic/2018/logo-ifce-vertical.png/@@images/a8ec0f9c-0cbd-499c-a903-e034b8b8579e.png");
//         final String expected = "CallImage(id=23, call=null, imagePath=https://ifce.edu.br/prpi/documentos-1/semic/2018/logo-ifce-vertical.png/@@images/a8ec0f9c-0cbd-499c-a903-e034b8b8579e.png)";
//         Assertions.assertEquals(expected, i.toString());
//     }

//     /**
//      * Teste para verificar save em Service de CallImage
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_save_saveCallImageAndCheckIfValid() {
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user"+seed+"@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         Category category = new Category();
//         category.setName("Categoria");
//         Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call c = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         c.setTitle("Titulo Titulo");
//         c.setDescription("Descricao Descricao Descricao");
//         c.setDateTime(instant);
//         c.setUser(savedUser);
//         c.setFloor(savedFloor);
//         c.setCategory(savedCategory);
//         c.setPriority(CallPriority.High);
//         c.setActive(true);
//         c.setJustification("Justificativa Justificativa");

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         CallImage i = new CallImage();
//         i.setImagePath("path path path");
//         i.setCall(savedCall);

//         CallImageRequestDTO callImageRequestDTO = new CallImageRequestDTO(i.getCall().getId(), i.getImagePath());
//         CallImage iSaved = callImageService.save(callImageRequestDTO);

//         Assertions.assertNotNull(iSaved);
//         Assertions.assertEquals(i.getImagePath(), iSaved.getImagePath());
//         Assertions.assertEquals(i.getCall().getId(), iSaved.getCall().getId());
//         Assertions.assertNotNull(iSaved.getId());
//         Assertions.assertNotEquals("0", iSaved.getId());
//     }

//     /**
//      * Teste para verificar save falha de save no Service de CallImage
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_save_saveCallImageWithouCallAndCheckIfFailed() {
//         CallImage i = new CallImage();
//         i.setImagePath("path path path");

//         CallImageRequestDTO callImageRequestDTO = new CallImageRequestDTO(null, i.getImagePath());

//         BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
//             @SuppressWarnings("unused")
//             CallImage iSaved = callImageService.save(callImageRequestDTO);
//         });

//         Assertions.assertEquals("The given call id must not be null", thrown.getMessage());
//     }


//     /**
//      * Teste para verificar get em Service de CallImage
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_getById_saveCallImageAndGetCreatedCallImageByIdThenVerifyIfEqualCreated() {
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user"+seed+"@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         Category category = new Category();
//         category.setName("Categoria");
//         Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call c = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         c.setTitle("Titulo Titulo");
//         c.setDescription("Descricao Descricao Descricao");
//         c.setDateTime(instant);
//         c.setUser(savedUser);
//         c.setFloor(savedFloor);
//         c.setCategory(savedCategory);
//         c.setPriority(CallPriority.High);
//         c.setActive(true);
//         c.setJustification("Justificativa Justificativa");

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         CallImage i = new CallImage();
//         i.setImagePath("path path path");
//         i.setCall(savedCall);

//         CallImageRequestDTO callImageRequestDTO = new CallImageRequestDTO(i.getCall().getId(), i.getImagePath());
//         CallImage savedCallImage = callImageService.save(callImageRequestDTO);
//         Long savedCallImageId = savedCallImage.getId();

//         CallImage gotCallImage = callImageService.getById(savedCallImageId);

//         Assertions.assertNotNull(gotCallImage);
//         Assertions.assertEquals(savedCallImage.getImagePath(), gotCallImage.getImagePath());
//         Assertions.assertEquals(savedCallImage.getCall().getId(), gotCallImage.getCall().getId());
//         Assertions.assertNotNull(gotCallImage.getId());
//         Assertions.assertNotEquals("0", gotCallImage.getId());
//     }

//     /**
//      * Teste para verificar get em Service de CallImage
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_getByCallId_saveCallImageAndGetByCallIdThenVerifyIfEqualCreated() {
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user"+seed+"@mail.c");
//         user.setUserType(List.of(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         Category category = new Category();
//         category.setName("Categoria");
//         Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call c = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         c.setTitle("Titulo Titulo");
//         c.setDescription("Descricao Descricao Descricao");
//         c.setDateTime(instant);
//         c.setUser(savedUser);
//         c.setFloor(savedFloor);
//         c.setCategory(savedCategory);
//         c.setPriority(CallPriority.High);
//         c.setActive(true);
//         c.setJustification("Justificativa Justificativa");

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         CallImage i = new CallImage();
//         i.setImagePath("path path path");
//         i.setCall(savedCall);

//         CallImageRequestDTO callImageRequestDTO = new CallImageRequestDTO(i.getCall().getId(), i.getImagePath());
//         CallImage savedCallImage = callImageService.save(callImageRequestDTO);

//         List<CallImage> gotCallImageList = callImageService.getByCallId(savedCall.getId());

//         Assertions.assertNotNull(gotCallImageList);
//         Assertions.assertNotEquals(0, gotCallImageList.size());
//         Assertions.assertEquals(savedCallImage.getImagePath(), gotCallImageList.get(0).getImagePath());
//     }

//     /**
//      * Teste para verificar get all em Service de CallImage
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_getAll_saveCallImageAndGetAllThenVerifyIfCreatedIsInList() {
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

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call c = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         c.setTitle("Titulo Titulo");
//         c.setDescription("Descricao Descricao Descricao");
//         c.setDateTime(instant);
//         c.setUser(savedUser);
//         c.setFloor(savedFloor);
//         c.setCategory(savedCategory);
//         c.setPriority(CallPriority.High);
//         c.setActive(true);
//         c.setJustification("Justificativa Justificativa");

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         CallImage i = new CallImage();
//         i.setImagePath("path path path");
//         i.setCall(savedCall);

//         CallImageRequestDTO callImageRequestDTO = new CallImageRequestDTO(i.getCall().getId(), i.getImagePath());
//         CallImage savedCallImage = callImageService.save(callImageRequestDTO);

//         List<CallImage> gotCallImages = callImageService.getAll();

//         Assertions.assertNotNull(gotCallImages);
//         Assertions.assertNotEquals(0, gotCallImages.size());
//         Assertions.assertTrue(gotCallImages.contains(savedCallImage));
//     }


//     /**
//      * Teste para verificar delete and exists em Service de CallImage
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_deleteAndExists_saveCallImageAndDeleteCreatedCallImageByIdThenVerifyIfExists() {
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

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call c = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         c.setTitle("Titulo Titulo");
//         c.setDescription("Descricao Descricao Descricao");
//         c.setDateTime(instant);
//         c.setUser(savedUser);
//         c.setFloor(savedFloor);
//         c.setCategory(savedCategory);
//         c.setPriority(CallPriority.High);
//         c.setActive(true);
//         c.setJustification("Justificativa Justificativa");

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         CallImage i = new CallImage();
//         i.setImagePath("path path path");
//         i.setCall(savedCall);

//         CallImageRequestDTO callImageRequestDTO = new CallImageRequestDTO(i.getCall().getId(), i.getImagePath());
//         CallImage savedCallImage = callImageService.save(callImageRequestDTO);
//         Long savedCallImageId = savedCallImage.getId();

//         boolean existsBefore = callImageService.existsById(savedCallImageId);

//         callImageService.deleteById(savedCallImageId);

//         boolean existsAfter = callImageService.existsById(savedCallImageId);

//         Assertions.assertEquals(true, existsBefore);
//         Assertions.assertEquals(false, existsAfter);
//     }

//     /**
//      * Teste para verificar update em Service de CallImage
//      */
//     @SuppressWarnings("null")
//     @Test
//     @Transactional
//     public void service_updateById_saveCallImageAndUpdateCallImageByIdThenVerifyIfValid() {
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

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call c = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         c.setTitle("Titulo Titulo");
//         c.setDescription("Descricao Descricao Descricao");
//         c.setDateTime(instant);
//         c.setUser(savedUser);
//         c.setFloor(savedFloor);
//         c.setCategory(savedCategory);
//         c.setPriority(CallPriority.High);
//         c.setActive(true);
//         c.setJustification("Justificativa Justificativa");

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         CallImage i = new CallImage();
//         i.setImagePath("path path path");
//         i.setCall(savedCall);

//         CallImageRequestDTO callImageRequestDTO = new CallImageRequestDTO(i.getCall().getId(), i.getImagePath());
//         CallImage savedCallImage = callImageService.save(callImageRequestDTO);
//         Long savedCallImageId = savedCallImage.getId();

//         String newPath = "path path path path";
//         CallImageRequestDTO updateCallImage = new CallImageRequestDTO(i.getCall().getId(), newPath);
//         callImageService.updateById(savedCallImageId, updateCallImage);

//         CallImage updatedCallImage = callImageService.getById(savedCallImageId);
//         Assertions.assertNotNull(updatedCallImage);
//         Assertions.assertEquals(newPath, updatedCallImage.getImagePath());
//     }


//     @Test
//     public void controller_searchById_createCallImageAndSearchByYourId() {
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         Category category = new Category();
//         category.setName("Categoria");
//         Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call c = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         c.setTitle("Titulo Titulo");
//         c.setDescription("Descricao Descricao Descricao");
//         c.setDateTime(instant);
//         c.setUser(savedUser);
//         c.setFloor(savedFloor);
//         c.setCategory(savedCategory);
//         c.setPriority(CallPriority.High);
//         c.setActive(true);
//         c.setJustification("Justificativa Justificativa");

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         CallImage i = new CallImage();
//         i.setImagePath("path path path");
//         i.setCall(savedCall);

//         CallImageRequestDTO callImageRequest = new CallImageRequestDTO(i.getCall().getId(), i.getImagePath());
//         HttpEntity<CallImageRequestDTO> entity = new HttpEntity<>(callImageRequest, getAuthorizationTokenHeader());
//         ResponseEntity<CallImageResponseDTO> savedCallImage = restTemplate.postForEntity(getRootUrl() + CALL_IMAGE_URL, entity, CallImageResponseDTO.class);

//         Assertions.assertNotNull(savedCallImage);

//         Long id = Objects.requireNonNull(savedCallImage.getBody()).id();

//         HttpEntity<Void> getEntity = new HttpEntity<>(null, getAuthorizationTokenHeader());
//         ResponseEntity<CallImageResponseDTO> searchResultCallImage = restTemplate.exchange(getRootUrl() + CALL_IMAGE_URL + "/" + id, HttpMethod.GET, getEntity, CallImageResponseDTO.class);

//         Assertions.assertNotNull(searchResultCallImage);
//         Assertions.assertEquals(savedCallImage.getBody().call_id(), searchResultCallImage.getBody().call_id());
//         Assertions.assertEquals(savedCallImage.getBody().imagePath(), searchResultCallImage.getBody().imagePath());
//     }

//     /**
//      * Teste para verificar o Controller de pesquisar por id do CallImage
//      */
//     @Test
//     public void controller_searchById_shouldThrowRunTimeExceptionWhenNotFoundCallImageWithId() {
//         HttpEntity<CallImageRequestDTO> entity = new HttpEntity<CallImageRequestDTO>(null, getAuthorizationTokenHeader());

//         ResponseEntity<ExceptionDetails> exceptionCallImage = restTemplate.exchange(getRootUrl() + CALL_IMAGE_URL + "/" + 1000000L, HttpMethod.GET, entity, ExceptionDetails.class);

//         Assertions.assertEquals(NOT_FOUND.value(), exceptionCallImage.getBody().getStatus());
//     }
//     /**
//      * Teste para verificar o Controller de retornar todos os CallImage
//      * @throws JSONException
//      */
//     @Test
//     public void controller_searchAll_returnCallImageList() throws JSONException {
//         HttpHeaders headers = getAuthorizationTokenHeader();
//         User user = new User();
//         user.setName("Usuario Usuario");
//         int seed = new Random().nextInt();
//         user.setEmail("user" + seed + "@mail.c");
//         user.setUserType(Collections.singletonList(UserType.ROLE_COMMON));
//         user.setPassword("passpass");
//         User savedUser = userService.save(new UserRequestDTO(user));

//         Category category = new Category();
//         category.setName("Categoria");
//         Category savedCategory = categoryService.save(new CategoryRequestDto(category.getName()));

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call c = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         c.setTitle("Titulo Titulo");
//         c.setDescription("Descricao Descricao Descricao");
//         c.setDateTime(instant);
//         c.setUser(savedUser);
//         c.setFloor(savedFloor);
//         c.setCategory(savedCategory);
//         c.setPriority(CallPriority.High);
//         c.setActive(true);
//         c.setJustification("Justificativa Justificativa");

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         CallImage i = new CallImage();
//         i.setImagePath("path path path");
//         i.setCall(savedCall);

//         CallImageRequestDTO callImageRequest = new CallImageRequestDTO(i.getCall().getId(), i.getImagePath());
//         HttpEntity<CallImageRequestDTO> entity = new HttpEntity<>(callImageRequest, headers);
//         ResponseEntity<CallImageResponseDTO> savedCallImage = restTemplate.postForEntity(getRootUrl() + CALL_IMAGE_URL, entity, CallImageResponseDTO.class);

//         Assertions.assertNotNull(savedCallImage);

//         entity = new HttpEntity<>(null, headers);

//         ResponseEntity<String> responseCallImageList = restTemplate.exchange(getRootUrl() + CALL_IMAGE_URL, HttpMethod.GET, entity, String.class);

//         JSONArray listCallImage = new JSONArray(responseCallImageList.getBody());
//         Assertions.assertNotNull(listCallImage);
//         Assertions.assertTrue(listCallImage.length() > 0);
//         Assertions.assertEquals(savedCallImage.getBody().call_id(), listCallImage.getJSONObject(0).getLong("call_id"));
//         Assertions.assertEquals(savedCallImage.getBody().imagePath(), listCallImage.getJSONObject(0).getString("imagePath"));
//     }

//     /**
//      * Teste para verificar o Controller de retornar todos os CallImage
//      * @throws JSONException
//      */
//     @Test
//     public void controller_searchAll_returnEmptyCallImageList() throws JSONException {
//         HttpEntity<String> entity = new HttpEntity<String>(null, getAuthorizationTokenHeader());

//         ResponseEntity<String> responseCallImageList = restTemplate.exchange(getRootUrl() + CALL_IMAGE_URL,
//                 HttpMethod.GET, entity, String.class);

//         JSONArray listCallImageEmpty = new JSONArray(responseCallImageList.getBody());
//         Assertions.assertEquals(0, listCallImageEmpty.length());
//     }
//     /**
//      * Teste para verificar o Controller de pesquisar por id da Call do CallImage
//      * @throws JSONException
//      */
//     @Test
//     public void controller_searchByIdCall_createCallAndCallImageAndSearchByIdCall() throws JSONException {
//         HttpHeaders headers = getAuthorizationTokenHeader();
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

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call c = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         c.setTitle("Titulo Titulo");
//         c.setDescription("Descricao Descricao Descricao");
//         c.setDateTime(instant);
//         c.setUser(savedUser);
//         c.setFloor(savedFloor);
//         c.setCategory(savedCategory);
//         c.setPriority(CallPriority.High);
//         c.setActive(true);
//         c.setJustification("Justificativa Justificativa");

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         CallImage i = new CallImage();
//         i.setImagePath("path path path");
//         i.setCall(savedCall);

//         CallImageRequestDTO callImageRequest = new CallImageRequestDTO(i.getCall().getId(), i.getImagePath());
//         HttpEntity<CallImageRequestDTO> entity = new HttpEntity<>(callImageRequest, headers);
//         ResponseEntity<CallImageResponseDTO> savedCallImage = restTemplate.postForEntity(getRootUrl() + CALL_IMAGE_URL, entity, CallImageResponseDTO.class);

//         Assertions.assertNotNull(savedCallImage);

//         Long id = savedCall.getId();

//         entity = new HttpEntity<>(null, headers);

//         ResponseEntity<String> responseCallImageList = restTemplate.exchange(getRootUrl() + CALL_IMAGE_URL + "/call/" + id,
//                 HttpMethod.GET, entity, String.class);

//         JSONArray listCallImage = new JSONArray(responseCallImageList.getBody());
//         Assertions.assertNotNull(responseCallImageList.getBody());
//         Assertions.assertTrue(listCallImage.length() > 0);
//         Assertions.assertEquals(savedCallImage.getBody().id(), listCallImage.getJSONObject(0).getLong("id"));
//         Assertions.assertEquals(savedCallImage.getBody().call_id(), listCallImage.getJSONObject(0).getLong("call_id"));
//         Assertions.assertEquals(savedCallImage.getBody().imagePath(), listCallImage.getJSONObject(0).getString("imagePath"));
//     }

//     /**
//      * Teste para verificar o Controller de pesquisar por id da Call do CallImage
//      * @throws JSONException
//      */
//     @Test
//     public void controller_searchByIdCall_shouldReturnEmptyListCallImageWhenIdCallIsInvalid() throws JSONException {
//         HttpEntity<String> entity = new HttpEntity<String>(null, getAuthorizationTokenHeader());

//         ResponseEntity<String> responseCallImageList = restTemplate.exchange(getRootUrl() + CALL_IMAGE_URL + "/call/" + 10000000L,
//                 HttpMethod.GET, entity, String.class);

//         JSONArray listCallImageEmpty = new JSONArray(responseCallImageList.getBody());
//         Assertions.assertEquals(0, listCallImageEmpty.length());
//     }
//     /**
//      * Teste para verificar o Controller de criar o CallImage
//      */
//     @Test
//     public void controller_createCallImage_shouldReturnCallImage() {
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

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call c = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         c.setTitle("Titulo Titulo");
//         c.setDescription("Descricao Descricao Descricao");
//         c.setDateTime(instant);
//         c.setUser(savedUser);
//         c.setFloor(savedFloor);
//         c.setCategory(savedCategory);
//         c.setPriority(CallPriority.High);
//         c.setActive(true);
//         c.setJustification("Justificativa Justificativa");

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         CallImage i = new CallImage();
//         i.setImagePath("path path path");
//         i.setCall(savedCall);

//         CallImageRequestDTO callImageRequest = new CallImageRequestDTO(i.getCall().getId(), i.getImagePath());
//         HttpEntity<CallImageRequestDTO> entity = new HttpEntity<>(callImageRequest, getAuthorizationTokenHeader());
//         ResponseEntity<CallImageResponseDTO> savedCallImage = restTemplate.postForEntity(getRootUrl() + CALL_IMAGE_URL, entity, CallImageResponseDTO.class);

//         Assertions.assertNotNull(savedCallImage);
//         Assertions.assertNotNull(savedCallImage.getBody());
//         Assertions.assertEquals(callImageRequest.callId(), savedCallImage.getBody().call_id());
//         Assertions.assertEquals(callImageRequest.imagePath(), savedCallImage.getBody().imagePath());
//     }

//     /**
//      * Teste para verificar o Controller de alterar dados do CallImage
//      * @throws JSONException
//      */
//     @Test
//     public void controller_updateById_createCallImageAndChangeTheDataLater() throws JSONException{
//         HttpHeaders headers = getAuthorizationTokenHeader();
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

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call c = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         c.setTitle("Titulo Titulo");
//         c.setDescription("Descricao Descricao Descricao");
//         c.setDateTime(instant);
//         c.setUser(savedUser);
//         c.setFloor(savedFloor);
//         c.setCategory(savedCategory);
//         c.setPriority(CallPriority.High);
//         c.setActive(true);
//         c.setJustification("Justificativa Justificativa");

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         CallImage i = new CallImage();
//         i.setImagePath("path path path");
//         i.setCall(savedCall);

//         CallImageRequestDTO callImageRequest = new CallImageRequestDTO(i.getCall().getId(), i.getImagePath());
//         HttpEntity<CallImageRequestDTO> entity = new HttpEntity<>(callImageRequest, headers);
//         ResponseEntity<CallImageResponseDTO> savedCallImage = restTemplate.postForEntity(getRootUrl() + CALL_IMAGE_URL, entity, CallImageResponseDTO.class);

//         // Preparar o DTO para atualização
//         CallImageRequestDTO requestUpdateDto = new CallImageRequestDTO(savedCallImage.getBody().call_id(), "Update update update");

//         entity = new HttpEntity<>(requestUpdateDto, headers);

//         // Usar o id correto para a atualização
//         ResponseEntity<CallImageResponseDTO> CallImageResponseUpdate = restTemplate.exchange(
//                 getRootUrl() + CALL_IMAGE_URL + "/" + savedCallImage.getBody().id(), HttpMethod.PUT, entity, CallImageResponseDTO.class);

//         Assertions.assertNotNull(CallImageResponseUpdate);
//         Assertions.assertNotNull(CallImageResponseUpdate.getBody());
//         Assertions.assertNotEquals(savedCallImage.getBody().imagePath(), CallImageResponseUpdate.getBody().imagePath());
//     }

//     /**
//      * Teste para verificar o Controller de deletar o CallImage
//      */
//     @Test
//     public void controller_deleteById_CallImageDeletedWithId() {
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

//         // Configura building e floor
//         Building building = new Building();
//         building.setName("Main Building");
//         Building savedBuilding = buildingService.saveNewBuilding(building);

//         Floor floor = new Floor();
//         floor.setName("First Floor");
//         floor.setBuilding(savedBuilding);
//         Floor savedFloor = floorService.saveNewFloor(floor);

//         Call c = new Call();
//         LocalDateTime instant = LocalDateTime.now();
//         c.setTitle("Titulo Titulo");
//         c.setDescription("Descricao Descricao Descricao");
//         c.setDateTime(instant);
//         c.setUser(savedUser);
//         c.setFloor(savedFloor);
//         c.setCategory(savedCategory);
//         c.setPriority(CallPriority.High);
//         c.setActive(true);
//         c.setJustification("Justificativa Justificativa");

//         CallUserRequestDTO callUserRequestDTO = new CallUserRequestDTO(c.getTitle(), c.getDescription(), c.getDateTime(), c.getUser().getId(), c.getFloor().getId());
//         Call savedCall = callService.save(callUserRequestDTO);

//         CallImage i = new CallImage();
//         i.setImagePath("path path path");
//         i.setCall(savedCall);

//         CallImageRequestDTO callImageRequest = new CallImageRequestDTO(savedCall.getId(), i.getImagePath());
//         HttpEntity<CallImageRequestDTO> entity = new HttpEntity<>(callImageRequest, getAuthorizationTokenHeader());
//         ResponseEntity<CallImageResponseDTO> savedCallImage = restTemplate.postForEntity(getRootUrl() + CALL_IMAGE_URL, entity, CallImageResponseDTO.class);

//         restTemplate.exchange(getRootUrl() + CALL_IMAGE_URL + "/" + savedCallImage.getBody().id(), HttpMethod.DELETE, entity, Void.class);

//         Long id = savedCallImage.getBody().id();

//         ResponseEntity<ExceptionDetails> exception = restTemplate.exchange(getRootUrl() + CALL_IMAGE_URL + "/" + id, HttpMethod.GET, entity, ExceptionDetails.class);

//         Assertions.assertEquals(NOT_FOUND.value(), exception.getBody().getStatus());
//     }

//     /**
//      * Teste para verificar o Controller de deletar o CallImage
//      */
//     @Test
//     public void controller_deleteById_shouldThrowRuntimeExceptionWhenCallImageNotFound(){
//         HttpEntity<ExceptionDetails> entity = new HttpEntity<ExceptionDetails>(null, getAuthorizationTokenHeader());

//         ResponseEntity<ExceptionDetails> exception = restTemplate.exchange(getRootUrl() + CALL_IMAGE_URL + "/" + 1L,
//                 HttpMethod.DELETE, entity, ExceptionDetails.class);

//         Assertions.assertEquals(NOT_FOUND.value(), exception.getBody().getStatus());

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
