package aether.event;

import org.elvin.je4.Subscription;

/**
 * Represents a response from an Aether Resource having processed a request.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class Response extends Message implements Attribute.Response
{
    /**
     * Construct an empty Response. This constructor should only be used if
     * you've received notification which contains response-data and you need
     * to parse it. Otherwise you should always construct a Response from the
     * Request that triggered it.
     */
    public Response()
    {
        ; // do nothing
    }

    public Response(Request req)
    {
        if (req == null)
        {
            String msg = "req can't be null";
            throw new IllegalArgumentException(msg);
        }

        setResponseTo(req.getEventId());
    }

    public String getEventType()
    {
        return aether.event.EventType.RESPONSE;
    }

    public void setResponseTo(String requestID)
    {
        notification.put(RESPONSE_TO, requestID);
    }

    public String getResponseTo()
    {
        return notification.getString(RESPONSE_TO);
    }

    /**
	 * Construct a Subscription indicating that a component wants to receive
	 * messages sent to a certain destination.
	 *
	 * @param dest destination to subscribe to (this may be either a GUID or a
     *             URL)
	 */
	public static Subscription createSubscriptionToReceiveRequests(String dest)
	{
		if (dest == null)
		{
			String msg = "dest can't be null";
			throw new IllegalArgumentException(msg);
		}

		// some destinations may be regexable URLS!
   		String expr = "regex(" + Attribute.Request.DESTINATION + ", " +
				   " \"" + dest + "\")";
		return new Subscription(expr);
	}
}
