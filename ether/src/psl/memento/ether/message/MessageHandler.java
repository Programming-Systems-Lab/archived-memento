package psl.memento.ether.message;

/**
 * Represents an object capable of handling messages.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public interface MessageHandler
{
	/**
	 * Process an incoming Message.
	 *
	 * @param msg incoming message which has been received
	 */
	public void handleMessage(Message msg);
}
