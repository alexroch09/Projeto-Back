package br.edu.lampi.infrareport.model.building;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.edu.lampi.infrareport.controller.dto.building.BuildingRequestDTO;
import br.edu.lampi.infrareport.controller.dto.building.BuildingResponseDTO;
import br.edu.lampi.infrareport.model.floor.Floor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "building")
@Entity
@JsonIgnoreProperties("floors")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL)
    private List<Floor> floors;

    public Building(BuildingResponseDTO dto) {
        this.setId(dto.id());
        this.setName(dto.name());
        this.setFloors(dto.floors());
    }

    public Building(BuildingRequestDTO dto) {
        this.setName(dto.name());
        this.setFloors(dto.floorNames() != null ? dto.floorNames()
            .stream()
            .map(floorName -> new Floor(null, floorName, this))
            .collect(Collectors.toList())
            : new ArrayList<>());
    }

}
