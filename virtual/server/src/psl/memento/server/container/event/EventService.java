package psl.memento.server.container.event;

import psl.memento.server.container.component.Component;

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
	private EventDispatcher dispatcher;
	
	/**
	 * Default ctor.
	 **/
	protected EventService()
	{
		dispatcher = new EventDispatcher(ehm);
	}
	
	/**
	 * Retrieve a connection to a topic hosted within the network.
	 * 
	 * @param topic  topic hosted within the network
	 * @param source component requesting this connection
	 * @throws EventException
	 *         if the connection couldn't be established to the topic
	 **/
	public TopicConnection openConnection(Topic topic, Component source) 
		throws EventException
	{
		return new TopicConnection(topic, this, source);
	}
	
	/**
	 * Retrieve a connection to a topic hosted within the network without
	 * providing a Component source. The returned TopicConnection cannot be
	 * used to send events, it can only be used to subscribe to topics and
	 * receieve events. This is useful if a non-Component must subscribe to a
	 * specific set of events.
	 * 
	 * @param topic topic hosted within the network
	 * @throws EventException
	 *         if the connection coulnd't be established
	 **/
	public TopicConnection openConnection(Topic topic) throws EventException
	{
		return new TopicConnection(topic, this);
	}
	
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
	void openVirtualConnection(String host, int port) throws EventException
	{
		if (host == null)
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		// if not connected to this event hub then open a real connection
		if (!vcm.isConnected(host, port))
		{
			openConnection(host, port);
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
	void closeVirtualConnection(String host, int port)
	{
		if (host == null)
		{
			String msg = "host can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		// close the virtual connection
		vcm.closeConnection(host, port);
		
		// if there are no more virtual connections then close the real
		// connection
		if (!vcm.isConnected(host, port))
		{
			closeConnection(host, port);
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
	protected abstract void openConnection(String host, int port)
		throws EventException;
		
	/**
	 * Close a physical connection to the event server. Subclasses must 
	 * override to implement the appropriate disconnection logic and resource
	 * cleanup.
	 * 
	 * @param host host or IP of the event hub to disconnect from
	 * @param port port of the event hub
	 **/
	protected abstract void closeConnection(String host, int port);
	
	/**
	 * Publish an event to a given topic.
	 * 
	 * @param topic Topic to publish the event to
	 * @param event event to publish to the given topic
	 * @param source component publishing the event
	 * @throws IllegalStateException
	 *         if no connection exists to the hub hosting the topic
	 * @throws EventException
	 *         if the event can't be sent
	 **/
	void publish(Topic topic, Event event, Component source) 
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
		event.setSource(source.getAddress());
		event.setTopic(topic);
		
		// send the event
		publish(topic, event);
	}
		
	
	/**
	 * Publish an event over the underlying event service.
	 * 
	 * @param topic topic the event should be published to
	 * @param event event to actually send
	 * @throws EventException
	 *         if the event can't be sent
	 **/
	protected abstract void publish(Topic topic, Event event) 
		throws EventException;
	
	/**
	 * Subscribe a given topic hosted within the network.
	 * 
	 * @param topic   the topic to subscribe to
	 * @param handler the handler to receive events published to that topic
	 * @throws EventException
	 *         if the subscription couldn't happen
	 **/
	void subscribe(Topic topic, EventHandler handler)
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
	protected abstract void addSubscription(Topic topic) 
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
	void unsubscribe(Topic topic, EventHandler handler)
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
	protected abstract void removeSubscription(Topic topic)
		throws EventException;
	
	/**
	 * Create an empty event. 
	 * 
	 * @return empty Event object
	 **/
	public abstract Event createEmptyEvent();
	
	/**
	 * When subclass implementations receive an incoming event they must call
	 * this method to dispatch the event to the appropriate handlers.
	 * 
	 * @param event event to dispatch
	 **/
	protected void dispatch(Event event)
	{
		dispatcher.dispatch(event);
	}
}
