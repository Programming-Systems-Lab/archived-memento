package psl.memento.pervasive.roca.gui;

import psl.memento.pervasive.roca.room.*;
import psl.memento.pervasive.roca.util.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import java.io.*;

/**
 * SummaryTab.java
 *
 * @author Kristina Holst
 */
public class SummaryTab extends JPanel implements ActionListener {
  
  private JPanel mainPanel, buttonPanel;
  private JScrollPane textScrollPane;
  private JButton updateRoomButton;
  private JTextArea textBox;
  
  public SummaryTab() {
    updateRoomButton = new JButton("Update Room");
    updateRoomButton.addActionListener(this);
    
    
    textBox = new JTextArea("", 25, 55);
    textBox.setEditable(true);
    textBox.setLineWrap(true);
    textBox.setWrapStyleWord(true);
    
    textScrollPane = new JScrollPane(textBox);
    textScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
    
    
    buttonPanel = new JPanel();
    //buttonPanel.add(saveRoom);
    //buttonPanel.add(Box.createRigidArea(new Dimension(10, 1)));
    buttonPanel.add(updateRoomButton);
    
    
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
  
  public Component getTextBox() {
    return textBox;
  }
  
  public void clearTextBox() {
    textBox.setText("");
  }
  
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    
    if (source == updateRoomButton) {
      String text = textBox.getText();
      
      File file = new File("temp");
      FileWriter fw = null;
      BufferedWriter bw = null;
      
      try {
        fw = new FileWriter(file);
        bw = new BufferedWriter(fw);
        bw.write(text);
        
        bw.close();
        fw.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      
      Room room = Room.getInstance();
      room.clearRoom();
      
      RoomTab roomTab = RoomTab.getInstance();
      roomTab.clear();
      
      ObjectTab objectTab = ObjectTab.getInstance();
      objectTab.clearFields();
      objectTab.clearCurrentObjectList();
      
      boolean success = FileManager.loadRoom(file);
      
      if (!success) {
        JOptionPane.showMessageDialog(null, "Invalid XML file.", "Error", JOptionPane.ERROR_MESSAGE);
      }
      
      
    }
  }
}