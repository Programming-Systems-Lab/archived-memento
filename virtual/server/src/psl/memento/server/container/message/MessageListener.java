package psl.memento.server.container.message;

/**
 * Represents an object capable of handling incoming point-to-point messages.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public interface MessageListener
{
	/**
	 * Process a Message which has been receieved through the point-to-point
	 * messaging system.
	 * 
	 * @param msg incoming message which has been receieved
	 **/
	public void onMessage(Message msg);
}
