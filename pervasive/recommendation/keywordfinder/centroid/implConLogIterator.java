package psl.memento.pervasive.recommendation.keywordfinder.centroid;

import java.util.NoSuchElementException;
import java.util.Vector;

import psl.conversation.ConversationLogIterator;
import psl.conversation.ConversationMessage;


public class implConLogIterator implements ConversationLogIterator {
	private Vector messages = new Vector();
	private int msgIndex = 0;
	
	/**
	 * Constructor for impConLogIterator.
	 */
	public implConLogIterator() {
		super();
	}

	public void addMessage(ConversationMessage msg) {
		messages.add(msg);
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if(msgIndex < messages.size()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		if(hasNext()) {
			return messages.get(msgIndex++);
		} else {
			throw new NoSuchElementException();
		}
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ConversationLogIterator#next2()
	 */
	public ConversationMessage next2() {
		return (ConversationMessage)next();
	}

}
