package psl.memento.pervasive.recommendation;

import psl.memento.pervasive.recommendation.exception.GenericException;

/**
 * Interface for a SuggestionEngine used for Conversations Provides a very non-specialized interface
 */
public interface ConversationSuggestionEngine extends SuggestionEngine {
	
	/**
	 * Register a log which contains the Conversation's content. Note that only one log can be registered per conversation engine.
	 * As soon as a log is registered, suggestions become available.
	 */
	void registerLog(ConversationLog cc) throws GenericException;
	
	/**
	 * This returns all suggestions since the last time this method was called. (polling)
	 */
	public SuggestionContainer getSuggestionsSinceLast();
	
	/**
	 * Push-style mechanism for getting suggestions (as opposed to polling).
	 */
	public void subscribeToSuggestions(SuggestionCallback sc);
	
	/**
	 * Unregister a suggestioncallback
	 */
	public void unsubscribeFromSuggestions(SuggestionCallback sc) throws GenericException;
	
	/**
	 * Stop this ConversationSuggestionEngine from operating (aka get ir ready for garbage collection). 
	 * Suggestions are no longer available at this point.
	 * 
	 * We aren't sure if this works
	 */
	public void stop();
}
