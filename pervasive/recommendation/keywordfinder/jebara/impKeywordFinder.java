package psl.memento.pervasive.recommendation.keywordfinder.jebara;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import psl.memento.pervasive.recommendation.Context;
import psl.memento.pervasive.recommendation.ConversationLogMessageStream;
import psl.memento.pervasive.recommendation.ConversationMessage;
import psl.memento.pervasive.recommendation.Keyword;
import psl.memento.pervasive.recommendation.KeywordContainer;
import psl.memento.pervasive.recommendation.KeywordFinder;
import psl.memento.pervasive.recommendation.KeywordFinderConfiguration;
import psl.memento.pervasive.recommendation.exception.GenericException;

/**
 * Implementation of a KeywordFinder based on Tony Jebara's conversation analysis algorithms.
 * (see "Tracking Conversational Context for Machine Mediation of Human Discourse" by Tony Jebara, Yuri Ivanov, Ali Rahimi and Alex Pentlanf)
 * 
 * This calls an implementation of the algorithm coded in C++. See documentation w/ the C++ code to see how 
 * the environment needs to be set up for it to work.
 * 
 * Implemented by Julia Cheng, jc424_at_columbia.edu
 * Additional comments and slight changes by Jean-Denis Greze jg253_at_columbia.edu
 */
public class impKeywordFinder implements KeywordFinder {

	/* Native calls */
	private native boolean init(String args);
	private native void addConversationWord(String s);
	private native void resetConversationHistory();
	private native void cleanUp();

	private static final String KEYWORD_SEPARATOR = ",";

	/* RegExp used to get rid of bad characters in documents */
	private static Pattern conversationTextValidChars;

	private impXMLContainer _config;
	private ConversationLogMessageStream _it;

	private boolean _running = true;

	/**
	 * To save memory space, all keywords that have occurred will be placed into
	 * a hashtable. The history vector of keywords will contain references to
	 * the hashtable entry.
	 */
	private Vector topicHistory = new Vector();

	/**
	 * See description for topicHistory
	 */
	private Hashtable topics = new Hashtable();

	/* ***** REQUIRED INTERFACE CLASSES (For A) ***** */
	/**
	 * Constructor for impKeywordFinder.
	 */
	public impKeywordFinder() {
		conversationTextValidChars =
			Pattern.compile("[^a-zA-Z]+", Pattern.DOTALL);
	}

	/**
	 * Initialized Keyword finger implementation. It is important the the KFC passed be of type impXMLContainer.
	 * Method is invoked automatically by the SuggestionManager upon startup.
	 * @see KeywordFinder@init(KeywordFinderConfiguration kfc)
	 */
	public void init(KeywordFinderConfiguration kfc) {
		_config = (impXMLContainer) kfc;
	}

	/**
	 * @see KeywordFinder#reset()
	 */
	public void reset() {
		resetConversationHistory(); // call to native function
	}

	/**
	 * This methods calls for data to be returned to the SuggestionManager
	 * @see KeywordFinder#signal(KeywordContainer)
	 */
	public void signal(KeywordContainer kc) {
		// System.out.println("JAVA KF: SIGNAL IS CALLED");
		if (topicHistory.isEmpty()) { // nothing to return
			// System.out.println("JAVA KF: NOTHING TO RETURN");
			return;
		}
		StringTokenizer stKeywords =
			new StringTokenizer(
				(String) topicHistory.get(topicHistory.size() - 1),
				KEYWORD_SEPARATOR);
		while (stKeywords.hasMoreTokens()) {
			// add everything we need to return to the return container kc
			// System.out.println("JAVA KF: RETURN KEYWORDS NOW");
			try {
			kc.add(
				new Keyword(
					stKeywords.nextToken(),
					new Context(),
					Keyword.NO_DELAY));
			} catch (GenericException ge) {
				ge.printStackTrace();
			}
		}
		kc.close(); // return the keywords to the SuggestionManager 
	}

	/**
	 * This executes the KeywordFinder algorithm
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// Invoke sweeperM here
		System.setProperty(
			"java.library.path",
			".:" + System.getProperty("java.library.path"));
		System.loadLibrary("sweeperM");

		// The call to init in the native call 
		boolean testReset = _config.getTestReset();
		String initArgs = _config.getInitString();
		// System.out.println("JAVA INVOKING sweeperM with " + initArgs);
		boolean success = init(initArgs);

		if (success) {
			// Continously pass data to sweeperM.  If the Converstaion Iterator is empty,
			// keep waiting because it might have more conversation messages later.
			while (_running) {
				while ((_it != null) && _it.hasNext()) {
					ConversationMessage msg = _it.next2();
					String msgText = msg.getMessage().trim();
					Matcher matches =
						conversationTextValidChars.matcher(msgText);
					// System.out.println(
					// 	"JAVA, CONVERSATION TEXT BEFORE CLEANING: " + msgText);
					msgText = matches.replaceAll(" ");
					// System.out.println("JAVA PASSING: " + msgText);

					if (testReset) { // TODO do we really want to be resetting every time?
						reset();
						// call to native function, disregard previous messages.
					}

					addConversationWord(msgText); // call to native function
				}
			}
		} else {
			System.out.println("Bad initialization parameters.");
			cleanUp();
			throw new RuntimeException("Bad initialization parameters to jebara.impKeywordFinder.run()");
		}

		// unreachable, but  if there is ever a clean way to exit the while loop, then we would want to do this.
		// cleanUp(); // call to native function to release heap memory.
	}

	/**
	 * To be used by the native class to pass in the keyword 
	 */
	private void setKeyword(String keyword) {
		// System.out.println("setKeyword called with " + keyword);
		topics.put(keyword, keyword);
		topicHistory.add(topics.get(keyword));
		// The vector contains only a pointer.
	}

	/**
	 * This implementation only support one ConversationLogMessageStream
	 * @see psl.memento.pervasive.recommendation.KeywordFinder#registerLogIterator(psl.memento.pervasive.recommendation.ConversationLogMessageStream)
	 */
	public void registerLogIterator(ConversationLogMessageStream cli) {
		_it = cli;
	}

	/**
	 * No support for unregistering a ConversationLogMessageStream in this implementation
	 * @see psl.memento.pervasive.recommendation.KeywordFinder#unregisterLogIterator(psl.memento.pervasive.recommendation.ConversationLogMessageStream)
	 */
	public void unregisterLogIterator(ConversationLogMessageStream cli) {
	}
	
	/**
	 * No support for reset in this implementation. The algorithm can only be initialized once
	 * @see psl.memento.pervasive.recommendation.KeywordFinder#reset(psl.memento.pervasive.recommendation.KeywordFinderConfiguration)
	 */
	public void reset(KeywordFinderConfiguration kfc) throws Exception {
	}
	
	/**
	 * Currently no support for stopping the implementation while running. Though this could be easily fixed by adding a variable
	 * to control the loop in the run() method.
	 * @see psl.memento.pervasive.recommendation.KeywordFinder#stop()
	 */
	public void stop() {
		_running = false;
	}

}
