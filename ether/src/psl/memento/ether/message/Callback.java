package psl.memento.ether.message;

/**
 * A lot of the time, components need to do synchronous messaging rather than
 * just one-way messaging. Synchronous messaging isn't supported directly, but
 * the messaging system does allow a basic callback mechanism using
 * implementations of this interface.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public interface Callback extends MessageHandler
{
	/**
	 * If the transaction times out because a response is not generated in
	 * enough time, this method will be called. The timeout for message
	 * transactions is determined by the server.
	 */
	public void handleTimeout();
}
