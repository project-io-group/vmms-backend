package pl.agh.edu.iisg.io.vmms.vmmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.MailSubject;

public interface MailSubjectRepository extends JpaRepository<MailSubject, Long> {
    MailSubject findFirstBySubject(String subject);
}
