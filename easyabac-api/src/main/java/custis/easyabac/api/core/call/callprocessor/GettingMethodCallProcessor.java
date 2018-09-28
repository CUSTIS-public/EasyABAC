package custis.easyabac.api.core.call.callprocessor;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.call.converters.GettingResultConverter;
import custis.easyabac.api.core.call.converters.ResultConverter;
import custis.easyabac.api.core.call.getters.AttributesValuesGetter;
import custis.easyabac.pdp.AttributiveAuthorizationService;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Call Processor for dynamic methods
 */
public class GettingMethodCallProcessor extends MethodCallProcessor {

    public GettingMethodCallProcessor(PermissionCheckerInformation permissionCheckerInformation, Method method, AttributiveAuthorizationService attributiveAuthorizationService) {
        super(permissionCheckerInformation, method, attributiveAuthorizationService);

        // method return type
    }

    @Override
    protected ResultConverter prepareResultConverter() {
        return new GettingResultConverter(methodType, decisionType, permissionCheckerInformation);
    }


    @Override
    protected Optional<AttributesValuesGetter> prepareCustomAttributesValuesGetter() {
        return Optional.empty();
    }
}
