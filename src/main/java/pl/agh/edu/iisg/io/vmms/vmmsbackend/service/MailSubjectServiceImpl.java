package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.MailKeySubject;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.MailKeySubjectRepository;

import java.util.Optional;

@Service
public class MailSubjectServiceImpl implements MailSubjectService {

    private final MailKeySubjectRepository mailKeySubjectRepository;

    @Autowired
    public MailSubjectServiceImpl(MailKeySubjectRepository mailKeySubjectRepository) {
        this.mailKeySubjectRepository = mailKeySubjectRepository;
    }

    @Override
    public Optional<String> find(String subjectKey) {
        return mailKeySubjectRepository.findFirstByKey(subjectKey).map(MailKeySubject::getSubject);
    }

    @Override
    public Optional<String> find(Long id) {
        return mailKeySubjectRepository.findById(id).map(MailKeySubject::getSubject);
    }

    @Override
    public void drop() {
        mailKeySubjectRepository.deleteAll();
    }

    @Override
    public void save(String key, String subject) {
        MailKeySubject mks = new MailKeySubject();
        mks.setKey(key);
        mks.setSubject(subject);
        mailKeySubjectRepository.save(mks);
    }

}
