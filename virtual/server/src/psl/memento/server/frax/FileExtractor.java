package psl.memento.server.frax;

// jdk imports
import java.net.URI;
import java.net.URISyntaxException;
import java.io.*;
import java.util.Date;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.common.SeqImpl;
import psl.memento.server.frax.FraxException;
import psl.memento.server.frax.util.MiscUtils;
import psl.memento.server.frax.vocabulary.ResourceVocab;
import psl.memento.server.frax.vocabulary.FileVocab;

class FileExtractor extends Extractor {
  private static final String kErrorBadFileURI =
    "A file URI cannot have authority, query, or fragment components.";

  private static final String kErrorFileDoesNotExist =
    "File does not exist.";
  
	/**
   * <p>
   * Extracts scheme metadata from the resource denoted by the given URI.
   * Scheme metadata is considered to be information that can be learned
   * without examining the contents, or actual byte data, of the resource
   * itself.  For example, for the "file" scheme, we can extract file size,
   * date last modified, MIME type, and other properties without knowing
   * anything about the actual file data.
   *
   * <p>
   * This method returns an <code>InputStream</code> object with which we can
   * extract the target resource's byte data with a content plug, or <code>
   * null</code> if either the notion of byte data is meaningless for this
   * type of resource or we cannot procure the <code>InputStream</code>.
   *
   * @param iURI the URI that denotes the resource from which to extract
   * metadata
   * @param iTarget the RDF resource object to which to add the extracted
   * metadata
   * @return an <code>InputStream</code> with which we can extract the
   * resource's byte data, or <code>null</code> if we cannot extract
   * byte data for resources with this scheme
   * @exception FraxException if an error occurred extracting metadata
   */
  public InputStream extractSchemeMetadata(URI iURI, Resource iTarget)
      throws FraxException {
    // we only support files on the local machine, so make sure the
    // authority, query, and fragment components are all null
    if (iURI.getAuthority() != null || iURI.getQuery() != null ||
        iURI.getFragment() != null) {
      throw new FraxException(kErrorBadFileURI);
    }
    
    File f = new File(iURI);      
    if (!f.exists()) {
      throw new FraxException(kErrorFileDoesNotExist);
    }

    try {
      // add properties common to both files and directories
      iTarget.addProperty(ResourceVocab.kName.getProperty(),
        f.getName());
      iTarget.addProperty(ResourceVocab.kDateModified.getProperty(),
        MiscUtils.dateToString(new Date(f.lastModified())));
      iTarget.addProperty(FileVocab.kIsHidden.getProperty(), f.isHidden());
      iTarget.addProperty(FileVocab.kIsReadOnly.getProperty(), !f.canWrite());
      
      if (f.isFile()) {
        // add file-only properties
        iTarget.addProperty(ResourceVocab.kSize.getProperty(), f.length());        
        
        // return an input stream to the file content
        try {
          return new FileInputStream(f);
        } catch (FileNotFoundException ex) {
          // impossible since we've already confirmed that the file exists,
          // but keep the compiler happy
          return null;
        }        
      } else if (f.isDirectory()) {
        // add directory-only properties
        String[] contents = f.list();
        if (contents != null) {
          Seq contentsSeq = new SeqImpl(iTarget.getModel());
          for (int i = 0; i < contents.length; i++) {
            contentsSeq.add(contents[i]);
          }
          iTarget.addProperty(FileVocab.kContents.getProperty(), contentsSeq);
        }
      
        // there is no actual byte data in the directory, so return null
        return null;
      }
    } catch (RDFException ex) {
      throw new FraxException(kErrorAddingProperty, ex);
    }
    
    // should never be reached, but keep the compiler happy
    return null;
  }
}