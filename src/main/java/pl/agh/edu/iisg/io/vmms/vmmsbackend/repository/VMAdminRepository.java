package pl.agh.edu.iisg.io.vmms.vmmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMAdmin;

public interface VMAdminRepository extends JpaRepository<VMAdmin, Long> {
    VMAdmin findFirstByName(String name);
}
