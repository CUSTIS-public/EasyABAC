package custis.easyabac.core.init;

import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.model.IdGenerator;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.AttributeWithValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.model.abac.attribute.DataType;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.MultiAuthRequest;
import custis.easyabac.pdp.MultiAuthResponse;
import custis.easyabac.pdp.RequestId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.AttributeAssignment;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.ctx.xacml3.Result;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.xacml3.*;

import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

import static custis.easyabac.core.init.AttributesFactory.ATTRIBUTE_REQUEST_ID;
import static custis.easyabac.core.init.AttributesFactory.balanaAttribute;
import static custis.easyabac.pdp.AuthResponse.Decision.getByIndex;
import static java.util.stream.Collectors.toSet;

public class BalanaPdpHandler implements PdpHandler {

    private final static Log log = LogFactory.getLog(EasyAbac.class);

    private final PDP pdp;

    private BalanaPdpHandler(PDP pdp) {
        this.pdp = pdp;
    }

    @Override
    public AuthResponse evaluate(List<AttributeWithValue> attributeWithValues) {
        Map<Category, Attributes> attributesMap;

        try {
            attributesMap = getBalanaAttributesByCategory(attributeWithValues);
        } catch (EasyAbacInitException e) {
            return new AuthResponse(e.getMessage());
        }
        RequestCtx requestCtx = new RequestCtx(new HashSet<>(attributesMap.values()), null);

        if (log.isDebugEnabled()) {
            requestCtx.encode(System.out);
        }
        ResponseCtx responseCtx = pdp.evaluate(requestCtx);

        if (log.isDebugEnabled()) {
            log.debug(responseCtx.encode());
        }

        return createResponse(responseCtx.getResults().iterator().next());
    }

    @Override
    public MultiAuthResponse evaluate(MultiAuthRequest multiAuthRequest) throws EasyAbacInitException {

        Set<RequestReference> requestReferences = new HashSet<>();
        Set<Attributes> attributesSet = new HashSet<>();
        for (RequestId requestId : multiAuthRequest.getRequests().keySet()) {

            List<AttributeWithValue> attributeWithValues = multiAuthRequest.getRequests().get(requestId);

            AttributeWithValue requestIdAttribute = new AttributeWithValue(new custis.easyabac.core.model.abac.attribute.Attribute(ATTRIBUTE_REQUEST_ID, Category.ENV, DataType.STRING),
                    Collections.singletonList(requestId.getId()));

            attributeWithValues.add(requestIdAttribute);

            Map<Category, Attributes> balanaAttributesByCategory = getBalanaAttributesByCategory(attributeWithValues);

            RequestReference requestReference = transformReference(balanaAttributesByCategory);
            requestReferences.add(requestReference);

            attributesSet.addAll(balanaAttributesByCategory.values());
        }
        MultiRequests multiRequests = new MultiRequests(requestReferences);


        RequestCtx requestCtx = new RequestCtx(null, attributesSet, false, false, multiRequests, null);

        if (log.isDebugEnabled()) {
            requestCtx.encode(System.out);
        }

        ResponseCtx responseCtx = pdp.evaluate(requestCtx);

        if (log.isDebugEnabled()) {
            log.debug(responseCtx.encode());
        }

        Map<RequestId, AuthResponse> results = new HashMap<>();

        for (AbstractResult abstractResult : responseCtx.getResults()) {
            Result result = (Result) abstractResult;

            Stream<Attributes> envAttributes = result.getAttributes()
                    .stream()
                    .filter(attributes -> attributes.getCategory().toString().equals(Category.ENV.getXacmlName()));

            Optional<Attribute> requestId = envAttributes.flatMap(attributes -> attributes.getAttributes().stream())
                    .filter(attribute -> attribute.getId().toString().equals("request-id")).findFirst();

            if (!requestId.isPresent()) {
                throw new RuntimeException("Not found requestId in response");
            }

            StringAttribute value = (StringAttribute) requestId.get().getValue();

            results.put(RequestId.of(value.getValue()), createResponse(abstractResult));

        }

        return new MultiAuthResponse(results);
    }

    private RequestReference transformReference(Map<Category, Attributes> balanaAttributesByCategory) {
        Set<AttributesReference> references = new HashSet<>();
        for (Attributes attributes : balanaAttributesByCategory.values()) {
            AttributesReference reference = new AttributesReference();
            reference.setId(attributes.getId());
            references.add(reference);
        }
        RequestReference requestReference = new RequestReference();
        requestReference.setReferences(references);
        return requestReference;
    }

