package psl.memento.pervasive.recommendation;

/**
 * A Keyword is the result of a KeywordFinder's analysis of a Conversation.
 * Keywrods are put into Context (which may chose to include any needed information to disambiguiate a Keywords, 
 * and may actually give information as to what part of the conversation the keyword is relevant to).
 * For above functionality, one needs to extent the current filler-only implementation of Context.
 * Additionally, Keywords have a concept of delay which is a milisecond time given by the KeywordFinder
 * indicating the relevance of the keyword in the timeline of the conversation.
 */
public class Keyword {

	// constant for no delay for a keyword 
	public static final long NO_DELAY = -1;

	// context of the keyword
	private Context _context;

	// keywords are strings, use the context to disambiguate
	private String _keyword;

	// timestamp of when the keyword was created
	private long _timestamp;

	// delay from the timstamp for when the timestamp was most relevant to the conversation
	private long _delay;

	// how relevant this keyword is to the ongoing conversation
	private Relevance _relevance;

	/** 
	 * Constructor, @see psl.memento.pervasive.recommendation.Keyword class description for more information.
	 */
	public Keyword(String s, Context c, long delay, Relevance r) {
		_keyword = s;
		_context = c;
		_timestamp = System.currentTimeMillis();
		_delay = delay;
		_relevance = r;
	}

	/**
	 * @return the context of the keyword
	 */
	public Context getContext() {
		return _context;
	}

	/**
	 * @return the String keyword
	 */
	public String getKeyword() {
		return _keyword;
	}

	/**
	 * @return the millisecond time (using java's System.currentTimeMillis() time semantics) of the object's creation
	 */
	public long getTimeMillis() {
		return _timestamp;
	}

	/**
	 * @return get the delay in milliseconds from this object's creation to when it was most relevant to the conversation
	 */
	public long getDelay() {
		return _delay;
	}
}
