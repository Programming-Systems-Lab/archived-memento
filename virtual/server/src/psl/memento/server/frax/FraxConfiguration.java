package psl.memento.server.frax;

import java.util.List;

public interface FraxConfiguration {  
  public Class getExtractorClass(String iScheme);
  public Class getPlugClass(String iContentType);
  public String getMIMEType(String iExtension);
  public String[] getLocalSchemes();
  public String[] getLocalTypes();
  public List getDependencies(String iClassName);
  public String getOracleHostName();  
  public int getOraclePort();
}