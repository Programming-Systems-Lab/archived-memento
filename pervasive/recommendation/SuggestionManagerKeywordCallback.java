package psl.memento.pervasive.recommendation;

/**
 * Callback used to let the SuggestionManager know of Keywords discovered by KFs.
 */
public interface SuggestionManagerKeywordCallback {
	
	/**
	 * Notify/Signal the class implementing the callback of Keywords
	 */
	public void signal(KeywordContainer kc);
}
