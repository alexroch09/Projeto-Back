package br.edu.lampi.infrareport.controller.dto.building;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BuildingRequestDTO(@NotBlank  @NotNull String name, Set<@NotBlank String> floorNames) {
    
}
