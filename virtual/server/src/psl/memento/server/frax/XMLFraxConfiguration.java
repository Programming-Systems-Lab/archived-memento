package psl.memento.server.frax; 

// jdk imports
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.*;

// non-jdk imports
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.*;
import org.xml.sax.SAXException;

public class XMLFraxConfiguration implements FraxConfiguration {
	private static final String kErrorInputStreamNull =
		"InputStream is null.";
  private static final String kErrorCouldNotFindClass =
    "Could not find class: ";

  private static final String kDefaultFileName = "etc/frax/frax-config.xml";
  private static final String[] kZeroString = new String[0];
  
  private static Log sLog = LogFactory.getLog(XMLFraxConfiguration.class);
  
  private Map mExtractorClassMap;
  private Map mPlugClassMap;
  private Map mExtensionMap;
  private Map mClassDepMap;
  private Map mDBMSVendorMap;
  private String mOracleHostName;  
  private int mOraclePort;
  private String mMetadataCacheVendor;
  private String mMetadataCacheJenaDBType;
  private String mMetadataCacheLocation;
  
  /** The digester object which interprets the configuration data. */
  private Digester mDigester;

  public XMLFraxConfiguration() throws IOException, SAXException {
    this(kDefaultFileName);
  }
  
  public XMLFraxConfiguration(String iFilename)
      throws IOException, SAXException {
		//this(new FileInputStream(iFilename));
    this(ClassLoader.getSystemResourceAsStream(iFilename));
	}

	public XMLFraxConfiguration(InputStream iIn)
      throws IOException, SAXException {
		if (iIn == null) {
			throw new NullPointerException(kErrorInputStreamNull);
    }
    
    mExtractorClassMap = new HashMap();
    mPlugClassMap = new HashMap();
    mExtensionMap = new HashMap();
    mClassDepMap = new HashMap();
    mDBMSVendorMap = new HashMap();
    
    // create and configure the digester object
    mDigester = new Digester();
    mDigester.push(this);

    // add extractor rules
    mDigester.addCallMethod("frax-config/extractors/extractor",
      "addExtractor", 3);
    mDigester.addCallParam("frax-config/extractors/extractor", 0, "class");
    mDigester.addCallParam("frax-config/extractors/extractor", 1, "schemes");
    mDigester.addCallParam("frax-config/extractors/extractor", 2,
      "dependencies");

    // add plug rules
    mDigester.addCallMethod("frax-config/plugs/plug", "addPlug", 3);
    mDigester.addCallParam("frax-config/plugs/plug", 0, "class");
    mDigester.addCallParam("frax-config/plugs/plug", 1, "types");
    mDigester.addCallParam("frax-config/plugs/plug", 2, "dependencies");

    // add extension map rules
    mDigester.addCallMethod("frax-config/extension-map/map-entry",
      "addExtensionMapping", 2);
    mDigester.addCallParam("frax-config/extension-map/map-entry", 0, "ext");
    mDigester.addCallParam("frax-config/extension-map/map-entry", 1, "mimetype");    

    // add oracle rules
    mDigester.addCallMethod("frax-config/oracle", "setOracleHostAndPort", 2,
      new String[] { "java.lang.String", "java.lang.Integer" });
    mDigester.addCallParam("frax-config/oracle", 0, "host");
    mDigester.addCallParam("frax-config/oracle", 1, "port");
    
    // add DBMS vendor rules
    mDigester.addCallMethod("frax-config/dbms-vendors/vendor",
      "addDBMSVendorMapping", 2);
    mDigester.addCallParam("frax-config/dbms-vendors/vendor", 0, "name");
    mDigester.addCallParam("frax-config/dbms-vendors/vendor", 1, "class");
    
    // add metadata cache rules
    mDigester.addCallMethod("frax-config/metadata-cache",
      "setMetadataCache", 3);
    mDigester.addCallParam("frax-config/metadata-cache", 0, "vendor");
    mDigester.addCallParam("frax-config/metadata-cache", 1, "jena-db-type");
    mDigester.addCallParam("frax-config/metadata-cache", 2, "location");
    
    // parse the XML configuration data
    mDigester.parse(iIn);
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
  
  public String[] getLocalSchemes() {
    return (String[]) mExtractorClassMap.keySet().toArray(kZeroString);
  }
  
  public String[] getLocalTypes() {
    return (String[]) mPlugClassMap.keySet().toArray(kZeroString);
  }
  
  public List getDependencies(String iClassName) {
    return Collections.unmodifiableList((List) mClassDepMap.get(iClassName));
  }
   
  public void addExtractor(String iClass, String iSchemes, String iDeps) {
    StringTokenizer st;
    
    // for each scheme
    st = new StringTokenizer(iSchemes, ", \n\r\t");
    while (st.hasMoreTokens()) {
      String scheme = st.nextToken();
      try {
        mExtractorClassMap.put(scheme, Class.forName(iClass));
      } catch (ClassNotFoundException ex) {
        sLog.warn(kErrorCouldNotFindClass + iClass);
      }
    }
    
    // for each dependency
    List depList = new ArrayList();
    st = new StringTokenizer(iDeps, "|\n\r\t");
    while (st.hasMoreTokens()) {
      depList.add(st.nextToken());      
    }
    
    mClassDepMap.put(iClass, depList);
  }
  
  public void addPlug(String iClass, String iTypes, String iDeps) {
    StringTokenizer st;
    
    // for each scheme
    st = new StringTokenizer(iTypes, ", \n\r\t");
    while (st.hasMoreTokens()) {
      String type = st.nextToken();
      try {
        mPlugClassMap.put(type, Class.forName(iClass));
      } catch (ClassNotFoundException ex) {
        sLog.warn(kErrorCouldNotFindClass + iClass);
      }
    }
    
    // for each dependency
    List depList = new ArrayList();
    st = new StringTokenizer(iDeps, "|\n\r\t");
    while (st.hasMoreTokens()) {
      depList.add(st.nextToken());      
    }
    
    mClassDepMap.put(iClass, depList);
  }
  
  public void addExtensionMapping(String iExt, String iType) {
    mExtensionMap.put(iExt, iType);
  }
  
  public void addDBMSVendorMapping(String iName, String iClassName) {
    mDBMSVendorMap.put(iName, iClassName);
  }
  
  public String getOracleHostName() {
    return mOracleHostName;
  }
  
  public int getOraclePort() {
    return mOraclePort;
  }
  
  public void setOracleHostAndPort(String iHost, int iPort) {
    mOracleHostName = iHost;
    mOraclePort = iPort;
  }
  
  public Map getDBMSVendorMap() {
    return Collections.unmodifiableMap(mDBMSVendorMap);
  }
  
  public void setMetadataCache(String iVendor, String iJenaDBType,
      String iLocation) {
    mMetadataCacheVendor = iVendor;
    mMetadataCacheJenaDBType = iJenaDBType;
    mMetadataCacheLocation = iLocation;
  }
  
  public String getMetadataCacheVendor() {
    return mMetadataCacheVendor;
  }
  
  public String getMetadataCacheJenaDBType() {
    return mMetadataCacheJenaDBType;
  }
  
  public String getMetadataCacheLocation() {
    return mMetadataCacheLocation;
  }
}