package psl.memento.server.model.component;

/**
 * Describes a managed Component's life cycle behavior. As the container manages
 * the component it will call these methods to let the component respond to 
 * life cycle changes.
 * 
 * @author Buko Obele (buko@cs.columbia.edu)
 * @version 0.1
 */
public interface ComponentLifeCycle
{
	/**
	 * Called when a component is first created. If a component is pooled, this
	 * method will be called for each instance of the component. If a component
	 * cannot load, it should throw a ComponentException to indicate that 
	 * further action is necessary.
	 * 
	 * @throws ComponentException
	 *         if the component cannot load
	 **/
	public void initialize() throws ComponentException;
	
	/**
	 * Called when a managed component is being destroyed to never be used 
	 * again. If a component is pooled, this method will be called for each
	 * instance of the component.
	 **/
	public void destroy();

}
