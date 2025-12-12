package br.edu.lampi.infrareport.model.callstatus;

import br.edu.lampi.infrareport.controller.dto.callStatus.CallStatusRequestDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "call_status")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CallStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status_name")
    private String callStatusName;

    public CallStatus(CallStatusRequestDTO callStatusRequestDTO) {
        this.callStatusName = callStatusRequestDTO.callStatusName();
    }

}
