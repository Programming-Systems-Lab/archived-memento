package psl.memento.pervasive.recommendation;

/**
 * Event are a current filler for an eventual event bus/messaging mechanism.
 * This is needed to that changes in User Profiles, etc. can be propagated to the various components
 * such as (SuggestionEngines, Conversations, etc) in a consistent, straight forward manner.
 */
public class Event {

	// The basic event is just a String with a message. Extensions of this class should include more data
	private String _message;

	/**
	 * Events are just a String (aka message). Extensions of this class should include more information for specific Event types. 
	 */
	public Event(String message) {
		_message = message;
	}

	/**
	 * toString() returns the message passed to the event via the constructor
	 */
	public String toString() {
		return _message;
	}
}
