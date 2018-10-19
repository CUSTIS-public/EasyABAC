package custis.easyabac.demo.repository;

import custis.easyabac.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, String> {
}
