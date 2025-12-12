package br.edu.lampi.infrareport.model.call;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import br.edu.lampi.infrareport.model.callstatus.CallStatus;
import br.edu.lampi.infrareport.model.category.Category;
import br.edu.lampi.infrareport.model.callComment.CallComment;
import br.edu.lampi.infrareport.model.callImage.CallImage;
import br.edu.lampi.infrareport.model.floor.Floor;
import br.edu.lampi.infrareport.model.team.Team;
import br.edu.lampi.infrareport.model.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "call_records")
@Entity
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    @ManyToOne
    private User user;
    
    @ManyToOne
    private Category category;

    @NotNull
    @ManyToOne
    private Floor floor;
    
    @ManyToOne
    private CallStatus callStatus;

    @ManyToOne
    private Team team;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "call")
    @JsonManagedReference
    private List<CallImage> images = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "call")
    @JsonManagedReference
    private List<CallComment> comments = new ArrayList<>();

    @Enumerated(EnumType.ORDINAL)
    private CallPriority priority;

    private Boolean active;

    private String justification;

    public List<CallImage> getImages() {
        if(images == null) {
            images = new ArrayList<>();
        }

        return images;
    }
}
