/*
 * RoomViewerPanel.java
 *
 * Created on November 27, 2002, 12:18 AM
 */

package psl.memento.server.vem;

/**
 *  This is a utility class for programs wanting to integrate the room viewer
 *  module into a Swing program.
 *
 * @author  vlad
 */
public class RoomViewerPanel extends DrawPanel implements LayoutParameters {
    
    public static final int DEFAULT_SPACING_VALUE = 2;
    
    private boolean showFreeSpace;
    private int spacingValue;
    
    public RoomViewerPanel() {
        super();
        
        showFreeSpace = true;
        spacingValue = DEFAULT_SPACING_VALUE;
    }
    
    /** 
     *  Initializes this instance of RoomViewerPanel 
     *
     *  @param  dr  the DataReader object which will describe the room
     */
    public void setRoomInformation(DataReader dr) {
       Layout layout = new Layout();
       layout.init(dr, this); 
       this.drawer = (LayoutDrawer) layout;
    }
    
    /**
     *  Causes this panel to update its contents based on changes in the
     *  room information
     */    
    public void updateRoomView() {
        if (this.drawer == null) return;
        ((Layout)drawer).setParameters();
        ((Layout)drawer).calculateLayout();        
        //clear();
        handleInput(drawer);
    }
    
    public int getSpacingValue() {
        return this.spacingValue;
    }
    
    public boolean highlightFreeSpace() {
        return showFreeSpace;
    }
    
    public void setSpacingValue(int val) {
        this.spacingValue = val;
    }
    
    public void setHighlightFeeSpace(boolean val) {
        this.showFreeSpace = val;
    }
    
    
}
