package psl.memento.server.container.event;

import psl.memento.server.util.Uid;

/**
 * Represents an entity within the network who can send and recieve events.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class NetworkEntity
{
	private String ipAddress;
	private Uid entityId;
	private TopicUrl address;
	
	/**
	 * Construct a new NetworkEntity to represent some component on the 
	 * network.
	 * 
	 * @param ipAddress IP address of the entity
	 * @param entityID  unique entity id for the entity
	 * @param address   address of the entity within the network
	 **/
	public NetworkEntity(String ipAddress, Uid entityId, TopicUrl address)
	{
		setIpAddress(ipAddress);
		setEntityId(entityId);
		setAddress(address);
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
	 * If an entity has a unique address within the network (a topic which only
	 * it is subscribed to) this retrieves the address.
	 * 
	 * @return address for this entity or <c>null</c>
	 **/
	public TopicUrl getAddress()
	{
		return address;
	}

	/**
	 * Set the address for this entity.
	 * 
	 * @param address for this entity
	 **/
	void setAddress(TopicUrl address)
	{
		this.address = address;
	}
}
