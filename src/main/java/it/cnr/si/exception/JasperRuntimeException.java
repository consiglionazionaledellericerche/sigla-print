package it.cnr.si.exception;

/**
 * Created by francesco on 09/09/16.
 */
public class JasperRuntimeException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JasperRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
