package br.edu.lampi.infrareport.controller.dto.callImage;

import br.edu.lampi.infrareport.model.callImage.CallImage;
import lombok.Getter;

@Getter
public class CallImageResponseDTO {
    private Long id;
    private String path;
    private String fileName;
    private Long callId;

    public CallImageResponseDTO(CallImage entity) {
        this.id = entity.getId();
        this.path = entity.getPath();
        this.fileName = entity.getFileName();
        this.callId = entity.getCallId();
    }

}