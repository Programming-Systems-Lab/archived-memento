package psl.memento.server.frax;

import java.util.List;
import java.util.Map;

public interface FraxConfiguration {  
  public Class getExtractorClass(String iScheme);
  public Class getPlugClass(String iContentType);
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
}