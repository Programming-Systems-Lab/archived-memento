package psl.memento.server.vem.layout;

import psl.memento.server.vem.DataReader;

/*
 * AbstractLayout.java
 *
 * Created on November 11, 2002, 3:16 AM
 */

/**
 *
 * @author  Vladislav
 */
public interface Layout {
    public void doLayout(DataReader dr);
    public void calculateLayout();
    public String getLayoutSchemeName();
}
