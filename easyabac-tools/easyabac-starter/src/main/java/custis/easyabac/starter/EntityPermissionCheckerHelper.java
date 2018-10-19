package custis.easyabac.starter;

import custis.easyabac.api.EntityPermissionChecker;
import custis.easyabac.api.impl.EasyABACPermissionChecker;
import custis.easyabac.core.pdp.AuthService;

public class EntityPermissionCheckerHelper {

    public static <T, A> EntityPermissionChecker<T, A> newPermissionChecker(AuthService service) {
        return new EasyABACPermissionChecker<>(service);
    }
}
