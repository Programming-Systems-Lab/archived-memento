package psl.memento.pervasive.recommendation;

/**
 * Extension of Suggestion that is simply a String of text and a Relevance indicating how closely
 * this suggestions adheres to the KeywordFinders' results.
 * 
 * @see psl.memento.pervasive.recommendation.Suggestion
 */
public class TextSuggestion extends Suggestion {

	// Text 
	private String _text;
	
	/**
	 * Constructor
	 */
	public TextSuggestion(String s, Relevance r, Search se) {
		super(r, se);
		_text = s;
	}

	/**
	 * Get the Text of this suggestion
	 */
	public String getText() {
		return _text;
	}
	
	/**
	 * return a String representation of this Suggestion
	 */
	public String toString() {
		return _text;
	}
}
