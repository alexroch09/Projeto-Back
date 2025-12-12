package br.edu.lampi.infrareport.controller.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ValidatePasswordResetToken(
        @NotNull
        int token,
        @NotBlank
        @NotEmpty
        @Email
        String email
) {
}
