package aether.event;

/**
 * Interface that defines the different event type constants.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface EventType
{
	/**
	 * Indicates that an Event is a request.
	 */
	public static final String REQUEST = "aether:request";

	/**
	 * Indicates that an Event is a response.
	 */
	public static final String RESPONSE = "aether:response";

	/**
	 * Indicates that an Event is a notice.
	 */
	public static final String NOTICE = "aether:notice";
}
