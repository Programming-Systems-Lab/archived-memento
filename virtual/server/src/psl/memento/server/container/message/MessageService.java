package psl.memento.server.container.message;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import psl.memento.server.container.component.Address;
import psl.memento.server.container.component.Component;
import psl.memento.server.container.event.*;

/**
 * Send Messages to specific components distributed within the network.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class MessageService
{
	private EventService eventService;
	private Topic msgTopic;
	private TopicConnection msgTopicConn;
	private MessageService.Dispatcher msgDispatcher = 
		new MessageService.Dispatcher();
	private Map addressMap = Collections.synchronizedMap(new HashMap());
	
	/**
	 * Construct a new MessageService which uses the underlying event
	 * service.
	 * 
	 * @param msgTopic     Topic used to receive messages for this container
	 * @param eventService event service used to send/receive messages
	 * @throws MessageException
	 *         if the messaging service fails to initialize itself
	 **/
	public MessageService(Topic msgTopic, EventService eventService)
		throws MessageException
	{
		if ((msgTopic == null) || (eventService == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.eventService = eventService;
		
		initialize();
	}
	
	/**
	 * Perform the basic initialization of the message service.
	 **/
	private void initialize() throws MessageException
	{
		// first subscribe to the messaging topic for this container
		try
		{
			msgTopicConn = eventService.openConnection(msgTopic);
			msgTopicConn.subscribe(msgDispatcher);
		}
		catch (EventException ee)
		{
			String msg = "couldn't subscribe to container message topic";
			throw new MessageException(msg);
		}
	}
	
	/**
	 * Shutdown the message service so that it will no longer dispatch
	 * incoming messages to components.
	 **/
	public void dispose()
	{
		// close the connection to the messaging topic
		msgTopicConn.close();
	}
			
	
	/**
	 * Send a message to a component at the given address.
	 * 
	 * @param msg     Message to send
	 * @param address address to send the message to
	 * @param source  component sending this message
	 * @throws MessageException
	 *         if the message can't be sent
	 **/
	public void send(Message msg, Address address, Component source) 
		throws MessageException
	{
		if ((msg == null) || (address == null) || (source == null))
		{
			String msge = "no parameter can be null";
			throw new IllegalArgumentException(msge);
		}
		
		// set the msg recipient and sender
		msg.setRecipient(address);
		msg.setSender(source.getAddress());
		
		// send the msg
		TopicConnection conn = null;
		try
		{
			conn = 
				eventService.openConnection(address.getMessageTopic(), source);
			conn.publish(msg.getUnderlyingEvent());
		}
		catch (Exception e)
		{
			String msge = "couldn't send message";
			throw new MessageException(msge, e);
		}
		finally
		{
			if (conn != null)
			{
				conn.close();
			}
		}
	}	
	
	/**
	 * In order for a component to receieve messages it must register itself
	 * with the messaging service using this method.
	 * 
	 * @param component component which wishes to receieve messages
	 * @param listener  MessageListener to handle incoming messages
	 **/
	public void register(Component component, MessageListener listener)
	{
		if (component == null)
		{
			String msg = "component can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		addressMap.put(component.getAddress(), listener);
	}
	
	/**
	 * If a component wishes to no longer receieve point-to-point messages it
	 * must unregister itself with the messaging service using this method.
	 * 
	 * @param component component which wishes to no longer receieve messages
	 **/	
	private void unregister(Component component)
	{
		addressMap.remove(component.getAddress());
	}
	
	/**
	 * Internal class used to dispatch incoming message-events.
	 * 
	 * @author Buko O. (buko@cs.columbia.edu)
	 * @version 0.1
	 **/
	private final class Dispatcher implements EventHandler
	{
		/**
		 * Construct a new Message from the event and then dispatch it.
		 * 
		 * @param event event which contains a message
		 **/
		public void handleEvent(Event event)
		{
			Message msg = new Message(event);
			
			// get the address of the component supposed to receieve this 
			// msg and get the message listener
			MessageListener listener = 
				(MessageListener) addressMap.get(msg.getRecipient());
			listener.onMessage(msg);
		}
	}
}
