package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import com.sendgrid.Email;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.SendMailException;


public interface MailService {

    void sendToAllAdmins(String subject, String content) throws SendMailException;

    void send(String subject, String content, Email to) throws SendMailException;

}
