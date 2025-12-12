package br.edu.lampi.infrareport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.edu.lampi.infrareport.controller.dto.floor.FloorRequestDTO;
import br.edu.lampi.infrareport.controller.dto.floor.FloorResponseDTO;
import br.edu.lampi.infrareport.model.building.Building;
import br.edu.lampi.infrareport.model.floor.Floor;
import br.edu.lampi.infrareport.repository.BuildingRepository;
import br.edu.lampi.infrareport.repository.FloorRepository;
import br.edu.lampi.infrareport.service.exceptions.ConflictException;
import br.edu.lampi.infrareport.service.exceptions.ResourceNotFoundException;

@Service
public class FloorService {
    @Autowired
    private FloorRepository fRepository;

    @Autowired
    private BuildingRepository bRepository;

    public Floor getFloorByID(Long id) {
        return fRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Floor not found with the given ID"));
    }

    public Page<FloorResponseDTO> getFloorsPageable(Pageable pageable) {
            return fRepository.findAll(pageable).map(FloorResponseDTO::new);
    }

    public boolean existById(Long id) {
        if (id != null) {
            return fRepository.existsById(id);
        }
        return false;
    }

    public Floor saveNewFloor(Floor floor) {
        Building building = bRepository.findById(floor.getBuilding().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Building not found with id: " + floor.getBuilding().getId()));

        Floor existingFloor = fRepository.findByNameAndBuildingId(floor.getName(), building.getId());
        if (existingFloor != null) {
            throw new ConflictException("There is already a floor with this name.");
        }

        floor.setBuilding(building);
        return fRepository.save(floor);
    }

    public void updateFloorById(Long id, FloorRequestDTO dto) {
            Floor floor = fRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Floor not found with id: " + id));

            Floor conflictingFloor = fRepository.findByNameAndBuildingId(dto.name(), dto.buildingId());
            if (conflictingFloor != null && !conflictingFloor.getId().equals(id)) {
                throw new ConflictException("A Floor with a similar name already exists.");
            }

            Building building = bRepository.findById(dto.buildingId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Building not found with id: " + dto.buildingId()));
                            
            floor.setBuilding(building);
            floor.setName(dto.name());
            fRepository.save(floor);

    }

    public void deleteFloorById(Long id) {
        if (id != null && fRepository.existsById(id)) {
            fRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException(
                    "It was not possible to exclude this floor or it does not exist.");
        }
    }

}
