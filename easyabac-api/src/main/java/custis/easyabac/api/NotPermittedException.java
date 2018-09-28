package custis.easyabac.api;

import custis.easyabac.pdp.AuthResponse;

public class NotPermittedException extends NotExpectedResultException {

    public NotPermittedException(String message) {
        super(AuthResponse.AuthResult.PERMIT, message);
    }

    public NotPermittedException(String message, Throwable cause) {
        super(AuthResponse.AuthResult.PERMIT, message, cause);
    }

    public NotPermittedException(Throwable cause) {
        super(AuthResponse.AuthResult.PERMIT, cause);
    }

}
