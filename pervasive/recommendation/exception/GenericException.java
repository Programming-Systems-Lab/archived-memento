package psl.memento.pervasive.recommendation.exception;

// TODO We should implement Exceptions as a hierarchy in the future

/**
 * Top of the psl.memento.pervasive.recommendation Exception hierarchy.
 */
public class GenericException extends Exception {

	/**
	 * @param description String description of what went wrong
	 */
	public GenericException(String description) {
		super(description);
	}

}
