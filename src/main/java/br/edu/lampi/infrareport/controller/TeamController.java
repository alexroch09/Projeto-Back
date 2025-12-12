package br.edu.lampi.infrareport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.lampi.infrareport.controller.dto.team.TeamDTO;
import br.edu.lampi.infrareport.controller.dto.team.TeamNoIdDTO;
import br.edu.lampi.infrareport.model.team.Team;

import br.edu.lampi.infrareport.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/teams")
@Tag(name = "Teams", description = "This is api of Teams.")
public class TeamController {
    @Autowired
    private TeamService tService;

    @Operation(summary = "Returns a teams page.", description = "Returns a team page.", 
        responses = {
            @ApiResponse(responseCode = "200", description = "Page returned successfully."),
            @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @GetMapping
    public Page<TeamDTO> getAllTeams(@PageableDefault(sort = {"name"}, size = 10) Pageable pageable) throws Exception{
        if(pageable != null){
            return tService.getTeamsPageable(pageable);
        } else {
            throw new Exception("Pageable NULL.");
        }
    }

    @Operation(summary = "Returns a team by id.", description = "Returns team by the given id.", 
    responses = {
        @ApiResponse(responseCode = "200", description = "Team returned successfully."),
        @ApiResponse(responseCode = "204", description = "Team not found with the given id.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @GetMapping("/{id}")
    public TeamDTO getTeam(@PathVariable Long id, HttpServletResponse res){
        if(id != null && tService.existsById(id)){
            Team t = tService.getTeamsById(id);
            TeamDTO td = new TeamDTO(t);
            return td;
        }
        res.setStatus(204);
        return null;
    }

    @Operation(summary = "create a new team.", description = "Returns a created team.", 
    responses = {
        @ApiResponse(responseCode = "200", description = "Team returned successfully."),
        @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @PostMapping
    @Transactional
    public TeamDTO createTeam(@RequestBody TeamNoIdDTO data){
        Team t = new Team(data);
        return new TeamDTO(tService.saveTeam(t));
    }

    @Operation(summary = "delete a team by id.", description = "delete team by the given id.", 
    responses = {
        @ApiResponse(responseCode = "200", description = "Team deleted successfully."),
        @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @DeleteMapping("/{id}")
    @Transactional
    public void deleteTeam(@PathVariable Long id){
        if(id != null){
            tService.deleteTeamById(id);
        }
    }

    @Operation(summary = "update a team.", description = "update a team by the given id.", 
    responses = {
        @ApiResponse(responseCode = "200", description = "Team returned successfully."),
        @ApiResponse(responseCode = "400", description = "Bad request. Please, submit a valide request.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Permission denied. you don't have the permission to do this request.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error! Sorry, something went wrong.", content = @Content(schema = @Schema(hidden = true)))
    })

    @PutMapping("/{id}")
    @Transactional
    public void updateTeam(@PathVariable Long id, @RequestBody TeamNoIdDTO data){
         if(id != null){
            tService.updateTeamById(id, data);
        }
    }
}
