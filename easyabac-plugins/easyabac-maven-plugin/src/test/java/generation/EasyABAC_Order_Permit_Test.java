package generation;

import custis.easyabac.api.test.EasyAbacBaseTestClass;
import generation.model.Order;
import generation.model.OrderAction;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import java.util.List;

import static custis.easyabac.pdp.AuthResponse.Decision.PERMIT;

public class EasyABAC_Order_Permit_Test extends EasyAbacBaseTestClass {

    public EasyABAC_Order_Permit_Test() throws Exception {
        super(loadModel(EasyABAC_Order_Permit_Test.class, "test.yaml"));
    }

    @Ignore
    @Test
    public void test_PERMIT() throws Exception {
        getPermissionChecker(Order.class).ensurePermitted(resource, action);
    }

    @Parameters(name = "{index}: resource({0}) and action({1})")
    public static List<Object[]> data() throws Exception {
        return generateTestData(EasyABAC_Order_Permit_Test.class, Order.class, OrderAction.class, PERMIT);
    }
}
