package psl.memento.server.container.event;

import java.util.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the subscriptions held by different EventHandlers to different
 * topics.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
class EventHandlerManager
{
	private Map handlerMap = Collections.synchronizedMap(new HashMap());
	
	/**
	 * Add an EventHandler to collection of subscribed handlers to a given
	 * topic.
	 * 
	 * @param topic   Topic the handler is subscribing to
	 * @param handler handler to handle events coming from that topic
	 **/
	public void add(Topic topic, EventHandler handler)
	{
		if ((topic == null) || (handler == null))
		{
			String msg = "no param can be null";
			throw new IllegalArgumentException(msg);
		}
		
		if (handlerMap.containsKey(topic))
		{
			Collection handlerCol = (Collection) handlerMap.get(topic);
			handlerCol.add(handler);
		}
		else
		{
			List handlerList = Collections.synchronizedList(new ArrayList());
			handlerList.add(handler);
			handlerMap.put(topic, handlerList);
		}	
	}
	
	/**
	 * Remove an EventHandler from the collection of handlers subscirbed to a 
	 * given topic.
	 * 
	 * @param topic   topic to unsubscirbe the handler from
	 * @param handler handler to unsubscribe
	 **/
	public void remove(Topic topic, EventHandler handler)
	{
		if ((topic == null) || (handler == null))
		{
			String msg = "no param can be null";
			throw new IllegalArgumentException(msg);
		}
		
		Collection handlerCol = (Collection) handlerMap.get(topic);
		if (handlerCol != null)
		{
			handlerCol.remove(handler);
		}
	}
	
	/**
	 * Retrieve the collection of EventHandlers subscribed to a given topic.
	 * 
	 * @param topic topic to retrieve the list of event handlers subscribed to
	 *              that topic
	 **/
	public EventHandler[] getHandlers(Topic topic)
	{
		if (topic == null)
		{
			String msg = "topic can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		if (handlerMap.containsKey(topic))
		{
			return (EventHandler[])  
				((Collection) handlerMap.get(topic)).toArray(new EventHandler[0]);
			
		}
		else
		{
			return new EventHandler[0];
		}
	}
	
	/**
	 * Get the number of handlers subscribed to a given topic.
	 * 
	 * @param topic a hosted topic in the network
	 * @return number of handlers subscribed to the topic
	 **/
	public int numSubscribed(Topic topic)
	{
		if (topic == null)
		{
			String msg = "topic can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		if (handlerMap.containsKey(topic))
		{
			return ((Collection) handlerMap.get(topic)).size();
		}
		else
		{
			return 0;
		}
	}

}
