package psl.memento.server.container.event.elvin;

import psl.memento.server.container.event.*;

import org.elvin.je4.*;

import java.util.*;

/**
 * Implementation of the EventService using Elvin.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class ElvinEventService extends EventService 
	implements NotificationListener
{
	private Map consumerMap = Collections.synchronizedMap(new HashMap());
	private Map producerMap = Collections.synchronizedMap(new HashMap());
	private Map connectionMap = Collections.synchronizedMap(new HashMap());
	
	/**
	 * Open a physical connection to an Elvin server.
	 * 
	 * @param host host of the elvin server
	 * @param port port of the elvin server
	 * @throws EventException
	 *         if the connection can't be opened
	 **/
	protected void openConnection(String host, int port)
		throws EventException
	{
		// make the connection
		try
		{
			ElvinURL url = 
				new ElvinURL("elvin:4.0/tcp,none,xdr/" + host + ":" + port);
			Connection conn = new Connection(url);
			
			Object connKey = makeConnectionKey(host, port);
			
			// put it in the map
			connectionMap.put(connKey, conn);
		}
		catch (Exception e)
		{
			String msg = "connection failed";
			throw new EventException(msg, e);
		}
	}
	
	/**
	 * Close a physical connection to an Elvin server.
	 * 
	 * @param host host of the elvin server
	 * @param port port of the elvin server
	 **/
	protected void closeConnection(String host, int port)
	{
		Object connKey = makeConnectionKey(host, port);
		
		// get the connection and close it
		Connection conn = (Connection) connectionMap.get(connKey);
		
		if (conn != null)
		{
			conn.close();
			
			// remove any producer and/or consumer on the connection
			producerMap.remove(connKey);
			consumerMap.remove(connKey);
			
			// remove the connection
			connectionMap.remove(connKey);
		}
	}
	
	/**
	 * Publish an event to a topic hosted on an Elvin server.
	 * 
	 * @param topic topic to publish the event on
	 * @param event event to publish
	 **/
	protected void publish(Topic topic, Event event) throws EventException
	{
		Object connKey = makeConnectionKey(topic.getHost(), topic.getPort());
		Connection conn = (Connection) connectionMap.get(connKey);
		
		// make sure the connection exists
		if (conn == null)
		{
			String msg = "no connection to " + topic.getHost() + ":" + 
				topic.getPort();
			throw new IllegalStateException(msg);
		}
		
		try
		{
			// see if a producer exists
			Producer producer;
			if (producerMap.containsKey(connKey))
			{
				producer = (Producer) producerMap.get(connKey);
			}
			else
			{
				producer = new Producer(conn);
				producerMap.put(connKey, producer);
			}
			
			// add the special topic key to the event
			event.put("event.elvin.topic", topic.getName());
			// send the event
			producer.notify( ((ElvinEvent) event).getUnderlyingNotification() );
		}
		catch (Exception e)
		{
			String msg = "event publish failed";
			throw new EventException(msg, e);
		}
	}
	
	/**
	 * Add a subscription to a topic hosted on an Elvin server.
	 * 
	 * @param topic topic to subscribe to
	 * @throws EventException
	 *         if the topic can't be subscribed to
	 **/
	public void addSubscription(Topic topic) throws EventException
	{
		Object connKey = makeConnectionKey(topic.getHost(), topic.getPort());
		Connection conn = (Connection) connectionMap.get(connKey);
		
		// make sure the connection exists
		if (conn == null)
		{
			String msg = "no connection to " + topic.getHost() + ":" + 
				topic.getPort();
			throw new IllegalStateException(msg);
		}
		
		// try to make the subscription
		try
		{
			Subscription sub = makeSubscription(topic);
			
			Consumer consumer;
			if (consumerMap.containsKey(connKey))
			{
				consumer = (Consumer) consumerMap.get(connKey);
			}
			else
			{
				consumer = new Consumer(conn);
				consumerMap.put(connKey, consumer);
			}
			
			consumer.addSubscription(sub);
		}
		catch (Exception e)
		{
			String msg = "couldn't make subscription";
			throw new EventException(msg, e);
		}
	}
		
	/**
	 * Remove a subscription from an Elvin server.
	 * 
	 * @param topic Topic to unsubscribe from
	 * @throws EventException
	 *         if the unsubscription fails
	 **/
	protected void removeSubscription(Topic topic) throws EventException
	{
		Object connKey = makeConnectionKey(topic.getHost(), topic.getPort());
		Connection conn = (Connection) connectionMap.get(connKey);
		
		// make sure the connection exists
		if (conn == null)
		{
			String msg = "no connection to " + topic.getHost() + ":" + 
				topic.getPort();
			throw new IllegalStateException(msg);
		}
		
		Consumer consumer = (Consumer) consumerMap.get(connKey);
		if (consumer == null)
		{
			String msg = "no subscription to " + topic;
			throw new IllegalStateException(msg);
		}
		
		try
		{
			Subscription sub = makeSubscription(topic);
			consumer.removeSubscription(sub);
		}
		catch (Exception e)
		{
			String msg = "unsubscription failed";
			throw new IllegalArgumentException(msg);
		}
	}
	
	/**
	 * Create a new Event understood by the Elvin system.
	 * 
	 * @return new ElvinEvent
	 **/
	public Event createEmptyEvent()
	{
		return new ElvinEvent();
	}
	
	/**
	 * Given a Topic, construct an Elvin subscription.
	 * 
	 * @param topic topic to turn into a subscription
	 **/	
	private Subscription makeSubscription(Topic topic)
	{
		String topicFilter = "event.elvin.topic == '" + topic.getName() + "'";
		return new Subscription(topicFilter, this);
	} 
	
	/**
	 * Turn a (host,port) into a single string for use with maps.
	 * 
	 * @param host host of an elvin server
	 * @param port port of an elvin server
	 * @return a unique object identifying the elvin server
	 **/
	private Object makeConnectionKey(String host, int port)
	{
		return host + ":" + port;
	}

	/**
	 * Method called by the Elvin consumer threads whenever an event is
	 * received.
	 * 
	 * @param notif incoming notification
	 **/
	public void notificationAction(Notification notif)
	{
		super.dispatch(new ElvinEvent(notif));
	}	
}
