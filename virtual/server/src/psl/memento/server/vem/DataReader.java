package psl.memento.server.vem;

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
    
    public Room mRoom;
    public ArrayList mObjs, mDoors;
    private String contents;
    
    private StringTokenizer strtok;
    
    /** Creates a new instance of DataReader */
    public DataReader(String filename) {
        mFilename = filename;
    }
    
    public boolean getRoomData() 
    {
        try {
            readData();
	    return parseInput();
        } catch (IOException ioe) {
            setError("I/O Problem: " + ioe);
	    return false;
        }
    }
    
    private void readData() throws IOException {
        String line;
        BufferedReader in = new BufferedReader(new FileReader(mFilename));
	StringBuffer buf = new StringBuffer(100);
	
        while ((line = in.readLine()) != null) {
	    buf.append(line + "\n");
	}
	
	contents = buf.toString();
    }
    
    public boolean parseInput() {
	String tok;
	int count;
        boolean gotRoom = false;	
        RoomObject o;
        Door d;
	
	// initialize
	mObjs = new ArrayList();
        mDoors = new ArrayList();
    
	StringTokenizer reader = new StringTokenizer(contents, "\n");

	while(reader.hasMoreTokens()) {
            strtok = new StringTokenizer(reader.nextToken(), "(), ");
            tok = strtok.nextToken();
            
            if (tok == null) continue;
            
            if (tok.equals("room")) {
                if (gotRoom)
                    return false;
                
		count = strtok.countTokens(); 
		if (count == 2) {
		    mRoom = new Room(nextInt(), nextInt());
		    gotRoom = true;
		}
		else if (count % 2 == 0) {
		    Polygon poly = new Polygon();
		    while (strtok.hasMoreTokens()) {
			poly.addPoint(nextInt(), nextInt());
		    }
		    Rectangle b = poly.getBounds();
		    mRoom = new Room(b.width, b.height);
		    mRoom.plan = poly;
		    gotRoom = true;
		}
            }
            
            if (tok.equals("obj")) {
                o = new RoomObject();
                
                o.width = nextInt();
                o.height = nextInt();
                o.type = nextStr();
                
                mObjs.add(o);
            }
            
            if (tok.equals("door")) {
                d = new Door(nextStr().charAt(0), nextInt(), nextInt());
                mDoors.add(d);
            }
        }
        
	if (!gotRoom)
	    setError("Room not specified or bad syntax");
	
        return gotRoom;
    }
    
    public String getFileContent() {
	return contents;
    }
    
    public void setFileContent(String c) {
	contents = c;
    }
    
    /** private variables **/
    
    private String mFilename;
    
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


