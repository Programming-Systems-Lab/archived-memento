package psl.memento.pervasive.roca.gui;

import psl.memento.pervasive.roca.room.*;
import psl.memento.pervasive.roca.data.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class ObjectTab {
  
  private JPanel mainPanel;
  private JTabbedPane positionTabbedPane;
  private JScrollPane scrollPane;
  private JLabel objectPreviewLabel, widthLabel, lengthLabel, heightLabel, LRSliderLabel, UDSliderLabel;
  private JComboBox objectSelector, directionSelector, userPositionSelector, userHeightSelector;
  private JComboBox cornerSelector, wallSelector, objectHeightSelector, objectClassSelector;
  private JRadioButton cornerRadioButton, wallRadioButton, middleRadioButton;
  private JTextField distanceField, directionField, inclineField;
  private JButton createButton, getDataButton, addButton, clearButton, editButton, removeButton, clearListButton;
  private DefaultListModel listModel;
  private JList objectList;
  private JSlider scaleSlider, LRSlider, UDSlider;
  private double widthValue, lengthValue, heightValue;
  private static final int FIELD_LENGTH = 8;
  private static String[] objectStrings;
  private static String[] objectClassStrings = {"Active", "Pervasive", "Physical", "Stationary", "All"};
  private static String[] userPositionStrings = {"middle", "NW corner", "NE corner", "SW corner",
  "SE corner"};
  private static String[] userHeightStrings = {"0", "1", "2", "3", "4", "5", "6", "7"};
  private static String[] cornerStrings = {"NW", "NE", "SW", "SE"};
  private static String[] wallStrings = {"North", "East", "South", "West"};
  private static String[] objectHeightStrings = {"Floor", "1/4 height of room", "1/2 height of room",
  "3/4 height of room", "Ceiling"};
  
  /** Builds the tab */
  public ObjectTab() {
    // Set up combo box to determine which list of objects to show
    objectClassSelector = new JComboBox(objectClassStrings);
    objectClassSelector.setSelectedIndex(1);
    objectClassSelector.addActionListener(new ComboBoxListener());
    
    // Set up combo box for choosing object
    objectStrings = updateObjectSelectorList((String)objectClassSelector.getSelectedItem());
    objectSelector = new JComboBox(objectStrings);
    objectSelector.setSelectedIndex(0);
    objectSelector.addActionListener(new ComboBoxListener());
    
    String currentObject = (String)objectSelector.getItemAt(0);
    
    // Will have to get the following values from database
    widthValue = 7.5;
    lengthValue = 25.85;
    heightValue = 12.34;
    
    // Set up initial labels
    objectPreviewLabel = new JLabel(new javax.swing.ImageIcon("images/" + currentObject + ".gif"));
    widthLabel = new JLabel(String.valueOf(widthValue));
    lengthLabel = new JLabel(String.valueOf(lengthValue));
    heightLabel = new JLabel(String.valueOf(heightValue));
    
    // Set up text fields
    distanceField = new JTextField(FIELD_LENGTH);
    directionField = new JTextField(FIELD_LENGTH);
    inclineField = new JTextField(FIELD_LENGTH);
    
    // Set up buttons
    createButton = new JButton("Create New Object");
    createButton.addActionListener(new ButtonListener());
    getDataButton = new JButton("Get Data");
    getDataButton.addActionListener(new ButtonListener());
    addButton = new JButton("Add to List");
    addButton.addActionListener(new ButtonListener());
    clearButton = new JButton("Clear");
    clearButton.addActionListener(new ButtonListener());
    editButton = new JButton("Edit");
    editButton.addActionListener(new ButtonListener());
    removeButton = new JButton("Remove");
    removeButton.addActionListener(new ButtonListener());
    clearListButton = new JButton("Clear List");
    clearListButton.addActionListener(new ButtonListener());
    
    JPanel objectSelectorSubPanel1 = new JPanel();
    objectSelectorSubPanel1.add(new JLabel("   List: "));
    objectSelectorSubPanel1.add(objectClassSelector);
    
    JPanel objectSelectorSubPanel2 = new JPanel();
    objectSelectorSubPanel2.add(new JLabel("Object: "));
    objectSelectorSubPanel2.add(objectSelector);
    
    JPanel objectSelectorSubPanel3 = new JPanel();
    objectSelectorSubPanel3.add(createButton);
    
    // Set up object selector panel with combo box and label
    JPanel objectSelectorPanel = new JPanel();
    objectSelectorPanel.setLayout(new BoxLayout(objectSelectorPanel, BoxLayout.Y_AXIS));
    objectSelectorPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,30));
    objectSelectorPanel.add(objectSelectorSubPanel1);
    objectSelectorPanel.add(objectSelectorSubPanel2);
    objectSelectorPanel.add(Box.createRigidArea(new Dimension(1,5)));
    objectSelectorPanel.add(objectSelectorSubPanel3);
    
    // Labels
    JPanel objectScalingSubPanel1 = new JPanel();
    objectScalingSubPanel1.setLayout(new GridLayout(0,1));
    objectScalingSubPanel1.add(new JLabel("width:   "));
    objectScalingSubPanel1.add(new JLabel("length:  "));
    objectScalingSubPanel1.add(new JLabel("height:  "));
    
    // Object sizes
    JPanel objectScalingSubPanel2 = new JPanel();
    objectScalingSubPanel2.setLayout(new GridLayout(0,1));
    objectScalingSubPanel2.add(widthLabel);
    objectScalingSubPanel2.add(lengthLabel);
    objectScalingSubPanel2.add(heightLabel);
    
    // Panel with labels and object sizes
    JPanel objectScalingSubPanel3 = new JPanel();
    objectScalingSubPanel3.setLayout(new BorderLayout());
    objectScalingSubPanel3.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    objectScalingSubPanel3.add(objectScalingSubPanel1, BorderLayout.WEST);
    objectScalingSubPanel3.add(objectScalingSubPanel2, BorderLayout.EAST);
    
    // Slider label
    JLabel scalingSliderLabel = new JLabel("Scale (% of default size)", JLabel.CENTER);
    scalingSliderLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
    
    // Create slider
    scaleSlider = new JSlider(JSlider.HORIZONTAL, 20, 300, 100);
    scaleSlider.addChangeListener(new psl.memento.pervasive.roca.gui.ObjectTab.SliderListener());
    scaleSlider.setMajorTickSpacing(40);
    scaleSlider.setMinorTickSpacing(10);
    scaleSlider.setPaintTicks(true);
    scaleSlider.setPaintLabels(true);
    
    // Add slider and label to its own panel
    JPanel objectScalingSubPanel4 = new JPanel();
    objectScalingSubPanel4.setLayout(new BoxLayout(objectScalingSubPanel4, BoxLayout.Y_AXIS));
    objectScalingSubPanel4.add(scalingSliderLabel);
    objectScalingSubPanel4.add(scaleSlider);
    
    // Combine size information and slider
    JPanel objectScalingPanel = new JPanel();
    objectScalingPanel.setLayout(new BorderLayout());
    objectScalingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));
    objectScalingPanel.add(new JLabel("Approximate object size (inches):"), BorderLayout.NORTH);
    objectScalingPanel.add(objectScalingSubPanel3, BorderLayout.WEST);
    objectScalingPanel.add(objectScalingSubPanel4, BorderLayout.SOUTH);
    
    // Group together object selector combo box and preview panel
    JPanel topObjectSubPanel = new JPanel();
    topObjectSubPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    topObjectSubPanel.add(objectSelectorPanel);
    topObjectSubPanel.add(objectPreviewLabel);
    
    // Put the selector, preview panel, size, and slider information together
    JPanel topObjectPanel = new JPanel();
    topObjectPanel.setLayout(new FlowLayout());
    topObjectPanel.add(topObjectSubPanel);
    topObjectPanel.add(Box.createRigidArea(new Dimension(40, 0)));
    topObjectPanel.add(objectScalingPanel);
    
    /*
     * All the following code makes the tabbed panel for the user to select
     * either the "simple" or "precise" way of determining an object's position.
     */
    
    cornerRadioButton = new JRadioButton("In a corner", true);
    wallRadioButton = new JRadioButton("Middle of a wall", false);
    middleRadioButton = new JRadioButton("Middle of the room", false);
    
    javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();
    group.add(cornerRadioButton);
    group.add(wallRadioButton);
    group.add(middleRadioButton);
    
    cornerSelector = new JComboBox(cornerStrings);
    cornerSelector.setSelectedIndex(0);
    cornerSelector.addActionListener(new ComboBoxListener());
    
    wallSelector = new JComboBox(wallStrings);
    wallSelector.setSelectedIndex(0);
    wallSelector.addActionListener(new ComboBoxListener());
    
    objectHeightSelector = new JComboBox(objectHeightStrings);
    objectHeightSelector.setSelectedIndex(0);
    
    JPanel simplePosSubPanel1 = new JPanel();
    simplePosSubPanel1.setLayout(new BorderLayout());
    simplePosSubPanel1.add(cornerRadioButton, BorderLayout.NORTH);
    simplePosSubPanel1.add(Box.createRigidArea(new Dimension(10, 1)), BorderLayout.WEST);
    simplePosSubPanel1.add(cornerSelector, BorderLayout.CENTER);
    simplePosSubPanel1.add(Box.createRigidArea(new Dimension(30, 1)), BorderLayout.EAST);
    
    JPanel simplePosSubPanel2 = new JPanel();
    simplePosSubPanel2.setLayout(new BorderLayout());
    simplePosSubPanel2.add(wallRadioButton, BorderLayout.NORTH);
    simplePosSubPanel2.add(Box.createRigidArea(new Dimension(10, 1)), BorderLayout.WEST);
    simplePosSubPanel2.add(wallSelector, BorderLayout.CENTER);
    simplePosSubPanel2.add(Box.createRigidArea(new Dimension(30, 1)), BorderLayout.EAST);
    
    JPanel simplePosSubPanel3 = new JPanel();
    simplePosSubPanel3.setLayout(new BorderLayout());
    simplePosSubPanel3.add(middleRadioButton, BorderLayout.NORTH);
    simplePosSubPanel3.add(Box.createRigidArea(new Dimension(10, 22)), BorderLayout.WEST);
    simplePosSubPanel3.add(Box.createRigidArea(new Dimension(160, 1)), BorderLayout.EAST);
    
    JPanel simplePosSubPanel4 = new JPanel();
    simplePosSubPanel4.setLayout(new BorderLayout());
    simplePosSubPanel4.add(new JLabel("At what height?"), BorderLayout.NORTH);
    simplePosSubPanel4.add(objectHeightSelector, BorderLayout.CENTER);
    
    JPanel simplePosPanel = new JPanel();
    simplePosPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    simplePosPanel.add(simplePosSubPanel1);
    simplePosPanel.add(simplePosSubPanel2);
    simplePosPanel.add(simplePosSubPanel3);
    simplePosPanel.add(simplePosSubPanel4);
    
    userPositionSelector = new JComboBox(userPositionStrings);
    userPositionSelector.setSelectedIndex(0);
    
    userHeightSelector = new JComboBox(userHeightStrings);
    userHeightSelector.setSelectedIndex(4);
    
    JPanel precisePosSubPanel1 = new JPanel();
    precisePosSubPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));
    precisePosSubPanel1.add(new JLabel("Your position within room: "));
    precisePosSubPanel1.add(userPositionSelector);
    precisePosSubPanel1.add(new JLabel("   Holding the device approx. "));
    precisePosSubPanel1.add(userHeightSelector);
    precisePosSubPanel1.add(new JLabel(" ft off the ground"));
    
    JPanel precisePosSubPanel2 = new JPanel();
    precisePosSubPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));
    precisePosSubPanel2.add(new JLabel("Distance: "));
    precisePosSubPanel2.add(distanceField);
    precisePosSubPanel2.add(new JLabel("  Direction: "));
    precisePosSubPanel2.add(directionField);
    precisePosSubPanel2.add(new JLabel("  Incline: "));
    precisePosSubPanel2.add(inclineField);
    precisePosSubPanel2.add(Box.createRigidArea(new Dimension(3, 1)));
    precisePosSubPanel2.add(getDataButton);
    
    JPanel precisePosPanel = new JPanel();
    precisePosPanel.setLayout(new GridLayout(0, 1));
    precisePosPanel.add(precisePosSubPanel1);
    precisePosPanel.add(precisePosSubPanel2);
    
    positionTabbedPane = new javax.swing.JTabbedPane();
    positionTabbedPane.setTabPlacement(javax.swing.JTabbedPane.TOP);
    positionTabbedPane.setPreferredSize(new Dimension(500, 105));
    positionTabbedPane.addTab("Simple Position Finder", simplePosPanel);
    positionTabbedPane.addTab("Precise Position Finder", precisePosPanel);
    
    String[] directionStrings = {"North", "Northeast", "East", "Southeast", "South", "Southwest", "West", "Northwest"};
    
    // Set up combo box for choosing the direction the object is facing
    directionSelector = new JComboBox(directionStrings);
    directionSelector.setSelectedIndex(0);
    
    JPanel rotationSubPanel1 = new JPanel();
    rotationSubPanel1.setLayout(new GridLayout(0,1));
    rotationSubPanel1.add(new JLabel("Facing:"));
    rotationSubPanel1.add(directionSelector);
    
    // Slider label
    LRSliderLabel = new JLabel("Left/Right Tilt: 0 degrees", JLabel.CENTER);
    LRSliderLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
    
    // Create slider
    LRSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
    LRSlider.addChangeListener(new SliderListener());
    LRSlider.setMajorTickSpacing(60);
    LRSlider.setMinorTickSpacing(15);
    LRSlider.setPaintTicks(true);
    LRSlider.setPaintLabels(true);
    
    // Add slider and label to its own panel
    JPanel rotationSubPanel2 = new JPanel();
    rotationSubPanel2.setLayout(new BoxLayout(rotationSubPanel2, BoxLayout.Y_AXIS));
    rotationSubPanel2.add(LRSliderLabel);
    rotationSubPanel2.add(LRSlider);
    
    // Slider label
    UDSliderLabel = new JLabel("Up/Down Tilt: 0 degrees", JLabel.CENTER);
    UDSliderLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
    
    // Create slider
    UDSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
    UDSlider.addChangeListener(new SliderListener());
    UDSlider.setMajorTickSpacing(60);
    UDSlider.setMinorTickSpacing(15);
    UDSlider.setPaintTicks(true);
    UDSlider.setPaintLabels(true);
    
    // Add slider and label to its own panel
    JPanel rotationSubPanel3 = new JPanel();
    rotationSubPanel3.setLayout(new BoxLayout(rotationSubPanel3, BoxLayout.Y_AXIS));
    rotationSubPanel3.add(UDSliderLabel);
    rotationSubPanel3.add(UDSlider);
    
    JPanel rotationPanel = new JPanel();
    rotationPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    rotationPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,0));
    rotationPanel.add(rotationSubPanel1);
    rotationPanel.add(Box.createRigidArea(new Dimension(15,0)));
    rotationPanel.add(rotationSubPanel2);
    rotationPanel.add(Box.createRigidArea(new Dimension(15,0)));
    rotationPanel.add(rotationSubPanel3);
    
    // Set up main button panel
    JPanel mainButtonPanel = new JPanel();
    mainButtonPanel.setLayout(new BoxLayout(mainButtonPanel, BoxLayout.X_AXIS));
    mainButtonPanel.setBorder(BorderFactory.createEmptyBorder(0,15,10,10));
    mainButtonPanel.add(Box.createRigidArea(new Dimension(395,0)));
    mainButtonPanel.add(addButton);
    mainButtonPanel.add(Box.createRigidArea(new Dimension(10,0)));
    mainButtonPanel.add(clearButton);
    
    // Set up list model
    listModel = new javax.swing.DefaultListModel();
    
    // Create the list and put it in a scroll pane
    objectList = new javax.swing.JList(listModel);
    objectList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    objectList.addListSelectionListener(new ListListener());
    scrollPane = new javax.swing.JScrollPane(objectList);
    scrollPane.setPreferredSize(new Dimension(250,100));
    
    // List button panel
    JPanel objectListButtonPanel = new JPanel();
    objectListButtonPanel.setLayout(new GridLayout(0,1));
    objectListButtonPanel.add(editButton);
    objectListButtonPanel.add(removeButton);
    objectListButtonPanel.add(clearListButton);
    
    JPanel objectListPanel = new JPanel();
    objectListPanel.setLayout(new FlowLayout());
    objectListPanel.add(scrollPane);
    objectListPanel.add(objectListButtonPanel);
    
    JPanel middlePanel = new JPanel();
    middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
    middlePanel.setBorder(BorderFactory.createEmptyBorder(20,30,0,0));
    middlePanel.add(positionTabbedPane);
    middlePanel.add(rotationPanel);
    middlePanel.add(mainButtonPanel);
    
    /* To get everything to stay aligned when window is maximized, we need to
     * add all the components to an intermediate panel. */
    JPanel dummyPanel = new JPanel();
    dummyPanel.setLayout(new BoxLayout(dummyPanel, BoxLayout.Y_AXIS));
    dummyPanel.add(topObjectPanel);
    dummyPanel.add(middlePanel);
    dummyPanel.add(Box.createRigidArea(new Dimension(0,10)));
    dummyPanel.add(objectListPanel);
    
    // Set up main panel
    mainPanel = new JPanel();
    mainPanel.add(dummyPanel);
  }
  
  
  /** Provides a handle on the tab's main panel
   *  @return the main panel
   */
  public Component getMainPanel() {
    return mainPanel;
  }
  
  
  /** Opens pop-up window to display error message
   *  @param message the error message to be displayed
   */
  private void errorPopUp(String message) {
    JOptionPane error = new JOptionPane();
    error.showMessageDialog(null, message, "Alert", JOptionPane.INFORMATION_MESSAGE);
  }
  
  
  /** Opens a new window to let the user add a new 3D object to the database
   *
   *  MODIFY TO ACCOUNT FOR NEW SINGLE OBJECT PANEL DESIGN
   */
  private void createObject() {
    JFrame addFrame = new JFrame("Create New Object");
    
    NewObjectWindow window = new NewObjectWindow("Active", objectSelector);
    
    addFrame.getContentPane().add(window.getMainPanel(), BorderLayout.CENTER);
    addFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    addFrame.pack();
    addFrame.setVisible(true);
  }
  
  
  /** Adds an object to the list of objects in the room */
  private void addObject() {
    /* Set the object's type */
    String type = (String)objectSelector.getSelectedItem();
    
    /* Set the object's size information */
    SizeData size = null;
    
    try {
      size = new SizeData(Double.parseDouble(widthLabel.getText())* (1/39.37),
      Double.parseDouble(lengthLabel.getText()) * (1/39.37), Double.parseDouble(heightLabel.getText()) * (1/39.37));
    } catch (NumberFormatException ex) {
      errorPopUp("Invalid size values.");
      return;
    }
    
    /* Set the object's position information */
    CartesianCoord position = null;
    
    /* Option 1: precise positioning selected */
    if (positionTabbedPane.getSelectedIndex() == 1) {
      if (distanceField.getText().equals("") || directionField.getText().equals("") ||
      inclineField.getText().equals("")) {
        errorPopUp("All fields must be filled before adding an object to the list.");
      } else {
        setPrecisePosition(position);
      }
      /* Option 2: simple positioning selected */
    } else {
      setSimplePosition(position);
    }
    
    /* Set the object's rotation information */
    RotationData rotation = null;
    setRotation(rotation);
    
    /* If there were no problems above, create the new RoomObject and update the list */
    if ((type != null) && (size != null) && (position != null) && (rotation != null)) {
      RoomObject roomObject = new RoomObject(type, size, position, rotation);
      
      Room room = Room.getInstance();
      room.addActiveObject(roomObject);
      
      listModel.addElement(roomObject);
      
      objectList.setSelectedIndex(listModel.indexOf(roomObject));
    }
  }
  
  
  /** Calculates the position of an object being added to the room, only called if
   *  user chooses precise positioning method
   *  @param iPosition the CartesianCoord that will store the requested positioning information
   */
  private void setPrecisePosition(CartesianCoord iPosition) {
    double dist = 0.0, dir = 0.0, inc = 0.0;
    
    /* Get the measurement data entered in the text fields */
    try {
      dist = Double.parseDouble(distanceField.getText());
      dir = Double.parseDouble(directionField.getText());
      inc = Double.parseDouble(inclineField.getText());
    } catch (NumberFormatException ex) {
      errorPopUp("All fields must contain valid numbers.");
      return;
    }
    
    /* Determine the point from which these measurements are being taken */
    CartesianCoord userPosition = determineUsersPositionInRoom((String)userPositionSelector.getSelectedItem(),
    (String)userHeightSelector.getSelectedItem());
    
    /* Adjust the directional coordinate since user has defined new directional frame */
    SphericalCoord spherPosition = new SphericalCoord(dist, dir, inc);
    spherPosition.synchronizeDirectionalOrientation();
    
    /* Translate object's position from spherical to Cartesian coordinate system */
    iPosition = new CartesianCoord();
    iPosition.createFromSphericalCoord(spherPosition);
    
    /* Finally, compensate for fact that the measurements were taken from a certain point in the room */
    iPosition.setX(iPosition.getX() + userPosition.getX());
    iPosition.setY(iPosition.getY() + userPosition.getY());
    iPosition.setZ(iPosition.getZ() + userPosition.getZ());
  }
  
  
  /** Calculates the position of an object being added to the room, only called if
   *  user chooses simple positioning method
   *  @param iPosition the CartesianCoord that will store the requested positioning information
   */
  private void setSimplePosition(CartesianCoord iPosition) {
    Room room = Room.getInstance();
    
    iPosition = new CartesianCoord();
    
    double roomWidth = room.getSpanNSWalls(); // "width" defined as span of north wall
    double roomDepth = room.getSpanWEWalls(); // "depth" defined as span of west wall
    
    /* Get object dimensions (1/39.37 used to convert from inches to meters) */
    double objWidth = Double.parseDouble(widthLabel.getText()) * (1/39.37);
    double objDepth = Double.parseDouble(lengthLabel.getText()) * (1/39.37);
    
    /* Object goes in a corner */
    if (cornerRadioButton.isSelected()) {
      String corner = (String)cornerSelector.getSelectedItem();
      
      if (corner.equalsIgnoreCase("nw")) {
        iPosition.setX(.5 * objWidth);
        iPosition.setY(roomDepth - (.5 * objDepth));
      } else if (corner.equalsIgnoreCase("ne")) {
        iPosition.setX(roomWidth - (.5 * objWidth));
        iPosition.setY(roomDepth - (.5 * objDepth));
      } else if (corner.equalsIgnoreCase("sw")) {
        iPosition.setX(.5 * objWidth);
        iPosition.setY(.5 * objDepth);
      } else if (corner.equalsIgnoreCase("se")) {
        iPosition.setX(roomWidth - (.5 * objWidth));
        iPosition.setY(.5 * objDepth);
      }
      
      /* Object goes against a wall, not in a corner */
    } else if (wallRadioButton.isSelected()) {
      String wall = (String)wallSelector.getSelectedItem();
      
      if (wall.equalsIgnoreCase("north")) {
        iPosition.setX(.5 * roomWidth);
        iPosition.setY(roomDepth - (.5 * objDepth));
      } else if (wall.equalsIgnoreCase("south")) {
        iPosition.setX(.5 * roomWidth);
        iPosition.setY(.5 * objDepth);
      } else if (wall.equalsIgnoreCase("east")) {
        iPosition.setX(roomWidth - (.5 * objWidth));
        iPosition.setY(.5 * roomDepth);
      } else if (wall.equalsIgnoreCase("west")) {
        iPosition.setX(.5 * objWidth);
        iPosition.setY(.5 * roomDepth);
      }
      
      /* Object goes in middle of the room */
    } else {
      iPosition.setX(.5 * roomWidth);
      iPosition.setY(.5 * roomDepth);
    }
    
    /* Determine how high off the floor to place the object */
    String chosenHeight = (String)objectHeightSelector.getSelectedItem();
    double roomHeight = room.getHeight() * (1/39.37);
    double objHeight = Double.parseDouble(heightLabel.getText());
    
    if (chosenHeight.equalsIgnoreCase("floor"))
      iPosition.setZ(.5 * objHeight);
    else if (chosenHeight.equalsIgnoreCase("1/4 height of room"))
      iPosition.setZ(.25 * roomHeight);
    else if (chosenHeight.equalsIgnoreCase("1/2 height of room"))
      iPosition.setZ(.5 * roomHeight);
    else if (chosenHeight.equalsIgnoreCase("3/4 height of room"))
      iPosition.setZ(.75 * roomHeight);
    else if (chosenHeight.equalsIgnoreCase("ceiling"))
      iPosition.setZ(roomHeight - (.5 * objHeight));
  }
  
  
  /** For precise positioning method, we need to know from what point in the
   *  room the user is taking his/her measurements
   *  @param iPosition the user's specified location in the room (i.e. NW corner)
   *  @param iHeight the height at which the user claims to be holding the device
   *  @return a CartesianCoord containing the user's current position in the room
   */
  private CartesianCoord determineUsersPositionInRoom(String iPosition, String iHeight) {
    CartesianCoord userPosition = new CartesianCoord();
    
    Room room = Room.getInstance();
    
    double roomWidth = room.getSpanNSWalls();  // "width" defined as span of north wall
    double roomDepth = room.getSpanWEWalls();  // "depth" defined as span of west wall
    
    /* Determine the height at which the measuring device is held */
    try {
      /* .3048 is conversion rate from feet to meters */
      userPosition.setZ(Double.parseDouble(iHeight) * .3048);
      /* Error should never occur, since value of iHeight comes from a ComboBox containing only int's */
    } catch(NumberFormatException ex) {
      System.out.println("Error parsing device height");
    }
    
    /* Determine (x,y)-position of user within room */
    if (iPosition.equalsIgnoreCase("middle")) {
      userPosition.setX(roomWidth / 2);
      userPosition.setY(roomDepth / 2);
    } else if (iPosition.equalsIgnoreCase("nw corner")) {
      userPosition.setX(0);
      userPosition.setY(roomDepth);
    } else if (iPosition.equalsIgnoreCase("ne corner")) {
      userPosition.setX(roomWidth);
      userPosition.setY(roomDepth);
    } else if (iPosition.equalsIgnoreCase("sw corner")) {
      userPosition.setX(0);
      userPosition.setY(0);
    } else if (iPosition.equalsIgnoreCase("se corner")) {
      userPosition.setX(roomWidth);
      userPosition.setY(0);
    }
    
    return userPosition;
  }
  
  
  /** Calculates the rotation of an object being added to the room
   *  @param iRotation the RotationData that will store the requested rotation information
   */
  private void setRotation(RotationData iRotation) {
    String dirFacing = (String)directionSelector.getSelectedItem();
    int orientation = 0;
    
    if (dirFacing.equalsIgnoreCase("North"))
      orientation = RotationData.N;
    else if (dirFacing.equalsIgnoreCase("Northeast"))
      orientation = RotationData.NE;
    else if (dirFacing.equalsIgnoreCase("East"))
      orientation = RotationData.E;
    else if (dirFacing.equalsIgnoreCase("Southeast"))
      orientation = RotationData.SE;
    else if (dirFacing.equalsIgnoreCase("South"))
      orientation = RotationData.S;
    else if (dirFacing.equalsIgnoreCase("Southwest"))
      orientation = RotationData.SW;
    else if (dirFacing.equalsIgnoreCase("West"))
      orientation = RotationData.W;
    else if (dirFacing.equalsIgnoreCase("Northwest"))
      orientation = RotationData.NW;
    
    iRotation = new RotationData(orientation, LRSlider.getValue(), UDSlider.getValue());
  }
  
  
  /** Sets all fields, sliders, etc. to the values set for a particular object previously
   *  added to the room, so that it may be edited
   *
   *  FINISH IMPLEMENTING &
   *  MODIFY TO ACCOUNT FOR NEW SINGLE OBJECT PANEL DESIGN
   */
  private void editObject() {
    Room room = Room.getInstance();
    RoomObject roomObject = room.getActiveObject((RoomObject)objectList.getSelectedValue());
    
    if (roomObject != null) {
      /* Set list selector - IMPLEMENT */
      
      /* Set object selector */
      objectSelector.setSelectedItem(roomObject.getType());
      
      /* Set size labels - CHANGE, SINCE SUHIT DOESN'T WANT THIS STORED IN ROOMOBJECT */
      SizeData size = roomObject.getSize();
      
      widthLabel.setText(String.valueOf(size.getWidth()));
      lengthLabel.setText(String.valueOf(size.getLength()));
      heightLabel.setText(String.valueOf(size.getHeight()));
      
      widthLabel.repaint();
      lengthLabel.repaint();
      heightLabel.repaint();
      
      
      /* Set position info - IMPLEMENT */
      
      
      /* Set rotation selector and sliders */
      RotationData rotation = roomObject.getRotation();
      
      if (rotation.getYaw() == 0)
        directionSelector.setSelectedItem("N");
      else if (rotation.getYaw() == 45)
        directionSelector.setSelectedItem("NE");
      else if (rotation.getYaw() == 90)
        directionSelector.setSelectedItem("E");
      else if (rotation.getYaw() == 135)
        directionSelector.setSelectedItem("SE");
      else if (rotation.getYaw() == 180)
        directionSelector.setSelectedItem("S");
      else if (rotation.getYaw() == 225)
        directionSelector.setSelectedItem("SW");
      else if (rotation.getYaw() == 270)
        directionSelector.setSelectedItem("W");
      else if (rotation.getYaw() == 315)
        directionSelector.setSelectedItem("NW");
      
      LRSlider.setValue(rotation.getRoll());
      UDSlider.setValue(rotation.getPitch());
    }
  }
  
  
  /** Remove item from list of objects in the room */
  private void removeObject() {
    int index = objectList.getSelectedIndex();
    
    /* Remove the object from the list */
    if (index >= 0) {
      listModel.remove(index);
    }
    
    int size = listModel.getSize();
      
    if (size == 0)
      removeButton.setEnabled(false);
    else {
      /* Adjust the selection, if necessary */
      if(index == size)
        index--;
      objectList.setSelectedIndex(index);
    }
    
    /* Remove the object from the room */
    Room room = Room.getInstance();
    room.removeActiveObject((RoomObject)objectList.getSelectedValue());
  }
  
  
  /** Reset all fields, combo boxes, etc */
  public void clearFields() {
    distanceField.setText("");
    directionField.setText("");
    inclineField.setText("");
    
    objectSelector.setSelectedIndex(0);
    scaleSlider.setValue(100);
    directionSelector.setSelectedIndex(0);
    LRSlider.setValue(0);
    UDSlider.setValue(0);
  }
  
  
  /** Clear list of objects current in room */
  public void clearCurrentObjectList() {
    Room room = Room.getInstance();
    room.removeAllActiveObjects();
    
    listModel.clear();
  }
  
  
  /** Change the list of available objects to match the chosen category of objects */
  private String[] updateObjectSelectorList(String iCategory) {
    String[] objects = new String[10];
    
    return objects;
  }
  
  
  /** Update the preview window to display a thumbnail of the currently selected object */
  private void updatePreviewWindow() {
    String objectName = (String)objectSelector.getSelectedItem();
    objectPreviewLabel.setIcon(new ImageIcon("images/" + objectName + ".gif"));
    
    // Get default width/length/height values from database and update size labels
  }
  
  /** Listener for all buttons in the ObjectTab */
  class ButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      
      if (source == createButton) {
        createObject();
      } else if (source == getDataButton) {
        
      /* NOT YET IMPLEMENTED
       * Retrieve data from hardware components, fill in text fields with retrieved values
       */
        
      } else if (source == addButton) {
        Room room = Room.getInstance();
        
        if (room.getHeight() > 0) {
          addObject();
        } else {
          errorPopUp("You must set the room dimensions before adding any objects.");
        }
      } else if(source == clearButton) {
        clearFields();
      } else if (source == editButton) {
        editObject();
      } else if (source == removeButton) {
        removeObject();
      } else if (source == clearListButton) {
        clearCurrentObjectList();
      }
    }
  }
  
  
  /** Listener for all combo boxes in the ObjectTab */
  class ComboBoxListener implements ActionListener {
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
      Object source = e.getSource();
      
      if (source == objectSelector) {
        updatePreviewWindow();
      } else if (source == cornerSelector) {
        cornerRadioButton.setSelected(true);
      } else if (source == wallSelector) {
        wallRadioButton.setSelected(true);
      } else if (source == objectClassSelector) {
        updateObjectSelectorList((String)objectClassSelector.getSelectedItem());
      }
    }
    
  }
  
  
  /** Listener for all sliders in the ObjectTab */
  class SliderListener implements ChangeListener {
    
    public void stateChanged(ChangeEvent e) {
      JSlider source = (JSlider)e.getSource();
      
      if (source == scaleSlider) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("######0.####");
        
        double scale = (double)source.getValue() / 100;
        
        widthLabel.setText(df.format(widthValue * scale));
        lengthLabel.setText(df.format(lengthValue * scale));
        heightLabel.setText(df.format(heightValue * scale));
        
        widthLabel.repaint();
        lengthLabel.repaint();
        heightLabel.repaint();
      }
      
      if (source == LRSlider) {
        int degrees = (int)source.getValue();
        
        if (degrees < 0) {
          LRSliderLabel.setText("Left/Right Tilt: " + (int)Math.abs(source.getValue()) + " degrees left");
          LRSliderLabel.repaint();
        } else if (degrees > 0) {
          LRSliderLabel.setText("Left/Right Tilt: " + (int)source.getValue() + " degrees right");
          LRSliderLabel.repaint();
        } else {
          LRSliderLabel.setText("Left/Right Tilt: 0 degrees");
          LRSliderLabel.repaint();
        }
      }
      
      if (source == UDSlider) {
        int degrees = (int)source.getValue();
        
        if (degrees < 0) {
          UDSliderLabel.setText("Up/Down Tilt: " + (int)Math.abs(source.getValue()) + " degrees down");
          UDSliderLabel.repaint();
        } else if (degrees > 0) {
          UDSliderLabel.setText("Up/Down Tilt: " + (int)source.getValue() + " degrees up");
          UDSliderLabel.repaint();
        } else {
          UDSliderLabel.setText("Up/Down Tilt: 0 degrees");
          UDSliderLabel.repaint();
        }
      }
    }
  }
  
  /** Listener for the list in the ObjectTab */
  class ListListener implements ListSelectionListener {
    
    public void valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
        if (objectList.getSelectedIndex() == -1) {
          // No selection, disable remove and edit buttons
          removeButton.setEnabled(false);
          editButton.setEnabled(false);
        } else {
          removeButton.setEnabled(true);
          editButton.setEnabled(true);
        }
      }
    }
  }
}
