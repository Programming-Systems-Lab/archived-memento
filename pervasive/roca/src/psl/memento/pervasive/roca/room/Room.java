package psl.memento.pervasive.roca.room;

import java.util.LinkedList;

/**
 * Describes the room to be modeled.
 *
 * @author Kristina Holst
 */
public class Room {
  
  /* Sole instance */
  private static Room theRoom;
  
  /* URL by which the user can access this room */
  private String mRoomURL;
  
  /* Height of the room. */
  private double mHeight;
  
  /* Width of the east/west walls (assumed to be same value) */
  private double mSpanWEWalls;
  
  /* Width of the north/south walls (assumed to be same value) */
  private double mSpanNSWalls;
  
  /* Door within the room */
  private Door mDoor;
  
  /* Lists of objects located within the room */
  private LinkedList mStationary, mPhysical, mPervasive, mActive;
  
  /* The direction that the user chooses to be considered north. */
  private double mNorth = 0.0;
  
  /** Protected constructor - initializes the lists of objects */
  protected Room() {
    mStationary = new LinkedList();
    mPhysical = new LinkedList();
    mPervasive = new LinkedList();
    mActive = new LinkedList();
  }
  
  /** Call this to obtain instance of Room 
   * @return the sole Room instance
   */
  public static Room getInstance() {
    // If there is no Room already, make a new one
    if (theRoom == null)
      theRoom = new Room();
    
    return theRoom;
  }
  
  /** Reset the Room */
  public void clearRoom() {
    theRoom = null;
  }
 
  /** Sets the height of the room and the width of its walls
   * @param iMeasurements set of measurements given by user
   */
  public void setRoomDimensions(double[] iMeasurements) {
    setSpanWEWalls(iMeasurements[0] + iMeasurements[1]);
    setSpanNSWalls(iMeasurements[2] + iMeasurements[3]);
    setHeight(iMeasurements[4] + iMeasurements[5]);
  }
  
  public void addStationaryObject(RoomObject iObject) {
    mStationary.add(iObject);
  }
  
  public RoomObject getStationaryObject(RoomObject iObject) {
    int index = mStationary.indexOf(iObject);
    RoomObject theObject = (RoomObject)mStationary.get(index);
    
    return theObject;
  }
  
  public LinkedList getAllStationaryObjects() {
    return mStationary;
  }
  
  public void removeStationaryObject(RoomObject iObject) {
    mStationary.remove(iObject);
  }
  
  public void removeAllStationaryObjects() {
    mStationary.clear();
  }
  
  public void addPervasiveObject(RoomObject iObject) {
    mPervasive.add(iObject);
  }
  
  public RoomObject getPervasiveObject(RoomObject iObject) {
    int index = mPervasive.indexOf(iObject);
    RoomObject theObject = (RoomObject)mPervasive.get(index);
    
    return theObject;
  }
  
  public LinkedList getAllPervasiveObjects() {
    return mPervasive;
  }
  
  public void removePervasiveObject(RoomObject iObject) {
    mPervasive.remove(iObject);
  }
  
  public void removeAllPervasiveObjects() {
    mPervasive.clear();
  }
  
  public void addPhysicalObject(RoomObject iObject) {
    mPhysical.add(iObject);
  }
  
  public RoomObject getPhysicalObject(RoomObject iObject) {
    int index = mPhysical.indexOf(iObject);
    RoomObject theObject = (RoomObject)mPhysical.get(index);
    
    return theObject;
  }
  
  public LinkedList getAllPhysicalObjects() {
    return mPhysical;
  }
  
  public void removePhysicalObject(RoomObject iObject) {
    mPhysical.remove(iObject);
  }
  
  public void removeAllPhysicalObjects() {
    mPhysical.clear();
  }
  
  public void addActiveObject(RoomObject iObject) {
    mActive.add(iObject);
  }
  
  public RoomObject getActiveObject(RoomObject iObject) {
    int index = mActive.indexOf(iObject);
    RoomObject theObject = (RoomObject)mActive.get(index);
    
    return theObject;
  }
  
  public LinkedList getAllActiveObjects() {
    return mActive;
  }
  
  public void removeActiveObject(RoomObject iObject) {
    mActive.remove(iObject);
  }
  
  public void removeAllActiveObjects() {
    mActive.clear();
  }
  
  public void setRoomURL(String iRoomURL) {
    mRoomURL = iRoomURL;
  }
  
  public String getRoomURL() {
    return mRoomURL;
  }
  
  public void setHeight(double iHeight) {
    mHeight = iHeight;
  }
  
  public double getHeight() {
    return mHeight;
  }
  
  public void setSpanWEWalls(double iSpanWEWalls) {
    mSpanWEWalls = iSpanWEWalls;
  }
  
  public double getSpanWEWalls() {
    return mSpanWEWalls;
  }
  
  public void setSpanNSWalls(double iSpanNSWalls) {
    mSpanNSWalls = iSpanNSWalls;
  }
  
  public double getSpanNSWalls() {
    return mSpanNSWalls;
  }
  
  public void setDoor(String iDoorURL, String iWall, String iSide) {
    mDoor = new Door(iDoorURL, iWall, iSide);
  }
  
  public Door getDoor() {
    return mDoor;
  }
  
  public void setNorth(double iNorth) {
    mNorth = iNorth;
  }
  
  public double getNorth() {
    return mNorth;
  }
}