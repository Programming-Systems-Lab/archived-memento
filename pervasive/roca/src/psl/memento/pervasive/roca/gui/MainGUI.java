package psl.memento.pervasive.roca.gui;

import psl.memento.pervasive.roca.data.*;
import psl.memento.pervasive.roca.room.Room;
import psl.memento.pervasive.roca.util.*;
import psl.memento.pervasive.roca.vem.RoomViewerPanel;
import org.jdom.*;
import org.jdom.output.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.File;

/**
 * MainGUI.java
 *
 * @author Kristina Holst
 */
public class MainGUI extends JComponent implements ActionListener, ChangeListener {
  private JFileChooser fc;
  private JTabbedPane tabbedPane;
  private static final int ROOM_INFO = 0, OBJ_INFO = 1, SUMMARY = 2, PREVIEW = 3;
  
  private RoomTab roomTab = RoomTab.getInstance();
  private ObjectTab objectTab = ObjectTab.getInstance();
  private SummaryTab summaryTab = new SummaryTab();
  private PreviewTab previewTab = new PreviewTab();
  
  public Component createComponents() {
    tabbedPane = new JTabbedPane();
    tabbedPane.setTabPlacement(JTabbedPane.TOP);
    tabbedPane.setPreferredSize(new Dimension(780,570));
    tabbedPane.addChangeListener(this);
    
    tabbedPane.addTab("Room Information", roomTab.getMainPanel());
    tabbedPane.addTab("Object Information", objectTab.getMainPanel());
    tabbedPane.addTab("Summary", summaryTab.getMainPanel());
    tabbedPane.addTab("Preview", previewTab.getMainPanel());
    
    JPanel pane = new JPanel();
    pane.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
    
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    pane.add(tabbedPane);
    
    return pane;
  }
  
  public JMenuBar createMenu() {
    // Add menu
    JMenu menu = new JMenu("File");
    menu.setMnemonic(KeyEvent.VK_F);
    
    // Add menu items
    JMenuItem menuItem = new JMenuItem("New");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Save Room");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Load Room");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    menu.addSeparator();
    
    menuItem = new JMenuItem("Exit");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    // Add menu bar
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(menu);
    
    return menuBar;
  }
  
  public void actionPerformed(ActionEvent e) {
    JMenuItem source = (JMenuItem)(e.getSource());
    
    if ((source.getText()).equals("New")) {
      
      JOptionPane confirmDialog = new JOptionPane();
      int response = confirmDialog.showConfirmDialog(null, "This will clear all of your data for the current room.  Continue?",
      "Confirm New Room Request", JOptionPane.YES_NO_OPTION);
      
      if (response == JOptionPane.YES_OPTION) {
        clearAll();
      }
      
    } else if ((source.getText()).equals("Save Room")) {
      fc = new JFileChooser();
      
      int returnVal = fc.showSaveDialog(this);
      
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        
        FileManager.saveRoom(file);
      }
    } else if ((source.getText()).equals("Load Room")) {
      boolean success = false;
      fc = new JFileChooser();
      
      int returnVal = fc.showOpenDialog(this);
      
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        
        success = FileManager.loadRoom(file);
      }
      
      if (!success) {
        JOptionPane.showMessageDialog(null, "Invalid XML file.", "Error", JOptionPane.ERROR_MESSAGE);
      }
    } else if ((source.getText()).equals("Exit")) {
      System.exit(0);
    }
  }
  
  public void stateChanged(ChangeEvent e) {
    int index = tabbedPane.getSelectedIndex();
    
    if (index == SUMMARY) {
      summaryTab.clearTextBox();
      JTextArea textBox = (JTextArea)summaryTab.getTextBox();
      
      Document doc = XMLGenerator.generateRoomXML();
      
      if (doc != null) {
        XMLOutputter outputter = new XMLOutputter("   ", true);
        
        textBox.append(outputter.outputString(doc));
      } else
        textBox.append("No information has been set for the current room.");
    } else if (index == PREVIEW) {
      RoomDrawer roomDrawer = RoomDrawer.getInstance();
      roomDrawer.createPreview(previewTab.getRoomPanel());
    }
  }
  
  public void clearAll() {
    Room room = Room.getInstance();
    room.clearRoom();
    
    roomTab.clear();
    
    objectTab.clearFields();
    objectTab.clearCurrentObjectList();
    
    summaryTab.clearTextBox();
  }
  
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch(Exception e) { }
    
    // Create top-level container and add its components
    JFrame frame = new JFrame("RoCa - Room Capture Tool");
    MainGUI gui = new MainGUI();
    Component contents = gui.createComponents();
    
    frame.setJMenuBar(gui.createMenu());
    frame.getContentPane().add(contents, BorderLayout.CENTER);
    
    // Finish setting up frame and show it
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    
    frame.pack();
    frame.setVisible(true);
  }
}