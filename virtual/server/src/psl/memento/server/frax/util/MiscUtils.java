package psl.memento.server.frax.util;

// jdk imports
import java.text.DateFormat;
import java.util.*;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.*;
import psl.memento.server.frax.*;
import psl.memento.server.frax.vocabulary.ResourceVocab;

public class MiscUtils {
	private static DateFormat sDateFormat;  

	private MiscUtils() {
		// prevent instantiation
	}
  
  public static String getMIMEType(Resource iResource) {
    try {
      Statement p = iResource.getProperty(
        ResourceVocab.kMIMEType.getProperty());
      
      return p.getString();
    } catch (RDFException ex) {
    }    

    // the resource has no mime type property
    // check the file extension
    String uri = iResource.getURI();
    int dotIndex = uri.lastIndexOf('.');
    String ext = (dotIndex == -1) ? "" : uri.substring(dotIndex + 1);      
    return Frax.getInstance().getConfiguration().getMIMEType(ext);
  }

	public static String dateToString(Date iDate) {
		initDateFormat();
		return sDateFormat.format(iDate);
	}
  
  public static Resource getResourceIfExists(Model iModel, String iURI)
      throws RDFException {
    Resource r = iModel.getResource(iURI);
    return (r.hasProperty(ResourceVocab.kExists.getProperty())) ? r : null;
  }
  
	private static void initDateFormat() {
		if (sDateFormat == null) {
			// lazy instantiation
			sDateFormat = DateFormat.getDateTimeInstance();
		}
	}
  
  public static class SubjectSelector implements Selector {
    private List mSubjects;
    
    public SubjectSelector(List iSubjects) {
      mSubjects = iSubjects;
    }
    
    public boolean test(Statement iS) {      
      for (int i = 0, n = mSubjects.size(); i < n; i++) {
        if (iS.getSubject().equals((mSubjects.get(i)))) {          
          return true;
        }
      }
     
      return false;
    }
  }
}