package aether.net;

import aether.event.Event;

import java.io.IOException;

/**
 * Allows a component within the network to publish events.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface Publisher
{
    /**
	 * Publish a event on the network.
	 *
	 * @param msg Event to publish
	 * @throws IOException
	 *         if something goes wrong
	 */
	public void publish(Event msg) throws IOException;
}
