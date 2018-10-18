package custis.easyabac.benchmark;

import custis.easyabac.benchmark.model.Customer;
import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;
import custis.easyabac.benchmark.model.Subject;
import custis.easyabac.core.EasyAbacDatasourceException;
import custis.easyabac.core.datasource.Datasource;
import custis.easyabac.core.extend.subject.SubjectAttributesProvider;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;

class AbstractAuthorizationBenchmark {

    Order getOrder() {
        return new Order("1", 100, "123", "456");
    }

    OrderAction getOrderApproveAction() {
        return OrderAction.APPROVE;
    }

    OrderAction getOrderRejectAction() {
        return OrderAction.REJECT;
    }

    Customer getCustomer() {
        return new Customer("456", "123");
    }

    Datasource getCustomerBranchIdDatasource() {
        return new CustomerBranchIdDatasource(getCustomer().getBranchId());
    }

    static Subject getManagerSubject() {
        return new Subject("1", "MANAGER", "123", 500);
    }
    static Subject getOperatorSubject() {
        return new Subject("2", "OPERATOR", "123", 100);
    }

    static SubjectAttributesProvider getSubjectAttributesProvider(Subject subject, AbacAuthModel model) {
        return () -> {
            List<AttributeWithValue> values = new ArrayList<>();
            values.add(new AttributeWithValue(model.getAttributes().get("subject.role"), singletonList(subject.getRole())));
            values.add(new AttributeWithValue(model.getAttributes().get("subject.branchId"), singletonList(subject.getBranchId())));
            values.add(new AttributeWithValue(model.getAttributes().get("subject.maxOrderAmount"), singletonList("" + subject.getMaxOrderAmount())));
            return values;
        };
    }

    private static class CustomerBranchIdDatasource extends Datasource {

        private String customerId;

        CustomerBranchIdDatasource(String id) {
            super(emptySet(), "customer.branchId");
            this.customerId = id;
        }

        @Override
        public List<String> find() throws EasyAbacDatasourceException {
            return singletonList(customerId);
        }
    }
}
