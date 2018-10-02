package custis.easyabac.core.init;

import custis.easyabac.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.model.abac.attribute.DataType;
import custis.easyabac.core.model.abac.attribute.Resource;
import custis.easyabac.core.model.easy.EasyAttribute;
import custis.easyabac.core.model.easy.EasyAuthModel;
import custis.easyabac.core.model.easy.EasyResource;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

public class AbacAuthModelFactory {

    private static final String XACML_ATTR_PREFIX = "urn:attr:";
    public static final String ACTION = "action";

    public AbacAuthModel getInstance(ModelType modelType, InputStream policy) throws Exception {
        if (modelType == ModelType.EASY_YAML) {
            EasyAuthModel easyAuthModel = load(policy);
            return transform(easyAuthModel);
        } else {
            throw new IllegalArgumentException(modelType.name() + " not supported");
        }
    }

    private EasyAuthModel load(InputStream policy) {

        Yaml yaml = new Yaml();
        EasyAuthModel easyAuthModel = yaml.loadAs(policy, EasyAuthModel.class);

        return easyAuthModel;
    }

    private AbacAuthModel transform(EasyAuthModel easyAuthModel) throws Exception {


        List<Attribute> attributes = new ArrayList<>();
        Map<String, Resource> resourceMap = new HashMap<>();

        Map<String, EasyResource> resources = easyAuthModel.getResources();

        for (String resourceName : resources.keySet()) {

            List<Attribute> resourceAttributeList = new ArrayList<>();

            EasyResource easyResource = resources.get(resourceName);

            if (easyResource.getAttributes().size() > 0) {
                transformAttributes(resourceName, easyResource, attributes, resourceAttributeList);
            }
            if (easyResource.getActions().size() > 0) {
                Attribute attribute = transformActions(resourceName, easyResource.getActions());

                resourceAttributeList.add(attribute);
                attributes.add(attribute);
            }

            resourceMap.put(resourceName, new Resource(resourceName, easyResource.getTitle(), easyResource.getActions(), resourceAttributeList));
        }

        List<Policy> policyList = Collections.emptyList();

        AbacAuthModel abacAuthModel = new AbacAuthModel(policyList, resourceMap, attributes);

        return abacAuthModel;
    }

    private Attribute transformActions(String resourceName, Set<String> actions) {
        String attributeId = makeAttributeId(resourceName, ACTION);
        String xacmlName = makeXacmlName(attributeId);
        DataType dataType = DataType.STRING;

        return new Attribute(attributeId, dataType, Category.ACTION, false,
                attributeId, actions, xacmlName);

    }

    private void transformAttributes(String resourceName, EasyResource easyResource, List<Attribute> attributes, List<Attribute> resourceAttributeList) throws Exception {
        for (EasyAttribute ea : easyResource.getAttributes()) {
            Attribute attribute = transformAttribute(resourceName, ea);

            resourceAttributeList.add(attribute);
            attributes.add(attribute);
        }
    }

    private Attribute transformAttribute(String resourceName, EasyAttribute ea) throws Exception {
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
