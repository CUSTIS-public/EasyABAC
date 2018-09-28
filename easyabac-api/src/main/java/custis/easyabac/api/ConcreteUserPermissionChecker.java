package custis.easyabac.api;

import java.util.List;
import java.util.Map;

@NoPermissionCheckerBean
public interface ConcreteUserPermissionChecker<T, A, U> extends PermitAwarePermissionChecker<T, A> {

    /**
     * проверить, что для сущности доступно действие
     *
     */
    void ensurePermittedForUser(T entity, A operation, U user) throws NotPermittedException;

    /**
     * проверить, что для сущности доступно хотя бы одно действие
     */
    void ensurePermittedAnyForUser(T entity, List<A> operations, U user) throws NotPermittedException;

    void ensurePermittedAllForUser(T entity, List<A> operations, U user) throws NotPermittedException ;


    /**
     * проверить, что для сущности доступны все действия
     */
    void ensurePermittedAllForUser(Map<T, A> operationsMap, U user) throws NotPermittedException ;

    /**
     * проверить, что для сущности доступно хотя бы одно действие
     */
    void ensurePermittedAnyForUser(Map<T, A> operationsMap, U user) throws NotPermittedException;



    /**
     * получить список доступных действий из списка для сущности
     */
    List<A> getPermittedActionsForUser(T entity, List<A> operations, U user);

    /**
     *  получить список доступных действий для сущностей в соответствии с map-ой
     */
    Map<T, List<A>> getPermittedActionsForUser(Map<T, List<A>> operationsMap, U user);

    /**
     * получить список доступных действий из списка для каждой сущности
     */
    Map<T, List<A>> getPermittedActionsForUser(List<T> entities, List<A> operations, U user);
}
