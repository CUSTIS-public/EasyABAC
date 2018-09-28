package custis.easyabac.demo.authz.rbac;

import custis.easyabac.api.NotPermittedException;
import custis.easyabac.demo.authz.AuthenticationContext;
import custis.easyabac.demo.authz.DummyPermissionChecker;
import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.model.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import static custis.easyabac.demo.authz.rbac.Role.ROLE_MANAGER;
import static custis.easyabac.demo.authz.rbac.Role.ROLE_OPERATOR;

// TODO JavaDoc для показа
@Service
@Primary
public class RbacDummyPermissionChecker_v1 implements DummyPermissionChecker {

    /**
     * Case 1. Для просмотра заказа требуется роль Менеджера или Операциониста
     * @param order заказ
     * @return true - разрешено
     */
    @Override
    public void сanView(Order order) {
        User user = AuthenticationContext.currentUser();
        if (user.hasRole(ROLE_MANAGER.name()) || user.hasRole(ROLE_OPERATOR.name())) {
            return;
        }
        throw new NotPermittedException("not permitted");
    }

    @Override
    public void canCreate(Order order) throws NotPermittedException {
        User user = AuthenticationContext.currentUser();
        if (user.hasRole(ROLE_OPERATOR.name())) {
            return;
        }
        throw new NotPermittedException("not permitted");
    }

    @Override
    public void canApprove(Order order) {
        User user = AuthenticationContext.currentUser();
        if (user.hasRole(ROLE_MANAGER.name())) {
            return;
        }
        throw new NotPermittedException("not permitted");
    }

    @Override
    public void checkReject(Order order) {
        User user = AuthenticationContext.currentUser();
        if (user.hasRole(ROLE_MANAGER.name())) {
            return;
        }
        throw new NotPermittedException("not permitted");
    }

}
