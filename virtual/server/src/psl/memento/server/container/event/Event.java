package psl.memento.server.container.event;

import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Map;

import psl.memento.server.util.Uid;

/**
 * Represents an event routed between different components within the network.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class Event
{
	private TopicUrl topicUrl;
	private Uid target;
	private EventHub sourceHub;
	private Uid source;
	private Session session;
	
	/**
	 * If this event was sent to a topic, retrieve the URL of the topic it was
	 * sent to.
	 * 
	 * @return topic the event was published to or <c>null</c>
	 **/
	public TopicUrl getTopic()
	{
		if (topicUrl == null)
		{
			try
			{
				topicUrl = new TopicUrl(this.getString("event.topic"));
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
	void setTopic(TopicUrl topicUrl)
	{	
		this.topicUrl = topicUrl;
		
		if (topicUrl != null)
		{
			this.put("event.topic", topicUrl.toUrl());
		}
	}
	
	/**
	 * If the event was sent to a specific entity (using the point-to-point
	 * system) get the id of the entity it was sent to.
	 * 
	 * @return entity id of the entity this event was sent to or <c>null</c>
	 **/
	public Uid getTarget()
	{
		if (target == null && containsKey("event.target"))
		{
			target = new Uid(getString("event.target"));
		}
			
		return target;
	}
	
	/**
	 * Set the specific target this event was sent to.
	 * 
	 * @param target the specific entity the event was sent to
	 **/
	void setTarget(Uid target)
	{
		this.target = target;
		
		if (target != null)
		{
			this.put("event.target", target.toString());
		}
	}
	
	/**
	 * Get the EventHub that this event came from.
	 * 
	 * @return event hub this event was routed from
	 **/
	public EventHub getSourceHub()
	{
		if (sourceHub == null)
		{
			String host = getString("event.sourceHub.host");
			int port = getInteger("event.sourceHub.port");
			sourceHub = new EventHub(host, port);
		}
		
		return sourceHub;
	}
	
	/**
	 * Set the event hub which sent this event.
	 * 
	 * @param sourceHub event hub which sent this event
	 **/
	void setSourceHub(EventHub sourceHub)
	{
		if (sourceHub == null)
		{
			String msg = "sourceHub can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.sourceHub = sourceHub;
		
		put("event.sourceHub.host", sourceHub.getHost());
		put("event.sourceHub.port", sourceHub.getPort());
	}
	
	/**
	 * Get the id of the entity which sent this event.
	 * 
	 * @return the entity which sent this event
	 **/
	public Uid getSource()
	{
		if (source == null)
		{
			source = new Uid(getString("event.source"));
		}
		
		return source;
	}

	/**
	 * Set the entity who sent this event.
	 * 
	 * @param source entity who sent this event
	 **/
	void setSource(Uid source)
	{
		if (source == null)
		{
			String msg = "source can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.source = source;
		put("event.source", source.toString());
	}
	
	/**
	 * Get the Session associated with the client who sent this event.
	 * 
	 * @return Session associated with the client who sent this event
	 **/
	public Session getSession()
	{
		return session;
	}
	
	/**
	 * Set the session assocaited with the client who sent this event.
	 * 
	 * @param sess Session associated with the client who sent this event
	 **/
	void setSession(Session session)
	{
		this.session = session;
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
