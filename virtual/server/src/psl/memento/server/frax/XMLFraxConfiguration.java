package psl.memento.server.frax; 

// jdk imports
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// non-jdk imports
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.*;
import org.xml.sax.SAXException;

public class XMLFraxConfiguration implements FraxConfiguration {
	private static final String kErrorInputStreamNull =
		"InputStream is null.";
  private static final String kErrorCouldNotFindClass =
    "Could not find class: ";

  private static final String kDefaultFileName = "etc/frax-config.xml";
  
  private static Log sLog = LogFactory.getLog(XMLFraxConfiguration.class);
  
  /** A mapping of URI schemes to extractor class names. */
  private Map mExtractorMap;
  
  /** A mapping of MIME types to content plug class names. */
  private Map mPlugMap;
  
  /** A mapping of URI schemes to extractor class objects. */
  private Map mExtractorClassMap;
  
  /** A mapping of MIME types to content plug class objects. */
  private Map mPlugClassMap;
  
  /** A mapping of file extensions to MIME types. */
  private Map mExtensionMap;
  
  /** The digester object which interprets the configuration data. */
  private Digester mDigester;

  public XMLFraxConfiguration() throws IOException, SAXException {
    this(kDefaultFileName);
  }
  
  public XMLFraxConfiguration(String iFilename)
      throws IOException, SAXException {
		this(new FileInputStream(iFilename));
	}

	public XMLFraxConfiguration(InputStream iIn)
      throws IOException, SAXException {
		if (iIn == null) {
			throw new NullPointerException(kErrorInputStreamNull);
    }		
    
    // create and configure the digester object
    mDigester = new Digester();
    mDigester.push(this);
    
    // add extractor rules
    mDigester.addObjectCreate("frax-config/extractors", HashMap.class);
    mDigester.addSetNext("frax-config/extractors", "setExtractorMap");
    mDigester.addCallMethod("frax-config/extractors/extractor", "put", 2);
    mDigester.addCallParam("frax-config/extractors/extractor", 0, "scheme");
    mDigester.addCallParam("frax-config/extractors/extractor", 1, "class");
    
    // add plug rules
    mDigester.addObjectCreate("frax-config/plugs", HashMap.class);
    mDigester.addSetNext("frax-config/plugs", "setPlugMap");
    mDigester.addCallMethod("frax-config/plugs/plug", "put", 2);
    mDigester.addCallParam("frax-config/plugs/plug", 0, "mimetype");
    mDigester.addCallParam("frax-config/plugs/plug", 1, "class");
    
    // add extension map rules
    mDigester.addObjectCreate("frax-config/extension-map", HashMap.class);
    mDigester.addSetNext("frax-config/extension-map", "setExtensionMap");
    mDigester.addCallMethod("frax-config/extension-map/map-entry", "put", 2);
    mDigester.addCallParam("frax-config/extension-map/map-entry", 0, "ext");
    mDigester.addCallParam("frax-config/extension-map/map-entry", 1,
      "mimetype");
    
    // parse the XML configuration data
    mDigester.parse(iIn);
    
    // initialize mappings from data we read in
    initExtractorClassMap();
    initPlugClassMap();
	}

	public Class getExtractorClass(String iScheme) {
		return (Class) mExtractorClassMap.get(iScheme);
	}
  
  public Class getPlugClass(String iContentType) {
    return (Class) mPlugClassMap.get(iContentType);
  }
  
  public String getMIMEType(String iExtension) {
    return (String) mExtensionMap.get(iExtension);
  }
  
  private void initExtractorClassMap() {
    mExtractorClassMap = new HashMap();
    if (mExtractorMap != null) {
      String scheme;
      String className;
      for (Iterator i = mExtractorMap.keySet().iterator(); i.hasNext(); ) {
        scheme = (String) i.next();
        className = (String) mExtractorMap.get(scheme);
        try {
          mExtractorClassMap.put(scheme, Class.forName(className));
        } catch (ClassNotFoundException ex) {
          sLog.warn(kErrorCouldNotFindClass + className);
        }
      }
    }
  }
  
  private void initPlugClassMap() {
    mPlugClassMap = new HashMap();
    if (mPlugMap != null) {
      String mimeType;
      String className;
      for (Iterator i = mPlugMap.keySet().iterator(); i.hasNext(); ) {
        mimeType = (String) i.next();
        className = (String) mPlugMap.get(mimeType);
        try {
          mPlugClassMap.put(mimeType, Class.forName(className));
        } catch (ClassNotFoundException ex) {
          sLog.warn(kErrorCouldNotFindClass + className);
        }
      }
    }
  }
  
  public void setExtractorMap(Map iMap) {
    mExtractorMap = iMap;
  }
  
  public void setPlugMap(Map iMap) {
    mPlugMap = iMap;
  }
  
  public void setExtensionMap(Map iMap) {
    mExtensionMap = iMap;
  }
}