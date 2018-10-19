package custis.easyabac.benchmark;

import custis.easyabac.benchmark.model.attrprovider.OrderActionExt;
import custis.easyabac.benchmark.model.attrprovider.OrderExt;

class AbstractAuthorizationBenchmarkExt extends AbstractAuthorizationBenchmark {

    OrderExt getOrderExt() {
        return new OrderExt("1", 100, "123", "456");
    }

    OrderActionExt getOrderApproveActionExt() {
        return OrderActionExt.APPROVE;
    }

    OrderActionExt getOrderRejectActionExt() {
        return OrderActionExt.REJECT;
    }

}
