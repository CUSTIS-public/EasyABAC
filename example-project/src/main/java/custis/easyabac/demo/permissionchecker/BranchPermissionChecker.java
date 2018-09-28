package custis.easyabac.demo.permissionchecker;

import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.demo.authz.abac.OrderAction;
import custis.easyabac.demo.model.Branch;

public interface BranchPermissionChecker extends PermitAwarePermissionChecker<Branch, OrderAction> {

}
