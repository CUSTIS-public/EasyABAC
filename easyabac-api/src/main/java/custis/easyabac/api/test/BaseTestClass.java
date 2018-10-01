package custis.easyabac.api.test;

import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.core.EasyAbac;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import org.junit.BeforeClass;

import java.io.InputStream;

public class BaseTestClass {

    protected static AttributiveAuthorizationService authService;
    protected static PermitAwarePermissionChecker permissionChecker;

    @BeforeClass
    public static void initEasyABAC() {
        EasyAbac.Builder builder = new EasyAbac.Builder((InputStream) null, null, null); // FIXME

        //authService = builder.build();
        //permissionChecker = new EasyABACPermissionChecker<>(authService);
    }
}
