/*
 * This is one of Vlad's files, up-to-date as of 12-22-02.
 *
 */

/*
 * LayoutClassManager.java
 *
 * Created on November 12, 2002, 12:03 AM
 */

package psl.memento.pervasive.roca.vem;

import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author  Vladislav
 */
public class LayoutClassManager implements java.awt.event.ActionListener {
    
    private ButtonGroup group;
    private RoomViewer rv;
    private String defaultClass;
    private Layout currentInstance;
    
    /** Creates a new instance of LayoutClassManager */
    public LayoutClassManager(RoomViewer rv) {
	this.group = new ButtonGroup();
	this.rv = rv;
	this.currentInstance = null;
    }
    
    public void addOption(AbstractButton b, String className) {
	group.add(b);
	className = getClass().getPackage().getName() + "." + className;
	
	b.setActionCommand(className);
	b.addActionListener(this);
	
	if (defaultClass == null) {
	    defaultClass = className;
	}
    }
    
    public Layout getLayoutInstance() {
	if (currentInstance == null) {
	    if (!loadLayoutClass(defaultClass))
		return null;
	}
	
	return currentInstance;
    }
    
    private boolean loadLayoutClass(String className)
    {
	try {
	    currentInstance = (Layout)Class.forName(className).newInstance();
	} catch (Exception e) { 
	    System.out.println("getLayoutInstance(): " + e);
	    return false;
	}	

	return true;
    }
    
    public void actionPerformed(ActionEvent e) {
	loadLayoutClass(e.getActionCommand());
	if (rv != null) {
	    rv.updateDrawPanel();
	}
    }    
    
}
