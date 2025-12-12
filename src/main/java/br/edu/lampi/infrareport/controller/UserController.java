package br.edu.lampi.infrareport.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.edu.lampi.infrareport.controller.dto.user.UserRequestDTO;
import br.edu.lampi.infrareport.controller.dto.user.UserResponseDTO;
import br.edu.lampi.infrareport.model.user.User;
import br.edu.lampi.infrareport.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "This is api of User.")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "return a user.", description = "Returns a user in database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User returned successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "User not found with the given id.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> searchById(@PathVariable Long id){
        return ResponseEntity.ok(new UserResponseDTO(this.userService.searchById(id)));
    }

    @Operation(summary = "save a new user.", description = "record a new user in database.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User saved successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request. You may have submitted some invalid data.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "409", description = "User email is already in use.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            })
    @PostMapping
    @Transactional
    public ResponseEntity<UserResponseDTO> save(@RequestBody @Valid UserRequestDTO data, UriComponentsBuilder uriBuilder){
        User savedUser = this.userService.save(data);

        URI uri = uriBuilder.path("/user/{id}").buildAndExpand(savedUser.getId()).toUri();

        return ResponseEntity.created(uri).body(new UserResponseDTO(savedUser));
    }

    @Operation(summary = "update a user.", description = "update a user by id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "users deleted successfully."),
                    @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request. You may have submitted some invalid data.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "404", description = "User not found with the given id.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "409", description = "User email is already in use.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            })

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @RequestBody UserRequestDTO userRequestDTO){
        return ResponseEntity.ok(new UserResponseDTO(this.userService.update(id, userRequestDTO)));
    }

    @Operation(summary = "delete a user.", description = "delete a user by id from database.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request. You may have submitted some invalid data.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
            })

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}