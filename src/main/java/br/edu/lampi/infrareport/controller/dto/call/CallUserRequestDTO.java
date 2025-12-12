package br.edu.lampi.infrareport.controller.dto.call;

import java.time.LocalDateTime;


import jakarta.validation.constraints.NotNull;
public record CallUserRequestDTO(@NotNull String title, @NotNull String description, LocalDateTime dateTime, Long userId, @NotNull Long floorId) {}

