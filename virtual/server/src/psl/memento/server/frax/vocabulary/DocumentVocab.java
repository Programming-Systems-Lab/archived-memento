package psl.memento.server.frax.vocabulary;

import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;

public class DocumentVocab {
	private static final String kNamespace =
    "http://www.psl.cs.columbia.edu/frax-rdf-document/1.0#";

  private Property mProperty;
  
  public static final DocumentVocab kAuthor =
    new DocumentVocab(kNamespace, "author");  
  public static final DocumentVocab kCharacterCount =
    new DocumentVocab(kNamespace, "characterCount");
	public static final DocumentVocab kComments =
    new DocumentVocab(kNamespace, "comments");
	public static final DocumentVocab kPageCount =
    new DocumentVocab(kNamespace, "pageCount");
  public static final DocumentVocab kRevisionNumber =
    new DocumentVocab(kNamespace, "revisionNumber");
  public static final DocumentVocab kSubject =
    new DocumentVocab(kNamespace, "subject");
  public static final DocumentVocab kTemplate =
    new DocumentVocab(kNamespace, "template");
  public static final DocumentVocab kTitle =
    new DocumentVocab(kNamespace, "title");
  public static final DocumentVocab kWordCount =
    new DocumentVocab(kNamespace, "wordCount");
  public static final DocumentVocab kText =
    new DocumentVocab(kNamespace, "text");
  
  private DocumentVocab(String iNamespace, String iPropertyName) {
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