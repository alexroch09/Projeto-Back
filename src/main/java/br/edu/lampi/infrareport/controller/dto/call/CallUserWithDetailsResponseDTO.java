package br.edu.lampi.infrareport.controller.dto.call;

import br.edu.lampi.infrareport.model.call.Call;
import br.edu.lampi.infrareport.model.call.CallPriority;
import br.edu.lampi.infrareport.model.callComment.CallComment;
import br.edu.lampi.infrareport.model.callImage.CallImage;
import br.edu.lampi.infrareport.model.floor.Floor;

import java.time.LocalDateTime;
import java.util.List;

public record CallUserWithDetailsResponseDTO(Long callId, String title, String description, LocalDateTime dateTime,
                                             Long userID, Long categoryID, Floor floor, Long callStatusID,
                                             List<Long> imagesID, List<Long> commentsID,
                                             CallPriority callPriority, Long teamID, Boolean active,
                                             String justification) {
    public CallUserWithDetailsResponseDTO(Call c) {
        this(
                c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getDateTime(),
                c.getUser().getId(),
                c.getCategory() != null ? c.getCategory().getId() : null,
                c.getFloor(),
                c.getCallStatus() != null ? c.getCallStatus().getId() : null,
                c.getImages().stream().map(CallImage::getId).toList(),
                c.getComments().stream().map(CallComment::getId).toList(),
                c.getPriority(),
                c.getTeam() != null ? c.getTeam().getId() : null,
                c.getActive(),
                c.getJustification());
    }
}