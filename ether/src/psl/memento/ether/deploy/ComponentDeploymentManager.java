package psl.memento.ether.deploy;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for deploying all components within the container at start-up as
 * well as deploying mobile components.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class ComponentDeploymentManager
{
   private File componentsDir;
	private File sharedResourcesDir;
	private File mobileComponentsDir;

	/**
	 * Construct a new ComponentDeploymentManager.
	 *
	 * @param componentsDir directory containing deployment descriptors for
	 *                      components to deploy
	 * @param sharedResourcesDir directory containing shared resources for all
	 *                           components
	 */
   public ComponentDeploymentManager(File componentsDir,
												 File sharedResourcesDir)
	{
      if ((componentsDir == null) || (sharedResourcesDir == null))
		{
			String msg = "no parameter can be null";
			throw new IllegalArgumentException(msg);
		}

      this.componentsDir = componentsDir;
		this.mobileComponentsDir = new File(componentsDir, "mobile");
		this.sharedResourcesDir = sharedResourcesDir;
	}

   /**
	 * Get an InputStream to a shared resource provided by the container.
	 *
	 * @param relPath relative path to the resource
	 * @throws DeploymentException
	 *         if the InputStream couldn't be created
	 */
	public InputStream getSharedResourceInputStream(String relPath)
		throws DeploymentException
	{
		if (relPath == null)
		{
			String msg = "relPath can't be null";
			throw new IllegalArgumentException(msg);
		}

      // append the relative path to the shared resources directory
      File resFile = new File(sharedResourcesDir, relPath);
		try
		{
			return new BufferedInputStream(new FileInputStream(resFile));
		}
		catch (IOException ioe)
		{
			String msg = "couldn't get InputStream to " + relPath;
			throw new DeploymentException(msg);
		}
	}

	/**
	 * Get an OutputStream to a shared resource provided by the container.
	 *
	 * @param relPath relative path to the resource
	 * @throws DeploymentException
	 *         if the OutputStream couldn't get downloaded
	 */
   public OutputStream getSharedResourceOutputStream(String relPath)
		throws DeploymentException
	{
		if (relPath == null)
		{
			String msg = "relPath can't be null";
			throw new IllegalArgumentException(msg);
		}

		// append the relative path to the shared resources directory
		File resFile = new File(sharedResourcesDir, relPath);
		try
		{
			return new BufferedOutputStream(new FileOutputStream(resFile));
		}
		catch (IOException ioe)
		{
			String msg = "couldn't get OutputStream to " + relPath;
			throw new DeploymentException(msg);
		}
	}

	/**
	 * Get the deployment descriptors for all the components currently deployed
	 * in the components directory.
	 *
	 * @return array of deployment descriptors for components in the
	 *         components directory
	 */
   public DeploymentDescriptor[] getDeploymentDescriptors()
	{
      List descriptorList = new ArrayList();

		// loop through the files in the components directory and try to
		// construct a deployment descriptor for each one
      String[] contents = componentsDir.list();

      // check for null, meaning there is no list
		if (contents == null)
		{
			String msg = "component directory isn't a directory";
			throw new IllegalStateException(msg);
		}

      File descriptorFile = null;
      DeploymentDescriptor deploy = null;

      for (int i = 0; i < contents.length; ++i)
		{
         descriptorFile = new File(componentsDir, contents[i]);
         deploy = getNewDescriptorForFile(descriptorFile);

			try
			{
            deploy.initialize(descriptorFile);
			}
			catch (DeploymentException de)
			{
            // log the bad descriptor

            // skip the bad descriptor
				continue;
			}

			descriptorList.add(deploy);
		}

		return (DeploymentDescriptor[])
			descriptorList.toArray(new DeploymentDescriptor[0]);
	}

	/**
	 * Get the correct DeploymentDescriptor type for a given file.
	 *
	 * @param file File containing info on how to deploy the descriptor
	 */
   private DeploymentDescriptor getNewDescriptorForFile(File file)
	{
      if (file.isDirectory())
		{
         return new FolderDeploymentDescriptor(this);
		}
		else if (file.getName().endsWith("xml"))
		{
			return new XmlDeploymentDescriptor(this);
		}
      else
		{
			// should never get here...
			String msg = "no deployment descriptor available for " + file;
			throw new IllegalArgumentException(msg);
		}
	}
}
