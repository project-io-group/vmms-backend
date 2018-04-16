package pl.agh.edu.iisg.io.vmms.vmmsbackend.model;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.UserRepository;

import static java.util.Collections.emptyList;

public class ApplicationUserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    public ApplicationUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser applicationUser = userRepository.findFirstByUserName(username);
        if (applicationUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(applicationUser.getUserName(), applicationUser.getPassword(), emptyList());

    }
}
