package br.edu.lampi.infrareport.controller.dto.building;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BuildingFloorUpdateDTO(Long id, @NotNull @NotBlank String name) {
}
