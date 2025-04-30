package se.rikardbq.exception;

public class SerfConnectorException extends RuntimeException {

    public SerfConnectorException(Exception e) {
        super(e.getMessage(), e.getCause());
    }
}
