package psl.memento.server.frax;

// jdk imports
import java.io.InputStream;
import java.net.URI;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.Resource;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.Seq;
import com.hp.hpl.mesa.rdf.jena.common.SeqImpl;
import psl.memento.server.frax.FraxException;
import psl.memento.server.frax.util.ImageInfo;
import psl.memento.server.frax.vocabulary.ImageVocab;

class ImagePlug extends Plug {
  private static final String kErrorBadImageData =
    "Could not extract metadata because of bad data or an unknown format.";
  private static final String kErrorAddingProperty =
    "Error adding RDF Property object.";
  
  public void extractContentMetadata(InputStream iSource, Resource iTarget)
      throws FraxException {
    ImageInfo ii = new ImageInfo();
    ii.setInput(iSource);
    ii.setCollectComments(true);
    
    // we may need to process the entire input stream in order to determine
    // the number of images -- remove this line if things are too slow
    ii.setDetermineImageNumber(true);    
        
    if(!ii.check()) {
      throw new FraxException(kErrorBadImageData);
    }

    try {
      iTarget.addProperty(ImageVocab.kHeightPixels.getProperty(),
        ii.getHeight());
      iTarget.addProperty(ImageVocab.kWidthPixels.getProperty(),
        ii.getWidth());
      
      int heightDPI = ii.getPhysicalHeightDpi();
      if (heightDPI != -1) {
        iTarget.addProperty(ImageVocab.kHeightDPI.getProperty(),
          ii.getPhysicalHeightDpi());
      }
      
      int widthDPI = ii.getPhysicalWidthDpi();
      if (widthDPI != -1) {
        iTarget.addProperty(ImageVocab.kWidthDPI.getProperty(),
          ii.getPhysicalWidthDpi());
      }
      
      iTarget.addProperty(ImageVocab.kBPP.getProperty(),
        ii.getBitsPerPixel());
      iTarget.addProperty(ImageVocab.kNumImages.getProperty(),
        ii.getNumberOfImages());

      int numComments = ii.getNumberOfComments();
      if (numComments > 0) {
        Seq commentsSeq = new SeqImpl(iTarget.getModel());
        for (int i = 0; i < numComments; i++) {             
          commentsSeq.add(ii.getComment(i));      
        }
        iTarget.addProperty(ImageVocab.kComments.getProperty(), commentsSeq);      
      }
    } catch (RDFException ex) {
      throw new FraxException(kErrorAddingProperty, ex);
    }
  }
}