package aether.event;

import java.util.EventListener;

/**
 * Implemented by objects which can asynchronously process messages.
 * Implementations of this class are assumed to be threadsafe.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public interface EventHandler extends EventListener
{
    /**
	 * Handle the given Event.
	 *
	 * @param msg Event to be handled
	 */
	public void handle(Event msg);
}
