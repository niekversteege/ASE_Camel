package nl.han.dare2date.service.jms.util;

import java.io.Serializable;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.NamingException;
import org.apache.log4j.Logger;

public abstract class Requestor {

    private final Logger log = Logger.getLogger(getClass().getName());
    private Session session;
    private Destination replyQueue;
    private MessageProducer requestProducer;
    private MessageConsumer replyConsumer;
    private MessageProducer invalidProducer;
    private ObjectMessage replyMessage;

    protected Requestor(Connection connection, String requestQueueName,
            String replyQueueName, String invalidQueueName) throws JMSException, NamingException {
        super();
        initialize(connection, requestQueueName, replyQueueName, invalidQueueName);
    }

    protected void initialize(Connection connection, String requestQueueName,
            String replyQueueName, String invalidQueueName)
            throws NamingException, JMSException {

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination requestQueue = JMSUtil.getDestinationForQueue(requestQueueName);
        replyQueue = JMSUtil.getDestinationForQueue(replyQueueName);
        Destination invalidQueue = JMSUtil.getDestinationForQueue(invalidQueueName);

        requestProducer = session.createProducer(requestQueue);
        replyConsumer = session.createConsumer(replyQueue);
        invalidProducer = session.createProducer(invalidQueue);
    }

    public void send() throws JMSException {
        ObjectMessage requestMessage = getObjectMessage();
        requestMessage.setJMSReplyTo(replyQueue);
        requestProducer.send(requestMessage);
        log.info("Sent request");
        log.info("\tTime:       " + System.currentTimeMillis() + " ms");
        log.info("\tMessage ID: " + requestMessage.getJMSMessageID());
        log.info("\tCorrel. ID: " + requestMessage.getJMSCorrelationID());
        log.info("\tReply to:   " + requestMessage.getJMSReplyTo());
        log.info("\tContents:   " + requestMessage.getObject());
    }

    public abstract ObjectMessage getObjectMessage();

    public void receiveSync() throws JMSException {
        Message msg = replyConsumer.receive();
        if (msg instanceof ObjectMessage) {
            ObjectMessage replyMessage = (ObjectMessage) msg;
            log.info("Received reply ");
            log.info("\tTime:       " + System.currentTimeMillis() + " ms");
            log.info("\tMessage ID: " + replyMessage.getJMSMessageID());
            log.info("\tCorrel. ID: " + replyMessage.getJMSCorrelationID());
            log.info("\tReply to:   " + replyMessage.getJMSReplyTo());
            log.info("\tContents:   " + replyMessage.getObject());

            setReplyMessage(replyMessage);

        } else {
            log.info("Invalid message detected");
            log.info("\tType:       " + msg.getClass().getName());
            log.info("\tTime:       " + System.currentTimeMillis() + " ms");
            log.info("\tMessage ID: " + msg.getJMSMessageID());
            log.info("\tCorrel. ID: " + msg.getJMSCorrelationID());
            log.info("\tReply to:   " + msg.getJMSReplyTo());

            msg.setJMSCorrelationID(msg.getJMSMessageID());
            invalidProducer.send(msg);

            log.info("Sent to invalid message queue");
            log.info("\tType:       " + msg.getClass().getName());
            log.info("\tTime:       " + System.currentTimeMillis() + " ms");
            log.info("\tMessage ID: " + msg.getJMSMessageID());
            log.info("\tCorrel. ID: " + msg.getJMSCorrelationID());
            log.info("\tReply to:   " + msg.getJMSReplyTo());
        }
    }

    public Session getSession() {
        return session;
    }

    public abstract Serializable getResponse();

    public ObjectMessage getReplyMessage() {
        return replyMessage;
    }

    private void setReplyMessage(ObjectMessage replyMessage) {
        this.replyMessage = replyMessage;
    }
}