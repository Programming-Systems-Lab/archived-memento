package psl.memento.server.frax.util;

// jdk imports
import java.io.*;
import java.text.DateFormat;
import java.net.*;
import java.util.*;
import java.util.logging.*;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.*;
import psl.memento.server.frax.*;
import psl.memento.server.frax.vocabulary.ResourceVocab;

public class MiscUtils {
  private static final URL[] kZeroURLs = new URL[0];
  
  private static DateFormat sDateFormat;
  private static boolean sLoggingConfigured;
  private static ClassLoader sClassLoader;

  static {
    sLoggingConfigured = false;
    sClassLoader = ClassLoader.getSystemClassLoader();
    rebuildClassLoaderPaths();
/*    
    try {
      System.out.println(Arrays.asList(((URLClassLoader) sClassLoader).getURLs()));
      System.out.println();      
      Class c = sClassLoader.loadClass("psl.memento.server.frax.FTPExtractor");      
      c.newInstance();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
 */
  }
  
	private MiscUtils() {
		// prevent instantiation
	}
  
  public static void configureLogging() {
    if (!sLoggingConfigured) {
      try {
        Handler logHandler = new FileHandler("frax-log.txt");
        logHandler.setFormatter(new SimpleFormatter());
        
        Logger logger = Logger.getLogger("psl.memento.server.frax");
        
        // add our own log handler
        logger.addHandler(logHandler);
        logger.setLevel(Level.ALL);
      } catch (IOException ex) {
        System.out.println("WARNING: Could not configure logger: " +
          ex.getMessage());
      } finally {
        sLoggingConfigured = true;
      }
    }
  }
  
  public static ClassLoader getFraxClassLoader() {
    return sClassLoader;
  }
  
  public static void rebuildClassLoaderPaths() {
    synchronized (sClassLoader) {
      URL acquiredURL = sClassLoader.getResource("etc/frax/acquired/");
      if (acquiredURL != null) {
        List urls = new ArrayList();

        urls.add(acquiredURL);

        try {
          File acquiredDir = new File(new URI(acquiredURL.toString()));
          File depsDir = new File(acquiredDir, "deps");
          if (depsDir.exists()) {
            addToURLListRecursively(urls, depsDir);
          }
        } catch (URISyntaxException ex) {
          // log exception
        } catch (MalformedURLException ex) {
        // log exception
        }        

        sClassLoader
          = URLClassLoader.newInstance((URL[]) urls.toArray(kZeroURLs));
      }
    }
  }
  
  private static void addToURLListRecursively(List iURLs, File iFile)
      throws MalformedURLException {
    File[] contents = iFile.listFiles();
    for (int i = 0; i < contents.length; i++) {
      if (contents[i].isFile()) {        
        iURLs.add(contents[i].toURI().toURL());        
      } else {
        addToURLListRecursively(iURLs, contents[i]);
      }
    }
  }
  
  public static String concatenateDeps(String[] iDeps) {
    StringBuffer sb = new StringBuffer();
    
    for (int i = 0; i < iDeps.length; i++) {
      sb.append(iDeps[i]).append('|');
    }
    
    if (sb.length() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    
    return sb.toString();
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