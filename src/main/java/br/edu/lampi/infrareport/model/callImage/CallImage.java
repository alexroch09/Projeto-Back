package br.edu.lampi.infrareport.model.callImage;

import br.edu.lampi.infrareport.model.call.Call;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Entity 
@Table(name = "call_image")
public class CallImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;

    private String fileName;

    @Column(name = "call_id")
    private Long callId;

    @ManyToOne
    @JoinColumn(name = "call_id", insertable = false, updatable = false)
    @JsonBackReference
    private Call call;

    // Construtores
    public CallImage() {}

    public CallImage(String path, String fileName, Long callId) {
        this.path = path;
        this.fileName = fileName;
        this.callId = callId;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
        this.callId = callId;
    }
}