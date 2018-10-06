package custis.easyabac.api.test;

import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.api.impl.EasyABACPermissionChecker;
import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.DummyAttributiveAuthorizationService;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RunWith(Parameterized.class)
public abstract class EasyAbacBaseTestClass {

    protected static AttributiveAuthorizationService authService;
    protected static PermitAwarePermissionChecker permissionChecker;

    protected static Yaml yaml = new Yaml();

    @Parameterized.Parameter
    public Object resource;

    @Parameterized.Parameter(value = 1)
    public Object action;

    @BeforeClass
    public static void initEasyABAC() throws FileNotFoundException, EasyAbacInitException {
        // FIXME доделайте уже блед EasyAbac.Builder builder = new EasyAbac.Builder(getModelSource(), ModelType.EASY_YAML);

        /*
        builder.subjectAttributesProvider(new SubjectAttributesProvider() {
            @Override
            public List<AttributeValue> provide() {
                return null;
            }
        });
        authService = builder.build();
        */

        authService = new DummyAttributiveAuthorizationService(AuthResponse.Decision.PERMIT);
        permissionChecker = new EasyABACPermissionChecker<>(authService);
    }

    private static InputStream getModelSource() throws FileNotFoundException {
        return new FileInputStream("E:\\Projects\\CustIS\\easyabac\\framework\\easyabac-plugins\\easyabac-maven-plugin\\src\\main\\resources\\test.yaml");
    }

}
