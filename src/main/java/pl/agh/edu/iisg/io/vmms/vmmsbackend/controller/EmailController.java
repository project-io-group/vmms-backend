package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.sendgrid.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.InvalidEmailRequestException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.MailSendingFailureException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/email")
public class EmailController {
    private final Email from;
    private final SendGrid sg;

    EmailController(){
        from = new Email("complaint@vmms.ki.agh.edu.pl");
        String apiKey = System.getenv("SENDGRID_API_KEY");
        if (apiKey.equals("")) {
            throw new RuntimeException("Failed to access SendGrid API key! " +
                    "Please set SENDGRID_API_KEY environment variable");
        }
        sg = new SendGrid(apiKey);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void importFromFile(@RequestParam("recipent") String recipentKey, @RequestParam("subject") String subjectKey,
                               @RequestParam("content") String rawContent)
            throws InvalidEmailRequestException, MailSendingFailureException {

        String subject = Helper.subjects.get(subjectKey);
        if (subject == null){
            throw new InvalidEmailRequestException("No such subject defined: '" + recipentKey + "'");
        }

        Email to = Helper.recipents.get(recipentKey);
        if (to == null){
            throw new InvalidEmailRequestException("No such recipent defined: '" + recipentKey + "'");
        }

        Content content = new Content("text/plain", rawContent);
        Mail mail = new Mail(from, subject, to, content);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw new MailSendingFailureException(ex);
        }
    }

    private static class Helper{
        static Map<String, Email> recipents = new HashMap<>();
        static Map<String, String> subjects = new HashMap<>();

        static {
            recipents.put("test", new Email("peantab@gmail.com"));

            subjects.put("test", "It's just a test message, you can ignore it.");
        }
    }
}