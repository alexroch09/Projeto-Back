package br.edu.lampi.infrareport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.lampi.infrareport.model.team.Team;

public interface TeamRepository extends JpaRepository<Team, Long>{
    
}
