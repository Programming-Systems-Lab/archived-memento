package psl.memento.ether.component;

import psl.memento.ether.deploy.DeploymentDescriptor;
import psl.memento.ether.deploy.DeploymentException;
import psl.memento.ether.util.Uid;
import psl.memento.ether.event.TopicUrl;
import psl.memento.ether.event.ComponentUrl;
import psl.memento.ether.message.MessageUrl;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manages the components within a container and also aids in component
 * discovery.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class ComponentManager
{
   private List componentList = Collections.synchronizedList(new ArrayList());

	private Uid containerId;
	private String containerHost;
	private TopicUrl containerMsgTopic;

   /**
	 * Register a component deployment descriptor with the manager. This method
	 * also instantiates an instance of the component, its context, and makes
	 * them available in the container.
	 *
	 * @param deployDesc deployment descriptor for the component to instantiate
	 * @throws ComponentException
	 *         if the component couldn't be hosted
	 */
	public void instantiate(DeploymentDescriptor deployDesc)
		throws ComponentException
	{
		if (deployDesc == null)
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

      // instantiate the component
		Component component = null;
		try
		{
			component = (Component) deployDesc.getClassInfo().newInstance();
		}
		catch (DeploymentException de)
		{
			String msg = "couldn't deploy component " + deployDesc.getClassInfo();
			throw new ComponentException(msg, de);
		}

		// create its context
		ComponentContext compContext = new ComponentContext(this);
		compContext.setDeploymentDescriptor(deployDesc);

		// create an address for this component
		Uid componentId = new Uid();
		ComponentUrl compUrl =
				  new ComponentUrl(componentId, containerId, containerHost);
		compContext.setComponentUrl(compUrl);

		// create a messaging address for this component
      MessageUrl msgUrl = new MessageUrl(containerMsgTopic, compUrl);
		compContext.setMessageUrl(msgUrl);

		// set the component context
		component.setContext(compContext);

		// put it in the list
		componentList.add(component);
	}

   /**
	 * Remove a component from the container.
	 *
	 * @param comp Component to remove from the container
	 */
   private void remove(Component comp)
	{
      // shutdown the component
		comp.shutdown();

		// remove its context
		comp.setContext(null);

		// remove it from the list
		componentList.remove(comp);
	}

   /**
	 * Find a component which implements the given interface.
	 *
	 * @param c Class the component should implement
	 * @return Component which implements the given interface or
	 *         <code>null</code>
	 */
	public Component findComponent(Class c)
	{
		if (c == null)
		{
			String msg = "c can't be null";
			throw new IllegalArgumentException(msg);
		}

      // search for the first component with the given class
		for (Iterator iter = componentList.iterator(); iter.hasNext(); )
		{
			Component comp = (Component) iter.next();
			if (c.isInstance(comp))
			{
				return comp;
			}
		}

		return null;
	}

}
