package br.edu.lampi.infrareport.controller.dto.user;

import br.edu.lampi.infrareport.model.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(
        @NotNull
        @NotBlank
        String name,
        @NotNull
        @NotBlank
        String email,
        @NotNull
        @NotBlank
        String password){
    public UserRequestDTO(User user) {
        this(user.getName(), user.getEmail(), user.getPassword());
    }
}
