package psl.memento.server.frax;

// jdk imports
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.logging.Logger;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.Resource;
import psl.memento.server.frax.FraxException;
import psl.memento.server.frax.util.MiscUtils;

public abstract class Plug implements Serializable {
  static {
    MiscUtils.configureLogging();
  }
  
  private static final String kWarningCouldNotInstantiatePlug = 
    "Could not instantiate plug class: ";
  
  private static Logger sLog = Logger.getLogger("psl.memento.server.frax");
  
 	protected Plug() {
    // make direct instantiation by non-subclasses impossible
  }
  
  /**
   * <p>
   * Obtains a <code>Plug</code> object that can extract metadata
   * from data of the specified content type.
   *
   * <p>
   * A list of known content types is available <a
   * href="http://hostutopia.com/support/s058.html">here</a>.
   *
   * @param iContentType the MIME content type for which to obtain a plug
   * @return a <code>Plug</code> object or <code>null</code> if no
   * plug that handles the given content type can be found
   */
  public static Plug getInstance(String iContentType) {
    Class plugClass;    

    plugClass = Frax.getInstance().getConfiguration()
      .getPlugClass(iContentType);
    
    if (plugClass == null) {
      return null;
    }
    
    Plug plug = null;
    
    try {
      plug = (Plug) plugClass.newInstance();      
    } catch (Exception ex) {      
      sLog.warning(kWarningCouldNotInstantiatePlug + plugClass);
    }
    
    return plug;
  }
  
  public abstract void extractContentMetadata(InputStream iSource,
    Resource iTarget) throws FraxException;
}