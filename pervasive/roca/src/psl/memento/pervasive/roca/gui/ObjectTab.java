package psl.memento.pervasive.roca.gui;

import psl.memento.pervasive.roca.room.*;
import psl.memento.pervasive.roca.data.*;
import psl.memento.pervasive.roca.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * ObjectTab.java
 *
 * @author Kristina Holst
 */
public class ObjectTab {
  private static ObjectTab theObjectTab;
  private JPanel mainPanel;
  private JTabbedPane positionTabbedPane;
  private JScrollPane scrollPane;
  private JLabel objectPreviewLabel, widthLabel, lengthLabel, heightLabel, LRSliderLabel, UDSliderLabel;
  //private JLabel[] frontViewRotationLabels, sideViewRotationLabels;
  private JComboBox objectSelector, directionSelector, userPositionSelector, userHeightSelector;
  private JComboBox cornerSelector, wallSelector, objectHeightSelector, objectClassSelector;
  private JRadioButton cornerRadioButton, wallRadioButton, middleRadioButton;
  private JTextField distanceField, directionField, inclineField;
  private JButton getDataButton, addButton, clearButton, editButton, removeButton, clearListButton;
  //private JButton sideViewLeftRotateButton, sideViewRightRotateButton, frontViewLeftRotateButton, frontViewRightRotateButton;
  private DefaultListModel listModel;
  private JList objectList;
  private JSlider scaleSlider, LRSlider, UDSlider;
  private double widthValue, lengthValue, heightValue;
  private static final int FIELD_LENGTH = 8;
  private static final double INCHES_TO_METERS = 1/39.37, FEET_TO_METERS = .3048;
  private static final int FRONT = 0, BEHIND = 1, LEFT = 2, RIGHT = 3, ABOVE = 4, UNDER = 5;
  private static final int SIMPLE = 0, GRAPHICAL = 1, PRECISE = 2;
  private static String[] objectClassStrings = {"Active", "Pervasive", "Physical", "Stationary", "All"};
  private static String[] userPositionStrings = {"middle", "NW corner", "NE corner", "SW corner",
  "SE corner"};
  private static String[] userHeightStrings = {"0", "1", "2", "3", "4", "5", "6", "7"};
  private static String[] cornerStrings = {"NW", "NE", "SW", "SE"};
  private static String[] wallStrings = {"North", "East", "South", "West"};
  private static String[] objectHeightStrings = {"Floor", "1/4 height of room", "1/2 height of room",
  "3/4 height of room", "Ceiling"};
  private static final String[] zeroStrings = new String[0];
  private HashMap activeMap, pervasiveMap, physicalMap, stationaryMap;
  private RoomObject currentObject;
  
