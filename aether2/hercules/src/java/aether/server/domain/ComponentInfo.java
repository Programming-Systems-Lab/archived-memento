package aether.server.domain;

import java.io.Serializable;

/**
 * Tracks basic information about components deployed within the aether
 * network.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public class ComponentInfo implements Serializable
{
    private Advertisement advertisement;
	private String componentId;
	private String type;

	public Advertisement getAdvertisement()
	{
		return advertisement;
	}

	public void setAdvertisement(Advertisement advertisement)
	{
		this.advertisement = advertisement;
	}

	public String getComponentId()
	{
		return componentId;
	}

	public void setComponentId(String componentId)
	{
		this.componentId = componentId;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String toString()
	{
		return getClass() + "[type=" + type + ", componentId=" + componentId
			+ ", advertisement=" + advertisement + "]";
	}
}
