package psl.memento.pervasive.recommendation.search.test;

import java.util.ArrayList;

import psl.memento.pervasive.recommendation.Feedback;
import psl.memento.pervasive.recommendation.Keyword;
import psl.memento.pervasive.recommendation.KeywordContainer;
import psl.memento.pervasive.recommendation.Relevance;
import psl.memento.pervasive.recommendation.SearchConfiguration;
import psl.memento.pervasive.recommendation.Suggestion;
import psl.memento.pervasive.recommendation.TextSuggestion;
import psl.memento.pervasive.recommendation.exception.GenericException;
import psl.memento.pervasive.recommendation.util.SearchSimpleImpl;

/**
 * Test implementation of a Search, it just spits out the Keywords that it gets.
 */
public class SearchTestImpl extends SearchSimpleImpl {

	//	How long to wait after one call of the DoWork() method.
	private final int _DOWORK_WAIT = 500;

	//	keywords to be processed
	protected ArrayList _keywords = new ArrayList();

	/**
	 * Turn keywords into suggestions
	 */
	protected void returnData() {

		// if we have some keywords to process, do so
		while (_keywords.size() > 0) {
			Keyword k = (Keyword) _keywords.get(0);
			Suggestion s =
				new TextSuggestion(
					"suggestion: " + k.getKeyword(),
					new Relevance(),
					this);
			// add the suggestion to the return container
			try {
				_returnData.addSuggestion(s);
			} catch (GenericException e) {
				e.printStackTrace();
			}
			_keywords.remove(0);
		}
	}

	/**
	 * we do not work in the test version
	 * but we spend some time pretending we are doing work	
	 */
	protected void doWork() {
		try {
			Thread.sleep(_DOWORK_WAIT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * No initialization in SearchTestImpl
	 */
	public void init(SearchConfiguration sconf) {
	}

	/**
	 * This method is used to push Keywords to the Seach algorithms
	 */
	public void push(KeywordContainer kc) {
		for (int i = 0; i < kc.size(); i++) {
			// we push at the tail of the ArrayList and pop at the head
			_keywords.add(kc.get(i));
		}
	}

	/**
	 * @see psl.memento.pervasive.recommendation.Search#signal(psl.memento.pervasive.recommendation.Feedback, psl.memento.pervasive.recommendation.Suggestion)
	 */
	public void signal(Feedback f, Suggestion suggestion) {
		// TODO handle feedback		
	}
}
