package psl.memento.pervasive.recommendation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import psl.memento.pervasive.recommendation.exception.GenericException;

/**
 * This represents a view of the ongoings of a conversation. What a log has access to depends on the access of the 
 * User associated with the log.
 */
// TODO Ideally this class should offer more information about the underlying Conversation
public class ConversationLog implements Log {

	/**
	 * Straight up implementation of a ConversationLogMessageStream
	 */
	private static class ConversationLogIteratorImpl
		implements ConversationLogMessageStream {

		private ArrayList _al;
		private int _index;

		/**
		 * ArrayList of ConversationMessages to iteratoe over
		 * Index of the first term in the list that is relevant
		 */
		public ConversationLogIteratorImpl(ArrayList al, int index) {
			_al = al;
			_index = index;
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return _index < _al.size();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public Object next() {
			if ((0 > _index) || (_index > _al.size()))
				throw new NoSuchElementException();
			return _al.get(_index++);
		}

		/**
		 * Not supported
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see psl.memento.pervasive.recommendation.ConversationLogMessageStream#next2()
		 */
		public ConversationMessage next2() {
			return (ConversationMessage) next();
		}
	}

	// the messages
	private ArrayList _messages;

	// this implies that the ConversationLog is blocked (aka no messages can be added to it). This is used when a conversation ends.
	private boolean _blocked;

	/**
	 * Create a Log
	 */
	public ConversationLog() {
		_messages = new ArrayList();
	}

	/**
	 * @return whether the Log is active or blocked (aka ended)
	 */
	public boolean isBlocked() {
		return _blocked;
	}

	/**
	 * @param cm messages to add to log
	 * @throws GenericException if the log is blocked
	 */
	public void add(ConversationMessage cm) throws GenericException {
		if (_blocked) {
			throw new GenericException("Cannot add ConversationMessage to a blocked/stopped ConversationLog");
		}
		_messages.add(cm);
	}

	/**
	 * @param i get ith message
	 * @return the message
	 * @throws IndexOutOfBoundsException if there is no ith message
	 * @throws GenericException if the log is blocked
	 */
	public ConversationMessage getMessageAt(int i)
		throws IndexOutOfBoundsException, GenericException {
		if (_blocked) {
			throw new GenericException("Cannot get a message from a blocked/stopped ConversationLog");
		}
		return (ConversationMessage) _messages.get(i);
	}

	/**
	 * @return number of messages in log
	 */
	public int size() {
		return _messages.size();
	}

	/**
	 * block the log, cannot be undone
	 */
	public void block() {
		_blocked = true;
	}

	/**
	 * returns iterator over messages in this log
	 *  @return   ConversationLogMessageStream
	 */
	public Iterator iterator() {
		return new ConversationLogIteratorImpl(_messages, _messages.size());
	}

	/**
	 * returns iterator over messsages in this log
	 *  @return   ConversationLogMessageStream
	 */
	public ConversationLogMessageStream iterator2() {
		try {
			return new ConversationLogIteratorImpl(_messages, _messages.size());
		} catch (Exception e) {
			System.err.println("ConversationLog.iterator() - error");
		}
		return null;
	}
}
