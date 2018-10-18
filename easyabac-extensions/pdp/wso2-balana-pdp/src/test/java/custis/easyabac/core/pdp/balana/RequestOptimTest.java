package custis.easyabac.core.pdp.balana;

import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.Options;
import custis.easyabac.core.cache.SampleCache;
import custis.easyabac.core.datasource.Datasource;
import custis.easyabac.core.datasource.Param;
import custis.easyabac.core.pdp.AttributiveAuthorizationService;
import custis.easyabac.core.pdp.AuthAttribute;
import custis.easyabac.core.pdp.AuthResponse;
import custis.easyabac.core.pdp.RequestId;
import custis.easyabac.core.trace.logging.LoggingViewTrace;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.easy.EasyAbacModelCreator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.*;

public class RequestOptimTest {

    private static final String ACTION_OPERATION = "report.action";
    private static final String SUBJECT_SUBJECT_ID = "subject.id";
    private static final String SUBJECT_ROLE = "subject.role";
    private static final String SUBJECT_ALLOWED_CATEGORIES = "subject.allowed-categories";
    private static final String REPORT_ID = "report.id";
    private static final String REPORT_BRANCH = "report.branch";
    private static final String REPORT_CATEGORY = "report.category";

    private AttributiveAuthorizationService authorizationService;

    private InputStream getResourceAsStream(String s) {
        return this.getClass()
                .getClassLoader()
                .getResourceAsStream(s);
    }

    @Before
    public void getAttributiveAuthorizationService() throws EasyAbacInitException {
        InputStream easyModel = getResourceAsStream("request_optim_test.yaml");

        HashSet<Param> userDsParams = new HashSet<>();
        Param userName = new Param("userName", SUBJECT_SUBJECT_ID);
        userDsParams.add(userName);

        Datasource datasourceUserCat = new UserCategoryDatasource(userDsParams, SUBJECT_ALLOWED_CATEGORIES);

        HashSet<Param> reportDsParams = new HashSet<>();
        Param reportId = new Param("reportId", REPORT_ID);
        reportDsParams.add(reportId);

        Datasource datasourceReportCat = new ReportCategoryDatasource(reportDsParams, REPORT_CATEGORY);

        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        Options options = new Options.OptionsBuilder().enableTrace(true).optimizeRequest(false).build();
        authorizationService = new EasyAbacBuilder(easyModel, creator, BalanaPdpHandlerFactory.PROXY_INSTANCE)
                .datasources(Arrays.asList(datasourceUserCat, datasourceReportCat))
                .options(options)
                .trace(new LoggingViewTrace())
                .cache(new SampleCache())
                .build();
    }


    @Test
    public void unuseAttributesOneRequest() {

        List<AuthAttribute> authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute(REPORT_ID, "2"));
        authAttrList.add(new AuthAttribute(REPORT_BRANCH, "3"));
        authAttrList.add(new AuthAttribute(ACTION_OPERATION, "report.edit"));
        authAttrList.add(new AuthAttribute(SUBJECT_SUBJECT_ID, "bob"));
        authAttrList.add(new AuthAttribute(SUBJECT_ROLE, "USER"));

        AuthResponse authResponse = authorizationService.authorize(authAttrList);

        Assert.assertEquals(AuthResponse.Decision.PERMIT, authResponse.getDecision());
    }

    @Test
    public void unuseAttributesMultiRequest() {

        Map<RequestId, List<AuthAttribute>> requestMap = new HashMap<>();


        List<AuthAttribute> authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute(REPORT_ID, "2"));
        authAttrList.add(new AuthAttribute(REPORT_BRANCH, "3"));
        authAttrList.add(new AuthAttribute(ACTION_OPERATION, "report.edit"));
        authAttrList.add(new AuthAttribute(SUBJECT_SUBJECT_ID, "bob"));
        authAttrList.add(new AuthAttribute(SUBJECT_ROLE, "USER"));
        RequestId editBobRequestId = RequestId.of("editBobRequestId");
        requestMap.put(editBobRequestId, authAttrList);

        authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute(REPORT_ID, "2"));
        authAttrList.add(new AuthAttribute(SUBJECT_ROLE, "USER"));
        authAttrList.add(new AuthAttribute(ACTION_OPERATION, "report.view"));
        authAttrList.add(new AuthAttribute(SUBJECT_SUBJECT_ID, "peter"));
        RequestId viewPeterRequestId = RequestId.of("viewPeterRequestId");
        requestMap.put(viewPeterRequestId, authAttrList);

        authAttrList = new ArrayList<>();
        authAttrList.add(new AuthAttribute(REPORT_ID, "2"));
        authAttrList.add(new AuthAttribute(ACTION_OPERATION, "report.view"));
        authAttrList.add(new AuthAttribute(SUBJECT_SUBJECT_ID, "alice"));
        authAttrList.add(new AuthAttribute(SUBJECT_ROLE, "USER"));
        RequestId viewAliceRequestId = RequestId.of("viewAliceRequestId");
        requestMap.put(viewAliceRequestId, authAttrList);

        Map<RequestId, AuthResponse> responseMap = authorizationService.authorizeMultiple(requestMap);

        Assert.assertEquals(AuthResponse.Decision.PERMIT, responseMap.get(editBobRequestId).getDecision());
        Assert.assertEquals(AuthResponse.Decision.PERMIT, responseMap.get(viewPeterRequestId).getDecision());
        Assert.assertEquals(AuthResponse.Decision.PERMIT, responseMap.get(viewPeterRequestId).getDecision());

    }

}
