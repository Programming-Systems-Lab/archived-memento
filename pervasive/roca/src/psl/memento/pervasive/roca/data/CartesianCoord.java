package psl.memento.pervasive.roca.data;

import java.lang.Math;

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
}