  /** Builds the tab */
  protected ObjectTab() {
    // Set up combo box to determine which list of objects to show
    objectClassSelector = new JComboBox(objectClassStrings);
    objectClassSelector.setSelectedIndex(1);
    objectClassSelector.addActionListener(new ComboBoxListener());
    
    // Set up combo box for choosing object
    objectSelector = new JComboBox(updateObjectSelectorList((String)objectClassSelector.getSelectedItem()));
    objectSelector.setSelectedIndex(0);
    objectSelector.addActionListener(new ComboBoxListener());
    
    String currentObject = (String)objectSelector.getItemAt(0);
    
    // Will have to get the following values from database
    widthValue = 7.5;
    lengthValue = 25.85;
    heightValue = 12.34;
    
    // Set up initial labels
    objectPreviewLabel = new JLabel(new ImageIcon(ClassLoader.getSystemResource("images/" + currentObject + ".gif")));
    widthLabel = new JLabel(String.valueOf(widthValue));
    lengthLabel = new JLabel(String.valueOf(lengthValue));
    heightLabel = new JLabel(String.valueOf(heightValue));
    
    // Set up text fields
    distanceField = new JTextField(FIELD_LENGTH);
    directionField = new JTextField(FIELD_LENGTH);
    inclineField = new JTextField(FIELD_LENGTH);
    
    // Set up buttons
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
    
    /*
     sideViewLeftRotateButton = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/leftarrow.gif")));
    sideViewLeftRotateButton.addActionListener(new ButtonListener());
    sideViewRightRotateButton = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/rightarrow.gif")));
    sideViewRightRotateButton.addActionListener(new ButtonListener());
    frontViewLeftRotateButton = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/leftarrow.gif")));
    frontViewLeftRotateButton.addActionListener(new ButtonListener());
    frontViewRightRotateButton = new JButton(new ImageIcon(ClassLoader.getSystemResource("images/rightarrow.gif")));
    frontViewRightRotateButton.addActionListener(new ButtonListener());
     */
    
    JPanel objectSelectorSubPanel1 = new JPanel();
    objectSelectorSubPanel1.add(new JLabel("   List: "));
    objectSelectorSubPanel1.add(objectClassSelector);
    
    JPanel objectSelectorSubPanel2 = new JPanel();
    objectSelectorSubPanel2.add(new JLabel("Object: "));
    objectSelectorSubPanel2.add(objectSelector);
    
    // Set up object selector panel with combo box and label
    JPanel objectSelectorPanel = new JPanel();
    objectSelectorPanel.setLayout(new BoxLayout(objectSelectorPanel, BoxLayout.Y_AXIS));
    objectSelectorPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,30));
    objectSelectorPanel.add(objectSelectorSubPanel1);
    objectSelectorPanel.add(objectSelectorSubPanel2);
    
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
     * either the "simple", "graphical" or "precise" way of determining an object's position.
     */
    
    cornerRadioButton = new JRadioButton("In a corner", true);
    wallRadioButton = new JRadioButton("Middle of a wall", false);
    middleRadioButton = new JRadioButton("Middle of the room", false);
    
    ButtonGroup group = new ButtonGroup();
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
    //precisePosSubPanel2.add(Box.createRigidArea(new Dimension(3, 1)));
    //precisePosSubPanel2.add(getDataButton);
    
    JPanel precisePosPanel = new JPanel();
    precisePosPanel.setLayout(new GridLayout(0, 1));
    precisePosPanel.add(precisePosSubPanel1);
    precisePosPanel.add(precisePosSubPanel2);
    
    JPanel labelPanel = new JPanel();
    labelPanel.setLayout(new GridLayout(0, 1));
    labelPanel.add(new JLabel("(Upon clicking \"Add to List\", the view will switch"));
    labelPanel.add(new JLabel("to the Preview tab for object placement)"));
    
    JPanel graphicalPosPanel = new JPanel();
    graphicalPosPanel.add(labelPanel);
    
    positionTabbedPane = new javax.swing.JTabbedPane();
    positionTabbedPane.setTabPlacement(javax.swing.JTabbedPane.TOP);
    positionTabbedPane.setPreferredSize(new Dimension(500, 105));
    positionTabbedPane.addTab("Simple Position Finder", simplePosPanel);
    positionTabbedPane.addTab("Graphical Position Finder", graphicalPosPanel);
    positionTabbedPane.addTab("Precise Position Finder", precisePosPanel);
    
    String[] directionStrings = {"North", "East", "South", "West"};
    
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
    LRSlider.setMajorTickSpacing(90);
    LRSlider.setSnapToTicks(true);
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
    UDSlider.setMajorTickSpacing(90);
    UDSlider.setSnapToTicks(true);
    UDSlider.setPaintTicks(true);
    UDSlider.setPaintLabels(true);
    
    // Add slider and label to its own panel
    JPanel rotationSubPanel3 = new JPanel();
    rotationSubPanel3.setLayout(new BoxLayout(rotationSubPanel3, BoxLayout.Y_AXIS));
    rotationSubPanel3.add(UDSliderLabel);
    rotationSubPanel3.add(UDSlider);
    
