package psl.memento.ether.component;

/**
 * Defines a managed component hosted within a container.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public interface Component
{

	/**
	 * Initialize the component. Before this method is called, the component's
	 * context has been initialized and all components have been deployed in
	 * the container.
	 *
	 * @throws ComponentException
	 *         if the container does not provide all the services the component
	 *         needs
	 */
	public void initialize() throws ComponentException;

   /**
	 * Start the component. Called after the initialize method.
	 */
	public void startup();

	/**
	 * Shutdown the component.
	 */
	public void shutdown();

	/**
	 * Get the componen't context.
	 *
	 * @return component's context
	 */
	public ComponentContext getContext();

	/**
	 * Set the component's context.
	 *
	 * @param context Component's context
	 */
	public void setContext(ComponentContext context);
}
