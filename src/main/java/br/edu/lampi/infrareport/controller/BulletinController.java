package br.edu.lampi.infrareport.controller;

import br.edu.lampi.infrareport.controller.dto.bulletin.BulletinRequestDTO;
import br.edu.lampi.infrareport.controller.dto.bulletin.BulletinResponseDTO;
import br.edu.lampi.infrareport.model.bulletin.Bulletin;
import br.edu.lampi.infrareport.service.BulletinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bulletins")
@Tag(name = "Bulletins", description = "This is the API of Bulletins.")
public class BulletinController {

    private final BulletinService bulletinService;

    public BulletinController(BulletinService bulletinService) {
        this.bulletinService = bulletinService;
    }

    @PostMapping
    @Operation(
        summary = "Save a new bulletin.",
        description = "Record a new bulletin in the database and return the recorded bulletin.",
        responses = {
                @ApiResponse(responseCode = "200", description = "Bulletin saved successfully.", content = @Content(schema = @Schema(implementation = Bulletin.class))),
                @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valid request.", content = @Content(schema = @Schema(hidden = true))),
                @ApiResponse(responseCode = "401", description = "Unauthorized. You need to authenticate to do this request.", content = @Content(schema = @Schema(hidden = true))),
                @ApiResponse(responseCode = "403", description = "Permission denied. You don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    public ResponseEntity<Bulletin> saveBulletin(@RequestBody @Valid BulletinRequestDTO bulletinDTO) {
        Bulletin bulletin = new Bulletin();
        BeanUtils.copyProperties(bulletinDTO, bulletin);
        bulletin.setDateTime(LocalDateTime.now());
        return ResponseEntity.ok(bulletinService.create(bulletin));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a bulletin by id.",
            description = "Returns a bulletin by the given id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bulletin found and returned successfully", content = @Content(schema = @Schema(implementation = Bulletin.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized. You need to authenticate to do this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Bulletin not found with the given id.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))

            }
    )
    public ResponseEntity<Optional<Bulletin>> getBulletinById(@PathVariable Long id) {
        return ResponseEntity.ok(bulletinService.readById(id));
    }

    @GetMapping
    @Operation(
            summary = "Get all bulletins.",
            description = "Returns all bulletins.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All bulletins returned successfully", content = @Content(schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized. You need to authenticate to do this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    public ResponseEntity<Page<BulletinResponseDTO>> getAllBulletins(@PageableDefault() Pageable pageable) {
        Pageable sortedByIdDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").descending());
        Page<BulletinResponseDTO> page = bulletinService.readAll(sortedByIdDesc);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a bulletin.",
            description = "Update a bulletin by the given id.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Bulletin updated successfully", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valid request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized. You need to authenticate to do this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Permission denied. You don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Bulletin not found with the given id.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    public ResponseEntity<Void> updateBulletin(@PathVariable Long id, @RequestBody @Valid BulletinRequestDTO bulletinDTO) {
        Bulletin bulletin = bulletinService.readById(id).orElseThrow();
        BeanUtils.copyProperties(bulletinDTO, bulletin);
        bulletinService.update(bulletin);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PutMapping("/{id}")
    @Operation(
            summary = "Delete a bulletin.",
            description = "Delete a bulletin by the given id.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Bulletin deleted successfully", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valid request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized. You need to authenticate to do this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Permission denied. You don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "Bulletin not found with the given id.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            }
    )
    public ResponseEntity<Void> deleteBulletin(@PathVariable Long id) {
        Bulletin bulletin = bulletinService.readById(id).orElseThrow();
        bulletinService.delete(bulletin);
        return ResponseEntity.noContent().build();
    }
}