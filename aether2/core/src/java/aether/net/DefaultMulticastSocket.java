package aether.net;

import aether.event.Event;
import aether.event.EventException;
import aether.event.EventHandler;
import aether.event.Notice;
import org.apache.log4j.Logger;
import org.elvin.je4.Consumer;
import org.elvin.je4.Notification;
import org.elvin.je4.NotificationListener;
import org.elvin.je4.Subscription;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides the basic implementation of the MulticastSocket interface.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class DefaultMulticastSocket implements MulticastSocket
{
    /**
     * List of listeners on this monitor.
     */
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Map to store the subscriptions to topic. String topics are mapped to
     * Subscription objects.
     */
    protected Map topicMap = new HashMap();

    /**
     * Open status of the monitor.
     */
    protected boolean open;

    private NotificationListener queueListener;
    private Connection connection;
    private Consumer consumer;

    private static final Logger log = Logger.getLogger(DefaultMulticastSocket.class);

    /**
     * Construct a new DefaultMulticastSocket.
     *
     * @param conn open connection to the event network
     */
    public DefaultMulticastSocket(Connection conn)
    {
        if (conn == null)
        {
            String msg = "no parameter can be null";
            throw new IllegalArgumentException(msg);
        }

        if (!conn.isOpen())
        {
            String msg = "conn must be an open connection";
            throw new IllegalArgumentException(msg);
        }

        this.connection = conn;
    }

    public synchronized void subscribe(String topic) throws IOException
    {
        if (topic == null)
        {
            String msg = "no parameter can be null";
            throw new IllegalArgumentException(msg);
        }

        if (!isOpen())
        {
            String msg = "monitor isn't open";
            throw new IllegalStateException(msg);
        }

        // have we already subscribed to this topic? if so, ignore it!
        if (!topicMap.containsKey(topic))
        {
            // create the Subscription from the uri and add our listener to it
            Subscription subscription = Notice.createTopicSubscription(topic);
            subscription.addNotificationListener(queueListener);

            // subscribe to it
            consumer.addSubscription(subscription);

            // put it in the map
            topicMap.put(topic, subscription);
        }
    }

    public synchronized void unsubscribe(String topic) throws IOException
    {
        if (topic == null)
        {
            String msg = "no parameter can be null";
            throw new IllegalArgumentException(msg);
        }

        if (!isOpen())
        {
            String msg = "monitor isn't open";
            throw new IllegalArgumentException(msg);
        }

        // if subscribed to this guid, remove the subscription
        if (topicMap.containsKey(topic))
        {
            Subscription sub = (Subscription) topicMap.remove(topic);
            consumer.removeSubscription(sub);
        }
    }

    public void broadcast(Notice notice, String topic) throws IOException
    {
        if ((notice == null) || (topic == null))
        {
            String msg = "no parameter can be null";
            throw new IllegalArgumentException(msg);
        }

        if (!isOpen())
        {
            String msg = "multicaster isn't open!";
            throw new IllegalStateException(msg);
        }

        // setup the meta info
        notice.setTopicId(topic);

        connection.publish(notice);
    }

    public void addEventHandler(EventHandler handler)
    {
        if (handler != null)
        {
            listenerList.add(EventHandler.class, handler);
        }
    }

    public void removeEventHandler(EventHandler handler)
    {
        if (handler != null)
        {
            listenerList.remove(EventHandler.class, handler);
        }
    }

    public synchronized void open() throws IOException
    {
        if (isOpen())
        {
            String msg = "monitor already open";
            throw new IllegalStateException(msg);
        }

        // create the consumer
        this.consumer = new Consumer(connection.elvinConnection());
        this.queueListener = new QueueNotificationListener();
        open = true;
    }

    public synchronized void close() throws IOException
    {
        topicMap.clear();
        consumer.close();
        consumer = null;
        queueListener = null;
        open = false;
    }

    public boolean isOpen()
    {
        return open;
    }

    /**
     * Fire an incoming Notice to all registered listeners.
     *
     * @param notice Notice to be delivered to all listeners
     */
    protected void fireNoticeReceived(Notice notice)
    {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == EventHandler.class)
            {
                ((EventHandler) listeners[i + 1]).handle(notice);
            }
        }
    }

    /**
     * NotificationListener that enqueues all incoming NOTICE objects.
     *
     * @author Buko O. (aso22@columbia.edu)
     * @version 0.1
     */
    private class QueueNotificationListener implements NotificationListener
    {
        public void notificationAction(Notification notification)
        {
            if (Event.isNotice(notification))
            {
                try
                {
                    Notice n = new Notice();
                    n.parse(notification);
                    fireNoticeReceived(n);
                }
                catch (EventException me)
                {
                    String msg = "receieved bad notification " + notification;
                    log.warn(msg, me);
                }
            }
        }

    }

}

