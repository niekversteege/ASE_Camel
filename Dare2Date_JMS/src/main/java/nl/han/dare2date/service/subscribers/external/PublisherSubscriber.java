/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.han.dare2date.service.subscribers.external;

import nl.han.dare2date.service.jms.util.Topics;
import nl.han.dare2date.service.subscribers.Subscriber;

/**
 *
 * @author Niek
 */
public class PublisherSubscriber {
    
    public static void main(String[] args) {
        Subscriber subscriber = new Subscriber("PublisherSubscriber");
        subscriber.subscribeToTopic(Topics.REGISTRATION_CONFIRMATION_TOPIC);
    }
}
