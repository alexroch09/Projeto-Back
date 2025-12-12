package br.edu.lampi.infrareport.controller.dto.bulletin;

import br.edu.lampi.infrareport.model.bulletin.Bulletin;

import java.time.LocalDateTime;

public record BulletinResponseDTO(
        Long id,
        String title,
        String message,
        LocalDateTime dateTime
) {
    public BulletinResponseDTO(Bulletin bulletin) {
        this(bulletin.getId(), bulletin.getTitle(), bulletin.getMessage(), bulletin.getDateTime());
    }
}