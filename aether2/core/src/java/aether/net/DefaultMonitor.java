package aether.net;

import java.beans.*;
import java.io.IOException;

/**
 * Provides a default implementation of the {@link Monitor} interface which
 * is also a bean.
 *
 * // TODO: consider firing javabean event everytime a Monitor recieves an event
 * // TODO: consider firing javabean event when begins/stops monitoring a guid
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultMonitor extends AbstractMonitor
{
	/**
	 * True iff the Connection should be closed when the monitor is
	 * closed.
	 */
	protected boolean closeConn = true;

	/**
	 * PropertyChangeSupport used to support bean properties
	 */
	protected PropertyChangeSupport pcSupport =
			new PropertyChangeSupport(this);

	/**
	 * VetoableChangeSupport used to support bean properties
	 */
	protected VetoableChangeSupport vcSupport =
			new VetoableChangeSupport(this);

	/**
	 * Constrained property name for monitor 'open'.
	 */
	public static final String OpenProperty = "property.open";

	/**
	 * Bound property for 'messageConnection' property
	 */
	public static final String ConnectionProperty =
			"property.connection";

	/**
	 * Construct a new Monitor to monitor resources.
	 *
	 * @param eventHost host of the underlying event server
	 * @param eventPort port of the underlying event server
	 */
	public DefaultMonitor(String eventHost, int eventPort)
	{
		if (eventHost == null)
		{
			String msg = "eventHost can't be null";
			throw new IllegalArgumentException(msg);
		}

    	this.connection = new DefaultConnection(eventHost, eventPort);
	}

	/**
	 * Construct a new Monitor from an existing Connection that
	 * must already be open.
	 *
	 * @param msgConn   Existing, open Connection
	 * @param closeConn True if the given connection should be closed when
	 *                  the monitor is closed
	 */
	public DefaultMonitor(Connection msgConn, boolean closeConn)
	{
        if (msgConn == null)
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

		this.connection = msgConn;
		this.closeConn = closeConn;
	}

	/**
	 * Construct a new DefaultMonitor.
	 */
	public DefaultMonitor()
	{
		; // do nothing
	}

	/**
	 * Get whether the DefaultMonitor will close the Connection.
	 *
	 * @return <code>true</code> if the Connection will be closed when
	 *         the DefaultMonitor is closed
	 */
	public boolean getCloseConnection()
	{
		return closeConn;
	}

	/**
	 * Set whether the DefaultMonitor should close the Connection when
	 * it itself is closed.
	 *
	 * @param closeConn close connection status
	 */
	public void setCloseConnection(boolean closeConn)
	{
		this.closeConn = closeConn;
	}

	/**
	 * Open the Monitor and allocate any necessary resources.
	 *
	 * @throws IOException
	 *         if the monitor fails to open
	 */
	public void open() throws IOException
	{
		// ask our listeners if it's ok for us to make this change
		try
		{
			vcSupport.fireVetoableChange(OpenProperty,
										 Boolean.FALSE, Boolean.TRUE);
		}
		catch (PropertyVetoException e)
		{
			String msg = "open property change vetoed";
			IOException ioe = new IOException(msg);
			ioe.initCause(e);
			throw ioe;
		}

		// now make the change!
		super.open();

		// now tell our property listeners the change succeeded
        pcSupport.firePropertyChange(OpenProperty, Boolean.FALSE,
									 Boolean.TRUE);
	}

	/**
	 * Close the Monitor and ignore all further notices.
	 *
	 * @throws IOException
	 *         if the monitor fails to close totally
	 */
	public void close() throws IOException
	{
		// ask our veto'ers if it's ok to close this monitor
		try
		{
			vcSupport.fireVetoableChange(OpenProperty,
										 Boolean.TRUE, Boolean.FALSE);
		}
		catch (PropertyVetoException e)
		{
			String msg = "close operation was vetoed";
            IOException ioe = new IOException(msg);
			ioe.initCause(e);
			throw ioe;
		}

		// completely override the close() logic here in order to only actually
		// close the connection if we should--we might be sharing it so we've
		// got to check

		synchronized (this)
		{
			clearSubscriptions();
			if (closeConn)
			{
				connection.close();
			}
			connection = null;
		}

		// notify our listeners
		pcSupport.firePropertyChange(OpenProperty, Boolean.TRUE,
									 Boolean.FALSE);
	}

	public void setConnection(Connection msgConn)
	{
		Object oldConn = null;

		synchronized (this)
		{
			oldConn = getConnection();
			super.setConnection(msgConn);
		}

		pcSupport.firePropertyChange(ConnectionProperty, oldConn,
									 msgConn);
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
}
