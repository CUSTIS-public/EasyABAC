package custis.easyabac.core.pdp.balana;

import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.cache.SampleCache;
import custis.easyabac.core.datasource.Datasource;
import custis.easyabac.core.datasource.Param;
import custis.easyabac.core.pdp.AuthAttribute;
import custis.easyabac.core.pdp.AuthResponse;
import custis.easyabac.core.pdp.AuthService;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.easy.EasyAbacModelCreator;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.*;

public class CacheTest {


    private static final String ACTION_OPERATION = "report.action";
    private static final String RESOURCE_CATEGORY = "report.category";
    private static final String SUBJECT_SUBJECT_ID = "subject.id";
    private static final String SUBJECT_ALLOWED_CATEGORIES = "subject.allowed-categories";
    private static final String REPORT_ID = "report.id";
    public static final String SUBJECTS[] = {"bob", "alice", "peter"};
    public static final String PERMITS[] = {AuthResponse.Decision.PERMIT.name(), AuthResponse.Decision.DENY.name(), AuthResponse.Decision.PERMIT.name()};


    @Test
    public void sampleCacheTest() throws EasyAbacInitException {
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

        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        AuthService authorizationService = new EasyAbacBuilder(easyModel, creator, BalanaPdpHandlerFactory.PROXY_INSTANCE)
                .useXacmlPolicy(policy).datasources(Arrays.asList(datasourceUserCat, datasourceReportCat)).cache(new SampleCache()).build();

        Random random = new Random();
        for (int i = 0; i < 1000; i++) {


            List<AuthAttribute> authAttrList = new ArrayList<>();
            authAttrList.add(new AuthAttribute(REPORT_ID, "1"));
            authAttrList.add(new AuthAttribute(ACTION_OPERATION, "edit"));
            int inx = random.nextInt(3);
            authAttrList.add(new AuthAttribute(SUBJECT_SUBJECT_ID, SUBJECTS[inx]));
            AuthResponse authResponse = authorizationService.authorize(authAttrList);

            Assert.assertEquals(PERMITS[inx], authResponse.getDecision().name());
        }

    }

    private InputStream getResourceAsStream(String s) {
        return this.getClass()
                .getClassLoader()
                .getResourceAsStream(s);
    }

}
