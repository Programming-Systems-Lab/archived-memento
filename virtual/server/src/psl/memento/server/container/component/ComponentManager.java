package psl.memento.server.container.component;

import java.util.*;

import psl.memento.server.container.Container;
import psl.memento.server.container.event.Topic;
import psl.memento.server.util.Uid;

/**
 * Used by Components within a Container to find other Components.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class ComponentManager
{
	private Container container;
	private List componentList = Collections.synchronizedList(new ArrayList());
	
	/**
	 * Construct a new component manager.
	 * 
	 * @param container Container which created this component manager
	 **/
	public ComponentManager(Container container)
	{
		if (container == null)
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.container = container;
	}
	
	/**
	 * Register a component with the component manager.
	 * 
	 * @param comp component to register with the component manager
	 **/
	public void register(Component comp)
	{
		if (comp == null)
		{
			String msg = "comp can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		// assign the component an address
		Uid componentId = new Uid();
		Address addr = new Address(container.getIpAddress(), 
			container.getEntityId(), componentId, 
			container.getMessagingTopic());
		comp.setAddress(addr);
		
		// add it to the list
		componentList.add(comp);
	}
	
	/**
	 * Unregister a component with the component manager.
	 * 
	 * @param comp component to unregister from the component manager
	 **/
	public void unregister(Component comp)
	{
		if (comp == null)
		{
			String msg = "component can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		// nullify the address
		comp.setAddress(null);
		
		// now remove it from the list
		componentList.remove(comp);
	}
	
	/**
	 * Retrieve the first component found with the given class.
	 * 
	 * @param c class of the component to retrieve
	 * @return first component found with the given class or <c>null</c>
	 **/
	public Component get(Class c)
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