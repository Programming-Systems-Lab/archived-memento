package psl.memento.ether.event;

import psl.memento.ether.event.session.Session;
import psl.memento.ether.event.session.SessionProvider;

/**
 * Represents the main interface to the ether event system.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class EventService
{
	private VirtualConnectionManager vcm = new VirtualConnectionManager();
	private EventHandlerRegistry handlerRegistry = new EventHandlerRegistry();
	private EventDispatcher dispatcher;
	private static EventService singleInstance;
	private SessionProvider sessionProvider;


	/**
	 * Default ctor.
	 *
	 * @param sessionProv SessionProvider to be used for event sessions
	 **/
	protected EventService(SessionProvider sessProv)
	{
		if (sessProv == null)
		{
			String msg = "sessionProv can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.sessionProvider = sessProv;

		dispatcher = new EventDispatcher(handlerRegistry);
	}

	/**
	 * Get the singleton instance of the EventService.
	 *
	 * @return singleton instance of the event service
	 */
	static EventService getInstance()
	{
		return singleInstance;
	}

	/**
	 * Set the singleton instance of the event service to be used by the
	 * container. Only the container should ever call this method.
	 *
	 * @param service EventService implementation to be used
	 */
	public static void setInstance(EventService service)
	{
		singleInstance = service;
	}

	/**
	 * Start the EventService.
	 */
	public void start()
	{
		; // do nothing
	}

	/**
	 * Stop the EventService.
	 */
	public void stop()
	{
		// stop the dispatcher
		dispatcher.stop();
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
	 * @param source url of the component publishing the event
	 * @throws IllegalStateException
	 *         if no connection exists to the hub hosting the topic
	 * @throws EventException
	 *         if the event can't be sent
	 **/
	void publish(TopicUrl topic, Event event, ComponentUrl source)
			throws EventException
	{
		if ((topic == null) || (event == null) || (source == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		if (!vcm.isConnected(topic.getHostname(), topic.getPort()))
		{
			String msg = "no connection exists to " + topic.getHostname() + ":"
					+ topic.getPort();
			throw new IllegalStateException(msg);
		}

		// add the routing info
		event.setSource(source);
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
	protected abstract void publish(TopicUrl topic, Event event)
			throws EventException;

	/**
	 * Subscribe a given topic hosted within the network.
	 *
	 * @param topic   the topic to subscribe to
	 * @param handler the handler to receive events published to that topic
	 * @throws EventException
	 *         if the subscription couldn't happen
	 **/
	void subscribe(TopicUrl topic, EventHandler handler)
			throws EventException
	{
		if ((topic == null) || (handler == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		// make sure a connection exists
		if (!vcm.isConnected(topic.getHostname(), topic.getPort()))
		{
			String msg = "no connection exists to " + topic.getHostname() + ":" +
					topic.getPort();
			throw new IllegalStateException(msg);
		}

		// 	if there are no handlers already subscribed to this topic this is
		// first one so a real subscription must be created
		if (handlerRegistry.numSubscribed(topic) < 1)
		{
			addSubscription(topic);
		}

		// if the subscription was made, add it to the ehm
		handlerRegistry.register(topic, handler);
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
	void unsubscribe(TopicUrl topic, EventHandler handler)
			throws EventException
	{
		if ((topic == null) || (handler == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		// make sure a connection exists
		if (!vcm.isConnected(topic.getHostname(), topic.getPort()))
		{
			String msg = "no connection exists to " + topic.getHostname() + ":" +
					topic.getPort();
			throw new IllegalStateException(msg);
		}

		// if this is the last handler subscribed to the topic then remove the
		// real subscription
		if (handlerRegistry.numSubscribed(topic) == 1)
		{
			removeSubscription(topic);
		}

		// remove the real subscription
		handlerRegistry.unregister(topic, handler);
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
		// set the session on the event
		Session sess = sessionProvider.getSession(event.getSource());
		event.setSession(sess);

		dispatcher.dispatch(event);
	}
}
