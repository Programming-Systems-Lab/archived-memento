package memento.frax;

import psl.memento.server.frax.Frax;
import psl.memento.server.frax.XMLFraxConfiguration;
import net.concedere.dundee.framework.Initializable;
import net.concedere.dundee.framework.Disposable;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.DefaultComponent;

import java.io.InputStream;
import java.beans.beancontext.BeanContextServiceProvider;
import java.beans.beancontext.BeanContextServices;
import java.util.Iterator;

/**
 * Default implementation of the FraxProvider interface.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultFraxProvider extends DefaultComponent
		implements Initializable, Disposable,
		BeanContextServiceProvider
{
	private Frax frax;

	public Frax getFrax()
	{
		return frax;
	}

	public void initialize() throws ComponentException
	{
        this.frax = Frax.getInstance();

		try
		{
			InputStream in = getClass().getClassLoader()
					.getResourceAsStream("frax-config.xml");
        	frax.setConfiguration(new XMLFraxConfiguration(in));
		}
		catch (Exception e)
		{
			String msg = "couldn't configure frax";
			throw new ComponentException(msg, e);
		}

        frax.getConfiguration().setExtractContentMetadata(true);

		// register to be the frax provider
		getContainer().addService(Frax.class,  this);
	}

	public void dispose() throws ComponentException
	{
		// remove the service
		getContainer().revokeService(Frax.class, this, false);
	}

	public Iterator getCurrentServiceSelectors(BeanContextServices bcs,
											   Class serviceClass)
	{
		return null;
	}

	public Object getService(BeanContextServices bcs, Object requestor,
							 Class serviceClass, Object serviceSelector)
	{
		return frax;
	}

	public void releaseService(BeanContextServices bcs, Object requestor,
							   Object service)
	{
		; // do nothing
	}
}

