package custis.easyabac.autogen;

import custis.easyabac.api.NotPermittedException;
import custis.easyabac.api.test.EasyAbacBaseTestClass;
import custis.easyabac.autogen.model.Order;
import custis.easyabac.autogen.model.OrderAction;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import java.util.List;

import static custis.easyabac.pdp.AuthResponse.Decision.DENY;

public class EasyABAC_Order_Deny_Test extends EasyAbacBaseTestClass {

    public EasyABAC_Order_Deny_Test() throws Exception {
        super(loadModel(EasyABAC_Order_Deny_Test.class, "policy.yaml"));
    }

    @Test(expected = NotPermittedException.class)
    public void test_DENY() throws Exception {
        getPermissionChecker(Order.class).ensurePermitted(resource, action);
    }

    @Parameters(name = "{index}: resource({0}) and action({1})")
    public static List<Object[]> data() throws Exception {
        return generateTestData(EasyABAC_Order_Deny_Test.class, Order.class, OrderAction.class, DENY);
    }
}
