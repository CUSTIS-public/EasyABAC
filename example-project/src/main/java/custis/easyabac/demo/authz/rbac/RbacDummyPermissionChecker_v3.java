package custis.easyabac.demo.authz.rbac;

import custis.easyabac.api.NotPermittedException;
import custis.easyabac.demo.authz.AuthenticationContext;
import custis.easyabac.demo.authz.DummyPermissionChecker;
import custis.easyabac.demo.model.Branch;
import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.model.User;
import org.springframework.stereotype.Service;

import static custis.easyabac.demo.authz.rbac.Role.*;

@Service
public class RbacDummyPermissionChecker_v3 implements DummyPermissionChecker {

    /**
     * Case 3. Для просмотра заказа требуется роль VIEW и должен совпадать филиал пользователя и заказа
     * @param order заказ
     * @return true - разрешено
     */
    public void сanView(Order order) {
        User user = AuthenticationContext.currentUser();
        checkBranchRole(user, order.getBranch());
        if (user.hasRole(ROLE_MANAGER.name()) || user.hasRole(ROLE_OPERATOR.name())) {
            return;
        }
        throw new NotPermittedException("not permitted");
    }

    @Override
    public void canCreate(Order order) throws NotPermittedException {
        User user = AuthenticationContext.currentUser();
        checkBranchRole(user, order.getBranch());
        if (user.hasRole(ROLE_OPERATOR.name())) {
            return;
        }
        throw new NotPermittedException("not permitted");
    }

    @Override
    public void canApprove(Order order) {
        User user = AuthenticationContext.currentUser();
        checkBranchRole(user, order.getBranch());
        if (user.hasRole(ROLE_MANAGER.name())) {
            return;
        }
        throw new NotPermittedException("not permitted");
    }

    @Override
    public void checkReject(Order order) {
        User user = AuthenticationContext.currentUser();
        checkBranchRole(user, order.getBranch());
        if (user.hasRole(ROLE_MANAGER.name())) {
            return;
        }
        throw new NotPermittedException("not permitted");
    }

    private void checkBranchRole(User user, Branch branch) throws NotPermittedException {
        if (user.hasRole(ROLE_USER.ofBranch(branch.getId()))) {
            return;
        }
        throw new NotPermittedException("not permitted");
    }
}
