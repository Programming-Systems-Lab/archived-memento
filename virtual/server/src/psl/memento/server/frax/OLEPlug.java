package psl.memento.server.frax;

// jdk imports
import java.io.*;
import java.net.URI;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.Resource;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.Seq;
import com.hp.hpl.mesa.rdf.jena.common.SeqImpl;
import org.apache.poi.hpsf.*;
import org.apache.poi.poifs.eventfilesystem.*;
import psl.memento.server.frax.FraxException;
import psl.memento.server.frax.vocabulary.DocumentVocab;

public class OLEPlug extends Plug {
  private static final String kErrorAddingProperty =
    "Error adding RDF Property object.";
  
  private FraxException mExceptionThrown;
  private Resource mTarget;
  
  public void extractContentMetadata(InputStream iSource, Resource iTarget)
      throws FraxException {    
    POIFSReader r = new POIFSReader();
    r.registerListener(new FraxPOIFSReaderListener(this),
      "\005SummaryInformation");
    
    mExceptionThrown = null;
    mTarget = iTarget;
    
    try {
      r.read(iSource);
    } catch (IOException ex) {
      throw new FraxException("Could not read OLE data.", ex);
    }
    
    if (mExceptionThrown != null) {
      throw mExceptionThrown;
    }
  }
  
  private static class FraxPOIFSReaderListener implements POIFSReaderListener {
    private OLEPlug mPlug;
    
    private FraxPOIFSReaderListener(OLEPlug iP) {
      mPlug = iP;
    }
    
    public void processPOIFSReaderEvent(POIFSReaderEvent iEvent) {
      SummaryInformation si = null;
      try {
        si = (SummaryInformation)
          PropertySetFactory.create(iEvent.getStream());
      } catch (Exception ex) {
        mPlug.mExceptionThrown = new FraxException(
          "Could not create an OLE summary information property set.", ex);
        return;
      }
      
      try {
        if (si.getAuthor() != null) {
          mPlug.mTarget.addProperty(DocumentVocab.kAuthor.getProperty(),
            si.getAuthor());
        }
        
        if (si.getCharCount() != 0) {
          mPlug.mTarget.addProperty(DocumentVocab.kCharacterCount.getProperty(),
            si.getCharCount());
        }
        
        if (si.getComments() != null) {
          mPlug.mTarget.addProperty(DocumentVocab.kComments.getProperty(),
            si.getComments());
        }
        
        if (si.getPageCount() != 0) {
          mPlug.mTarget.addProperty(DocumentVocab.kPageCount.getProperty(),
            si.getPageCount());
        }
        
        if (si.getRevNumber() != null) {
          mPlug.mTarget.addProperty(DocumentVocab.kRevisionNumber.getProperty(),
            si.getRevNumber());
        }
        
        if (si.getSubject() != null) {
          mPlug.mTarget.addProperty(DocumentVocab.kSubject.getProperty(),
            si.getSubject());
        }
        
        if (si.getTemplate() != null) {
          mPlug.mTarget.addProperty(DocumentVocab.kTemplate.getProperty(),
            si.getTemplate());
        }
        
        if (si.getTitle() != null) {
          mPlug.mTarget.addProperty(DocumentVocab.kTitle.getProperty(),
            si.getTitle());
        }
        
        if (si.getWordCount() != 0) {
          mPlug.mTarget.addProperty(DocumentVocab.kWordCount.getProperty(),
            si.getWordCount());
        }
      } catch (RDFException ex) {
        mPlug.mExceptionThrown = new FraxException(kErrorAddingProperty, ex);
        return;
      }
    }
  }
}