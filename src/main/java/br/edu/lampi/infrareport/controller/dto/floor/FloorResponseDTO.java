package br.edu.lampi.infrareport.controller.dto.floor;

import br.edu.lampi.infrareport.model.building.Building;
import br.edu.lampi.infrareport.model.floor.Floor;

public record FloorResponseDTO(Long id, String name, Building building) {
    public FloorResponseDTO(Floor f) {
        this(f.getId(), f.getName(), f.getBuilding());
    }
}