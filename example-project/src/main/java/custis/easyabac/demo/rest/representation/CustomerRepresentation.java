package custis.easyabac.demo.rest.representation;

import custis.easyabac.demo.model.Customer;

public class CustomerRepresentation {

    private String id;
    private String firstName;
    private String lastName;
    private String branchId;

    public CustomerRepresentation(String id, String firstName, String lastName, String branchId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.branchId = branchId;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBranchId() {
        return branchId;
    }

    public static CustomerRepresentation of(Customer customer) {
        return new CustomerRepresentation(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getBranchId());
    }
}