/*
    JPanel upperRotationSubPanel2 = new JPanel();
    upperRotationSubPanel2.add(frontViewLeftRotateButton);
    upperRotationSubPanel2.add(new JLabel("Rotate"));
    upperRotationSubPanel2.add(frontViewRightRotateButton);
 
    frontViewRotationLabels = new JLabel[4];
 
    frontViewRotationLabels[0] = new JLabel("Top");
    frontViewRotationLabels[0].setHorizontalAlignment(SwingConstants.CENTER);
    frontViewRotationLabels[1] = new JLabel("Right");
    frontViewRotationLabels[2] = new JLabel("Bottom");
    frontViewRotationLabels[2].setHorizontalAlignment(SwingConstants.CENTER);
    frontViewRotationLabels[3] = new JLabel("Left");
 
    JPanel lowerRotationSubPanel2 = new JPanel();
    lowerRotationSubPanel2.setLayout(new BorderLayout());
    lowerRotationSubPanel2.add(frontViewRotationLabels[0], BorderLayout.NORTH);
    lowerRotationSubPanel2.add(frontViewRotationLabels[1], BorderLayout.EAST);
    lowerRotationSubPanel2.add(frontViewRotationLabels[2], BorderLayout.SOUTH);
    lowerRotationSubPanel2.add(frontViewRotationLabels[3], BorderLayout.WEST);
    lowerRotationSubPanel2.add(new JLabel(new ImageIcon(ClassLoader.getSystemResource("images/frontview.gif"))), BorderLayout.CENTER);
 
    JPanel rotationSubPanel2 = new JPanel();
    rotationSubPanel2.setLayout(new BoxLayout(rotationSubPanel2, BoxLayout.Y_AXIS));
    rotationSubPanel2.add(upperRotationSubPanel2);
    rotationSubPanel2.add(lowerRotationSubPanel2);
 
    JPanel upperRotationSubPanel3 = new JPanel();
    upperRotationSubPanel3.add(sideViewLeftRotateButton);
    upperRotationSubPanel3.add(new JLabel("Rotate"));
    upperRotationSubPanel3.add(sideViewRightRotateButton);
 
    sideViewRotationLabels = new JLabel[4];
 
    sideViewRotationLabels[0] = new JLabel("Top");
    sideViewRotationLabels[0].setHorizontalAlignment(SwingConstants.CENTER);
    sideViewRotationLabels[1] = new JLabel("Right");
    sideViewRotationLabels[2] = new JLabel("Bottom");
    sideViewRotationLabels[2].setHorizontalAlignment(SwingConstants.CENTER);
    sideViewRotationLabels[3] = new JLabel("Left");
 
    JPanel lowerRotationSubPanel3 = new JPanel();
    lowerRotationSubPanel3.setLayout(new BorderLayout());
    lowerRotationSubPanel3.add(sideViewRotationLabels[0], BorderLayout.NORTH);
    lowerRotationSubPanel3.add(sideViewRotationLabels[1], BorderLayout.EAST);
    lowerRotationSubPanel3.add(sideViewRotationLabels[2], BorderLayout.SOUTH);
    lowerRotationSubPanel3.add(sideViewRotationLabels[3], BorderLayout.WEST);
    lowerRotationSubPanel3.add(new JLabel(new ImageIcon(ClassLoader.getSystemResource("images/sideview.gif"))), BorderLayout.CENTER);
 
    JPanel rotationSubPanel3 = new JPanel();
    rotationSubPanel3.setLayout(new BoxLayout(rotationSubPanel3, BoxLayout.Y_AXIS));
    rotationSubPanel3.add(upperRotationSubPanel3);
    rotationSubPanel3.add(lowerRotationSubPanel3);
 */
    
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
    middlePanel.setBorder(BorderFactory.createEmptyBorder(5, 30, 15, 0));
    middlePanel.add(rotationPanel);
    middlePanel.add(positionTabbedPane);
    middlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
    middlePanel.add(mainButtonPanel);
    
    /* To get everything to stay aligned when window is maximized, we need to
     * add all the components to an intermediate panel. */
    JPanel dummyPanel = new JPanel();
    dummyPanel.setLayout(new BoxLayout(dummyPanel, BoxLayout.Y_AXIS));
    dummyPanel.add(topObjectPanel);
    dummyPanel.add(middlePanel);
    dummyPanel.add(objectListPanel);
    
    // Set up main panel
    mainPanel = new JPanel();
    mainPanel.add(dummyPanel);
  }
  
  public static ObjectTab getInstance() {
    if (theObjectTab == null) {
      theObjectTab = new ObjectTab();
    }
    
    return theObjectTab;
  }
  
  
  /** Provides a handle on the tab's main panel
   *  @return the main panel
   */
  public Component getMainPanel() {
    return mainPanel;
  }
  
  public RoomObject getCurrentObject() {
    return currentObject;
  }
  
  /** Opens pop-up window to display error message
   *  @param message the error message to be displayed
   */
  private void errorPopUp(String message) {
    JOptionPane error = new JOptionPane();
    error.showMessageDialog(null, message, "Alert", JOptionPane.INFORMATION_MESSAGE);
  }
  
  /** Adds an object to the list of objects in the room */
  private void beginAddObject() {
    boolean positionSet = false;
    
    /* Set the object's type */
    String type = (String)objectSelector.getSelectedItem();
    
    /* Set the object's category */
    String category = determineObjectCategoryForSelector(type);
    
    /* Set the object's size information */
    SizeData size = null;
    
    try {
      size = new SizeData(Double.parseDouble(widthLabel.getText()) * INCHES_TO_METERS,
      Double.parseDouble(lengthLabel.getText()) * INCHES_TO_METERS, Double.parseDouble(heightLabel.getText()) * INCHES_TO_METERS);
    } catch (NumberFormatException ex) {
      errorPopUp("Invalid size values.");
      return;
    }
    
    /* Set the object's rotation information */
    RotationData rotation = setRotation();
    
    /* Set the object's position information */
    CartesianCoord position = new CartesianCoord();
    
    /* If there were no problems above, create the new RoomObject, excluding the position */
    if ((type != null) && (size != null) && (rotation != null)) {
      currentObject = new RoomObject(category, type, size, rotation);

      SizeData positioningSize = new SizeData();
      determineSpaceNeeded(positioningSize);
      currentObject.setPlacingSize(positioningSize);
      positionSet = placeObject(positioningSize, position);
    }
    
    if (positionSet) {
      if (position != null) {
        finishAddObject(position);
      }
    } else {
      // do nothing, RoomDrawer.newObjectPlaced() will call finishAddObject() later
    }
  }
  
  public void finishAddObject(CartesianCoord iPos) {
    
    if (iPos != null) {
      SizeData size = currentObject.getPlacingSize();
      if ((iPos = checkConflicts(size, iPos, true)) == null) {
        errorPopUp("Object cannot be placed there -- not enough free space in surrounding area.");
      } else {
        currentObject.setPosition(iPos);
        Room room = Room.getInstance();
        String category = currentObject.getCategory();
        
        if (category.equalsIgnoreCase("pervasive")) {
          room.addPervasiveObject(currentObject);
        } else if (category.equalsIgnoreCase("physical")) {
          room.addPhysicalObject(currentObject);
        } else if (category.equalsIgnoreCase("active")) {
          room.addActiveObject(currentObject);
        } else if (category.equalsIgnoreCase("stationary")) {
          room.addStationaryObject(currentObject);
        }
        
        listModel.addElement(currentObject);
        
        objectList.setSelectedIndex(listModel.indexOf(currentObject));
      }
    }
  }
  
  private boolean placeObject(SizeData iSize, CartesianCoord iPos) {
    boolean notGraphical = false;
    
    /* Option 1: precise positioning selected */
    if (positionTabbedPane.getSelectedIndex() == PRECISE) {
      if (distanceField.getText().equals("") || directionField.getText().equals("") ||
      inclineField.getText().equals("")) {
        errorPopUp("All fields must be filled before adding an object to the list.");
      } else {
        CartesianCoord tempPos = setPrecisePosition();
        iPos.setX(tempPos.getX());
        iPos.setY(tempPos.getY());
        iPos.setZ(tempPos.getZ());
        notGraphical = true;
      }
      /* Option 2: simple positioning selected */
    } else if (positionTabbedPane.getSelectedIndex() == SIMPLE) {
      CartesianCoord tempPos = setSimplePosition();
      iPos.setX(tempPos.getX());
      iPos.setY(tempPos.getY());
      iPos.setZ(tempPos.getZ());
      notGraphical = true;
    } else if (positionTabbedPane.getSelectedIndex() == GRAPHICAL){
      setGraphicalPosition(iSize);
      notGraphical = false;
    }
    
    return notGraphical;
  }
  
    /*
     * true = search for conflicts and try to find available space nearby
     * false = search for conflicts and stop if conflicts exist
     */
  private CartesianCoord checkConflicts(SizeData iSpaceNeeded, CartesianCoord iPosition, boolean iResolve) {
    Room room = Room.getInstance();
    LinkedList[] existingObjects = room.getAllObjects();
    RoomObject obj;
    
    for (int i = 0; i < existingObjects.length; i++) {
      if (existingObjects[i].size() > 0) {
        ListIterator itr = existingObjects[i].listIterator(0);
        
        while (itr.hasNext() && iPosition != null) {
          obj = (RoomObject)itr.next();
          
          if (iPosition.checkOverlap(iSpaceNeeded, obj)) {
            if (iResolve) {
              iPosition = findNeighboringFreeSpace(iSpaceNeeded, obj);
            } else {
              iPosition = null;
            }
          }
        }
      }
    }
    
    return iPosition;
  }
  
    /* If two objects overlap, we try to put the newer one either directly above, or below, or
     * next to, etc., the other one.  Not the best way, since the object may be shifted
     * from its original destination more than necessary, but this should only be a temporary
     * fix anyway, until Vlad has written the real version.
     */
  private CartesianCoord findNeighboringFreeSpace(SizeData iSpaceNeeded, RoomObject iObstacle) {
    CartesianCoord freeSpace = null, testPos;
    CartesianCoord[] possibleDestinations = new CartesianCoord[6];
    Room room = Room.getInstance();
    SizeData size = iObstacle.getPlacingSize();
    CartesianCoord pos = iObstacle.getPosition();
    /* Add a slight buffer to distance objects from one another */
    double buffer = .01;
    boolean[] options = new boolean[6];
    boolean doPopUp = false;
    
    // try in front of obstacle
    if (room.canContain(iSpaceNeeded, testPos = new CartesianCoord(pos.getX(), pos.getY() + size.getLength() + buffer, pos.getZ()))) {
      if (checkConflicts(iSpaceNeeded, testPos, false) != null) {
        options[FRONT] = true;
        possibleDestinations[FRONT] = testPos;
      } else {
        options[FRONT] = false;
      }
    }
    
    // try behind obstacle
    if (room.canContain(iSpaceNeeded, testPos = new CartesianCoord(pos.getX(), pos.getY() - iSpaceNeeded.getLength() - buffer, pos.getZ()))) {
      if (checkConflicts(iSpaceNeeded, testPos, false) != null) {
        options[BEHIND] = true;
        possibleDestinations[BEHIND] = testPos;
      } else {
        options[BEHIND] = false;
      }
    }
    
    // try above the obstacle
    if (room.canContain(iSpaceNeeded, testPos = new CartesianCoord(pos.getX(), pos.getY(), pos.getZ() + size.getHeight() + buffer))) {
      if (checkConflicts(iSpaceNeeded, testPos, false) != null) {
        options[ABOVE] = true;
        possibleDestinations[ABOVE] = testPos;
      } else {
        options[ABOVE] = false;
      }
    }
    
    // try below the obstacle
    if (room.canContain(iSpaceNeeded, testPos = new CartesianCoord(pos.getX(), pos.getY(), pos.getZ() - iSpaceNeeded.getHeight() - buffer))) {
      if (checkConflicts(iSpaceNeeded, testPos, false) != null) {
        options[UNDER] = true;
        possibleDestinations[UNDER] = testPos;
      } else {
        options[UNDER] = false;
      }
    }
    
    // try to the left of the obstacle
    if (room.canContain(iSpaceNeeded, testPos = new CartesianCoord(pos.getX() - iSpaceNeeded.getWidth() - buffer, pos.getY(), pos.getZ()))) {
      if (checkConflicts(iSpaceNeeded, testPos, false) != null) {
        options[LEFT] = true;
        possibleDestinations[LEFT] = testPos;
      } else {
        options[LEFT] = false;
      }
    }
    
    // try to the right of the obstacle
    if (room.canContain(iSpaceNeeded, testPos = new CartesianCoord(pos.getX() + size.getWidth() + buffer, pos.getY(), pos.getZ()))) {
      if (checkConflicts(iSpaceNeeded, testPos, false) != null) {
        options[RIGHT] = true;
        possibleDestinations[RIGHT] = testPos;
      } else {
        options[RIGHT] = false;
      }
    }
    
    for (int i = 0; i < options.length; i++) {
      if (options[i]) {
        doPopUp = true;
      }
    }
    
    if (doPopUp) {
      String selection = placementPopUp(options, iObstacle.getType());
      
      if (selection == null) {
        freeSpace = null;
      } else if (selection.startsWith("In front")) {
        freeSpace = possibleDestinations[FRONT];
      } else if (selection.startsWith("Behind")) {
        freeSpace = possibleDestinations[BEHIND];
      } else if (selection.startsWith("Above")) {
        freeSpace = possibleDestinations[ABOVE];
      } else if (selection.startsWith("Under")) {
        freeSpace = possibleDestinations[UNDER];
      } else if (selection.startsWith("Left")) {
        freeSpace = possibleDestinations[LEFT];
      } else if (selection.startsWith("Right")) {
        freeSpace = possibleDestinations[RIGHT];
      }
    }
    
    return freeSpace;
  }
  
  private String placementPopUp(boolean[] choices, String obstacle) {
    LinkedList strings = new LinkedList();
    
    if (choices[FRONT])
      strings.add("In front of the " + obstacle);
    if (choices[BEHIND])
      strings.add("Behind the " + obstacle);
    if (choices[ABOVE])
      strings.add("Above the " + obstacle);
    if (choices[UNDER])
      strings.add("Under the " + obstacle);
    if (choices[LEFT])
      strings.add("Left of the " + obstacle);
    if (choices[RIGHT])
      strings.add("Right of the " + obstacle);
    
    String[] values = (String[])strings.toArray(zeroStrings);
    
    JOptionPane option = new JOptionPane();
    
    return (String)option.showInputDialog(null, "There is a conflict with another object. Choose a new position:", "Conflict",
    JOptionPane.INFORMATION_MESSAGE, null, values, values[0]);
  }
  
  public String determineObjectCategoryForSelector(String iType) {
    String objectClass = (String)objectClassSelector.getSelectedItem();
    
    if (objectClass.equalsIgnoreCase("All")) {
      if (activeMap.containsKey(iType)) {
        objectClass = "Active";
      } else if (pervasiveMap.containsKey(iType)) {
        objectClass = "Pervasive";
      } else if (physicalMap.containsKey(iType)) {
        objectClass = "Physical";
      } else if (stationaryMap.containsKey(iType)) {
        objectClass = "Stationary";
      }
    }
    
    return objectClass;
  }
  
  public String determineObjectCategoryForEditing(String iType) {
    if ((activeMap == null) || (pervasiveMap == null) || (physicalMap == null)
    || (stationaryMap == null)) {
     initializeHashMaps(); 
    }
     
    String objectClass = "";
    
      if (activeMap.containsKey(iType)) {
        objectClass = "Active";
      } else if (pervasiveMap.containsKey(iType)) {
        objectClass = "Pervasive";
      } else if (physicalMap.containsKey(iType)) {
        objectClass = "Physical";
      } else if (stationaryMap.containsKey(iType)) {
        objectClass = "Stationary";
      }
    
    return objectClass;
  }
  
  /** Calculates the position of an object being added to the room, only called if
   *  user chooses precise positioning method
   *  NOTE: To match with Vlad's code, this uses the convention that the upper left corner is (0,0)
   *
   */
  private CartesianCoord setPrecisePosition() {
    double dist = 0.0, dir = 0.0, inc = 0.0;
    
    /* Get the measurement data entered in the text fields */
    try {
      dist = Double.parseDouble(distanceField.getText());
      dir = Double.parseDouble(directionField.getText());
      inc = Double.parseDouble(inclineField.getText());
    } catch (NumberFormatException ex) {
      errorPopUp("All fields must contain valid numbers.");
      return null;
    }
    
    /* Determine the point from which these measurements are being taken */
    CartesianCoord userPosition = determineUsersPositionInRoom((String)userPositionSelector.getSelectedItem(),
    (String)userHeightSelector.getSelectedItem());
    
    /* Adjust the directional coordinate since user has defined new directional frame */
    SphericalCoord spherPosition = new SphericalCoord(dist, dir, inc);
    spherPosition.synchronizeDirectionalOrientation();
    
    /* Translate object's position from spherical to Cartesian coordinate system */
    CartesianCoord position = new CartesianCoord();
    position.createFromSphericalCoord(spherPosition);
    
    /* Finally, compensate for fact that the measurements were taken from a certain point in the room */
    position.setX(position.getX() + userPosition.getX());
    position.setY(position.getY() + userPosition.getY());
    position.setZ(position.getZ() + userPosition.getZ());
    
    return position;
  }
  
  
  /** Calculates the position of an object being added to the room, only called if
   *  user chooses simple positioning method
   *  NOTE: To match with Vlad's code, this uses the convention that the upper left corner is (0,0)
   *  @param iPosition the CartesianCoord that will store the requested positioning information
   */
  private CartesianCoord setSimplePosition() {
    psl.memento.pervasive.roca.room.Room room = psl.memento.pervasive.roca.room.Room.getInstance();
    
    CartesianCoord position = new CartesianCoord();
    
    double roomWidth = room.getSpanNSWalls(); // "width" defined as span of north wall
    double roomDepth = room.getSpanWEWalls(); // "depth" defined as span of west wall
    
    /* Get object dimensions */
    double objWidth = Double.parseDouble(widthLabel.getText()) * INCHES_TO_METERS;
    double objDepth = Double.parseDouble(lengthLabel.getText()) * INCHES_TO_METERS;
    
    /* Object goes in a corner */
    if (cornerRadioButton.isSelected()) {
      String corner = (String)cornerSelector.getSelectedItem();
      
      if (corner.equalsIgnoreCase("nw")) {
        position.setX(0);
        position.setY(0);
      } else if (corner.equalsIgnoreCase("ne")) {
        position.setX(roomWidth - objWidth);
        position.setY(0);
      } else if (corner.equalsIgnoreCase("sw")) {
        position.setX(0);
        position.setY(roomDepth - objDepth);
      } else if (corner.equalsIgnoreCase("se")) {
        position.setX(roomWidth - objWidth);
        position.setY(roomDepth - objDepth);
      }
      
      /* Object goes against a wall, not in a corner */
    } else if (wallRadioButton.isSelected()) {
      String wall = (String)wallSelector.getSelectedItem();
      
      if (wall.equalsIgnoreCase("north")) {
        position.setX((.5 * roomWidth) - (.5 * objWidth));
        position.setY(0);
      } else if (wall.equalsIgnoreCase("south")) {
        position.setX((.5 * roomWidth) - (.5 * objWidth));
        position.setY(roomDepth - objDepth);
      } else if (wall.equalsIgnoreCase("east")) {
        position.setX(roomWidth - objWidth);
        position.setY((.5 * roomDepth) - (.5 * objDepth));
      } else if (wall.equalsIgnoreCase("west")) {
        position.setX(0);
        position.setY((.5 * roomDepth) - (.5 * objDepth));
      }
      
      /* Object goes in middle of the room */
    } else {
      position.setX((.5 * roomWidth) - (.5 * objWidth));
      position.setY((.5 * roomDepth) - (.5 * objDepth));
    }
    
    /* Determine how high off the floor to place the object */
    String chosenHeight = (String)objectHeightSelector.getSelectedItem();
    double roomHeight = room.getHeight();
    double objHeight = Double.parseDouble(heightLabel.getText()) * INCHES_TO_METERS;
    
    if (chosenHeight.equalsIgnoreCase("floor"))
      position.setZ(0);
    else if (chosenHeight.equalsIgnoreCase("1/4 height of room"))
      position.setZ((.25 * roomHeight) - (.5 * objHeight));
    else if (chosenHeight.equalsIgnoreCase("1/2 height of room"))
      position.setZ((.5 * roomHeight) - (.5 * objHeight));
    else if (chosenHeight.equalsIgnoreCase("3/4 height of room"))
      position.setZ((.75 * roomHeight) - (.5 * objHeight));
    else if (chosenHeight.equalsIgnoreCase("ceiling"))
      position.setZ(roomHeight - objHeight);
    
    return position;
  }
  
  
  /** For precise positioning method, we need to know from what point in the
   *  room the user is taking his/her measurements
   *  @param iPosition the user's specified location in the room (i.e. NW corner)
   *  @param iHeight the height at which the user claims to be holding the device
   *  @return a CartesianCoord containing the user's current position in the room
   */
  private CartesianCoord determineUsersPositionInRoom(String iPosition, String iHeight) {
    CartesianCoord userPosition = new CartesianCoord();
    
    psl.memento.pervasive.roca.room.Room room = psl.memento.pervasive.roca.room.Room.getInstance();
    
    double roomWidth = room.getSpanNSWalls();  // "width" defined as span of north wall
    double roomDepth = room.getSpanWEWalls();  // "depth" defined as span of west wall
    
    /* Determine the height at which the measuring device is held */
    try {
      userPosition.setZ(Double.parseDouble(iHeight) * FEET_TO_METERS);
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
  
  
  private void setGraphicalPosition(SizeData iSize) {
    CartesianCoord position = null;
    
    JTabbedPane tabbedPane = (JTabbedPane)mainPanel.getParent();
    tabbedPane.setSelectedIndex(3);
    
    RoomDrawer roomDrawer = RoomDrawer.getInstance();
    roomDrawer.beginPlacingNewObject(iSize);
  }
  
  /** Calculates the rotation of an object being added to the room
   */
  private RotationData setRotation() {
    String dirFacing = (String)directionSelector.getSelectedItem();
    int yaw = 0;
    
    if (dirFacing.equalsIgnoreCase("North"))
      yaw = RotationData.N;
    //else if (dirFacing.equalsIgnoreCase("Northeast"))
    //yaw = RotationData.NE;
    else if (dirFacing.equalsIgnoreCase("East"))
      yaw = RotationData.E;
    //else if (dirFacing.equalsIgnoreCase("Southeast"))
    //yaw = RotationData.SE;
    else if (dirFacing.equalsIgnoreCase("South"))
      yaw = RotationData.S;
    //else if (dirFacing.equalsIgnoreCase("Southwest"))
    //yaw = RotationData.SW;
    else if (dirFacing.equalsIgnoreCase("West"))
      yaw = RotationData.W;
    //else if (dirFacing.equalsIgnoreCase("Northwest"))
    //yaw = RotationData.NW;
    
    int roll = LRSlider.getValue();
    
    /* If roll value is negative, find its positive equivalent */
    if (roll < 0) {
      roll = 360 + roll;
    }
    
    int pitch = UDSlider.getValue();
    
    if (pitch < 0) {
      pitch = 360 + pitch;
    }
    
    
    RotationData rotation = new RotationData(yaw, roll, pitch);
    
    return rotation;
  }
  
  private void determineSpaceNeeded(SizeData iSize) {
    SizeData objSize = currentObject.getSize();
    RotationData objRotation = currentObject.getRotation();
    double width = objSize.getWidth(), length = objSize.getLength(), height = objSize.getHeight();
    
    iSize.setWidth(width);
    iSize.setLength(length);
    iSize.setHeight(height);
    
    if (objRotation.getYaw() == 90 || objRotation.getYaw() == 270) {
      double temp = width;
      width = length;
      length = temp;
      iSize.setWidth(width);
      iSize.setLength(length);
    }
    
    if (objRotation.getRoll() == 90 || objRotation.getRoll() == 270) {
      double temp = width;
      width = height;
      height = temp;
      iSize.setWidth(width);
      iSize.setHeight(height);
    }
    
    if (objRotation.getPitch() == 90 || objRotation.getPitch() == 270) {
      double temp = height;
      height = length;
      length = temp;
      iSize.setHeight(height);
      iSize.setLength(length);
    }
  }
  
  
  /** Sets all fields, sliders, etc. to the values set for a particular object previously
   *  added to the room, so that it may be edited
   */
  private void editObject() {
    Room room = Room.getInstance();
    RoomObject roomObject = null;
    
    String type = ((RoomObject)objectList.getSelectedValue()).getType();
    String category = determineObjectCategoryForEditing(type);

    if (category.equalsIgnoreCase("pervasive")) {
      roomObject = room.getPervasiveObject((RoomObject)objectList.getSelectedValue());
    } else if (category.equalsIgnoreCase("physical")) {
      roomObject = room.getPhysicalObject((RoomObject)objectList.getSelectedValue());
    } else if (category.equalsIgnoreCase("active")) {
      roomObject = room.getActiveObject((RoomObject)objectList.getSelectedValue());
    } else if (category.equalsIgnoreCase("stationary")) {
      roomObject = room.getStationaryObject((RoomObject)objectList.getSelectedValue());
    }
    
    if (roomObject != null) {
      /* Set list selector */
      objectClassSelector.setSelectedItem(category);
      
      /* Set object selector */
      objectSelector.setSelectedItem(type);
      
      SizeData size = roomObject.getSize();
      
      widthLabel.setText(String.valueOf(size.getWidth()));
      lengthLabel.setText(String.valueOf(size.getLength()));
      heightLabel.setText(String.valueOf(size.getHeight()));
      
      widthLabel.repaint();
      lengthLabel.repaint();
      heightLabel.repaint();
      
            /* Size slider should be set by dividing the object's width, length, and
             * height by the default values and taking the average of these 3
             */
      
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
  
  
  /** Removes item from list of objects in the room */
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
    RoomObject roomObject = (RoomObject)objectList.getSelectedValue();
    String category = roomObject.getCategory();
    
    if (category.equalsIgnoreCase("pervasive")) {
      room.removePervasiveObject(roomObject);
    } else if (category.equalsIgnoreCase("physical")) {
      room.removePhysicalObject(roomObject);
    } else if (category.equalsIgnoreCase("active")) {
      room.removeActiveObject(roomObject);
    } else if (category.equalsIgnoreCase("stationary")) {
      room.removeStationaryObject(roomObject);
    }
  }
  
  
  /** Resets all fields, combo boxes, etc */
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
  
  
  /** Clears list of objects current in room */
  public void clearCurrentObjectList() {
    Room room = Room.getInstance();
    
    room.removeAllActiveObjects();
    room.removeAllPhysicalObjects();
    room.removeAllPervasiveObjects();
    room.removeAllStationaryObjects();
    
    listModel.clear();
  }
  
  
  /** Changes the list of available objects to match the chosen category of objects */
  private String[] updateObjectSelectorList(String iCategory) {
    if (iCategory.equalsIgnoreCase("active")) {
      /* Will need to get real list of active objects from database,
       * but since that doesn't exist yet, use these values */
      return getDummyActiveObjects();
    } else if (iCategory.equalsIgnoreCase("pervasive")) {
      /* Will need to get real list of pervasive objects from database,
       * but since that doesn't exist yet, use these values */
      return getDummyPervasiveObjects();
    } else if (iCategory.equalsIgnoreCase("physical")) {
      /* Will need to get real list of physical objects from database,
       * but since that doesn't exist yet, use these values */
      return getDummyPhysicalObjects();
    } else if (iCategory.equalsIgnoreCase("stationary")) {
      /* Will need to get real list of stationary objects from database,
       * but since that doesn't exist yet, use these values */
      return getDummyStationaryObjects();
    } else if (iCategory.equalsIgnoreCase("all")) {
      /* Will need to get real list of all objects from database,
       * but since that doesn't exist yet, use these values */
      return getAllDummyObjects();
    }
    
    return null;
  }
  
  private void initializeHashMaps() {
    getAllDummyObjects();
  }
  
  private String[] getDummyActiveObjects() {
    if (activeMap == null) {
      activeMap = new HashMap();
      activeMap.put("AR Object1", "AR Object1");
      activeMap.put("AR Object2", "AR Object2");
      activeMap.put("AR Object3", "AR Object3");
    }
    
    return (String[])activeMap.keySet().toArray(zeroStrings);
  }
  
  private String[] getDummyPervasiveObjects() {
    if (pervasiveMap == null) {
      pervasiveMap = new HashMap();
      pervasiveMap.put("Monitor", "Monitor");
      pervasiveMap.put("Projector Screen", "Projector Screen");
      pervasiveMap.put("Whiteboard", "Whiteboard");
    }
    
    return (String[])pervasiveMap.keySet().toArray(zeroStrings);
  }
  
  private String[] getDummyPhysicalObjects() {
    if (physicalMap == null) {
      physicalMap = new HashMap();
      physicalMap.put("Chair", "Chair");
      physicalMap.put("Desk", "Desk");
      physicalMap.put("Lamp", "Lamp");
    }
    
    return (String[])physicalMap.keySet().toArray(zeroStrings);
  }
  
  private String[] getDummyStationaryObjects() {
    if (stationaryMap == null) {
      stationaryMap = new HashMap();
      stationaryMap.put("Pillar", "Pillar");
      stationaryMap.put("Wall Projection", "Wall Projection");
    }
    
    return (String[])stationaryMap.keySet().toArray(zeroStrings);
  }
  
  private String[] getAllDummyObjects() {
    LinkedList allObjects = new LinkedList();
    
    getDummyActiveObjects();
    getDummyPervasiveObjects();
    getDummyPhysicalObjects();
    getDummyStationaryObjects();
    
    allObjects.addAll(activeMap.keySet());
    allObjects.addAll(pervasiveMap.keySet());
    allObjects.addAll(physicalMap.keySet());
    allObjects.addAll(stationaryMap.keySet());
    
    return (String[])allObjects.toArray(zeroStrings);
  }
  
  
  /** Update the preview window to display a thumbnail of the currently selected object */
  private void updatePreviewWindow() {
    String objectName = (String)objectSelector.getSelectedItem();
    objectPreviewLabel.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/" + objectName + ".gif")));
    
    // Get default width/length/height values from database and update size labels
  }
  
  public void addObjectToListModel(RoomObject iRoomObject) {
    listModel.addElement(iRoomObject); 
    objectList.setSelectedIndex(listModel.indexOf(iRoomObject));
  }
  /*
  private void rotateLeft(JLabel[] labels) {
    String temp = labels[0].getText();
   
    for (int i = 0; i < labels.length - 1; i++) {
      labels[i].setText(labels[i + 1].getText());
    }
   
    labels[3].setText(temp);
  }
   
  private void rotateRight(JLabel[] labels) {
    String temp = labels[3].getText();
   
    for (int i = labels.length - 1; i > 0; i--) {
      labels[i].setText(labels[i - 1].getText());
    }
   
    labels[0].setText(temp);
  }
   */
  /** Listener for all buttons in the ObjectTab */
  class ButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      
      if (source == getDataButton) {
        
      /* NOT YET IMPLEMENTED
       * Retrieve data from hardware components, fill in text fields with retrieved values
       */
        
      } else if (source == addButton) {
        psl.memento.pervasive.roca.room.Room room = psl.memento.pervasive.roca.room.Room.getInstance();
        
        if (room.getHeight() > 0) {
          beginAddObject();
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
      
      /*else if (source == sideViewLeftRotateButton) {
        rotateLeft(sideViewRotationLabels);
      } else if (source == sideViewRightRotateButton) {
        rotateRight(sideViewRotationLabels);
      } else if (source == frontViewLeftRotateButton) {
        rotateLeft(frontViewRotationLabels);
      } else if (source == frontViewRightRotateButton) {
        rotateRight(frontViewRotationLabels);
      }
       */
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
        String[] objects = updateObjectSelectorList((String)objectClassSelector.getSelectedItem());
        
        objectSelector.removeAllItems();
        
        for (int i = 0; i < objects.length; i++) {
          objectSelector.addItem(objects[i]);
        }
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