package aether.server.core;

import aether.net.Link;
import aether.net.DefaultLink;
import aether.net.Connection;

import java.beans.beancontext.BeanContextServices;
import java.util.Iterator;
import java.io.IOException;

import org.apache.log4j.Logger;
import net.concedere.dundee.AbstractProvider;

/**
 * Provides container-managed links to components.
 *
 * TODO: add a listener to Links provided that prevent components from closing
 * them
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class LinkProvider extends AbstractProvider
{
	private static final Logger log = Logger.getLogger(LinkProvider.class);

	/**
	 * <code>serviceSelector</code> must be an object of type
	 * LinkProvider.Request.
	 */
	public Object getService(BeanContextServices bcs, Object requestor,
							 Class serviceClass, Object serviceSelector)
	{
		try
		{
			Request params =
					(Request) serviceSelector;
			aether.net.Connection conn = params.getConnection();
			String dest = params.getDestination();

			Link link = new DefaultLink(conn, dest, false);
			link.connect();
			return link;
		}
		catch (IOException ioe)
		{
			log.warn("couldn't open link", ioe);
			return null;
		}
	}

	public Iterator getCurrentServiceSelectors(BeanContextServices bcs,
											   Class serviceClass)
	{
		return null;
	}

	public void releaseService(BeanContextServices bcs, Object requestor,
							   Object service)
	{
		if ((service != null) && (service instanceof Link))
		{
			try
			{
				((Link) service).close();
			}
			catch (IOException ioe)
			{
				log.warn("couldn't close link", ioe);
			}

		}
	}

	/**
	 * Request class that should be passed to this provider.
	 */
	public static class Request
	{
		private Connection connection;
		private String dest;

		public Request(Connection conn, String dest)
		{
			if ((conn == null) || (dest == null))
			{
				String msg = "no parameter can be null";
				throw new IllegalArgumentException(msg);
			}

			this.connection = conn;
			this.dest = dest;
		}

		public Connection getConnection() { return connection; }
		public void setConnection(Connection conn) { this.connection = conn; }
		public String getDestination() { return dest; }
		public void setDestination(String dest) { this.dest = dest; }
	}
}
