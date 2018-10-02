package custis.easyabac.core;

import custis.easyabac.ModelType;
import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.init.*;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.AttributeValue;
import custis.easyabac.core.processors.RequestPreProcessor;
import custis.easyabac.core.processors.ResponsePostProcessor;
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

    private final List<RequestPreProcessor> preProcessors = new ArrayList<>();
    private final List<ResponsePostProcessor> postProcessors = new ArrayList<>();


    private EasyAbac(PdpHandler pdpHandler) {
        this(pdpHandler, Collections.emptyMap());
    }

    private EasyAbac(PdpHandler pdpHandler, Map<String, Attribute> attributeMap) {
        this.pdpHandler = pdpHandler;
        this.attributeMap = attributeMap;
    }

    @Override
    public AuthResponse authorize(List<AuthAttribute> authAttributes) {
        List<AttributeValue> attributeValueList = computeAttributeValues(authAttributes);

        for (RequestPreProcessor preProcessor : preProcessors) {
            preProcessor.preProcess();
        }

        AuthResponse authResponse = pdpHandler.evaluate(attributeValueList);

        for (ResponsePostProcessor postProcessor : postProcessors) {
            postProcessor.postProcess();
        }

        return authResponse;
    }

    @Override
    public Map<RequestId, AuthResponse> authorizeMultiple(Map<RequestId, List<AuthAttribute>> attributes) {
        for (RequestPreProcessor preProcessor : preProcessors) {
            preProcessor.preProcess();
        }

        MdpAuthResponse result = pdpHandler.evaluate();

        for (ResponsePostProcessor postProcessor : postProcessors) {
            postProcessor.postProcess();
        }

        return result.getResults();
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

        public AttributiveAuthorizationService build() throws Exception {
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

            return new EasyAbac(pdpHandler, attributeMap);
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