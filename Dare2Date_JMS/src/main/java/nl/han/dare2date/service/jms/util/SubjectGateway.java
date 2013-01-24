package nl.han.dare2date.service.jms.util;

import java.io.Serializable;
import javax.jms.*;
import javax.naming.NamingException;
import org.apache.log4j.Logger;

public class SubjectGateway {
    
    private final Logger log = Logger.getLogger(getClass().getName());
    private Connection connection;
    private Session session;
    private MessageProducer updateProducer;
    
    public SubjectGateway(String topicName) throws JMSException, NamingException {
        log.info("Initializing new SubjectGateway for topic: " + topicName);
        this.initialize(topicName);
    }
    
    protected final void initialize(String topicName) throws JMSException, NamingException {
        connection = JMSUtil.getConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination updateTopic = JMSUtil.getDestinationForQueue(topicName);
        updateProducer = session.createProducer(updateTopic);
    }
    
    public void notifyObservers(Serializable o) throws JMSException {
        ObjectMessage message = session.createObjectMessage(o);
        message.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
        updateProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
        updateProducer.send(message);
    }
}
