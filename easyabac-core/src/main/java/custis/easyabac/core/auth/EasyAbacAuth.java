package custis.easyabac.core.auth;

public interface EasyAbacAuth {
    EasyAbacResponse auth(EasyAbacRequest request);
}
