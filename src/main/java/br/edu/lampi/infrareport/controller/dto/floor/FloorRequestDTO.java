package br.edu.lampi.infrareport.controller.dto.floor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FloorRequestDTO(@NotNull @NotBlank String name, @NotNull Long buildingId) {
    
}
