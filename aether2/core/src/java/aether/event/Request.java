package aether.event;

import org.elvin.je4.Subscription;

/**
 * Defines an Aether Request issued against some Aether Resource.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class Request extends Message implements Attribute.Request
{

    public String getEventType()
    {
        return aether.event.EventType.REQUEST;
    }

    /**
	 * Get the destination of the message.
	 *
	 * @return destination of the message
	 */
	public String getDestination()
	{
		return notification.getString(Attribute.Request.DESTINATION);
	}

	/**
	 * Set of the destination of this message.
	 *
	 * @param dest the destination of this message
	 */
	public void setDestination(String dest)
	{
		notification.put(Attribute.Request.DESTINATION, dest);
	}

    /**
	 * Construct a Subscription necessary to receive responses over a given
	 * link.
	 *
	 * @param linkId ID of the link to receive responses over
	 * @return Subscription necessary to receive responses
	 */
	public static Subscription createSubscriptionToReceiveResponses(
            String linkId)
	{
		if (linkId == null)
		{
			String msg = "linkId can't be null";
			throw new IllegalArgumentException(msg);
		}

		// receive all events that are responses and were sent over this link
        String expr = Attribute.Event.EVENT_TYPE + " == \"" +
				EventType.RESPONSE + "\" && " +
				LINK_ID + " == \"" + linkId + "\"";
		return new Subscription(expr);
	}

}
