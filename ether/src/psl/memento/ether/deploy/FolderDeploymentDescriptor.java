package psl.memento.ether.deploy;

import org.dom4j.*;
import org.dom4j.io.*;

import java.io.*;
import java.util.*;

/**
 * Represents a component deployed in a folder. In this case, all component
 * resources are deployed in subdirectories of the folder. The folder must
 * contain a special file 'deploy.xml' which contains configuration parameters
 * and class information for the component.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */

public class FolderDeploymentDescriptor extends DeploymentDescriptor
{
   private File compDir;

   /**
	 * Construct a new FolderDeploymentDescriptor.
	 *
	 * @param cdm ComponentDeploymentManager deploying the component
	 */
	public FolderDeploymentDescriptor(ComponentDeploymentManager cdm)
	{
		super(cdm);
	}

   /**
	 * Initialize the folder deployment descriptor.
	 *
	 * @param param File representing the component folder
	 * @throws DeploymentException
	 *         if the descriptor is invalid
	 */
   public void initialize(Object param) throws DeploymentException
	{
      if ((param == null) || !(param instanceof File))
		{
         String msg = "param must be a File object";
			throw new IllegalArgumentException(msg);
		}

      compDir = (File) param;

		// process the deploy.xml file
      File deployXml = new File(compDir, "deploy.xml");
      Document document = null;

		try
		{
         SAXReader reader = new SAXReader();
			document = reader.read(deployXml);
		}
		catch (Exception de)
		{
			String msg = "couldn't process deploy.xml";
			throw new DeploymentException(msg);
		}

      // get all the parameter nodes and process them
      List parameters = document.selectNodes("/deploy/parameters/param");
		for (Iterator iter = parameters.iterator(); iter.hasNext(); )
		{
			Element elem = (Element) iter.next();
			String name = elem.attributeValue("name");
			String val = elem.attributeValue("value");
			paramMap.put(name, val);
		}

      // get the class info
      Element classInfoElem =
         (Element) document.selectSingleNode("/deploy/component/class-info/name");
      this.classInfo = new ClassInfo(classInfoElem.getText());
	}

   /**
	 * Retreive an input stream to a component resource from the deployment
	 * folder.
	 *
	 * @param relPath relative path to the resource
	 * @throws DeploymentException
	 *         if the resource doesn't exist or can't be retreived
	 */
   public InputStream getResourceInputStream(String relPath)
		throws DeploymentException
	{
		if (relPath == null)
		{
         String msg = "relPath can't be null";
			throw new IllegalArgumentException(msg);
		}

		// append the relative path to the component directory
      File resFile = new File(compDir, relPath);
      try
		{
			return new BufferedInputStream(new FileInputStream(resFile));
		}
		catch (IOException ioe)
		{
			String msg = "couldn't get input stream to " + relPath;
			throw new DeploymentException(msg, ioe);
		}
	}

   /**
	 * Get an OutputStream to a resource provided in the component's folder.
	 *
	 * @param relPath relative path to the resource
	 * @throws DeploymentException
	 *         if the OutputStream couldn't be generated
	 */
	public OutputStream getResourceOutputStream(String relPath)
		throws DeploymentException
	{
		if (relPath == null)
		{
			String msg = "relPath can't be null";
			throw new IllegalArgumentException(msg);
		}

		// append the relative path to the component directory
      File resFile = new File(compDir, relPath);
		try
		{
			return new BufferedOutputStream(new FileOutputStream(resFile));
		}
		catch (IOException ioe)
		{
			String msg = "couldn't get output stream to " + relPath;
			throw new DeploymentException(msg);
		}
	}


}
