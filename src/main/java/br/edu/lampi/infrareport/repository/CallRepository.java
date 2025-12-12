package br.edu.lampi.infrareport.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import br.edu.lampi.infrareport.model.call.Call;

@Repository
public interface CallRepository extends JpaRepository <Call, Long>, JpaSpecificationExecutor<Call>{
    boolean existsByCallStatusId(Long id);

    Page<Call> findByUserId(Long userId, Pageable pageable);
}
