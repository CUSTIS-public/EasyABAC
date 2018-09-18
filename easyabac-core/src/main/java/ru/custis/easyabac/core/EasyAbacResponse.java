package ru.custis.easyabac.core;

import org.wso2.balana.ctx.ResponseCtx;

public class EasyAbacResponse {
    private final ResponseCtx responseCtx;


    public EasyAbacResponse(ResponseCtx responseCtx) {
        this.responseCtx = responseCtx;
    }


    public Decision getDesicion() {
        return Decision.getByIndex(responseCtx.getResults().iterator().next().getDecision());
    }

}
