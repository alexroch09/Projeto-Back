package br.edu.lampi.infrareport.service.exceptions;

public class EmailAlreadyRegisteredException extends RuntimeException{
    public EmailAlreadyRegisteredException(String details) {
        super(details);
    }
}
