package custis.easyabac.api.impl;

import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.pdp.AuthAttribute;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class AttributeValueExtractor {

    public static <T, A> List<AuthAttribute> extract(T object, A action) {
        List<AuthAttribute> attributes = extractAttributesFromResource(object);
        attributes.addAll(extractAttributesFromAction(action));
        return attributes;
    }

    public static <T> List<AuthAttribute> extractAttributesFromResource(T object) {
        List<AuthAttribute> attributes = new ArrayList<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(AuthorizationAttribute.class)) {
                AuthorizationAttribute fieldAnnotation = field.getAnnotation(AuthorizationAttribute.class);

                String fieldName = fieldAnnotation.id();
                if (fieldName.isEmpty()) {
                    fieldName = field.getName();
                }

                try {
                    field.setAccessible(true);
                    Object value = field.get(object);

                    attributes.add(new AuthAttribute(fieldName, value.toString()));
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return attributes;
    }

    public static <T> List<AuthAttribute> extractAttributesFromAction(T object) {
        return Arrays.asList(new AuthAttribute("actionId", object.toString()));
    }
}
