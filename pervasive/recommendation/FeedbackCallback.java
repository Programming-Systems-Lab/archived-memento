/*
 * Created on Jun 16, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package psl.memento.pervasive.recommendation;

/**
 * @author jg253
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface FeedbackCallback {

	/**
	 * Signal the implementor of this interface (most likely a Search algorithm) that there is feedback for a particular suggestion.
	 */
	void signal(Feedback f, Suggestion suggestion);
}