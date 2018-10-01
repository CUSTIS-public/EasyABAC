package custis.easyabac.generation.test;


import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Testing entity Order
 * @see Order
 */
@RunWith(JUnit4.class)
public class EasyABAC_Order_Test {

   /* private static AttributiveAuthorizationService authorizationService;
    private static EasyABACPermissionChecker<Order, OrderAction, Object> permissionChecker;
    private static EasyAttributeModel model;
    private static Policy policy;


    *//**
     * Test action OrderAction.READ
     * @see OrderAction
     *//*
    @Ignore
    @Test
    public void test_Read() {
        permissionChecker.ensurePermitted(getEntityForTest(), OrderAction.READ);
    }

    private Order getEntityForTest() {
        return null;
    }


    @BeforeClass
    public static void initEasyABAC() {
        model = new EasyAttributeModel();

        policy = new Policy();


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
    }*/
}
