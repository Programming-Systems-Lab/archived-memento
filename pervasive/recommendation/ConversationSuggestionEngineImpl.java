package psl.memento.pervasive.recommendation;

import psl.memento.pervasive.recommendation.exception.GenericException;

/**
 * Implementation of a ConversationSuggestionEngine
 * @see psl.memento.pervasive.recommendation.ConversationSuggestionEngine
 */
public class ConversationSuggestionEngineImpl implements ConversationSuggestionEngine {

	private ConversationLog _log = null;

	// SugestionManager handles conversations while the conversation occurs
	private SuggestionManager _suggestionManager = null;
	private Thread _suggestionManagerThread = null;

	// StartManager (currently not supported) handles pre-conversation-starting recommendations, such as recommending a topic ahead of time
	// TODO implement/integrate StartManager
	private StartManager _startManager = null;
	private Thread _startManagerThread = null;

	/**
	 * Constructor
	 */
	public ConversationSuggestionEngineImpl(SuggestionEngineConfiguration cc) {
		_suggestionManager = new SuggestionManagerImpl(cc);
		_startManager = new StartManagerImpl();

		_startManagerThread = new Thread(_startManagerThread);
		_startManagerThread.start();

		_suggestionManagerThread = new Thread(_suggestionManager);
		_suggestionManagerThread.start();
	}

	/**
	 * @see psl.memento.pervasive.recommendation.ConversationSuggestionEngine#registerLog(psl.memento.pervasive.recommendation.ConversationLog)
	 */
	public void registerLog(ConversationLog cc) throws GenericException {
		if (_log != null) {
			throw new GenericException("ConversationSuggestionEngineImpl only supports the registration of one ConversationLog at a time.");
		}
		_log = cc;
		_suggestionManager.registerLog(cc);
	}

	/**
	 * @see psl.memento.pervasive.recommendation.ConversationSuggestionEngine#getSuggestionsSinceLast()
	 */
	public SuggestionContainer getSuggestionsSinceLast() {
		return _suggestionManager.getSuggestionsSinceLast();
	}

	/**
	 * @see psl.memento.pervasive.recommendation.ConversationSuggestionEngine#subscribeToSuggestions(psl.memento.pervasive.recommendation.SuggestionCallback)
	 */
	public void subscribeToSuggestions(SuggestionCallback sc) {
		_suggestionManager.subscribeToSuggestions(sc);
	}

	/**
	 * @see psl.memento.pervasive.recommendation.ConversationSuggestionEngine#unsubscribeToSuggestions(psl.memento.pervasive.recommendation.SuggestionCallback)
	 */
	public void unsubscribeFromSuggestions(SuggestionCallback sc)
		throws GenericException {
		_suggestionManager.unsubscribeToSuggestions(sc);
	}

	/**
	 * @see psl.memento.pervasive.recommendation.ConversationSuggestionEngine#stop()
	 */
	public void stop() {
		_suggestionManager.stop();
	}
}
