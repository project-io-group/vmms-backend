package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.MailSendingFailureException;

/**
 * Created by Paweł Taborowski on 31.05.18.
 */
public interface MailService {
    void sendMail(String subject, String content, String toEmail, String toName) throws MailSendingFailureException;

    void sendMail(String subject, String content, String toEmail)  throws MailSendingFailureException;
}
