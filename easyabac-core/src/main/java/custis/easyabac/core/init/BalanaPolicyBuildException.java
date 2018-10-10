package custis.easyabac.core.init;

/**
 * Raised if something went wrong in mapping from Abac domain object to Balana policy.
 */
public class BalanaPolicyBuildException extends RuntimeException {
    public BalanaPolicyBuildException(String message) {
        super(message);
    }
}