package memento.world.model;

/**
 * Represents an active user in the world.
 *
 * @author Buko O. (buko@concedere.net)
 * @version 0.1
 **/
public interface Avatar extends LocatableWorldObject
{
	/**
	 * Get the GUID of the client this avatar represents.
	 *
	 * @return GUID of the client
	 */
	public String getClientGuid();

	/**
	 * Set the GUID of the client this avatar represents.
	 *
	 * @param guid GUID of the client
	 */
	public void setClientGuid(String guid);
}
