package psl.memento.server.frax.vocabulary;

import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;

public class FileVocab implements Vocab {
	private static final String kNamespace =
    "http://www.psl.cs.columbia.edu/frax-rdf-file/1.0#";

  private Property mProperty;
  
  public static final FileVocab kIsHidden =
    new FileVocab(kNamespace, "isHidden");
  public static final FileVocab kIsReadOnly =
    new FileVocab(kNamespace, "isReadOnly");
  public static final FileVocab kContents =
    new FileVocab(kNamespace, "contents");
  
  private FileVocab(String iNamespace, String iPropertyName) {
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