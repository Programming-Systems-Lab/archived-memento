package psl.memento.pervasive.recommendation;

/**
 * @author jg253
 * 
 * This class should give the particular relevance (aka a grade) of a particular suggestion in a given conversation. 
 * For now, we only offer the concept of a delay aka the millisecond difference between the creation of
 * a Suggestion object and the "rough" (mean) average of the Keyword objects taht resulted in the Suggestion
 * plus the "rough" (mean) average of the delays of these Keyword objects.
 * 
 * These semantics are enforced in the creation of a Suggestion Object.
 * 
 * We would eventually want more funtionality out of this object (such as grading a suggestion)
 */
public class Relevance {

	// static final meaning no delay
	public static final long NO_DELAY = -1;

	// millisecond delay from this object's creation to the mean of the objects based on which the suggestion was made 
	// (plus the delays inherent to those objects)
	private long _delay;

	/**
	 * See semantics described above for delay
	 */
	public Relevance(long delay) {
		_delay = delay;
	}

}
