package aether;

import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;

import java.util.Properties;
import java.io.IOException;

/**
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public abstract class AetherTestCase extends TestCase
{
	static
	{
		// start log4j
		BasicConfigurator.configure();
	}

	protected Properties testProperties = new Properties();
	protected String elvinHost;
	protected int elvinPort;


	public AetherTestCase()
	{
		try
		{
        	testProperties.load(getClass()
							.getResourceAsStream("/aether-test.properties"));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			fail("caught exception due to exception " + ioe);
		}

		elvinHost = testProperties.getProperty("aether.test.elvinHost");
		elvinPort = Integer
				.parseInt(testProperties.getProperty("aether.test.elvinPort"));
	}

	public String getElvinHost()
	{
		return elvinHost;
	}

	public int getElvinPort()
	{
		return elvinPort;
	}

	public Properties getTestProperties()
	{
		return testProperties;
	}
}

