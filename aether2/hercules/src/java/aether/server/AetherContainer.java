package aether.server;

import net.concedere.dundee.DefaultContainer;
import net.concedere.dundee.ComponentException;
import aether.server.framework.Identifiable;
import aether.util.GuidFactory;

/**
 * Basic AetherContainer for hosting components.
 *
 * @author Buko O. (buko@concedere.net)
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
