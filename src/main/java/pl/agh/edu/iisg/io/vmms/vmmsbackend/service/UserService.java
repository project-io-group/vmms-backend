package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.ApplicationUser;

import java.util.List;

public interface UserService {
    List<ApplicationUser> getUsers();

    List<ApplicationUser> findByNameContaining(String name);

    ApplicationUser find(String name);

    ApplicationUser find(Long id);

    ApplicationUser save(ApplicationUser applicationUser);

    ApplicationUser save(String name);

    void delete(ApplicationUser applicationUser);
}
