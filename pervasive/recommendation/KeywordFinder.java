package psl.memento.pervasive.recommendation;

import psl.memento.pervasive.recommendation.exception.GenericException;

/**
 * Interface for a KwyrodFinder, an component that analyzes an ongoing conversation (from a ConversationLogMessageStream)
 * and upon request gives Keywords that describe the contents of the Conversations. Keywords are currently
 * Strings associated with Context, though in the future more complex conversation-descriptions should be made available.
 */
public interface KeywordFinder extends Runnable {
	
	/**
	 * Register a ConversationLogMessageStream from which this KeywordFinder gets information about an ongoing Conversation.
	 * Note that a KeywordFinder can only operate on one log at a time.
	 */
	public void registerLogIterator(ConversationLogMessageStream cli) throws GenericException;
	
	/**
	 * Start the underlying analysis algorithm.
	 * 
	 * @param kfc is a specific configuration for this implementation, based on an xml document
	 * @throws Exception If the XMLContainer is not of the correct type for this
	 * KeywordFinder implementation
	 */
	public void init(KeywordFinderConfiguration kfc);
	
	/**
	 * For some algorithms, the concept of a "reset" is useful. This forces the
	 * underlying implementation to ignore all information prior to the reset.
	 * This can be thought of as a restart where the algorithm's config remains
	 * unchanged. Note that after a reset, the KF keeps looking at data from teh same ConversationLogMessageStream
	 */
	public void reset();
	
	/**
	 * Same as reset() except we load up the algorithm with a different
	 * configuration.
	 * 
	 * @param kfc is a specific configuration for this implementation, based on an xml document
	 * @throws Exception If the XMLContainer is not of the correct type for this
	 * KeywordFinder implementation
	 */
	public void reset(KeywordFinderConfiguration kfc) throws Exception;
	
	/**
	 * Stop this keyword finder. Note that after stopping, the object cannot be restarted (init) or reset. It is 
	 * essentially ready for garbage collection.
	 */
	public void stop();
	
	/**
	 * This signals the analysis algorithm for the most recent keywords.
	 */
	public void signal(KeywordContainer kc);
}
