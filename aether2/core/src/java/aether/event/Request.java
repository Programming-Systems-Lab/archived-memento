package aether.event;

import org.elvin.je4.Notification;

import java.util.*;

/**
 * Defines an Aether Request issued against some Aether Resource.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class Request extends Message implements Attribute.Request, Verb
{
    public String getEventType()
    {
        return aether.event.EventType.REQUEST;
    }

	/**
	 * Construct an appropriate Response object for responding to this Request
     * object.
	 *
	 * @param srcId  GUID of the component generating the response
	 * @return Response object appropriate for this request
	 */
    public Response createResponse(String srcId)
	{
		if (srcId == null)
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

        Response resp = new Response();

        // maintain the link
		resp.setLink(getLink());

		// destination of the response is the source of the request
		resp.setDestination(getSourceId());

		// source of the response is the GUID of the responder
		resp.setSourceId(srcId);

		resp.setTime(System.currentTimeMillis());

		return resp;
	}

}
