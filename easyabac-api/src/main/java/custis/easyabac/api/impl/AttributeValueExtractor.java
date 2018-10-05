package custis.easyabac.api.impl;

import custis.easyabac.api.AuthorizationActionId;
import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.api.AuthorizationEntity;
import custis.easyabac.pdp.AuthAttribute;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AttributeValueExtractor {

    public static <T, A> List<AuthAttribute> extract(T object, A action) {
        List<AuthAttribute> attributes = extractAttributesFromResource(object);
        attributes.addAll(extractAttributesFromAction(action));
        return attributes;
    }

    public static <T> List<AuthAttribute> extractAttributesFromResource(T object) {
        String entityName = object.getClass().getSimpleName();
        if (object.getClass().isAnnotationPresent(AuthorizationEntity.class)) {
            AuthorizationEntity ann = object.getClass().getAnnotation(AuthorizationEntity.class);
            if (!ann.name().isEmpty()) {
                entityName = ann.name();
            }
        }

        List<AuthAttribute> attributes = new ArrayList<>();

        Field[] fields = object.getClass().getDeclaredFields();
        if (fields == null) {
            return attributes;
        }
        for (Field field : fields) {
            if (field.isAnnotationPresent(AuthorizationAttribute.class)) {
                AuthorizationAttribute fieldAnnotation = field.getAnnotation(AuthorizationAttribute.class);

                String fieldName = fieldAnnotation.id();
                if (fieldName.isEmpty()) {
                    fieldName = field.getName();
                }

                try {
                    field.setAccessible(true);
                    Object value = field.get(object);


                    if (value instanceof Iterable) {
                        List<String> values = new ArrayList<>();
                        ((Iterable) value).forEach(o -> values.add(o.toString()));
                        attributes.add(new AuthAttribute(entityName + "." + fieldName, values));
                    } else {
                        attributes.add(new AuthAttribute(entityName + "." + fieldName, value.toString()));
                    }
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return attributes;
    }

    public static <T> List<AuthAttribute> extractAttributesFromAction(T object) {
        List<AuthAttribute> attributes = new ArrayList<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(AuthorizationActionId.class)) {
                AuthorizationActionId fieldAnnotation = field.getAnnotation(AuthorizationActionId.class);

                try {
                    field.setAccessible(true);
                    Object value = field.get(object);

                    attributes.add(new AuthAttribute("actionId", value.toString()));
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return attributes;
    }
}
