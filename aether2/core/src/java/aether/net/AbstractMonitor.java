package aether.net;

import aether.event.Event;
import aether.event.EventException;
import aether.event.EventHandler;
import aether.event.Notice;
import org.elvin.je4.Consumer;
import org.elvin.je4.Notification;
import org.elvin.je4.NotificationListener;
import org.elvin.je4.Subscription;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides the basic implementation of the Monitor interface.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class AbstractMonitor implements Monitor
{
    /**
	 * List of listeners on this monitor.
	 */
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * Map to store the subscriptions we've created. Each Subscription is
	 * mapped to the guid of the component being monitored.
	 */
	protected Map subscriptionMap = new HashMap();

	/**
	 * Map to store the subscriptions to topic. Each Subscription is mapped
	 * to the topic subscribed to.
	 */
	protected Map topicMap = new HashMap();

	/**
	 * Consumer that actually adds and removes Subscriptions. Subclasses are
	 * responsible for initializing this.
	 */
	protected Consumer consumer;

	/**
	 * Connection used to connect to the underlying network.
	 */
	protected Connection connection;

	/**
	 * Special NotificationListener that enqueues incoming Notices.
	 */
	protected NotificationListener queueListener =
			new QueueNotificationListener();


	public void subscribe(String topic) throws IOException
	{
		if (topic == null)
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		if (!isOpen())
		{
			String msg = "monitor is closed";
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

	public void unsubscribe(String topic) throws IOException
	{
		if (topic == null)
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		if (!isOpen())
		{
			String msg = "monitor is closed";
			throw new IllegalStateException(msg);
		}

        // if subscribed to this guid, remove the subscription
		if (topicMap.containsKey(topic))
		{
			Subscription sub = (Subscription) topicMap.remove(topic);
			consumer.removeSubscription(sub);
		}
	}

	public Connection getConnection()
	{
		return connection;
	}

	public synchronized void setConnection(Connection msgConn)
	{
        if (isOpen())
		{
            String msg = "monitor is open";
			throw new IllegalStateException(msg);
		}

		if (msgConn == null)
		{
			String msg = "msgConn can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.connection = msgConn;
	}

	public void addNoticeListener(EventHandler handler)
	{
		if (handler != null)
		{
			listenerList.add(EventHandler.class, handler);
		}
	}

	public void removeNoticeListener(EventHandler handler)
	{
		if (handler != null)
		{
			listenerList.remove(EventHandler.class, handler);
		}
	}

	public synchronized boolean isOpen()
	{
		return (consumer != null);
	}

	public synchronized void open() throws IOException
	{
		if (isOpen())
		{
			String msg = "monitor already open";
			throw new IllegalStateException(msg);
		}

		// open the event connection if it's not already open
		if (!connection.isOpen()) connection.open();

		// create the consumer
		this.consumer = new Consumer(connection.elvinConnection());
	}

	/**
	 * Unsubscribe from all the currently active subscriptions.
	 *
	 * @throws IOException
	 *         if an unsubscription op fails
	 */
	protected synchronized void clearSubscriptions() throws IOException
	{
		if (!isOpen())
		{
			String msg = "monitor already closed";
			throw new IllegalStateException(msg);
		}

		// unsubscribe from all subscription being monitored
		for (Iterator iter = subscriptionMap.entrySet().iterator();
			 iter.hasNext(); )
		{
			Subscription sub = (Subscription)
					((Map.Entry) iter.next()).getValue();

			consumer.removeSubscription(sub);

			iter.remove();
		}

		// unsubscribe from all topics subscribed to
        for (Iterator iter = topicMap.entrySet().iterator();
			 iter.hasNext(); )
		{
			Subscription sub = (Subscription)
					((Map.Entry) iter.next()).getValue();

			consumer.removeSubscription(sub);

			iter.remove();
		}
	}

	public synchronized void close() throws IOException
	{
		clearSubscriptions();
		connection.close();
		connection = null;
	}

	/**
	 * Fire an incoming NOTICE to all registered listeners.
	 *
	 * @param notice NOTICE to be delivered to all listeners
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
	 * @author Buko O. (buko@concedere.net)
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
					String msg = "receieved bad getNotification!";
					throw new Error(msg, me);
				}
			}
		}

	}

}

