/*
 * This is one of Vlad's files, up-to-date as of 12-22-02.
 *
 */

package psl.memento.pervasive.roca.vem;

import java.util.*;
import java.io.*;
import java.awt.*;

/*
 * DataReader.java
 *
 * Created on November 6, 2002, 7:09 PM
 */

/**
 *
 * @author  vlad
 */
public class DataReader {
  
  public static final int DEFAULT_ROOM_HEIGHT = 100;
  
  public Room mRoom;
  public ArrayList mObjs, mFixedObjs, mDoors;
  private String contents;
  
  private StringTokenizer strtok;
  
  public DataReader() {
    // initialize
    mObjs = new ArrayList();
    mFixedObjs = new ArrayList();
    mDoors = new ArrayList();
  }
  
  public void reset() {
    mObjs.clear();
    mFixedObjs.clear();
    mDoors.clear();
  }
  
  public void setRoom(Polygon poly, int dz) {
    Rectangle b = poly.getBounds();
    mRoom = new Room(b.width, b.height);
    mRoom.plan = poly;
  }
  
  public void setRoom(int dx, int dy, int dz) {
    mRoom = new Room(dx, dy);
  }
  
  public void addRoomObject(RoomObject ro) {
    if (ro.fixed) {
      mFixedObjs.add(ro);
    } else {
      mObjs.add(ro);
    }
  }
  
  public void addDoor(char direction, int offset, int length) {
    Door d = new Door(direction, offset, length);
    mDoors.add(d);
  }
  
  /**
   *	Opens the input file, saves the contents to a buffer and then calls
   *	<code>parseInput()</code>
   *
   */
  public boolean getRoomData(String filename) {
    try {
      readData(filename);
      return parseInput();
    } catch (IOException ioe) {
      setError("I/O Problem: " + ioe);
      return false;
    }
  }
  
  private void readData(String filename) throws IOException {
    String line;
    BufferedReader in = new BufferedReader(new FileReader(filename));
    StringBuffer buf = new StringBuffer(100);
    
    while ((line = in.readLine()) != null) {
      buf.append(line + "\n");
    }
    
    contents = buf.toString();
  }
  
  /**
   *	Converts whatever data is saved in the buffer and creates objects to
   *	represent the data.
   */
  public boolean parseInput() {
    String tok;
    int count;
    boolean gotRoom = false;
    RoomObject o;
    Door d;
    
    reset();
    
    StringTokenizer reader = new StringTokenizer(contents, "\n");
    
    while(reader.hasMoreTokens()) {
      strtok = new StringTokenizer(reader.nextToken(), "(), ");
      tok = strtok.nextToken();
      count = strtok.countTokens();
      
      if (tok == null) continue;
      
      if (tok.equals("room")) {
        if (gotRoom)
          return false;
        
        if (count == 2) {
          setRoom(nextInt(), nextInt(), DEFAULT_ROOM_HEIGHT);
          gotRoom = true;
        }
        else if (count % 2 == 0) {
          Polygon poly = new Polygon();
          while (strtok.hasMoreTokens()) {
            poly.addPoint(nextInt(), nextInt());
          }
          setRoom(poly, DEFAULT_ROOM_HEIGHT);
          gotRoom = true;
        }
      }
      
      if (tok.equals("obj")) {
        if (count != 3 && count != 5) {
          System.out.println("Object ignored: wrong number of parameters");
          continue;
        }
        
        if (count == 5) {   // location fixed
          o = new RoomObject(nextInt(), nextInt(), 1, nextInt(), nextInt(), 0);
        } else {
          o = new RoomObject(nextInt(), nextInt(), 1);
        }
        
        o.type = nextStr();
        addRoomObject(o);
      }
      
      if (tok.equals("door")) {
        addDoor(nextStr().charAt(0), nextInt(), nextInt());
      }
    }
    
    if (!gotRoom)
      setError("Room not specified or bad syntax");
    
    return gotRoom;
  }
  
  /**
   *	Returns the file content buffer.
   */
  public String getFileContent() {
    return contents;
  }
  
  /**
   *	Sets the file content buffer to some other String, possibly editted by
   *	the user.
   */
  public void setFileContent(String c) {
    contents = c;
  }
  
  /** Holds value of property errorMessage. */
  private String errorMessage;
  
  // helper methods
  
  public int nextInt() {
    return Integer.parseInt(strtok.nextToken());
  }
  
  public String nextStr() {
    return strtok.nextToken();
  }
  
  /** Getter for property errorMessage.
   * @return Value of property errorMessage.
   *
   */
  public String getError() {
    return this.errorMessage;
  }
  
  /** Setter for property errorMessage.
   * @param errorMessage New value of property errorMessage.
   *
   */
  public void setError(String errorMessage) {
    this.errorMessage = errorMessage;
  }
  
}


