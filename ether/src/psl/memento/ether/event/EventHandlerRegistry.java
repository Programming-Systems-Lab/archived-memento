package psl.memento.ether.event;

import java.util.*;

/**
 * Manages the subscriptions held by all EventHandlers by keeping track of
 * which EventHandlers are subscribed to which topics.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
class EventHandlerRegistry
{
   private Map handlerMap = Collections.synchronizedMap(new HashMap());

   /**
	 * Register an EventHandler as being subscribed to a given topic.
	 *
	 * @param topic   URL of the topic the handler is registered to
	 * @param handler handler being registered
	 */
	public void register(TopicUrl topic, EventHandler handler)
	{
		if ((topic == null) || (handler == null))
		{
			String msg = "no parameter can be null";
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
         handlerMap.put(topic, handler);
		}
	}

	/**
	 * Unregister an EventHandler from a given Topic since it's no longer
	 * subscribed to that topic.
	 *
	 * @param topic   topic to unsubscribe the handler from
	 * @param handler handler to unsubscribe
	 */
   public void unregister(TopicUrl topic, EventHandler handler)
	{
		if ((topic == null) || (handler == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

      Collection handlerCol = (Collection) handlerMap.get(topic);
		if (handlerCol != null)
		{
			handlerCol.remove(handler);

			// if the collection is empty remove it
			if (handlerCol.isEmpty())
			{
				handlerMap.remove(topic);
			}
		}
	}

	/**
	 * Get all the EventHandlers subscribed to a given topic.
	 *
	 * @param topic topic to retreive the event handlers for
	 * @return array of event handlers subscribed to <code>topic</code>
	 */
   public EventHandler[] getHandlers(TopicUrl topic)
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
	 * Get the number of event handlers subscribed to a given topic.
	 *
	 * @param topic topic hosted within the ether
	 * @return number of EventHandlers subscribed to the topic
	 */
	public int numSubscribed(TopicUrl topic)
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
