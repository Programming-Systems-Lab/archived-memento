package psl.memento.pervasive.recommendation;

/**
 * Call back mechanism for events. Provides a callback for Events that occur in other components
 */
public interface EventCallback {
	
	/**
	 * Called whenever an event registered via this callback occurs.
	 */
	public void notify(Event e);
}
