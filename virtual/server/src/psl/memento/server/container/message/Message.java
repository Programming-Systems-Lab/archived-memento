package psl.memento.server.container.message;

import java.net.MalformedURLException;

import psl.memento.server.container.component.Address;
import psl.memento.server.container.event.Event;
import psl.memento.server.container.event.Topic;
import psl.memento.server.util.Uid;

/**
 * Represents a message that one component can send directly to another.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class Message
{
	private Address sender;
	private Address recipient;
	private Event event;
	
	/**
	 * Construct a new Message with the underlying event.
	 * 
	 * @param event underlying Event used to transport the message
	 **/
	Message(Event event)
	{
		if (event == null)
		{
			String msg = "event cannot be null";
			throw new IllegalArgumentException(msg);
		}
		 
		this.event = event;
	}
	
	/**
	 * Get the underlying event used to transport this message.
	 * 
	 * @return underlying event used to transport this message
	 **/
	Event getUnderlyingEvent()
	{
		return event;
	}
	
	/**
	 * Get the Address of the component which sent this message.
	 * 
	 * @return address of the component which sent this message
	 **/
	public Address getSender()
	{
		if (sender == null)
		{
			try
			{
				Uid compId = new Uid(get("message.sender.component-id"));
				Uid entityId = new Uid(get("message.sender.entity-id"));
				String ip = get("message.sender.ip");
				Topic msgTopic = new Topic(get("message.sender.message-topic"));
				
				sender = new Address(ip, entityId, compId, msgTopic);
			}
			catch (MalformedURLException mue)
			{
				// temporary fix
				String msg = "bad topic received";
				throw new IllegalArgumentException(msg);
			}
		}
		
		return sender;
	}
	
	/**
	 * Set the address of the component which sent this message.
	 * 
	 * @param sender address of the component which sent this message
	 **/
	void setSender(Address sender)
	{
		if (sender == null)
		{
			String msg = "sender can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		put("message.sender.component-id", sender.getComponentId().toString());
		put("message.sender.entity-id", sender.getEntityId().toString());
		put("message.sender.ip", sender.getIpAddress());
		put("message.sender.message-topic", sender.getMessageTopic().toUrl());
		
		this.sender = sender;
	}
	
	/**
	 * Get the address of the component which received this message.
	 * 
	 * @return address of the component which received this message
	 **/
	public Address getRecipient()
	{
		if (recipient == null)
		{
			try
			{
				Uid compId = new Uid(get("message.recipient.component-id"));
				Uid entId = new Uid(get("message.recipient.entity-id"));
				String ip = get("message.recipient.ip");
				Topic msgTopic = new Topic(get("message.recipient.message-topic"));
				
				recipient = new Address(ip, entId, compId, msgTopic);
			}
			catch (MalformedURLException mue)
			{
				String msg = "illegal topic received";
				throw new IllegalArgumentException(msg);
			}
		}
		
		return recipient;
	}
	
	/**
	 * Set the address of the component which recieved this message.
	 * 
	 * @param recipient address of the component which received this message
	 **/
	void setRecipient(Address recipient)
	{
		if (recipient == null)
		{
			String msg = "recipient can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		put("message.recipient.component-id", recipient.getComponentId().toString());
		put("message.recipient.entity-id", recipient.getEntityId().toString());
		put("message.recipient.ip", recipient.getIpAddress());
	}
	
	
	/**
	 * Retrieve a value stored from the key/value pairs stored in the message.
	 * 
	 * @param key name of the value to retreive from the message
	 * @return a String value mapped to <c>key</c> or <c>null</c>
	 **/
	public String get(String key)
	{
		return event.getString(key);
	}
	
	/**
	 * Store a key/value pair in the message.
	 * 
	 * @param key name of the key to map the value to
	 * @param val value to store in the map
	 **/
	public void put(String key, String val)
	{
		event.put(key, val);
	}
}
