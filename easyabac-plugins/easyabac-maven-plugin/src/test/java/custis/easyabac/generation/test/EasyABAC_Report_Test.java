package custis.easyabac.generation.test;

import custis.easyabac.api.test.BaseTestClass;
import custis.easyabac.generation.test.model.Report;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static custis.easyabac.generation.test.model.ReportAction.*;

@RunWith(JUnit4.class)
public class EasyABAC_Report_Test extends BaseTestClass {

    @Test()
    public void testEDIT_Permit() {
        permissionChecker.ensurePermitted(getDataForTestEDIT_Permit(), EDIT);
    }

    private Report getDataForTestEDIT_Permit() {
        return null;
    }

    @Test()
    public void testVIEW_Permit() {
        permissionChecker.ensurePermitted(getDataForTestVIEW_Permit(), VIEW);
    }

    private Report getDataForTestVIEW_Permit() {
        return null;
    }

    @Test()
    public void testREMOVE_Permit() {
        permissionChecker.ensurePermitted(getDataForTestREMOVE_Permit(), REMOVE);
    }

    private Report getDataForTestREMOVE_Permit() {
        return null;
    }
}
