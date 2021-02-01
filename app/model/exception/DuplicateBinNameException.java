package model.exception;

/**
 * Indicates same {@link model.annotation.Bin} name has been used multiple times in a object to be saved in Aerospike.
 * 
 */
public class DuplicateBinNameException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 4474231508925302781L;

    public DuplicateBinNameException() {
        super();
    }

    public DuplicateBinNameException(String message) {
        super(message);
    }

    public DuplicateBinNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
