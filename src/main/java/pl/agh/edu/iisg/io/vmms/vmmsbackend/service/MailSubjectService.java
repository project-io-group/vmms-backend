package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.MailSubject;

public interface MailSubjectService {
    MailSubject find(String subject);
    MailSubject find(Long id);
    void drop();
    MailSubject save(MailSubject mailSubject);
}
