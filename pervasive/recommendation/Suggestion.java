package psl.memento.pervasive.recommendation;

/**
 * A bare suggestion, which is simply a Relevance object and a timestamp. Should really be considered as a class to extend
 * for real Suggestion implementations (such as LinkSuggestion, etc)
 */
public class Suggestion  {
	
	// how relevant this Suggestion is to the ongoing conversation
	protected Relevance _relevance;
	
	// a callback for feedback on a particular suggestion
	protected Search _search;
	
	// timestamp of when this suggestion was created
	private long _timestamp;
	
	private FeedbackCallback _feedbackcallback;
	
	/**
	 * Constructor
	 * The Search is passed so that it may receive feedback about how good a suggestion is.
	 */
	public Suggestion(Relevance r, Search s) {
		_relevance = r;
		_timestamp = System.currentTimeMillis();
		_search = s;
		_feedbackcallback = s;
	}
	
	/**
	 * @return how relevant this suggestion was to the conversation when it was constructed
	 */
	public Relevance getRelevance () {
		return _relevance;
	}
	
	/**
	 * @return get the timstamp of the creation of this Suggestion (semantics from the System.currentTimeMillis() call)
	 */
	public long getTimeMillis() {
		return _timestamp;
	}
	
	public void feedback(Feedback f) {
		_feedbackcallback.signal(f, this);
	}
	
	public void setFeedbackCallback(FeedbackCallback fc) {
		_feedbackcallback = fc;
	}
}
