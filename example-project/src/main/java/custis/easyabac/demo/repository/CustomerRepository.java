package custis.easyabac.demo.repository;

import custis.easyabac.demo.model.Customer;
import custis.easyabac.demo.model.CustomerId;
import org.springframework.data.repository.CrudRepository;


public interface CustomerRepository extends CrudRepository<Customer, CustomerId> {
}
