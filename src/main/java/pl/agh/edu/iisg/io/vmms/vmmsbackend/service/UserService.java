package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    List<User> findByNameContaining(String name);

    User find(String name);

    User find(Long id);

    User save(User user);

    User save(String name);

    void delete(User user);
}
