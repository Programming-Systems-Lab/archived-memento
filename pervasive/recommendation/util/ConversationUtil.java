package psl.memento.pervasive.recommendation.util;

import psl.memento.pervasive.recommendation.Conversation;
import psl.memento.pervasive.recommendation.ConversationImpl;
import psl.memento.pervasive.recommendation.User;
import psl.memento.pervasive.recommendation.exception.GenericException;

/**
 * This class is a util class for the creation and manipulation of Conversations. More specifically it enforces some of the
 * call dependencies between objects such as User and Conversation.
 */
public class ConversationUtil {

	/** Do not allow the creation of ConversationUtil objects */
	private ConversationUtil() {
	}

	/**
	 * Creates a Conversation from the SuggestionEngineConfiguration in User's Profile. Note that this does not add u to the 
	 * conversation.
	 * @param u the User whose SuggestionEngineConfiguration should be used to create a Conversation
	 * @return the Conversation
	 * @throws Exception in case the Conversation cannot be started such as if we are unable to create a SuggestionEngine
	 */
	public static Conversation createConversation(User u) throws GenericException {
		// default to ConversationImpl
		// TODO we should consider getting the ConversationImpl from a User's SuggestionEngineConfiguration in the future
		Conversation conversation = new ConversationImpl(u);
		return conversation;
	}

	/**
	 * Add a user to a Conversation
	 * @param c the Conversation
	 * @param u the User
	 * @throws Exception gets thrown if the User is already in the Conversation or in case the User is unable to join the Conversation 
	 * for other causes such as not being able to load a SuggestionEngine
	 */
	public static void addUser(Conversation c, User u) throws GenericException {
		c.addUser(u);
		u.addConversation(c);
	}
}
