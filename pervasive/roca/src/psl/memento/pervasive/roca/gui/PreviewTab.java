package psl.memento.pervasive.roca.gui;

import psl.memento.pervasive.roca.vem.RoomViewerPanel;
import psl.memento.pervasive.roca.util.RoomDrawer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * RoomTab.java
 *
 * @author Kristina Holst
 */
public class PreviewTab extends JPanel {
    private JPanel mainPanel;
    private RoomViewerPanel roomPanel;
    private RoomDrawer roomDrawer = RoomDrawer.getInstance();
    
    /** Creates a new instance of PreviewTab */
    public PreviewTab() {
        roomPanel = new RoomViewerPanel();
        roomPanel.setPreferredSize(new Dimension(500, 500));
        
        roomPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                roomDrawer.newObjectPlaced(evt, roomPanel);
            }
        });
        
        roomPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                roomDrawer.newObjectMoving(evt, roomPanel);
            }
        });
        
    /* To get everything to stay aligned when window is maximized, we need to
     * add all the components to an intermediate panel. */
        JPanel dummyPanel = new JPanel();
        dummyPanel.setLayout(new BorderLayout());
        dummyPanel.add(roomPanel, BorderLayout.CENTER);
        
        mainPanel = new JPanel();
        mainPanel.add(dummyPanel);
    }
    
    public Component getMainPanel() {
        return mainPanel;
    }
    
    public RoomViewerPanel getRoomPanel() {
        return roomPanel;
    }
}
