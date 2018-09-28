package custis.easyabac.api;


import custis.easyabac.pdp.AuthResponse;

public class NotExpectedResultException extends RuntimeException {

    public NotExpectedResultException(AuthResponse.AuthResult result, String message) {
        super(message);
    }

    public NotExpectedResultException(AuthResponse.AuthResult result, String message, Throwable cause) {
        super(message, cause);
    }

    public NotExpectedResultException(AuthResponse.AuthResult result, Throwable cause) {
        super(cause);
    }

}
