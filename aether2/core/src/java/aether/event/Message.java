package aether.event;



/**
 * Indicates an Event that has a specific destination. Message events are used
 * to allow point-to-point communication over the underlying event network.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public abstract class Message extends Event implements Attribute.Message
{
    /**
	 * Get the link ID of this Message.
	 *
	 * @return link ID of this Message
	 */
	public String getLink()
	{
		return notification.getString(LINK_ID);
	}

	/**
	 * Set the link ID of this Message.
	 *
	 * @param link ID of this Message
	 */
	public void setLink(String link)
	{
		notification.put(LINK_ID, link);
	}
}
