package psl.memento.server.container.component;

/**
 * Defines the funamental managed component of the container.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public interface Component
{
	/**
	 * Get the address which has been assigned to this component. The address
	 * consists of a unique component id assigned by the server, the entity
	 * id of the container of the component (either a client or server), and 
	 * the IP address of the machine hosting the entity.
	 * 
	 * @return address of the component
	 **/
	public Address getAddress();

	/**
	 * Set the globally unique address of this component.
	 * 
	 * @param addr address of this component
	 **/
	public void setAddress(Address addr);
}
