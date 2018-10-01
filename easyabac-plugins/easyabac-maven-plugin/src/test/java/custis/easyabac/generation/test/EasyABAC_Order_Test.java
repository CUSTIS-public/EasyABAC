package custis.easyabac.generation.test;


import custis.easyabac.api.impl.EasyABACPermissionChecker;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.RequestId;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.Map;

/**
 * Testing entity Order
 * @see Order
 */
@RunWith(JUnit4.class)
public class EasyABAC_Order_Test {

    private static AttributiveAuthorizationService authorizationService;
    private static EasyABACPermissionChecker<Order, OrderAction, Object> permissionChecker;


    /**
     * Test action OrderAction.READ
     * @see OrderAction
     */
    @Test
    public void test_Read() {
        permissionChecker.ensurePermitted(getEntityForTest(), OrderAction.READ);
    }

    private Order getEntityForTest() {
        return null;
    }


    @BeforeClass
    public static void initEasyABAC() {
        authorizationService = new AttributiveAuthorizationService() {
            @Override
            public AuthResponse authorize(List<AuthAttribute> attributes) {
                return null;
            }

            @Override
            public Map<RequestId, AuthResponse> authorizeMultiple(Map<RequestId, List<AuthAttribute>> attributes) {
                return null;
            }
        };
        permissionChecker  = new EasyABACPermissionChecker<>(authorizationService);
    }
}
