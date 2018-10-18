package custis.easyabac.demo.authz.abac;

import custis.easyabac.api.NotPermittedException;
import custis.easyabac.core.pdp.AttributiveAuthorizationService;
import custis.easyabac.demo.authz.DemoPermissionChecker;
import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.permissionchecker.OrderPermissionChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AbacDemoPermissionChecker implements DemoPermissionChecker {

    @Autowired
    private AttributiveAuthorizationService attributiveAuthorizationService;

    @Autowired
    private OrderPermissionChecker orderPermissionChecker;

    /**
     * Case 1. Для просмотра заказа требуется роль VIEW
     * @param order заказ
     * @return true - разрешено
     */
    @Override
    public void сanView(Order order) {
        orderPermissionChecker.ensurePermitted(order, OrderAction.VIEW);
    }

    @Override
    public void canCreate(Order order) throws NotPermittedException {
        orderPermissionChecker.ensurePermitted(order, OrderAction.CREATE);
    }

    @Override
    public void canApprove(Order order) {
        orderPermissionChecker.ensurePermitted(order, OrderAction.APPROVE);
    }

    @Override
    public void checkReject(Order order) {
        orderPermissionChecker.ensurePermitted(order, OrderAction.REJECT);
    }

}
