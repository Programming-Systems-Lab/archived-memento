package psl.memento.pervasive.recommendation;

/**
 * Callback used to let the SuggestionManager know of Suggestions
 */
public interface SuggestionManagerSuggestionCallback {
	
	/**
	 * Notify/Signal the class implementing the callback of Suggestions
	 */
	public void signal(SuggestionContainer sc);
}
