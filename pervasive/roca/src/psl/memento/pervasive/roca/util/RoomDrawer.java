/*
 * RoomDrawer.java
 *
 * Created on January 26, 2003, 2:15 PM
 */

package psl.memento.pervasive.roca.util;

import psl.memento.pervasive.roca.room.*;
import psl.memento.pervasive.roca.vem.*;
import psl.memento.pervasive.roca.data.*;
import psl.memento.pervasive.roca.gui.ObjectTab;
import java.util.LinkedList;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author  Kristina Holst
 */
public class RoomDrawer {
  private static RoomDrawer theDrawer;
  private DataReader dataReader;
  private boolean placingObject;
  private int dx, dy, dz;
  private double x, y, z = -1;
  private static final int PIXEL_CONVERSION_FACTOR = 70;
  
  /** Creates a new instance of RoomDrawer */
  protected RoomDrawer() {
  }
  
  public static RoomDrawer getInstance() {
    if (theDrawer == null) {
      theDrawer = new RoomDrawer();
    }
    
    return theDrawer;
  }
  
  public void createPreview(RoomViewerPanel iRoomPanel) {
    dataReader = new DataReader();
    
    psl.memento.pervasive.roca.room.Room room = psl.memento.pervasive.roca.room.Room.getInstance();
    
    int width = (int)room.getSpanWEWalls();  // distance from W wall to E wall
    int length = (int)room.getSpanNSWalls(); // distance from N wall to S wall
    int height = (int)room.getHeight();
    
    dataReader.setRoom(PIXEL_CONVERSION_FACTOR * length, PIXEL_CONVERSION_FACTOR * width, PIXEL_CONVERSION_FACTOR * height);
    
    psl.memento.pervasive.roca.room.Door door = room.getDoor();
    
    if ((door != null) && (door.isSet())) {
      CartesianCoord doorPos = door.getPosition();
      String wall = door.getWall();
      char wallChar = 'n';  // default value
      int offset = 100;     // default value
      
      if (wall != null && doorPos != null) {
        if (wall.equalsIgnoreCase("north")) {
          wallChar = 'n';
          offset = (int)(doorPos.getX() * PIXEL_CONVERSION_FACTOR);
        } else if (wall.equalsIgnoreCase("south")) {
          wallChar = 's';
          offset = (int)(doorPos.getX() * PIXEL_CONVERSION_FACTOR);
        } else if (wall.equalsIgnoreCase("west")) {
          wallChar = 'w';
          offset = (int)(doorPos.getY() * PIXEL_CONVERSION_FACTOR);
        } else if (wall.equalsIgnoreCase("east")) {
          wallChar = 'e';
          offset = (int)(doorPos.getY() * PIXEL_CONVERSION_FACTOR);
        }
      }
      
      dataReader.addDoor(wallChar, offset, 20);
    }
    
    LinkedList roomObjects = room.getAllStationaryObjects();
    psl.memento.pervasive.roca.room.RoomObject currentObject;
    psl.memento.pervasive.roca.vem.RoomObject vladsObject;
    
    for (int i = 0; i < roomObjects.size(); i++) {
      currentObject = (psl.memento.pervasive.roca.room.RoomObject)roomObjects.get(i);
      SizeData size = currentObject.getPlacingSize();
      CartesianCoord pos = currentObject.getPosition();
      vladsObject = new psl.memento.pervasive.roca.vem.RoomObject((int)(size.getWidth() * PIXEL_CONVERSION_FACTOR), (int)(size.getLength() * PIXEL_CONVERSION_FACTOR),
      (int)(size.getHeight() * PIXEL_CONVERSION_FACTOR), (int)(pos.getX() * PIXEL_CONVERSION_FACTOR), (int)(pos.getY() * PIXEL_CONVERSION_FACTOR), (int)(pos.getZ() * PIXEL_CONVERSION_FACTOR));
      vladsObject.type = "stationary";
      dataReader.addRoomObject(vladsObject);
    }
    
    roomObjects = room.getAllPervasiveObjects();
    
    for (int i = 0; i < roomObjects.size(); i++) {
      currentObject = (psl.memento.pervasive.roca.room.RoomObject)roomObjects.get(i);
      SizeData size = currentObject.getPlacingSize();
      CartesianCoord pos = currentObject.getPosition();
      vladsObject = new psl.memento.pervasive.roca.vem.RoomObject((int)(size.getWidth() * PIXEL_CONVERSION_FACTOR), (int)(size.getLength() * PIXEL_CONVERSION_FACTOR),
      (int)(size.getHeight() * PIXEL_CONVERSION_FACTOR), (int)(pos.getX() * PIXEL_CONVERSION_FACTOR), (int)(pos.getY() * PIXEL_CONVERSION_FACTOR), (int)(pos.getZ() * PIXEL_CONVERSION_FACTOR));
      vladsObject.type = "pervasive";
      dataReader.addRoomObject(vladsObject);
    }
    
    roomObjects = room.getAllPhysicalObjects();
    
    for (int i = 0; i < roomObjects.size(); i++) {
      currentObject = (psl.memento.pervasive.roca.room.RoomObject)roomObjects.get(i);
      SizeData size = currentObject.getPlacingSize();
      CartesianCoord pos = currentObject.getPosition();
      vladsObject = new psl.memento.pervasive.roca.vem.RoomObject((int)(size.getWidth() * PIXEL_CONVERSION_FACTOR), (int)(size.getLength() * PIXEL_CONVERSION_FACTOR),
      (int)(size.getHeight() * PIXEL_CONVERSION_FACTOR), (int)(pos.getX() * PIXEL_CONVERSION_FACTOR), (int)(pos.getY() * PIXEL_CONVERSION_FACTOR), (int)(pos.getZ() * PIXEL_CONVERSION_FACTOR));
      vladsObject.type = "physical";
      dataReader.addRoomObject(vladsObject);
    }
    
    roomObjects = room.getAllActiveObjects();
    
    for (int i = 0; i < roomObjects.size(); i++) {
      currentObject = (psl.memento.pervasive.roca.room.RoomObject)roomObjects.get(i);
      SizeData size = currentObject.getPlacingSize();
      CartesianCoord pos = currentObject.getPosition();
      vladsObject = new psl.memento.pervasive.roca.vem.RoomObject((int)(size.getWidth() * PIXEL_CONVERSION_FACTOR), (int)(size.getLength() * PIXEL_CONVERSION_FACTOR),
      (int)(size.getHeight() * PIXEL_CONVERSION_FACTOR), (int)(pos.getX() * PIXEL_CONVERSION_FACTOR), (int)(pos.getY() * PIXEL_CONVERSION_FACTOR), (int)(pos.getZ() * PIXEL_CONVERSION_FACTOR));
      vladsObject.type = "active";
      dataReader.addRoomObject(vladsObject);
    }
    
    
    // Tell the panel where to get its room information
    iRoomPanel.setRoomInformation(dataReader);
    // Draw the room!
    // the last method can be called every time changes are made to the room
    iRoomPanel.updateRoomView();
  }
  
