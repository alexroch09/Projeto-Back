package br.edu.lampi.infrareport.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.lampi.infrareport.model.floor.Floor;


public interface FloorRepository extends JpaRepository<Floor, Long>{

    Floor findByName(String name);

    Floor findByNameAndBuildingId(String name, Long buildingId);

}