    private Map<Category, Attributes> getBalanaAttributesByCategory(List<AttributeWithValue> attributeWithValues) throws EasyAbacInitException {
        Map<Category, Attributes> attributesMap = new HashMap<>();

        for (AttributeWithValue attributeWithValue : attributeWithValues) {
            Category cat = attributeWithValue.getAttribute().getCategory();

            Attributes attributes = attributesMap.computeIfAbsent(cat,
                    category -> new Attributes(URI.create(category.getXacmlName()), null, new HashSet<>(), IdGenerator.newId())
            );
            boolean includeInResult = attributeWithValue.getAttribute().getId().equals(ATTRIBUTE_REQUEST_ID);


            Attribute newBalanaAttribute = transformAttributeValue(attributeWithValue, includeInResult);

            attributes.getAttributes().add(newBalanaAttribute);
        }

        return attributesMap;
    }


    private Attribute transformAttributeValue(AttributeWithValue attributeWithValue, boolean includeInResult) throws EasyAbacInitException {

        custis.easyabac.core.model.abac.attribute.Attribute attribute = attributeWithValue.getAttribute();
        return balanaAttribute(attribute.getXacmlName(), attribute.getType(), attributeWithValue.getValues(), includeInResult);
    }

    private AuthResponse createResponse(AbstractResult abstractResult) {
        AuthResponse.Decision decision = getByIndex(abstractResult.getDecision());
        Set<AttributeAssignment> assignments = abstractResult.getObligations()
                .stream()
                .filter(obligationResult -> obligationResult instanceof Obligation)
                .flatMap(obligationResult -> ((Obligation) obligationResult).getAssignments().stream())
                .collect(toSet());
        Map<String, String> obligations = new HashMap<>();
        for (AttributeAssignment assignment : assignments) {
            obligations.put(assignment.getAttributeId().toString(), assignment.getContent());
        }


        return new AuthResponse(decision, obligations);
    }

    public static PdpHandler getInstance(AbacAuthModel abacAuthModel, List<Datasource> datasources, Cache cache) {


        PolicyFinder policyFinder = new PolicyFinder();

        PolicyFinderModule policyFinderModule = new EasyPolicyFinderModule(abacAuthModel);
        Set<PolicyFinderModule> policyModules = new HashSet<>();

        policyModules.add(policyFinderModule);
        policyFinder.setModules(policyModules);

        Balana balana = Balana.getInstance();
        PDPConfig pdpConfig = balana.getPdpConfig();


        AttributeFinder attributeFinder = pdpConfig.getAttributeFinder();

        List<AttributeFinderModule> finderModules = attributeFinder.getModules();
        finderModules.clear();

        for (Datasource datasource : datasources) {
            finderModules.add(new DatasourceAttributeFinderModule(datasource, cache));
        }
        attributeFinder.setModules(finderModules);

        PDP pdp = new PDP(new PDPConfig(attributeFinder, policyFinder, null, true));

        return new BalanaPdpHandler(pdp);
    }


    public static PdpHandler getInstance(InputStream policyXacml, List<Datasource> datasources, Cache cache) {
        PolicyFinder policyFinder = new PolicyFinder();

        PolicyFinderModule stringPolicyFinderModule = new InputStreamPolicyFinderModule(policyXacml);
        Set<PolicyFinderModule> policyModules = new HashSet<>();

        policyModules.add(stringPolicyFinderModule);
        policyFinder.setModules(policyModules);

        Balana balana = Balana.getInstance();
        PDPConfig pdpConfig = balana.getPdpConfig();

        // registering new attribute finder. so default PDPConfig is needed to change
        AttributeFinder attributeFinder = pdpConfig.getAttributeFinder();

        List<AttributeFinderModule> finderModules = attributeFinder.getModules();
        finderModules.clear();

        for (Datasource datasource : datasources) {
            finderModules.add(new DatasourceAttributeFinderModule(datasource, cache));
        }
        attributeFinder.setModules(finderModules);

        PDP pdp = new PDP(new PDPConfig(attributeFinder, policyFinder, null, true));


        return new BalanaPdpHandler(pdp);
    }

}
