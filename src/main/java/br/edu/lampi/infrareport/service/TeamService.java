package br.edu.lampi.infrareport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.edu.lampi.infrareport.controller.dto.team.TeamDTO;
import br.edu.lampi.infrareport.controller.dto.team.TeamNoIdDTO;
import br.edu.lampi.infrareport.model.team.Team;
import br.edu.lampi.infrareport.repository.TeamRepository;

@Service
public class TeamService {
    @Autowired
    private TeamRepository tRepo;

    public Team getTeamsById(Long id){
        if(id != null){
            return tRepo.getReferenceById(id);
        }
        return null;
    }
    public boolean existsById(Long id){
        if(id != null){
            return tRepo.existsById(id);
        }
        return false;
    }
    public Page<TeamDTO> getTeamsPageable(Pageable pageable){
        if(pageable != null){
            return tRepo.findAll(pageable).map(TeamDTO::new);
        }
        return null;
    }
    public Team saveTeam(Team t){
        if(t != null){
            return tRepo.save(t);
        }
        return null;
    }

    public void deleteTeamById(Long id){
        if(id != null){
            tRepo.deleteById(id);
        }
    }

    public void updateTeamById(Long id, TeamNoIdDTO updated){
        if(id != null){
            Team t = tRepo.getReferenceById(id);
            t.update(updated);
        }
    }

}
