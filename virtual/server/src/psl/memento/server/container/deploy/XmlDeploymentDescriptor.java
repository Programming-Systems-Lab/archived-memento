package psl.memento.server.container.deploy;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.util.*;
import java.io.*;

/**
 * Represents an XML file used to deploy a component.
 * 
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class XmlDeploymentDescriptor extends DeploymentDescriptor
{
	private File resourceBaseDir;
	
	/**
	 * Construct a new XmlDeploymentDescriptor.
	 * 
	 * @param man ComponentDeploymentManager which created this descriptor
	 **/
	public XmlDeploymentDescriptor(ComponentDeploymentManager man)
	{
		super(man);
	}

	/**
	 * Initialize an XmlDeploymentDescriptor.
	 * 
	 * @param parameter File object representing the XML file containing the
	 *                  deployment descriptor
	 * @throws DeploymentException
	 *         if the deployment descriptor is malformed
	 **/
	public void initialize(Object parameter) throws DeploymentException
	{
		if ((parameter == null) || !(parameter instanceof File))
		{
			String msg = "parameter is null or not a file";
			throw new IllegalArgumentException(msg);
		}
		
		File xmlFile = (File) parameter;
		
		Document document = null;
		
		try
		{
			SAXReader reader = new SAXReader();
			document = reader.read(xmlFile);
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
	
		// get the resource base
		Attribute resBaseDir = 
			(Attribute) document.selectSingleNode("/deploy/resources/@base");
		resourceBaseDir = new File(resBaseDir.getValue());
		
		// get the class of the component
		Element classElem = 
			(Element) document.selectSingleNode("/deploy/component/class");
		this.className = classElem.getText();
	}
	
	/**
	 * Retrieve an input stream to a resource defined by the XML deployment
	 * descriptor. The path to the resource will be appended to the resource
	 * base directory defined in the deployment descriptor.
	 * 
	 * @param path path to the resource
	 * @throws DeploymentException
	 *         if the resource can't be found or is unavailable
	 **/
	public InputStream getResourceInputStream(String path) 
		throws DeploymentException
	{
		if (path == null)
		{
			String msg = "path can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		// append the path to the base dir	
		File resFile = new File(resourceBaseDir, path);
		
		try
		{
			return new BufferedInputStream(new FileInputStream(resFile));
		}
		catch (IOException ie)
		{
			String msg = "couldn't access component resource " + path;
			throw new DeploymentException(msg, ie);
		}
	}
	
	/**
	 * Retrieve an output stream to a component resource. The path to the 
	 * resource will be appended to the resource base directory defined in the
	 * deployment descriptor file.
	 * 
	 * @param path path to the resource
	 * @throws DeploymentException
	 *         if the resource can't be written to
	 **/
	public OutputStream getResourceOutputStream(String path)
		throws DeploymentException
	{
		if (path == null)
		{
			String msg = "path can't be null";
			throw new IllegalArgumentException(msg);
		}
		
		// append the path to the resource base dir
		File resFile = new File(resourceBaseDir, path);
		
		try
		{
			return new BufferedOutputStream(new FileOutputStream(resFile));
		}
		catch (IOException ie)
		{
			String msg = "couldn't write to resource " + path;
			throw new DeploymentException(msg, ie);
		}
	}
}
