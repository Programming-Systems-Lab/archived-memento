package psl.memento.ether.event;

/**
 * Represents a connection to a Topic hosted within the network.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class TopicUrlConnection
{
	private TopicUrl topic;
	private EventService eventService;
	private ComponentUrl source;
	private boolean connected;

	/**
	 * Construct a new TopicUrlConnection to a given topic hosted within the
	 * network.
	 *
	 * @param topic  URL of the Topic to connect to
	 * @param source source of the component generating events to be sent to the
	 *               given topic
	 */
	public TopicUrlConnection(TopicUrl topic, ComponentUrl source)
	{
		if ((topic == null) || (source == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		this.topic = topic;
		this.source = source;

		// get the event service
		this.eventService = EventService.getInstance();
		if (eventService == null)
		{
			String msg = "can't open TopicUrlConnection when there is no event " +
					"service";
			throw new IllegalStateException(msg);
		}
	}

	/**
	 * Open the connection to the topic hosted on the network.
	 *
	 * @throws EventException
	 *         if the connection couldn't be opened
	 */
	public void open() throws EventException
	{
		if (connected)
		{
			String msg = "connection already open";
			throw new IllegalStateException(msg);
		}
		else
		{
			eventService.openVirtualConnection(topic.getHostname(),
											   topic.getPort());
			connected = true;
		}
	}

	/**
	 * Close the connection to the topic hosted on the network.
	 */
	public void close()
	{
		eventService.closeVirtualConnection(topic.getHostname(), topic.getPort());
		connected = false;
	}

	/**
	 * Publish an event on this topic.
	 *
	 * @param event event to publish on this topic
	 * @throws EventException
	 *         if the event couldn't be sent
	 */
	public void publish(Event event) throws EventException
	{
		if (source == null)
		{
			String msg = "source is null, event publishing impossible";
			throw new IllegalStateException(msg);
		}

		eventService.publish(topic, event, source);
	}

	/**
	 * Publish an event which comes from the specified source. This allows the
	 * connection to be shared by multiple components.
	 *
	 * @param event   event to publish on this topic
	 * @param source  url of the component to specify as the event source
	 */
	public void publish(Event event, ComponentUrl source) throws EventException
	{
		if (source == null)
		{
			String msg = "source can't be null";
			throw new IllegalArgumentException(msg);
		}

		eventService.publish(topic, event, source);
	}

	/**
	 * Subscribe an EventHandler to the topic.
	 *
	 * @param handler EventHandler to subscribe to the topic
	 * @throws EventException
	 *         if the subscription couldn't be made
	 */
	public void subscribe(EventHandler handler) throws EventException
	{
		eventService.subscribe(topic, handler);
	}

	/**
	 * Unsubscribe an EventHandler from the topic.
	 *
	 * @param handler EventHandler to unsubscribe from the topic
	 * @throws EventException
	 *         if the unsubscription fails
	 */
	public void unsubscribe(EventHandler handler) throws EventException
	{
		eventService.unsubscribe(topic, handler);
	}

	/**
	 * Get a brand-new, blank event.
	 *
	 * @return a new empty event
	 */
	public Event newEvent()
	{
		return eventService.createEmptyEvent();
	}
}
