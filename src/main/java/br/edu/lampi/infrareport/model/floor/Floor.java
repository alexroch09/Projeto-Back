package br.edu.lampi.infrareport.model.floor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.edu.lampi.infrareport.controller.dto.floor.FloorRequestDTO;
import br.edu.lampi.infrareport.controller.dto.floor.FloorResponseDTO;
import br.edu.lampi.infrareport.model.building.Building;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "floor")
@Entity
@JsonIgnoreProperties("building")
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    public Floor(FloorResponseDTO dto) {
        this.setId(dto.id());
        this.setName(dto.name());
        this.setBuilding(dto.building());
    }

    public Floor(FloorRequestDTO dto) {
        this.setName(dto.name());
        this.setBuilding(new Building(dto.buildingId(), null, null));
    }

    public Floor(Floor floor) {
        this.setId(floor.getId());
        this.setBuilding(floor.getBuilding());
        this.setBuilding(floor.getBuilding());
    }
    
    public Floor(String name) {
        this.name = name;
    }

}
