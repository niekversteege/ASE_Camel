package nl.han.dare2date.service.web;

import javax.jms.JMSException;
import javax.naming.NamingException;
import nl.han.dare2date.applyregistrationservice.Registration;
import nl.han.dare2date.applyregistrationservice.User;
import nl.han.dare2date.service.jms.util.SubjectGateway;
import nl.han.dare2date.service.jms.util.Topics;
import org.apache.log4j.Logger;

/**
 *
 * @author mdkr
 *
 * Is used as a JMS publisher
 */
public class ConfirmRegistrationService extends SubjectGateway implements Topics {

    public ConfirmRegistrationService() throws JMSException, NamingException {
        super(REGISTRATION_CONFIRMATION_TOPIC);
    }
    private final Logger log = Logger.getLogger(getClass().getName());
    private static final String notifyMessage = "Registration Succesful";

    public void confirm(Registration reg) throws JMSException {

        // andere dingen doen na confirmatie? geen idee
        String message = notifyMessage + " " + createUserString(reg.getUser());
        notifyObservers(message);
    }

    private String createUserString(User user) {
        return user.getLastname() + " " + user.getFirstname();
    }
}
