package aether.event;

import org.elvin.je4.Notification;
import org.elvin.je4.Subscription;

/**
 * Represents a response from an Aether Resource having processed a request.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class Response extends Message implements Attribute.Response
{

    public String getEventType()
    {
        return aether.event.EventType.RESPONSE;
    }

	/**
	 * Construct a Subscription necessary to receive responses over a given
	 * link.
	 *
	 * @param linkId ID of the link to receive responses over
	 * @return Subscription necessary to receive responses
	 */
	public static Subscription createSubscriptionToReceive(String linkId)
	{
		if (linkId == null)
		{
			String msg = "linkId can't be null";
			throw new IllegalArgumentException(msg);
		}

		// receive all events that are responses and were sent over this link
        String expr = Attribute.Event.EVENT_TYPE + " == \"" +
				aether.event.EventType.RESPONSE + "\" && " +
				LINK_ID + " == \"" + linkId + "\"";
		return new Subscription(expr);
	}
}
