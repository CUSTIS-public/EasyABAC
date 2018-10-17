package custis.easyabac.api;


import custis.easyabac.pdp.AuthResponse;

public class NotExpectedResultException extends RuntimeException {

    public NotExpectedResultException(AuthResponse.Decision result, String message) {
        super(message);
    }

    public NotExpectedResultException(AuthResponse.Decision result, String message, Throwable cause) {
        super(message, cause);
    }

    public NotExpectedResultException(AuthResponse.Decision result, Throwable cause) {
        super(cause);
    }

}
