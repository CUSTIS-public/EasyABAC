package custis.easyabac.api;

import java.util.List;
import java.util.Map;

@NoPermissionCheckerBean
public interface PermitAwarePermissionChecker<T, A> {

    /** проверить, что для сущности доступно действие */
    void ensurePermitted(T entity, A operation) throws NotPermittedException;

    /** проверить, что для сущности доступно хотя бы одно действие */
    void ensurePermittedAny(T entity, List<A> operations) throws NotPermittedException;

    /** проверить, что для сущности доступны все действия */
    void ensurePermittedAll(T entity, List<A> operations) throws NotPermittedException ;

    /** проверить, что для сущности доступны все действия */
    void ensurePermittedAll(Map<T, A> operationsMap) throws NotPermittedException ;

    /** проверить, что для сущности доступно хотя бы одно действие */
    void ensurePermittedAny(Map<T, A> operationsMap) throws NotPermittedException;

    /** получить список доступных действий из списка для сущности */
    List<A> getPermittedActions(T entity, List<A> operations);

    /** получить список доступных действий для сущностей в соответствии с map-ой */
    Map<T, List<A>> getPermittedActions(Map<T, List<A>> operationsMap);

    /** получить список доступных действий из списка для каждой сущности */
    Map<T, List<A>> getPermittedActions(List<T> entities, List<A> operations);
}
