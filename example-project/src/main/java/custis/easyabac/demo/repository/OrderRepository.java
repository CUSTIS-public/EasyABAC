package custis.easyabac.demo.repository;

import custis.easyabac.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, String> {
}
