package psl.memento.server.vem;

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
