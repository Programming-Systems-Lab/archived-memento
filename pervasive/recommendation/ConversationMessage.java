package psl.memento.pervasive.recommendation;

/**
 * A User's message in a conversation
 */
public class ConversationMessage {

	// User who sent the message
	private User _sender;

	// String message went by user
	private String _message;

	// timestamp of when the message was written given as a long of millis from the Syste.currentTimeMillis() call
	private long _timestamp;

	/**
	 * @param sender
	 * @param message
	 */
	public ConversationMessage(User sender, String message) {
		_sender = sender;
		_message = message;
		_timestamp = System.currentTimeMillis();
	}

	/**
	 * Method getUser.
	 * @return User
	 */
	public User getUser() {
		return _sender;
	}

	/**
	 * Method getMessage.
	 * @return String
	 */
	public String getMessage() {
		return _message;
	}

	/**
	 * Get the timestamp of this ConversationMessage Object's creation
	 */
	public long getTimeMillis() {
		return _timestamp;
	}

	/**
	 * Arbitrary String representation of the message.
	 */
	public String toString() {
		return "{" + getUser().getName() + "}" + " " + "[" + getMessage() + "]";
	}
}
