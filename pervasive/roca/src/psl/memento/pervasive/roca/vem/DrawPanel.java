/*
 * This is one of Vlad's files, up-to-date as of 12-22-02.
 *
 */

package psl.memento.pervasive.roca.vem;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.*;

/*
 * DrawPanel.java
 *
 * Created on November 7, 2002, 3:33 PM
 */

/**
 *
 * @author  Vladislav
 */
public class DrawPanel extends JPanel {
  
  VolatileImage vImg;
  LayoutDrawer drawer;
  
  public DrawPanel() {
    super();
  }
  
  public void clear() {
    getGraphics().clearRect(0, 0, getWidth(), getHeight());
  }
  
  public void doError(String message) {
    clear();
    getGraphics().drawString("Error: " + message, 10, 10);
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    setBackground(Color.LIGHT_GRAY);
    if (vImg != null)
      draw(g);
  }
  
  private void draw(Graphics g) {
    do {
      // Test if image is lost and restore it.
      GraphicsConfiguration gc =
      this.getGraphicsConfiguration();
      int valCode = vImg.validate(gc);
      // No need to check for IMAGE_RESTORED since we are
      // going to re-render the image anyway.
      if (valCode == VolatileImage.IMAGE_INCOMPATIBLE) {
        createBackBuffer();
      }
      
      // Render to the Image
      renderFrame();
      // Render image to screen.
      g.drawImage(vImg, 0, 0, this);
      // Test if content is lost
    } while(vImg.contentsLost());
  }
  
  private void createBackBuffer() {
    GraphicsConfiguration gc = getGraphicsConfiguration();
    vImg = gc.createCompatibleVolatileImage(getWidth(), getHeight());
  }
  
  private void renderFrame() {
    Graphics g = vImg.getGraphics();
    g.setColor(Color.LIGHT_GRAY);
    g.fillRect(0, 0, getWidth(), getHeight());
    drawer.doDrawing(g);
  }
  
  public void handleInput(LayoutDrawer drawer) {
    this.drawer = drawer;
    
    // do drawing
    createBackBuffer();
    draw(getGraphics());
  }
  
  public void redraw() {
    draw(getGraphics());
  }
}
