package psl.memento.pervasive.recommendation;

import java.util.Iterator;

/**
 * Iterator/Stream over a Conversation
 */
public interface ConversationLogMessageStream extends Iterator {

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext();

	/**
	 * returns a ConversationMessage disguised as an Object
	 * @see java.util.Iterator#next()
	 */
	public Object next();

	/**
	 * No support for remove, should through an exception or not do anything.
	 * @see java.util.Iterator#remove()
	 */
	public void remove();

	/**
	 * Like next but returns a ConversationMessage guaranteed
	 * @return ConversationMessage
	 */
	public ConversationMessage next2();
}
