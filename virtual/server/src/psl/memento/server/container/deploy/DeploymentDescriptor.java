package psl.memento.server.container.deploy;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Represents an object which describes how a component should be deployed 
 * within the container.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class DeploymentDescriptor
{
	protected Map paramMap;
	protected String className;
	private ComponentDeploymentManager manager;
	
	/**
	 * Construct a new DeploymentDescriptor.
	 * 
	 * @param paramMap deployment parameters for the component
	 **/
	protected DeploymentDescriptor(ComponentDeploymentManager man)
	{
		if (man == null)
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.manager = man;
	}
	
	/**
	 * Initialize the deployment descriptor. The parameter depends on the type
	 * of the deployment descriptor.
	 * 
	 * @param parameter parameter used to intiialize the deployment descriptor
	 * @throws DeploymentException
	 *         if the deployment descriptor is invalid
	 **/
	public abstract void initialize(Object parameter) 
		throws DeploymentException;
	
	/**
	 * Get the class of the component described by this descriptor.
	 * 
	 * @return class of the component described by this descriptor
	 * @throws DeploymentException
	 *         if the class of the component couldn't be loaded
	 **/
	public Class getComponentClass() throws DeploymentException
	{
		try
		{
			return Thread.currentThread().getContextClassLoader().
				loadClass(className);
		}
		catch (ClassNotFoundException cnfe)
		{
			String msg = "couldn't load the component class " + className;
			throw new DeploymentException(msg, cnfe);
		}
	}
	
	/**
	 * Get a parameter defined in the deployment descriptor.
	 * 
	 * @param name name of the parameter
	 * @return value of the parameter or <c>null</c>
	 **/
	public String getParameter(String name)
	{
		return (String) paramMap.get(name);
	}
	
	/**
	 * Retreive a shared resource provided by the container to all components.
	 * 
	 * @param path path to the resource to retreive
	 * @throws DeploymentException
	 *         if the resource couldn't be retrieved
	 **/
	public InputStream getSharedResourceInputStream(String path)
		throws DeploymentException
	{
		return manager.getSharedResourceInputStream(path);
	}
	
	/**
	 * Retrieve an output stream to a shared resourced provided by the
	 * container.
	 * 
	 * @param path path to the resource to retrieve for writing
	 * @throws DeploymentException
	 *         if the shared resource can't be written to
	 **/
	public OutputStream getSharedResourceOutputStream(String path)
		throws DeploymentException
	{
		return manager.getSharedResourceOutputStream(path);
	}
	
	/**
	 * Retrieve an input stream to a resource provided for this component.
	 * 
	 * @param path path to the resource
	 * @throws DeploymentException
	 *         if the resource can't be retreived
	 **/
	public abstract InputStream getResourceInputStream(String path)
		throws DeploymentException;
	
	/**
	 * Retrieve an output stream to a resource provided for this component.
	 * 
	 * @param path path to the resource
	 * @throws DeploymentException 
	 *         if the output stream can't be retrieved
	 **/
	public abstract OutputStream getResourceOutputStream(String path)
		throws DeploymentException;
}
