package psl.memento.server.model.component;

/**
 * The Component interface is the common interface to all the managed components
 * which are hosted by a chime server. Through this interfaced managed 
 * components can access all of the services provided by the chime container.
 * 
 * @author Buko Obele (buko@cs.columbia.edu)
 * @version 0.1
 */
public interface Component
{
	/**
	 * Retrieve the ComponentContext which provides access to the server
	 * resources to the managed component. Even if a resource is pooled and
	 * exists across multiple instances they will all share the same 
	 * component.
	 * 
	 * @return ComponentContext which abstracts the chime container
	 **/
	public ComponentContext getContext();
	
	/**
	 * Set the ComponentContext which provides access to the server resources
	 * to the managed component. Only the chime container will call this 
	 * method.
	 * 
	 * @param ctx ComponentContext which abstracts the server
	 **/
	public void setContext(ComponentContext ctx);
	
	/**
	 * Retrieve the ComponentLifecycle which describes the component's behavior
	 * as it passes through life cycle stages.
	 * 
	 * @return ComponentLifeCycle object which describes life cycle behavior
	 **/
	public ComponentLifeCycle getLifeCycle();

}