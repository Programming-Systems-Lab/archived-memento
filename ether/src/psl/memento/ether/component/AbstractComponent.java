package psl.memento.ether.component;

/**
 * An abstract implementation of the Component interface which provides
 * reasonable implementations of its methods.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class AbstractComponent implements Component
{
   private ComponentContext context;

	/**
	 * Initialize the component. This implementation of the method does
	 * nothing.
	 *
	 * @throws ComponentException
	 *         never
	 */
	public void initialize() throws ComponentException
	{
		; // do nothing
	}

	/**
	 * Start the component. This implementation of the method does nothing.
	 */
	public void startup()
	{
		; // do nothing
	}

	/**
	 * Shutdown the component. This implementation of the method does nothing.
	 */
	public void shutdown()
	{
		; // do nothing
	}

	/**
	 * Get the component's context.
	 *
	 * @return component's context
	 */
	public ComponentContext getContext()
	{
		return context;
	}

	/**
	 * Set the component's context.
	 *
	 * @param ctx Component context
	 */
	public void setContext(ComponentContext ctx)
	{
		this.context = ctx;
	}
}
