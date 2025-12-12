package br.edu.lampi.infrareport.controller;

import br.edu.lampi.infrareport.controller.dto.callComment.CallCommentRequestDTO;
import br.edu.lampi.infrareport.controller.dto.callComment.CallCommentResponseDTO;
import br.edu.lampi.infrareport.model.callComment.CallComment;
import br.edu.lampi.infrareport.service.CallCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CallCommentController {
    private final CallCommentService callCommentService;

    public CallCommentController(CallCommentService callCommentService) {
        this.callCommentService = callCommentService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "return callComment filtered by id", tags = "CallComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CallComment returned successfully"),
            @ApiResponse(responseCode = "204", description = "No content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing data to authenticate", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized, unauthorized resource", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    public ResponseEntity<CallCommentResponseDTO> searchById(@PathVariable Long id) {
        return ResponseEntity.ok(new CallCommentResponseDTO(this.callCommentService.getById(id)));
    }

    @GetMapping("/call/{id}")
    @Operation(summary = "return all callComment filtered by id call", tags = "CallComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List CallComment returned successfully"),
            @ApiResponse(responseCode = "204", description = "No content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing data to authenticate", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized, unauthorized resource", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    public List<CallCommentResponseDTO> searchByIdCall(@PathVariable Long id) {
        return this.callCommentService.getByCallId(id).stream()
                .map(CallCommentResponseDTO::new)
                .toList();
    }

    @Operation(summary = "return all callComment", tags = "CallComment")
    @GetMapping
    public List<CallCommentResponseDTO> searchAllCallComment() {
        return this.callCommentService.getAll()
                .stream()
                .map(CallCommentResponseDTO::new)
                .toList();
    }

    @PostMapping
    @Transactional
    @Operation(summary = "return saved callComment", tags = "CallComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saved success"),
            @ApiResponse(responseCode = "204", description = "No content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing data to authenticate", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized, unauthorized resource", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    public ResponseEntity<CallCommentResponseDTO> createCallComment(@RequestBody CallCommentRequestDTO callCommentRequestDTO, UriComponentsBuilder uriComponentsBuilder) {
        CallComment savedComment = callCommentService.save(callCommentRequestDTO);
        URI uri = uriComponentsBuilder.path("/comment/{id}").buildAndExpand(savedComment.getId()).toUri();
        return ResponseEntity.created(uri).body(new CallCommentResponseDTO(savedComment));
    }

    @Transactional
    @PutMapping(value = "/{id}")
    @Operation(summary = "return an updated callComment", tags = "CallComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update success"),
            @ApiResponse(responseCode = "204", description = "No content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing data to authenticate", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized, unauthorized resource", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    public ResponseEntity<CallCommentResponseDTO> updateCallComment(@PathVariable("id") Long id, @Valid @RequestBody CallCommentRequestDTO categoryDto) {
        return ResponseEntity.ok(new CallCommentResponseDTO(callCommentService.updateById(id, categoryDto)));
    }

    @DeleteMapping(value = "/{id}")
    @Transactional
    @Operation(summary = "Delete comment", tags = "CallComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update success"),
            @ApiResponse(responseCode = "204", description = "No content", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing data to authenticate", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized, unauthorized resource", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content)
    })
    public ResponseEntity<Void> deleteCallComment(@PathVariable Long id) {
        callCommentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "add viewed status", description = "add viewed status to call comment", 
        responses = {
            @ApiResponse(responseCode = "204", description = "comment status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request."),
            @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request."),
            @ApiResponse(responseCode = "404", description = "Call not found."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.")
        }
    )

    @PutMapping("/call/{id}/messages/viewed")
    public ResponseEntity<Void>  addViewedStatusToCallCommentByCallId(@PathVariable Long id){
        this.callCommentService.addViewedStatusToCallCommentByCallId(id);

        return ResponseEntity.noContent().build();
    }
}
