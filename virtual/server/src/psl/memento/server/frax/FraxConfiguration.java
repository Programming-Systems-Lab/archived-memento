package psl.memento.server.frax;

public interface FraxConfiguration {
  public Class getExtractorClass(String iScheme);
  public Class getPlugClass(String iContentType);
  public String getMIMEType(String iExtension);
}