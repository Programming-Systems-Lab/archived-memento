package aether.net;

import aether.event.Event;
import org.elvin.je4.ElvinURL;
import org.elvin.je4.Producer;

import java.io.IOException;

/**
 * Partial implementation of the Connection interface.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class AbstractConnection implements Connection
{
	/**
	 * Host of the event server
	 */
	protected String host;

	/**
	 * Port of the event server
	 */
	protected int port;

	/**
	 * Underlying connection to Elvin
	 */
	protected org.elvin.je4.Connection connection;

	/**
	 * Producer used to publish messages
	 */
	protected Producer producer;

	public synchronized void open() throws IOException
	{
		// only open the connection if we don't already have one
		if (isOpen())
		{
			String msg = "connection already open";
			throw new IllegalStateException(msg);
		}

		ElvinURL url = new ElvinURL("elvin:4.0/tcp,none,xdr/" + host
									+ ":" + port);
		this.connection = new org.elvin.je4.Connection(url);
		this.producer = new Producer(connection);
	}

	public synchronized void close() throws IOException
	{
		if (!isOpen())
		{
			String msg = "connection hasn't been opened yet!";
			throw new IllegalArgumentException(msg);
		}

		connection.close();
		connection = null;
		producer.close();
		producer = null;
	}

	public synchronized void publish(Event msg) throws IOException
	{
		if (msg == null)
		{
			String emsg = "msg can't be null";
			throw new IllegalArgumentException(emsg);
		}

		if (!isOpen())
		{
			String emsg = "connection hasn't been opened yet!";
			throw new IllegalStateException(emsg);
		}

		producer.notify(msg.getNotification());
	}

	public boolean isOpen()
	{
		return connection != null;
	}

	public synchronized org.elvin.je4.Connection elvinConnection()
	{
		if (!isOpen())
		{
			String msg = "connection isn't open";
			throw new IllegalStateException(msg);
		}

		return connection;
	}

	public String getHost()
	{
		return host;
	}

	public int getPort()
	{
		return port;
	}
}
