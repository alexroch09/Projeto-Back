package br.edu.lampi.infrareport.controller.dto.callComment;

import java.time.LocalDateTime;

import br.edu.lampi.infrareport.model.callComment.CallComment;

public record CallCommentResponseDTO(Long id, Long userId, Long call_id, LocalDateTime dateTime, String message, Boolean viewed) {
    public CallCommentResponseDTO(CallComment c){
        this(c.getId(), c.getUser().getId(), c.getCall().getId(), c.getDateTime(), c.getMessage(), c.getViewed());
    }
}
