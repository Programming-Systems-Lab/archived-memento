package psl.memento.ether.logging.log4j;

import org.apache.log4j.BasicConfigurator;
import psl.memento.ether.logging.Logger;

/**
 * Provides Log4j-based logs to the container.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class Log4jLoggerProvider
		  extends psl.memento.ether.logging.LoggerProvider
{
   /**
	 * Construct a new Log4jLoggerProvider and initialize the root logger.
	 */
	public Log4jLoggerProvider()
	{
		BasicConfigurator.configure();
      rootLogger =
				  new Log4jLogger(
							 org.apache.log4j.Logger.getLogger("container-root"));
	}

    /**
	 * Construct a new logger with a given logger as its parent.
	 *
	 * @param name   name of the new logger to construct
	 * @param parent Logger to serve as the new logger
	 * @return new child logger with the given name and parent
	 */
	public Logger getLogger(String name,
									psl.memento.ether.logging.Logger parent)
	{
		if ((name == null) || (parent == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

      org.apache.log4j.Logger parentLogger =
				  ((Log4jLogger) parent).getUnderlyingLogger();

		String childName = parentLogger.getName() + "." + name;
		org.apache.log4j.Logger childLogger =
				  org.apache.log4j.Logger.getLogger(childName);

		return new Log4jLogger(childLogger);
	}
}
