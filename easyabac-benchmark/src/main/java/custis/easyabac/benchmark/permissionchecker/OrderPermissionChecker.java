package custis.easyabac.benchmark.permissionchecker;

import custis.easyabac.api.NotExpectedResultException;
import custis.easyabac.api.PermissionChecker;
import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;

public interface OrderPermissionChecker extends PermissionChecker<Order, OrderAction> {

    void ensurePermittedApprove(Order order) throws NotExpectedResultException;
}
