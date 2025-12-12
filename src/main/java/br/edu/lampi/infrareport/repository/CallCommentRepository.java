package br.edu.lampi.infrareport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.lampi.infrareport.model.callComment.CallComment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public interface CallCommentRepository extends JpaRepository <CallComment, Long> {

    List<CallComment> findByCallId(@NotNull @NotBlank Long callId);

    boolean existsByCallId(@NotNull @NotBlank Long callId);

}
