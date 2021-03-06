package psl.memento.server.container.event;

import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Map;

import psl.memento.server.container.component.*;
import psl.memento.server.util.Uid;

/**
 * Represents an event routed between different components within the network.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class Event
{
	private Topic topicUrl;
	private Address source;
	
	/**
	 * If this event was sent to a topic, retrieve the URL of the topic it was
	 * sent to.
	 * 
	 * @return topic the event was published to or <c>null</c>
	 **/
	public Topic getTopic()
	{
		if (topicUrl == null)
		{
			try
			{
				topicUrl = new Topic(this.getString("event.topic"));
			}
			catch (MalformedURLException mue)
			{
				throw new IllegalStateException("bad topic url");
			}
		}
		
		return topicUrl;
	}
	
	/**
	 * Set the topic this event should be published to.
	 * 
	 * @param topicUrl topic this event will be published to
	 **/
	void setTopic(Topic topicUrl)
	{	
		this.topicUrl = topicUrl;
		
		if (topicUrl != null)
		{
			this.put("event.topic", topicUrl.toUrl());
		}
	}
	
	/**
	 * Get the address of the component which generated this event.
	 * 
	 * @return the address of the entity which published this event
	 **/
	public Address getSource()
	{
		if (source == null)
		{
			Uid compId = new Uid(getString("event.source.component-id"));
			Uid entityId = new Uid(getString("event.source.entity-id"));
			String ipAddr = getString("event.source.ip");
			
			try
			{
				Topic msgTopic = 
					new Topic(getString("event.source.message-topic"));
				source = new Address(ipAddr, entityId, compId, msgTopic);
			}
			catch (MalformedURLException mue)
			{
				// temporary fix
				String msg = "bad topic url received";
				throw new IllegalArgumentException(msg);
			}
		}
		
		return source;
	}

	/**
	 * Set the entity who sent this event.
	 * 
	 * @param source Address of the component which published this event
	 **/
	void setSource(Address source)
	{
		if (source == null)
		{
			String msg = "source can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.source = source;
		put("event.source.component-id", source.getComponentId().toString());
		put("event.source.entity-id", source.getEntityId().toString());
		put("event.source.ip", source.getIpAddress());
		put("event.source.message-topic", source.getMessageTopic().toUrl());
	}
	
	 /**
    * Determine whether an event contains a value mapped to a given key.
    *
    * @param key the key to test for existence
    * @return <code>true</code> if the event contains a value mapped to
    *         <code>key</code> else <code>false</code>
    **/
   public abstract boolean containsKey(String key);

   /**
    * Erase all key/value pairs within the event.
    **/
   public abstract void clear();

   /**
    * Get an integer value stored in the event.
    *
    * @param key the index the integer is stored under in the event
    * @return integer value mapped to <code>key</code>
    * @throws IllegalArgumentException
    *         if there is no integer mapped to <code>key</code>
    **/
   public abstract int getInteger(String key);

   /**
    * Get a long value stored in the event.
    *
    * @param key the index the long is stored under in the event
    * @return long value mapped to <code>key</code>
    * @throws IllegalArgumentException
    *         if there is no long mapped to <code>key</code>
    **/
   public abstract long getLong(String key);

   /**
    * Get a double value stored in the event.
    *
    * @param key the index the double is stored under in the event
    * @return double value mapped to <code>key</code>
    * @throws IllegalArgumentException
    *         if there is no double mapped to <code>key</code>
    **/
   public abstract double getDouble(String key);

   /**
    * Get a string stored in the event.
    *
    * @param key the index the string is stored under in the event
    * @return string value mapped to <code>key</code>
    * @throws IllegalArgumentException
    *         if there is no String mapped to <code>key</code>
    **/
   public abstract String getString(String key);

   /**
    * Get an array of arbitrary bytes stored in the event.
    *
    * @param key the index the byte array is stored under in the event
    * @return byte[] value mapped to <code>key</code>
    * @throws IllegalArgumentException
    *         if there is no array of bytes mapped to <code>key</code>
    **/
   public abstract byte[] getByteArray(String key);

   /**
    * Put an integer value into the event.
    *
    * @param key the key to store the integer value under
    * @param val the value of the integer value
    **/
   public abstract void put(String key, int val);

   /**
    * Put a long value into the event.
    *
    * @param key the key to store the long value under
    * @param val the value of the integer value
    **/
   public abstract void put(String key, long val);

   /**
    * Put a double value into the event.
    *
    * @param key the key to store the double value under
    * @param val the double to store into the event
    **/
   public abstract void put(String key, double val);

   /**
    * Put an ASCII string value into the event.
    *
    * @param key the key to store the String value under
    * @param val the value of the String value
    * @throws IllegalArgumentException
    *         if <code>val</code> is <code>null</code>
    **/
   public abstract void put(String key, String val);

   /**
    * Put an array of bytes into the event.
    *
    * @param key the key to store the byte array under
    * @param val the bytes to store into the event
    **/
   public abstract void put(String key, byte[] val);

   /**
    * Remove a value from the event.
    *
    * @param key the value is mapped to in the event
    **/
   public abstract  void remove(String key);
   
   /**
    * Get an enumeration over all the keys in this event.
    *
    * @return enumeration all the keys in this event
    **/
   public abstract Enumeration keys();
}
