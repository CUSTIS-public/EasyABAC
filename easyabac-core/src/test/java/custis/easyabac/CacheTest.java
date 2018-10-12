package custis.easyabac;

import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.EasyAbacDatasourceException;
import custis.easyabac.core.cache.SampleCache;
import custis.easyabac.core.init.Datasource;
import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.init.Param;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
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

        AttributiveAuthorizationService authorizationService = new EasyAbac.Builder(easyModel, ModelType.XACML)
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
