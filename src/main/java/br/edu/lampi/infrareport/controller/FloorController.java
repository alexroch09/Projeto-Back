package br.edu.lampi.infrareport.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.edu.lampi.infrareport.controller.dto.floor.FloorRequestDTO;
import br.edu.lampi.infrareport.controller.dto.floor.FloorResponseDTO;
import br.edu.lampi.infrareport.model.floor.Floor;
import br.edu.lampi.infrareport.service.FloorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/floor")
@Tag(name = "Floor", description = "This is the API for Floor.")
public class FloorController {

    @Autowired
    private FloorService fService;

    @Operation(summary = "Returns a list of Floors.", description = "Returns a list of all Floors in the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List returned successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad request. Please submit a valid request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Permission denied. You don't have permission to make this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            })
    @GetMapping
    public Page<FloorResponseDTO> getAllFloors(@PageableDefault(sort = "id", size = 10, direction = Sort.Direction.DESC) Pageable pageable)
            throws Exception {
            return fService.getFloorsPageable(pageable);
    }

    @Operation(summary = "Returns a Floor by ID.", description = "Returns a Floor by ID from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Floor returned successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad request. Please submit a valid request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Permission denied. You don't have permission to make this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Floor not found with the given ID.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<FloorResponseDTO> getFloor(@PathVariable Long id) {
            return ResponseEntity.ok(new FloorResponseDTO(fService.getFloorByID(id)));
    }

    @Operation(summary = "Save a new Floor.", description = "Save a Floor in the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Floor saved successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad request. Please submit a valid request. You may have submitted some invalid data.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "409", description = "Floor name is already in use.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            })
    @PostMapping
    @Transactional
    public ResponseEntity<FloorResponseDTO> createFloor(@RequestBody @Valid FloorRequestDTO dto, UriComponentsBuilder uriBuilder) {
        Floor floor = fService.saveNewFloor(new Floor(dto));
        
        URI uri = uriBuilder.path("/floor/{id}").buildAndExpand(floor.getId()).toUri();

        return ResponseEntity.created(uri).body(new FloorResponseDTO(floor));
    }

    @Operation(summary = "Update a Floor.", description = "Update a Floor in the database by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Floor updated successfully.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "400", description = "Bad request. Please submit a valid request. You may have submitted some invalid data.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "409", description = "Floor name is already in use.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            })
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> updateFloorById(@PathVariable Long id, @RequestBody @Valid FloorRequestDTO dto) {
        fService.updateFloorById(id, dto);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a Floor.", description = "Delete a Floor in the database by ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Floor deleted successfully.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "400", description = "Bad request. Please submit a valid request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Permission denied. You don't have permission to make this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            })
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteFloorById(@PathVariable Long id) {
        fService.deleteFloorById(id);

        return ResponseEntity.noContent().build();
    }
}
