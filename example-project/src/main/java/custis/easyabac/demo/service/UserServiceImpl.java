package custis.easyabac.demo.service;

import custis.easyabac.demo.model.User;
import custis.easyabac.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }
}
