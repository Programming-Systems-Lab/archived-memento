package psl.memento.server.frax.vocabulary;

import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;

public class HTTPVocab implements Vocab {
	private static final String kNamespace =
    "http://www.psl.cs.columbia.edu/frax-rdf-http/1.0#";

  private Property mProperty;
  
  public static final HTTPVocab kServerInfo =
    new HTTPVocab(kNamespace, "serverInfo");
  public static final HTTPVocab kDateDelivered =
    new HTTPVocab(kNamespace, "dateDelivered");
  public static final HTTPVocab kDateExpires =
    new HTTPVocab(kNamespace, "dateExpires");  
  
  private HTTPVocab(String iNamespace, String iPropertyName) {
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