package nl.han.dare2date.service.web;

import nl.han.dare2date.applyregistrationservice.ApplyRegistrationRequest;
import nl.han.dare2date.applyregistrationservice.ApplyRegistrationResponse;
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
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @PayloadRoot(localPart = "ApplyRegistrationRequest", namespace = "http://www.han.nl/schemas/messages")
    public ApplyRegistrationResponse applyRegistration(ApplyRegistrationRequest req) {
        
        System.out.println("System print test");
        log.debug("log debug message");
        log.info("log info message");
        
        ApplyRegistrationResponse ret = new ApplyRegistrationResponse();
        boolean success = false;
        //check if valid

        //invalid -> ValidateCreditcardservice
        //valid -> ConfirmRegistrationService

        ret.setRegistration(req.getRegistration());
        ret.getRegistration().setSuccesFul(success);
        return ret;
    }
}
