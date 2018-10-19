package custis.easyabac.api.test;

import custis.easyabac.api.EntityPermissionChecker;
import custis.easyabac.api.NotPermittedException;
import custis.easyabac.api.impl.EasyABACPermissionChecker;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.datasource.Datasource;
import custis.easyabac.core.datasource.SimpleDatasource;
import custis.easyabac.core.pdp.AuthService;
import custis.easyabac.core.pdp.PdpHandlerFactory;
import custis.easyabac.core.pdp.balana.BalanaPdpHandlerFactory;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.attribute.Attribute;
import custis.easyabac.model.attribute.AttributeWithValue;
import custis.easyabac.model.easy.EasyAbacModelCreator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static custis.easyabac.api.impl.AttributeValueExtractor.SUBJECT_NAME;

@RunWith(Parameterized.class)
public abstract class EasyAbacBaseTestClass<T, A> {

    protected final AbacAuthModel model;
    private final PdpHandlerFactory pdpHandlerFactory = BalanaPdpHandlerFactory.PROXY_INSTANCE;

    public EasyAbacBaseTestClass(InputStream modelSource) throws EasyAbacInitException {
        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        this.model = creator.createModel(modelSource);
    }

    public EasyAbacBaseTestClass(AbacAuthModel model) {
        this.model = model;

    }

    @Parameterized.Parameter
    public T resource;

    @Parameterized.Parameter(value = 1)
    public A action;

    @Parameterized.Parameter(value = 2)
    public boolean expectedPermit;

    @Parameterized.Parameter(value = 3)
    public TestDescription testDescription;

    @Test
    public void authorizationTest() throws EasyAbacInitException {
        EntityPermissionChecker entityPermissionChecker = getPermissionChecker();
        if (expectedPermit) {
            entityPermissionChecker.ensurePermitted(resource, action);
        } else {
            try {
                entityPermissionChecker.ensurePermitted(resource, action);
                Assert.fail();
            } catch (NotPermittedException e) {
                // that's good
            }
        }
    }


    protected EntityPermissionChecker getPermissionChecker() throws EasyAbacInitException {
        EasyAbacBuilder builder = new EasyAbacBuilder(model, pdpHandlerFactory);

        // subject extender
        if (testDescription.containsAttributesByCode(SUBJECT_NAME)) {
            builder.subjectAttributesProvider(() -> testDescription.getAttributesByCode(SUBJECT_NAME).entrySet()
                    .stream()
                    .map(stringObjectEntry -> {
                        Attribute attribute = model.getAttributes().get(SUBJECT_NAME + "." + stringObjectEntry.getKey());
                        return new AttributeWithValue(attribute, Collections.singletonList(stringObjectEntry.getValue().toString()));
                    }).collect(Collectors.toList()));
        }

        // other datasources
        List<Datasource> datasources = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : testDescription.getAttributes().entrySet()) {
            String entryKey = entry.getKey();
            if (!entryKey.equals(SUBJECT_NAME) && !entryKey.equals("order")) {
                for (Map.Entry<String, Object> valEntry : entry.getValue().entrySet()) {
                    datasources.add(new SimpleDatasource(entry.getKey() + "." + valEntry.getKey(), valEntry.getValue().toString()));
                }
            }
        }
        builder.datasources(datasources);

        AuthService authService = builder.build();
        EasyABACPermissionChecker<Object, Object> permissionChecker = new EasyABACPermissionChecker<>(authService);
        return permissionChecker;
    }

}
