package psl.memento.server.container.event;

/**
 * Represents the main interface to the event system.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class EventService
{
	private VirtualConnectionManager vcm = new VirtualConnectionManager();
	private EventHandlerManager ehm = new EventHandlerManager();
	private EventQueue eventQueue = new EventQueue();
	
	/**
	 * Open a connection to an event hub. Before subscribing to a topic or 
	 * publishing an event to a topic you must first open a connection to the
	 * event hub.
	 * 
	 * @param host host of the event hub
	 * @param port port of the event hub
	 * @throws EventException
	 *         if no connection can be made
	 **/
	public void openConnection(String host, int port) throws EventException
	{
		if (host == null)
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		// if not connected to this event hub then open a physical connection
		if (!vcm.isConnected(host, port))
		{
			openPhysicalConnection(host, port);
		}
		
		// increment the virtual connection count
		vcm.openConnection(host, port);
	}
	
	/**
	 * Close a connection to an event hub. After you have unsubscribed from a 
	 * topic or finished sending an event you must close the outgoing connection
	 * to the event hub.
	 * 
	 * @param host host of the event hub
	 * @param port port of the event hub
	 **/
	public void closeConnection(String host, int port)
	{
		if (host == null)
		{
			String msg = "host can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		// close the virtual connection
		vcm.closeConnection(host, port);
		
		// if there are no more virtual connections then close the physical
		// connection
		if (!vcm.isConnected(host, port))
		{
			closePhysicalConnection(host, port);
		}
	}
	
	/**
	 * Open a physical connection to an event server. Subclasses must override
	 * this method to implement the appropriate connection logic necessary to
	 * publish and subscribe to events.
	 * 
	 * @param host host or IP of the event hub to connect to
	 * @param port port of the event hub
	 * @throws EventException
	 * 		   if the connection can't be made
	 **/
	protected abstract void openPhysicalConnection(String host, int port)
		throws EventException;
		
	/**
	 * Close a physical connection to the event server. Subclasses must 
	 * override to implement the appropriate disconnection logic and resource
	 * cleanup.
	 * 
	 * @param host host or IP of the event hub to disconnect from
	 * @param port port of the event hub
	 **/
	protected abstract void closePhysicalConnection(String host, int port);
	
	/**
	 * Publish an event to a given topic.
	 * 
	 * @param topic Topic to publish the event to
	 * @param event event to publish to the given topic
	 * @param source entity sending the event
	 * @throws IllegalStateException
	 *         if no connection exists to the hub hosting the topic
	 * @throws EventException
	 *         if the event can't be sent
	 **/
	public void publish(TopicUrl topic, Event event, NetworkEntity source) 
		throws EventException
	{
		if ((topic == null) || (event == null) || (source == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		if (!vcm.isConnected(topic.getHost(), topic.getPort()))
		{
			String msg = "no connection exists to " + topic.getHost() + ":" 
				+ topic.getPort();
			throw new IllegalStateException(msg);
		}
		
		// add the routing info
		event.setSource(source.getEntityId());
		event.setSourceHub(new EventHub(topic.getHost(), topic.getPort()));
		event.setTopic(topic);
		event.setTarget(null);
		
		// send the event
		send(topic, event);
	}
	
	/**
	 * Send an event to a specific entity within the network using the 
	 * point-to-point framework.
	 * 
	 * @param entity entity to send the event to
	 * @param event  event to send
	 * @param source the entity generating this event
	 **/
	public void send(NetworkEntity entity, Event event, NetworkEntity source)
		throws EventException
	{
		if ((entity == null) || (event == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		if (entity.getAddress() == null)
		{
			String msg = "cannot send to " + entity + " it's not addressable";
			throw new IllegalArgumentException(msg);
		}
		
		// make sure a connection exists
		TopicUrl topic = entity.getAddress();
		if (!vcm.isConnected(topic.getHost(), topic.getPort()))
		{
			String msg = "no connection exists to " + topic.getHost() + ":" +
				topic.getPort();
			throw new IllegalStateException(msg);
		}
		
		// add the routing info
		event.setSource(source.getEntityId());
		event.setSourceHub(new EventHub(topic.getHost(), topic.getPort()));
		event.setTopic(null);
		event.setTarget(entity.getEntityId());
		
		// send the event
		send(topic, event);
	}	
		
	
	/**
	 * Send an event over the underlying event service.
	 * 
	 * @param topic topic the event should be published to
	 * @param event event to actually send
	 * @throws EventException
	 *         if the event can't be sent
	 **/
	protected abstract void send(TopicUrl topic, Event event) 
		throws EventException;
	
	/**
	 * Subscribe a given topic hosted within the network.
	 * 
	 * @param topic   the topic to subscribe to
	 * @param handler the handler to receive events published to that topic
	 * @throws EventException
	 *         if the subscription couldn't happen
	 **/
	public void subscribe(TopicUrl topic, EventHandler handler)
		throws EventException
	{
		if ((topic == null) || (handler == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		// make sure a connection exists
		if (!vcm.isConnected(topic.getHost(), topic.getPort()))
		{
			String msg = "no connection exists to " + topic.getHost() + ":" +
				topic.getPort();
			throw new IllegalStateException(msg);
		}
		
		// 	if there are no handlers already subscribed to this topic this is
		// first one so a real subscription must be created
		if (ehm.numSubscribed(topic) < 1)
		{
			addSubscription(topic);
		}
		
		// if the subscription was made, add it to the ehm
		ehm.add(topic, handler);
	}
	
	/**
	 * Add a subscription using the underlying event system.
	 * 
	 * @param topic the topic to subscribe to
	 * @throws EventException
	 *         if the subscription couldn't happen
	 **/
	protected abstract void addSubscription(TopicUrl topic) 
		throws EventException;
	
	
	/**
	 * Unsubscribe from a topic. Once you've unsubscribed from a topic you must
	 * close the connection to the event hub to free up network resources.
	 * 
	 * @param topic   topic to unsubscribe from
	 * @param handler handler to do the unsubscribing
	 * @throws EventException
	 *         if the unsubscriotion doesn't happen
	 **/
	public void unsubscribe(TopicUrl topic, EventHandler handler)
		throws EventException
	{
		if ((topic == null) || (handler == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		// make sure a connection exists
		if (!vcm.isConnected(topic.getHost(), topic.getPort()))
		{
			String msg = "no connection exists to " + topic.getHost() + ":" +
				topic.getPort();
			throw new IllegalStateException(msg);
		}
		
		// if this is the last handler subscribed to the topic then remove the
		// real subscription
		if (ehm.numSubscribed(topic) == 1)
		{
			removeSubscription(topic);
		}
		
		// remove the real subscription
		ehm.remove(topic, handler);
	}

	/**
	 * Remove a subscription from the underlying event service.
	 * 
	 * @param topic topic to unsubscribe from
	 * @throws EventException
	 *         if the subscription couldn't be removed
	 **/	
	protected abstract void removeSubscription(TopicUrl topic)
		throws EventException;
	
	/**
	 * When an event is recieved subclasses should call this method so that
	 * the event can be dispatched to handlers.
	 * 
	 * @param event event which has been received from the underlying event
	 *              system
	 **/
	protected void eventReceived(Event event)
	{
		if (event != null)
		{
			eventQueue.enqueue(event);
		}
	}
	
	/**
	 * Create an empty event. 
	 * 
	 * @return empty Event object
	 **/
	public abstract Event createEmptyEvent();
	
}
