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
    
    public int xloc, yloc, zloc;
    public int length, width, height;
    public String type;
    public boolean fixed;
    public boolean placed;
    
    /** Creates a new instance of RoomObject */
    public RoomObject() {
	placed = false;
        fixed = false;        
    }
    
    public RoomObject(int dx, int dy, int dz)
    {
        this();
        width = dx;
        length = dy;
        height = dz;
    }
    
    public RoomObject(int dx, int dy, int dz, int xloc, int yloc, int zloc)
    {
        this(dx, dy, dz);
        this.xloc = xloc;
        this.yloc = yloc;
        this.zloc = zloc;
        this.fixed = true;
    }
    
    public int compareTo(Object o) {
	return type.compareTo(((RoomObject)o).type);
    }
}
