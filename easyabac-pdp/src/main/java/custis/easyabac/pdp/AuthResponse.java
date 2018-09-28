package custis.easyabac.pdp;

public class AuthResponse {

    public AuthResult getResult() {
        return AuthResult.PERMIT;
    }

    public enum AuthResult {
        PERMIT, DENY, NOT_APPLICABLE, INDETERMINATE;
    }
}
