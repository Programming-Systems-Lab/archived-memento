package aether.net;

import aether.event.EventHandler;
import aether.event.Notice;

import java.io.IOException;

/**
 * A MulticastSocket provides the means to work with general topics (one-to-many
 * channels) within the event network. Using a MulticastSocket it's possible to
 * subscribe to topics and broadcast Notice objects over a topic.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface MulticastSocket
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
     * Broadcast a Notice over a topic.
     *
     * @param notice notice to broadcast
     * @param topic  topic to broadcast it over
     * @throws IOException
     *         if broadcast fails
     */
    public void broadcast(Notice notice, String topic) throws IOException;

	/**
	 * Add a listener to the MulticastSocket. Whenever a MulticastSocket
     * notices an event it'll be passed to the given listener.
	 *
	 * @param listener listener to handle incoming events
	 */
	public void addEventHandler(EventHandler listener);

	/**
	 * Remove a listener from the monitor.
	 *
	 * @param listener listener to unsubscribe
	 */
	public void removeEventHandler(EventHandler listener);

	/**
	 * Determine if this MulticastSocket is open.
	 *
	 * @return <code>true</code> if this MulticastSocket is open
	 */
	public boolean isOpen();

    /**
	 * Open the MulticastSocket and allocate any necessary resources.
	 *
	 * @throws IOException
	 *         if the monitor fails to open
	 */
	public void open() throws IOException;

	/**
	 * Close the MulticastSocket and ignore all further notices.
	 *
	 * @throws IOException
	 *         if the monitor doesn't close successfully
	 */
	public void close() throws IOException;
}
