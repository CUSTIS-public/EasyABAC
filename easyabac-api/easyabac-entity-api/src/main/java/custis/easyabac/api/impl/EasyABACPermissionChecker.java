package custis.easyabac.api.impl;

import custis.easyabac.api.EntityPermissionChecker;
import custis.easyabac.api.NotPermittedException;
import custis.easyabac.api.attr.imp.AttributiveAuthAction;
import custis.easyabac.api.attr.imp.AttributiveAuthEntity;
import custis.easyabac.api.utils.ResourceActionPair;
import custis.easyabac.core.pdp.AuthAttribute;
import custis.easyabac.core.pdp.AuthResponse;
import custis.easyabac.core.pdp.AuthService;
import custis.easyabac.core.pdp.RequestId;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static custis.easyabac.api.impl.AttributeValueExtractor.extract;

@Slf4j
public class EasyABACPermissionChecker<T, A> implements EntityPermissionChecker<T, A> {

    private AuthService authService;

    public EasyABACPermissionChecker(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void ensurePermitted(T entity, A operation) throws NotPermittedException {
        AuthResponse response = authService.authorize(extract(entity, operation));
        if (response.getDecision() != AuthResponse.Decision.PERMIT) {
            final String[] details = {""};
            if (operation instanceof AttributiveAuthAction) {
                AttributiveAuthAction authAction = (AttributiveAuthAction) operation;
                details[0] += ", " + authAction.getAuthAttribute().getId() + ":" + authAction.getAuthAttribute().getValues().stream().map(e -> e.toString()).reduce("", String::concat);
            }
            if (entity instanceof AttributiveAuthEntity) {
                AttributiveAuthEntity authEntity = (AttributiveAuthEntity) entity;
                authEntity.getAuthAttributes().stream().forEach(e -> {
                    details[0] += ", " + e.getId() + ":" + e.getValues();
                });
            }
            throw new NotPermittedException("Not permitted " + details[0] +" "+ operation);
        }
    }

    @Override
    public void ensurePermittedAll(Map<T, A> operationsMap) throws NotPermittedException {
        Map<RequestId, List<AuthAttribute>> attributes = operationsMap.entrySet().stream()
                .map(taEntry -> extract(taEntry.getKey(), taEntry.getValue()))
                .collect(Collectors.toMap(o -> RequestId.newRandom(), o -> o));

        Map<RequestId, AuthResponse> results = authService.authorizeMultiple(attributes);

        for (AuthResponse result : results.values()) {
            if (result.getDecision() != AuthResponse.Decision.PERMIT) {
                throw new NotPermittedException("Not permitted");
            }
        }
    }

    @Override
    public void ensurePermittedAll(T entity, List<A> operations) throws NotPermittedException {
        Map<RequestId, List<AuthAttribute>> attributes = operations.stream()
                .map(operation -> extract(entity, operation))
                .collect(Collectors.toMap(o -> RequestId.newRandom(), o -> o));

        Map<RequestId, AuthResponse> results = authService.authorizeMultiple(attributes);
        for (AuthResponse result : results.values()) {
            if (result.getDecision() != AuthResponse.Decision.PERMIT) {
                throw new NotPermittedException("Not permitted");
            }
        }
    }

    @Override
    public void ensurePermittedAny(Map<T, A> operationsMap) throws NotPermittedException {
        Map<RequestId, List<AuthAttribute>> attributes = operationsMap.entrySet().stream()
                .map(taEntry -> extract(taEntry.getKey(), taEntry.getValue()))
                .collect(Collectors.toMap(o -> RequestId.newRandom(), o -> o));

        Map<RequestId, AuthResponse> results = authService.authorizeMultiple(attributes);

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
                .map(operation -> extract(entity, operation))
                .collect(Collectors.toMap(o -> RequestId.newRandom(), o -> o));

        Map<RequestId, AuthResponse> results = authService.authorizeMultiple(attributes);

        for (AuthResponse result : results.values()) {
            if (result.getDecision() == AuthResponse.Decision.PERMIT) {
                return;
            }
        }

        throw new NotPermittedException("Not permitted");
    }

    @Override
    public List<A> getPermittedActions(T entity, List<A> operations) {
        Map<RequestId, A> clientMapping = new HashMap<>();
        Map<RequestId, List<AuthAttribute>> attributes = new HashMap<>();
        for (A operation : operations) {
            RequestId reqId = RequestId.newRandom();
            attributes.put(reqId, extract(entity, operation));
            clientMapping.put(reqId, operation);
        }

        List<A> out = new ArrayList<>();
        Map<RequestId, AuthResponse> results = authService.authorizeMultiple(attributes);
        for (Map.Entry<RequestId, AuthResponse> entry : results.entrySet()) {
            if (entry.getValue().getDecision() == AuthResponse.Decision.PERMIT) {
                out.add(clientMapping.get(entry.getKey()));
            }
        }
        return out;
    }

    @Override
    public Map<T, List<A>> getPermittedActions(Map<T, List<A>> operationsMap) {
        Map<RequestId, ResourceActionPair> clientMapping = new HashMap<>();
        Map<RequestId, List<AuthAttribute>> attributes = new HashMap<>();

        for (Map.Entry<T, List<A>> entry : operationsMap.entrySet()) {
            T entity = entry.getKey();
            List<A> operations = entry.getValue();

            for (A operation : operations) {
                RequestId reqId = RequestId.newRandom();
                attributes.put(reqId, extract(entity, operation));
                clientMapping.put(reqId, new ResourceActionPair(entity, operation));
            }

        }

        Map<T, List<A>> out = new HashMap<>();
        Map<RequestId, AuthResponse> results = authService.authorizeMultiple(attributes);
        for (Map.Entry<RequestId, AuthResponse> entry : results.entrySet()) {
            if (entry.getValue().getDecision() == AuthResponse.Decision.PERMIT) {

                ResourceActionPair resourceAndAction = clientMapping.get(entry.getKey());
                List<A> actions = out.computeIfAbsent((T) resourceAndAction.getResource(), t -> new ArrayList<>());
                actions.add((A) resourceAndAction.getAction());
            }
        }
        return out;


    }

    @Override
    public Map<T, List<A>> getPermittedActions(List<T> entities, List<A> operations) {
        Map<RequestId, ResourceActionPair> clientMapping = new HashMap<>();
        Map<RequestId, List<AuthAttribute>> attributes = new HashMap<>();

        for (T entity : entities) {
            for (A operation : operations) {
                RequestId reqId = RequestId.newRandom();
                attributes.put(reqId, extract(entity, operation));
                clientMapping.put(reqId, new ResourceActionPair(entity, operation));
            }

        }

        Map<T, List<A>> out = new HashMap<>();
        Map<RequestId, AuthResponse> results = authService.authorizeMultiple(attributes);
        for (Map.Entry<RequestId, AuthResponse> entry : results.entrySet()) {
            if (entry.getValue().getDecision() == AuthResponse.Decision.PERMIT) {

                ResourceActionPair resourceAndAction = clientMapping.get(entry.getKey());
                List<A> actions = out.computeIfAbsent((T) resourceAndAction.getResource(), t -> new ArrayList<>());
                actions.add((A) resourceAndAction.getAction());
            }
        }
        return out;
    }


}
