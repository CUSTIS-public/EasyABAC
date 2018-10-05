package custis.easyabac;

import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
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
    public void authTest1() throws Exception {

        InputStream policy = getResourceAsStream("test1_policy.xml");
        InputStream attributes = getResourceAsStream("test_pip_policy.yaml");
        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(policy, ModelType.XACML).build();

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
    public void authTest2() throws Exception {

        InputStream policy = getResourceAsStream("test2_policy.xml");
        InputStream attributes = getResourceAsStream("test_pip_policy.yaml");

        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(policy, ModelType.XACML).build();

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


    @Test
    @Ignore
    public void authTest3() throws Exception {

        InputStream policy = getResourceAsStream("test_pip_policy.yaml");

        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(policy, ModelType.EASY_YAML).build();

//        List<AuthAttribute> authAttrList = new ArrayList<>();
//        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:role", "USER"));
//        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:object", "report"));
//        AuthResponse authResponse = authorizationService.authorize(authAttrList);
//        Assert.assertEquals(AuthResponse.Decision.PERMIT, authResponse.getDecision());
//
//        authAttrList = new ArrayList<>();
//        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:role", "ADMIN"));
//        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:object", "report"));
//        authResponse = authorizationService.authorize(authAttrList);
//        Assert.assertEquals(AuthResponse.Decision.DENY, authResponse.getDecision());
//
//        authAttrList = new ArrayList<>();
//        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:role", "ADMIN"));
//        authAttrList.add(new AuthAttribute("urn:s_tst1:attr:01:resource:object", "report2"));
//        authResponse = authorizationService.authorize(authAttrList);
//        Assert.assertEquals(AuthResponse.Decision.DENY, authResponse.getDecision());

    }
}
