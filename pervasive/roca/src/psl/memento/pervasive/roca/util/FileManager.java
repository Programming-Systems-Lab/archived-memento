package psl.memento.pervasive.roca.util;

import org.jdom.*;
import org.jdom.output.*;
import java.io.*;

/** 
 * Handles writing and opening files.
 *
 * @author Kristina Holst
 */
public class FileManager {
  
  /** Creates a new instance of FileManager */
  public FileManager() {
  }
  
  /** Writes XML file to disk */
  public static void saveRoom(File file) {
    
    /*
     * check to see if file already exists
     * if so, prompt for overwrite
     */
    
    FileWriter fw = null;
    BufferedWriter bw = null;
    
    Document doc = XMLGenerator.generateRoomXML();
    
    if (doc != null) {
      XMLOutputter outputter = new XMLOutputter("   ", true);
      
      String output = outputter.outputString(doc);
      
      try {
        fw = new FileWriter(file);
        bw = new BufferedWriter(fw);
        bw.write(output);
        
        bw.close();
        fw.close();
      } catch (IOException ex) {
        ex.printStackTrace();
        
        
      } 
    }
  }
  
  /** Loads Room from XML file */
  public static void loadRoom(File file) {
    System.out.println("load " + file.getName());
    
    /*
     * set summary tab's text area
     * add objects to menus at bottom of all object tabs
     */
  }
}
