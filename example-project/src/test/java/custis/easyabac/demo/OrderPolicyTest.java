package custis.easyabac.demo;

import custis.easyabac.api.test.EasyAbacBaseTestClass;
import custis.easyabac.model.EasyAbacInitException;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.List;

import static custis.easyabac.api.test.helper.AutogeneratingTestDataHelper.loadGeneratedTestsFromPackage;
import static custis.easyabac.api.test.helper.ModelHelper.loadModelFromResource;

/**
 * Created by Mahdi Razavi on 1/13/19-9:40 AM
 */
public class OrderPolicyTest extends EasyAbacBaseTestClass {
    public OrderPolicyTest() throws EasyAbacInitException, IOException {
        super(loadModelFromResource("/policy.yaml"));
    }

    @Parameterized.Parameters(name = "{index}: resource({0}) and action({1}). Expecting permit = ({2})")
    public static List<Object[]> data() throws Exception {
        return loadGeneratedTestsFromPackage(OrderAuthTest.class, "order_auth_test");
    }
}
