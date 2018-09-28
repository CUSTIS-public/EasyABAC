package custis.easyabac.pdp;

public class AuthResponse {

    public AuthResult getResult() {
        return AuthResult.DENY;
    }

    public enum AuthResult {
        PERMIT, DENY, NOT_APPLICABLE, INDETERMINATE;
    }
}
