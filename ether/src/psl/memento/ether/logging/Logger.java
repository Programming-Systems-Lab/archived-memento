package psl.memento.ether.logging;

/**
 * A Logger provides an interface to the container's centralized logging
 * facilities.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public interface Logger
{
   /**
	 * Perform a logical assertion and log a message if it fails.
	 *
	 * @param assertion logical assertion to evaluate
	 * @param msg       message to log if the assertion fails
	 */
	public void logAssert(boolean assertion, String msg);

   /**
	 * Log a message at the debug level.
	 *
	 * @param msg message to log
	 */
	public void debug(Object msg);

	/**
	 * Log a message at the debug level.
	 *
	 * @param msg message to log
	 * @param t   Throwable which caused this log event
	 */
	public void debug(Object msg, Throwable t);

   /**
	 * Log a message at the info level.
	 *
	 * @param msg message to log
	 **/
	public void info(Object msg);

	/**
	 * Log a message at the info level.
	 *
	 * @param msg message to log
	 * @param t   Throwable which caused this log event
	 **/
	public void info(Object msg, Throwable t);

	/**
	 * Log a message at the warning level.
	 *
	 * @param msg message to log
	 */
	public void warn(Object msg);

	/**
	 * Log a message at the warning level.
	 *
	 * @param msg message to log
	 * @param t   Throwable which caused this log event
	 */
	public void warn(Object msg, Throwable t);

   /**
	 * Log a message at the error level.
	 *
	 * @param msg message to log
	 */
	public void error(Object msg);

	/**
	 * Log a message at the error level.
	 *
	 * @param msg message to log
	 * @param t   Throwable which caused this log event
	 */
	public void error(Object msg, Throwable t);

	/**
	 * Log a message at the fatal level.
	 *
	 * @param msg message to log
	 */
	public void fatal(Object msg);

	/**
	 * Log a message at the fatal level.
	 *
	 * @param msg message to log
	 * @param t   Throwable which caused this log event
	 */
	public void fatal(Object msg, Throwable t);

}
