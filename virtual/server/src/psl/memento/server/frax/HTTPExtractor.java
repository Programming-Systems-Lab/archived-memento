package psl.memento.server.frax;

// jdk imports
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.Resource;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import psl.memento.server.frax.FraxException;
import psl.memento.server.frax.vocabulary.ResourceVocab;
import psl.memento.server.frax.vocabulary.HTTPVocab;

public class HTTPExtractor extends Extractor {
  private static final String kErrorCantCreateURL =
    "Could not create a URL object out of: ";
  private static final String kErrorHttp =
    "HTTP error.";
  private static final String kErrorCantAuthenticate =
    "Authentication data missing.  Please prepend username:password to URI.";
  private static final String kErrorCantGetResource =
    "Could not retrieve resource.  HTTP status code: ";
  
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
    try {      
      HttpClient httpClient = new HttpClient();
            
      String userInfo = iURI.getUserInfo();
      if (userInfo != null) {
        int index = userInfo.indexOf(':');
        String userName;
        String password;
        if (index == -1) {
          userName = userInfo;
          password = "";
        } else {
          userName = userInfo.substring(0, index);
          if (index + 1 >= userInfo.length()) {
            password = "";
          } else {
            password = userInfo.substring(index + 1);
          }
        }
        
        HttpState state = httpClient.getState();      
        Credentials c = new UsernamePasswordCredentials(userName, password);
        state.setCredentials(null, c);
      }

      URL u = iURI.toURL();
      httpClient.startSession(u);      
      String path = u.getFile();
      
      if (path == null || path.trim().equals("")) {
        path = "/";
      }      
      
      HttpMethod method = new GetMethod(path);
      int responseCode = httpClient.executeMethod(method);
      
      // if the response status code is not 2xx, it is a failure,
      // and we're not interested in the metadata      
      if ((responseCode / 100) != 2) {
        if (responseCode == 401) {
          throw new FraxException(kErrorCantAuthenticate);
        }
        
        throw new FraxException(kErrorCantGetResource + responseCode);
      }
      
      extractMetadataFromHeaders(method, iTarget);
      
      InputStream stream = method.getResponseBodyAsStream();
      httpClient.endSession();      
      
      return stream;
    } catch (MalformedURLException ex) {
      throw new FraxException(kErrorCantCreateURL + iURI, ex);
    } catch (IOException ex) {      
      throw new FraxException(kErrorHttp, ex);
    } catch (HttpException ex) {
      throw new FraxException(kErrorHttp, ex);
    } catch (RDFException ex) {
      throw new FraxException(kErrorAddingProperty, ex);
    }
  }
  
  private void extractMetadataFromHeaders(HttpMethod iMethod,
      Resource iTarget) throws RDFException {
    Header h;    
    
    h = iMethod.getResponseHeader(Headers.kContentLength);
    if (h != null) {
      try {
        iTarget.addProperty(ResourceVocab.kSize.getProperty(),
          Integer.parseInt(h.getValue()));
      } catch (NumberFormatException ex) {
      }
    }
    
    h = iMethod.getResponseHeader(Headers.kLastModified);
    if (h != null) {
      iTarget.addProperty(ResourceVocab.kDateModified.getProperty(),
        h.getValue());
    }

    h = iMethod.getResponseHeader(Headers.kContentType);
    if (h != null) {
      String contentType = h.getValue();
      // omit the parameter after the semicolon, if one exists
      int semiIndex = contentType.indexOf(';');
      if (semiIndex != -1) {
        contentType = contentType.substring(0, semiIndex);
      }
      
      iTarget.addProperty(ResourceVocab.kMIMEType.getProperty(),
        contentType);
    }
    
    h = iMethod.getResponseHeader(Headers.kServer);
    if (h != null) {
      iTarget.addProperty(HTTPVocab.kServerInfo.getProperty(),
        h.getValue());
    }

    h = iMethod.getResponseHeader(Headers.kExpires);
    if (h != null) {
      iTarget.addProperty(HTTPVocab.kDateExpires.getProperty(),
        h.getValue());
    }

    h = iMethod.getResponseHeader(Headers.kDate);
    if (h != null) {
      iTarget.addProperty(HTTPVocab.kDateDelivered.getProperty(),
        h.getValue());
    }
  }
  
  private static class Headers {
    public static final String kContentLength = "Content-Length";
    public static final String kContentType = "Content-Type";
    public static final String kLastModified = "Last-Modified";
    public static final String kExpires = "Expires";
    public static final String kDate = "Date";
    public static final String kServer = "Server";
    
    private Headers() {
      // prevent instantiation
    }
  }
}