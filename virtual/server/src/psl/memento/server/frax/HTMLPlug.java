package psl.memento.server.frax;

// jdk imports
import java.io.*;
import java.net.URI;
import java.util.Enumeration;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.Resource;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.Seq;
import com.hp.hpl.mesa.rdf.jena.common.SeqImpl;
import psl.memento.server.frax.FraxException;
import psl.memento.server.frax.vocabulary.DocumentVocab;

class HTMLPlug extends Plug {
  private static final String kErrorAddingProperty =
    "Error adding RDF Property object.";
  
  public void extractContentMetadata(InputStream iSource, Resource iTarget)
      throws FraxException {
    StringBuffer text = new StringBuffer();

    // extract text into text StringBuffer

    try {
      iTarget.addProperty(DocumentVocab.kText.getProperty(), text.toString());
    } catch (RDFException ex) {
      throw new FraxException(kErrorAddingProperty, ex);
    }
  }
}