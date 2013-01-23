/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.han.dare2date.service.subscribers.external;

import javax.jms.JMSException;
import javax.naming.NamingException;
import nl.han.dare2date.service.jms.util.ObserverGateway;
import nl.han.dare2date.service.jms.util.Topics;
import org.apache.log4j.Logger;

/**
 *
 * @author Niek
 */
public class PublisherSubscriber {

    private final Logger log = Logger.getLogger(getClass().getName());

    public static void main(String[] args) {
        PublisherSubscriber pub = new PublisherSubscriber();
        pub.listen();
    }

    private void listen() {
        //Subscriber subscriber = new Subscriber("PublisherSubscriber");
        //subscriber.subscribeToTopic(Topics.REGISTRATION_CONFIRMATION_TOPIC);

        try {
            RegistrationConfirmationObserver observer = new RegistrationConfirmationObserver("Publisher Subscriber");
            ObserverGateway gate = new ObserverGateway(observer, Topics.REGISTRATION_CONFIRMATION_TOPIC);
            
        } catch (NamingException | JMSException ex) {
            log.error("" + ex.toString(), ex);
        }
    }
}
