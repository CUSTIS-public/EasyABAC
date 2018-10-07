package custis.easyabac.api.test;

import custis.easyabac.ModelType;
import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.api.AuthorizationEntity;
import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.api.impl.EasyABACPermissionChecker;
import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.AttributeValue;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthResponse;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public abstract class EasyAbacBaseTestClass {

    private static final Yaml yaml = new Yaml();

    @Parameterized.Parameter
    public Object resource;

    @Parameterized.Parameter(value = 1)
    public Object action;

    @Parameterized.Parameter(value = 2)
    public TestDescription testDescription;

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


    }

    protected PermitAwarePermissionChecker getPermissionChecker() throws FileNotFoundException, EasyAbacInitException {
        AbacAuthModel authModel = AbacAuthModelFactory.getInstance(ModelType.EASY_YAML, getModelSource());
        EasyAbac.Builder builder = new EasyAbac.Builder(getModelSource(), ModelType.EASY_YAML);
        builder.subjectAttributesProvider(() -> testDescription.getAttributesByCode("subject").entrySet()
                .stream()
                .map(stringObjectEntry -> {
                    Attribute attribute = authModel.getAttributes().get(stringObjectEntry.getKey());
                    return new AttributeValue(attribute, Collections.singletonList(stringObjectEntry.getValue().toString()));
                }).collect(Collectors.toList()));

        AttributiveAuthorizationService authService = builder.build();
        EasyABACPermissionChecker<Object, Object> permissionChecker = new EasyABACPermissionChecker<>(authService);
        return permissionChecker;
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

    private static TestDescription getTestDescription(Class clazz, String fileName) throws FileNotFoundException {
        // FIXME InputStream is = getResourceAsStream(clazz, fileName);
        InputStream is = new FileInputStream("E:\\Projects\\CustIS\\easyabac\\framework\\easyabac-plugins\\easyabac-maven-plugin\\src\\test\\resources\\generation\\" + fileName);
        TestDescription testDescription = yaml.loadAs(is, TestDescription.class);
        return testDescription;
    }

    private static <T> T createResource(Class<T> clazz, Map<String, Object> attributes) throws IllegalAccessException, InstantiationException {
        T obj = clazz.newInstance();
        return populateResource(obj, attributes);
    }

    private static <T> T createAction(Class<T> clazz, String action) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = clazz.getMethod("byId", String.class);
        return (T) method.invoke(null, action);
    }

    protected static List<Object[]> generateTestData(Class testClass, Class entityClass, Class actionClass,  AuthResponse.Decision decision) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, FileNotFoundException {
        String entityCode = entityClass.getSimpleName();
        if (entityClass.isAnnotationPresent(AuthorizationEntity.class)) {
            Annotation ann = entityClass.getDeclaredAnnotation(AuthorizationEntity.class);
            AuthorizationEntity authEnt = ((AuthorizationEntity) ann);
            if (!authEnt.name().isEmpty()) {
                entityCode = authEnt.name();
            }
        }

        List<Object[]> data = new ArrayList<>();
        // TODO scan tests

        String folderName = testClass.getProtectionDomain().getCodeSource().getLocation() + testClass.getPackage().getName().replace(".", "/");
        File folder = new File("E:\\Projects\\CustIS\\easyabac\\framework\\easyabac-plugins\\easyabac-maven-plugin\\src\\test\\resources\\generation");
        String finalEntityCode = entityCode;
        for (String fileName : folder.list((dir, name) -> name.startsWith(finalEntityCode + "_" + decision.name().toLowerCase()))) {
            Object[] testData = new Object[3];
            TestDescription testDescription = getTestDescription(testClass, fileName);
            testData[0] = createResource(entityClass, testDescription.getAttributesByCode(entityCode));
            testData[1] = createAction(actionClass, testDescription.getShortAction());
            testData[2] = testDescription;
            data.add(testData);
        }
        return data;
    }

}
