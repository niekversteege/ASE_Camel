/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.han.dare2date.service.subscribers.newpackage.external;

import nl.han.dare2date.service.jms.util.Topics;
import nl.han.dare2date.service.subscribers.Subscriber;

/**
 *
 * @author Niek
 */
public class MemberSubscriber {
    
    public static void main(String[] args) {
        Subscriber subscriber = new Subscriber();
        subscriber.subscribeToTopic(Topics.REGISTRATION_CONFIRMATION_TOPIC);
    }
}
