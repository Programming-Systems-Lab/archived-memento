package psl.memento.pervasive.recommendation;

import java.util.ArrayList;

/**
 * This class will be used to keep track of keywordFinder results and 
 * Search results (aka suggestions) given by a particular SuggestionManager.
 * It woulc be advantageous to allow for indexing of these by timestamps.
 * 
 * More functinality (especially useful functionality should be added to this)
 */
public class History {
	private ArrayList _suggestionContainers;
	private ArrayList _suggestionContainerTimestamps;
	
	private ArrayList _keywordContainers;
	private ArrayList _keywordContainerTimestamps;
	
	/**
	 * Constructor
	 */
	public History() {
		_suggestionContainers = new ArrayList();
		_suggestionContainerTimestamps = new ArrayList();
		_keywordContainers = new ArrayList();
		_keywordContainerTimestamps = new ArrayList();
	}
	
	/**
	 * Add a SuggestionContainer to the History (it is assumed that these Suggestions occur after all other suggestions already
	 * added to the History)
	 */
	public void addSuggestionContainer(SuggestionContainer sc) {
		_suggestionContainers.add(sc);
		_suggestionContainerTimestamps.add(new Long(System.currentTimeMillis()));
	}
	
	/**
	 * Get the number of SuggestionContainers added to this History
	 */
	public int getNumSuggestionContainers() {
		return _suggestionContainers.size();
	}
	
	/**
	 * Get the ith SuggestionContainer in this History
	 */
	public SuggestionContainer getSuggestionContainerAt(int i) {
		return (SuggestionContainer) _suggestionContainers.get(i);
	}
	
	/**
	 * Get the add-timestamp (as a System.currentTimeMillis() semantics long) for the ith SuggestionContainer
	 */
	public long getSuggestionTimestampAt(int i) {
		return ((Long) _suggestionContainerTimestamps.get(i)).longValue();
	}
	
	/**
	 * Add a KeywordContainer to the History (it is assumed that these Keywords occur after all other keywords already
	 * added to the History)
	 */
	public void addKeywordContainer(KeywordContainer kc) {
		_keywordContainers.add(kc);
		_keywordContainerTimestamps.add(new Long(System.currentTimeMillis()));
	}
	/**
	 * Get the number of KeywordContainers added to this History
	 */
	public int getNumKeywordContainers() {
		return _keywordContainers.size();
	}
	
	/**
	 * Get the ith KeywordContainer in this History
	 */
	public KeywordContainer getKeywordContainerAt(int i) {
		return (KeywordContainer) _keywordContainers.get(i);
	}
	
	/**
	 * Get the add-timestap (as a System.currentTimeMillis() semantics long) for the ith KeywordContainer
	 */
	public long getKeywordTimestampAt(int i) {
		return ((Long) _keywordContainerTimestamps.get(i)).longValue();
	}
	
}
