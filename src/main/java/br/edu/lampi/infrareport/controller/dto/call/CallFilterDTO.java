package br.edu.lampi.infrareport.controller.dto.call;

import java.time.LocalDateTime;
import java.util.Set;

import br.edu.lampi.infrareport.model.call.CallPriority;

public record CallFilterDTO(LocalDateTime initDateTime,
                            LocalDateTime endDateTime,
                            Set<Long> teamIdList,
                            Set<Long> categoryIdList,
                            Set<Long> callStatusIdList,
                            Set<Long> floorIdList,
                            Set<CallPriority> callPriorityList,
                            Boolean active,
                            Set<Long> userIdList,
                            Boolean classified) {

    public CallFilterDTO(){
        this(null, null, null, null, null, null, null, null, null, null);
    }
}