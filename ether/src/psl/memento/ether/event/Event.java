package psl.memento.ether.event;


import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Map;

import psl.memento.ether.util.Uid;
import psl.memento.ether.event.session.Session;

/**
 * Represents an event routed between different components within the network.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class Event
{
	private TopicUrl topicUrl;
	private ComponentUrl source;
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
	 * Get the address of the component which generated this event.
	 *
	 * @return the address of the component which published this event
	 **/
	public ComponentUrl getSource()
	{
		if (source == null)
		{
			try
			{
				source = new ComponentUrl(this.getString("event.source"));
			}
			catch (MalformedURLException mue)
			{
				String msg = "bad source component url received";
				throw new IllegalStateException(msg);
			}
		}

		return source;
	}

	/**
	 * Set the entity who sent this event.
	 *
	 * @param source Address of the component which published this event
	 **/
	void setSource(ComponentUrl source)
	{
		if (source == null)
		{
			String msg = "source can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.source = source;
		put("event.source.", source.toUrl());
	}

	/**
	 * Get the session associated with this event.
	 *
	 * @return session associated with this event
	 */
	public Session getSession()
	{
		return session;
	}

	/**
	 * Set the session associated with this event.
	 *
	 * @param session Session to associate with this event
	 */
	public void setSession(Session session)
	{
		if (session == null)
		{
			String msg = "session can't be null";
			throw new IllegalArgumentException(msg);
		}

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
