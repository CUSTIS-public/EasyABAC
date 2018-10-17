package custis.easyabac.api.checking;

import custis.easyabac.api.NotExpectedResultException;
import custis.easyabac.api.PermissionChecker;
import custis.easyabac.api.model.Order;
import custis.easyabac.api.model.OrderAction;

import java.util.List;
import java.util.Map;

public interface EnsureMethods extends PermissionChecker<Order, OrderAction> {

    void ensureDenied(Order order, OrderAction action) throws NotExpectedResultException;

    void ensureDenied(OrderAction action, Order order) throws NotExpectedResultException;

    void ensureDeniedAll(Order order, List<OrderAction> actions) throws NotExpectedResultException;

    void ensureDeniedAll(List<OrderAction> actions, Order order) throws NotExpectedResultException;

    void ensureDeniedView(Order order) throws NotExpectedResultException;

    void ensureDeniedViewOrApprove(Order order) throws NotExpectedResultException;

    void ensureDeniedAll(OrderAction action, List<Order> orders) throws NotExpectedResultException;

    void ensureDeniedAll(List<Order> orders, OrderAction action) throws NotExpectedResultException;

    void ensureDeniedAll(List<Order> orders, List<OrderAction> actions) throws NotExpectedResultException;

    void ensureDeniedAll_2(List<OrderAction> actions, List<Order> orders) throws NotExpectedResultException;

    void ensureDeniedAll(Map<Order, OrderAction> orderActionMap) throws NotExpectedResultException;

    void ensureDeniedAll_2(Map<OrderAction, Order> orderActionMap) throws NotExpectedResultException;

    void ensureDeniedAll_3(Map<Order, List<OrderAction>> orderActionMap) throws NotExpectedResultException;

    void ensureDeniedAll_4(Map<OrderAction, List<Order>> orderActionMap) throws NotExpectedResultException;

    void ensureDeniedAll_5(Map<List<Order>, OrderAction> orderActionMap) throws NotExpectedResultException;

    void ensureDeniedAll_6(Map<List<OrderAction>, Order> orderActionMap) throws NotExpectedResultException;

    void ensureDeniedAll_7(Map<List<Order>, List<OrderAction>> orderActionMap) throws NotExpectedResultException;

    void ensureDeniedAll_8(Map<List<OrderAction>, List<Order>> orderActionMap) throws NotExpectedResultException;
}
