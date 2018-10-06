package custis.easyabac.autogen;

import custis.easyabac.api.test.EasyAbacBaseTestClass;
import custis.easyabac.autogen.model.Order;
import org.junit.Ignore;
import org.junit.Test;

import static custis.easyabac.autogen.model.OrderAction.*;

public class EasyABAC_Order_Test extends EasyAbacBaseTestClass {

    @Ignore
    @Test
    public void testVIEW_Permit() {
        permissionChecker.ensurePermitted(getDataForTestVIEW_Permit(), VIEW);
    }

    private Order getDataForTestVIEW_Permit() {
        return null;
    }

    @Ignore
    @Test
    public void testCREATE_Permit() {
        permissionChecker.ensurePermitted(getDataForTestCREATE_Permit(), CREATE);
    }

    private Order getDataForTestCREATE_Permit() {
        return null;
    }

    @Ignore
    @Test
    public void testAPPROVE_Permit() {
        permissionChecker.ensurePermitted(getDataForTestAPPROVE_Permit(), APPROVE);
    }

    private Order getDataForTestAPPROVE_Permit() {
        return null;
    }

    @Ignore
    @Test
    public void testREJECT_Permit() {
        permissionChecker.ensurePermitted(getDataForTestREJECT_Permit(), REJECT);
    }

    private Order getDataForTestREJECT_Permit() {
        return null;
    }
}
