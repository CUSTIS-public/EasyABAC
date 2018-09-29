package custis.easyabac.api.core.call.getters;

import custis.easyabac.api.core.PermissionCheckerMetadata;
import custis.easyabac.api.core.UnsupportedPermissionCheckerMethodSignature;
import custis.easyabac.api.core.call.DecisionType;
import custis.easyabac.api.core.call.MethodType;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class AttributesValuesGetterFactory {
    public static RequestGenerator prepareDefault(Method method, PermissionCheckerMetadata metadata, MethodType methodType, DecisionType decisionType) throws ClassNotFoundException {
        Class<?>[] classes = method.getParameterTypes();
        checkOneOrTwoArguments(method, classes);

        Class<?> resourceType = metadata.getResourceType();
        Class<?> actionType = metadata.getActionType();

        Type[] types = method.getGenericParameterTypes();


        Class<?> first = classes[0];

        if (classes.length == 1) {
            if (Map.class.isAssignableFrom(first)) {
                Type firstType = ((ParameterizedType) types[0]).getActualTypeArguments()[0];
                Type secondType = ((ParameterizedType) types[0]).getActualTypeArguments()[1];

                boolean firstIsList = checkIsList(firstType);
                boolean secondIsList = checkIsList(secondType);
                boolean resourceIsFirst = isOfClassOrList(firstType, resourceType);

                return new MapAttributeValueGetter(metadata, firstIsList, secondIsList, resourceIsFirst);
            }

            throw new UnsupportedPermissionCheckerMethodSignature(method, "Expected Map");
        }

        Class<?> second = classes[1];

        if (resourceType.isAssignableFrom(first) && actionType.isAssignableFrom(second)) {
            return new TwoArgumentsValueGetter(metadata, false, false, true);
        }

        if (actionType.isAssignableFrom(first) && resourceType.isAssignableFrom(second)) {
            return new TwoArgumentsValueGetter(metadata, false, false,false);
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

                return new TwoArgumentsValueGetter(metadata, true, true, resourceIsFirst);
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
                return new TwoArgumentsValueGetter(metadata, true, false, resourceIsFirst);
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

            return new TwoArgumentsValueGetter(metadata, false, true, resourceIsFirst);
        }

        throw new UnsupportedPermissionCheckerMethodSignature(method, "Unexpected type " + first);
    }

    private static void checkOneOrTwoArguments(Method method, Class<?>[] classes) {
        if (classes.length == 0){
            throw new UnsupportedPermissionCheckerMethodSignature(method, "Method require parameters!");
        }

        if (classes.length > 2){
            throw new UnsupportedPermissionCheckerMethodSignature(method, "Method require less then 3 parameters!");
        }
    }

    private static boolean isOfClassOrList(Type type, Class<?> clazz) throws ClassNotFoundException {
        if (type instanceof ParameterizedType) {
            return clazz.isAssignableFrom(Class.forName(((ParameterizedType) type).getActualTypeArguments()[0].getTypeName()));
        } else {
            return clazz.isAssignableFrom(Class.forName(type.getTypeName()));
        }
    }

    private static boolean checkIsList(Type type) throws ClassNotFoundException {
        if (type instanceof ParameterizedType) {
            // first is generic
            if (List.class.isAssignableFrom(Class.forName(((ParameterizedType) type).getRawType().getTypeName()))) {
                return true;
            }
        }
        return false;
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
