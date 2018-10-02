package custis.easyabac.demo.permissionchecker;

import custis.easyabac.api.NotExpectedResultException;
import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.demo.authz.abac.OrderAction;
import custis.easyabac.demo.model.Order;

import java.util.List;

public interface OrderPermissionChecker extends PermitAwarePermissionChecker<Order, OrderAction> {

    /*void ensurePermittedRead(Order order) throws NotExpectedResultException;

    boolean isPermitted(Order order, OrderAction action);

    boolean isPermittedRead(Order order);

    boolean isPermittedReadOrApprove(Order order);

    void ensureDenied(Order order, OrderAction action) throws NotExpectedResultException;*/

    void ensureDeniedAll(Order order, List<OrderAction> actions) throws NotExpectedResultException;

    /*void ensureDeniedAny(Order order, List<OrderAction> actions) throws NotExpectedResultException;


    void ensureDeniedAll(Map<Order, OrderAction> actionsMap) throws NotExpectedResultException;

    void ensureDeniedAny(Map<Order, OrderAction> actionsMap) throws NotExpectedResultException;*/
}
