package psl.memento.ether.event;

/**
 * Represents an object which wishes to be notified of events.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public interface EventHandler
{
	/**
	 * Handle an incoming Event which the handler has expressed interest in
	 * receiving.
	 *
	 * @param event event to handle
	 **/
	public void handleEvent(Event event);

}
