package psl.memento.ether.component;

import psl.memento.ether.event.ComponentUrl;
import psl.memento.ether.message.MessageUrl;
import psl.memento.ether.deploy.DeploymentDescriptor;

/**
 * Represents a component's interface to the container.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class ComponentContext
{
	private ComponentManager compManager;
	private ComponentUrl componentUrl;
	private MessageUrl messageUrl;
	private DeploymentDescriptor deployDescriptor;

	/**
	 * Construct a new ComponentContext.
	 *
	 * @param compManager Container's ComponentManager that created this context
	 */
	public ComponentContext(ComponentManager compManager)
	{
		if (compManager == null)
		{
			String msg = "compManager can't be null";
			throw new IllegalArgumentException(msg);
		}

		this.compManager = compManager;
	}

   /**
	 * Get the component's URL as assigned by the container.
	 *
	 * @return component's URL
	 */
	public ComponentUrl getComponentUrl()
	{
		return componentUrl;
	}

	/**
	 * Set the component's URL.
	 *
	 * @param compUrl component's container-assigned URL
	 */
	public void setComponentUrl(ComponentUrl compUrl)
	{
		this.componentUrl = compUrl;
	}

	/**
	 * Get the component's message URL as assigned by the container.
	 *
	 * @return component's message URL
	 */
	public MessageUrl getMessageUrl()
	{
		return messageUrl;
	}

	/**
	 * Set the component's message URL.
	 *
	 * @param msgUrl message URL for the component
	 */
	public void setMessageUrl(MessageUrl msgUrl)
	{
		this.messageUrl = msgUrl;
	}

	/**
	 * Get the component's deployment descriptor.
	 *
	 * @return deployment descriptor for the component
	 */
	public DeploymentDescriptor getDeploymentDescriptor()
	{
		return deployDescriptor;
	}

	/**
	 * Set the component's deployment descriptor.
	 *
	 * @param deployDesc deployment descriptor for the component
	 */
	public void setDeploymentDescriptor(DeploymentDescriptor deployDesc)
	{
		this.deployDescriptor = deployDesc;
	}

   /**
	 * Find another component hosted within the container.
	 *
	 * @param c Class of the component to find
	 * @return Component which implements the given interface or
	 *         <code>null</code>
	 */
	public Component findComponent(Class c)
	{
		return compManager.findComponent(c);
	}

}
