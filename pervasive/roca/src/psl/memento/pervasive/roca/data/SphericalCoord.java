package psl.memento.pervasive.roca.data;

import psl.memento.pervasive.roca.room.Room;

/** 
 * Describes a point in 3D space, as given by spherical coordinates.
 *
 * @author Kristina Holst
 */
public class SphericalCoord {
  
  private double mDistance, mDirection, mIncline;
  
  /** Creates a new instance of CartesianCoord
   * @param iDistance the distance from origin to point P
   * @param iDirection the angle between the line OP and the positive polar axis
   * @param iIncline the angle between the initial ray and the projection of OP to the equatorial plane
   */
  public SphericalCoord(double iDistance, double iDirection, double iIncline) {
    mDistance = iDistance;
    mDirection = iDirection;
    mIncline = iIncline;
  }
  
  /*
   * Since user defines which direction is north, the measurements
   * must be adjusted to match this
   */
  public void synchronizeDirectionalOrientation() {
    Room room = Room.getInstance();
    
    mDirection = (mDirection + room.getNorth()) % 360;
  }
  
  public void setDistance(double iDistance) {
    mDistance = iDistance;
  }
  
  public double getDistance() {
    return mDistance;
  }
  
  public void setDirection(double iDirection) {
    mDirection = iDirection;
  }
  
  public double getDirection() {
    return mDirection;
  }
  
  public void setIncline(double iIncline) {
    mIncline = iIncline;
  }
  
  public double getIncline() {
    return mIncline;
  }
}

