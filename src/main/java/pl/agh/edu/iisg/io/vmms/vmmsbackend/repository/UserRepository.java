package pl.agh.edu.iisg.io.vmms.vmmsbackend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.ApplicationUser;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findFirstByUserName(String userName);

    List<ApplicationUser> findAllByUserNameContaining(String userName);
}
