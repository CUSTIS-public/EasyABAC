package custis.easyabac.core.init;

import custis.easyabac.core.model.IdGenerator;
import custis.easyabac.core.model.abac.*;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.model.abac.attribute.DataType;
import custis.easyabac.core.model.abac.attribute.Resource;
import custis.easyabac.core.model.easy.EasyAttribute;
import custis.easyabac.core.model.easy.EasyAuthModel;
import custis.easyabac.core.model.easy.EasyPolicy;
import custis.easyabac.core.model.easy.EasyResource;

import java.util.*;

public class AuthModelTransformer {

    private static final String XACML_ATTR_PREFIX = "urn:attr:";

    private static final String ACTION = "action";

    private final EasyAuthModel easyAuthModel;

    private Map<String, Resource> resourceMap = new HashMap<>();

    private List<Attribute> attributes = new ArrayList<>();

    private List<Policy> policyList = new ArrayList<>();


    public AuthModelTransformer(EasyAuthModel easyAuthModel) {
        this.easyAuthModel = easyAuthModel;
    }

    public AbacAuthModel transform() throws EasyAbacInitException {

        transformAttributes(easyAuthModel.getResources());

        transformPermissions(easyAuthModel.getPermissions());

        AbacAuthModel abacAuthModel = new AbacAuthModel(policyList, resourceMap, attributes);

        return abacAuthModel;
    }

    private void transformPermissions(List<EasyPolicy> permissions) throws EasyAbacInitException {
        for (EasyPolicy permission : permissions) {

            List<TargetCondition> conditions = transformConditions(permission.getAccessToActions());

            Target target = new Target(Operation.OR, conditions, permission.getAccessToActions());

            Map<String, Rule> rules = transformRules(permission);

            List<Attribute> returnAttributes = transformReturnAttributes(permission.getReturnAttributes());

            policyList.add(new Policy(IdGenerator.newId(), permission.getTitle(), target, rules, returnAttributes));
        }
    }

    private List<TargetCondition> transformConditions(List<String> accessToActions) throws EasyAbacInitException {

        List<TargetCondition> targetConditions = new ArrayList<>();

        for (String action : accessToActions) {
            findAttributeByAction(action);


            TargetCondition targetCondition = new TargetCondition(IdGenerator.newId(), Function.EQUAL, findAttributeByAction(action), action);

            targetConditions.add(targetCondition);
        }
        return targetConditions;
    }

    private Attribute findAttributeByAction(String action) throws EasyAbacInitException {
        Resource resource = findResource(action);
        String actionAttributeName = makeAttributeId(resource.getId(), ACTION);
        for (Attribute attribute : attributes) {
            if (actionAttributeName.equals(attribute.getId())) {
                return attribute;
            }
        }
        throw new EasyAbacInitException("В модели не найден атрибут: " + actionAttributeName);
    }

    private Resource findResource(String action) throws EasyAbacInitException {
        String[] split = action.split("\\.");
        if (split.length == 0) {
            throw new EasyAbacInitException("Дейстие " + action + " заданно без указания объекта");
        }
        String resourceId = split[0];
        Resource resource = resourceMap.get(resourceId);
        if (resource == null) {
            throw new EasyAbacInitException("В моделе не найден ресурс: " + resourceId);
        }

        return resource;
    }

    private List<Attribute> transformReturnAttributes(List<String> returnAttributes) {
        return null;
    }

    private Map<String, Rule> transformRules(EasyPolicy permission) {
        return null;
    }

    private List<Attribute> transformAttributes(Map<String, EasyResource> resources) throws EasyAbacInitException {

        for (String resourceName : resources.keySet()) {

            List<Attribute> resourceAttributes = new ArrayList<>();

            EasyResource easyResource = resources.get(resourceName);

            if (easyResource.getAttributes().size() > 0) {
                for (EasyAttribute ea : easyResource.getAttributes()) {
                    Attribute attribute = transformAttribute(resourceName, ea);

                    resourceAttributes.add(attribute);
                    attributes.add(attribute);
                }
            }
            if (easyResource.getActions().size() > 0) {
                Attribute attribute = transformActions(resourceName, easyResource.getActions());

                resourceAttributes.add(attribute);
                attributes.add(attribute);
            }

            Resource resource = new Resource(resourceName, easyResource.getTitle(), easyResource.getActions(), resourceAttributes);

            resourceMap.put(resourceName, resource);
        }

        return attributes;
    }

    private Attribute transformActions(String resourceName, Set<String> actions) {
        String attributeId = makeAttributeId(resourceName, ACTION);
        String xacmlName = makeXacmlName(attributeId);
        DataType dataType = DataType.STRING;

        return new Attribute(attributeId, dataType, Category.ACTION, false,
                attributeId, actions, xacmlName);

    }

    private Attribute transformAttribute(String resourceName, EasyAttribute ea) throws EasyAbacInitException {
        String attributeId = makeAttributeId(resourceName, ea.getId());
        String xacmlName = makeXacmlName(attributeId);
        DataType dataType = DataType.findByEasyName(ea.getType());

        return new Attribute(attributeId, dataType, findCategory(resourceName), ea.isMultiple(),
                ea.getTitle(), ea.getAllowableValues(), xacmlName);
    }


    private Category findCategory(String resourceName) {
        switch (resourceName) {
            case "subject": {
                return Category.SUBJECT;
            }
            case "env": {
                return Category.ENV;
            }
            default: {
                return Category.RESOURCE;
            }
        }
    }

    private String makeXacmlName(String attributeId) {
        return XACML_ATTR_PREFIX + attributeId;
    }

    private String makeAttributeId(String resourceName, String id) {
        return resourceName + "." + id;
    }
}
