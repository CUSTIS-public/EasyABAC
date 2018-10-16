package custis.easyabac.benchmark;

import custis.easyabac.api.NotPermittedException;
import custis.easyabac.api.PermitAwarePermissionChecker;
import custis.easyabac.api.impl.EasyABACPermissionChecker;
import custis.easyabac.benchmark.model.Order;
import custis.easyabac.benchmark.model.OrderAction;
import custis.easyabac.benchmark.model.Subject;
import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.EasyAbacDatasourceException;
import custis.easyabac.core.extend.subject.SubjectAttributesProvider;
import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.init.Datasource;
import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.AttributeWithValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.model.abac.attribute.DataType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.List;

import static custis.easyabac.core.init.AuthModelTransformer.makeXacmlName;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;

@State(Scope.Thread)
public class PermissionCheckerBenchmark {

    private PermitAwarePermissionChecker<Order, OrderAction> managerOrderPermissionChecker;
    private PermitAwarePermissionChecker<Order, OrderAction> operatorOrderPermissionChecker;
    private Order order;

    @Setup
    public void setup() throws EasyAbacInitException {
        AbacAuthModel model = AbacAuthModelFactory.getInstance(ModelType.EASY_YAML,
                getClass().getResourceAsStream("/OrdersPolicy.yaml"));

        Subject managerSubject = new Subject("subject-id-1", "MANAGER", "branch-1234", 20000);
        Subject operatorSubject = new Subject("subject-id-2", "OPERATOR", "branch-1234", 5000);

        this.managerOrderPermissionChecker = new EasyABACPermissionChecker<>(
                new EasyAbac.Builder(model)
                        .subjectAttributesProvider(new SubjectInstanceAttributeProvider(managerSubject))
                        .datasources(singletonList(new CustomerBranchIdDatasource()))
                        .build());

        this.operatorOrderPermissionChecker = new EasyABACPermissionChecker<>(
                new EasyAbac.Builder(model)
                        .subjectAttributesProvider(new SubjectInstanceAttributeProvider(operatorSubject))
                        .build());

        this.order = new Order("order-1234", 1000, "branch-1234", "customer-4321");
    }

    @Benchmark
    public boolean ensureApproveSameBranchOrderPermitted() {
        boolean hasException;
        try {
            this.managerOrderPermissionChecker.ensurePermitted(order, OrderAction.APPROVE);
            hasException = false;
        } catch (NotPermittedException npe) {
            hasException = true;
        }
        return hasException;
    }

//    @Benchmark
    public boolean ensureRejectSameClientOrderPermitted() {
        boolean hasException;
        try {
            this.managerOrderPermissionChecker.ensurePermitted(order, OrderAction.REJECT);
            hasException = false;
        } catch (NotPermittedException npe) {
            hasException = true;
        }
        return hasException;
    }

//    @Benchmark
    public boolean ensureApproveByNonManagerDenied() {
        boolean hasException;
        try {
            this.operatorOrderPermissionChecker.ensurePermitted(order, OrderAction.APPROVE);
            hasException = false;
        } catch (NotPermittedException npe) {
            hasException = true;
        }
        return hasException;
    }

    private static class SubjectInstanceAttributeProvider implements SubjectAttributesProvider {

        private Subject subject;

        private SubjectInstanceAttributeProvider(Subject subject) {
            this.subject = subject;
        }

        @Override
        public List<AttributeWithValue> provide() {
            List<AttributeWithValue> authAttributes = new ArrayList<>();

            //TODO очень подозрительно, что пришлось здесь использовать makeXacmlName, иначе Balana находила 2 атрибута subject.branchId
            authAttributes.add(new AttributeWithValue(
                    new Attribute(makeXacmlName("subject.id"), Category.SUBJECT, DataType.STRING),
                    singletonList(subject.getId())));
            authAttributes.add(new AttributeWithValue(
                    new Attribute(makeXacmlName("subject.role"), Category.SUBJECT, DataType.STRING),
                    singletonList(subject.getRole())));
            authAttributes.add(new AttributeWithValue(
                    new Attribute(makeXacmlName("subject.branchId"), Category.SUBJECT, DataType.STRING),
                    singletonList(subject.getBranchId())));
            authAttributes.add(new AttributeWithValue(
                    new Attribute(makeXacmlName("subject.maxOrderAmount"), Category.SUBJECT, DataType.INT),
                    singletonList(Integer.toString(subject.getMaxOrderAmount()))));
            return authAttributes;
        }
    }

    private static class CustomerBranchIdDatasource extends Datasource {

        CustomerBranchIdDatasource() {
            super(emptySet(), "customer.branchId");
        }

        @Override
        public List<String> find() throws EasyAbacDatasourceException {
            return singletonList("branch-1234");
        }
    }

    public static void main(String[] args) throws EasyAbacInitException {
        PermissionCheckerBenchmark benchmark = new PermissionCheckerBenchmark();
        benchmark.setup();
        System.out.printf("Has exception: %b\n", benchmark.ensureApproveByNonManagerDenied());
    }
}