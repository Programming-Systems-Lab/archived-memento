/*
 * ObjectModeler.java
 *
 * Created on February 8, 2003, 12:17 PM
 */

package psl.memento.server.vem.modeler;

import com.hp.hpl.mesa.rdf.jena.model.Property;

import psl.memento.server.frax.vocabulary.Vocab;
import psl.memento.server.vem.RoomObject;

/**
 *
 * @author  vlad
 */

public interface ObjectModeler {
    public RoomObject createRoomObjectView(Property iVocab, String iName);
    public Vocab[] getVocabsToSearch();
}
