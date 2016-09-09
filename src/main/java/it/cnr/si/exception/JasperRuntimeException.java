package it.cnr.si.exception;

/**
 * Created by francesco on 09/09/16.
 */
public class JasperRuntimeException extends RuntimeException {
    public JasperRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
