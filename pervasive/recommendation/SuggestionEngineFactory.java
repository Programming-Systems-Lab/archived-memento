package psl.memento.pervasive.recommendation;

import psl.memento.pervasive.recommendation.exception.GenericException;

/**
 * A factory to get an appropriate SuggestionEngine. Note that it is required to get a User's Profile in order to get a ConversationEngine.
 * 
 * Also note the difference between getting a SuggestionEngine specific to Conversations and a general SuggestionEngine. 
 * This is meant for compitability with different types of suggestions (on documents for example).
 */
public class SuggestionEngineFactory {

	/**
	 * Load a ConversationSuggestionEngine from a User's Profile 
	 * @param _profile
	 * @return ConversationSuggestionEngine
	 */
	public static ConversationSuggestionEngine loadConversationSEFromProfile(Profile _profile)
		throws GenericException {

		// TODO We are always loading a ConversationSuggestionEngineImpl here, as opposed to looking in a Profile
		// for the appropriate extengion of COnversationSuggestionEngine to load.
		SuggestionEngineConfiguration cc;
		cc = _profile.getConversationConfiguration();
		return new ConversationSuggestionEngineImpl(cc);
	}
	/**
	 * Load a SuggestionEngine from a User's Profile
	 * @param _profile
	 * @return SuggestionEngine
	 */
	public static SuggestionEngine loadSEFromProfile(Profile _profile) throws GenericException {
		
		// TODO We don't provide any other SuggestionEngine than the ConversationSuggestionEngine loaded in the above call
		// we should really get the specific extension from the User's Profile.
		return loadConversationSEFromProfile(_profile);
	}
}
