package custis.easyabac.core;

import custis.easyabac.ModelType;
import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.extend.RequestExtender;
import custis.easyabac.core.extend.subject.DummySubjectAttributesProvider;
import custis.easyabac.core.extend.subject.SubjectAttributesExtender;
import custis.easyabac.core.extend.subject.SubjectAttributesProvider;
import custis.easyabac.core.init.*;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.AttributeGroup;
import custis.easyabac.core.model.abac.attribute.AttributeValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.trace.Trace;
import custis.easyabac.pdp.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

public class EasyAbac implements AttributiveAuthorizationService {

    private final static Log log = LogFactory.getLog(EasyAbac.class);

    private final PdpHandler pdpHandler;
    private final AbacAuthModel abacAuthModel;
    private final List<Datasource> datasources;
    private final List<RequestExtender> requestExtenders;

    private EasyAbac(PdpHandler pdpHandler, AbacAuthModel abacAuthModel, List<Datasource> datasources, List<RequestExtender> requestExtenders) {
        this.pdpHandler = pdpHandler;
        this.abacAuthModel = abacAuthModel;
        this.datasources = datasources;
        this.requestExtenders = requestExtenders;
    }

    @Override
    public AuthResponse authorize(List<AuthAttribute> authAttributes) {
        List<AttributeValue> attributeValueList = computeAttributeValues(authAttributes);

        for (RequestExtender extender : requestExtenders) {
            extender.extend(attributeValueList);
        }

        return pdpHandler.evaluate(attributeValueList);
    }

    @Override
    public Map<RequestId, AuthResponse> authorizeMultiple(Map<RequestId, List<AuthAttribute>> attributes) {
        MdpAuthRequest requestContext = generate(attributes);

        for (RequestExtender extender : requestExtenders) {
            extender.extend(requestContext);
        }

        MdpAuthResponse result = pdpHandler.evaluate(requestContext);

        return result.getResults();
    }

    /**
     * Generating optimizable request
     *
     * @param attributes
     * @return
     */
    private MdpAuthRequest generate(Map<RequestId, List<AuthAttribute>> attributes) {
        // 0. simple request prepare
        MdpAuthRequest request = prepareSimpleMdpAuthRequest(attributes);

        // 1. clearing not used attributes in policies


        return request;
    }

    private MdpAuthRequest prepareSimpleMdpAuthRequest(Map<RequestId, List<AuthAttribute>> attributes) {
        MdpAuthRequest request = new MdpAuthRequest();
        attributes.forEach((requestId, authAttributes) -> {

            MdpAuthRequest.RequestReference reference = new MdpAuthRequest.RequestReference();
            Map<Category, AttributeGroup> groupMap = new HashMap<>();

            for (AuthAttribute authAttribute : authAttributes) {
                Attribute attribute = abacAuthModel.getAttributes().get(authAttribute.getId());

                AttributeGroup group = groupMap.computeIfAbsent(attribute.getCategory(), category -> new AttributeGroup(requestId + "#" + category, category, new ArrayList<>()));
                group.addAttribute(new AttributeValue(attribute, authAttribute.getValues()));
            }

            request.addRequest(reference);
        });

        return request;
    }

    private List<AttributeValue> computeAttributeValues(List<AuthAttribute> authAttributes) {
        List<AttributeValue> attributeValueList = new ArrayList<>();
        for (AuthAttribute authAttribute : authAttributes) {
            Attribute attribute = abacAuthModel.getAttributes().get(authAttribute.getId());
            if (attribute == null) {
                throw new EasyAbacAuthException("Атрибут " + authAttribute.getId() + " не найден в модели");
            }
            AttributeValue attributeValue = new AttributeValue(attribute, authAttribute.getValues());
            attributeValueList.add(attributeValue);
        }
        return attributeValueList;
    }


    public static class Builder {
        private final InputStream easyModel;
        private final ModelType modelType;

        private PdpHandler pdpHandler;
        private AbacAuthModel abacAuthModel;
        private List<Datasource> datasources = Collections.emptyList();
        private Cache cache;
        private Trace trace;
        private PdpType pdpType = PdpType.BALANA;
        private SubjectAttributesProvider subjectAttributesProvider = DummySubjectAttributesProvider.INSTANCE;
        private InputStream xacmlPolicy;

        public Builder(String easyModel, ModelType modelType) {
            this.easyModel = new ByteArrayInputStream(easyModel.getBytes());
            this.modelType = modelType;
        }

        public Builder(InputStream easyModel, ModelType modelType) {
            this.easyModel = easyModel;
            this.modelType = modelType;
        }

        public Builder pdpType(PdpType pdpType) {
            this.pdpType = pdpType;
            return this;
        }

        public Builder datasources(List<Datasource> datasources) {
            this.datasources = datasources;
            return this;
        }

        public Builder cache(Cache cache) {
            this.cache = cache;
            return this;
        }

        public Builder trace(Trace trace) {
            this.trace = trace;
            return this;
        }

        public Builder subjectAttributesProvider(SubjectAttributesProvider subjectAttributesProvider) {
            this.subjectAttributesProvider = subjectAttributesProvider;
            return this;
        }

        public Builder xacmlPolicy(InputStream xacmlPolicy) {
            this.xacmlPolicy = xacmlPolicy;
            return this;
        }


        public AttributiveAuthorizationService build() throws EasyAbacInitException {

            abacAuthModel = AbacAuthModelFactory.getInstance(modelType, easyModel);

            enrichDatasources(datasources, abacAuthModel);

            pdpHandler = PdpHandlerFactory.getPdpHandler(pdpType, modelType, abacAuthModel, xacmlPolicy, datasources, cache);

            List<RequestExtender> extenders = new ArrayList<>();
            extenders.add(new SubjectAttributesExtender(subjectAttributesProvider));


            if (log.isDebugEnabled()) {
                for (Attribute attribute : abacAuthModel.getAttributes().values()) {
                    log.debug(attribute.getId() + "  ->  " + attribute.getXacmlName() + "  ->  " + attribute.getType().getXacmlName() + "  ->  " + attribute.getCategory().getXacmlName());
                }

            }

            return new EasyAbac(pdpHandler, abacAuthModel, datasources, extenders);
        }

        private void enrichDatasources(List<Datasource> datasources, AbacAuthModel abacAuthModel) throws EasyAbacInitException {
            for (Datasource datasource : datasources) {
                for (Param param : datasource.getParams()) {
                    Attribute attributeParam = findAttribute(abacAuthModel.getAttributes(), param.getAttributeParamId());
                    param.setAttributeParam(attributeParam);
                }

                Attribute requiredAttribute = findAttribute(abacAuthModel.getAttributes(), datasource.getRequiredAttributeId());
                datasource.setRequiredAttribute(requiredAttribute);
            }
        }

        private Attribute findAttribute(Map<String, Attribute> attributeMap, String attributeParamId) throws EasyAbacInitException {
            Attribute attributeParam = attributeMap.get(attributeParamId);
            if (attributeParam == null) {
                throw new EasyAbacInitException("Attribute " + attributeParamId + " not found in model");
            }
            return attributeParam;
        }

    }
}