package ru.custis.easyabac.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.PDP;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.*;
import org.wso2.balana.ctx.xacml3.Result;
import org.yaml.snakeyaml.Yaml;
import ru.custis.easyabac.core.auth.EasyAbacAuth;
import ru.custis.easyabac.core.auth.EasyAbacRequest;
import ru.custis.easyabac.core.auth.EasyAbacResponse;
import ru.custis.easyabac.core.cache.Cache;
import ru.custis.easyabac.core.init.PolicyInitializer;
import ru.custis.easyabac.core.models.attribute.Datasource;
import ru.custis.easyabac.core.models.attribute.load.EasyAttributeModel;
import ru.custis.easyabac.core.models.policy.EasyPolicy;
import ru.custis.easyabac.core.trace.Trace;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EasyAbac implements EasyAbacAuth {

    private final static Log log = LogFactory.getLog(EasyAbac.class);

    private final PDP pdpInstance;


    private EasyAbac(PDP pdpInstance) {

        this.pdpInstance = pdpInstance;
    }


    public static class Builder {
        private final EasyPolicy easyPolicy;
        private final EasyAttributeModel easyAttributeModel;
        private List<Datasource> datasources;
        private Cache cache;
        private Trace trace;

        public Builder(String policy, String attributes) {

            Yaml yaml = new Yaml();

            easyPolicy = yaml.loadAs(policy, EasyPolicy.class);

            easyAttributeModel = yaml.loadAs(attributes, EasyAttributeModel.class);

        }

        public Builder(InputStream policy, InputStream attributes) {

            Yaml yaml = new Yaml();

            easyPolicy = yaml.loadAs(policy, EasyPolicy.class);

            easyAttributeModel = yaml.loadAs(attributes, EasyAttributeModel.class);
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

        public EasyAbacAuth build() {
            PolicyInitializer policyInitializer = new PolicyInitializer();
            PDP pdpInstance = policyInitializer.getPDPNewInstance(easyPolicy, easyAttributeModel, datasources);

            return new EasyAbac(pdpInstance);
        }
    }


//    public static EasyAbacAuth newInstance(String policy, String attributes, List<Datasource> datasources, Cache cache, Trace trace) {
//
//    }
//
//
//    public static EasyAbacAuth newInstance(InputStream policy, InputStream attributes, List<Datasource> datasources, Cache cache, Trace trace) {
//
//        EasyAbac easyAbac = new EasyAbac();
//
//        Yaml yaml = new Yaml();
//
//        EasyPolicy easyPolicy = yaml.loadAs(policy, EasyPolicy.class);
//
//        EasyAttributeModel easyAttributeModel = yaml.loadAs(attributes, EasyAttributeModel.class);
//
//    }
//
//    public static EasyAbacAuth newInstanceFromXacml(String policyXacml) {
//
//        PolicyInitializer policyInitializer = new PolicyInitializer();
//        PDP pdpInstance = policyInitializer.getPDPNewInstance(policyXacml);
//
//        EasyAbac easyAbac = new EasyAbac(pdpInstance);
//
//    }
//
//
//    public String getXacmlPolicy() {
//        return "";
//    }

    @Override
    public EasyAbacResponse auth(EasyAbacRequest request) {
        AbstractRequestCtx requestCtx;
        ResponseCtx responseCtx;
        try {
            requestCtx = RequestCtxFactory.getFactory().getRequestCtx(request.getXacmlRequest().replaceAll(">\\s+<", "><"));
            responseCtx = pdpInstance.evaluate(requestCtx);
        } catch (ParsingException e) {
            List<String> code = new ArrayList<>();
            code.add(Status.STATUS_SYNTAX_ERROR);
            String error = "Invalid request  : " + e.getMessage();
            Status status = new Status(code, error);
            responseCtx = new ResponseCtx(new Result(AbstractResult.DECISION_INDETERMINATE, status));
        }
        log.debug(responseCtx.encode());
        return new EasyAbacResponse(responseCtx);
    }
}
