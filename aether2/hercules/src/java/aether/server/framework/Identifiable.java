package aether.server.framework;

/**
 * Implemented by objects that have a globally unique id.
 *
 * @author Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public interface Identifiable
{
	/**
	 * Get the globally unique ID of this object.
	 *
	 * @return globally unique ID of this object
	 */
	public String getGuid();

	/**
	 * Set the globally unique ID of this object
	 *
	 * @param guid Globally unique id of this object
	 */
	public void setGuid(String guid);
}
