package aether.net;

import aether.event.EventHandler;

import java.io.IOException;

/**
 * Defines an object capable of monitoring a set of resources in the Aether
 * network.
 *
 * TODO: need a special MonitorListener because listeners should know if a
 * monitor is opening or closing?
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface Monitor
{
	/**
	 * Subscribe to a topic in the Aether. Any Notices broadcast to the topic
	 * will be received.
	 *
	 * @param topic topic to subscribe to
	 * @throws IOException
	 *         if the subscription fails
	 */
	public void subscribe(String topic) throws IOException;

    /**
	 * Unsubscribe from a topic in the Aether.
	 *
	 * @param topic name of the topic
	 * @throws IOException
	 *         if the unsubscription fails
	 */
	public void unsubscribe(String topic) throws IOException;

	/**
	 * Get the underlying Connection used by this Monitor to connect
	 * to the event network.
	 *
	 * @return Connection used by this monitor to connect to the network
	 */
	public Connection getConnection();

	/**
	 * Set the underlying Connection used by this Monitor to connect
	 * to the event network.
	 *
	 * @param msgConn Connection to connect to the event network
	 */
  	public void setConnection(Connection msgConn);

	/**
	 * Add a listener to the Monitor. Whenever a Monitor notices an event it'll
	 * be passed to the given listener.
	 *
	 * @param listener listener to handle incoming events
	 */
	public void addNoticeListener(EventHandler listener);

	/**
	 * Remove a listener from the monitor.
	 *
	 * @param listener listener to unsubscribe
	 */
	public void removeNoticeListener(EventHandler listener);

	/**
	 * Determine if this Monitor is open.
	 *
	 * @return <code>true</code> if this Monitor is open
	 */
	public boolean isOpen();

    /**
	 * Open the Monitor and allocate any necessary resources.
	 *
	 * @throws IOException
	 *         if the monitor fails to open
	 */
	public void open() throws IOException;

	/**
	 * Close the Monitor and ignore all further notices.
	 *
	 * @throws IOException
	 *         if the monitor doesn't close successfully
	 */
	public void close() throws IOException;
}
