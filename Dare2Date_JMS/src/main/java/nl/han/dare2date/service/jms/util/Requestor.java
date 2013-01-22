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

public abstract class Requestor {

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
		System.out.println("Sent request");
		System.out.println("\tTime:       " + System.currentTimeMillis() + " ms");
		System.out.println("\tMessage ID: " + requestMessage.getJMSMessageID());
		System.out.println("\tCorrel. ID: " + requestMessage.getJMSCorrelationID());
		System.out.println("\tReply to:   " + requestMessage.getJMSReplyTo());
		System.out.println("\tContents:   " + requestMessage.getObject());
	}

	public abstract ObjectMessage getObjectMessage();

	public void receiveSync() throws JMSException {
		Message msg = replyConsumer.receive();
		if (msg instanceof ObjectMessage) {
			ObjectMessage replyMessage = (ObjectMessage) msg;
			System.out.println("Received reply ");
			System.out.println("\tTime:       " + System.currentTimeMillis() + " ms");
			System.out.println("\tMessage ID: " + replyMessage.getJMSMessageID());
			System.out.println("\tCorrel. ID: " + replyMessage.getJMSCorrelationID());
			System.out.println("\tReply to:   " + replyMessage.getJMSReplyTo());
			System.out.println("\tContents:   " + replyMessage.getObject());
			
			setReplyMessage(replyMessage);
			
		} else {
			System.out.println("Invalid message detected");
			System.out.println("\tType:       " + msg.getClass().getName());
			System.out.println("\tTime:       " + System.currentTimeMillis() + " ms");
			System.out.println("\tMessage ID: " + msg.getJMSMessageID());
			System.out.println("\tCorrel. ID: " + msg.getJMSCorrelationID());
			System.out.println("\tReply to:   " + msg.getJMSReplyTo());

			msg.setJMSCorrelationID(msg.getJMSMessageID());
			invalidProducer.send(msg);

			System.out.println("Sent to invalid message queue");
			System.out.println("\tType:       " + msg.getClass().getName());
			System.out.println("\tTime:       " + System.currentTimeMillis() + " ms");
			System.out.println("\tMessage ID: " + msg.getJMSMessageID());
			System.out.println("\tCorrel. ID: " + msg.getJMSCorrelationID());
			System.out.println("\tReply to:   " + msg.getJMSReplyTo());
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