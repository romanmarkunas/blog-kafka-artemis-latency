package com.romanmarkunas.blog.queues.latency.benchmark;

import com.romanmarkunas.blog.queues.latency.artemis.ArtemisMessageReceiver;
import com.romanmarkunas.blog.queues.latency.artemis.ArtemisMessageSender;
import com.romanmarkunas.blog.queues.latency.artemis.MessageSerde;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

public class ArtemisClients {

    private ArtemisClients() {}

    private static final String QUEUE = "queue/latency-test";
    private static final String BROKER = "tcp://127.0.0.1:61616";

    public static ArtemisMessageSender defaultSender() {
        try {
            SessionAndQueueAndConnection jmsObjects = lookupJmsObjects();

            Session session = jmsObjects.session;
            MessageProducer producer = session.createProducer(jmsObjects.queue);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            jmsObjects.connection.start();

            return new ArtemisMessageSender(
                    producer,
                    session,
                    new MessageSerde.MessageSerializer()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ArtemisMessageReceiver defaultReceiver() {
        try {
            SessionAndQueueAndConnection jmsObjects = lookupJmsObjects();

            Session session = jmsObjects.session;
            MessageConsumer consumer = session.createConsumer(jmsObjects.queue);
            jmsObjects.connection.start();

            return new ArtemisMessageReceiver(
                    consumer,
                    new MessageSerde.MessageDeserializer()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SessionAndQueueAndConnection lookupJmsObjects() throws NamingException, JMSException {
        Hashtable<String, Object> props = new Hashtable<>();
        props.put(
                Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
        props.put(
                "connectionFactory.ConnectionFactory",
                BROKER);

        InitialContext initialContext = new InitialContext(props);
        ConnectionFactory factory
                = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(QUEUE);
        return new SessionAndQueueAndConnection(session, queue, connection);
    }

    private static class SessionAndQueueAndConnection {

        private final Session session;
        private final Queue queue;
        private final Connection connection;

        public SessionAndQueueAndConnection(
                Session session,
                Queue queue,
                Connection connection) {
            this.session = session;
            this.queue = queue;
            this.connection = connection;
        }
    }
}
