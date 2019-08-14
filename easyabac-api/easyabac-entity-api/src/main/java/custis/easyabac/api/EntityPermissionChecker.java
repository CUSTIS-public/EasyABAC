package custis.easyabac.api;

import java.util.List;
import java.util.Map;

@NoPermissionCheckerBean
public interface EntityPermissionChecker<T, A> {

    // check that an action is available for the entity * /
    void ensurePermitted(T entity, A operation) throws NotPermittedException;

    // check that at least one action is available for the entity * /
    void ensurePermittedAny(T entity, List<A> operations) throws NotPermittedException;

    // check that all actions are available for the entity * /
    void ensurePermittedAll(T entity, List<A> operations) throws NotPermittedException ;

    // check that all actions are available for the entity * /
    void ensurePermittedAll(Map<T, A> operationsMap) throws NotPermittedException ;

    // check that at least one action is available for the entity * /
    void ensurePermittedAny(Map<T, A> operationsMap) throws NotPermittedException;

    // get the list of available actions from the list for the entity * /
    List<A> getPermittedActions(T entity, List<A> operations);

    // get a list of available actions for entities in accordance with the map-oh * /
    Map<T, List<A>> getPermittedActions(Map<T, List<A>> operationsMap);

    // get a list of available actions from the list for each entity * /
    Map<T, List<A>> getPermittedActions(List<T> entities, List<A> operations);
}
