package psl.memento.pervasive.recommendation;

/**
 * Callback mechanism used if a class wants to be notified of Suggestions as opposed to having to poll the system.
 */
public interface SuggestionCallback {
	
	/**
	 * Call used to notify/signal that suggestions are available. This passes a container with the suggestion/suggestions
	 */
	public void signal(SuggestionContainer sc);
}
