package psl.memento.server.frax.vocabulary;

import com.hp.hpl.mesa.rdf.jena.model.Property;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.common.PropertyImpl;

public class JavaSourceVocab {
	private static final String kNamespace =
    "http://www.psl.cs.columbia.edu/frax-rdf-javasource/1.0#";

  private Property mProperty;
  
  public static final JavaSourceVocab kPackage =
    new JavaSourceVocab(kNamespace, "package");
  public static final JavaSourceVocab kImports =
    new JavaSourceVocab(kNamespace, "imports");
  public static final JavaSourceVocab kClass =
    new JavaSourceVocab(kNamespace, "class");
  public static final JavaSourceVocab kInterface =
    new JavaSourceVocab(kNamespace, "interface");
  public static final JavaSourceVocab kMethod =
    new JavaSourceVocab(kNamespace, "method");
  public static final JavaSourceVocab kConstructor =
    new JavaSourceVocab(kNamespace, "constructor");
  public static final JavaSourceVocab kModifierMask =
    new JavaSourceVocab(kNamespace, "modifierMask");
  public static final JavaSourceVocab kReturnType =
    new JavaSourceVocab(kNamespace, "returnType");
  public static final JavaSourceVocab kType =
    new JavaSourceVocab(kNamespace, "type");
  public static final JavaSourceVocab kParam =
    new JavaSourceVocab(kNamespace, "param");
  
  private JavaSourceVocab(String iNamespace, String iPropertyName) {
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