package br.edu.lampi.infrareport.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.edu.lampi.infrareport.controller.dto.callStatus.CallStatusRequestDTO;
import br.edu.lampi.infrareport.controller.dto.callStatus.CallStatusResponseDTO;
import br.edu.lampi.infrareport.model.callstatus.CallStatus;
import br.edu.lampi.infrareport.service.CallStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/call_status")
@Tag(name = "CallStatus", description = "This is api of CallStatus.")
public class CallStatusController {
    private final CallStatusService callStatusService;

    public CallStatusController(CallStatusService callStatusService) {
        this.callStatusService = callStatusService;
    }

    @Operation(summary = "returns a list of call status.", description = "Returns a list of all call status in database.", 
        responses = {
            @ApiResponse(responseCode = "200", description = "List returned successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @GetMapping
    public List<CallStatusResponseDTO> search() {
        return this.callStatusService.search()
                    .stream()
                    .map(CallStatusResponseDTO::new)
                    .toList();
    }

    
    @Operation(summary = "returns a call status by id.", description = "Returns call status by the given id.", 
        responses = {
            @ApiResponse(responseCode = "200", description = "Call status returned successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Call status not found with the given id.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @GetMapping("/{id}")
    public ResponseEntity<CallStatusResponseDTO> searchById(@PathVariable Long id){
        return ResponseEntity.ok(new CallStatusResponseDTO(this.callStatusService.searchById(id)));
    }

    @Operation(summary = "save a new call status.", description = "record a new call status in database.", 
    responses = {
        @ApiResponse(responseCode = "201", description = "Call status saved successfully."),
        @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request. You may have submitted some invalid data.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "409", description = "Call status name is already in use.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @PostMapping
    public ResponseEntity<CallStatusResponseDTO> save(@RequestBody @Valid CallStatusRequestDTO callStatusRequestDTO, UriComponentsBuilder uriBuilder) {
        CallStatus savedCallStatus = this.callStatusService.save(callStatusRequestDTO);

        URI uri = uriBuilder.path("/call_status/{id}").buildAndExpand(savedCallStatus.getId()).toUri();

        return ResponseEntity.created(uri).body(new CallStatusResponseDTO(savedCallStatus));
    }

    @Operation(summary = "delete a call status.", description = "delete a call status by id from database.", 
    responses = {
        @ApiResponse(responseCode = "204", description = "Call status deleted successfully.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request. You may have submitted some invalid data.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "409", description = "There are calls associated with this call status.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.callStatusService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "update a call status.", description = "update a call status by id.", 
    responses = {
        @ApiResponse(responseCode = "200", description = "Call status deleted successfully."),
        @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request. You may have submitted some invalid data.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "Call status not found with the given id.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "409", description = "Call status name is already in use.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @PutMapping("/{id}")
    public ResponseEntity<CallStatusResponseDTO> update(@PathVariable Long id, @RequestBody CallStatusRequestDTO callStatusRequestDTO){
        return ResponseEntity.ok(new CallStatusResponseDTO(this.callStatusService.update(id, callStatusRequestDTO)));
    }

}
