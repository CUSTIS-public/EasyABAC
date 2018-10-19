package custis.easyabac.api.test.helper;

import custis.easyabac.api.attr.annotation.AuthorizationAttribute;
import custis.easyabac.api.attr.annotation.AuthorizationEntity;
import custis.easyabac.api.test.TestDescription;
import custis.easyabac.core.pdp.AuthResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import static custis.easyabac.api.impl.AttributeValueExtractor.extractActionEntityByValue;
import static custis.easyabac.api.test.helper.TestDescriptionHelper.loadTestDescriptionFromResource;

public class TestDataHelper {

    public static Object[] loadTestFromResource(String resource, Class entityClass, Class actionClass, String entityCode) throws Exception {
        return getTestData(loadTestDescriptionFromResource(resource), entityClass, actionClass, entityCode);
    }

    public static Object[] getTestData(TestDescription testDescription, Class entityClass, Class actionClass, String entityCode) throws Exception {
        Object[] testData = new Object[4];
        Map<String, Object> resourceMap = testDescription.getAttributesByCode(entityCode);
        testData[0] = createResource(entityClass, resourceMap == null ? Collections.emptyMap() : resourceMap);
        testData[1] = extractActionEntityByValue(actionClass, testDescription.getShortAction());
        testData[2] = AuthResponse.Decision.PERMIT.name().equals(testDescription.getExpectedResult());
        testData[3] = testDescription;
        return testData;
    }

    private static <T> T createResource(Class<T> clazz, Map<String, Object> attributes) throws Exception {
        T obj = clazz.newInstance();
        return populateResource(obj, attributes);
    }

    private static <T> T populateResource(T resource, Map<String, Object> attributes) throws IllegalAccessException {
        for (Field declaredField : resource.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);

            AuthorizationAttribute authAttribute = declaredField.getAnnotation(AuthorizationAttribute.class);
            declaredField.set(resource, attributes.get(authAttribute.id()));
        }
        return resource;
    }

    public static String getEntityCode(Class entityClass) {
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

}
