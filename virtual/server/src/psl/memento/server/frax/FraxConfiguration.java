package psl.memento.server.frax;

import java.util.List;
import java.util.Map;

public interface FraxConfiguration {  
  public Class getExtractorClass(String iScheme);
  public Class getPlugClass(String iContentType);
  public String getSchemeString(String iClassName);
  public String getTypeString(String iClassName);
  public String getMIMEType(String iExtension);
  public String[] getLocalSchemes();
  public String[] getLocalTypes();
  public List getDependencies(String iClassName);
  public String getOracleHostName();  
  public int getOraclePort();
  public Map getDBMSVendorMap();
  public String getMetadataCacheVendor();  
  public String getMetadataCacheJenaDBType();
  public String getMetadataCacheLocation();
  
  public void addExtractor(String iClass, String iSchemes, String iDeps);
  public void addPlug(String iClass, String iTypes, String iDeps);
  
  // can be (re)set at run-time
  public boolean getUseMetadataCache();
  public void setUseMetadataCache(boolean iB);
  public boolean getExtractContentMetadata();
  public void setExtractContentMetadata(boolean iB);
  public boolean getUseOracleForExtractors();
  public void setUseOracleForExtractors(boolean iB);
  public boolean getUseOracleForPlugs();
  public void setUseOracleForPlugs(boolean iB);
}