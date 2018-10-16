package custis.easyabac.core;

public class EasyAbacAuthException extends RuntimeException {
    public EasyAbacAuthException(String message) {
        super(message);
    }

    public EasyAbacAuthException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
