package psl.memento.pervasive.roca.gui;

import psl.memento.pervasive.roca.room.*;
import psl.memento.pervasive.roca.util.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jdom.*;
import org.jdom.output.*;

public class SummaryTab extends JPanel implements ActionListener {
  
  private JPanel mainPanel, buttonPanel;
  private JScrollPane textScrollPane;
  private JButton generateSummary, saveRoom;
  private JTextArea textBox;
  
  public SummaryTab() {
    //saveRoom = new JButton("Save This Room");
    //saveRoom.addActionListener(this);
    generateSummary = new JButton("Generate Summary");
    generateSummary.addActionListener(this);
    
    textBox = new JTextArea("", 25, 55);
    textBox.setEditable(false);
    textBox.setLineWrap(true);
    textBox.setWrapStyleWord(true);
    
    textScrollPane = new JScrollPane(textBox);
    textScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
    
    buttonPanel = new JPanel();
    //buttonPanel.add(saveRoom);
    buttonPanel.add(Box.createRigidArea(new Dimension(10, 1)));
    buttonPanel.add(generateSummary);
    
    /* To get everything to stay aligned when window is maximized, we need to
     * add all the components to an intermediate panel. */
    JPanel dummyPanel = new JPanel();
    dummyPanel.setLayout(new BorderLayout());
    dummyPanel.add(buttonPanel, BorderLayout.NORTH);
    dummyPanel.add(textScrollPane, BorderLayout.CENTER);
    
    mainPanel = new JPanel();
    mainPanel.add(dummyPanel);
  }
  
  public Component getMainPanel() {
    return mainPanel;
  }
  
  /*
  public void generateSummary() {
    textBox.setText("");
   
    Document doc = XMLGenerator.generateRoomXML();
    XMLOutputter outputter = new XMLOutputter("   ", true);
   
    textBox.append(outputter.outputString(doc));
   
    Room room = Room.getInstance();
   
    textBox.setText("");
   
    if (room != null) {
      textBox.append("Current Room\n");
   
      String roomURL;
   
      if (!(roomURL = room.getRoomURL()).equals(""))
        textBox.append("Room URL: " + roomURL + "\n");
   
      double height = room.getHeight();
      double width = room.getSpanNSWalls();
      double depth = room.getSpanWEWalls();
   
      textBox.append("Width of N/S walls: " + width + "\n");
      textBox.append("Width of W/E walls: " + depth + "\n");
      textBox.append("Height: " + height + "\n");
   
      Element elemDoor = new Element("Door");
   
      String doorURL;
      if (!(doorURL = room.getDoorURL()).equals("")) {
        Element elemDoorURL = new Element("DoorURL");
        elemDoorURL.addContent(doorURL);
        elemDoor.addContent(elemDoorURL);
      }
   
      CartesianCoord doorPosition;
      if ((doorPosition = room.getDoorPosition()) != null) {
        Element elemDoorPosition = new Element("DoorPosition");
        elemDoorPosition.setAttribute("x", String.valueOf(doorPosition.getX()));
        elemDoorPosition.setAttribute("y", String.valueOf(doorPosition.getY()));
        elemDoorPosition.setAttribute("z", String.valueOf(doorPosition.getZ()));
        elemDoor.addContent(elemDoorPosition);
      }
   
      if (elemDoor.hasChildren())
        elemCompleteRoom.addContent(elemDoor);
   
      Element elemStationaryObjects = new Element("StationaryObjects");
   
      LinkedList objectList = room.getAllStationaryObjects();
   
      for (int i = 0; (i < objectList.size()) && (objectCounter < kMaxNumObjects); i++) {
        RoomObject roomObject = (RoomObject)objectList.get(i);
   
        elemObject[objectCounter] = new Element("Object");
        elemStationaryObjects.addContent(elemObject[objectCounter]);
   
        elemType[objectCounter] = new Element("Type");
        elemType[objectCounter].addContent(roomObject.getType());
        elemObject[objectCounter].addContent(elemType[objectCounter]);
   
        SizeData size;
        if ((size = roomObject.getSize()) != null) {
          elemSize[objectCounter] = new Element("Size");
          elemSize[objectCounter].setAttribute("width", String.valueOf(size.getWidth()));
          elemSize[objectCounter].setAttribute("length", String.valueOf(size.getLength()));
          elemSize[objectCounter].setAttribute("height", String.valueOf(size.getHeight()));
          elemObject[objectCounter].addContent(elemSize[objectCounter]);
        }
   
        CartesianCoord position;
        if ((position = roomObject.getPosition()) != null) {
          elemPosition[objectCounter] = new Element("Position");
          elemPosition[objectCounter].setAttribute("x", String.valueOf(position.getX()));
          elemPosition[objectCounter].setAttribute("y", String.valueOf(position.getY()));
          elemPosition[objectCounter].setAttribute("z", String.valueOf(position.getZ()));
          elemObject[objectCounter].addContent(elemPosition[objectCounter]);
        }
   
        RotationData rotation;
        if ((rotation = roomObject.getRotation()) != null) {
          elemRotation[objectCounter] = new Element("Rotation");
          elemRotation[objectCounter].setAttribute("yaw", String.valueOf(rotation.getYaw()));
          elemRotation[objectCounter].setAttribute("roll", String.valueOf(rotation.getRoll()));
          elemRotation[objectCounter].setAttribute("pitch", String.valueOf(rotation.getPitch()));
          elemObject[objectCounter].addContent(elemRotation[objectCounter]);
        }
   
        objectCounter++;
      }
}
   **/
  
  public void clearTextBox() {
    textBox.setText("");
  }
  
  private void errorPopUp(String message) {
    JOptionPane error = new JOptionPane();
    error.showMessageDialog(null, message, "Alert", JOptionPane.INFORMATION_MESSAGE);
  }
  
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    /*
    if (source == saveRoom) {
      Document doc = XMLGenerator.generateRoomXML();
     
      if (doc != null) {
        XMLOutputter outputter = new XMLOutputter("   ", true);
     
        String output = outputter.outputString(doc);
        System.out.println(output);
      } else
        errorPopUp("No information has been set for the current room.");
    } else
     */
    if (source == generateSummary) {
      textBox.setText("");
      
      Document doc = XMLGenerator.generateRoomXML();
      
      if (doc != null) {
        XMLOutputter outputter = new XMLOutputter("   ", true);
        
        textBox.append(outputter.outputString(doc));
      } else
        errorPopUp("No information has been set for the current room.");
    }
  }
}