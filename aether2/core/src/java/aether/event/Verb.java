package aether.event;

/**
 * Defines the accepted verbs within the Aether request/response system.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface Verb
{
	/**
	 * An HEAD request issued against a Resource.
	 */
	public static final String Head = "HEAD";

	/**
	 * A GET request issued against a Resource.
	 */
	public static final String Get = "GET";

	/**
	 * A PUT request issued against a Resource.
	 */
	public static final String Put = "PUT";

    /**
	 * A DELETE request.
	 */
	public static final String Delete = "DELETE";

	/**
	 * An OPITIONS request.
	 */
	public static final String Options = "OPTIONS";

	/**
	 * A TRACE request.
	 */
	public static final String Trace = "TRACE";
}
