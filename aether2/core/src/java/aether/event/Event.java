package aether.event;

import aether.util.GuidFactory;
import org.elvin.je4.Notification;

import java.util.*;

/**
 * Fundamental Event class that represents all events published in the Aether
 * network.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public abstract class Event extends java.util.EventObject
		implements Attribute.Event
{
	static
	{
		// don't throw exceptions when values aren't found!
        Notification.throwExceptions(false);
	}

	private Map headers = new HashMap();
    private Map procInstructions = new HashMap();

    /**
	 * Underlying Elvin notification that'll actually be published.
	 */
	protected Notification notification;

    /**
	 * Construct a new Event containing no data.
	 */
	protected Event()
	{
		super(new Object());
		// TODO: fix this, what should be the source of this event?

		this.notification = new Notification();

		// assign a unique event id
		this.notification.put(EventId, GuidFactory.createId());
	}

	/**
	 * Construct a new Event from an existing Notification data.
	 *
	 * @param notif Notificaton containing Event data
	 * @throws EventException
	 *         if <code>notif</code> contains invalid data
	 */
	protected Event(Notification notif) throws EventException
	{
		super(new Object());

        if (notif == null)
		{
			String msg = "notif can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.notification = notif;

		// initialize the headers
		readHeaders(notif);
	}

	/**
	 * Get the underlying Notification object of the event.
	 *
	 * @return underlying Notification object
	 */
	Notification getNotification()
	{
		return notification;
	}

	/**
	 * Get the unqiue event ID for this event.
	 *
	 * @return unique event id
	 */
	public String getEventId()
	{
		return notification.getString(EventId);
	}

	/**
	 * Get the time this event was sent.
	 *
	 * @return time this event was sent
	 */
	public long getTime()
	{
		return notification.getLong(Time);
	}

	/**
	 * Set the time this event was sent.
	 *
	 * @param time time this event was sent
	 */
	public void setTime(long time)
	{
		notification.put(Time, time);
	}

	/**
	 * Get the GUID of the component that generated this notice.
	 *
	 * @return guid of the component that generated this notice
	 */
	public String getSourceId()
	{
		return notification.getString(SourceId);
	}

	/**
	 * Set the GUID of the component that generated this notice.
	 *
	 * @param guid GUID of the component that generated this notice
	 */
	public void setSourceId(String guid)
	{
		notification.put(SourceId, guid);
	}

    /**
	 * Get the body of this event.
	 *
	 * @return body of this event or <code>null</code>
	 */
	public byte[] getBody()
	{
		return notification.getBytes(Body);
	}

	/**
	 * Set the body of this event.
	 *
	 * @param body body of this event
	 */
	public void setBody(byte[] body)
	{
		notification.put(Body, body);
	}

	/**
	 * Get the event type of this event.
	 */
    public String getMessageType()
	{
		return notification.getString(EventType);
	}

	/**
	 * Set the event type of this event.
	 *
	 * @param type type of this event. this must be one of the EventType
	 *             constants
	 */
	protected void setEventType(String type)
	{
		notification.put(EventType, type);
	}

    /**
	 * Set a header on this event.
	 *
	 * @param name name of the header
	 * @param val  value of the header
	 */
	public void setHeader(String name, String val)
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		headers.put(name, val);
	}

    /**
	 * Get a header from this event.
	 *
	 * @param name name of the header
	 * @return value of the header with <code>name</code> or <code>null</code>
	 */
	public String getHeader(String name)
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		return (String) headers.get(name);
	}

	/**
	 * Retrieve an enumeration over the headers.
	 *
	 * @return enumeration of he headers of this event
	 */
	public Enumeration headers()
	{
		return Collections.enumeration(headers.keySet());
	}

	/**
	 * Store all the headers on the Event in the underlying event.
	 */
	private void writeHeaders(Map headers)
	{
		StringBuffer sb = new StringBuffer();

        for (Iterator i = headers.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry me = (Map.Entry) i.next();

			// add it to the space-separated list of headers
            sb.append((String) me.getKey()).append(' ');

            // store it in the notification
			notification.put("aether.event.header." + (String) me.getKey(),
							 (String) me.getValue());
		}

		// store the list of headers in the notification
		notification.put(Headers, sb.toString());
	}

	/**
	 * Initialize the headers map from Notification.
	 */
	private void readHeaders(Notification notif)
	{
		// skip processing if no headers were defined
        if (!notification.containsKey(Headers)) return;

        // get the list of headers and tokenize it
		StringTokenizer tokenizer =
				new StringTokenizer(notif.getString(Headers));

		// set each header
		while (tokenizer.hasMoreTokens())
		{
			String key = tokenizer.nextToken();

			String val = notification.getString("aether.event.header." + key);
			setHeader(key, val);
		}
	}

	/**
	 * Set a processing instruction on the event.
	 *
	 * @param name name of of the PI
	 * @param val  object to bind to the PI
	 */
	public void setProcessingInstruction(String name, Object val)
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		procInstructions.put(name, val);
	}

    /**
	 * Get a processing instruction set on this event.
	 *
	 * @param name name of the PI
	 * @return Object bound to <code>name</code> or <code>null</code>
	 */
	public Object getProcessingInstruction(String name)
	{
		if (name == null)
		{
			String msg = "name can't be null";
			throw new IllegalArgumentException(msg);
		}

		return procInstructions.get(name);
	}

    /**
	 * Allow this event to perform any lifecycle logic right before it is
	 * published.
	 *
	 * @throws EventException
	 *         if this event can't be queued
	 */
	public void onPublish() throws EventException
	{
		writeHeaders(headers);
	}

	/**
	 * Allow a Event to perform lifecycle logic right after it's been
	 * received.
	 *
	 * @throws EventException
	 *         if something goes wrong
	 */
	public void onReceive() throws EventException
	{
		; // do nothing
	}

	/**
	 * Get the underlying Elvin notification.
	 *
	 * @return underlying Elvin notification
	 */
	public Notification notification()
	{
		return notification;
	}

	/**
	 * Determine if an Elvin notification contains Notice data.
	 *
	 * @param notification  notification to query
	 * @return <code>true</code> iff <code>notification</code> is a Notice
	 */
	public static boolean isNotice(Notification notification)
	{
		if (notification == null)
		{
			String msg = "notification can't be null";
			throw new IllegalArgumentException(msg);
		}

		return aether.event.EventType.Notice.equals(
				notification.getString(EventType));
	}

	/**
	 * Determine if a given Notification is a request.
	 *
	 * @param notif Notification to query
	 * @return <code>true</code> if Notification is a request
	 */
	public static boolean isRequest(Notification notif)
	{
		if (notif == null)
		{
			String msg = "notif can't be null";
			throw new IllegalArgumentException(msg);
		}

		return aether.event.EventType.Request.equals(
				notif.getString(EventType));
	}

	/**
	 * Determine if a given Notification is a response.
	 *
	 * @param notif Notification to query
	 * @return <code>true</code> if Notification is a response
	 */
	public static boolean isResponse(Notification notif)
	{
		if (notif == null)
		{
			String msg = "notif can't be null";
			throw new IllegalArgumentException(msg);
		}

		return aether.event.EventType.Response.equals(
				notif.getString(EventType));
	}
}
