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
	private Map query = new HashMap();

    /**
	 * Construct a new Request containing no data.
	 */
	public Request()
	{
		super();
		setEventType(aether.event.EventType.Request);
	}

	/**
	 * Construct a new Request from an existing notification.
	 *
	 * @param notif Notification containing request data
	 * @throws EventException
	 *         if <code>notif</code> is invalid
	 */
	public Request(Notification notif) throws EventException
	{
		super(notif);

		readQuery();
	}

    /**
	 * Get the VERB of this request.
	 *
	 * @return verb of this request
	 */
	public String getVerb()
	{
		return this.notification.getString(Verb);
	}

	/**
	 * Set the VERB of this request.
	 *
	 * @param verb verb of this request
	 */
	public void setVerb(String verb)
	{
		this.notification.put(Verb, verb);
	}

	/**
	 * Retrieve a query parameter of the request.
	 *
	 * @param name name of the parameter
	 * @return value of the parameter or <code>null</code>
	 */
	public String getParameter(String name)
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		return (String)  query.get(name);
	}

	/**
	 * Set a query parameter on the request.
	 *
	 * @param name name of the parameter
	 * @param val  value of the parameter
	 */
	public void setParameter(String name, String val)
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		query.put(name, val);
	}

    /**
	 * Return an enumeration of all the parameters in the request.
	 *
	 * @return enumeration of all the parameters in the map
	 */
	public Enumeration parameters()
	{
		return Collections.enumeration(query.keySet());
	}

    /**
	 * Write all the query parameters to the underlying notification.
	 */
	private void writeQuery()
	{
  		StringBuffer sb = new StringBuffer();

        for (Iterator iter = query.entrySet().iterator(); iter.hasNext(); )
		{
			Map.Entry me = (Map.Entry) iter.next();

			// add it to the space separated list of query params
			sb.append((String) me.getKey()).append(' ');

            // store it in the notification
			this.notification.put("aether.request.query." +
								  (String) me.getKey(),
								  (String) me.getValue());
		}

        // store the list of query params
		notification.put(Query, sb.toString());
	}

	/**
	 * Read the query parameters from the underlying notification object.
	 */
	private void readQuery()
	{
		// skip processing if no query parameters were passed
		if (!notification.containsKey(Query)) return;

        // tokenize the list of query parameters
        StringTokenizer tokenizer =
				new StringTokenizer(notification.getString(Query));

		while (tokenizer.hasMoreTokens())
		{
			String param = tokenizer.nextToken();

			String val = notification.getString("aether.request.query." +
												param);
			query.put(param, val);
		}
	}

	/**
	 * Execute lifecycle management logic right before the request is
	 * enqueued.
	 *
	 * @throws EventException
	 *         if the request can't be queued
	 */
	public void onPublish() throws EventException
	{
		super.onPublish();

		writeQuery();
	}

	/**
	 * Given a request, construct an appropriate Response object.
	 *
	 * @param req    Request received
	 * @param srcId  GUID of the component generating the response
	 * @return Response object appropriate for this request
	 */
    public static Response createResponse(Request req, String srcId)
	{
		if ((req == null) || (srcId == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

        Response resp = new Response();

        // maintain the link
		resp.setLink(req.getLink());

		// destination of the response is the source of the request
		resp.setDestination(req.getSourceId());

		// source of the response is the GUID of the responder
		resp.setSourceId(srcId);

		resp.setTime(System.currentTimeMillis());

		return resp;
	}

}
