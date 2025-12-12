package br.edu.lampi.infrareport.controller.dto.call;

import java.time.LocalDateTime;

import br.edu.lampi.infrareport.model.call.Call;

public record CallUserResponseDTO(Long id, String title, String description, LocalDateTime dateTime, String userName, String building, String floor, String firstImageUrl, String status, String category, String priority, String teamResponsible) {
    public CallUserResponseDTO(Call c){
        this(c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getDateTime(),
                c.getUser().getName(),
                c.getFloor().getBuilding().getName(),
                c.getFloor().getName(),
                (c.getImages() == null || c.getImages().isEmpty()) ? null : c.getImages().get(0).getPath(),
                c.getCallStatus() != null ? c.getCallStatus().getCallStatusName() : null,
                c.getCategory() != null ? c.getCategory().getName() : null,
                c.getPriority() != null ? c.getPriority().name() : null,
                c.getTeam() != null ? c.getTeam().getName() : null);
    }
}