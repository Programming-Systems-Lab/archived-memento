package aether.server.core;

import net.concedere.dundee.DefaultComponent;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.framework.Disposable;
import net.concedere.dundee.framework.Startable;
import net.concedere.dundee.framework.Initializable;
import aether.net.Connection;

import java.beans.beancontext.BeanContextServiceProvider;
import java.beans.beancontext.BeanContextServices;
import java.io.IOException;
import java.util.Iterator;

/**
 * Provides the default connection to other components in the Aether container.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultConnectionProvider extends DefaultComponent
		implements Initializable, Startable, Disposable,
		BeanContextServiceProvider
{
    private Connection connection;
    private BeanContextServiceProvider spi;

	public void initialize() throws ComponentException
	{
        try
		{
			connection.open();
		}
		catch (IOException ioe)
		{
			String msg = "couldn't open default connection";
			throw new ComponentException(msg, ioe);
		}
	}

	public void dispose() throws ComponentException
	{
		try
		{
        	connection.close();
		}
		catch (IOException ioe)
		{
			String msg = "coouldn't close default connection";
			throw new ComponentException(msg, ioe);
		}
	}

	public void start() throws ComponentException
	{
        if (! getContainer().addService(Connection.class, this))
		{
			String msg = "couldn't register to provide default connection";
			throw new ComponentException(msg);
		}
	}

	public void stop() throws ComponentException
	{
		getContainer().revokeService(Connection.class, this, false);
	}

	public Iterator getCurrentServiceSelectors(BeanContextServices bcs,
											   Class serviceClass)
	{
		return null;
	}

	public Object getService(BeanContextServices bcs, Object requestor,
							 Class serviceClass, Object serviceSelector)
	{
		return connection;
	}

	public void releaseService(BeanContextServices bcs, Object requestor,
							   Object service)
	{
		; // do nothing
	}

	/**
	 * Get the default connection.
	 *
	 * @return default connection
	 */
	public Connection getDefaultConnection()
	{
		return connection;
	}

	/**
	 * Set the default connection.
	 *
	 * @param conn default connection
	 */
	public void setDefaultConnection(Connection conn)
	{
		this.connection = conn;
	}

}
