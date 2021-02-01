package model.exception;


/**
 * Indicates no {@link model.annotation.Key} is defined for a object to be saved in Aerospike.
 * 
 */
public class KeyNotDefinedException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 4474231508925302781L;

    public KeyNotDefinedException() {
        super();
    }

    public KeyNotDefinedException(String message) {
        super(message);
    }

    public KeyNotDefinedException(String message, Throwable cause) {
        super(message, cause);
    }
}
