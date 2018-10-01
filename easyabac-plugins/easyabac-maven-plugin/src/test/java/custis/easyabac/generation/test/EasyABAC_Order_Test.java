package custis.easyabac.generation.test;

import custis.easyabac.api.NotPermittedException;
import custis.easyabac.api.test.BaseTestClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import custis.easyabac.generation.test.model.*;
import static custis.easyabac.generation.test.model.OrderAction.*;

@RunWith(JUnit4.class)
public class EasyABAC_Order_Test extends BaseTestClass {

    @Test()
    public void testREAD_Permit() {
        permissionChecker.ensurePermitted(getDataForTestREAD_Permit(), READ);
    }

    private Order getDataForTestREAD_Permit() {
        return;
    }

    @Test(expected = NotPermittedException.class)
    public void testREAD_Deny() {
        permissionChecker.ensurePermitted(getDataForTestREAD_Deny(), READ);
    }

    private Order getDataForTestREAD_Deny() {
        return;
    }

    @Test()
    public void testWRITE_Permit() {
        permissionChecker.ensurePermitted(getDataForTestWRITE_Permit(), WRITE);
    }

    private Order getDataForTestWRITE_Permit() {
        return;
    }

    @Test(expected = NotPermittedException.class)
    public void testWRITE_Deny() {
        permissionChecker.ensurePermitted(getDataForTestWRITE_Deny(), WRITE);
    }

    private Order getDataForTestWRITE_Deny() {
        return;
    }
}
