package psl.memento.pervasive.recommendation;

/**
 * This is the interface for an algorithm responsible for finding suggestions from keywords/text analysis
 */
public interface Search extends Runnable {

	/**
	 * This signals the analysis algorithm for the most recent suggestions. 
	 */
	public void signal(SuggestionContainer sc);

	/**
	 * init and start the underlying analysis algorithm (though make sure to invoke the Runanble interface via a Thread's start() call as well)
	 * 
	 * @param sconf is a specific configuration for an implementation of a Search, based on an xml document 
	 */
	public void init(SearchConfiguration sconf);

	/**
	 * Stop this search. Note that after stopping, the object cannot be restarted (init) or reset. Essentially readies
	 * this algorithm for garbage collection.
	 */
	public void stop();

	/**
	 * @param kc a KeywordContainers of keywords that are relevant to a search that should be executed
	 */
	public void push(KeywordContainer kc);
	
	/**
	 * Signal the implementor of this interface (most likely a Search algorithm) that there is feedback for a particular suggestion.
	 */
	void signal(Feedback f, Suggestion suggestion);
}
