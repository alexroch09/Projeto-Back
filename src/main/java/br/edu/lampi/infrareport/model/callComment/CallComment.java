package br.edu.lampi.infrareport.model.callComment;

import java.time.LocalDateTime;

import br.edu.lampi.infrareport.model.call.Call;
import br.edu.lampi.infrareport.model.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "call_comment")
@Entity
public class CallComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST)
    private User user;

    @ManyToOne
    @JsonBackReference
    private Call call;

    private LocalDateTime dateTime;

    private String message;

    private Boolean viewed;

}
