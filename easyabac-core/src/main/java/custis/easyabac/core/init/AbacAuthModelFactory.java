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

    private static final String XACML_ATTR_PREFIX = "urn:attr:01:resource:";

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
            for (EasyAttribute ea : easyResource.getAttributes()) {

                String attributeId = makeAttributeId(resourceName, ea.getId());
                String xacmlName = makeXacmlName(attributeId);
                DataType dataType = DataType.findByEasyName(ea.getType());

                Attribute attribute = new Attribute(attributeId, dataType, Category.RESOURCE, ea.isMultiple(),
                        ea.getTitle(), ea.getAllowableValues(), xacmlName);

                resourceAttributeList.add(attribute);
                attributes.add(attribute);
            }
            resourceMap.put(resourceName, new Resource(resourceName, easyResource.getTitle(), easyResource.getActions(), resourceAttributeList));

        }

        List<Policy> policyList = Collections.emptyList();

        AbacAuthModel abacAuthModel = new AbacAuthModel(policyList, resourceMap, attributes);

        return abacAuthModel;
    }

    private String makeXacmlName(String attributeId) {
        return XACML_ATTR_PREFIX + attributeId;
    }

    private String makeAttributeId(String resourceName, String id) {
        return resourceName + "." + id;
    }
}
