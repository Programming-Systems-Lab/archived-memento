/*
 * RoomViewerPanel.java
 *
 * Created on November 27, 2002, 12:18 AM
 */

package psl.memento.server.vem.ui;

import psl.memento.server.vem.*;
import psl.memento.server.vem.layout.*;

/**
 *  This is a utility class for programs wanting to integrate the room viewer
 *  module into a Swing program.
 *
 * @author  vlad
 */
public class RoomViewerPanel extends DrawPanel {
    
    public static final int DEFAULT_SPACING_VALUE = 2;
    
    private Layout layout;
    
    public RoomViewerPanel() {
        super();
    }
    
    /** 
     *  Initializes this instance of RoomViewerPanel 
     *
     *  @param  dr  the DataReader object which will describe the room
     */
    public void setRoomInformation(DataReader dr) {
       layout = new SimpleLayout();
       layout.doLayout(dr); 
       this.drawer = new LayoutDrawer((SimpleLayout)layout);
    }
    
    /**
     *  Causes this panel to update its contents based on changes in the
     *  room information
     */    
    public void updateRoomView() {
        if (this.drawer == null) return;
        
        layout.calculateLayout();        
        
        handleInput(drawer);
    }
}
