package psl.memento.pervasive.recommendation;

/**
 * State object representing a conversation being either in the process of initialization, started or ended.
 * This class does not support a FA-style transfer mechanism
 */
public class ConversationState {

	// conversation is being initialized (1)
	public static final ConversationState init = new ConversationState(0);

	// conversation is started and ongoing (2)
	public static final ConversationState started = new ConversationState(1);

	// conversation is ended (3)
	public static final ConversationState ended = new ConversationState(2);

	// internal representation of the state as an integer, still we only ever compare objects to the constants above so this is of no matter
	private int _state;

	/**
	 * private constructor is used to define constants
	 */
	private ConversationState(int i) {
		_state = i;
	}

	/**
	 * public constructor: Essentially a copy constructor from a pre-existing state (aka a constant)
	 */
	public ConversationState(ConversationState cs) {
		_state = cs._state;
	}

	/**
	 * Change this current state to another pre-existing state (aka a constant)
	 */
	public void changeTo(ConversationState cs) {
		_state = cs._state;
	}

	/**
	 * Used to compare 2 state objects
	 */
	public boolean equals(ConversationState cs) {
		return cs._state == _state;
	}
}
