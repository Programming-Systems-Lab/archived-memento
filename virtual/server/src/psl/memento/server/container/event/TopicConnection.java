package psl.memento.server.container.event;

import psl.memento.server.container.component.Component;

/**
 * Represents a connection to a topic hosted within the network.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class TopicConnection
{
	private Topic topic;
	private EventService eventService;
	private Component source;
	private boolean connected;
	
	/**
	 * Construct a new TopicConnection using the given event service and 
	 * connecting to the given topic.
	 * 
	 * @param topic   the topic to connect to
	 * @param service event service to use to send messages to the topic
	 * @param source  component which will be sending events to this topic
	 * @throws EventException
	 *         if the topic cannot be opened
	 **/
	TopicConnection(Topic topic, EventService service, Component source) 
		throws EventException
	{
		if ((topic == null) || (service == null) || (source == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.topic = topic;
		this.eventService = service;
		this.source = source;
		connected = true;
		
		// open the connection to the event hub
		service.openVirtualConnection(topic.getHost(), topic.getPort());
	}
	
	/**
	 * Construct a new TopicConnection to be used only for subscribing to 
	 * topics. Because no Component source is provided, this connection can't
	 * be used to send events.
	 * 
	 * @param topic   topic to connect to
	 * @param service event service to use 
	 **/
	TopicConnection(Topic topic, EventService service) throws EventException
	{
		if ((topic == null) || (service == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.topic = topic;
		this.eventService = service;
		connected = true;
		
		// open the connection to the hub
		service.openVirtualConnection(topic.getHost(), topic.getPort());
	}
	
	/**
	 * Close the connection to the topic. You should close a connection to a
	 * topic whenever you are finished with it ie after you're done publishing
	 * events to the topic or you've unsubscribed all handlers from the topic.
	 * <p>
	 * Note that before closing a connection to a topic you <em>must</em> 
	 * unsubscribe all EventHandlers which subscribed to the topic. 
	 **/
	public void close()
	{
		connected = false;
		eventService.closeVirtualConnection(topic.getHost(), topic.getPort());
	}
	
	/**
	 * Send an event to this topic.
	 * 
	 * @param event event to send to this topic
	 * @throws EventException
	 *         if the event can't be sent
	 **/
	public void publish(Event event) throws EventException
	{
		if (source != null)
		{
			eventService.publish(topic, event, source);
		}
		else
		{
			String msg = "source is null; connection can't be used to send" +
				" events";
			throw new IllegalStateException(msg);
		}
	}
	
	/**
	 * Subscribe an event handler to this topic.
	 * 
	 * @param handler handler to subscribe to this topic
	 **/
	public void subscribe(EventHandler handler) throws EventException
	{
		eventService.subscribe(topic, handler);
	}
	
	/**
	 * Unsubscribe an event handler from this topic.
	 * 
	 * @param handler handler to unsubscribe from this topic
	 **/
	public void unsubscribe(EventHandler handler) throws EventException
	{
		eventService.unsubscribe(topic, handler);
	}
}
