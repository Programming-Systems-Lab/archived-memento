package aether.event;

import org.elvin.je4.Notification;
import org.elvin.je4.Subscription;

/**
 * Indicates that an Aether Resource has changed it's state.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class Notice extends Event implements Attribute.Notice
{
    public String getEventType()
    {
        return aether.event.EventType.NOTICE;
    }

	/**
	 * Get the topic ID of this notice if it was broadcast on a topic.
	 *
	 * @return topic ID of this notice rather than emitted
	 */
	public String getTopicId()
	{
		return notification.getString(TOPIC_ID);
	}

	/**
	 * Set the topic ID of this notice if it was broadcast on atopic.
	 *
	 * @param topic topic that the notice will be broadcast on
	 */
	public void setTopicId(String topic)
	{
		notification.put(TOPIC_ID, topic);
	}

	/**
	 * Create a subscription for watching notices that're published to a
	 * specific topic on the network.
	 *
	 * @param topic topic to publish events to
	 * @return Subscription necessary to subscribe the topic
	 */
	public static Subscription createTopicSubscription(String topic)
	{
		if (topic == null)
		{
			String msg = "topic can't be null";
			throw new IllegalArgumentException(msg);
		}

        // subscribe to notices published to the topic
        String expr = TOPIC_ID + " == \"" + topic + "\" && " +
				EVENT_TYPE + " == \"" + aether.event.EventType.NOTICE + "\"";
		return new Subscription(expr);
	}
}
