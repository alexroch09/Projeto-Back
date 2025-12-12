package br.edu.lampi.infrareport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.lampi.infrareport.model.callImage.CallImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface CallImageRepository extends JpaRepository <CallImage, Long> {
    boolean existsByPath(@NotNull @NotBlank String path);

    List<CallImage> findByCallId(@NotNull @NotBlank Long callId);

    boolean existsByCallId(@NotNull @NotBlank Long callId);
}
