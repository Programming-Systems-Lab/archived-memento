package aether.server.domain;

/**
 * Defines a registry for known components within the event network.
 *
 * todo: A ComponentRegistry should have its own listener and fire events when
 * --- a component registers and unregisters
 *
 * Buko O. (aso22@columbia.edu)
 * @version 0.1
 **/
public interface ComponentRegistry
{
    /**
     * Register a component within the registry.
     *
     * @param guid guid of the component
     * @param info info about the component
     * @throws RegistryException
     *         if something goes wrong
     */
    public void register(String guid, ComponentInfo info)
            throws RegistryException;

    /**
     * Unregister a component.
     *
     * @param guid GUID of the component
     * @throws RegistryException
     *         if something goes wrong
     */
    public void unregister(String guid) throws RegistryException;

    /**
     * Get the info about a component.
     *
     * @param guid GUID of the component to get the info for
     * @return info for the component
     * @throws RegistryException
     *         if the info can't be retreived
     */
    public ComponentInfo getInfo(String guid) throws RegistryException;
}
