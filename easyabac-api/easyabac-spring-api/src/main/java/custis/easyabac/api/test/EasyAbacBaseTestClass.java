package custis.easyabac.api.test;

import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.api.AuthorizationEntity;
import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.api.impl.EasyABACPermissionChecker;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.init.Datasource;
import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.AttributeWithValue;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthResponse;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public abstract class EasyAbacBaseTestClass {

    public static final String SUBJECT_SYNONYM = "subject";

    protected final AbacAuthModel model;

    public EasyAbacBaseTestClass(InputStream modelSource) throws EasyAbacInitException {
        this(AbacAuthModelFactory.getInstance(ModelType.EASY_YAML, modelSource));
    }

    public EasyAbacBaseTestClass(AbacAuthModel model) {
        this.model = model;
    }

    private static final Yaml yaml = new Yaml();

    @Parameterized.Parameter
    public Object resource;

    @Parameterized.Parameter(value = 1)
    public Object action;

    @Parameterized.Parameter(value = 2)
    public TestDescription testDescription;

    protected PermitAwarePermissionChecker getPermissionChecker(Class entityClass) throws FileNotFoundException, EasyAbacInitException {
        EasyAbacBuilder builder = new EasyAbacBuilder(model);

        // subject extender
        if (testDescription.containsAttributesByCode(SUBJECT_SYNONYM)) {
            builder.subjectAttributesProvider(() -> testDescription.getAttributesByCode(SUBJECT_SYNONYM).entrySet()
                    .stream()
                    .map(stringObjectEntry -> {
                        Attribute attribute = model.getAttributes().get(SUBJECT_SYNONYM + "." + stringObjectEntry.getKey());
                        return new AttributeWithValue(attribute, Collections.singletonList(stringObjectEntry.getValue().toString()));
                    }).collect(Collectors.toList()));
        }
        // TODO environment extender

        // other datasources
        List<Datasource> datasources = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : testDescription.getAttributes().entrySet()) {
            String entryKey = entry.getKey();
            if (!entryKey.equals(SUBJECT_SYNONYM) && !entryKey.equals(getEntityCode(entityClass))) {
                for (Map.Entry<String, Object> valEntry : entry.getValue().entrySet()) {
                    datasources.add(new SimpleDatasource(entry.getKey() + "." + valEntry.getKey(), valEntry.getValue().toString()));
                }
            }
        }
        builder.datasources(datasources);

        AttributiveAuthorizationService authService = builder.build();
        EasyABACPermissionChecker<Object, Object> permissionChecker = new EasyABACPermissionChecker<>(authService);
        return permissionChecker;
    }

    public static <T> T populateResource(T resource, Map<String, Object> attributes) throws IllegalAccessException {
        for (Field declaredField : resource.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);

            AuthorizationAttribute authAttribute = declaredField.getAnnotation(AuthorizationAttribute.class);
            declaredField.set(resource, attributes.get(authAttribute.id()));
        }
        return resource;
    }

    private static TestDescription getTestDescription(File folder, String fileName) throws FileNotFoundException {
        InputStream is = new FileInputStream(new File(folder, fileName));

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

    protected static List<Object[]> generateTestData(Class testClass, Class entityClass, Class actionClass,  AuthResponse.Decision decision) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
        String entityCode = getEntityCode(entityClass);

        List<Object[]> data = new ArrayList<>();

        String packageName = testClass.getPackage().getName().replace(".", "/");

        Enumeration<URL> e = testClass.getClassLoader().getResources("");
        while (e.hasMoreElements()) {
            URL url = e.nextElement();


            File folder = new File(url.getFile(), packageName);
            if (!folder.exists()) {
                continue;
            }
            String finalEntityCode = entityCode;
            for (String fileName : folder.list((dir, name) -> name.startsWith(finalEntityCode + "_" + decision.name().toLowerCase()))) {
                Object[] testData = new Object[3];
                TestDescription testDescription = getTestDescription(folder, fileName);
                Map<String, Object> resourceMap = testDescription.getAttributesByCode(entityCode);
                testData[0] = createResource(entityClass, resourceMap == null ? Collections.emptyMap() : resourceMap);
                testData[1] = createAction(actionClass, testDescription.getShortAction());
                testData[2] = testDescription;
                data.add(testData);
            }
        }

        return data;
    }

    private static String getEntityCode(Class entityClass) {
        String entityCode = entityClass.getSimpleName();
        if (entityClass.isAnnotationPresent(AuthorizationEntity.class)) {
            Annotation ann = entityClass.getDeclaredAnnotation(AuthorizationEntity.class);
            AuthorizationEntity authEnt = ((AuthorizationEntity) ann);
            if (!authEnt.name().isEmpty()) {
                entityCode = authEnt.name();
            }
        }
        return entityCode;
    }

    public static InputStream loadModel(Class testClass, String resource) throws IOException {
        Enumeration<URL> e = testClass.getClassLoader().getResources("");
        while (e.hasMoreElements()) {
            URL url = e.nextElement();


            File modelSource = new File(url.getFile(), resource);
            if (modelSource.exists() && modelSource.isFile()) {
                return new FileInputStream(modelSource);
            }
        }
        throw new FileNotFoundException(resource);
    }
}
