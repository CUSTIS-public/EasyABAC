package custis.easyabac.api.core.call.getters;

import custis.easyabac.api.core.PermissionCheckerMetadata;

public abstract class AbstractRequestGenerator implements RequestGenerator {

    protected final Class<?> resourceType;
    protected final Class<?> actionType;


    protected AbstractRequestGenerator(PermissionCheckerMetadata metadata) {
        this.resourceType = metadata.getResourceType();
        this.actionType = metadata.getActionType();
    }

}
