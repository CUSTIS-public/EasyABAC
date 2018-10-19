package custis.easyabac.starter;

import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.api.impl.EasyABACPermissionChecker;
import custis.easyabac.core.pdp.AttributiveAuthorizationService;

public class PermitAwareCheckerHelper {

    public static <T, A> PermitAwarePermissionChecker<T, A> newInstance(AttributiveAuthorizationService service) {
        return new EasyABACPermissionChecker<>(service);
    }
}
