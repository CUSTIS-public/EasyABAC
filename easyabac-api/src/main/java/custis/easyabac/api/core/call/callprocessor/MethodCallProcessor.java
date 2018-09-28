package custis.easyabac.api.core.call.callprocessor;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.call.DecisionType;
import custis.easyabac.api.core.call.MethodType;
import custis.easyabac.api.core.call.converters.ResultConverter;
import custis.easyabac.api.core.call.getters.AttributesValuesGetter;
import custis.easyabac.api.core.call.getters.AttributesValuesGetterFactory;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.RequestId;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class MethodCallProcessor {
    protected final AttributiveAuthorizationService attributiveAuthorizationService;
    protected final Method method;
    protected final PermissionCheckerInformation permissionCheckerInformation;
    protected final MethodType methodType;
    protected final DecisionType decisionType;
    protected AttributesValuesGetter attributesValuesGetter;
    protected ResultConverter resultConverter;

    protected MethodCallProcessor(PermissionCheckerInformation permissionCheckerInformation, Method method, AttributiveAuthorizationService attributiveAuthorizationService) {
        this.permissionCheckerInformation = permissionCheckerInformation;
        this.method = method;
        this.attributiveAuthorizationService = attributiveAuthorizationService;
        this.methodType = MethodType.findByMethod(method);
        this.decisionType = DecisionType.findByMethod(method, methodType);
    }

    public Object execute(Object[] arguments) {
        Map<RequestId, List<AuthAttribute>> req = attributesValuesGetter.getAttributes(arguments);
        Map<RequestId, AuthResponse> responses = attributiveAuthorizationService.authorizeMultiple(req);
        return resultConverter.convert(arguments, responses);
    }

    protected abstract Optional<AttributesValuesGetter> prepareCustomAttributesValuesGetter();

    protected abstract ResultConverter prepareResultConverter();

    public void afterPropertiesSet() {
        this.attributesValuesGetter = prepareAttributesValuesGetter();
        this.resultConverter = prepareResultConverter();
    }

    private AttributesValuesGetter prepareAttributesValuesGetter() {
        Optional<AttributesValuesGetter> optional = prepareCustomAttributesValuesGetter();
        if (optional.isPresent()) {
            return optional.get();
        }
        return AttributesValuesGetterFactory.prepareDefault(method, permissionCheckerInformation, methodType, decisionType);
    }


}

