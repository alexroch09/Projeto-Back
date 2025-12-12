package br.edu.lampi.infrareport.service.exceptions;

public class TokenPasswordResetException extends RuntimeException {
    public TokenPasswordResetException(String details) {
        super(details);
    }

    public TokenPasswordResetException() {

    }
}
