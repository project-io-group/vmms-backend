package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import java.util.Optional;

public interface MailSubjectService {
    Optional<String> find(String key);

    Optional<String> find(Long id);

    void drop();

    void save(String key, String subject);
}
