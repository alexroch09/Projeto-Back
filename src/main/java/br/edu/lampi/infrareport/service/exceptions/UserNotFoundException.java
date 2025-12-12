package br.edu.lampi.infrareport.service.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String details) {
        super(details);
    }

    public UserNotFoundException() {
        super();
    }
}
