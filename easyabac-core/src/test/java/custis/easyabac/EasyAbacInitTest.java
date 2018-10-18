package custis.easyabac;

import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.datasource.Datasource;
import custis.easyabac.core.datasource.Param;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.core.pdp.AttributiveAuthorizationService;
import custis.easyabac.core.pdp.AuthAttribute;
import custis.easyabac.core.pdp.AuthResponse;
import custis.easyabac.core.pdp.RequestId;
import custis.easyabac.model.EasyAbacInitException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.*;

public class EasyAbacInitTest {

    private static final String ACTION_OPERATION = "report.action";
    private static final String RESOURCE_CATEGORY = "report.category";
    private static final String SUBJECT_SUBJECT_ID = "subject.id";
    private static final String SUBJECT_ALLOWED_CATEGORIES = "subject.allowed-categories";
    private static final String REPORT_ID = "report.id";

    private AttributiveAuthorizationService authorizationService;

    private InputStream getResourceAsStream(String s) {
        return this.getClass()
                .getClassLoader()
                .getResourceAsStream(s);
    }

    @Before
    public void getAttributiveAuthorizationService() throws EasyAbacInitException {
        InputStream easyModel = getResourceAsStream("test_pip_policy.yaml");

        HashSet<Param> userDsParams = new HashSet<>();
        Param userName = new Param("userName", SUBJECT_SUBJECT_ID);
        userDsParams.add(userName);

        Datasource datasourceUserCat = new UserCategoryDatasource(userDsParams, SUBJECT_ALLOWED_CATEGORIES);

        HashSet<Param> reportDsParams = new HashSet<>();
        Param reportId = new Param("reportId", REPORT_ID);
        reportDsParams.add(reportId);

        Datasource datasourceReportCat = new ReportCategoryDatasource(reportDsParams, RESOURCE_CATEGORY);

        authorizationService = new EasyAbacBuilder(easyModel, ModelType.EASY_YAML)
                .datasources(Arrays.asList(datasourceUserCat, datasourceReportCat)).build();
    }


    @Test
    public void initTest() {

        List<AuthAttribute> authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute(REPORT_ID, "2"));
        authAttrList.add(new AuthAttribute(ACTION_OPERATION, "report.edit"));
        authAttrList.add(new AuthAttribute(SUBJECT_SUBJECT_ID, "bob"));

        AuthResponse authResponse = authorizationService.authorize(authAttrList);

        System.out.println(authResponse.getErrorMsg());
        Assert.assertEquals(AuthResponse.Decision.PERMIT, authResponse.getDecision());
    }


    @Test
    public void initMultiTest() {

        Map<RequestId, List<AuthAttribute>> requestMap = new HashMap<>();


        List<AuthAttribute> authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute(REPORT_ID, "2"));
        authAttrList.add(new AuthAttribute(ACTION_OPERATION, "report.edit"));
        authAttrList.add(new AuthAttribute(SUBJECT_SUBJECT_ID, "bob"));
        RequestId editBobRequestId = RequestId.newRandom();
        requestMap.put(editBobRequestId, authAttrList);

        authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute(REPORT_ID, "2"));
        authAttrList.add(new AuthAttribute(ACTION_OPERATION, "report.view"));
        authAttrList.add(new AuthAttribute(SUBJECT_SUBJECT_ID, "peter"));
        RequestId viewPeterRequestId = RequestId.newRandom();
        requestMap.put(viewPeterRequestId, authAttrList);


        Map<RequestId, AuthResponse> responseMap = authorizationService.authorizeMultiple(requestMap);

        Assert.assertEquals(AuthResponse.Decision.PERMIT, responseMap.get(editBobRequestId).getDecision());
        Assert.assertEquals(AuthResponse.Decision.DENY, responseMap.get(viewPeterRequestId).getDecision());

    }


}
