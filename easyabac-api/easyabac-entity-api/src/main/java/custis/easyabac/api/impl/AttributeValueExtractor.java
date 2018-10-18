package custis.easyabac.api.impl;

import custis.easyabac.api.attr.annotation.AuthorizationAction;
import custis.easyabac.api.attr.annotation.AuthorizationActionId;
import custis.easyabac.api.attr.annotation.AuthorizationAttribute;
import custis.easyabac.api.attr.annotation.AuthorizationEntity;
import custis.easyabac.api.attr.imp.AttributeAuthorizationEntity;
import custis.easyabac.api.attr.imp.AttributiveAction;
import custis.easyabac.core.pdp.AuthAttribute;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class AttributeValueExtractor {

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
        if (object instanceof AttributiveAction) {
            return Collections.singletonList(((AttributiveAction) object).getAuthAttribute());
        } else {
            return performReflectiveActionExtraction(object);
        }
    }

    private static <T> List<AuthAttribute> performReflectiveExtraction(T object) {
        String entityName = object.getClass().getSimpleName();
        if (object.getClass().isAnnotationPresent(AuthorizationEntity.class)) {
            AuthorizationEntity ann = object.getClass().getAnnotation(AuthorizationEntity.class);
            if (!ann.name().isEmpty()) {
                entityName = ann.name();
            }
        }

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

    private static <T> List<AuthAttribute> performReflectiveActionExtraction(T object) {
        String entityName = object.getClass().getSimpleName();
        if (object.getClass().isAnnotationPresent(AuthorizationAction.class)) {
            AuthorizationAction ann = object.getClass().getAnnotation(AuthorizationAction.class);
            if (!ann.entity().isEmpty()) {
                entityName = ann.entity();
            }
        }

        List<AuthAttribute> attributes = new ArrayList<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(AuthorizationActionId.class)) {
                AuthorizationActionId fieldAnnotation = field.getAnnotation(AuthorizationActionId.class);

                try {
                    field.setAccessible(true);
                    Object value = field.get(object);

                    attributes.add(new AuthAttribute(entityName + ".action", entityName + "." + value.toString()));
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return attributes;
    }
}
