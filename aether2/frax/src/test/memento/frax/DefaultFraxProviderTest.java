package memento.frax;

import net.concedere.dundee.Container;
import net.concedere.dundee.DefaultContainer;

import java.beans.beancontext.BeanContextChildSupport;
import java.net.URI;

import psl.memento.server.frax.Frax;
import com.hp.hpl.mesa.rdf.jena.model.Model;

/**
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public class DefaultFraxProviderTest extends FraxTestCase
{
    public void testDefaultFraxProvider() throws Exception
	{
        Container container = new DefaultContainer();
       	DefaultFraxProvider fraxProvider = new DefaultFraxProvider();
		container.add(fraxProvider);

		assertTrue(container.hasService(Frax.class));

		BeanContextChildSupport bccs = new BeanContextChildSupport();
		container.add(bccs);

		Frax frax = (Frax) container.getService(bccs, bccs, Frax.class,
												bccs, bccs);

		assertNotNull(frax);

		// use frax?
		//Model model = frax.extractMetadata(new URI("http://www.yahoo.com"));
		//System.out.println("model is: " + model);

        container.releaseService(bccs, bccs, frax);

		container.remove(fraxProvider);

		assertFalse(container.hasService(Frax.class));
	}
}
