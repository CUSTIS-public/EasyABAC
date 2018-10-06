package custis.easyabac.api.test;

import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.api.impl.EasyABACPermissionChecker;
import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.DummyAttributiveAuthorizationService;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(Parameterized.class)
public abstract class EasyAbacBaseTestClass {

    protected static AttributiveAuthorizationService authService;
    protected static PermitAwarePermissionChecker permissionChecker;

    private static final Yaml yaml = new Yaml();

    public static final String DATA_FILE_SUFFIX = ".yaml";

    @Parameterized.Parameter
    public Object resource;

    @Parameterized.Parameter(value = 1)
    public Object action;

    @BeforeClass
    public static void initEasyABAC() throws FileNotFoundException, EasyAbacInitException {
        // FIXME доделайте уже блед EasyAbac.Builder builder = new EasyAbac.Builder(getModelSource(), ModelType.EASY_YAML);

        /*
        builder.subjectAttributesProvider(new SubjectAttributesProvider() {
            @Override
            public List<AttributeValue> provide() {
                return null;
            }
        });
        authService = builder.build();
        */

        authService = new DummyAttributiveAuthorizationService(AuthResponse.Decision.PERMIT);
        permissionChecker = new EasyABACPermissionChecker<>(authService);
    }

    private static InputStream getModelSource() throws FileNotFoundException {
        return new FileInputStream("E:\\Projects\\CustIS\\easyabac\\framework\\easyabac-plugins\\easyabac-maven-plugin\\src\\main\\resources\\test.yaml");
    }

    public static <T> T populateResource(T resource, Map<String, Object> attributes) throws IllegalAccessException {
        for (Field declaredField : resource.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);

            AuthorizationAttribute authAttribute = declaredField.getAnnotation(AuthorizationAttribute.class);
            declaredField.set(resource, attributes.get(authAttribute.id()));
        }
        return resource;
    }

    private static InputStream getResourceAsStream(Class clazz, String s) {
        return clazz.getClassLoader()
                .getResourceAsStream(s);
    }

    private static TestDescription getTestDescription(Class clazz, String fileName) {
        InputStream is = getResourceAsStream(clazz, fileName + DATA_FILE_SUFFIX);
        TestDescription testDescription = yaml.loadAs(is, TestDescription.class);
        return testDescription;
    }

    private static <T> T createResource(Class<T> clazz, Map<String, Object> attributes) throws IllegalAccessException, InstantiationException {
        T obj = clazz.newInstance();
        return populateResource(obj, attributes);
    }

    private static <T> T createAction(Class<T> clazz, String action) {
        for (Method method : clazz.getMethods()) {
            //method.isS
        }
        //return OrderAction.byId(action);
        return null;
    }

    protected static List<Object[]> generateTestData(Class testClass, Class entityClass, Class actionClass, int numberOfTests, String dataFilePrefix, String entityCode) throws InstantiationException, IllegalAccessException {
        List<Object[]> data = new ArrayList<>();
        for (int i = 0; i < numberOfTests; i++) {
            Object[] testData = new Object[2];
            TestDescription testDescription = getTestDescription(testClass, dataFilePrefix + i);
            testData[0] = createResource(entityClass, testDescription.getAttributesByCode(entityCode));
            testData[1] = createAction(actionClass, testDescription.getShortAction());
            data.add(testData);
        }
        return data;
    }

}
