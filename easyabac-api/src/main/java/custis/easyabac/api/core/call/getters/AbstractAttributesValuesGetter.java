package custis.easyabac.api.core.call.getters;

import custis.easyabac.api.core.PermissionCheckerMetadata;

public abstract class AbstractAttributesValuesGetter implements AttributesValuesGetter {

    private final PermissionCheckerMetadata permissionCheckerInformation;
    protected final Class<?> resourceType;
    protected final Class<?> actionType;

    protected AbstractAttributesValuesGetter(PermissionCheckerMetadata permissionCheckerInformation) {
        this.permissionCheckerInformation = permissionCheckerInformation;
        this.resourceType = permissionCheckerInformation.getResourceType();
        this.actionType = permissionCheckerInformation.getActionType();
    }
}
