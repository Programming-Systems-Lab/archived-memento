package psl.memento.ether.logging;

/**
 * Represents an object which provides logs to all components and providers
 * within the container.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */

public abstract class LoggerProvider
{
	private static LoggerProvider instance;
	protected Logger rootLogger;

	/**
	 * Get the container-wide logger provider.
	 *
	 * @return container-wide logger provider
	 */
	public static LoggerProvider getInstance()
	{
		return instance;
	}

	/**
	 * Set the container-wide logger provider.
	 *
	 * @param logProv logger provider to be used by this container
	 */
	public static void setInstance(LoggerProvider logProv)
	{
		if (logProv == null)
		{
			String msg = "logProv can't be null";
			throw new IllegalArgumentException(msg);
		}

		instance = logProv;
	}

	/**
	 * Get the root logger for all loggers in this container.
	 *
	 * @return root logger for all loggers in the container
	 */
	public Logger getRootLogger()
	{
		return rootLogger;
	}

   /**
	 * Construct a new logger with a given logger as its parent.
	 *
	 * @param name   name of the new logger to construct
	 * @param parent Logger to serve as the new logger
	 * @return new child logger with the given name and parent
	 */
	public abstract Logger getLogger(String name, Logger parent);

}
