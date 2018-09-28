package custis.easyabac.core.auth;

public class EasyAbacRequest {
    private final String xacmlRequest;

    public EasyAbacRequest(String xacmlRequest) {
        this.xacmlRequest = xacmlRequest;
    }

    public String getXacmlRequest() {
        return xacmlRequest;
    }
}
