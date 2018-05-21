package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.MailSubject;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.MailSubjectRepository;

@Service
public class MailSubjectServiceImpl implements MailSubjectService {

    private final MailSubjectRepository mailSubjectRepository;

    @Autowired
    public MailSubjectServiceImpl(MailSubjectRepository mailSubjectRepository) {
        this.mailSubjectRepository = mailSubjectRepository;
    }

    @Override
    public MailSubject find(String subject) {
        return mailSubjectRepository.findFirstBySubject(subject);
    }

    @Override
    public MailSubject find(Long id) {
        return mailSubjectRepository.getOne(id);
    }
}
