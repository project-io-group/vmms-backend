package pl.agh.edu.iisg.io.vmms.vmmsbackend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findFirstByUserName(String userName);

    List<User> findAllByUserNameContaining(String userName);
}
