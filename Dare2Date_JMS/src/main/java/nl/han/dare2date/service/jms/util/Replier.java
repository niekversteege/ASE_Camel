package nl.han.dare2date.service.jms.util;

import java.io.Serializable;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.NamingException;
import org.apache.log4j.Logger;

/**
 *
 * @author mdkr
 *
 * Replier does not need the replyQueue, it is included in the message.
 */
public abstract class Replier implements MessageListener {

    private final Logger log = Logger.getLogger(getClass().getName());
    
    private Session session;
    private MessageProducer invalidProducer;

    protected Replier(Connection connection, String requestQueueName, String invalidQueueName)
            throws JMSException, NamingException {
        initialize(connection, requestQueueName, invalidQueueName);
    }

    protected void initialize(Connection connection, String requestQueueName, String invalidQueueName)
            throws NamingException, JMSException {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination requestQueue = JMSUtil.getDestinationForQueue(requestQueueName);
        Destination invalidQueue = JMSUtil.getDestinationForQueue(invalidQueueName);
        MessageConsumer requestConsumer = session.createConsumer(requestQueue);
        MessageListener listener = this;
        requestConsumer.setMessageListener(listener);
        invalidProducer = session.createProducer(invalidQueue);
    }

    public void onMessage(Message message) {
        try {
            if ((message instanceof ObjectMessage) && (message.getJMSReplyTo() != null)) {
                ObjectMessage requestMessage = (ObjectMessage) message;

                log.info("Received request");
                log.info("\tTime:       " + System.currentTimeMillis() + " ms");
                log.info("\tMessage ID: " + requestMessage.getJMSMessageID());
                log.info("\tCorrel. ID: " + requestMessage.getJMSCorrelationID());
                log.info("\tReply to:   " + requestMessage.getJMSReplyTo());
                log.info("\tContents:   " + requestMessage.getObject());

                Serializable contents = requestMessage.getObject();

                handleMessage(contents);

                Destination replyDestination = message.getJMSReplyTo();
                MessageProducer replyProducer = session.createProducer(replyDestination);

                ObjectMessage replyMessage = getReplyMessage();
                replyMessage.setJMSCorrelationID(requestMessage.getJMSMessageID());
                replyProducer.send(replyMessage);

                log.info("Sent reply");
                log.info("\tTime:       " + System.currentTimeMillis() + " ms");
                log.info("\tMessage ID: " + replyMessage.getJMSMessageID());
                log.info("\tCorrel. ID: " + replyMessage.getJMSCorrelationID());
                log.info("\tReply to:   " + replyMessage.getJMSReplyTo());
                log.info("\tContents:   " + replyMessage.getObject());
            } else {
                log.info("Invalid message detected");
                log.info("\tType:       " + message.getClass().getName());
                log.info("\tTime:       " + System.currentTimeMillis() + " ms");
                log.info("\tMessage ID: " + message.getJMSMessageID());
                log.info("\tCorrel. ID: " + message.getJMSCorrelationID());
                log.info("\tReply to:   " + message.getJMSReplyTo());

                message.setJMSCorrelationID(message.getJMSMessageID());
                invalidProducer.send(message);

                log.info("Sent to invalid message queue");
                log.info("\tType:       " + message.getClass().getName());
                log.info("\tTime:       " + System.currentTimeMillis() + " ms");
                log.info("\tMessage ID: " + message.getJMSMessageID());
                log.info("\tCorrel. ID: " + message.getJMSCorrelationID());
                log.info("\tReply to:   " + message.getJMSReplyTo());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public abstract ObjectMessage getReplyMessage();

    public abstract void handleMessage(Serializable contents);

    public Session getSession() {
        return session;
    }
}