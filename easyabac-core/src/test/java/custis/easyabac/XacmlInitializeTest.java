package custis.easyabac;

import custis.easyabac.core.EasyAbac;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class XacmlInitializeTest {

    private InputStream getResourceAsStream(String s) {
        return this.getClass()
                .getClassLoader()
                .getResourceAsStream(s);
    }


    @Test
    @Ignore
    public void authTest1() throws URISyntaxException {

        InputStream policy = getResourceAsStream("test1_policy.xacml");
        InputStream attributes = getResourceAsStream("attributes-1.yaml");
        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(policy, attributes, ModelType.XACML).build();

        List<AuthAttribute> authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:role", "ADMIN"));
        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:object", "form"));
        AuthResponse authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.PERMIT, authResponse.getDecision());

        authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:role", "USER"));
        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:object", "form"));
        authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.DENY, authResponse.getDecision());

        authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:role", "USER"));
        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:object", "report"));
        authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.DENY, authResponse.getDecision());
    }


    @Test
    @Ignore
    public void authTest2() {

        InputStream policy = getResourceAsStream("test2_policy.xacml");
        InputStream attributes = getResourceAsStream("attributes-1.yaml");

        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(policy, attributes, ModelType.XACML).build();

        List<AuthAttribute> authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:role", "USER"));
        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:object", "report"));
        AuthResponse authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.PERMIT, authResponse.getDecision());

        authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:role", "ADMIN"));
        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:object", "report"));
        authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.DENY, authResponse.getDecision());

        authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:role", "ADMIN"));
        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:object", "report2"));
        authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.DENY, authResponse.getDecision());

    }
}
