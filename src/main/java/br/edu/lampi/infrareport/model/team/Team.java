package br.edu.lampi.infrareport.model.team;

import br.edu.lampi.infrareport.controller.dto.team.TeamDTO;
import br.edu.lampi.infrareport.controller.dto.team.TeamNoIdDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "team")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    public Team(TeamNoIdDTO data) {
        this.setName(data.name());
    }

    public Team(TeamDTO data) {
        this.setId(data.id());
        this.setName(data.name());
    }

    public void update(TeamNoIdDTO data) {
        this.setName(data.name());
    }

}
