package psl.memento.ether.deploy;

import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a file which describes how to host a component within a given
 * container.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public abstract class DeploymentDescriptor
{
   protected Map paramMap = new HashMap();
   protected ClassInfo classInfo;
   private ComponentDeploymentManager manager;

   /**
	 * Construct a new DeploymentDescriptor.
	 *
	 * @param manager ComponentDeploymentManager object which constructed this
	 *                descriptor
	 */
   protected DeploymentDescriptor(ComponentDeploymentManager manager)
	{
      if (manager == null)
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

      this.manager = manager;
	}

	/**
	 * Initialize the deployment descriptor. Depending on the type of the
	 * descriptor, the parameter may be a File or InputStream.
	 *
	 * @param parameter parameter used to initialize the descriptor
	 * @throws DeploymentException
	 *         if the parameter is invalid
	 */
	public abstract void initialize(Object parameter)
		throws DeploymentException;

   /**
	 * Get information about the class of the component being deployed.
	 *
	 * @return ClassInfo object which describes the component's class
	 */
	public ClassInfo getClassInfo()
	{
		return classInfo;
	}

	/**
	 * Get the value of a parameter defined in the descriptor.
	 *
	 * @param name name of the parameter defined in the descriptor
	 * @return value of the parameter with <code>name</code> or <code>null</code>
	 */
   public String getParameter(String name)
	{
		synchronized (paramMap)
		{
			return (String) paramMap.get(name);
		}
	}

   /**
	 * Retreive a shared resourced provided by the container to all deployed
	 * components.
	 *
	 * @param relPath relative path to the shared resource
	 * @throws DeploymentException
	 *         if the resource couldn't be retreived
	 */
	public InputStream getSharedResourceInputStream(String relPath)
		throws DeploymentException
	{
      return manager.getSharedResourceInputStream(relPath);
	}

	/**
	 * Retreieve an output stream to a shared resource provided by the container.
	 *
	 * @param relPath relative path to the shared resource
	 * @throws DeploymentException
	 *         if the output stream couldn't be retreived
	 */
	public OutputStream getSharedResourceOutputStream(String relPath)
		throws DeploymentException
	{
		return manager.getSharedResourceOutputStream(relPath);
	}

	/**
	 * Retrieve an InputStream to a resource provided by the deployment
	 * descriptor.
	 *
	 * @param relPath relative path to the resource
	 * @throws DeploymentException
	 *         if the inputstream couldn't be retreieved
	 */
   public abstract InputStream getResourceInputStream(String relPath)
		throws DeploymentException;

	/**
	 * Retrieve an OutputStream to a resource provided by the deployment
	 * descriptor.
	 *
	 * @param relPath relative path to the resource
	 * @throws DeploymentException
	 *         if the outputstream couldn't be retreived
	 */
   public abstract OutputStream getResourceOutputStream(String relPath)
		throws DeploymentException;
}
