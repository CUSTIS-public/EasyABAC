package custis.easyabac.core;

import custis.easyabac.ModelType;
import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.init.PolicyInitializer;
import custis.easyabac.core.model.attribute.Category;
import custis.easyabac.core.model.attribute.Datasource;
import custis.easyabac.core.model.attribute.load.EasyAttributeModel;
import custis.easyabac.core.model.policy.EasyPolicy;
import custis.easyabac.core.trace.Trace;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.RequestId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.PDP;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.ctx.xacml3.Result;
import org.wso2.balana.xacml3.Attributes;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class EasyAbac implements AttributiveAuthorizationService {

    private final static Log log = LogFactory.getLog(EasyAbac.class);

    private final PDP pdpInstance;


    private EasyAbac(PDP pdpInstance) {

        this.pdpInstance = pdpInstance;
    }

    @Override
    public AuthResponse authorize(List<AuthAttribute> attributes) {
        ResponseCtx responseCtx;
        try {
            Set<Attribute> balanaAttributes = new HashSet<>();
            for (AuthAttribute attribute : attributes) {
                //TODO: здесь определять тип и массив, подставлять префикс
                Attribute newBalanaAttribute = new Attribute(new URI(attribute.getId()), "",
                        null, new StringAttribute(attribute.getValues().get(0)), 3);
                balanaAttributes.add(newBalanaAttribute);
            }

            Set<Attributes> attributesSet = new HashSet<>();
            Attributes newBalanaAttributesSet = new Attributes(new URI(Category.RESOURCE.getXacmlName()), balanaAttributes);
            attributesSet.add(newBalanaAttributesSet);

            RequestCtx requestCtx = new RequestCtx(attributesSet, null);

            requestCtx.encode(System.out);

//            requestCtx = RequestCtxFactory.getFactory().getRequestCtx(request.getXacmlRequest().replaceAll(">\\s+<", "><"));
            responseCtx = pdpInstance.evaluate(requestCtx);
        } catch (URISyntaxException e) {
            List<String> code = new ArrayList<>();
            code.add(Status.STATUS_SYNTAX_ERROR);
            String error = "Invalid request  : " + e.getMessage();
            Status status = new Status(code, error);
            responseCtx = new ResponseCtx(new Result(AbstractResult.DECISION_INDETERMINATE, status));
        }
        log.debug(responseCtx.encode());

        AuthResponse.Decision decision = AuthResponse.Decision.getByIndex(responseCtx.getResults().iterator().next().getDecision());

        return new AuthResponse(decision);
    }

    @Override
    public Map<RequestId, AuthResponse> authorizeMultiple(Map<RequestId, List<AuthAttribute>> attributes) {
        return null;
    }


    public static class Builder {
        private final InputStream policy;
        private final InputStream attributes;
        private final ModelType modelType;

        private EasyPolicy easyPolicy;
        private EasyAttributeModel easyAttributeModel;
        private List<Datasource> datasources;
        private Cache cache;
        private Trace trace;

        public Builder(String policy, String attributes, ModelType modelType) {
            this.policy = new ByteArrayInputStream(policy.getBytes());
            this.attributes = new ByteArrayInputStream(attributes.getBytes());
            this.modelType = modelType;
        }

        public Builder(InputStream policy, InputStream attributes, ModelType modelType) {
            this.policy = policy;
            this.attributes = attributes;
            this.modelType = modelType;
        }

//        public Builder(String policy, String attributes) {
//
//            Yaml yaml = new Yaml();
//
//            easyPolicy = yaml.loadAs(policy, EasyPolicy.class);
//
//            easyAttributeModel = yaml.loadAs(attributes, EasyAttributeModel.class);
//
//        }
//
//        public Builder(InputStream policy, InputStream attributes) {
//
//            Yaml yaml = new Yaml();
//
//            easyPolicy = yaml.loadAs(policy, EasyPolicy.class);
//
//            easyAttributeModel = yaml.loadAs(attributes, EasyAttributeModel.class);
//        }

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

        public AttributiveAuthorizationService build() {

            switch (modelType) {
                case XACML: {
                    PolicyInitializer policyInitializer = new PolicyInitializer();
                    PDP pdpInstance = policyInitializer.newPDPInstance(policy);

                    return new EasyAbac(pdpInstance);
                }
                case EASY_YAML: {
                    Yaml yaml = new Yaml();

                    easyPolicy = yaml.loadAs(policy, EasyPolicy.class);

                    easyAttributeModel = yaml.loadAs(attributes, EasyAttributeModel.class);
                }

            }


            PolicyInitializer policyInitializer = new PolicyInitializer();
            PDP pdpInstance = policyInitializer.newPDPInstance(easyPolicy, easyAttributeModel, datasources);

            return new EasyAbac(pdpInstance);
        }
    }


}
