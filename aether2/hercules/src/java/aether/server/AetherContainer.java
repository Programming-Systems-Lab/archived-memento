package aether.server;

import aether.server.framework.Identifiable;
import aether.util.GuidFactory;
import net.concedere.dundee.ComponentException;
import net.concedere.dundee.DefaultContainer;

/**
 * Basic AetherContainer for hosting components.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class AetherContainer extends DefaultContainer
{
	protected void enforceLifeCycleBegin(Object o) throws ComponentException
	{
		super.enforceLifeCycleBegin(o);

		// enforce identifiable
		if (beans.isInstanceOf(o, Identifiable.class))
		{
			((Identifiable) beans.getInstanceOf(o, Identifiable.class))
					.setGuid(GuidFactory.createId());
		}
	}
}
