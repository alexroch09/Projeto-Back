package br.edu.lampi.infrareport.controller.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ForgotPasswordRequestDTO(
        @NotBlank
        @NotEmpty
        @Email
        String email
) {
}
