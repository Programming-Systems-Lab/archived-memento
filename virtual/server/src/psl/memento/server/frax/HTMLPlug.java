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
import com.kizna.html.*;
import com.kizna.html.tags.*;
import com.kizna.html.scanners.*;
import psl.memento.server.frax.FraxException;
import psl.memento.server.frax.vocabulary.DocumentVocab;
import psl.memento.server.frax.vocabulary.HTMLVocab;

class HTMLPlug extends Plug {
  private static final String kErrorAddingProperty =
    "Error adding RDF Property object.";  
  
  public void extractContentMetadata(InputStream iSource, Resource iTarget)
      throws FraxException {
    StringBuffer text = new StringBuffer();

    try {
      HTMLParser parser = new HTMLParser(new HTMLReader(new BufferedReader(
        new InputStreamReader(iSource)), 8192));

      parser.addScanner(new HTMLLinkScanner("-l"));
      parser.addScanner(new HTMLImageScanner("-i"));
      parser.addScanner(new HTMLTitleScanner("-t"));

      Seq linksSeq = new SeqImpl(iTarget.getModel());
      Seq imagesSeq = new SeqImpl(iTarget.getModel());
      for (Enumeration e = parser.elements(); e.hasMoreElements(); ) {
        HTMLNode node = (HTMLNode) e.nextElement();

        if (node instanceof HTMLLinkTag) {
          HTMLLinkTag link = (HTMLLinkTag) node;
          linksSeq.add(link.getLink());
        } else if (node instanceof HTMLImageTag) {
          HTMLImageTag image = (HTMLImageTag) node;
          imagesSeq.add(image.getImageLocation());
        } else if (node instanceof HTMLTitleTag) {
          HTMLTitleTag title = (HTMLTitleTag) node;
          iTarget.addProperty(DocumentVocab.kTitle.getProperty(),
            title.getTitle());
        } else if (node instanceof HTMLStringNode) {
          HTMLStringNode sn = (HTMLStringNode) node;
          text.append(sn.getText());
        }
      }

      iTarget.addProperty(HTMLVocab.kLinks.getProperty(), linksSeq);
      iTarget.addProperty(HTMLVocab.kImages.getProperty(), imagesSeq);
      iTarget.addProperty(DocumentVocab.kText.getProperty(),
        text.toString());
    } catch (RDFException ex) {
      throw new FraxException(kErrorAddingProperty, ex);
    }
  }
}