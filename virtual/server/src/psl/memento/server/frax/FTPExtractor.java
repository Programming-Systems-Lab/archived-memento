package psl.memento.server.frax;

// jdk imports
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.StringTokenizer;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.common.SeqImpl;
import cz.dhl.ftp.*;
import cz.dhl.io.CoFile;
import psl.memento.server.frax.FraxException;
import psl.memento.server.frax.util.MiscUtils;
import psl.memento.server.frax.vocabulary.FileVocab;
import psl.memento.server.frax.vocabulary.ResourceVocab;

public class FTPExtractor extends Extractor {
  private static final String kDefaultPassword = "frax@";
  private static final String kErrorFtp =
    "FTP error.";
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
    Ftp ftp = null;
    try {
      FtpConnect connectInfo = FtpConnect.newConnect(iURI.toString());
      completeConnectInfoFromURI(connectInfo, iURI);
      
      ftp = new Ftp();
      
      // disable logging of FTP command messages
      ftp.getContext().setConsole(null);
      
      ftp.connect(connectInfo);
      
      String path = iURI.getPath();
      String dirPath;
      String filePath;
      
      int index = path.lastIndexOf('/');
      if (index == -1) {
        dirPath = "/";
        filePath = "";
      } else {
        if (index + 1 == path.length()) {
          dirPath = path;
          filePath = "";
        } else {
          dirPath = path.substring(0, index + 1);
          filePath = path.substring(index + 1);
        }
      }

      FtpFile dir = new FtpFile(dirPath, ftp);
      CoFile f = null;
      
      if (filePath == "") {
        f = dir;
      } else {
        CoFile[] dirContents = dir.listCoFiles();
        if (dirContents != null) {
          for (int i = 0; i < dirContents.length; i++) {
            if (filePath.equals(dirContents[i].getName())) {
              f = dirContents[i];
            }
          }
        }
      }
      
      if (f == null) {
        throw new FraxException(kErrorFileDoesNotExist);
      }
      
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
        return f.getInputStream();
      } else if (f.isDirectory()) {
        // add directory-only properties
        CoFile[] contents = f.listCoFiles();
        if (contents != null) {
          Seq contentsSeq = new SeqImpl(iTarget.getModel());
          for (int i = 0; i < contents.length; i++) {
            contentsSeq.add(contents[i].getName());
          }
          iTarget.addProperty(FileVocab.kContents.getProperty(), contentsSeq);
        }
      
        // there is no actual byte data in the directory, so return null
        return null;
      }
    } catch (IOException ex) {
      throw new FraxException(kErrorFtp, ex);
    } catch (RDFException ex) {
      throw new FraxException(kErrorAddingProperty, ex);
    } finally {
      if (ftp != null) {
        ftp.disconnect();        
      }
    }
    
    // should never be reached, but keep the compiler happy
    return null;
  }
  
  private void completeConnectInfoFromURI(FtpConnect iConnectInfo, URI iURI) {    
    iConnectInfo.setUserName("anonymous");
    iConnectInfo.setPassWord(kDefaultPassword);
    iConnectInfo.setPathName("/");

    String userInfo = iURI.getUserInfo();
    if (userInfo == null) {
      return;
    }
    
    StringTokenizer st = new StringTokenizer(userInfo, ":");

    if (st.hasMoreTokens()) {
      String userName = st.nextToken().trim();
      if (!userName.equals("")) {
        iConnectInfo.setUserName(userName);
      }

      if (st.hasMoreTokens()) {
        String password = st.nextToken(); 
        iConnectInfo.setPassWord(password);
      }      
    }
  }
}