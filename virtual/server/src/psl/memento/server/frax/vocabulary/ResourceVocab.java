package psl.memento.server.frax.vocabulary;

import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;

public class ResourceVocab implements Vocab {
	private static final String kNamespace =
    "http://www.psl.cs.columbia.edu/frax-rdf-resource/1.0#";

  private Property mProperty;
  
  public static final ResourceVocab kStatements =
    new ResourceVocab(kNamespace, "statements");
  public static final ResourceVocab kExists =
    new ResourceVocab(kNamespace, "exists");
  public static final ResourceVocab kName =
    new ResourceVocab(kNamespace, "name");  
  public static final ResourceVocab kSummary =
    new ResourceVocab(kNamespace, "summary");
	public static final ResourceVocab kDateCreated =
    new ResourceVocab(kNamespace, "dateCreated");
	public static final ResourceVocab kDateModified =
    new ResourceVocab(kNamespace, "dateModified");
  public static final ResourceVocab kSize =
    new ResourceVocab(kNamespace, "size");
  public static final ResourceVocab kMIMEType =
    new ResourceVocab(kNamespace, "mimeType");
  
  private ResourceVocab(String iNamespace, String iPropertyName) {
    try {
      mProperty = new PropertyImpl(iNamespace, iPropertyName);
    } catch (RDFException ex) {
      throw new RuntimeException("Could not create property: " +
        iPropertyName, ex);
    }
  }
  
  public Property getProperty() {
    return mProperty;
  }
}