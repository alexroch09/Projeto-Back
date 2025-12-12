package br.edu.lampi.infrareport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.lampi.infrareport.model.building.Building;


public interface BuildingRepository extends JpaRepository<Building, Long> {
    
    Building findByName(String name);

}
