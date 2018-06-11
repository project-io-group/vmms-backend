package pl.agh.edu.iisg.io.vmms.vmmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.MailKeySubject;

import java.util.Optional;

public interface MailKeySubjectRepository extends JpaRepository<MailKeySubject, Long> {
    Optional<MailKeySubject> findFirstByKey(String key);
}
