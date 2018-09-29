package custis.easyabac.api.impl;

import custis.easyabac.api.ConcreteUserPermissionChecker;
import custis.easyabac.api.NotPermittedException;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.RequestId;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static custis.easyabac.api.impl.AttributeValueExtractor.collectAttributes;

@Slf4j
public class EasyABACPermissionChecker<T, A, U> implements ConcreteUserPermissionChecker<T, A, U> {

    private AttributiveAuthorizationService attributiveAuthorizationService;

    public EasyABACPermissionChecker(AttributiveAuthorizationService attributiveAuthorizationService) {
        this.attributiveAuthorizationService = attributiveAuthorizationService;
    }

    @Override
    public AuthResponse.Decision authorize(T object, A action) {
        AuthResponse response = attributiveAuthorizationService.authorize(collectAttributes(object, action));
        return response.getDecision();
    }

    @Override
    public void ensurePermitted(T entity, A operation) throws NotPermittedException {
        if (authorize(entity, operation) != AuthResponse.Decision.PERMIT) {
            throw new NotPermittedException("Not permitted " + operation);
        }
    }

    @Override
    public void ensurePermittedAll(Map<T, A> operationsMap) throws NotPermittedException {
        Map<RequestId, List<AuthAttribute>> attributes = operationsMap.entrySet().stream()
                .map(taEntry -> collectAttributes(taEntry.getKey(), taEntry.getValue()))
                .collect(Collectors.toMap(o -> RequestId.newRandom(), o -> o));

        Map<RequestId, AuthResponse> results = attributiveAuthorizationService.authorizeMultiple(attributes);

        for (AuthResponse result : results.values()) {
            if (result.getDecision() != AuthResponse.Decision.PERMIT) {
                throw new NotPermittedException("Not permitted");
            }
        }
    }

    @Override
    public void ensurePermittedAll(T entity, List<A> operations) throws NotPermittedException {
        Map<RequestId, List<AuthAttribute>> attributes = operations.stream()
                .map(operation -> collectAttributes(entity, operation))
                .collect(Collectors.toMap(o -> RequestId.newRandom(), o -> o));

        Map<RequestId, AuthResponse> results = attributiveAuthorizationService.authorizeMultiple(attributes);
        for (AuthResponse result : results.values()) {
            if (result.getDecision() != AuthResponse.Decision.PERMIT) {
                throw new NotPermittedException("Not permitted");
            }
        }
    }

    @Override
    public void ensurePermittedAny(Map<T, A> operationsMap) throws NotPermittedException {
        Map<RequestId, List<AuthAttribute>> attributes = operationsMap.entrySet().stream()
                .map(taEntry -> collectAttributes(taEntry.getKey(), taEntry.getValue()))
                .collect(Collectors.toMap(o -> RequestId.newRandom(), o -> o));

        Map<RequestId, AuthResponse> results = attributiveAuthorizationService.authorizeMultiple(attributes);

        for (AuthResponse result : results.values()) {
            if (result.getDecision() == AuthResponse.Decision.PERMIT) {
                return;
            }
        }
        throw new NotPermittedException("Not permitted");
    }

    @Override
    public void ensurePermittedAny(T entity, List<A> operations) throws NotPermittedException {
        Map<RequestId, List<AuthAttribute>> attributes = operations.stream()
                .map(operation -> collectAttributes(entity, operation))
                .collect(Collectors.toMap(o -> RequestId.newRandom(), o -> o));

        Map<RequestId, AuthResponse> results = attributiveAuthorizationService.authorizeMultiple(attributes);

        for (AuthResponse result : results.values()) {
            if (result.getDecision() == AuthResponse.Decision.PERMIT) {
                return;
            }
        }

        throw new NotPermittedException("Not permitted");
    }

    @Override
    public List<A> getPermittedActions(T entity, List<A> operations) {
        return null;
    }

    @Override
    public Map<T, List<A>> getPermittedActions(Map<T, List<A>> operationsMap) {
        return null;
    }

    @Override
    public Map<T, List<A>> getPermittedActions(List<T> entities, List<A> operations) {
        return null;
    }


    @Override
    public void ensurePermittedForUser(T entity, A operation, U user) throws NotPermittedException {

    }

    @Override
    public void ensurePermittedAnyForUser(T entity, List<A> operations, U user) throws NotPermittedException {

    }

    @Override
    public void ensurePermittedAllForUser(T entity, List<A> operations, U user) throws NotPermittedException {

    }

    @Override
    public void ensurePermittedAllForUser(Map<T, A> operationsMap, U user) throws NotPermittedException {

    }

    @Override
    public void ensurePermittedAnyForUser(Map<T, A> operationsMap, U user) throws NotPermittedException {

    }

    @Override
    public List<A> getPermittedActionsForUser(T entity, List<A> operations, U user) {
        return null;
    }

    @Override
    public Map<T, List<A>> getPermittedActionsForUser(Map<T, List<A>> operationsMap, U user) {
        return null;
    }

    @Override
    public Map<T, List<A>> getPermittedActionsForUser(List<T> entities, List<A> operations, U user) {
        return null;
    }
}
