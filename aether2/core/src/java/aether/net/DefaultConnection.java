package aether.net;

import org.apache.log4j.Logger;

import java.beans.*;
import java.io.IOException;


/**
 * Basic ability to connect to the event network and publish messages.
 *
 * // TODO: consider firing javabean event everytime a event published
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultConnection extends AbstractConnection
{

	private static final Logger logger =
			Logger.getLogger(DefaultConnection.class);

	/**
	 * Vetoable change support for managing constrained properties
	 */
	protected VetoableChangeSupport vcSupport =
			new VetoableChangeSupport(this);

	/**
	 * PropertyChangeSupport used to support bean properties
	 */
	protected PropertyChangeSupport pcSupport =
			new PropertyChangeSupport(this);

	/**
	 * Constrained property name for the 'open' property
	 */
	public static final String OpenProperty = "open.property";

	/**
	 * Construct a new connection to the Elvin event server.
	 *
	 * @param host host of the event server
	 * @param port port of the event server
	 */
	public DefaultConnection(String host, int port)
	{
		if (host == null)
		{
			String msg = "host can be null";
			throw new IllegalArgumentException(msg);
		}

		this.host = host;
		this.port = port;
	}

	/**
	 * Construct a new DefaultConnection.
	 */
	public DefaultConnection()
	{
		; // do nothing
	}

	public void open() throws IOException
	{
		// ask our veto'ers if we can open the connections
		try
		{
			vcSupport.fireVetoableChange(OpenProperty, Boolean.FALSE,
										 Boolean.TRUE);
		}
		catch (PropertyVetoException e)
		{
			String msg = "open operation was vetoed";
			IOException ioe = new IOException(msg);
			ioe.initCause(e);
			throw ioe;
		}

        super.open();

		// make the change
        pcSupport.firePropertyChange(OpenProperty, Boolean.FALSE,
									 Boolean.TRUE);
	}

	public void close() throws IOException
	{
        // ask our vetoers
		try
		{
			vcSupport.fireVetoableChange(OpenProperty, Boolean.TRUE,
										 Boolean.FALSE);
		}
		catch (PropertyVetoException e)
		{
			String msg = "close operation was vetoed";
			IOException ioe = new IOException(msg);
			ioe.initCause(e);
			throw ioe;
		}

        super.close();

		pcSupport.firePropertyChange(OpenProperty, Boolean.TRUE,
									 Boolean.FALSE);
	}

	/**
	 * Set the host that this event connection will connect to.
	 *
	 * @param host host this connection connects to
	 */
	public void setHost(String host)
	{
		this.host = host;
	}

	/**
	 * Set the port of the event server this connection connects to.
	 *
	 * @param port port that this connection connects to
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	public void addVetoableChangeListener(VetoableChangeListener vcl)
	{
		vcSupport.addVetoableChangeListener(vcl);
	}

	public void addVetoableChangeListener(String name,
										  VetoableChangeListener vcl)
	{
		vcSupport.addVetoableChangeListener(name, vcl);
	}

	public void removeVetoableChangeListener(VetoableChangeListener vcl)
	{
		vcSupport.removeVetoableChangeListener(vcl);
	}

	public void removeVetoableChangeListener(String name,
											 VetoableChangeListener vcl)
	{
		vcSupport.removeVetoableChangeListener(name, vcl);
	}

		public void addPropertyChangeListener(PropertyChangeListener pcl)
	{
		pcSupport.addPropertyChangeListener(pcl);
	}

	public void addPropertyChangeListener(String name,
										  PropertyChangeListener pcl)
	{
		pcSupport.addPropertyChangeListener(name, pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl)
	{
		pcSupport.removePropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(String name,
											 PropertyChangeListener pcl)
	{
		pcSupport.removePropertyChangeListener(name, pcl);
	}

}
