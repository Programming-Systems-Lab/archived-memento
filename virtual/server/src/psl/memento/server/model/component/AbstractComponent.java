package psl.memento.server.model.component;

/**
 * Provides a basic implementation of the Component and ComponentLifeCycle interfaces.
 * 
 * @author Buko Obele (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class AbstractComponent implements Component, ComponentLifeCycle
{
	private ComponentContext ctx;

	/**
	 * @see psl.memento.server.model.component.Component#getContext()
	 */
	public ComponentContext getContext()
	{
		return ctx;
	}

	/**
	 * @see psl.memento.server.model.component.Component#setContext(ComponentContext)
	 */
	public void setContext(ComponentContext ctx)
	{
		this.ctx = ctx;
	}

	/**
	 * Default implementation of <c>getLifeCycle</c> which returns a reference to the
	 * <c>this</c> object.
	 * 
	 * @see psl.memento.server.model.component.Component#getLifeCycle()
	 */
	public ComponentLifeCycle getLifeCycle()
	{
		return this;
	}

	/**
	 * Default implementation of initialize which does nothing.
	 * 
	 * @see psl.memento.server.model.component.ComponentLifeCycle#initialize()
	 */
	public void initialize() throws ComponentException
	{
		; // do nothing
	}

	/**
	 * Default implementation of destroy which does nothing.
	 * 
	 * @see psl.memento.server.model.component.ComponentLifeCycle#destroy()
	 */
	public void destroy()
	{
		; // do nothing
	}

}
