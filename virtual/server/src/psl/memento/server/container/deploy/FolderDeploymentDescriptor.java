package psl.memento.server.container.deploy;

import org.dom4j.*;
import org.dom4j.io.*;

import java.io.*;
import java.util.*;

/**
 * Represents when a component is deployed in folder in the server's components
 * directory. In this case, all the component resources are defined in 
 * subdirectories of the folder. The folder must contain a special file 
 * 'deploy.xml' which containts configuration parameters and the such for the
 * component.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class FolderDeploymentDescriptor extends DeploymentDescriptor
{
	private File compDir;
	
	/**
	 * Construct a new folder deployment descriptor.
	 * 
	 * @param man deployment manager object which created this descriptor
	 **/
	public FolderDeploymentDescriptor(ComponentDeploymentManager man)
	{
		super(man);
	}

	/**
	 * Initialize the folder deployment descriptor.
	 * 
	 * @param param File representing the folder which contains the component
	 * @throws DeploymentException
	 *         if the descriptor is invalid
	 **/
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
			String msg = "couldn't read xml file";
			throw new DeploymentException(msg, de);
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
		
		// get the class of the component
		Element classElem = 
			(Element) document.selectSingleNode("/deploy/component/class");
		this.className = classElem.getText();
	}
	
	/**
	 * Retrieve a component resource from the folder deployment descriptor.
	 * The path to the resource will be appended to the folder which the 
	 * component was deployed in.
	 * 
	 * @param path path to the resource
	 * @throws DeploymentException
	 *         if the resource couldn't be retrieved or doesn't exist
	 **/
	public InputStream getResourceInputStream(String path)
		throws DeploymentException
	{
		if (path == null)
		{
			String msg = "path can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		// append the path to the component directory
		File resFile = new File(compDir, path);
		
		try
		{
			return new BufferedInputStream(new FileInputStream(resFile));
		}
		catch (IOException ioe)
		{
			String msg = "couldn't open resource " + path;
			throw new DeploymentException(msg, ioe);
		}
	}
	
	/**
	 * Retrieve a component resource from the folder deployment descriptor. The
	 * path to the resource will be appended to the folder the component is
	 * deployed in.
	 * 
	 * @param path path to the resource
	 * @throws DeploymentException
	 *         if the outputstream couldn't be opened
	 **/
	public OutputStream getResourceOutputStream(String path)
		throws DeploymentException
	{
		if (path == null)
		{
			String msg = "path can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		// append the path to the component dir
		File resFile = new File(compDir, path);
		
		try
		{
			return new BufferedOutputStream(new FileOutputStream(resFile));
		}
		catch (IOException ie)
		{
			String msg = "couldn't open outputstream to resource " + path;
			throw new DeploymentException(msg, ie);
		}
	}
}
