package psl.memento.pervasive.roca.util;

import java.util.List;
import java.text.DecimalFormat;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import psl.memento.pervasive.roca.room.*;
import psl.memento.pervasive.roca.data.*;
import psl.memento.pervasive.roca.gui.ObjectTab;

/*
 * RoomBuilder.java
 *
 * @author  Kristina Holst
 */
public class RoomBuilder {
  
  /** Creates a new instance of RoomBuilder */
  public RoomBuilder() {
  }
  
  public boolean buildRoomFromDoc(Document doc) {
    boolean success = true;
    Room room = Room.getInstance();
    
    Element elemCompleteRoom = doc.getRootElement();
    
    Element elemRoomURL;
    if ((elemRoomURL = elemCompleteRoom.getChild("RoomURL")) != null) {
      room.setRoomURL(elemRoomURL.getText());
    }
    
    Element elemRoomBoundaries;
    if ((elemRoomBoundaries = elemCompleteRoom.getChild("RoomBoundaries")) == null) {
      return false;
    }
    
    List points = elemRoomBoundaries.getChildren("Point");
    
    boolean xFound = false, yFound = false, zFound = false;
    for (int i = 0; (i < points.size()) && (!xFound || !yFound || !zFound); i++) {
      Element point = (Element)points.get(i);
      
      if (!xFound) {
        String xString = point.getAttributeValue("x");
        double x;
        if ((x = Double.parseDouble(xString)) > 0) {
          room.setSpanNSWalls(x);
          xFound = true;
        }
      }
      
      if (!yFound) {
        String yString = point.getAttributeValue("y");
        double y;
        if ((y = Double.parseDouble(yString)) > 0) {
          room.setSpanWEWalls(y);
          yFound = true;
        }
      }
      
      if (!zFound) {
        String zString = point.getAttributeValue("z");
        double z;
        if ((z = Double.parseDouble(zString)) > 0) {
          room.setHeight(z);
          zFound = true;
        }
      }
    }
    
    Element elemDoor;
    if ((elemDoor = elemCompleteRoom.getChild("Door")) == null) {
      return false;
    }
    
    Element elemDoorURL, elemDoorPosition;
    String doorURL;
    Door door = new Door();
    if ((elemDoorURL = elemDoor.getChild("DoorURL")) != null) {
      door.setDoorURL(elemDoorURL.getText());
    }
    
    if ((elemDoorPosition = elemDoor.getChild("DoorPosition")) != null) {
      double x = Double.parseDouble(elemDoorPosition.getAttributeValue("x"));
      double y = Double.parseDouble(elemDoorPosition.getAttributeValue("y"));
      double z = Double.parseDouble(elemDoorPosition.getAttributeValue("z"));
      
      door.setPosition(new CartesianCoord(x, y, z));
    }
    
    room.setDoor(door);
    
    Element elemPervasiveObjects;
    if ((elemPervasiveObjects = elemCompleteRoom.getChild("PervasiveObjects")) != null) {
      List objects = elemPervasiveObjects.getChildren("Object");
      
      for (int i = 0; i < objects.size(); i++) {
        Element obj = (Element)objects.get(i);
        
        Element elemType;
        String type = "";
        if ((elemType = obj.getChild("Type")) != null) {
          type = elemType.getText();
        }
        
        Element elemSize;
        SizeData size = null;
        if ((elemSize = obj.getChild("Size")) != null) {
          double width = Double.parseDouble(elemSize.getAttributeValue("width"));
          double length = Double.parseDouble(elemSize.getAttributeValue("length"));
          double height = Double.parseDouble(elemSize.getAttributeValue("height"));
          
          DecimalFormat df = new DecimalFormat("######0.####");
          
          size = new SizeData(Double.parseDouble(df.format(width)), Double.parseDouble(df.format(length)), 
          Double.parseDouble(df.format(height)));
        }
        
        Element elemPosition;
        CartesianCoord pos = null;
        if ((elemPosition = obj.getChild("Position")) != null) {
          double x = Double.parseDouble(elemPosition.getAttributeValue("x"));
          double y = Double.parseDouble(elemPosition.getAttributeValue("y"));
          double z = Double.parseDouble(elemPosition.getAttributeValue("z"));
          
          pos = new CartesianCoord(x, y, z);
        }
        
        Element elemRotation;
        RotationData rot = null;
        if ((elemRotation = obj.getChild("Rotation")) != null) {
          int yaw = Integer.parseInt(elemRotation.getAttributeValue("yaw"));
          int roll = Integer.parseInt(elemRotation.getAttributeValue("roll"));
          int pitch = Integer.parseInt(elemRotation.getAttributeValue("pitch"));
          
          rot = new RotationData(yaw, roll, pitch);
        }
       
        String category = "Pervasive";

        RoomObject roomObject = new RoomObject(category, type, size, pos, rot);
        
        room.addPervasiveObject(roomObject);
        
        ObjectTab objectTab = ObjectTab.getInstance();
        objectTab.addObjectToListModel(roomObject);
      }
    }
    
    Element elemPhysicalObjects;
    if ((elemPhysicalObjects = elemCompleteRoom.getChild("PhysicalObjects")) != null) {
      List objects = elemPhysicalObjects.getChildren("Object");
      
      for (int i = 0; i < objects.size(); i++) {
        Element obj = (Element)objects.get(i);
        
        Element elemType;
        String type = "";
        if ((elemType = obj.getChild("Type")) != null) {
          type = elemType.getText();
        }
        
        Element elemSize;
        SizeData size = null;
        if ((elemSize = obj.getChild("Size")) != null) {
          double width = Double.parseDouble(elemSize.getAttributeValue("width"));
          double length = Double.parseDouble(elemSize.getAttributeValue("length"));
          double height = Double.parseDouble(elemSize.getAttributeValue("height"));
          
          DecimalFormat df = new DecimalFormat("######0.####");
          
          size = new SizeData(Double.parseDouble(df.format(width)), Double.parseDouble(df.format(length)), 
          Double.parseDouble(df.format(height)));
        }
        
        Element elemPosition;
        CartesianCoord pos = null;
        if ((elemPosition = obj.getChild("Position")) != null) {
          double x = Double.parseDouble(elemPosition.getAttributeValue("x"));
          double y = Double.parseDouble(elemPosition.getAttributeValue("y"));
          double z = Double.parseDouble(elemPosition.getAttributeValue("z"));
          
          pos = new CartesianCoord(x, y, z);
        }
        
        Element elemRotation;
        RotationData rot = null;
        if ((elemRotation = obj.getChild("Rotation")) != null) {
          int yaw = Integer.parseInt(elemRotation.getAttributeValue("yaw"));
          int roll = Integer.parseInt(elemRotation.getAttributeValue("roll"));
          int pitch = Integer.parseInt(elemRotation.getAttributeValue("pitch"));
          
          rot = new RotationData(yaw, roll, pitch);
        }      
        
        String category = "Physical";

        RoomObject roomObject = new RoomObject(category, type, size, pos, rot);
        
        room.addPhysicalObject(roomObject);
        
        ObjectTab objectTab = ObjectTab.getInstance();
        objectTab.addObjectToListModel(roomObject);
      }
    }
    
    Element elemActiveObjects;
    if ((elemActiveObjects = elemCompleteRoom.getChild("ActiveObjects")) != null) {
      List objects = elemActiveObjects.getChildren("Object");
      
      for (int i = 0; i < objects.size(); i++) {
        Element obj = (Element)objects.get(i);
        
        Element elemType;
        String type = "";
        if ((elemType = obj.getChild("Type")) != null) {
          type = elemType.getText();
        }
        
        Element elemSize;
        SizeData size = null;
        if ((elemSize = obj.getChild("Size")) != null) {
          double width = Double.parseDouble(elemSize.getAttributeValue("width"));
          double length = Double.parseDouble(elemSize.getAttributeValue("length"));
          double height = Double.parseDouble(elemSize.getAttributeValue("height"));
          
          DecimalFormat df = new DecimalFormat("######0.####");
          
          size = new SizeData(Double.parseDouble(df.format(width)), Double.parseDouble(df.format(length)), 
          Double.parseDouble(df.format(height)));
        }
        
        Element elemPosition;
        CartesianCoord pos = null;
        if ((elemPosition = obj.getChild("Position")) != null) {
          double x = Double.parseDouble(elemPosition.getAttributeValue("x"));
          double y = Double.parseDouble(elemPosition.getAttributeValue("y"));
          double z = Double.parseDouble(elemPosition.getAttributeValue("z"));
          
          pos = new CartesianCoord(x, y, z);
        }
        
        Element elemRotation;
        RotationData rot = null;
        if ((elemRotation = obj.getChild("Rotation")) != null) {
          int yaw = Integer.parseInt(elemRotation.getAttributeValue("yaw"));
          int roll = Integer.parseInt(elemRotation.getAttributeValue("roll"));
          int pitch = Integer.parseInt(elemRotation.getAttributeValue("pitch"));
          
          rot = new RotationData(yaw, roll, pitch);
        }      
        
        String category = "Active";

        RoomObject roomObject = new RoomObject(category, type, size, pos, rot);
        
        room.addActiveObject(roomObject);
        
        ObjectTab objectTab = ObjectTab.getInstance();
        objectTab.addObjectToListModel(roomObject);
      }
    }
    
    Element elemStationaryObjects;
    if ((elemStationaryObjects = elemCompleteRoom.getChild("StationaryObjects")) != null) {
      List objects = elemStationaryObjects.getChildren("Object");
      
      for (int i = 0; i < objects.size(); i++) {
        Element obj = (Element)objects.get(i);
        
        Element elemType;
        String type = "";
        if ((elemType = obj.getChild("Type")) != null) {
          type = elemType.getText();
        }
        
        Element elemSize;
        SizeData size = null;
        if ((elemSize = obj.getChild("Size")) != null) {
          double width = Double.parseDouble(elemSize.getAttributeValue("width"));
          double length = Double.parseDouble(elemSize.getAttributeValue("length"));
          double height = Double.parseDouble(elemSize.getAttributeValue("height"));
          
          DecimalFormat df = new DecimalFormat("######0.####");
          
          size = new SizeData(Double.parseDouble(df.format(width)), Double.parseDouble(df.format(length)), 
          Double.parseDouble(df.format(height)));
        }
        
        Element elemPosition;
        CartesianCoord pos = null;
        if ((elemPosition = obj.getChild("Position")) != null) {
          double x = Double.parseDouble(elemPosition.getAttributeValue("x"));
          double y = Double.parseDouble(elemPosition.getAttributeValue("y"));
          double z = Double.parseDouble(elemPosition.getAttributeValue("z"));
          
          pos = new CartesianCoord(x, y, z);
        }
        
        Element elemRotation;
        RotationData rot = null;
        if ((elemRotation = obj.getChild("Rotation")) != null) {
          int yaw = Integer.parseInt(elemRotation.getAttributeValue("yaw"));
          int roll = Integer.parseInt(elemRotation.getAttributeValue("roll"));
          int pitch = Integer.parseInt(elemRotation.getAttributeValue("pitch"));
          
          rot = new RotationData(yaw, roll, pitch);
        }
        
        
        String category = "Stationary";

        RoomObject roomObject = new RoomObject(category, type, size, pos, rot);
        
        room.addStationaryObject(roomObject);
        
        ObjectTab objectTab = ObjectTab.getInstance();
        objectTab.addObjectToListModel(roomObject);
      }
    }
    
    return success;
  }
  
}
