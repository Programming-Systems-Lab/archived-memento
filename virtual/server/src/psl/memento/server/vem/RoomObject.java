package psl.memento.server.vem;

/*
 * RoomObject.java
 *
 * Created on November 6, 2002, 7:16 PM
 */

/**
 *
 * @author  vlad
 */
public class RoomObject implements Comparable {
    
    public int xloc, yloc;
    public int height, width;
    public String type;
    public boolean fixed;
    public boolean placed;
    
    /** Creates a new instance of RoomObject */
    public RoomObject() {
	fixed = false;
	placed = false;
    }
    
    public int compareTo(Object o) {
	return type.compareTo(((RoomObject)o).type);
    }
}
