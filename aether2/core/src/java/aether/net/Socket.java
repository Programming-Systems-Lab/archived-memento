package aether.net;

import aether.event.Request;
import aether.event.Response;

import java.io.IOException;

/**
 * A Socket represents a stateful, synchronous communication channel between
 * two components.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface Socket
{
    /**
     * Default timeout value of 30 seconds.
     */
    public static final long DEFAULT_TIMEOUT = 1000 * 30;

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
	public void disconnect() throws IOException;

    /**
     * Determine if the link is currently connected.
     *
     * @return <code>true</code> if the link is currently connected else
     *         <code>false</code>
     */
    public boolean isConnected();

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
     * Set the max number of milliseconds that the <code>send</code> method
     * may block while waiting for a response.
     *
     * @param timeout timeout to wait for a response
     */
    public void setTimeOut(long timeout);

    /**
     * Get the timeout time.
     *
     * @return timeout time
     */
    public long getTimeOut();

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
