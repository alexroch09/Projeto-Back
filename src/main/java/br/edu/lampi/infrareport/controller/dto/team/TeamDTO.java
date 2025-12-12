package br.edu.lampi.infrareport.controller.dto.team;

import br.edu.lampi.infrareport.model.team.Team;

public record TeamDTO(Long id, String name) {
    public TeamDTO(Team e){
        this(e.getId(), e.getName());
    }
}
