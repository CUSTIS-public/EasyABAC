package custis.easyabac;

import custis.easyabac.core.EasyAbac;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipTest {

    private InputStream getResourceAsStream(String s) {
        return this.getClass()
                .getClassLoader()
                .getResourceAsStream(s);
    }

    @Test
    public void TwoAttrEquelsTest() {
        InputStream policy = getResourceAsStream("test_pip.xacml");
        InputStream attributes = getResourceAsStream("attributes-1.yaml");
        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(policy, attributes, ModelType.XACML).build();

        List<AuthAttribute> authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute("urn:s_tst2:attr:01:action:operation", "edit"));
        authAttrList.add(new AuthAttribute("urn:s_tst2:attr:01:resource:category", "report"));
        authAttrList.add(new AuthAttribute("urn:s_tst2:attr:01:subject:allow-category", Arrays.asList("form", "report")));
        AuthResponse authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.PERMIT, authResponse.getDecision());

    }
}
