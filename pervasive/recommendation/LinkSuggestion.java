package psl.memento.pervasive.recommendation;

import java.net.URL;

/**
 * Extension of Suggestion that is a URL and a description and a Relevance indicating how 
 * closely this suggestion adheres to the KeywordFinders' results.
 *
 * @see psl.memento.pervasive.recommendation.Suggestion
 **/
public class LinkSuggestion extends Suggestion {

	// URL description
	private String _description;

	// URL
	private URL _url;

	/**
	 * Constructor
	 */
	public LinkSuggestion(String description, URL url, Relevance r, Search s) {
		super(r, s);
		_description = description;
		_url = url;
	}

	/**
	 * @return the URL of this suggestion
	 */
	public URL getUrl() {
		return _url;
	}

	/**
	 * @return a textual description of this suggestion
	 */
	public String getDescription() {
		return _description;
	}
}
