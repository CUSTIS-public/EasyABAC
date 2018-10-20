package custis.easyabac.demo;

import custis.easyabac.api.test.EasyAbacBaseTestClass;
import custis.easyabac.model.EasyAbacInitException;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static custis.easyabac.api.test.helper.ModelHelper.loadModelFromResource;
import static custis.easyabac.api.test.helper.TestDataHelper.loadTestFromResource;

public class OrderAuthTest extends EasyAbacBaseTestClass {
        public OrderAuthTest() throws EasyAbacInitException, IOException {
            super(loadModelFromResource("/policy.yaml"));
        }

        @Parameterized.Parameters(name = "{index}: resource({0}) and action({1}). Expecting permit = ({2})")
        public static List<Object[]> data() throws Exception {
            Object[] data = loadTestFromResource("/custis/easyabac/demo/order_auth_test_0.yaml");
            return Collections.singletonList(data);
        }
    }
