package com.romanmarkunas.blog.queues.latency.benchmark;

import com.romanmarkunas.blog.queues.latency.artemis.ArtemisMessageReceiver;
import com.romanmarkunas.blog.queues.latency.artemis.ArtemisMessageSender;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

class ArtemisClientsFactory {

    private static final String QUEUE = "queue/latency-test";

    private final String broker;


    ArtemisClientsFactory(String broker) {
        this.broker = broker;
    }


    ArtemisMessageSender defaultSender() {
        try {
            SessionAndQueueAndConnection jmsObjects = lookupJmsObjects();

            Session session = jmsObjects.session;
            MessageProducer producer = session.createProducer(jmsObjects.queue);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            jmsObjects.connection.start();

            return new ArtemisMessageSender(producer, session);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ArtemisMessageReceiver defaultReceiver() {
        try {
            SessionAndQueueAndConnection jmsObjects = lookupJmsObjects();

            Session session = jmsObjects.session;
            MessageConsumer consumer = session.createConsumer(jmsObjects.queue);
            jmsObjects.connection.start();

            return new ArtemisMessageReceiver(consumer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private SessionAndQueueAndConnection lookupJmsObjects() throws NamingException, JMSException {
        Hashtable<String, Object> props = new Hashtable<>();
        props.put(
                Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
        props.put(
                "connectionFactory.ConnectionFactory",
                this.broker);

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
