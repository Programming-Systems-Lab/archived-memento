package aether.event;

import org.elvin.je4.Notification;
import org.elvin.je4.Subscription;

/**
 * Indicates an Event that has a specific destination.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public abstract class Message extends Event implements Attribute.Message
{
	/**
	 * Default ctor.
	 */
	protected Message()
	{
		super();
	}

	/**
	 * Construct a new Message from an existing Notification.
	 *
	 * @param notif Notification containing Notification data
	 * @throws EventException
	 *         if something goes wrong
	 */
	protected Message(Notification notif) throws EventException
	{
		super(notif);
	}

    /**
	 * Get the link ID of this Message.
	 *
	 * @return link ID of this Message
	 */
	public String getLink()
	{
		return notification.getString(LinkId);
	}

	/**
	 * Set the link ID of this Message.
	 *
	 * @param link ID of this Message
	 */
	public void setLink(String link)
	{
		notification.put(LinkId, link);
	}

	/**
	 * Get the destination of the message. Depending on who sent it and how,
	 * this may either be an aether URL (eg aether://cs.columbia.edu/master)
	 * or it might be the GUID of a component.
	 *
	 * @return destination of the message
	 */
	public String getDestination()
	{
		return notification.getString(Destination);
	}

	/**
	 * Set of the destination of this message.
	 *
	 * @param dest the destination of this message
	 */
	public void setDestination(String dest)
	{
		notification.put(Destination, dest);
	}

	/**
	 * Construct a Subscription indicating that a component wants to receive
	 * messages sent to a certain destination.
	 *
	 * @param dest Destination to subscribe to
	 */
	public static Subscription createSubForDestination(String dest)
	{
		if (dest == null)
		{
			String msg = "dest can't be null";
			throw new IllegalArgumentException(msg);
		}

		// some destinations may be regexable URLS!
   		String expr = "regex(" + Attribute.Message.Destination + ", " +
				   " \"" + dest + "\")";
		return new Subscription(expr);
	}
}
