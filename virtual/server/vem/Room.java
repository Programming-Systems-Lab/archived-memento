package psl.memento.virtual.server.vem;

import java.awt.Polygon;

/*
 * Room.java
 *
 * Created on November 6, 2002, 7:20 PM
 */

/**
 *
 * @author  vlad
 */
public class Room {
    
    public int height, width;
    public Polygon plan;
    
    /** Creates a new instance of Room */
    public Room(int w, int h) {
        setHeight(h);
        setWidth(w);
    }
    
    /** Getter for property height.
     * @return Value of property height.
     *
     */
    public int getHeight() {
        return this.height;
    }
    
    /** Setter for property height.
     * @param height New value of property height.
     *
     */
    public void setHeight(int height) {
        this.height = height;
    }
    
    /** Getter for property width.
     * @return Value of property width.
     *
     */
    public int getWidth() {
        return this.width;
    }
    
    /** Setter for property width.
     * @param width New value of property width.
     *
     */
    public void setWidth(int width) {
        this.width = width;
    }
    
}
