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


import br.edu.lampi.infrareport.controller.dto.building.BuildingRequestDTO;
import br.edu.lampi.infrareport.controller.dto.building.BuildingResponseDTO;
import br.edu.lampi.infrareport.controller.dto.building.BuildingUpdateDTO;
import br.edu.lampi.infrareport.model.building.Building;
import br.edu.lampi.infrareport.service.BuildingService;
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
@RequestMapping("/building")
@Tag(name = "Building", description = "This is the API for Building.")
public class BuildingController {
    @Autowired
    BuildingService bService;

    @Operation(summary = "Returns a list of Buildings.", description = "Returns a list of all Buildings in the database.",
        responses = {
            @ApiResponse(responseCode = "200", description = "List returned successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Please submit a valid request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Permission denied. You don't have permission to make this request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @GetMapping
    public Page<BuildingResponseDTO> getAllBuildings(@PageableDefault(sort = "id", size = 10, direction = Sort.Direction.DESC) Pageable pageable)
            throws Exception {
            return bService.getBuildingsPageable(pageable);
    }

    @Operation(summary = "Returns a Building by ID.", description = "Returns a Building by ID from the database.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Building returned successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Please submit a valid request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Permission denied. You don't have permission to make this request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Building not found with the given ID.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @GetMapping("/{id}")
    public ResponseEntity<BuildingResponseDTO> getBuilding(@PathVariable Long id) {
            return ResponseEntity.ok(new BuildingResponseDTO(bService.getBuildingByID(id)));
    }

    @Operation(summary = "Save a new Building.", description = "Save a Building in the database.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Building saved successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Please submit a valid request. You may have submitted some invalid data.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "Building name is already in use.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @PostMapping
    @Transactional
    public ResponseEntity<BuildingResponseDTO> createBuilding(@RequestBody @Valid BuildingRequestDTO dto, UriComponentsBuilder uriBuilder) {
        Building building = bService.saveNewBuilding(new Building(dto));

        URI uri = uriBuilder.path("/building/{id}").buildAndExpand(building.getId()).toUri();

        return ResponseEntity.created(uri).body(new BuildingResponseDTO(building));
    }

    @Operation(summary = "Update a Building.", description = "Update a Building in the database by ID.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Building updated successfully.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "Bad request. Please submit a valid request. You may have submitted some invalid data.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "Building name is already in use.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> updateBuildingById(@PathVariable Long id, @RequestBody @Valid BuildingUpdateDTO dto) {
        bService.updateBuildingById(id, dto);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a Building.", description = "Delete a Building in the database by ID.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Building deleted successfully.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "Bad request. Please submit a valid request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Permission denied. You don't have permission to make this request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteBuildingById(@PathVariable Long id) {
        bService.deleteBuildingById(id);

        return ResponseEntity.noContent().build();
    }
}
