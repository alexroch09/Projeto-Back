package br.edu.lampi.infrareport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.lampi.infrareport.model.callstatus.CallStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Repository
public interface CallStatusRepository extends JpaRepository<CallStatus, Long>{

    boolean existsByCallStatusName(@NotNull @NotBlank String callStatusName);
    
}
