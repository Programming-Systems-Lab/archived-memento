package aether.server;

import aether.net.Connection;
import aether.net.DefaultConnection;
import aether.net.Publisher;

import java.beans.beancontext.BeanContextServices;
import java.beans.beancontext.BeanContextServiceProvider;
import java.util.*;
import java.io.IOException;

import org.apache.log4j.Logger;
import net.concedere.dundee.AbstractProvider;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.DefaultComponent;
import net.concedere.dundee.framework.Startable;
import net.concedere.dundee.framework.Initializable;
import net.concedere.dundee.framework.Disposable;

/**
 * Provides Connection objects to components in the container. This class can
 * either construct new Connection objects  or can be used to retrieve a
 * special 'default' Connection that is available to all components in the
 * container.
 * <p />
 * When added to a Container this component will register itself as a service
 * provider for the 'Connection' service.
 *
 * TODO: add PropertyChangeListener to connections returned to make sure
 * they don't get closed by vetoing the close operation unless we do it
 *
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class ConnectionFactory extends DefaultComponent
		implements Initializable, Disposable, Startable
{
	private Connection defaultConnection;
	private boolean serviceProvider = true;
	private BeanContextServiceProvider spi;

	private static Logger log = Logger.getLogger(ConnectionFactory.class);

	/**
	 * Determine whether this factory is also a Service Provider for the
	 * 'Connection' class.
	 *
	 * @return true iff this factory is also a service provider
	 */
	public boolean isServiceProvider()
	{
		return serviceProvider;
	}

	/**
	 * Set whether this factory is also a connection factory should also
	 * register itself as a service provider for 'Connection' objects.
	 *
	 * @param provide  whether this factory will provide services
	 */
	public void setServiceProvider(boolean provide)
	{
		serviceProvider = provide;
	}

	public void initialize() throws ComponentException
	{
		if (serviceProvider)
		{
			spi = new Provider();
			if (! getContainer().addService(Connection.class, spi))
			{
				String msg = "couldn't register as Connection Service " +
						"Provider";
				throw new ComponentException(msg);
			}

			if (! getContainer().addService(Publisher.class, spi))
			{
				String msg = "couldn't register as SP for Publisher class";
				throw new ComponentException(msg);
			}
		}
	}

	public void dispose() throws ComponentException
	{
		if (serviceProvider)
		{
			getContainer().revokeService(Connection.class, spi, true);
			getContainer().revokeService(Publisher.class, spi, true);
		}
	}

	/**
	 * Get the default connection used by this factory.
	 *
	 * @return default connection for this factory
	 */
	public Connection getDefaultConnection()
	{
		return defaultConnection;
	}

	/**
	 * Set the default connection for this factory.
	 *
	 * @param con new default connection for this factory
	 */
	public void setDefaultConnection(Connection con)
	{
		this.defaultConnection = con;
	}

	public void start() throws ComponentException
	{
		// open the default connection if we created one
		try
		{
			if (defaultConnection != null)
			{
				defaultConnection.open();
			}
		}
		catch (IOException ioe)
		{
			String msg = "failed to open default connection";
			throw new ComponentException(msg, ioe);
		}
	}

	public void stop() throws ComponentException
	{
		try
		{
			if (defaultConnection != null)
			{
				defaultConnection.close();
			}
		}
		catch (IOException ioe)
		{
			String msg = "failed to close default connection";
			throw new ComponentException(msg, ioe);
		}
	}

	/**
	 * The Request object that this Provider must receive.
	 */
	public static class Request
	{

		private String host;
		private int port;

		public Request(String host, int port)
		{
			if (host == null || port < 0)
			{
				String msg = "illegal request";
				throw new IllegalArgumentException(msg);
			}

			this.host = host;
			this.port = port;
		}

		public String getHost() { return host; }
		public void setHost(String host) { this.host = host; }
		public int getPort() { return port; }
		public void setPort(int port) { this.port = port; }
	}

	/**
	 * BeanContextServiceProvider implementation used by this class to act as a
	 * service provider for Connection and Publisher classes.
	 *
	 * @author Buko O. (buko@cs.columbia.edu)
	 * @version 0.1
	 */
	public class Provider extends AbstractProvider
	{

		/**
		 * If <code>serviceSelector</code> is <code>null</code> the 'default
		 * connection' for the container will be returned, otherwise the
		 * serviceSelector must be an object of type ConnectionFactory.Request.
		 * <p />
		 * Note that Connections provided by this class <em>belong to the
		 * container</em>. Clients <em>must not</em> open() or close() them
		 * instead they should simply use them and then release them.
		 *
		 * @return Connection object requested or <code>null</code>
		 */
		public Object getService(BeanContextServices bcs, Object requestor,
								 Class serviceClass, Object serviceSelector)
		{
			if (serviceSelector == null)
			{
				return defaultConnection;
			}
			else
			{
				try
				{
					Request params = (Request) serviceSelector;

					// enforce arguments
					String host = params.getHost();
					int port = params.getPort();


					Connection conn = new DefaultConnection(host, port);
					conn.open();
					return conn;
				}
				catch (Exception e)
				{
					log.warn("failed to construct Connection object due to "
							 + e);
					return null;
				}
			}
		}

		public void releaseService(BeanContextServices bcs, Object requestor,
								   Object service)
		{
			// if it's not the default service then we close it
			if (service != defaultConnection)
			{
				try
				{
					((Connection) service).close();
				}
				catch (IOException ioe)
				{
					log.warn("failed to close connection", ioe);
				}
			}
		}

		public Iterator getCurrentServiceSelectors(BeanContextServices bcs,
												   Class serviceClass)
		{
			return null;
		}
	}
}
