package aether.event;

/**
 * Interface that defines the different event types.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface EventType
{
	/**
	 * Indicates that an Event is a request.
	 */
	public static final String Request = "aether:request";

	/**
	 * Indicates that an Event is a response.
	 */
	public static final String Response = "aether:response";

	/**
	 * Indicates that an Event is a notice.
	 */
	public static final String Notice = "aether:notice";
}
