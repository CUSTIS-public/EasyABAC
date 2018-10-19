package custis.easyabac.api.test.helper;

import custis.easyabac.api.attr.imp.AttributiveAuthAction;
import custis.easyabac.api.attr.imp.AttributiveAuthEntity;
import custis.easyabac.api.test.TestDescription;
import custis.easyabac.core.pdp.AuthAttribute;
import custis.easyabac.core.pdp.AuthResponse;

import java.util.ArrayList;
import java.util.List;

import static custis.easyabac.api.test.helper.TestDescriptionHelper.loadTestDescriptionFromResource;

public class TestDataHelper {

    public static Object[] loadTestFromResource(String resource) throws Exception {
        return getTestData(loadTestDescriptionFromResource(resource));
    }

    public static Object[] getTestData(TestDescription testDescription) throws Exception {
        Object[] testData = new Object[4];
        testData[0] = (AttributiveAuthEntity) () -> {
            List<AuthAttribute> out = new ArrayList<>();
            testDescription.getAttributes().forEach((entity, attrPair) -> {
                attrPair.forEach((key, value) -> {
                    out.add(new AuthAttribute(entity + "." + key, String.valueOf(value)));
                });
            });
            return out;
        };
        testData[1] = (AttributiveAuthAction) () -> new AuthAttribute(testDescription.getAction().getId(), testDescription.getAction().getValue());
        testData[2] = AuthResponse.Decision.PERMIT.name().equals(testDescription.getExpectedResult());
        testData[3] = testDescription;
        return testData;
    }

}
