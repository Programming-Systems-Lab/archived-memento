package psl.memento.server.frax;

import java.util.*;
import java.util.logging.Logger;

import psl.memento.server.frax.util.MiscUtils;

public abstract class FraxConfigurationBase implements FraxConfiguration {
  static {
    MiscUtils.configureLogging();
  }
  
  protected static final String kErrorCouldNotFindClass =
    "Could not find class: ";
  
  private static Logger sLog = Logger.getLogger("psl.memento.server.frax");
  
  private static final String[] kZeroString = new String[0];
  
  private Map mExtractorClassMap;
  private Map mPlugClassMap;
  private Map mExtensionMap;
  private Map mClassDepMap;
  private Map mDBMSVendorMap;
  private Map mExtractorToSchemesMap;
  private Map mPlugToTypesMap;
  private String mOracleHostName;  
  private int mOraclePort;
  private String mMetadataCacheVendor;
  private String mMetadataCacheJenaDBType;
  private String mMetadataCacheLocation;
  private boolean mUseMetadataCache;
  private boolean mExtractContentMetadata;
  private boolean mUseOracleForExtractors;
  private boolean mUseOracleForPlugs;
  
  protected FraxConfigurationBase() {
    mExtractorClassMap = new HashMap();
    mPlugClassMap = new HashMap();
    mExtensionMap = new HashMap();
    mClassDepMap = new HashMap();
    mDBMSVendorMap = new HashMap();
    mExtractorToSchemesMap = new HashMap();
    mPlugToTypesMap = new HashMap();
    
    setUseMetadataCache(true);
    setExtractContentMetadata(true);
    setUseOracleForExtractors(true);
    setUseOracleForPlugs(true);
  }
  
  public Class getExtractorClass(String iScheme) {
		return (Class) mExtractorClassMap.get(iScheme);
	}
  
  public Class getPlugClass(String iContentType) {
    return (Class) mPlugClassMap.get(iContentType);
  }
  
  public String getSchemeString(String iClassName) {
    return (String) mExtractorToSchemesMap.get(iClassName);
  }
  
  public String getTypeString(String iClassName) {
    return (String) mPlugToTypesMap.get(iClassName);
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
        Class c = Class.forName(iClass, true, MiscUtils.getFraxClassLoader());        
        mExtractorClassMap.put(scheme, c);
      } catch (ClassNotFoundException ex) {
        sLog.warning(kErrorCouldNotFindClass + iClass);
      }
      mExtractorToSchemesMap.put(iClass, iSchemes);
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
        Class c = Class.forName(iClass, true, MiscUtils.getFraxClassLoader());        
        mPlugClassMap.put(type, c);
      } catch (ClassNotFoundException ex) {
        sLog.warning(kErrorCouldNotFindClass + iClass);
      }
      mPlugToTypesMap.put(iClass, iTypes);
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
  
  public boolean getUseMetadataCache() {
    return mUseMetadataCache;
  }
  
  public void setUseMetadataCache(boolean iB) {
    mUseMetadataCache = iB;
  }
  
  public boolean getExtractContentMetadata() {
    return mExtractContentMetadata;
  }
  
  public void setExtractContentMetadata(boolean iB) {
    mExtractContentMetadata = iB;
  }
  
  public boolean getUseOracleForExtractors() {
    return mUseOracleForExtractors;
  }
  
  public void setUseOracleForExtractors(boolean iB) {
    mUseOracleForExtractors = iB;
  }
  
  public boolean getUseOracleForPlugs() {
    return mUseOracleForPlugs;
  }
  
  public void setUseOracleForPlugs(boolean iB) {
    mUseOracleForPlugs = iB;
  }
}