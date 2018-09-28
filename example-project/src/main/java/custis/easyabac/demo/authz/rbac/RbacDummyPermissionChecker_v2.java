package custis.easyabac.demo.authz.rbac;

import custis.easyabac.api.NotPermittedException;
import custis.easyabac.demo.authz.AuthenticationContext;
import custis.easyabac.demo.authz.DummyPermissionChecker;
import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.model.User;
import org.springframework.stereotype.Service;

import static custis.easyabac.demo.authz.rbac.Role.ROLE_MANAGER;
import static custis.easyabac.demo.authz.rbac.Role.ROLE_OPERATOR;

@Service
public class RbacDummyPermissionChecker_v2 implements DummyPermissionChecker {

    /**
     * Case 2. Для просмотра заказа требуется роль VIEW_ORDER_N, где N - id магазина, в котором сделан заказ
     * @param order заказ
     * @return true - разрешено
     */
    public void сanView(Order order) {
        User user = AuthenticationContext.currentUser();
        if (user.hasRole(ROLE_MANAGER.ofBranch(order.getBranchId()))
                || user.hasRole(ROLE_OPERATOR.ofBranch(order.getBranchId()))) {
            return;
        }
        throw new NotPermittedException("not permitted");
    }

    @Override
    public void canCreate(Order order) throws NotPermittedException {
        User user = AuthenticationContext.currentUser();
        if (user.hasRole(ROLE_OPERATOR.ofBranch(order.getBranchId()))) {
            return;
        }
        throw new NotPermittedException("not permitted");
    }

    @Override
    public void canApprove(Order order) {
        User user = AuthenticationContext.currentUser();
        if (user.hasRole(ROLE_MANAGER.ofBranch(order.getBranchId()))) {
            return;
        }
        throw new NotPermittedException("not permitted");
    }

    @Override
    public void checkReject(Order order) {
        User user = AuthenticationContext.currentUser();
        if (user.hasRole(ROLE_MANAGER.ofBranch(order.getBranchId()))) {
            return;
        }
        throw new NotPermittedException("not permitted");
    }
}
