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
    /**
	 * Construct a new Response containing empty data.
	 */
	public Response()
	{
		super();
        setEventType(aether.event.EventType.Response);
	}

	/**
	 * Construct a Response from an existing notification.
	 *
	 * @param notif Notification containing response data
	 * @throws EventException
	 *         if <code>notif</code> is invalid
	 */
	public Response(Notification notif) throws EventException
	{
		super(notif);
	}

    /**
	 * Get the response code for this response.
	 *
	 * @return response code for this response or <code>-1</code>
	 */
	public int getCode()
	{
		return notification.getInt(Code);
	}

	/**
	 * Set the response code for this response.
	 *
	 * @param code response code for this response
	 */
	public void setCode(int code)
	{
		notification.put(Code, code);
	}

    /**
	 * Get the reason line for this response.
	 *
	 * @return reason line for this response
	 */
	public String getReasonLine()
	{
        return notification.getString(ReasonLine);
	}

	/**
	 * Set the reason line for this response.
	 *
	 * @param rl reason line for this response
	 */
	public void setReasonLine(String rl)
	{
		notification.put(ReasonLine, rl);
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
        String expr = Attribute.Event.EventType + " == \"" +
				aether.event.EventType.Response + "\" && " +
				LinkId + " == \"" + linkId + "\"";
		return new Subscription(expr);
	}
}
