package aether.net;

import aether.event.Request;
import aether.event.Response;

import java.io.IOException;

/**
 * A Link represents an established communication channel between a client
 * component and a server component. Once established, clients can send
 * requests over a link and receive responses.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface Link
{
    /**
	 * Connect to the destination of the link.
	 *
	 * @throws IOException
	 *         if the link can't be established
	 */
	public void connect() throws IOException;

    /**
	 * Close the link.
	 *
	 * @throws IOException
	 *         if the link isn't properly closed
	 */
	public void close() throws IOException;

	/**
	 * Get the unique link id.
	 *
	 * @return unique id of this link
	 */
	public String getLinkId();

	/**
	 * Get the GUID of the component which sent the most recent response
	 * over this link. (It's possible for the component generating responses
	 * to change over the lifetime of the link).
	 *
	 * @return GUID of the component that generated the response. This may be
	 *         <code>null</code> until the first response is received
	 */
	public String getDestination();

    /**
	 * Construct a Request to be sent over this link.
     *
     * @return Request that may be sent over this Link
	 */
	public Request createRequest();

	/**
	 * Send a Request over this link.
	 *
	 * @param Request Request to send
	 * @return Response to the given Request
	 * @throws IOException
	 *         if something goes wrong
	 */
	public Response send(Request Request) throws IOException;
}
