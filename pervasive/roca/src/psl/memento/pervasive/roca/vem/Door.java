/*
 * This is one of Vlad's files, up-to-date as of 12-22-02.
 *
 */

package psl.memento.pervasive.roca.vem;

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
