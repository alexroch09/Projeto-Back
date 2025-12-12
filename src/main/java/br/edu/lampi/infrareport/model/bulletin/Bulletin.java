package br.edu.lampi.infrareport.model.bulletin;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bulletins")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Bulletin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bulletin_title")
    private String title;

    @Column(name = "bulletin_message")
    private String message;

    @Column(name = "bulletin_date_time")
    private LocalDateTime dateTime;

}