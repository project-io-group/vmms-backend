package pl.agh.edu.iisg.io.vmms.vmmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;

import java.util.List;

@Repository
public interface VMPoolRepository extends JpaRepository<VMPool, Long> {

    VMPool findFirstByShortName(String shortName);

    List<VMPool> findAllByEnabled(Boolean enabled);
    List<VMPool> findAllByDescriptionContaining(String pattern);

}
