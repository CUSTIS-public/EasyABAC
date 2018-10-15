package custis.easyabac.core;

import custis.easyabac.core.audit.Audit;
import custis.easyabac.core.audit.DefaultAudit;
import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.extend.RequestExtender;
import custis.easyabac.core.extend.subject.DummySubjectAttributesProvider;
import custis.easyabac.core.extend.subject.SubjectAttributesExtender;
import custis.easyabac.core.extend.subject.SubjectAttributesProvider;
import custis.easyabac.core.init.*;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.AttributeGroup;
import custis.easyabac.core.model.abac.attribute.AttributeWithValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.trace.DefaultTrace;
import custis.easyabac.core.trace.Trace;
import custis.easyabac.core.trace.model.TraceResult;
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
    private final Audit audit;
    private final Trace trace;

    private EasyAbac(PdpHandler pdpHandler, AbacAuthModel abacAuthModel, List<Datasource> datasources, List<RequestExtender> requestExtenders, Audit audit, Trace trace) {
        this.pdpHandler = pdpHandler;
        this.abacAuthModel = abacAuthModel;
        this.datasources = datasources;
        this.requestExtenders = requestExtenders;
        this.audit = audit;
        this.trace = trace;
    }

    @Override
    public AuthResponse authorize(List<AuthAttribute> authAttributes) {
        try {
            List<AttributeWithValue> attributeWithValueList = computeAttributeValues(authAttributes);
            for (RequestExtender extender : requestExtenders) {
                extender.extend(attributeWithValueList);
            }

            AuthResponse result = pdpHandler.evaluate(attributeWithValueList);

            TraceResult traceResult = result.getTraceResult();
            if (!pdpHandler.xacmlPolicyMode()) {
                traceResult.populateByModel(abacAuthModel);
            }
            trace.handleTrace(abacAuthModel, traceResult);
            audit.onRequest(attributeWithValueList, result);

            return result;
        } catch (Exception e) {
            log.error("authorize", e);
            return new AuthResponse(e.getMessage());
        }
    }

    @Override
    public Map<RequestId, AuthResponse> authorizeMultiple(Map<RequestId, List<AuthAttribute>> attributes) {
        MdpAuthRequest requestContext = generate(attributes);

        for (RequestExtender extender : requestExtenders) {
            extender.extend(requestContext);
        }

        MdpAuthResponse result = pdpHandler.evaluate(requestContext);
        result.getResults().forEach((requestId, authResponse) -> {
            TraceResult traceResult = authResponse.getTraceResult();
            if (!pdpHandler.xacmlPolicyMode()) {
                traceResult.populateByModel(abacAuthModel);
            }
            trace.handleTrace(abacAuthModel, traceResult);
        });

        audit.onMultipleRequest(requestContext, result);

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
                group.addAttribute(new AttributeWithValue(attribute, authAttribute.getValues()));
            }

            request.addRequest(reference);
        });

        return request;
    }

    private List<AttributeWithValue> computeAttributeValues(List<AuthAttribute> authAttributes) throws EasyAbacInitException {
        List<AttributeWithValue> attributeWithValueList = new ArrayList<>();
        for (AuthAttribute authAttribute : authAttributes) {
            Attribute attribute = findAttribute(abacAuthModel.getAttributes(), authAttribute.getId());
            AttributeWithValue attributeWithValue = new AttributeWithValue(attribute, authAttribute.getValues());
            attributeWithValueList.add(attributeWithValue);
        }
        return attributeWithValueList;
    }

    public static Attribute findAttribute(Map<String, Attribute> attributeMap, String attributeId) throws EasyAbacInitException {
        Attribute attributeParam = attributeMap.get(attributeId);
        if (attributeParam == null) {
            throw new EasyAbacInitException("Attribute " + attributeId + " is not found in the model");
        }
        return attributeParam;
    }

    public static class Builder {

        private final AbacAuthModel abacAuthModel;

        private PdpHandlerFactory pdpHandlerFactory = BalanaPdpHandlerFactory.PROXY_INSTANCE;
        private List<Datasource> datasources = Collections.emptyList();
        private Cache cache;
        private Trace trace = DefaultTrace.INSTANCE;
        private Audit audit = DefaultAudit.INSTANCE;
        private SubjectAttributesProvider subjectAttributesProvider = DummySubjectAttributesProvider.INSTANCE;
        private InputStream xacmlPolicy;

        public Builder(AbacAuthModel abacAuthModel) {
            this.abacAuthModel = abacAuthModel;
        }

        public Builder(String easyModel, ModelType modelType) throws EasyAbacInitException {
            this(new ByteArrayInputStream(easyModel.getBytes()), modelType);
        }

        public Builder(InputStream easyModel, ModelType modelType) throws EasyAbacInitException {
            this.abacAuthModel = AbacAuthModelFactory.getInstance(modelType, easyModel);
        }

        public Builder pdpHandlerFactory(PdpHandlerFactory pdpHandlerFactory) {
            if (xacmlPolicy != null && !pdpHandlerFactory.supportsXacmlPolicies()) {
                throw new IllegalArgumentException(pdpHandlerFactory.getClass().getName() + " should supports XACML!");
            }
            this.pdpHandlerFactory = pdpHandlerFactory;
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

        public Builder audit(Audit audit) {
            this.audit = audit;
            return this;
        }

        public Builder subjectAttributesProvider(SubjectAttributesProvider subjectAttributesProvider) {
            this.subjectAttributesProvider = subjectAttributesProvider;
            return this;
        }

        public Builder useXacmlPolicy(InputStream xacmlPolicy) {
            if (!pdpHandlerFactory.supportsXacmlPolicies()) {
                throw new IllegalArgumentException(pdpHandlerFactory.getClass().getName() + " doesn't supports XACML!");
            }
            this.xacmlPolicy = xacmlPolicy;
            return this;
        }


        public AttributiveAuthorizationService build() throws EasyAbacInitException {
            enrichDatasources(datasources, abacAuthModel);

            PdpHandler pdpHandler = null;
            if (xacmlPolicy != null) {
                // this is xacml source
                pdpHandler = pdpHandlerFactory.newXacmlInstance(xacmlPolicy, datasources, cache);
            } else {
                pdpHandler = pdpHandlerFactory.newInstance(abacAuthModel, datasources, cache);
            }

            List<RequestExtender> extenders = new ArrayList<>();
            extenders.add(new SubjectAttributesExtender(subjectAttributesProvider));


            if (log.isDebugEnabled()) {
                for (Attribute attribute : abacAuthModel.getAttributes().values()) {
                    log.debug(attribute.getId() + "  ->  " + attribute.getXacmlName() + "  ->  " + attribute.getType().getXacmlName() + "  ->  " + attribute.getCategory().getXacmlName());
                }

            }

            return new EasyAbac(pdpHandler, abacAuthModel, datasources, extenders, audit, trace);
        }

        private void enrichDatasources(List<Datasource> datasources, AbacAuthModel abacAuthModel) throws EasyAbacInitException {
            for (Datasource datasource : datasources) {
                for (Param param : datasource.getParams()) {
                    Attribute attributeParam = findAttribute(abacAuthModel.getAttributes(), param.getAttributeParamId());
                    param.setAttributeParam(attributeParam);
                }

                Attribute requiredAttribute = findAttribute(abacAuthModel.getAttributes(), datasource.getReturnAttributeId());
                datasource.setReturnAttribute(requiredAttribute);
            }
        }

    }
}