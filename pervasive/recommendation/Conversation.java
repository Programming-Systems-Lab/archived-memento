package psl.memento.pervasive.recommendation;

import psl.memento.pervasive.recommendation.exception.GenericException;

/**
 * Backend implementation of a conversation between users
 */
public interface Conversation extends EventSupport {

	/**
	 * Starts the conversation (aka users are now allowed to add conversation messages).
	 * This triggers the initial round of recommendations if a StartManager is associated with the
	 * Conversation's SuggestionEngine
	 */
	public void start();

	/**
	 * End the conversation (aka users are no longer allowed to add conversation messages).
	 */
	public void end();

	/**
	 * Add a user to the conversation
	 * @throws GenericException if it was not possible to add the User
	 */
	public void addUser(User u) throws GenericException;

	/**
	 * Register a log to which the conversation's ongoings can be outputed. A ConversationLog is associated with a User
	 * because the only ConversationMessages that should be outputed to a particular log are those that
	 * are fit for the user to whom the log belongs. This should be ensured by seeing a User's credentials in his/her Profile.
	 * @throws GenericException if it was not possible to register this log
	 **/
	public void registerLog(User u, ConversationLog cl)
		throws GenericException;

	/**
	 * Returns whether the conversation has a particular user
	 */
	public boolean hasUser(User u);

	/**
	 * Remove a user from the conversation. That user may no longer add ConversationMessages to the conversation. 
	 * @throws GenericException if it was not possible to remove the User
	 **/
	public void removeUser(User u) throws GenericException;

	/**
	 * Add a ConversationMessage to the conversation. Note that the ConversationMessage has a User associated with it.
	 * @throws GenericException if it was not possible to add a ConversationMessage to the conversation.
	 */
	public void addConversationMessage(ConversationMessage cm)
		throws GenericException;

	/**
	 * Get the generalConversationSuggestionEngine associated with this Conversation. Note that in order to 
	 * have access to the generalConversationSuggestionEngine, one must have a User Profile that gives access
	 * to all the information in the conversation.
	 * @throws GenericException if it was not possible to return a GeneralConversationSuggestionEngine in accordance
	 * with the passed User's Profile.
	 */
	public SuggestionEngine getGeneralConversationSuggestionEngine(User u)
		throws GenericException;
}
