package psl.memento.server.frax.vocabulary;

import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;

public class HTMLVocab {
	private static final String kNamespace =
    "http://www.psl.cs.columbia.edu/frax-rdf-HTML/1.0#";

  private Property mProperty;
  
  public static final HTMLVocab kImages =
    new HTMLVocab(kNamespace, "images");
  public static final HTMLVocab kLinks =
    new HTMLVocab(kNamespace, "links");
  
  private HTMLVocab(String iNamespace, String iPropertyName) {
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