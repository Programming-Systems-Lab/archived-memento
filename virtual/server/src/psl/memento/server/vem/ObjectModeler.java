/*
 * ObjectModeler.java
 *
 * Created on February 8, 2003, 12:17 PM
 */

package psl.memento.server.vem;

/**
 *
 * @author  vlad
 */

public interface ObjectModeler {
    
    public RoomObject createRoomObjectView(Object iVocab, String iName);
    public Object[] getVocabsToSearch();
}
