package custis.easyabac.api.test;

import custis.easyabac.api.PermitAwarePermissionChecker;
import org.junit.BeforeClass;

public class BaseTestClass<T, A> {

    protected PermitAwarePermissionChecker<T, A> permissionChecker;

    @BeforeClass
    public static void initEasyABAC() {

    }
}
