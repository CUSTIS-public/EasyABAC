package custis.easyabac.api.impl;

import custis.easyabac.api.attr.annotation.AuthorizationAction;
import custis.easyabac.api.attr.annotation.AuthorizationActionId;
import custis.easyabac.api.attr.annotation.AuthorizationAttribute;
import custis.easyabac.api.attr.annotation.AuthorizationEntity;
import custis.easyabac.api.attr.imp.AttributeAuthorizationEntity;
import custis.easyabac.api.attr.imp.AttributiveAuthorizationAction;
import custis.easyabac.core.pdp.AuthAttribute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttributeValueExtractor {

    private final static Log log = LogFactory.getLog(AttributeValueExtractor.class);
    public static final String ACTION_NAME = "action";
    public static final String SUBJECT_NAME = "subject";
    public static final String ENVIRONMENT_NAME = "env";

    public static <T> List<AuthAttribute> extractAttributesFromSubject(T object) {
        if (object instanceof AttributeAuthorizationEntity) {
            return ((AttributeAuthorizationEntity) object).getAuthAttributes();
        } else {
            return performReflectiveExtraction(object, SUBJECT_NAME);
        }
    }

    public static <T, A> List<AuthAttribute> extract(T object, A action) {
        List<AuthAttribute> attributes = extractAttributesFromResource(object);
        attributes.addAll(extractAttributesFromAction(action));
        return attributes;
    }

    public static <T> List<AuthAttribute> extractAttributesFromResource(T object) {
        if (object instanceof AttributeAuthorizationEntity) {
            return ((AttributeAuthorizationEntity) object).getAuthAttributes();
        } else {
            return performReflectiveExtraction(object);
        }
    }

    private static String convertValue(Object value) {
        return value.toString();
    }

    public static <T> List<AuthAttribute> extractAttributesFromAction(T object) {
        if (object instanceof AttributiveAuthorizationAction) {
            return Collections.singletonList(((AttributiveAuthorizationAction) object).getAuthAttribute());
        } else {
            return performReflectiveActionExtraction(object);
        }
    }

    public static <T> Object extractActionEntityByValue(Class<T> clazz, String value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        try {
            Method method = clazz.getMethod("byId", String.class);
            Object o = method.invoke(null, value);
            return o;
        } catch (NoSuchMethodException e) {
            // may be it is enum
            if (clazz.isEnum()) {
                for (T enumConstant : clazz.getEnumConstants()) {
                    if (((Enum) enumConstant).name().toLowerCase().equals(value)) {
                        return enumConstant;
                    }
                }
                throw new IllegalArgumentException("Not found action value for \"" + value + "\" in class " + clazz.getSimpleName());
            } else {
                throw e;
            }
        }
    }

    private static <T> List<AuthAttribute> performReflectiveExtraction(T object) {
        String entityName = object.getClass().getSimpleName();
        entityName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
        if (object.getClass().isAnnotationPresent(AuthorizationEntity.class)) {
            AuthorizationEntity ann = object.getClass().getAnnotation(AuthorizationEntity.class);
            if (!ann.name().isEmpty()) {
                entityName = ann.name();
            }
        }
        return performReflectiveExtraction(object, entityName);
    }

        private static <T> List<AuthAttribute> performReflectiveExtraction(T object, String entityName) {


        Field[] fields = object.getClass().getDeclaredFields();
        if (fields == null) {
            return new ArrayList<>();
        }

        boolean foundAnnotatedField = false;

        for (Field field : fields) {
            if (field.isAnnotationPresent(AuthorizationAttribute.class)) {
                foundAnnotatedField = true;
                break;
            }
        }

        if (foundAnnotatedField) {
            return extractAnnotatedFields(object, fields, entityName);
        } else {
            return extractAllFields(object, fields, entityName);
        }
    }

    private static <T> List<AuthAttribute> extractAllFields(T object, Field[] fields, String entityName) {
        List<AuthAttribute> attributes = new ArrayList<>();
        for (Field field : fields) {
            try {
                AuthAttribute val = generateAuthAttribute(field, entityName, object);
                if (val != null) {
                    attributes.add(val);
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }
        return attributes;
    }

    private static <T> AuthAttribute generateAuthAttribute(Field field, String entityName, T object) throws IllegalAccessException {
            String fieldName = field.getName();
            field.setAccessible(true);
            Object value = field.get(object);

            if (value == null) {
                return null;
            }

            if (value instanceof Iterable) {
                List<String> values = new ArrayList<>();
                ((Iterable) value).forEach(o -> {
                    if (o != null) {
                        values.add(convertValue(o.toString()));
                    }
                });

                if (values.isEmpty()) {
                    return null;
                }

                return new AuthAttribute(entityName + "." + fieldName, values);
            } else {
                return new AuthAttribute(entityName + "." + fieldName, convertValue(value));
            }
    }

    private static <T> List<AuthAttribute> extractAnnotatedFields(T object, Field[] fields, String entityName) {
        List<AuthAttribute> attributes = new ArrayList<>();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(AuthorizationAttribute.class)) {
                continue;
            }
            AuthorizationAttribute fieldAnnotation = field.getAnnotation(AuthorizationAttribute.class);

            String fieldName = fieldAnnotation.id();
            if (fieldName.isEmpty()) {
                fieldName = field.getName();
            }

            try {
                field.setAccessible(true);
                Object value = field.get(object);

                if (value == null) {
                    continue;
                }

                if (value instanceof Iterable) {
                    List<String> values = new ArrayList<>();
                    ((Iterable) value).forEach(o -> {
                        if (o != null) {
                            values.add(convertValue(o.toString()));
                        }
                    });

                    if (values.isEmpty()) {
                        continue;
                    }

                    attributes.add(new AuthAttribute(entityName + "." + fieldName, values));
                } else {
                    attributes.add(new AuthAttribute(entityName + "." + fieldName, convertValue(value)));
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
        }
        return attributes;
    }
    private static <T> String getActionName(T object) {
        String entityName = object.getClass().getSimpleName();
        entityName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
        if (entityName.toLowerCase().endsWith(ACTION_NAME)) {
            entityName = entityName.substring(0, entityName.length() - 6);
        }
        if (object.getClass().isAnnotationPresent(AuthorizationAction.class)) {
            AuthorizationAction ann = object.getClass().getAnnotation(AuthorizationAction.class);
            if (!ann.entity().isEmpty()) {
                entityName = ann.entity();
            }
        }
        return entityName;
    }

    private static <T> List<AuthAttribute> performReflectiveActionExtraction(T object) {
        String entityName = getActionName(object);

        List<AuthAttribute> attributes = new ArrayList<>();
        boolean foundAnnotated = false;
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(AuthorizationActionId.class)) {
                AuthorizationActionId fieldAnnotation = field.getAnnotation(AuthorizationActionId.class);

                try {
                    field.setAccessible(true);
                    Object value = field.get(object);

                    attributes.add(new AuthAttribute(entityName + "." + ACTION_NAME, entityName + "." + value.toString()));
                    foundAnnotated = true;
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage());
                }
            }
        }

        if (!foundAnnotated) {
            if (object instanceof Enum) {
                attributes.add(new AuthAttribute(entityName + "." + ACTION_NAME, entityName + "." + ((Enum) object).name().toLowerCase()));
            }
        }
        return attributes;
    }
}
