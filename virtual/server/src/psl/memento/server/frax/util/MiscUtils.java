package psl.memento.server.frax.util;

// jdk imports
import java.text.DateFormat;
import java.util.Date;

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
  
	private static void initDateFormat() {
		if (sDateFormat == null) {
			// lazy instantiation
			sDateFormat = DateFormat.getDateTimeInstance();
		}
	}
}