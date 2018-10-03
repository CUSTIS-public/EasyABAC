package custis.easyabac;

import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.init.Param;
import custis.easyabac.core.init.SampleDatasource;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class PipTest {

    public static final String ACTION_OPERATION = "urn:s_tst2:attr:01:action:operation";
    public static final String RESOURCE_CATEGORY = "urn:s_tst2:attr:01:resource:category";
    public static final String SUBJECT_SUBJECT_ID = "urn:oasis:names:tc:xacml:1.0:subject:subject-id";
    public static final String SUBJECT_ALLOWED_CATEGORIES = "urn:s_tst2:attr:01:subject:allowed-categories";

    private InputStream getResourceAsStream(String s) {
        return this.getClass()
                .getClassLoader()
                .getResourceAsStream(s);
    }

    @Test
    public void TwoAttrEquelsTest() throws Exception {
        InputStream policy = getResourceAsStream("test_pip_policy.xacml");
        InputStream easyModel = getResourceAsStream("test_init_xacml.yaml");
        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(easyModel, ModelType.XACML).xacmlPolicy(policy).build();

        List<AuthAttribute> authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute("report.action", "edit"));
        authAttrList.add(new AuthAttribute("report.category", "iod"));
        authAttrList.add(new AuthAttribute("subject.allowed-categories", Arrays.asList("iod", "dsp")));
        AuthResponse authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.PERMIT, authResponse.getDecision());
    }

    @Test
    @Ignore
    public void SamplePipTest() throws Exception {
        InputStream policy = getResourceAsStream("test_pip_policy.xacml");
        InputStream attributes = getResourceAsStream("attributes-1.yaml");

        HashSet<Param> params = new HashSet<>();
        Param userName = new Param("userName", new Attribute(SUBJECT_SUBJECT_ID));
        params.add(userName);

        SampleDatasource sampleDatasource = new SampleDatasource(params, new Attribute(SUBJECT_ALLOWED_CATEGORIES));

        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(policy, ModelType.XACML).datasources(Arrays.asList(sampleDatasource)).build();

        List<AuthAttribute> authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute(ACTION_OPERATION, "edit"));
        authAttrList.add(new AuthAttribute(RESOURCE_CATEGORY, "iod"));
        authAttrList.add(new AuthAttribute(SUBJECT_SUBJECT_ID, "bob"));
        AuthResponse authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.PERMIT, authResponse.getDecision());


        authAttrList.add(new AuthAttribute(ACTION_OPERATION, "edit"));
        authAttrList.add(new AuthAttribute(RESOURCE_CATEGORY, "iod"));
        authAttrList.add(new AuthAttribute(SUBJECT_SUBJECT_ID, "alice"));
        authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.DENY, authResponse.getDecision());

    }
}
