package custis.easyabac;

import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.init.Param;
import custis.easyabac.core.init.SampleDatasource;
import custis.easyabac.core.model.attribute.Attribute;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
        authAttrList.add(new AuthAttribute("urn:s_tst2:attr:01:resource:category", "iod"));
        authAttrList.add(new AuthAttribute("urn:s_tst2:attr:01:subject:allowed-categories", Arrays.asList("iod", "dsp")));
        AuthResponse authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.PERMIT, authResponse.getDecision());

    }

    @Test
    public void SamplePipTest() {
        InputStream policy = getResourceAsStream("test_pip.xacml");
        InputStream attributes = getResourceAsStream("attributes-1.yaml");

        HashSet<Param> params = new HashSet<>();
        Param userName = new Param("userName", new Attribute("urn:oasis:names:tc:xacml:1.0:subject:subject-id"));
        params.add(userName);

        SampleDatasource sampleDatasource = new SampleDatasource(params, new Attribute("urn:s_tst2:attr:01:subject:allowed-categories"));

        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(policy, attributes, ModelType.XACML).datasources(Arrays.asList(sampleDatasource)).build();

        List<AuthAttribute> authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute("urn:s_tst2:attr:01:action:operation", "edit"));
        authAttrList.add(new AuthAttribute("urn:s_tst2:attr:01:resource:category", "iod"));
        authAttrList.add(new AuthAttribute("urn:oasis:names:tc:xacml:1.0:subject:subject-id", "bob"));
        AuthResponse authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.PERMIT, authResponse.getDecision());


        authAttrList.add(new AuthAttribute("urn:s_tst2:attr:01:action:operation", "edit"));
        authAttrList.add(new AuthAttribute("urn:s_tst2:attr:01:resource:category", "iod"));
        authAttrList.add(new AuthAttribute("urn:oasis:names:tc:xacml:1.0:subject:subject-id", "alice"));
        authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.DENY, authResponse.getDecision());

    }
}
