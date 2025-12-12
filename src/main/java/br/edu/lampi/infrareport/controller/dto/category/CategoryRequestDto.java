package br.edu.lampi.infrareport.controller.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequestDto(@NotNull @NotBlank String name){}
