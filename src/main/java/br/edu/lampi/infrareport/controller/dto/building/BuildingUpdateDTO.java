package br.edu.lampi.infrareport.controller.dto.building;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BuildingUpdateDTO(@NotBlank @NotNull String name, @Valid List<BuildingFloorUpdateDTO> floorUpdateDTO) {
    
}
