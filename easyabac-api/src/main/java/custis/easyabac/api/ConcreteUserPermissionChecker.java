package custis.easyabac.api;

@NoPermissionCheckerBean
public interface ConcreteUserPermissionChecker<T, A> extends PermitAwarePermissionChecker<T, A> {


}
