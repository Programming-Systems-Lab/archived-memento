package psl.memento.server.frax.vocabulary;

import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;

public class ImageVocab implements Vocab {
	private static final String kNamespace =
    "http://www.psl.cs.columbia.edu/frax-rdf-image/1.0#";

  private Property mProperty;
  
  public static final ImageVocab kHeightPixels =
    new ImageVocab(kNamespace, "heightPixels");
  public static final ImageVocab kWidthPixels =
    new ImageVocab(kNamespace, "widthPixels");
  public static final ImageVocab kHeightDPI =
    new ImageVocab(kNamespace, "heightDPI");  
  public static final ImageVocab kWidthDPI =
    new ImageVocab(kNamespace, "widthDPI");  
  public static final ImageVocab kBPP =
    new ImageVocab(kNamespace, "bitsPerPixel");
  public static final ImageVocab kNumImages =
    new ImageVocab(kNamespace, "numImages");
  public static final ImageVocab kComments =
    new ImageVocab(kNamespace, "comments");
  
  private ImageVocab(String iNamespace, String iPropertyName) {
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