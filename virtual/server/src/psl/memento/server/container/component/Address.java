package psl.memento.server.container.component;

import psl.memento.server.util.Uid;

/**
 * Represents the address of a component within the network which can send and
 * receive events. At its finest level, the network is made up of components
 * which are constantly sending and receiving events. You can have multiple 
 * components within a single entity and multiple entities within a single 
 * computer. Every component is therefore identified by a 3-tuple consisting of
 * a unique component ID (assigned by the container), a unique entity id 
 * (assigned by the master server), and an IP address.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class Address
{
	private String ipAddress;
	private Uid entityId;
	private Uid componentId;
	
	/**
	 * Construct a new Address to represent some component on the 
	 * network.
	 * 
	 * @param ipAddress   IP address of the entity
	 * @param entityID    unique entity id for the entity
	 * @param componentId unique component id of the component that generated 
	 *                    this event
	 **/
	public Address(String ipAddress, Uid entityId, Uid componentId)
	{
		setIpAddress(ipAddress);
		setEntityId(entityId);
		setComponentId(componentId);
	}
	
	/**
	 * Get the IP address for this entity.
	 * 
	 * @return ip address of the entity
	 **/
	public String getIpAddress()
	{
		return ipAddress;
	}
	
	/**
	 * Set the IP address for this entity.
	 * 
	 * @param ipAddress IP address for this entity
	 **/
	void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}
	
	/**
	 * Get the entity ID for this entity. This is the unique ID assigned to 
	 * every entity within the network by the Master Server.
	 * 
	 * @return entity id of the entity
	 **/
	public Uid getEntityId()
	{
		return entityId;
	}
	
	/**
	 * Set the entity ID for this entity.
	 * 
	 * @param entityId entity id for this entity
	 **/
	void setEntityId(Uid entityId)
	{
		this.entityId = entityId;
	}
	
	/**
	 * Each entity can host multiple components and the entity/container is
	 * responsible for assigning every hosted component a component id which is
	 * unique to that container.
	 * 
	 * @return unique (to the container) component id
	 **/
	public Uid getComponentId()
	{
		return componentId;
	}
	
	/**
	 * Set the component id of the addressed component.
	 * 
	 * @param compid component id of the addressed component
	 **/
	public void setComponentId(Uid componentId)
	{
		this.componentId = componentId;
	}
}
