package custis.easyabac.api.test;

import custis.easyabac.api.EntityPermissionChecker;
import custis.easyabac.api.NotPermittedException;
import custis.easyabac.api.impl.EasyABACPermissionChecker;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.pdp.AuthService;
import custis.easyabac.core.pdp.PdpHandlerFactory;
import custis.easyabac.core.pdp.balana.BalanaPdpHandlerFactory;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.easy.EasyAbacModelCreator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.InputStream;

@RunWith(Parameterized.class)
public abstract class EasyAbacBaseTestClass {

    @Parameterized.Parameter
    public Object resource;

    @Parameterized.Parameter(value = 1)
    public Object action;

    @Parameterized.Parameter(value = 2)
    public boolean expectedPermit;

    @Parameterized.Parameter(value = 3)
    public TestDescription testDescription;

    protected final AbacAuthModel model;
    protected final PdpHandlerFactory pdpHandlerFactory = BalanaPdpHandlerFactory.PROXY_INSTANCE;

    public EasyAbacBaseTestClass(InputStream modelSource) throws EasyAbacInitException {
        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        this.model = creator.createModel(modelSource);
    }

    public EasyAbacBaseTestClass(AbacAuthModel model) {
        this.model = model;
    }

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

    private EntityPermissionChecker getPermissionChecker() throws EasyAbacInitException {
        EasyAbacBuilder builder = new EasyAbacBuilder(model, pdpHandlerFactory);
        AuthService authService = builder.build();
        EasyABACPermissionChecker<Object, Object> permissionChecker = new EasyABACPermissionChecker<>(authService);
        return permissionChecker;
    }


}
