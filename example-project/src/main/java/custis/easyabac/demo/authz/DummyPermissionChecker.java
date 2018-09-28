package custis.easyabac.demo.authz;

import custis.easyabac.api.NotPermittedException;
import custis.easyabac.demo.model.Order;

public interface DummyPermissionChecker {

    void сanView(Order order) throws NotPermittedException;

    void canCreate(Order order) throws NotPermittedException;

    void canApprove(Order order) throws NotPermittedException;

    void checkReject(Order order) throws NotPermittedException;
}
