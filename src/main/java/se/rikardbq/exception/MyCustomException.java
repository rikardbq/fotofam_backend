package se.rikardbq.exception;

public class MyCustomException extends RuntimeException {

    public MyCustomException(String entity) {
        super("STATUS=204,NO-CONTENT", new Throwable("HTTP response contained no body for entity=" + entity));
    }
}
