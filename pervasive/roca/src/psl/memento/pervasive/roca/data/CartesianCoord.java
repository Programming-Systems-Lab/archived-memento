package psl.memento.pervasive.roca.data;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.Math;
import psl.memento.pervasive.roca.room.RoomObject;

/**
 * Describes a point in 3D space, as given by Cartesian coordinates.
 *
 * @author Kristina Holst
 */
public class CartesianCoord {
    
    private double mX, mY, mZ;
    
    /** Creates a new instance of CartesianCoord. */
    public CartesianCoord() {
    }
    
    /** Creates a new instance of CartesianCoord.
     * @param iX x-coordinate
     * @param iY y-coordinate
     * @param iZ z-coordinate
     */
    public CartesianCoord(double iX, double iY, double iZ) {
        mX = iX;
        mY = iY;
        mZ = iZ;
    }
    
    /** Performs transformation from spherical to Cartesian coordinate system.
     * @param iSpher
     */
    public void createFromSphericalCoord(SphericalCoord iSpher) {
        setX(iSpher.getDistance() * Math.cos(iSpher.getDirection()) * Math.sin(iSpher.getIncline()));
        setY(iSpher.getDistance() * Math.sin(iSpher.getDirection()) * Math.sin(iSpher.getIncline()));
        setZ(iSpher.getDistance() * Math.cos(iSpher.getIncline()));
    }
    
    public boolean checkOverlap(SizeData iSpaceNeeded, RoomObject iOccupied) {
        boolean conflict = false;
        /* All dimensions and coordinates must be converted to int's to use the classes
         * in java.awt, so to avoid losing precision, multiply these doubles by some large
         * conversion factor before casting to an int, then divide by the conversion factor
         * to restore the original value */
        int conversionFactor = 100000;
        Point pPlacing, pOccupied;
        Dimension dPlacing, dOccupied;
        
        int xPlacingInt = (int)(mX * conversionFactor);
        int yPlacingInt = (int)(mY * conversionFactor);
        int zPlacingInt = (int)(mZ * conversionFactor);
        
        int widthPlacingInt = (int)(iSpaceNeeded.getWidth() * conversionFactor);
        int lengthPlacingInt = (int)(iSpaceNeeded.getLength() * conversionFactor);
        int heightPlacingInt = (int)(iSpaceNeeded.getHeight() * conversionFactor);
        
        CartesianCoord posOccupied = iOccupied.getPosition();
        SizeData sizeOccupied = iOccupied.getSize();
        
        int xOccupiedInt = (int)(posOccupied.getX() * conversionFactor);
        int yOccupiedInt = (int)(posOccupied.getY() * conversionFactor);
        int zOccupiedInt = (int)(posOccupied.getZ() * conversionFactor);
        
        int widthOccupiedInt = (int)(sizeOccupied.getWidth() * conversionFactor);
        int lengthOccupiedInt = (int)(sizeOccupied.getLength() * conversionFactor);
        int heightOccupiedInt = (int)(sizeOccupied.getHeight() * conversionFactor);
                
        pPlacing = new Point(xPlacingInt, yPlacingInt);
        dPlacing = new Dimension(widthPlacingInt, lengthPlacingInt);
        Rectangle xyPlane1 = new Rectangle(pPlacing, dPlacing);
        
        pOccupied = new Point(xOccupiedInt, yOccupiedInt);
        dOccupied = new Dimension(widthOccupiedInt, lengthOccupiedInt);
        Rectangle xyPlane2 = new Rectangle(pOccupied, dOccupied);

        pPlacing = new Point(xPlacingInt, zPlacingInt);
        dPlacing = new Dimension(widthPlacingInt, heightPlacingInt);
        Rectangle xzPlane1 = new Rectangle(pPlacing, dPlacing);

        pOccupied = new Point(xOccupiedInt, zOccupiedInt);
        dOccupied = new Dimension(widthOccupiedInt, heightOccupiedInt);
        Rectangle xzPlane2 = new Rectangle(pOccupied, dOccupied);
        
        pPlacing = new Point(yPlacingInt, zPlacingInt);
        dPlacing = new Dimension(lengthPlacingInt, heightPlacingInt);
        Rectangle yzPlane1 = new Rectangle(pPlacing, dPlacing);

        pOccupied = new Point(yOccupiedInt, zOccupiedInt);
        dOccupied = new Dimension(lengthOccupiedInt, heightOccupiedInt);
        Rectangle yzPlane2 = new Rectangle(pOccupied, dOccupied);

        if (xyPlane1.intersects(xyPlane2) && xzPlane1.intersects(xzPlane2) &&
            yzPlane1.intersects(yzPlane2)) {
                conflict = true;
        }

        return conflict;
    }
    
    public void setX(double iX) {
        mX = iX;
    }
    
    public double getX() {
        return mX;
    }
    
    public void setY(double iY) {
        mY = iY;
    }
    
    public double getY() {
        return mY;
    }
    
    public void setZ(double iZ) {
        mZ = iZ;
    }
    
    public double getZ() {
        return mZ;
    }
    
    public String toString() {
        return "(" + mX + ", " + mY + ", " + mZ + ")";
    }
}
