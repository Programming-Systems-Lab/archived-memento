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
public abstract class Event implements Attribute.Event
{
	static
	{
		// don't throw exceptions when values aren't found!
        Notification.throwExceptions(false);
	}

    /**
	 * Underlying Elvin getNotification that'll actually be published.
	 */
	protected Notification notification;

    /**
	 * Construct a new Event containing no data.
	 */
	protected Event()
	{
		this.notification = new Notification();

        // create the unique event id and set the event type
        setEventID(GuidFactory.createId());
        setEventType(getEventType());
	}

    /**
     * Initialize an event structure from an existing Notification.
     *
     * @param notif Notification containing Event data
     * @throws EventException
     *         if <code>notif</code> is malformed
     */
    public void parse(Notification notif) throws EventException
    {
        if (notif == null)
		{
			String msg = "notif can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.notification = notif;
    }

    /**
     * Set the unique event ID.
     *
     * @param id unique event ID
     */
    private void setEventID(String id)
    {
		this.notification.put(EVENT_ID, id);
    }

	/**
	 * Get the unqiue event ID for this event.
	 *
	 * @return unique event id
	 */
	public String getEventId()
	{
		return notification.getString(EVENT_ID);
	}

	/**
	 * Get the time this event was sent.
	 *
	 * @return time this event was sent
	 */
	public long getTime()
	{
		return notification.getLong(TIME);
	}

	/**
	 * Set the time this event was sent.
	 *
	 * @param time time this event was sent
	 */
	public void setTime(long time)
	{
		notification.put(TIME, time);
	}

	/**
	 * Get the GUID of the component that generated this notice.
	 *
	 * @return guid of the component that generated this notice
	 */
	public String getSourceId()
	{
		return notification.getString(SOURCE_ID);
	}

	/**
	 * Set the GUID of the component that generated this notice.
	 *
	 * @param guid GUID of the component that generated this notice
	 */
	public void setSourceId(String guid)
	{
		notification.put(SOURCE_ID, guid);
	}

    /**
     * Get the event type of the Event.
     *
     * @return EVENT_TYPE of the Event
     */
    public abstract String getEventType();

    /**
     * Set the EVENT_TYPE for the event.
     *
     * @param type type of the event
     */
    private void setEventType(String type)
    {
        notification.put(EVENT_TYPE, type);
    }

	/**
	 * Get the underlying Elvin getNotification.
	 *
	 * @return underlying Elvin getNotification
	 */
	public Notification getNotification()
	{
		return notification;
	}

	/**
	 * Determine if an Elvin getNotification contains NOTICE data.
	 *
	 * @param notification  getNotification to query
	 * @return <code>true</code> iff <code>getNotification</code> is a NOTICE
	 */
	public static boolean isNotice(Notification notification)
	{
		if (notification == null)
		{
			String msg = "getNotification can't be null";
			throw new IllegalArgumentException(msg);
		}

		return aether.event.EventType.NOTICE.equals(
				notification.getString(EVENT_TYPE));
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

		return aether.event.EventType.REQUEST.equals(
				notif.getString(EVENT_TYPE));
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

		return aether.event.EventType.RESPONSE.equals(
				notif.getString(EVENT_TYPE));
	}
}