  public void beginPlacingNewObject(SizeData iSize) {
    dx = (int)(iSize.getWidth() * PIXEL_CONVERSION_FACTOR);
    dy = (int)(iSize.getLength() * PIXEL_CONVERSION_FACTOR);
    dz = (int)(iSize.getHeight() * PIXEL_CONVERSION_FACTOR);
    
    placingObject = true;
  }
  
  public void newObjectMoving(MouseEvent e, RoomViewerPanel iRoomPanel) {
    if (placingObject) {
      iRoomPanel.redraw();   // erase the previous outline
      iRoomPanel.getGraphics().drawRect(e.getX(), e.getY(), dx, dy);
    }
  }
  
  public void newObjectPlaced(MouseEvent e, RoomViewerPanel iRoomPanel) {
    if (placingObject) {
      x = e.getX() / (double)PIXEL_CONVERSION_FACTOR;
      y = e.getY() / (double)PIXEL_CONVERSION_FACTOR;
      
      String zValue = JOptionPane.showInputDialog("Enter the object's distance from the floor (meters):", "0");
      
      try {
        z = Double.parseDouble(zValue);
      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(null, zValue + " is not a valid number", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      
      ObjectTab objectTab = ObjectTab.getInstance();
      psl.memento.pervasive.roca.room.RoomObject obj = objectTab.getCurrentObject();
      
      psl.memento.pervasive.roca.room.Room room = psl.memento.pervasive.roca.room.Room.getInstance();
      
      if (z + obj.getPlacingSize().getHeight() > room.getHeight()) {
        JOptionPane.showMessageDialog(null, "Object does not fit at this height. Try a new position.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      
      psl.memento.pervasive.roca.vem.RoomObject ro = new psl.memento.pervasive.roca.vem.RoomObject(dx, dy, dz,
      e.getX(), e.getY(), (int)(z * PIXEL_CONVERSION_FACTOR));
      dataReader.addRoomObject(ro);
      iRoomPanel.updateRoomView();
      
      placingObject = false;
      
      
      objectTab.finishAddObject(new CartesianCoord(x, y, z));
      
      JTabbedPane tabbedPane = (JTabbedPane)objectTab.getMainPanel().getParent();
      tabbedPane.setSelectedIndex(1);
    }
  }
  
  public boolean isPlacingObject() {
    return placingObject;
  }
}
