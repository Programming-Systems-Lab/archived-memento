package psl.memento.server.vem;

/*
 * Door.java
 *
 * Created on November 6, 2002, 7:38 PM
 */

/**
 *
 * @author  vlad
 */
public class Door {
    
    public static final char NORTH = 'n';
    public static final char SOUTH = 's';
    public static final char EAST  = 'e';
    public static final char WEST  = 'w';
    
    public char mWall;
    public int mOffset, mLength;
    
    /** Creates a new instance of Door */
    public Door(char whichWall, int offset, int length) {
        mWall = whichWall;
        mOffset = offset;
        mLength = length;
    }
    
}
