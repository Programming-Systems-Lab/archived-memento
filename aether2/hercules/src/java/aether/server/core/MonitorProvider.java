package aether.server.core;

import aether.net.Connection;
import aether.net.DefaultMonitor;
import aether.net.Monitor;

import java.util.Iterator;
import java.beans.beancontext.BeanContextServices;
import java.io.IOException;

import org.apache.log4j.Logger;
import net.concedere.dundee.AbstractProvider;

/**
 * Provides container-owned Monitor to components within the container.
 *
 * TODO: add a listener to returned monitors that prevents components from
 * closing it
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class MonitorProvider extends AbstractProvider
{

	private static final Logger log = Logger.getLogger(MonitorProvider.class);

	public void releaseService(BeanContextServices bcs, Object requestor,
							   Object service)
	{
		if ((service != null) && (service instanceof Monitor))
		{
			try
			{
				((Monitor) service).close();
			}
			catch (IOException ioe)
			{
				log.warn("failed to close returned monitor", ioe);
			}
		}
	}

	/**
	 * Construct a new Monitor. <code>serviceSelector</code> must be a valid
	 * Connection. Once the Monitor is retrieved, users must contextualize it
	 * by assigning it a valid EventHandler.
	 */
	public Object getService(BeanContextServices bcs, Object requestor,
							 Class serviceClass, Object serviceSelector)
	{
		// if somebody fails to get a connection, give them back nothing
		if (serviceSelector == null) return null;

		try
		{
			Connection connection = (Connection) serviceSelector;
			Monitor monitor = new DefaultMonitor(connection, false);
			monitor.open();
			return monitor;
		}
		catch (IOException ioe)
		{
			log.warn("couldn't open new monitor", ioe);
			return null;
		}
	}

	public Iterator getCurrentServiceSelectors(BeanContextServices bcs,
											   Class serviceClass)
	{
		return null;
	}
}
