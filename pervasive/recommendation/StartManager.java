package psl.memento.pervasive.recommendation;

/**
 * A Manager to be used for the initial round of suggestions. These are really pre-suggestions (aka recommendations about 
 * what the conversation should be before it starts, such as suggestions of topics to discuss, etc.
 */
public interface StartManager {
	
	/**
	 * Stops all Algorithms managed by this StartManager. Essentially makes the StartManager and all underlying threads
	 * ready for garbage collection.
	 */
	public void stop();
}
