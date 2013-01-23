/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.han.dare2date.service.subscribers;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;

/**
 *
 * @author Niek
 */
public class Subscriber {

    private final Logger log = Logger.getLogger(getClass().getName());
    Context context = null;
    TopicConnectionFactory topicConnectionFactory = null;
    TopicConnection topicConnection = null;
    TopicSession topicSession = null;
    Topic topic = null;
    TopicSubscriber topicSubscriber = null;
    SubscriberMessageListener listener = null;

    public void subscribeToTopic(String topicString) {

        log.debug("Attmpting to subscribe to topic: " + topicString);

        initContext();
        initTopicConnectionFactory(topicString);
        initConnection();
    }

    private void initContext() {
        try {
            context = new InitialContext();
        } catch (NamingException ex) {
            log.error("JNDI lookup failed: " + ex.toString(), ex);
        }
    }

    private void initTopicConnectionFactory(String topicString) {
        try {
            topicConnectionFactory = (TopicConnectionFactory) context
                    .lookup("TopicConnectionFactory");
            topic = (Topic) context.lookup(topicString);
        } catch (NamingException ex) {
            log.error("TopicConnectionFactory lookup failed: " + ex.toString(), ex);
        }
    }

    private void initConnection() {
        try {
            topicConnection = topicConnectionFactory.createTopicConnection();
            topicSession = topicConnection.createTopicSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            topicSubscriber = topicSession.createSubscriber(topic);
            listener = new SubscriberMessageListener();
            topicSubscriber.setMessageListener(listener);
            topicConnection.start();
        } catch (JMSException ex) {
            log.error("TopicConnection failed: " + ex.toString(), ex);
        }
    }
}
