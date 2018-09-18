package ru.custis.easyabac.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.PDP;
import org.wso2.balana.ParsingException;
import org.wso2.balana.ctx.*;
import org.wso2.balana.ctx.xacml3.Result;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.custis.easyabac.core.models.attribute.Attribute;
import ru.custis.easyabac.core.models.policy.simple.SimplePolicy;

import java.util.ArrayList;
import java.util.List;

public class EasyAbac implements EasyAbacInit, EasyAbacAuth {

    private final static Log log = LogFactory.getLog(EasyAbac.class);

    private PDP pdpInstance;


    public void initInstanceSimplePolicy(String policy, String attributes) {
        Constructor constructor = new Constructor(SimplePolicy.class);
        Yaml yaml = new Yaml();
        SimplePolicy simplePolicy = yaml.loadAs(policy, SimplePolicy.class);

    }

    public void initInstanceSimplePolicy(SimplePolicy simplePolicy, List<Attribute> attributes) {

    }

    public void initInstanceXacmlPolicy(String policyXacml, String attributes) {
        PolicyInitializer policyInitializer = new PolicyInitializer();
        pdpInstance = policyInitializer.getPDPNewInstance(policyXacml);
    }

    public String getXacmlPolicy() {
        return "";
    }

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
