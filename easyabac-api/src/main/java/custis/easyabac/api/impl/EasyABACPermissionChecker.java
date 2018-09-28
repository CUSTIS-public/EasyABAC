package custis.easyabac.api.impl;

import custis.easyabac.api.ConcreteUserPermissionChecker;
import custis.easyabac.api.NotPermittedException;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static custis.easyabac.api.impl.AttributeValueExtractor.collectAttributes;

@Slf4j
public class EasyABACPermissionChecker<T, A> implements ConcreteUserPermissionChecker<T, A> {

    private AttributiveAuthorizationService attributiveAuthorizationService;

    public EasyABACPermissionChecker(AttributiveAuthorizationService attributiveAuthorizationService) {
        this.attributiveAuthorizationService = attributiveAuthorizationService;
    }

    @Override
    public AuthResponse.AuthResult authorize(T object, A action) {
        AuthResponse response = attributiveAuthorizationService.authorize(collectAttributes(object, action));
        return response.getResult();
    }

    @Override
    public void ensurePermitted(T entity, A operation) throws NotPermittedException {
        if (authorize(entity, operation) != AuthResponse.AuthResult.PERMIT) {
            throw new NotPermittedException("Not permitted " + operation);
        }
    }

    @Override
    public void ensurePermittedAll(Map<T, A> operationsMap) throws NotPermittedException {
        List<List<AuthAttribute>> attributes = operationsMap.entrySet().stream()
                .map(taEntry -> collectAttributes(taEntry.getKey(), taEntry.getValue()))
                .collect(Collectors.toList());
        List<AuthResponse> results = attributiveAuthorizationService.authorizeMultiple(attributes);
        for (AuthResponse result : results) {
            if (result.getResult() != AuthResponse.AuthResult.PERMIT) {
                throw new NotPermittedException("Not permitted");
            }
        }
    }

    @Override
    public void ensurePermittedAll(T entity, List<A> operations) throws NotPermittedException {
        List<List<AuthAttribute>> attributes = operations.stream()
                .map(operation -> collectAttributes(entity, operation))
                .collect(Collectors.toList());
        List<AuthResponse> results = attributiveAuthorizationService.authorizeMultiple(attributes);
        for (AuthResponse result : results) {
            if (result.getResult() != AuthResponse.AuthResult.PERMIT) {
                throw new NotPermittedException("Not permitted");
            }
        }
    }

    @Override
    public void ensurePermittedAny(Map<T, A> operationsMap) throws NotPermittedException {
        List<List<AuthAttribute>> attributes = operationsMap.entrySet().stream()
                .map(taEntry -> collectAttributes(taEntry.getKey(), taEntry.getValue()))
                .collect(Collectors.toList());
        List<AuthResponse> results = attributiveAuthorizationService.authorizeMultiple(attributes);
        for (AuthResponse result : results) {
            if (result.getResult() == AuthResponse.AuthResult.PERMIT) {
                return;
            }
        }
        throw new NotPermittedException("Not permitted");
    }

    @Override
    public void ensurePermittedAny(T entity, List<A> operations) throws NotPermittedException {
        List<List<AuthAttribute>> attributes = operations.stream()
                .map(operation -> collectAttributes(entity, operation))
                .collect(Collectors.toList());
        List<AuthResponse> results = attributiveAuthorizationService.authorizeMultiple(attributes);
        for (AuthResponse result : results) {
            if (result.getResult() == AuthResponse.AuthResult.PERMIT) {
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

}
