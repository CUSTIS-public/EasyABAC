package generation;

import custis.easyabac.api.test.EasyAbacBaseTestClass;
import org.junit.runners.Parameterized.Parameters;

import java.util.List;

import static custis.easyabac.api.test.helper.AutogeneratingTestDataHelper.loadGeneratedTestsFromPackage;
import static custis.easyabac.api.test.helper.ModelHelper.loadModelFromResource;

public class OrderAuthTest extends EasyAbacBaseTestClass {

    public OrderAuthTest() throws Exception {
        super(loadModelFromResource("test.yaml"));
    }

    @Parameters(name = "{index}: resource({0}) and action({1}). Expecting permit = ({2})")
    public static List<Object[]> data() throws Exception {
        return loadGeneratedTestsFromPackage(OrderAuthTest.class, "order");
    }
}
