package psl.memento.pervasive.crunch;

/**
 * Signals that a timeout has occurred.
 */
public class ReadTimeoutException extends Exception {
	public ReadTimeoutException() {
		super();
	}
	public ReadTimeoutException(String s) {
		super(s);
	}
}
