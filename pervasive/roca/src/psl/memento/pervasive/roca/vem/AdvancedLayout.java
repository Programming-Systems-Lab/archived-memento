/*
 * This is one of Vlad's files, up-to-date as of 12-22-02.
 *
 */

/*
 * AdvancedLayout.java
 *
 * Created on November 11, 2002, 11:09 PM
 */

package psl.memento.pervasive.roca.vem;

import java.util.Collections;

/**
 *
 * @author  Vladislav
 */
public class AdvancedLayout extends Layout {
    
    public String getLayoutSchemeName()
    {
	return "Advanced Layout";
    }    
    
    public void calculateLayout() {
	
	// sort object list by type
	Collections.sort(dr.mObjs);
	
	// do simple layout
	super.calculateLayout();
    }
}
