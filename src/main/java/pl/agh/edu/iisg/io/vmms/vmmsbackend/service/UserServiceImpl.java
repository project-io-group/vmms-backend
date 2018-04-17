package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.ApplicationUser;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ApplicationUser> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<ApplicationUser> findByNameContaining(String name) {
        return userRepository.findAllByUserNameContaining(name);
    }

    @Override
    public ApplicationUser find(String name) {
        return userRepository.findFirstByUserName(name);
    }

    @Override
    public ApplicationUser find(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public ApplicationUser save(ApplicationUser applicationUser) {
        return userRepository.save(applicationUser);
    }

    @Override
    public ApplicationUser save(String name) {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setUserName(name);
        return userRepository.save(applicationUser);
    }

    @Override
    public void delete(ApplicationUser applicationUser) {
        userRepository.delete(applicationUser);
    }
}
