package br.edu.lampi.infrareport.controller.dto.callStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CallStatusRequestDTO(@NotNull @NotBlank String callStatusName) {

}
