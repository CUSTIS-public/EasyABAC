package custis.easyabac.demo.resource.dto;

import custis.easyabac.demo.model.Customer;

public class CustomerDto {

    private String id;
    private String firstName;
    private String lastName;
    private String branchId;

    public CustomerDto(String id, String firstName, String lastName, String branchId) {
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

    public static CustomerDto of(Customer customer) {
        return new CustomerDto(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getBranchId());
    }
}
