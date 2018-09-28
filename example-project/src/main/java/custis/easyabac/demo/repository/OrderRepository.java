package custis.easyabac.demo.repository;

import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.model.OrderId;
import org.springframework.data.repository.CrudRepository;


public interface OrderRepository extends CrudRepository<Order, OrderId> {
}
