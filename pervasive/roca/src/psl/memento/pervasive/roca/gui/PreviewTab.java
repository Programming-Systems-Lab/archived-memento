package psl.memento.pervasive.roca.gui;

import psl.memento.pervasive.roca.vem.*;
import psl.memento.pervasive.roca.room.*;
import psl.memento.pervasive.roca.data.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.LinkedList;

public class PreviewTab extends JPanel implements ActionListener {
  private JPanel mainPanel;
  private JButton previewButton;
  private RoomViewerPanel roomPanel;
  private DataReader dataReader;
  private static final int PIXEL_CONVERSION_FACTOR = 70;
  
  /** Creates a new instance of PreviewTab */
  public PreviewTab() {
    previewButton = new JButton("Generate Preview");
    previewButton.addActionListener(this);
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(previewButton);
    
    roomPanel = new RoomViewerPanel();
    roomPanel.setPreferredSize(new Dimension(500, 500));
    
    /* To get everything to stay aligned when window is maximized, we need to
     * add all the components to an intermediate panel. */
    JPanel dummyPanel = new JPanel();
    dummyPanel.setLayout(new BorderLayout());
    dummyPanel.add(buttonPanel, BorderLayout.NORTH);
    dummyPanel.add(roomPanel, BorderLayout.CENTER);
    
    mainPanel = new JPanel();
    mainPanel.add(dummyPanel);
  }
  
  public Component getMainPanel() {
    return mainPanel;
  }
  
  private void createPreview() {
    dataReader = new DataReader();
    
    psl.memento.pervasive.roca.room.Room room = psl.memento.pervasive.roca.room.Room.getInstance();
    
    int width = (int)room.getSpanWEWalls();  // distance from W wall to E wall
    int length = (int)room.getSpanNSWalls(); // distance from N wall to S wall
    int height = (int)room.getHeight();
    
    dataReader.setRoom(80 * length, 80 * width, 80 * height);
    dataReader.addDoor('n', 100, 20);
    
    LinkedList roomObjects = room.getAllStationaryObjects();
    psl.memento.pervasive.roca.room.RoomObject currentObject;
    psl.memento.pervasive.roca.vem.RoomObject vladsObject;
    
    for (int i = 0; i < roomObjects.size(); i++) 
    {
      currentObject = (psl.memento.pervasive.roca.room.RoomObject)roomObjects.get(i);
      SizeData size = currentObject.getSize();
      CartesianCoord pos = currentObject.getPosition();
      vladsObject = new psl.memento.pervasive.roca.vem.RoomObject((int)(size.getWidth() * PIXEL_CONVERSION_FACTOR), (int)(size.getLength() * PIXEL_CONVERSION_FACTOR),
      (int)(size.getHeight() * PIXEL_CONVERSION_FACTOR), (int)(pos.getX() * PIXEL_CONVERSION_FACTOR), (int)(pos.getY() * PIXEL_CONVERSION_FACTOR), (int)(pos.getZ() * PIXEL_CONVERSION_FACTOR));
      dataReader.addRoomObject(vladsObject);
    }
    
    roomObjects = room.getAllPervasiveObjects();

    for (int i = 0; i < roomObjects.size(); i++) 
    {
      currentObject = (psl.memento.pervasive.roca.room.RoomObject)roomObjects.get(i);
      SizeData size = currentObject.getSize();
      CartesianCoord pos = currentObject.getPosition();
      vladsObject = new psl.memento.pervasive.roca.vem.RoomObject((int)(size.getWidth() * PIXEL_CONVERSION_FACTOR), (int)(size.getLength() * PIXEL_CONVERSION_FACTOR),
      (int)(size.getHeight() * PIXEL_CONVERSION_FACTOR), (int)(pos.getX() * PIXEL_CONVERSION_FACTOR), (int)(pos.getY() * PIXEL_CONVERSION_FACTOR), (int)(pos.getZ() * PIXEL_CONVERSION_FACTOR));
      dataReader.addRoomObject(vladsObject);
    }
    
    roomObjects = room.getAllPhysicalObjects();

    for (int i = 0; i < roomObjects.size(); i++) 
    {
      currentObject = (psl.memento.pervasive.roca.room.RoomObject)roomObjects.get(i);
      SizeData size = currentObject.getSize();
      CartesianCoord pos = currentObject.getPosition();
      vladsObject = new psl.memento.pervasive.roca.vem.RoomObject((int)(size.getWidth() * PIXEL_CONVERSION_FACTOR), (int)(size.getLength() * PIXEL_CONVERSION_FACTOR),
      (int)(size.getHeight() * PIXEL_CONVERSION_FACTOR), (int)(pos.getX() * PIXEL_CONVERSION_FACTOR), (int)(pos.getY() * PIXEL_CONVERSION_FACTOR), (int)(pos.getZ() * PIXEL_CONVERSION_FACTOR));
      dataReader.addRoomObject(vladsObject);
    }
    
    roomObjects = room.getAllActiveObjects();

    for (int i = 0; i < roomObjects.size(); i++) 
    {
      currentObject = (psl.memento.pervasive.roca.room.RoomObject)roomObjects.get(i);
      SizeData size = currentObject.getSize();
      CartesianCoord pos = currentObject.getPosition();
      vladsObject = new psl.memento.pervasive.roca.vem.RoomObject((int)(size.getWidth() * PIXEL_CONVERSION_FACTOR), (int)(size.getLength() * PIXEL_CONVERSION_FACTOR),
      (int)(size.getHeight() * PIXEL_CONVERSION_FACTOR), (int)(pos.getX() * PIXEL_CONVERSION_FACTOR), (int)(pos.getY() * PIXEL_CONVERSION_FACTOR), (int)(pos.getZ() * PIXEL_CONVERSION_FACTOR));
      dataReader.addRoomObject(vladsObject);
    }
    
    
    /*
    // create a new object, a cube with side of length 30
    // position it at location (10, 10, 10)
    psl.memento.pervasive.roca.vem.RoomObject ro = new psl.memento.pervasive.roca.vem.RoomObject(30, 30, 30, 10, 10, 10);
    // set the object's type (optional)
    ro.type = "table";
    
    // add the object to the room
    dataReader.addRoomObject(ro);
    
    // create a new object, a cube with side of length 30
    // position it at location (10, 10, 10)
    psl.memento.pervasive.roca.vem.RoomObject ro2 = new psl.memento.pervasive.roca.vem.RoomObject(100, 20, 30, 80, 80, 80);
    // set the object's type (optional)
    ro2.type = "chair";
    
    // add the object to the room
    dataReader.addRoomObject(ro2);
    */
    
    // Tell the panel where to get its room information
    roomPanel.setRoomInformation(dataReader);
    // Draw the room!
    // the last method can be called every time changes are made to the room
    roomPanel.updateRoomView();
  }
  
  public void actionPerformed(java.awt.event.ActionEvent e) {
    Object source = e.getSource();
    
    if (source == previewButton) {
      createPreview();
    }
  }
  
}
