package br.edu.lampi.infrareport.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.edu.lampi.infrareport.controller.dto.building.BuildingFloorUpdateDTO;
import br.edu.lampi.infrareport.controller.dto.building.BuildingResponseDTO;
import br.edu.lampi.infrareport.controller.dto.building.BuildingUpdateDTO;
import br.edu.lampi.infrareport.model.building.Building;
import br.edu.lampi.infrareport.model.floor.Floor;
import br.edu.lampi.infrareport.repository.BuildingRepository;
import br.edu.lampi.infrareport.repository.FloorRepository;
import br.edu.lampi.infrareport.service.exceptions.BadRequestException;
import br.edu.lampi.infrareport.service.exceptions.ConflictException;
import br.edu.lampi.infrareport.service.exceptions.ResourceNotFoundException;

@Service
public class BuildingService {

    @Autowired
    private BuildingRepository bRepository;

    @Autowired
    private FloorRepository fRepository;

    public Building getBuildingByID(Long id) {
            return bRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with the given ID"));

    }

    public Page<BuildingResponseDTO> getBuildingsPageable(Pageable pageable) {
            return bRepository.findAll(pageable).map(BuildingResponseDTO::new);
    }

    public boolean existById(Long id) {
        if (id != null) {
            return bRepository.existsById(id);
        }
        return false;
    }

    public Building saveNewBuilding(Building b) {
        Building existingBuilding = bRepository.findByName(b.getName());
        if (existingBuilding != null) {
            throw new ConflictException("There is already a building with this name.");
        }

        Building savedBuilding = bRepository.save(b);

        return savedBuilding;
    }

    public void updateBuildingById(Long id, BuildingUpdateDTO dto) {
            Building b = this.getBuildingByID(id);

            validateUpdateBuildingNameConflict(dto.name(), id);

            if (dto.floorUpdateDTO() != null) {
                validateFloorNames(dto.floorUpdateDTO());

                List<Floor> floorsToRemove = getAbsentFloors(b.getFloors(), dto.floorUpdateDTO());
                List<Floor> updatedFloors = updateExistentFloorsAndRemoveThemFromFloorUpdateDTO(dto.floorUpdateDTO(), b);
                b.setFloors(updatedFloors);
                
                List<Floor> newFloors = addNewFloors(dto.floorUpdateDTO(), b);
                b.getFloors().addAll(newFloors);
                b.getFloors().forEach(f -> f.setBuilding(b));
                this.fRepository.deleteAll(floorsToRemove);
                }else{
                    this.fRepository.deleteAll(b.getFloors());
                    b.setFloors(null);
                }

            b.setName(dto.name());

            bRepository.save(b);
    }

    public void deleteBuildingById(Long id) {
            bRepository.delete(this.getBuildingByID(id));
    }

    private void validateFloorNames(List<BuildingFloorUpdateDTO> floorUpdateDTOs) {
        Set<String> floorNames = floorUpdateDTOs.stream()
                .map(BuildingFloorUpdateDTO::name)
                .collect(Collectors.toSet());

        if (floorNames.size() != floorUpdateDTOs.size()) {
            throw new BadRequestException("There are repeated floor names");
        }
    }

    private List<Floor> getAbsentFloors(List<Floor> oldFloors, List<BuildingFloorUpdateDTO> floorUpdateDTO) {
        return oldFloors.stream()
                        .filter(floor -> floorUpdateDTO.stream()
                                                            .noneMatch(f -> Objects.equals(floor.getId(), f.id())))
                        .collect(Collectors.toList());
    }

    private void validateUpdateBuildingNameConflict(String name, Long id) {
        Building conflictingBuilding = bRepository.findByName(name);
        if (conflictingBuilding != null && !conflictingBuilding.getId().equals(id)) {
            throw new ConflictException("A Building with a similar name already exists.");
        }
    }

    private List<Floor> updateExistentFloorsAndRemoveThemFromFloorUpdateDTO(List<BuildingFloorUpdateDTO> floorUpdateDTO, Building building) {
        List<Floor> updatedFloors = new ArrayList<>();
        List<BuildingFloorUpdateDTO> existentFloors = floorUpdateDTO.stream()
                                                            .filter(f -> f.id() != null)
                                                            .collect(Collectors.toList());

        for(BuildingFloorUpdateDTO floorUpdate :existentFloors){
            Floor floor = building.getFloors()
                                        .stream()
                                        .filter(f -> floorUpdate.id().equals(f.getId()))
                                        .findFirst()
                                        .orElseThrow(() -> new ResourceNotFoundException("Floor not found with the id " + floorUpdate.id()));

                floor.setName(floorUpdate.name());
                updatedFloors.add(floor);
        }

        floorUpdateDTO.removeAll(existentFloors);

        return updatedFloors;
    }

    private List<Floor> addNewFloors(List<BuildingFloorUpdateDTO> newFloors, Building building) {
        List<Floor> newFloorsToBeAdded = new ArrayList<>();

        for(BuildingFloorUpdateDTO floorUpdate : newFloors){
            boolean floorAlreadyExists = building.getFloors().stream().anyMatch(f -> f.getName().equals(floorUpdate.name()));

            if(floorAlreadyExists){
                throw new ConflictException("a floor with name " + floorUpdate.name() + "already exists at this buildin");
            }

            Floor floor = new Floor(null, floorUpdate.name(), null);
            newFloorsToBeAdded.add(floor);
        }

        return newFloorsToBeAdded;
    }

    public Building findByName(String name) {
        return bRepository.findByName(name);
    }

}
