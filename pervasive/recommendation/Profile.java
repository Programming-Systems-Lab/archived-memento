package psl.memento.pervasive.recommendation;

import psl.memento.pervasive.recommendation.exception.GenericException;

/**
 * A User's Profile.
 * This contains information about a User's preferences, etc. in the world of Conversations and psl.memento.pervasive.recommendation.
 */
// TODO profiles should be loaded from xml files
public class Profile {

	// A User should havea  Configuration for a SuggestionEngine associated with him/her
	private SuggestionEngineConfiguration _conversationConfiguration = null;

	/**
	 * Get the SuggestionEngineConfiguration for a User
	 */
	public SuggestionEngineConfiguration getConversationConfiguration()
		throws GenericException {
		if (_conversationConfiguration == null)
			throw new GenericException("There is no SuggestionEngineConfiguration in this Profile");
		return _conversationConfiguration;
	}

	/**
	 * Set the SuggestionEngineCOnfiguration from the path to an xml config file with said configuration
	 */
	public void setConversationConfiguration(String configfile) throws GenericException {
		_conversationConfiguration =
			new SuggestionEngineConfiguration(configfile);
	}
}
