package psl.memento.server.container.deploy;

import java.io.*;
import java.util.*;

/**
 * Object responsible for deploying all components within a container.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class ComponentDeploymentManager
{
	private File componentDir;
	private File sharedResourcesDir;
	
	/**
	 * Construct a new component deployment manager.
	 * 
	 * @param componentDir directory containing the component deployment 
	 *                     descriptors
	 * @param resourcesDir directory containing the shared resources for the
	 *                     container
	 **/
	public ComponentDeploymentManager(File componentDir, File resourcesDir)
	{
		if ((componentDir == null) || (resourcesDir == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}
		
		this.componentDir = componentDir;
		this.sharedResourcesDir = resourcesDir;
	}
	
	/**
	 * Retrieve the deployment descriptors for all the components with 
	 * deployment descriptors currently in the components directory of the
	 * container.
	 * 
	 * @return array of deployment descriptors of the components currently 
	 *         in the components directory
	 **/
	public DeploymentDescriptor[] getDeploymentDescriptors() 
	{
		List descriptorList = new ArrayList();
		
		// loop through the components directory
		String[] contents = componentDir.list();
		File descriptorFile = null;
		DeploymentDescriptor deploy = null;
		for (int i = 0; i < contents.length; ++i)
		{
			descriptorFile = new File(componentDir, contents[i]);
			
			// if it's a directory create a FolderDeploymentDescriptor
			if (descriptorFile.isDirectory())
			{
				deploy = new FolderDeploymentDescriptor(this);
				try
				{
					deploy.initialize(descriptorFile);
				}
				catch (DeploymentException de)
				{
					// log the bad descriptor and don't load the component
					continue; // skip to the next file
				}
			} 
			else if (descriptorFile.getName().endsWith("xml"))
			{
				// if it's an xml file attempt to load the xml deployment desc
				deploy = new XmlDeploymentDescriptor(this);
				try
				{
					deploy.initialize(descriptorFile);
				}
				catch (DeploymentException de)
				{
					continue;
				}
			}
			
			// add the deployent descriptor to the list
			descriptorList.add(deploy);
		}
		
		return (DeploymentDescriptor[]) 
			descriptorList.toArray(new DeploymentDescriptor[0]);	
	}
	
	/**
	 * Get an inputstream to a shared resource provided by the container. 
	 * 
	 * @param path path to the resource to retreive
	 * @throws DeploymentException
	 *         if the shared resource can't be retreived
	 **/
	public InputStream getSharedResourceInputStream(String path) 
		throws DeploymentException
	{
		if (path == null)
		{
			String msg = "path can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		// append resName to the shared resources directory
		File resFile = new File(sharedResourcesDir, path);
		
		try
		{
			return new BufferedInputStream(new FileInputStream(resFile));
		}
		catch (Exception e)
		{
			String msg = "couldn't get shared resource " + path;
			throw new DeploymentException(msg, e);
		}
	}
	
	/**
	 * Get an OutputStream to a shared resource.
	 * 
	 * @param path path to the resource
	 * @throws DeploymentException
	 *         if the OutputStream couldn't be retrieved
	 **/
	public OutputStream getSharedResourceOutputStream(String path)
		throws DeploymentException
	{
		if (path == null)
		{
			String msg = "resName can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		// append resName to the shared resources directory
		File resFile = new File(sharedResourcesDir, path);
		
		try
		{
			return new BufferedOutputStream(new FileOutputStream(resFile));
		}
		catch (Exception e)
		{
			String msg = "could write shared resource " + path;
			throw new DeploymentException(msg, e);
		}
	}
	

}
