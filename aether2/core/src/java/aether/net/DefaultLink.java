package aether.net;

import java.io.IOException;

/**
 * A DefaultLink that is also a proper javabean.
 *
 * // TODO: add a isConnected property
 * // TODO: make the connect property a Constrained property
 * // TODO: consider generating bean event every time request sent
 * // TODO: consider generating bean event every time response received
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultLink extends AbstractLink
{
	private boolean closeConn = true;

    /**
	 * Construct a new DefaultLink to a given responder on the Aether network.
	 *
	 * @param host host of the event server to use
	 * @param port port of the event server to use
	 * @param dest Destination to send requests to
	 */
	public DefaultLink(String host, int port, String dest)
	{
		this.connection = new DefaultConnection(host, port);
		this.destination = dest;
	}

	/**
	 * Construct a DefaultLink from an existing connection.
	 *
	 * @param conn      existing Connection to the network
	 * @param dest      destination of the link
	 * @param closeConn whether the connection will be closed when the link
	 *                  is closed
	 */
	public DefaultLink(Connection conn, String dest, boolean closeConn)
	{
		this.connection = conn;
		this.destination = dest;
		this.closeConn = closeConn;
	}

	/**
	 * Default constructor.
	 */
	public DefaultLink()
	{
		; // do nothing
	}

    /**
	 * Set the Connection to be used by the link.
	 *
	 * @param conn Connection to be used by the link
	 */
	public void setConnection(Connection conn)
	{
		if (conn == null)
		{
			String msg = "conn can't be null";
			throw new IllegalArgumentException(msg);
		}

		if (!closed)
		{
			String msg = "link is already open";
			throw new IllegalStateException(msg);
		}

        this.connection = conn;
	}

	/**
	 * Set the destination of this link.
	 *
	 * @param dest destination of the link
	 */
	public void setDestination(String dest)
	{
		if (dest == null)
		{
			String msg = "dest can't be null";
			throw new IllegalArgumentException(msg);
		}

		if (!closed)
		{
            String msg = "link is open";
			throw new IllegalStateException(msg);
		}

        this.destination = dest;
	}

	public void close() throws IOException
	{
        super.close();

		if (closeConn)
		{
			connection.close();
			connection = null;
		}
	}

}
