package psl.memento.pervasive.recommendation;

import java.util.ArrayList;
import java.util.Iterator;

import psl.memento.pervasive.recommendation.exception.GenericException;
import psl.memento.pervasive.recommendation.util.NoRemoveIterator;

/**
 * This contains 0 or more suggestions.
 */
public class SuggestionContainer {

	// stores the suggestions
	private ArrayList _suggestions;

	// reference to the component that should be notified when this container is closed
	private SuggestionManagerSuggestionCallback _smsc;

	// whether or not this container is closed. Once closed, no more suggestions can be added
	private boolean _closed = false;

	/**
	 * Constructor. Must include a manager to callback once the container is closed.
	 */
	public SuggestionContainer(SuggestionManagerSuggestionCallback smsc) {
		_smsc = smsc;
		_suggestions = new ArrayList();
	}

	/**
	 * Add a suggestion to this container
	 * @throws GenericException if the container is closed
	 */
	public void addSuggestion(Suggestion s) throws GenericException {
		if (!_closed)
			_suggestions.add(s);
		else
			throw new GenericException("SuggestionContainer closed");
	}

	/**
	 * Allows for iteration over the Suggestions in this container. However, the returned Iterator does not support the remove() call.
	 * @return
	 */
	public Iterator iterator() {
		return new NoRemoveIterator(_suggestions.iterator());
	}

	/**
	 * close this container. This causes the managing component to be signalled. Additionally, no content may be added
	 * to this container after the invoking of close()
	 */
	public void close() {
		_smsc.signal(this);

	}
	/**
	 * merge two SuggestionContainers. this (suggestion container) cannot be closed for this to work.
	 */
	public void merge(SuggestionContainer sc) throws GenericException {
		Iterator it = sc.iterator();
		while (it.hasNext()) {
			addSuggestion((Suggestion) it.next());
		}
	}

	/**
	 * @return # of suggestions in this container
	 */
	public int size() {
		return _suggestions.size();
	}
}
