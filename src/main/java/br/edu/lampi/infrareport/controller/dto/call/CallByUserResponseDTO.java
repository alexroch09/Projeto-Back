package br.edu.lampi.infrareport.controller.dto.call;

import br.edu.lampi.infrareport.controller.dto.callImage.CallImageResponseDTO;
import br.edu.lampi.infrareport.controller.dto.callStatus.CallStatusResponseDTO;
import br.edu.lampi.infrareport.model.call.Call;
import br.edu.lampi.infrareport.model.callComment.CallComment;
import br.edu.lampi.infrareport.model.callImage.CallImage;
import br.edu.lampi.infrareport.model.callstatus.CallStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CallByUserResponseDTO(Long id, String title, String description, LocalDateTime dateTime, CallStatusResponseDTO callStatus, CallImageResponseDTO callImages, Boolean active, String justification, int numberUnseenComments) {
    public CallByUserResponseDTO(Call call){
        this(call.getId(),
                call.getTitle(),
                call.getDescription(),
                call.getDateTime(),
                (call.getCallStatus() == null) ? null : new CallStatusResponseDTO(call.getCallStatus()),
                (call.getImages() == null || call.getImages().isEmpty()) ? null : new CallImageResponseDTO(call.getImages().get(0)),
                call.getActive(),
                call.getJustification(),
                call.getComments().size());
    }
}
