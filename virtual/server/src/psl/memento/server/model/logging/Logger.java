package psl.memento.server.model.logging;

/**
 * Basic logger interface which allows managed components to use the server-defined
 * logging facilities.
 * 
 * @author Buko Obele (buko@cs.columbia.edu)
 * @version 0.1
 */
public interface Logger
{
	/**
	 * Perform an assertion and if the assertion fails log a message.
	 * 
	 * @param assertion assertion to perform
	 * @param msg       msg to log if <c>assertion</c> fails.
	 **/
	public void assert(boolean assertion, String msg);

}
