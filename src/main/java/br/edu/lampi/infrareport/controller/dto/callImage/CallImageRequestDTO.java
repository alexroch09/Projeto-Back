package br.edu.lampi.infrareport.controller.dto.callImage;

public class CallImageRequestDTO {

    private String path;
    private String fileName;
    private Long callId;

    public CallImageRequestDTO() {
        
    }

    public CallImageRequestDTO(String path, String fileName, Long callId) {
        this.path = path;
        this.fileName = fileName;
        this.callId = callId;
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
