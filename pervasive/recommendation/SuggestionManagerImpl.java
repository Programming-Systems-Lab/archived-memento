package psl.memento.pervasive.recommendation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import psl.memento.pervasive.recommendation.exception.GenericException;

/**
 * Simple implementation of the Suggestionmanager that only suports one log. 
 * All of its KeywordFinders look at this log, and all of their Keywords are "forwarded" to all the Search algorithms.
 * 
 * Eventually we want to look at more complicated implementations where some KeywordFinders are associated with 
 * some Search algorithms for specific results (aka results from KFs A and B go to Search S, whle results from KF C go to Search T, etc.)
 */
// TODO put all constants inside the Configurations so that they are easy to adjust
public class SuggestionManagerImpl
	implements
		SuggestionManager,
		SuggestionManagerKeywordCallback,
		SuggestionManagerSuggestionCallback,
		FeedbackCallback {

	// How long to wait before signalling KFs and Searches
	private final int _SIGNAL_WAIT = 10000;

	// How long to wait for a ConversationLog before getting into the main part of the run() thread
	private final int _START_WAIT = 1000;

	// Keep track of recent suggestions (used as part of the getSuggestionsSinceLast() call)
	private Object _suggestionsSync;
	private SuggestionContainer _suggestions;

	// Keyword finding algorithms
	private ArrayList _keywordFinders;

	// the log from which we get information about the Conversation
	private ConversationLog _conversationLog = null;

	// Search suggestion algorithms
	private ArrayList _searches;

	// Callbacks for suggestions
	private ArrayList _suggestionCallbacks;

	private History _history;

	/**
	 * Constructor
	 * @param cc SuggestionEngineConfiguration includes a list of the KFs and Searches that should be loaded
	 */
	public SuggestionManagerImpl(SuggestionEngineConfiguration cc) {
		_history = new History();
		_suggestionsSync = new Object();
		_suggestions = new SuggestionContainer(null);
		_keywordFinders = new ArrayList();
		_searches = new ArrayList();
		_suggestionCallbacks = new ArrayList();

		// Keyword Finders
		Iterator it = cc.getKeywordFinders();
		while (it.hasNext()) {
			try {
				KeywordFinderContainer kfc = (KeywordFinderContainer) it.next();
				String xmlconfig = kfc.getXmlConfig();
				Class[] parameterTypes = {
				};
				Object[] params = {
				};
				Class c = kfc.getKeywordFinderClass();
				Constructor cons = c.getConstructor(parameterTypes);
				KeywordFinder kf = (KeywordFinder) cons.newInstance(params);
				c = kfc.getConfigurationClass();
				parameterTypes = new Class[] { String.class };
				cons = c.getConstructor(parameterTypes);
				params = new Object[] { xmlconfig };
				KeywordFinderConfiguration kfconf =
					(KeywordFinderConfiguration) cons.newInstance(params);
				kf.init(kfconf);
				// don't forget to start the KFs' threads
				Thread t = new Thread(kf);
				t.start();
				_keywordFinders.add(kf);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		// Searches
		it = cc.getSearches();
		while (it.hasNext()) {
			try {
				SearchContainer sc = (SearchContainer) it.next();
				String xmlconfig = sc.getXmlConfig();
				Class[] parameterTypes = {
				};
				Object[] params = {
				};
				Class c = sc.getSearchClass();
				Constructor cons = c.getConstructor(parameterTypes);
				Search se = (Search) cons.newInstance(params);
				c = sc.getConfigurationClass();
				parameterTypes = new Class[] { String.class };
				cons = c.getConstructor(parameterTypes);
				params = new Object[] { xmlconfig };
				SearchConfiguration sconf =
					(SearchConfiguration) cons.newInstance(params);
				se.init(sconf);
				// don't forget to start the Searches' Threads
				Thread t = new Thread(se);
				t.start();
				_searches.add(se);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Passed data back and forth between KeywordFinders and Searches
	 */
	public void run() {

		// No need to start until we have a log that we are analyzing
		while (_conversationLog == null) {
			try {
				Thread.sleep(_START_WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// SuggestionManagerImpl at work
		while (true) {

			// get keywords from all KeywordFinders
			for (int i = 0; i < _keywordFinders.size(); i++) {
				KeywordFinder kf = (KeywordFinder) _keywordFinders.get(i);
				KeywordContainer kc = new KeywordContainer(this);
				kf.signal(kc);
			}

			// get suggestions from all the Searches
			for (int i = 0; i < _searches.size(); i++) {
				Search s = (Search) _searches.get(i);
				SuggestionContainer sc = new SuggestionContainer(this);
				s.signal(sc);
			}

			try {
				Thread.sleep(_SIGNAL_WAIT); // wait a bit
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Keywords from KeywordFinders, pass them to Searches
	 * @see psl.memento.pervasive.recommendation.SuggestionManagerKeywordCallback#signal(psl.memento.pervasive.recommendation.KeywordContainer)
	 */
	public void signal(KeywordContainer kc) {
		_history.addKeywordContainer(kc);
		for (int i = 0; i < _searches.size(); i++) {
			Search s = (Search) _searches.get(i);
			s.push(kc);
		}
	}

	/**
	 * Suggestions from Searches, keep track of them and invoke callbacks
	 * @param sc
	 */
	public void signal(SuggestionContainer sc) {
		_history.addSuggestionContainer(sc);

		// the following commented out loop allows us to change the Feedback for a particular Suggestion to come back to this manager
		/*
		 		 for (int i = 0; i < sc.size(); i++) {
			Suggestion s = sc.get(i);
			s.setFeedbackCallback(this);
		}
		*/

		if (sc.size() == 0)
			return;
		synchronized (_suggestionCallbacks) {
			for (int i = 0; i < _suggestionCallbacks.size(); i++) {
				SuggestionCallback sca =
					(SuggestionCallback) _suggestionCallbacks.get(i);
				sca.signal(sc);
			}
		}

		synchronized (_suggestionsSync) {
			try {
				_suggestions.merge(sc);
			} catch (GenericException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * We currently only support one ConversationLog per SuggestionManager. Additionally, we don't support unregistering this log.
	 * @see psl.memento.pervasive.recommendation.SuggestionManager#unregisterLog(psl.memento.pervasive.recommendation.ConversationLog)
	 */
	public void unregisterLog(ConversationLog cc) {
	}

	/**
	 * @see ps.recommendation.SuggestionManager#getSuggestionsSinceLast()
	 */
	public SuggestionContainer getSuggestionsSinceLast() {
		SuggestionContainer sc = _suggestions;
		synchronized (_suggestionsSync) {
			_suggestions = new SuggestionContainer(null);
			return sc;
		}
	}

	/**
	 * @see psl.memento.pervasive.recommendation.SuggestionManager#subscribeToSuggestions(psl.memento.pervasive.recommendation.SuggestionCallback)
	 */
	public void subscribeToSuggestions(SuggestionCallback sc) {
		synchronized (_suggestionCallbacks) {
			_suggestionCallbacks.add(sc);
		}
	}

	/**
	 * @see psl.memento.pervasive.recommendation.SuggestionManager#unsubscribeToSuggestions(psl.memento.pervasive.recommendation.SuggestionCallback)
	 */
	public void unsubscribeToSuggestions(SuggestionCallback sc)
		throws GenericException {
		if (_suggestionCallbacks.contains(sc)) {
			synchronized (_suggestionCallbacks) {
				_suggestionCallbacks.remove(sc);
			}
		} else
			throw new GenericException("unsubscribeToSuggestions: no such SuggestionCallback to unregister");
	}

	/**
	 * We currently only support one ConversationLog per SuggestionManager.
	 * @see psl.memento.pervasive.recommendation.SuggestionManager#registerLog(psl.memento.pervasive.recommendation.ConversationLog)
	 */
	public void registerLog(ConversationLog cc) throws GenericException {
		_conversationLog = cc;
		for (int i = 0; i < _keywordFinders.size(); i++) {
			KeywordFinder kf = (KeywordFinder) _keywordFinders.get(i);
			kf.registerLogIterator(cc.iterator2());
		}
	}

	/**
	 * @see psl.memento.pervasive.recommendation.SuggestionManager#getSuggestionManagerHistory()
	 */
	public History getSuggestionManagerHistory() {
		return _history;
	}

	/**
	 * Stops all KeywordFinders and Searches managed by this SuggestionManager.
	 */
	public void stop() {
		for (int i = 0; i < _keywordFinders.size(); i++) {
			KeywordFinder kf = (KeywordFinder) _keywordFinders.get(i);
			kf.stop();
		}
		for (int i = 0; i < _searches.size(); i++) {
			Search s = (Search) _searches.get(i);
			s.stop();
		}
	}

	/* 
	 * what do do if we get feedback on a Suggestion, right now nothing.
	 */
	public void signal(Feedback f, Suggestion suggestion) {
		// what do do if we get feedback on a Suggestion, right now nothing.		
	}
}
