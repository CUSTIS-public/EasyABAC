package custis.easyabac;

import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.EasyAbacDatasourceException;
import custis.easyabac.core.init.Datasource;
import custis.easyabac.core.init.Param;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.util.*;

@Ignore
public class PipTest {

    private static final String ACTION_OPERATION = "report.action";
    private static final String RESOURCE_CATEGORY = "report.category";
    private static final String SUBJECT_SUBJECT_ID = "subject.id";
    private static final String SUBJECT_ALLOWED_CATEGORIES = "subject.allowed-categories";
    public static final String REPORT_ID = "report.id";

    private InputStream getResourceAsStream(String s) {
        return this.getClass()
                .getClassLoader()
                .getResourceAsStream(s);
    }

    @Test
    public void TwoAttrEquelsTest() throws Exception {
        InputStream policy = getResourceAsStream("test_pip_policy.xml");
        InputStream easyModel = getResourceAsStream("test_init_xacml.yaml");
        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(easyModel, ModelType.XACML).xacmlPolicy(policy).build();

        List<AuthAttribute> authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute(ACTION_OPERATION, "edit"));
        authAttrList.add(new AuthAttribute(RESOURCE_CATEGORY, "iod"));
        authAttrList.add(new AuthAttribute(SUBJECT_ALLOWED_CATEGORIES, Arrays.asList("iod", "dsp")));
        AuthResponse authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.PERMIT, authResponse.getDecision());
    }

    @Test
    public void SamplePipTest() throws Exception {
        InputStream policy = getResourceAsStream("test_pip_policy.xml");
        InputStream easyModel = getResourceAsStream("test_init_xacml.yaml");

        HashSet<Param> params = new HashSet<>();
        Param userName = new Param("userName", SUBJECT_SUBJECT_ID);
        params.add(userName);

        Datasource datasource = new UserCategoryDatasource(params, SUBJECT_ALLOWED_CATEGORIES);

        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(easyModel, ModelType.XACML).xacmlPolicy(policy)
                .datasources(Collections.singletonList(datasource)).build();

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

    @Test
    public void SamplePipWithObligationPermitTest() throws Exception {
        InputStream policy = getResourceAsStream("test_pip_oblig.xml");
        InputStream easyModel = getResourceAsStream("test_init_xacml.yaml");

        HashSet<Param> userDsParams = new HashSet<>();
        Param userName = new Param("userName", SUBJECT_SUBJECT_ID);
        userDsParams.add(userName);

        Datasource datasourceUserCat = new UserCategoryDatasource(userDsParams, SUBJECT_ALLOWED_CATEGORIES);

        HashSet<Param> reportDsParams = new HashSet<>();
        Param reportId = new Param("reportId", REPORT_ID);
        reportDsParams.add(reportId);

        Datasource datasourceReportCat = new ReportCategoryDatasource(reportDsParams, RESOURCE_CATEGORY);

        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(easyModel, ModelType.XACML)
                .xacmlPolicy(policy).datasources(Arrays.asList(datasourceUserCat, datasourceReportCat)).build();

        List<AuthAttribute> authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute(REPORT_ID, "1"));
        authAttrList.add(new AuthAttribute(ACTION_OPERATION, "edit"));
        authAttrList.add(new AuthAttribute(SUBJECT_SUBJECT_ID, "peter"));
        AuthResponse authResponse = authorizationService.authorize(authAttrList);
        System.out.println(authResponse.getErrorMsg());
        Assert.assertEquals(AuthResponse.Decision.PERMIT, authResponse.getDecision());
    }

    @Test
    public void SamplePipWithObligationDenyTest() throws Exception {
        InputStream policy = getResourceAsStream("test_pip_oblig.xml");
        InputStream easyModel = getResourceAsStream("test_init_xacml.yaml");

        HashSet<Param> userDsParams = new HashSet<>();
        Param userName = new Param("userName", SUBJECT_SUBJECT_ID);
        userDsParams.add(userName);

        Datasource datasource = new UserCategoryDatasource(userDsParams, SUBJECT_ALLOWED_CATEGORIES);

        HashSet<Param> reportDsParams = new HashSet<>();
        Param reportId = new Param("reportId", REPORT_ID);
        reportDsParams.add(reportId);

        Datasource datasourceReportCat = new ReportCategoryDatasource(reportDsParams, RESOURCE_CATEGORY);

        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(easyModel, ModelType.XACML)
                .xacmlPolicy(policy).datasources(Arrays.asList(datasource, datasourceReportCat)).build();

        List<AuthAttribute> authAttrList = new ArrayList<>();

        authAttrList.add(new AuthAttribute(REPORT_ID, "1"));
        authAttrList.add(new AuthAttribute(ACTION_OPERATION, "edit"));
        authAttrList.add(new AuthAttribute(SUBJECT_SUBJECT_ID, "alice"));
        AuthResponse authResponse = authorizationService.authorize(authAttrList);
        Assert.assertEquals(AuthResponse.Decision.DENY, authResponse.getDecision());

    }


    class UserCategoryDatasource extends Datasource {

        public UserCategoryDatasource(Set<Param> params, String requiredAttributeId) {
            super(params, requiredAttributeId);
        }

        public UserCategoryDatasource(Set<Param> params, String requiredAttributeId, Long expire) {
            super(params, requiredAttributeId, expire);
        }

        @Override
        public List<String> find() throws EasyAbacDatasourceException {
            {
                String userName = null;
                for (Param param : getParams()) {
                    if (param.getName().equals("userName")) {
                        userName = param.getValue();
                    }
                }

                if (userName == null) {
                    throw new EasyAbacDatasourceException("userName not found");
                }

                if (userName != null) {
                    switch (userName) {
                        case "bob":
                            return Arrays.asList("iod", "dsp");
                        case "alice":
                            return Arrays.asList("dsp");
                        case "peter":
                            return Arrays.asList("iod");
                    }
                }
                return Collections.emptyList();
            }
        }
    }


    class ReportCategoryDatasource extends Datasource {

        public ReportCategoryDatasource(Set<Param> params, String requiredAttributeId) {
            super(params, requiredAttributeId);
        }

        public ReportCategoryDatasource(Set<Param> params, String requiredAttributeId, Long expire) {
            super(params, requiredAttributeId, expire);
        }

        @Override
        public List<String> find() throws EasyAbacDatasourceException {
            {
                String reportId = null;
                for (Param param : getParams()) {
                    if (param.getName().equals("reportId")) {
                        reportId = param.getValue();
                    }
                }

                if (reportId == null) {
                    throw new EasyAbacDatasourceException("reportId not found");
                }

                if (reportId != null) {
                    switch (reportId) {
                        case "1":
                            return Arrays.asList("iod");
                        case "2":
                            return Arrays.asList("dsp");
                        case "3":
                            return Arrays.asList("iod");
                    }
                }
                return Collections.emptyList();
            }
        }
    }


}
