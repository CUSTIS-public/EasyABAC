package custis.easyabac.api.core.call.getters;

import custis.easyabac.api.core.PermissionCheckerMetadata;
import custis.easyabac.api.core.UnsupportedPermissionCheckerMethodSignature;
import custis.easyabac.api.core.call.DecisionType;
import custis.easyabac.api.core.call.MethodType;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

public class AttributesValuesGetterFactory {
    public static AttributesValuesGetter prepareDefault(Method method, PermissionCheckerMetadata permissionCheckerInformation, MethodType methodType, DecisionType decisionType) {
        Class<?> resourceType = permissionCheckerInformation.getResourceType();
        Class<?> actionType = permissionCheckerInformation.getActionType();
        Class<?>[] classes = method.getParameterTypes();
        Type[] types = method.getGenericParameterTypes();
        if (classes.length == 0){
            throw new UnsupportedPermissionCheckerMethodSignature(method, "Method require parameters!");
        }

        Class<?> first = classes[0];

        if (classes.length == 1) {
            if (Map.class.isAssignableFrom(first)) {
                return new MapAttributeValueGetter(permissionCheckerInformation);
            }

            throw new UnsupportedPermissionCheckerMethodSignature(method, "Unexpected type " + first);
        }

        Class<?> second = classes[1];


        if (resourceType.isAssignableFrom(first) && actionType.isAssignableFrom(second)) {
            return new SingleResourceAndAction(permissionCheckerInformation, true);
        }

        if (actionType.isAssignableFrom(first) && resourceType.isAssignableFrom(second)) {
            return new SingleResourceAndAction(permissionCheckerInformation, false);
        }

        if (List.class.isAssignableFrom(first)) {
            String firstGenericTypeName = ((ParameterizedType) types[0]).getActualTypeArguments()[0].getTypeName();
            boolean resourceIsFirst = firstGenericTypeName.equals(resourceType.getName());
            boolean actionIsFirst = firstGenericTypeName.equals(actionType.getName());
            if (!resourceIsFirst && !actionIsFirst) {
                throw new UnsupportedPermissionCheckerMethodSignature(method, "Expected types " + resourceType + " or " + actionType + ". Found " + firstGenericTypeName);
            }

            // contains resource or action

            if (List.class.isAssignableFrom(second)) {
                String secondGenericTypeName = ((ParameterizedType) types[1]).getActualTypeArguments()[0].getTypeName();

               checkSecondGeneric(resourceType, actionType, resourceIsFirst, secondGenericTypeName, method);

                return new ListAttributesValueGetter(permissionCheckerInformation, true, true, resourceIsFirst);
            } else {
                if (resourceIsFirst) {
                    if (!actionType.isAssignableFrom(second)) {
                        throw new UnsupportedPermissionCheckerMethodSignature(method, "Expected type " + actionType + " found " + second);
                    }
                } else {
                    // action first
                    if (!resourceType.isAssignableFrom(second)) {
                        throw new UnsupportedPermissionCheckerMethodSignature(method, "Expected type " + resourceType + " found " + second);
                    }
                }
                return new ListAttributesValueGetter(permissionCheckerInformation, true, false, resourceIsFirst);
            }
        }

        if (List.class.isAssignableFrom(second)) {
            // first not list
            String secondGenericTypeName = ((ParameterizedType) types[1]).getActualTypeArguments()[0].getTypeName();
            boolean resourceIsFirst = resourceType.isAssignableFrom(first);
            boolean actionIsFirst = actionType.isAssignableFrom(first);

            if (!resourceIsFirst && !actionIsFirst) {
                throw new UnsupportedPermissionCheckerMethodSignature(method, "Expected types " + resourceType + " or " + actionType + ". Found " + first);
            }

            checkSecondGeneric(resourceType, actionType, resourceIsFirst, secondGenericTypeName, method);

            return new ListAttributesValueGetter(permissionCheckerInformation, false, true, resourceIsFirst);
        }


        if (Map.class.isAssignableFrom(first)) {
            // todo check generic
            return new MapAttributeValueGetter(permissionCheckerInformation);
        }

        throw new UnsupportedPermissionCheckerMethodSignature(method, "Unexpected type " + first);
    }

    private static void checkSecondGeneric(Class<?> resourceType, Class<?> actionType, boolean resourceIsFirst, String secondGenericTypeName, Method method) {
        if (resourceIsFirst) {
            if (!actionType.getName().equals(secondGenericTypeName)) {
                throw new UnsupportedPermissionCheckerMethodSignature(method, "Expected type " + actionType + " found " + secondGenericTypeName);
            }
        } else {
            // action first
            if (!resourceType.getName().equals(secondGenericTypeName)) {
                throw new UnsupportedPermissionCheckerMethodSignature(method, "Expected type " + resourceType + " found " + secondGenericTypeName);
            }
        }
    }

}
