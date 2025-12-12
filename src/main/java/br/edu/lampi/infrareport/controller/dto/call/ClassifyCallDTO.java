package br.edu.lampi.infrareport.controller.dto.call;

import br.edu.lampi.infrareport.model.call.CallPriority;

public record ClassifyCallDTO(Long teamId, 
                                Long categoryId, 
                                Long callStatusId, 
                                CallPriority priority) {
}
