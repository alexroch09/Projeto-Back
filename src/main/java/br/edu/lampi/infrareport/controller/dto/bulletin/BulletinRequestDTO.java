package br.edu.lampi.infrareport.controller.dto.bulletin;

import jakarta.validation.constraints.NotBlank;

public record BulletinRequestDTO(
        @NotBlank String title,
        @NotBlank String message
) {}