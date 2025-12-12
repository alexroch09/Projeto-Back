package br.edu.lampi.infrareport.controller.dto.callComment;

import java.time.LocalDateTime;

import org.springframework.lang.NonNull;

public record CallCommentRequestDTO(@NonNull Long userId, @NonNull Long call_id, LocalDateTime dateTime, String message, boolean viewed) {}
