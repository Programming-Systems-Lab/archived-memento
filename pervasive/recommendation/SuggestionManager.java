package psl.memento.pervasive.recommendation;

import psl.memento.pervasive.recommendation.exception.GenericException;

/**
 * This manages suggestions for an ongoing conversation. 
 * It managaes KeywordFinders and Searches, and passes data between them (ConversationLogs for the Conversations, Keywords from the KFs, 
 * and Suggestions from the Searches). 
 */
public interface SuggestionManager extends Runnable {

	/**
	 * Unregister a log from which the SuggestionManager gets the Conversation's content
	 */
	void unregisterLog(ConversationLog cc) throws GenericException;

	/**
	 * Register a log from which the Suggestionmanager get sthe COnversation's content. This is then how the KeywordFinders
	 * have access to the ongoing conversation.
	 */
	void registerLog(ConversationLog cc) throws GenericException;

	/**
	 * This returns all suggestions since the last time this method was called. (polling)
	 */
	public SuggestionContainer getSuggestionsSinceLast();

	/**
	 * Push/subscriptions style mechanism for getting suggestions.
	 */
	public void subscribeToSuggestions(SuggestionCallback sc);

	/**
	 * Unregister a callback
	 */
	public void unsubscribeToSuggestions(SuggestionCallback sc)
		throws GenericException;

	/**
	 * Get a History of Keywords and Suggestions handled by this SuggestionManager
	 */
	public History getSuggestionManagerHistory();

	/**
	 * Stops all KeywordFinders and Searches managed by this SuggestionManager.
	 */
	public void stop();
}
