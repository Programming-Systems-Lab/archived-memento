package psl.memento.pervasive.roca.gui;

import psl.memento.pervasive.roca.room.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * RoomTab.java
 *
 * @author Kristina Holst
 */
public class RoomTab extends JComponent implements ActionListener {
  private static RoomTab theRoomTab;
  // Main groupings in the panel
  private JPanel mainPanel, URLPanel, northDataPanel, southDataPanel, westDataPanel, eastDataPanel;
  private JPanel upDataPanel, downDataPanel, northSouthPanel, eastWestPanel, upDownPanel, doorDataPanel, buttonPanel;
  // Sub-panels
  private JPanel northDataSubPanel1, northDataSubPanel2, northDataSubPanel3, southDataSubPanel, westDataSubPanel;
  private JPanel eastDataSubPanel, upDataSubPanel, downDataSubPanel, doorDataSubPanel1, doorDataSubPanel2;
  private JButton saveButton, clearButton;
  private JTextField[] distance;
  private JTextField direction, doorLink, roomLink;
  private JComboBox doorSideSelector, doorWallSelector;
  private static final int FIELD_LENGTH = 10, STRING_FIELD_LENGTH = 20, SETS_OF_MEAS = 6;
  private static final String[] doorSideChoices = {"left side", "right side", "middle"};
  private static final String[] doorWallChoices = {"North", "East", "South", "West"};
  
  
  protected RoomTab() {
    // Set up text fields
    doorLink = new JTextField(STRING_FIELD_LENGTH);
    roomLink = new JTextField(STRING_FIELD_LENGTH);
    
    distance = new JTextField[SETS_OF_MEAS];
    
    for (int i = 0; i < SETS_OF_MEAS; i++)
      distance[i] = new JTextField(FIELD_LENGTH);
    
    direction = new JTextField(FIELD_LENGTH);
    
    // Set up buttons
    saveButton = new JButton("Set Room Data");
    saveButton.addActionListener(this);
    clearButton = new JButton("Clear");
    clearButton.addActionListener(this);
    
    // Set up combo boxes
    doorSideSelector = new JComboBox(doorSideChoices);
    doorSideSelector.setSelectedIndex(0);
    
    doorWallSelector = new JComboBox(doorWallChoices);
    doorWallSelector.setSelectedIndex(0);
    
    // Set up panel containing room's URL info
    URLPanel = new JPanel();
    URLPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    URLPanel.setBorder(BorderFactory.createEmptyBorder(15,20,0,20));
    URLPanel.add(new JLabel("Room URL:  "));
    URLPanel.add(roomLink);
    
    // Set up panel for data collected while pointing at NORTH wall
    northDataPanel = new JPanel();
    northDataPanel.setLayout(new BorderLayout());
    northDataPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
    northDataPanel.setPreferredSize(new Dimension(300, 85));
    
    northDataSubPanel1 = new JPanel();
    northDataSubPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));
    northDataSubPanel1.add(new JLabel("Distance: "));
    northDataSubPanel1.add(distance[0]);
    northDataSubPanel1.add(new JLabel(" (meters)"));
    
    northDataSubPanel2 = new JPanel();
    northDataSubPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));
    northDataSubPanel2.add(new JLabel("Direction: "));
    northDataSubPanel2.add(direction);
    
    northDataSubPanel3 = new JPanel();
    northDataSubPanel3.setLayout(new GridLayout(0, 1));
    northDataSubPanel3.add(northDataSubPanel1);
    northDataSubPanel3.add(northDataSubPanel2);
    
    northDataPanel.add(new JLabel("Pointing the device directly at NORTH wall:"), BorderLayout.NORTH);
    northDataPanel.add(Box.createRigidArea(new Dimension(10, 1)), BorderLayout.WEST);
    northDataPanel.add(northDataSubPanel3, BorderLayout.CENTER);
    
    // Set up panel for data collected while pointing at SOUTH wall
    southDataPanel = new JPanel();
    southDataPanel.setLayout(new BorderLayout());
    southDataPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
    southDataPanel.setPreferredSize(new Dimension(300, 85));
    
    southDataSubPanel = new JPanel();
    southDataSubPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    southDataSubPanel.add(new JLabel("   Distance: "));
    southDataSubPanel.add(distance[1]);
    southDataSubPanel.add(new JLabel(" (meters)"));
    
    southDataPanel.add(new JLabel("Pointing the device directly at SOUTH wall:"), BorderLayout.NORTH);
    southDataPanel.add(Box.createRigidArea(new Dimension(10, 1)), BorderLayout.WEST);
    southDataPanel.add(southDataSubPanel, BorderLayout.CENTER);
    
    // Set up panel to hold both the NORTH and SOUTH measurements
    northSouthPanel = new JPanel();
    northSouthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    northSouthPanel.add(northDataPanel);
    northSouthPanel.add(southDataPanel);
    
    // Set up panel for data collected while pointing at EAST wall
    eastDataPanel = new JPanel();
    eastDataPanel.setLayout(new BorderLayout());
    eastDataPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 20));
    eastDataPanel.setPreferredSize(new Dimension(300, 50));
    
    eastDataSubPanel = new JPanel();
    eastDataSubPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    eastDataSubPanel.add(new JLabel("   Distance: "));
    eastDataSubPanel.add(distance[2]);
    eastDataSubPanel.add(new JLabel(" (meters)"));
    
    eastDataPanel.add(new JLabel("Pointing the device directly at EAST wall:"), BorderLayout.NORTH);
    eastDataPanel.add(Box.createRigidArea(new Dimension(10, 1)), BorderLayout.WEST);
    eastDataPanel.add(eastDataSubPanel, BorderLayout.CENTER);
    
    // Set up panel for data collected while pointing at WEST wall
    westDataPanel = new JPanel();
    westDataPanel.setLayout(new BorderLayout());
    westDataPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 20));
    westDataPanel.setPreferredSize(new Dimension(300, 50));
    
    westDataSubPanel = new JPanel();
    westDataSubPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    westDataSubPanel.add(new JLabel("   Distance: "));
    westDataSubPanel.add(distance[3]);
    westDataSubPanel.add(new JLabel(" (meters)"));
    
    westDataPanel.add(new JLabel("Pointing the device directly at WEST wall:"), BorderLayout.NORTH);
    westDataPanel.add(Box.createRigidArea(new Dimension(10, 1)), BorderLayout.WEST);
    westDataPanel.add(westDataSubPanel, BorderLayout.CENTER);
    
    // Set up panel to hold both the EAST and WEST measurements
    eastWestPanel = new JPanel();
    eastWestPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    eastWestPanel.add(eastDataPanel);
    eastWestPanel.add(westDataPanel);
    
    // Set up panel for data collected while pointing UP
    upDataPanel = new JPanel();
    upDataPanel.setLayout(new BorderLayout());
    upDataPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 20));
    upDataPanel.setPreferredSize(new Dimension(300, 50));
    
    upDataSubPanel = new JPanel();
    upDataSubPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    upDataSubPanel.add(new JLabel("   Distance: "));
    upDataSubPanel.add(distance[4]);
    upDataSubPanel.add(new JLabel(" (meters)"));
    
    upDataPanel.add(new JLabel("Pointing the device directly UP:"), BorderLayout.NORTH);
    upDataPanel.add(Box.createRigidArea(new Dimension(10, 1)), BorderLayout.WEST);
    upDataPanel.add(upDataSubPanel, BorderLayout.CENTER);
    
    // Set up panel for data collected while pointing DOWN
    downDataPanel = new JPanel();
    downDataPanel.setLayout(new BorderLayout());
    downDataPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 20));
    downDataPanel.setPreferredSize(new Dimension(300, 50));
    
    downDataSubPanel = new JPanel();
    downDataSubPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    downDataSubPanel.add(new JLabel("   Distance: "));
    downDataSubPanel.add(distance[5]);
    downDataSubPanel.add(new JLabel(" (meters)"));
    
    downDataPanel.add(new JLabel("Pointing the device directly DOWN:"), BorderLayout.NORTH);
    downDataPanel.add(Box.createRigidArea(new Dimension(10, 1)), BorderLayout.WEST);
    downDataPanel.add(downDataSubPanel, BorderLayout.CENTER);
    
    // Set up panel to hold both the UP and DOWN measurements
    upDownPanel = new JPanel();
    upDownPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    upDownPanel.add(upDataPanel);
    upDownPanel.add(downDataPanel);
    
    doorDataSubPanel1 = new JPanel();
    doorDataSubPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));
    doorDataSubPanel1.add(new JLabel("Door is on the "));
    doorDataSubPanel1.add(doorSideSelector);
    doorDataSubPanel1.add(new JLabel(" of the "));
    doorDataSubPanel1.add(doorWallSelector);
    doorDataSubPanel1.add(new JLabel(" wall."));
    
    doorDataSubPanel2 = new JPanel();
    doorDataSubPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));
    doorDataSubPanel2.add(new JLabel("Door URL:  "));
    doorDataSubPanel2.add(doorLink);
    
    doorDataPanel = new JPanel();
    doorDataPanel.setLayout(new GridLayout(0, 1));
    doorDataPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
    doorDataPanel.add(doorDataSubPanel1);
    doorDataPanel.add(doorDataSubPanel2);
    
    // Set up buttons
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    buttonPanel.add(Box.createRigidArea(new Dimension(310, 0)));
    buttonPanel.add(saveButton);
    buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPanel.add(clearButton);
    
    /* To get everything to stay aligned when window is maximized, we need to
     * add all the components to an intermediate panel. */
    JPanel dummyPanel = new JPanel();
    dummyPanel.setLayout(new BoxLayout(dummyPanel, BoxLayout.Y_AXIS));
    dummyPanel.add(URLPanel);
    dummyPanel.add(northSouthPanel);
    dummyPanel.add(eastWestPanel);
    dummyPanel.add(upDownPanel);
    dummyPanel.add(doorDataPanel);
    dummyPanel.add(buttonPanel);
    
    mainPanel = new JPanel();
    mainPanel.add(dummyPanel);
  }
  
  public static RoomTab getInstance() {
    if (theRoomTab == null) {
      theRoomTab = new RoomTab();
    }
    
    return theRoomTab;
  }
  
  public Component getMainPanel() {
    return mainPanel;
  }
  
  private void errorPopUp(String message) {
    JOptionPane error = new JOptionPane();
    error.showMessageDialog(null, message, "Alert", JOptionPane.INFORMATION_MESSAGE);
  }
  
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    
    // Save data to Room
    if (source == saveButton) {
      Room room = Room.getInstance();
      
      /* If user changes the room dimensions after some objects have already been added
       * to the room, the objects' positions within the room will be wrong. To avoid this,
       * clear all objects when user tries to set new room dimensions (but prompt for
       * confirmation first).
       */
      if ((room.getAllStationaryObjects().size() != 0) || (room.getAllPervasiveObjects().size() != 0) ||
      (room.getAllPhysicalObjects().size() != 0) || (room.getAllActiveObjects().size() != 0)) {
        JOptionPane confirmDialog = new JOptionPane();
        
        int response = confirmDialog.showConfirmDialog(null, "Changing the room dimensions will reset all data, " +
        "including any objects you have added to the room.  Continue?", "Confirm Change of Room Dimensions",
        JOptionPane.YES_NO_OPTION);
        
        if (response == JOptionPane.YES_OPTION) {
          room.clearRoom();
          
          ObjectTab objTab = ObjectTab.getInstance();
          objTab.clearFields();
          objTab.clearCurrentObjectList();
          
          room = Room.getInstance();
          
        } else
          return;
      }
      
      room.setRoomURL(roomLink.getText());
      
      try {
        room.setNorth(Double.parseDouble(direction.getText()));
      } catch (NumberFormatException ex) {
        errorPopUp("The direction field must contain a valid number.");
        return;
      }
      
      double[] roomMeasurements = new double[SETS_OF_MEAS];
      
      try {
        for (int i = 0; i < roomMeasurements.length; i++) {
          roomMeasurements[i] = Double.parseDouble(distance[i].getText());
        }
      } catch (NumberFormatException ex) {
        errorPopUp("All distance fields must contain valid numbers.");
        return;
      }
      
      room.setRoomDimensions(roomMeasurements);
      room.setDoor(doorLink.getText(), (String)doorWallSelector.getSelectedItem(),
      (String)doorSideSelector.getSelectedItem());
    }
    
    // Clear all text fields
    if (source == clearButton) {
      clear();
    }
  }
  
  public void clear() {
    roomLink.setText("");
    
    for (int i = 0; i < SETS_OF_MEAS; i++) {
      distance[i].setText("");
    }
    
    direction.setText("");
    doorLink.setText("");
    
    doorSideSelector.setSelectedIndex(0);
    doorWallSelector.setSelectedIndex(0);
  }
}