package custis.easyabac.generation.util;

import custis.easyabac.api.test.TestDescription;

import java.util.ArrayList;
import java.util.List;

public class TestDataHolder {

    private List<TestDescription> permitTests = new ArrayList<>();
    private List<TestDescription> denyTests = new ArrayList<>();

    public List<TestDescription> getPermitTests() {
        return permitTests;
    }

    public void setPermitTests(List<TestDescription> permitTests) {
        this.permitTests = permitTests;
    }

    public List<TestDescription> getDenyTests() {
        return denyTests;
    }

    public void setDenyTests(List<TestDescription> denyTests) {
        this.denyTests = denyTests;
    }

    public void addPermitTest(TestDescription testDescription) {
        permitTests.add(testDescription);
    }
}
