package psl.memento.pervasive.roca.room;

import psl.memento.pervasive.roca.data.*;

/**
 * Describes a door, which provides a link to another virtual room.
 *
 * @author  Kristina Holst
 */
public class Door {
  
  private String mDoorURL;
  private CartesianCoord mPosition;
  private String mWall;
  
  /* Default values for the size of the door, given in meters. */
  private static final int WIDTH = 1;
  private static final int HEIGHT = 2;
  
  /** Creates a new instance of Door
   * @param iDoorURL URL of room to be loaded when user clicks on this door
   * @param iWall wall (N, S, E, W) where door is located
   * @param iSide side of the wall (left, right, middle) where door is located
   */
  public Door(String iDoorURL, String iWall, String iSide) {
    mDoorURL = iDoorURL;
    mWall = iWall;
    mPosition = determinePosition(iWall, iSide);
  }
  
  public Door() {

  }
  
  /** Determines coordinate position of door based on the wall and the side of
   * that wall on which the door is located
   * @param iWall wall (N, S, E, W) where door is located
   * @param iSide side of the wall (left, right, middle) where door is located
   * @return CartesianCoord containing the position of the door's center
   */
  private CartesianCoord determinePosition(String iWall, String iSide) {
    Room room = Room.getInstance();
    
    /* Get wall sizes of the room */
    double widthNSWalls = room.getSpanNSWalls();
    double widthWEWalls = room.getSpanWEWalls();
    
    double x = 0.0, y = 0.0, z = .5 * HEIGHT;
    
    if (iSide.equalsIgnoreCase("left side") && iWall.equalsIgnoreCase("north")) {
      x = .5 * WIDTH;
      y = 0;
    } else if (iSide.equalsIgnoreCase("right side") && iWall.equalsIgnoreCase("north")) {
      x = widthNSWalls - (.5 * WIDTH);
      y = 0;
    } else if (iSide.equalsIgnoreCase("middle") && iWall.equalsIgnoreCase("north")) {
      x = .5 * widthNSWalls;
      y = 0;
    } else if (iSide.equalsIgnoreCase("left side") && iWall.equalsIgnoreCase("east")) {
      x = widthNSWalls;
      y = widthWEWalls - (.5 * WIDTH);
    } else if (iSide.equalsIgnoreCase("right side") && iWall.equalsIgnoreCase("east")) {
      x = widthNSWalls;
      y = .5 * WIDTH;
    } else if (iSide.equalsIgnoreCase("middle") && iWall.equalsIgnoreCase("east")) {
      x = widthNSWalls;
      y = .5 * widthWEWalls;
    } else if (iSide.equalsIgnoreCase("left side") && iWall.equalsIgnoreCase("south")) {
      x = widthNSWalls - (.5 * WIDTH);
      y = widthWEWalls;
    } else if (iSide.equalsIgnoreCase("right side") && iWall.equalsIgnoreCase("south")) {
      x = .5 * WIDTH;
      y = widthWEWalls;
    } else if (iSide.equalsIgnoreCase("middle") && iWall.equalsIgnoreCase("south")) {
      x = .5 * widthNSWalls;
      y = widthWEWalls;
    } else if (iSide.equalsIgnoreCase("left side") && iWall.equalsIgnoreCase("west")) {
      x = 0;
      y = .5 * WIDTH;
    } else if (iSide.equalsIgnoreCase("right side") && iWall.equalsIgnoreCase("west")) {
      x = 0;
      y = widthWEWalls - (.5 * WIDTH);
    } else if (iSide.equalsIgnoreCase("middle") && iWall.equalsIgnoreCase("west")) {
      x = 0;
      y = .5 * widthWEWalls;
    } else
      System.out.println("Error computing door position.");
    
    return new CartesianCoord(x, y, z);
  }

  public void setDoorURL(String iDoorURL) {
    mDoorURL = iDoorURL;
  }  
  
  public String getDoorURL() {
    return mDoorURL;
  }  

  public void setPosition(CartesianCoord iPosition) {
    mPosition = iPosition;
  }
  
  public CartesianCoord getPosition() {
    return mPosition;
  }
  
  public void setWall(String iWall) {
    mWall = iWall;
  }
  
  public String getWall() {
    return mWall;
  }
  
  public boolean isSet() {
    boolean set = false;
    
    if (mPosition != null) {
      set = true;
    }
    
    return set;
  }
}
