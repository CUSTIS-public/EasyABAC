package custis.easyabac.core;

import custis.easyabac.core.audit.Audit;
import custis.easyabac.core.audit.DefaultAudit;
import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.cache.DefaultCache;
import custis.easyabac.core.datasource.Datasource;
import custis.easyabac.core.datasource.Param;
import custis.easyabac.core.extend.RequestExtender;
import custis.easyabac.core.extend.subject.DummySubjectAttributesProvider;
import custis.easyabac.core.extend.subject.SubjectAttributesExtender;
import custis.easyabac.core.extend.subject.SubjectAttributesProvider;
import custis.easyabac.core.pdp.AttributiveAuthorizationService;
import custis.easyabac.core.pdp.PdpHandler;
import custis.easyabac.core.pdp.PdpHandlerFactory;
import custis.easyabac.core.trace.DefaultTrace;
import custis.easyabac.core.trace.Trace;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.attribute.Attribute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EasyAbacBuilder {

    private final static Log log = LogFactory.getLog(EasyAbacBuilder.class);

    private final AbacAuthModel abacAuthModel;
    private final PdpHandlerFactory pdpHandlerFactory;

    private List<Datasource> datasources = Collections.emptyList();
    private Cache cache = DefaultCache.INSTANCE;
    private Trace trace = DefaultTrace.INSTANCE;
    private Audit audit = DefaultAudit.INSTANCE;
    private SubjectAttributesProvider subjectAttributesProvider = DummySubjectAttributesProvider.INSTANCE;
    private InputStream xacmlPolicy;
    private Options options = Options.getDefaultOptions();

    public EasyAbacBuilder(AbacAuthModel abacAuthModel, PdpHandlerFactory pdpHandlerFactory) {
        this.abacAuthModel = abacAuthModel;
        this.pdpHandlerFactory = pdpHandlerFactory;
    }

    public EasyAbacBuilder(String easyModel, ModelCreator modelCreator, PdpHandlerFactory pdpHandlerFactory) throws EasyAbacInitException {
        this(new ByteArrayInputStream(easyModel.getBytes()), modelCreator, pdpHandlerFactory);
    }

    public EasyAbacBuilder(InputStream stream, ModelCreator modelCreator, PdpHandlerFactory pdpHandlerFactory) throws EasyAbacInitException {
        this(modelCreator.createModel(stream), pdpHandlerFactory);
    }

    public EasyAbacBuilder datasources(List<Datasource> datasources) {
        this.datasources = datasources;
        return this;
    }

    public EasyAbacBuilder cache(Cache cache) {
        this.cache = cache;
        return this;
    }

    public EasyAbacBuilder trace(Trace trace) {
        this.trace = trace;
        return this;
    }

    public EasyAbacBuilder audit(Audit audit) {
        this.audit = audit;
        return this;
    }

    public EasyAbacBuilder options(Options options) {
        this.options = options;
        return this;
    }

    public EasyAbacBuilder subjectAttributesProvider(SubjectAttributesProvider subjectAttributesProvider) {
        this.subjectAttributesProvider = subjectAttributesProvider;
        return this;
    }

    public EasyAbacBuilder useXacmlPolicy(InputStream xacmlPolicy) {
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


        Map<String, Map<String, Attribute>> attributesByAction = groupAttributesByAction(datasources, abacAuthModel);

        return new EasyAbac(pdpHandler, abacAuthModel, datasources, extenders, audit, trace, attributesByAction, options);
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

    private Attribute findAttribute(Map<String, Attribute> attributeMap, String attributeId) throws EasyAbacInitException {
        Attribute attributeParam = attributeMap.get(attributeId);
        if (attributeParam == null) {
            throw new EasyAbacInitException("Attribute " + attributeId + " is not found in the model");
        }
        return attributeParam;
    }


    private Map<String, Map<String, Attribute>> groupAttributesByAction(List<Datasource> datasources, AbacAuthModel abacAuthModel) {
        Set<String> actions = abacAuthModel.getPolicies().stream().flatMap(policy -> policy.getTarget().getAccessToActions().stream()).collect(Collectors.toSet());

        Map<String, Map<String, Attribute>> attributesByActionMap = new HashMap<>();
        for (String action : actions) {
            List<Attribute> attributesByAction = abacAuthModel.getPolicies().stream()
                    .filter(policy -> policy.getTarget().getAccessToActions().contains(action))
                    .flatMap(policy -> policy.getRules().stream()
                            .flatMap(rule -> rule.getConditions().stream()
                                    .flatMap(condition -> Stream.of(condition.getFirstOperand(), condition.getSecondOperandAttribute()).filter(Objects::nonNull))))
                    .distinct().collect(Collectors.toList());

            // TODO: 16.10.18 добавить цикл для зависимых датасорсов
            List<Attribute> attributeFromParams = attributesByAction.stream()
                    .flatMap(attribute -> datasources.stream()
                            .filter(datasource -> datasource.getReturnAttribute().equals(attribute))
                            .flatMap(datasource -> datasource.getParams().stream().map(param -> param.getAttributeParam())))
                    .distinct().collect(Collectors.toList());

            attributesByAction.addAll(attributeFromParams);

            Map<String, Attribute> collect = attributesByAction.stream().distinct().collect(Collectors.toMap(Attribute::getId, a -> a));

            attributesByActionMap.put(action, collect);
        }


        return attributesByActionMap;
    }

}
