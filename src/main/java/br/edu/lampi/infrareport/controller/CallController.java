package br.edu.lampi.infrareport.controller;

import java.net.URI;

import br.edu.lampi.infrareport.controller.dto.call.*;
import br.edu.lampi.infrareport.controller.dto.call.CallUserWithDetailsResponseDTO;
import br.edu.lampi.infrareport.controller.dto.call.CallDeactivationDTO;
import br.edu.lampi.infrareport.model.floor.Floor;
import br.edu.lampi.infrareport.service.FloorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import br.edu.lampi.infrareport.model.call.Call;
import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.service.CallService;
import br.edu.lampi.infrareport.service.UserService;
import br.edu.lampi.infrareport.service.exceptions.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/calls")
@Tag(name = "Calls", description = "This is api of Calls.")
public class CallController {
    @Autowired
    private CallService callService;

    @Autowired
    private UserService userService;

    @Autowired
    private FloorService floorService;

    @Operation(
        summary = "returns filtered calls.", description = "Returns a call list filtered by the specifieds fields. If a field is not specified, it is not considered.", 
        responses = {
            @ApiResponse(responseCode = "200", description = "Call list returned successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
        }
    )

    @PostMapping("/search_by_filter")
    public Page<CallUserResponseDTO> searchByFilter(@RequestBody(required = false) CallFilterDTO callFilter, @PageableDefault(sort = {"id"}, size = 10) Pageable pageable){
        return this.callService
            .searchFilter(callFilter != null ? callFilter : new CallFilterDTO(), pageable)
            .map(CallUserResponseDTO::new);
    }

    @Operation(
        summary = "returns a call by id.", description = "Returns call by the given id.", 
        responses = {
            @ApiResponse(responseCode = "200", description = "Call returned successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Call not found with the given id.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CallUserResponseDTO> searchById(@PathVariable Long id){
        return ResponseEntity.ok(new CallUserResponseDTO(this.callService.getById(id)));
    }
    @Operation(
            summary = "returns a call by id with details.", description = "Returns call by the given id with details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Call returned successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Call not found with the given id.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    @GetMapping("/details/{id}")
    public ResponseEntity<CallUserWithDetailsResponseDTO> searchByIdAndReturnDetails(@PathVariable Long id){
        if(id == null){
            throw new BadRequestException("call id must not be null");
        }
        return ResponseEntity.ok(new CallUserWithDetailsResponseDTO(this.callService.getById(id)));
    }

    @Operation(
            summary = "returns a calls by user id.", description = "Returns calls by the given user id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Calls returned successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valid request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Call not found with the given id.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    @GetMapping("/my_calls")
    public Page<CallByUserResponseDTO> searchCallsByUserId(@PageableDefault(sort = {"id"}, size = 10) Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.searchByEmail(email);
        return callService.searchCallsByUserId(user.getId(), pageable)
                .map(CallByUserResponseDTO::new);
    }

    @Operation(
        summary = "save a new call.", description = "record a new call in database and return recorded call.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Call saved successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request. You may have submitted some invalid data.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping
    @Transactional
    public ResponseEntity<CallUserResponseDTO> createCall(@RequestBody @Valid CallUserRequestDTO data, UriComponentsBuilder uriBuilder){
        //TODO: fix user to entity, then check if user is equal to logged in
        User user = userService.searchById(data.userId());

        Floor floor = floorService.getFloorByID(data.floorId());

        CallUserRequestDTO processedData = new CallUserRequestDTO(data.title(), data.description(), data.dateTime(), user.getId(), floor.getId());
        Call savedCall = callService.save(processedData);
        URI uri = uriBuilder.path("/calls/{id}").buildAndExpand(savedCall.getId()).toUri();
        return ResponseEntity.created(uri).body(new CallUserResponseDTO(savedCall));
    }

    @Operation(
        summary = "Deactivate a call by id.", description = "Deactivate a call by the given id.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Call deactivated successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Call not found with the given id.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @PostMapping("/deactivate/{id}")
    public ResponseEntity<Void> deactivateCall(@PathVariable Long id, @RequestBody(required = false) CallDeactivationDTO dto) {
        String justification = dto.justification() != null ? dto.justification() : "Chamada deletada pelo administrador";
        callService.deactivateById(id, justification);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/active/{id}")
    public ResponseEntity<Void> activeCall(@PathVariable Long id) {
        callService.activeCall(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "return a call CSV file.", description = "Returns a call CSV filtered by the specifieds fields. If a field is not specified, it is not considered.", 
        responses = {
            @ApiResponse(responseCode = "200", description = "Call CSV file returned successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
        }
    )

    @PostMapping("/csv_file")
    public ResponseEntity<byte[]> generateCSV(@RequestBody(required = false) CallFilterDTO callFilter) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename("calls.csv").build());
        return ResponseEntity.ok().headers(headers).body(this.callService.generateCSV(callFilter != null ? callFilter : new CallFilterDTO()));
    }

    @Operation(
        summary = "classify a call ", description = "Set attributes like priority, status, team and category", 
        responses = {
            @ApiResponse(responseCode = "204", description = "Call clssified successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request."),
            @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request."),
            @ApiResponse(responseCode = "404", description = "Resource not found."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.")
        }
    )

    @PutMapping("/{id}")
    public ResponseEntity<Void> classifyCall(@PathVariable Long id, @RequestBody ClassifyCallDTO classifyCallDTO){
        this.callService.classifyCall(id, classifyCallDTO);

        return ResponseEntity.noContent().build();
    }
}
