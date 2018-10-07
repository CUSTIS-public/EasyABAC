package custis.easyabac.demo.permissionchecker;

import custis.easyabac.api.NotExpectedResultException;
import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.demo.authz.abac.OrderAction;
import custis.easyabac.demo.model.Order;

import java.util.List;
import java.util.Map;

public interface OrderPermissionChecker extends PermitAwarePermissionChecker<Order, OrderAction> {

    void ensureDenied(Order order, OrderAction action) throws NotExpectedResultException;

    void ensureIndeterminateAll(Order order, List<OrderAction> action) throws NotExpectedResultException;




    void ensureDeniedRead(Order order) throws NotExpectedResultException;

    void ensurePermittedReadOrApprove(Order order) throws NotExpectedResultException;

    List<OrderAction> getDeniedActions(Order order, List<OrderAction> actions);

    List<Order> getPermittedResources(List<Order> orders, OrderAction action);

    Map<Order, List<OrderAction>> getPermittedActions(List<Order> orders, OrderAction action);

    Map<OrderAction, List<Order>> getPermittedResources(List<OrderAction> actions, List<Order> orders);


    void ensureDeniedAll(Order order, List<OrderAction> actions) throws NotExpectedResultException;

    boolean isPermitted(Order order);

    /*void ensureDeniedAny(Order order, List<OrderAction> actions) throws NotExpectedResultException;


    void ensureDeniedAll(Map<Order, OrderAction> actionsMap) throws NotExpectedResultException;

    void ensureDeniedAny(Map<Order, OrderAction> actionsMap) throws NotExpectedResultException;*/
}
