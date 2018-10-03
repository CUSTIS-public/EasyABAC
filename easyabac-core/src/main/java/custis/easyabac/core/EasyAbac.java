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
    private final Map<String, Attribute> attributeMap;

    private final List<RequestExtender> requestExtenders;


    private EasyAbac(PdpHandler pdpHandler) {
        this(pdpHandler, Collections.emptyMap(), Collections.emptyList());
    }

    private EasyAbac(PdpHandler pdpHandler, Map<String, Attribute> attributeMap, List<RequestExtender> requestExtenders) {
        this.pdpHandler = pdpHandler;
        this.attributeMap = attributeMap;
        this.requestExtenders = requestExtenders;
    }

    @Override
    public AuthResponse authorize(List<AuthAttribute> authAttributes) {
        List<AttributeValue> attributeValueList = computeAttributeValues(authAttributes);

        for (RequestExtender extender : requestExtenders) {
            extender.extend(attributeValueList);
        }

        AuthResponse authResponse = pdpHandler.evaluate(attributeValueList);

        return authResponse;
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
                Attribute attribute = attributeMap.get(authAttribute.getId());

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
            Attribute attribute = attributeMap.get(authAttribute.getId());

            AttributeValue attributeValue = new AttributeValue(attribute, authAttribute.getValues());
            attributeValueList.add(attributeValue);
        }
        return attributeValueList;
    }



    public static class Builder {
        private final InputStream policy;
        private final ModelType modelType;

        private PdpHandler pdpHandler;
        private AbacAuthModel abacAuthModel;
        private List<SampleDatasource> datasources = Collections.emptyList();
        private Cache cache;
        private Trace trace;
        private PdpType pdpType = PdpType.BALANA;
        private SubjectAttributesProvider subjectAttributesProvider = DummySubjectAttributesProvider.INSTANCE;

        public Builder(String policy, String attributes, ModelType modelType) {
            this.policy = new ByteArrayInputStream(policy.getBytes());
            this.modelType = modelType;
        }

        public Builder(InputStream policy, ModelType modelType) {
            this.policy = policy;
            this.modelType = modelType;
        }

        public Builder pdpType(PdpType pdpType) {
            this.pdpType = pdpType;
            return this;
        }

        public Builder datasources(List<SampleDatasource> datasources) {
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


        public AttributiveAuthorizationService build() throws EasyAbacInitException {
            PdpHandlerFactory pdpHandlerFactory = new PdpHandlerFactory();
            Map<String, Attribute> attributeMap = Collections.emptyMap();

            switch (modelType) {
                case XACML: {
                    PdpHandler pdpHandler = pdpHandlerFactory.getPdpHandler(pdpType, policy, datasources, cache);
                }
                case EASY_YAML: {
                    AbacAuthModel abacAuthModel = new AbacAuthModelFactory().getInstance(ModelType.EASY_YAML, policy);
                    PdpHandler pdpHandler = pdpHandlerFactory.getPdpHandler(pdpType, abacAuthModel, datasources, cache);
                    attributeMap = getAttributeMap(abacAuthModel);
                }
            }

            List<RequestExtender> extenders = new ArrayList<>();
            extenders.add(new SubjectAttributesExtender(subjectAttributesProvider));
            return new EasyAbac(pdpHandler, attributeMap, extenders);
        }


        private Map<String, Attribute> getAttributeMap(AbacAuthModel abacAuthModel) {
            Map<String, Attribute> attributeMap = new HashMap<>();
            for (Attribute attribute : abacAuthModel.getAttributes()) {
                attributeMap.put(attribute.getId(), attribute);
            }
            return attributeMap;
        }

    }
}