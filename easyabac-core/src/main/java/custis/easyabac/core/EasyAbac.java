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
import custis.easyabac.pdp.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class EasyAbac implements AttributiveAuthorizationService {

    private final static Log log = LogFactory.getLog(EasyAbac.class);

    private final PdpHandler pdpHandler;
    private final AbacAuthModel abacAuthModel;
    private final List<Datasource> datasources;
    private final List<RequestExtender> requestExtenders;
    private final Audit audit;

    private EasyAbac(PdpHandler pdpHandler, AbacAuthModel abacAuthModel, List<Datasource> datasources, List<RequestExtender> requestExtenders, Audit audit) {
        this.pdpHandler = pdpHandler;
        this.abacAuthModel = abacAuthModel;
        this.datasources = datasources;
        this.requestExtenders = requestExtenders;
        this.audit = audit;
    }

    @Override
    public AuthResponse authorize(List<AuthAttribute> authAttributes) {
        try {
            List<AttributeWithValue> attributeWithValueList = computeAttributeValues(authAttributes);
            for (RequestExtender extender : requestExtenders) {
                extender.extend(attributeWithValueList);
            }

            AuthResponse result = pdpHandler.evaluate(attributeWithValueList);

            performAudit(attributeWithValueList, result);

            return result;
        } catch (Exception e) {
            log.error(e);
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

        performAudit(requestContext, result);

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

    private void performAudit(List<AttributeWithValue> attributeWithValues, AuthResponse result) {
        List<AttributeWithValue> subject = attributeWithValues.stream()
                .filter(attributeWithValue -> attributeWithValue.getAttribute().getCategory() == Category.SUBJECT)
                .collect(Collectors.toList());

        Optional<AttributeWithValue> action = attributeWithValues.stream()
                .filter(attributeWithValue -> attributeWithValue.getAttribute().getCategory() == Category.ACTION)
                .findFirst();

        Map<String, String> resourceMap = new HashMap<>();
        attributeWithValues.stream()
                .filter(attributeWithValue -> attributeWithValue.getAttribute().getCategory() == Category.RESOURCE)
                .forEach(attributeWithValue -> resourceMap.put(attributeWithValue.getAttribute().getId(), attributeWithValue.getValues().toString()));


        audit.onAction(serializeSubject(subject), resourceMap, action.get().getValues().get(0), result.getDecision());
    }

    private void performAudit(MdpAuthRequest request, MdpAuthResponse response) {
        List<AttributeWithValue> subject = request.getAttributeGroups()
                .stream()
                .filter(attributeGroup -> attributeGroup.getCategory() == Category.SUBJECT)
                .flatMap(attributeGroup -> attributeGroup.getAttributes().stream())
                .collect(Collectors.toList());

        List<String> actions = request.getAttributeGroups()
                .stream()
                .filter(attributeGroup -> attributeGroup.getCategory() == Category.ACTION)
                .flatMap(attributeGroup -> attributeGroup.getAttributes().stream())
                .flatMap(attributeWithValue -> attributeWithValue.getValues().stream())
                .collect(Collectors.toList());
        // FIXME сделать

        audit.onMultipleActions(serializeSubject(subject), Collections.emptyMap(), Collections.emptyMap());
    }

    private static String serializeSubject(List<AttributeWithValue> subject) {
        return subject.toString();
    }

    public static class Builder {
        private final InputStream easyModel;
        private final ModelType modelType;

        private PdpHandler pdpHandler;
        private AbacAuthModel abacAuthModel;
        private List<Datasource> datasources = Collections.emptyList();
        private Cache cache;
        private Trace trace = DefaultTrace.INSTANCE;
        private Audit audit = DefaultAudit.INSTANCE;
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

        public Builder audit(Audit audit) {
            this.audit = audit;
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

            return new EasyAbac(pdpHandler, abacAuthModel, datasources, extenders, audit);
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