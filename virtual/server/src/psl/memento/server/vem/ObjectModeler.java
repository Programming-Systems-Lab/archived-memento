/*
 * ObjectModeler.java
 *
 * Created on February 8, 2003, 12:17 PM
 */

package psl.memento.server.vem;

import psl.memento.server.frax.vocabulary.Vocab;

/**
 *
 * @author  vlad
 */

public interface ObjectModeler {
    public RoomObject createRoomObjectView(Object iVocab, String iName);
    public Vocab[] getVocabsToSearch();
}
