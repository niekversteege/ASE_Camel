package nl.han.dare2date.service.web;

import java.util.logging.Level;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.naming.NamingException;
import nl.han.dare2date.applyregistrationservice.ApplyRegistrationRequest;
import nl.han.dare2date.applyregistrationservice.ApplyRegistrationResponse;
import nl.han.dare2date.applyregistrationservice.Creditcard;
import nl.han.dare2date.applyregistrationservice.Registration;
import nl.han.dare2date.service.jms.ValidateCreditcardRequestor;
import nl.han.dare2date.service.jms.util.JMSUtil;
import nl.han.dare2date.service.jms.util.Queues;
import org.apache.log4j.Logger;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

@Endpoint
public class ApplyRegistrationServiceEndpoint {

    private Logger log = Logger.getLogger(getClass().getName());
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    public ApplyRegistrationServiceEndpoint(Marshaller marshaller,
            Unmarshaller unmarshaller) {
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
    }

    @PayloadRoot(localPart = "ApplyRegistrationRequest", namespace = "http://www.han.nl/schemas/messages")
    public ApplyRegistrationResponse applyRegistration(ApplyRegistrationRequest req) {
        boolean success = false;
        System.out.println("Yz: REQUEST");
        ApplyRegistrationResponse response = new ApplyRegistrationResponse();
        
        Creditcard cc = req.getRegistration().getUser().getCard();

        //check if valid
        try {
            log.debug("Probeer request::");
            Connection c = JMSUtil.getConnection();
             ValidateCreditcardRequestor vcr = new ValidateCreditcardRequestor(
                    c, Queues.REQUEST_QUEUE,
                    Queues.REPLY_QUEUE, Queues.INVALID_QUEUE, cc);
             vcr.send();
             vcr.receiveSync();
             success = Boolean.parseBoolean( vcr.getResponse().toString() );
             c.close();

        } catch (JMSException ex) {
            java.util.logging.Logger.getLogger(ApplyRegistrationServiceEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            java.util.logging.Logger.getLogger(ApplyRegistrationServiceEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (success) {
          notifyEveryone(req.getRegistration());  
        }        
        
        response.setRegistration(req.getRegistration());
        response.getRegistration().setSuccesFul(success);
        return response;
    }

    private void notifyEveryone(Registration registration) {
        try {
            ConfirmRegistrationService confirm = new ConfirmRegistrationService();
            confirm.confirm(registration);

        } catch (JMSException | NamingException ex) {
            log.error(ex.toString(), ex);
        }
    }
}
