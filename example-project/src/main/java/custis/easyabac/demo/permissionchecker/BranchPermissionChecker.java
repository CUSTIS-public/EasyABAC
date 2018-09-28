package custis.easyabac.demo.permissionchecker;

import custis.easyabac.api.ConcreteUserPermissionChecker;
import custis.easyabac.demo.authz.abac.OrderAction;
import custis.easyabac.demo.model.Branch;

public interface BranchPermissionChecker extends ConcreteUserPermissionChecker<Branch, OrderAction> {

}
