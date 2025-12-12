package br.edu.lampi.infrareport.controller.dto.building;

import java.util.List;

import br.edu.lampi.infrareport.model.building.Building;
import br.edu.lampi.infrareport.model.floor.Floor;

public record BuildingResponseDTO(Long id, String name, List<Floor> floors) {
    public BuildingResponseDTO(Building b) {
        this(b.getId(), b.getName(), b.getFloors());
    }
}
