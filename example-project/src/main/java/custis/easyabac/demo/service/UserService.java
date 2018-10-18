package custis.easyabac.demo.service;

import custis.easyabac.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

   List<User> getAllUsers();

   Optional<User> findById(String id);
}
