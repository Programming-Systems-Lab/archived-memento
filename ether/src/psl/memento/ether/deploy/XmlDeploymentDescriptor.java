package psl.memento.ether.deploy;

import org.dom4j.*;
import org.dom4j.io.*;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Iterator;

/**
 * Represents an XML file used to deploy a component.
 *
 * @author Buko O. (buko@cs.columbia.edu)
 * @version 0.1
 */
public class XmlDeploymentDescriptor extends DeploymentDescriptor
{

   /**
	 * Construct a new XmlDeploymentDescriptor.
	 *
	 * @param man ComponentDeploymentManager which created this descriptor
	 */
   public XmlDeploymentDescriptor(ComponentDeploymentManager man)
	{
		super(man);
	}

   /**
	 * Initialize an XmlDeploymentDescriptor.
	 *
	 * @param parameter File object representing the Xml descriptor document
	 * @throws DeploymentException
	 *         if the deployment descriptor is invalid
	 */
   public void initialize(Object parameter) throws DeploymentException
	{
      if ((parameter == null) || !(parameter instanceof File))
		{
         String msg = "parameter is null or not a file";
			throw new IllegalArgumentException(msg);
		}

      File xmlFile = (File) parameter;
      Document document = null;

		// try to parse the file
		try
		{
         SAXReader reader = new SAXReader();
         document = reader.read(xmlFile);
		}
		catch (Exception de)
		{
         String msg = "couldn't parse xml file " + xmlFile;
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
	 * Components deployed with XML deployment descriptors aren't allowed to
	 * have external resources so this method always throws a
	 * DeploymentException.
	 *
	 * @param relPath ignored
	 * @throws DeploymentException
	 *         always
	 */
	public InputStream getResourceInputStream(String relPath)
		throws DeploymentException
	{
		String msg = "component deployed with xml deployment descriptor, no" +
			" external resources allowed";
		throw new DeploymentException(msg);
	}

	/**
	 * Components deployed with XML deployment descriptors aren't allowed to
	 * have external resources so this method always throws a
	 * DeploymentException.
	 *
	 * @param relPath ignored
	 * @throws DeploymentException
	 *         always
	 */
   public OutputStream getResourceOutputStream(String relPath)
		throws DeploymentException
	{
		String msg = "component deployed with xml deployment descriptor, no" +
			" external resources allowed";
		throw new DeploymentException(msg);
	}
}
