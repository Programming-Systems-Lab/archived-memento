package aether.net;

import java.io.IOException;

/**
 * Defines an object that allows components in the network to publish
 * messages.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public interface Connection extends Publisher
{
	/**
	 * Open the connection to the underlying server.
	 *
	 * @throws IOException
	 *         if something goes wrong
	 */
	public void open() throws IOException;

	/**
	 * Close the connection.
	 *
	 * @throws IOException
	 *         if the connection can't close completely
	 */
	public void close() throws IOException;

	/**
	 * Determine if a connection is already open.
	 *
	 * @return <code>true</code> iff a connection is already open
	 */
	public boolean isOpen();

	/**
	 * Get the host of the event server this connection connects to.
	 *
	 * @return host this connection is connected to
	 */
	public String getHost();

	/**
	 * Get the portof the event server that this connection connects to.
	 *
	 * @return port of the event server connected to
	 */
	public int getPort();

	/**
	 * Get the underlying Connection object to the Elvin server.
	 *
	 * @return Connection object to the Elvin server
	 */
	public org.elvin.je4.Connection elvinConnection();
}
