package custis.easyabac.benchmark;

import custis.easyabac.benchmark.model.Customer;
import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;
import custis.easyabac.benchmark.model.Subject;
import custis.easyabac.core.extend.subject.SubjectAttributesProvider;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.AttributeWithValue;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

public class AbstractAuthorizationBenchmark {

    Order getOrder() {
        return new Order("1", 100, "123", "456");
    }

    OrderAction getOrderAction() {
        return OrderAction.APPROVE;
    }

    Customer getCustomer() {
        return new Customer("456", "123");
    }

    static Subject getSubject() {
        return new Subject("1", "MANAGER", "123", 500);
    }

    static SubjectAttributesProvider getSubjectAttributesProvider(AbacAuthModel model) {
        return () -> {
            Subject subject = getSubject();
            List<AttributeWithValue> values = new ArrayList<>();
            values.add(new AttributeWithValue(model.getAttributes().get("subject.role"), singletonList(subject.getRole())));
            values.add(new AttributeWithValue(model.getAttributes().get("subject.branchId"), singletonList(subject.getBranchId())));
            values.add(new AttributeWithValue(model.getAttributes().get("subject.maxOrderAmount"), singletonList("" + subject.getMaxOrderAmount())));
            return values;
        };
    }
}
