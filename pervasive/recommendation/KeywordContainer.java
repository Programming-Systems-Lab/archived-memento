package psl.memento.pervasive.recommendation;

import java.util.ArrayList;
import java.util.Iterator;

import psl.memento.pervasive.recommendation.exception.GenericException;
import psl.memento.pervasive.recommendation.util.NoRemoveIterator;

/**
 * Container class used to hold many Keywords together for transportation.
 * It is also used to provide asynchronous communication between any method that accepts a KeywordContainer
 * and a class implementing the SuggestionManagerKeywordCallback. The callback is automatically invoked
 * when the container's close() method is invoked.
 * 
 * @see psl.memento.pervasive.recommendation.SuggestionManagerKeywordCallback
 */
public class KeywordContainer {

	// holds all the Keyword objects in this container
	private ArrayList _keywords;

	// this is the class that gets called back when the container is close()
	private SuggestionManagerKeywordCallback _smkc;

	// this is used to ensure that operations are not completed once the Container is closed
	private boolean closed = false;

	/**
	 * When the container is constructed, one needs to specify the class that should be calledback when the container is close();
	 */
	public KeywordContainer(SuggestionManagerKeywordCallback smkc) {
		_keywords = new ArrayList();
		_smkc = smkc;
	}

	/**
	 * add a Keyword to the container. Throws a GenericException if the container's close() method has already been invoked
	 */
	public void add(Keyword s) throws GenericException {
		if (!closed)
			_keywords.add(s);
		else
			throw new GenericException("KeywordContainer closed");
	}

	/**
	 * @return how many Keywords are in this container
	 */
	public int size() {
		return _keywords.size();
	}

	/**
	 * Returns an iterator to the Keywords in this container. Note that the returned Iterator does not support the remove() method call.
	 * @return
	 */
	public Iterator iterator() {
		return new NoRemoveIterator(_keywords.iterator());
	}

	/**
	 * get the ith Keyword from the container
	 */
	public Keyword get(int i) {
		return (Keyword) _keywords.get(i);
	}

	/**
	 * Close the container, and invoke the callback. After this call, nothing can be added to the Container.
	 *
	 */
	public void close() {
		_smkc.signal(this);
	}
}
