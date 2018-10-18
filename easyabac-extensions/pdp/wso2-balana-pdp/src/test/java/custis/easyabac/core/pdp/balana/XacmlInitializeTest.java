package custis.easyabac.core.pdp.balana;

import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.pdp.AttributiveAuthorizationService;
import custis.easyabac.core.pdp.AuthAttribute;
import custis.easyabac.core.pdp.AuthResponse;
import custis.easyabac.model.easy.EasyAbacModelCreator;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Ignore
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
        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        AttributiveAuthorizationService authorizationService = new EasyAbacBuilder(policy, creator, BalanaPdpHandlerFactory.PROXY_INSTANCE).build();


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

        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        AttributiveAuthorizationService authorizationService = new EasyAbacBuilder(policy, creator, BalanaPdpHandlerFactory.PROXY_INSTANCE).build();

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

        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        AttributiveAuthorizationService authorizationService = new EasyAbacBuilder(policy, creator, BalanaPdpHandlerFactory.PROXY_INSTANCE).build();

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
