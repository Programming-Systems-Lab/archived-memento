package psl.memento.ether.logging.log4j;

/**
 * Adapts the Log4j to the ether logger interface.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
class Log4jLogger implements psl.memento.ether.logging.Logger
{
	private org.apache.log4j.Logger logger;

	/**
	 * Construct a new Log4jLogger which wraps the given Log4j logger.
	 *
	 * @param logger Log4j logger to adapt
	 */
	public Log4jLogger(org.apache.log4j.Logger logger)
	{
		if (logger == null)
		{
			String msg = "logger can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.logger = logger;
	}

	/**
	 * Perform a logical assertion and log a message if it fails.
	 *
	 * @param assertion logical assertion to evaluate
	 * @param msg       message to log if the assertion fails
	 */
	public void logAssert(boolean assertion, String msg)
	{
		logger.assertLog(assertion, msg);
	}

   /**
	 * Log a message at the debug level.
	 *
	 * @param msg message to log
	 */
	public void debug(Object msg)
	{
		logger.debug(msg);
	}

	/**
	 * Log a message at the debug level.
	 *
	 * @param msg message to log
	 * @param t   Throwable which caused this log event
	 */
	public void debug(Object msg, Throwable t)
	{
		logger.debug(msg, t);
	}

   /**
	 * Log a message at the info level.
	 *
	 * @param msg message to log
	 **/
	public void info(Object msg)
	{
		logger.info(msg);
	}

	/**
	 * Log a message at the info level.
	 *
	 * @param msg message to log
	 * @param t   Throwable which caused this log event
	 **/
	public void info(Object msg, Throwable t)
	{
		logger.info(msg, t);
	}

	/**
	 * Log a message at the warning level.
	 *
	 * @param msg message to log
	 */
	public void warn(Object msg)
	{
		logger.warn(msg);
	}

	/**
	 * Log a message at the warning level.
	 *
	 * @param msg message to log
	 * @param t   Throwable which caused this log event
	 */
	public void warn(Object msg, Throwable t)
	{
		logger.warn(msg, t);
	}

   /**
	 * Log a message at the error level.
	 *
	 * @param msg message to log
	 */
	public void error(Object msg)
	{
		logger.error(msg);
	}

	/**
	 * Log a message at the error level.
	 *
	 * @param msg message to log
	 * @param t   Throwable which caused this log event
	 */
	public void error(Object msg, Throwable t)
	{
		logger.error(msg, t);
	}

	/**
	 * Log a message at the fatal level.
	 *
	 * @param msg message to log
	 */
	public void fatal(Object msg)
	{
		logger.fatal(msg);
	}

	/**
	 * Log a message at the fatal level.
	 *
	 * @param msg message to log
	 * @param t   Throwable which caused this log event
	 */
	public void fatal(Object msg, Throwable t)
	{
		logger.fatal(msg, t);
	}

	/**
	 * Get the underlying log4j logger.
	 *
	 * @return underlying log4j logger
	 */
	public org.apache.log4j.Logger getUnderlyingLogger()
	{
		return logger;
	}
}
