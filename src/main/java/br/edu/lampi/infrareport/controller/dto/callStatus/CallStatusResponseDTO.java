package br.edu.lampi.infrareport.controller.dto.callStatus;

import br.edu.lampi.infrareport.model.callstatus.CallStatus;

public record CallStatusResponseDTO(Long id,
    String callStatusName) {
    
    public CallStatusResponseDTO(CallStatus callStatus){
        this(callStatus.getId(), callStatus.getCallStatusName());
    }
}
