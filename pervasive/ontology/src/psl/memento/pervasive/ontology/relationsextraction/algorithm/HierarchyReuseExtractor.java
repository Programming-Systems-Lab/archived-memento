package psl.memento.pervasive.ontology.relationsextraction.algorithm;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import edu.unika.aifb.kaon.api.*;
import edu.unika.aifb.kaon.api.oimodel.*;
import edu.unika.aifb.kaon.api.vocabulary.*;

import psl.memento.pervasive.ontology.relationsextraction.*;

/**
 * Implements an algorithm that reuses the hierarchy of some other ontology. For any two concepts from
 * the source ontology, the algorithm matches the labels of the concepts from the source ontology with
 * the labels from the target ontology. If these concepts are in a hierarchy relationship, then a rule
 * is generated for the source concepts as well.
 */
public class HierarchyReuseExtractor extends AbstractOIModelLinguisticAlgorithm {
    /** The phase of selecting concepts from the control ontology. */
    public static final int PHASE_SELECT_CONTROL_ONTOLOGY_CONCEPTS=1;
    /** The phase of loading concepts from the control ontology. */
    public static final int PHASE_LOAD_CONTROL_ONTOLOGY_CONCEPTS=2;
    /** The phase of reusing the hierarchy. */
    public static final int PHASE_REUSING_HIERARCHY=3;
    /** The number of phases. */
    public static final int PHASE_NUMBER=3;

    /** The map of concepts of the source ontology indexed by their stem. */
    protected Map m_conceptMap;
    /** Concepts of the control ontology indexed by their stem values. */
    protected Map m_controlOntologyIndex;
    /** The rules being extracted. */
    protected AssociationRules m_associationRules;
    /** The language URI. */
    protected String m_languageURI;

    /**
     * Constructs an instance of this class.
     *
     * @param language                          the language of texts
     * @param associationRules                  the list of rules being filled in
     * @throws IllegalArgumentException         thrown if the language is not supported
     */
    public HierarchyReuseExtractor(String language,AssociationRules associationRules) throws IllegalArgumentException {
        super(language);
        m_associationRules=associationRules;
        m_controlOntologyIndex=new HashMap();
        m_languageURI=KAONVocabularyAdaptor.INSTANCE.getLanguageURI(language);
    }
    /**
     * Initializes this algorithm.
     *
     * @param conceptMap                        the map of concepts indexed by the stem
     */
    public void initialize(Map conceptMap) {
        m_conceptMap=conceptMap;
    }
    /**
     * Applies the algorithm to the given target ontology.
     *
     * @param oimodel                           the target oimodel
     * @throws KAONException                    thrown if there is an error in accessing the ontology
     * @throws InterruptedException             thrown if the algorithm is interrupted
     */
    public void reuseHierarchy(OIModel oimodel) throws KAONException,InterruptedException {
        forAllEntities(new ConceptLexicalIndexBuilder(oimodel,m_controlOntologyIndex),PHASE_SELECT_CONTROL_ONTOLOGY_CONCEPTS,PHASE_NUMBER);
        processSourceConcepts();
    }
    /**
     * Processes all concepts from the source OI-model and matches them against the
     * target OI-model.
     *
     * @throws KAONException                    thrown if there is an error in accessing the ontology
     * @throws InterruptedException             thrown if the algorithm is interrupted
     */
    protected void processSourceConcepts() throws KAONException,InterruptedException {
        int processedStems=0;
        Iterator stems=m_conceptMap.keySet().iterator();
        while (stems.hasNext()) {
            String stem=(String)stems.next();
            Object object=m_conceptMap.get(stem);
            if (object instanceof Concept)
                processSubConcept((Concept)object,stem);
            else if (object instanceof Set) {
                Iterator iterator=((Set)object).iterator();
                while (iterator.hasNext()) {
                    Concept subConcept=(Concept)iterator.next();
                    processSubConcept(subConcept,stem);
                }
            }
            processedStems++;
            checkInterrupted();
            fireProgressReport(PHASE_REUSING_HIERARCHY,PHASE_NUMBER,processedStems,m_conceptMap.size());
        }
    }
    /**
     * Processes one concept from the source OI-model.
     *
     * @param subConcept                        the subconcept being processed
     * @param stem                              the stem of the subconcept
     * @throws KAONException                    thrown if there is an error in accessing the ontology
     * @throws InterruptedException             thrown if the algorithm is interrupted
     */
    protected void processSubConcept(Concept subConcept,String stem) throws KAONException,InterruptedException {
        Object object=m_controlOntologyIndex.get(stem);
        if (object instanceof Concept)
            processConceptPair(subConcept,(Concept)object);
        else if (object instanceof Set) {
            Iterator iterator=((Set)object).iterator();
            while (iterator.hasNext()) {
                Concept matchingControlSubConcept=(Concept)iterator.next();
                processConceptPair(subConcept,matchingControlSubConcept);
            }
        }
    }
    /**
     * Processes one sub- super-concept pair.
     *
     * @param subConcept                        the subconcept being processed
     * @param matchingControlSubConcept         the matching subconcept from the control ontology
     * @throws KAONException                    thrown if there is an error in accessing the ontology
     * @throws InterruptedException             thrown if the algorithm is interrupted
     */
    protected void processConceptPair(Concept subConcept,Concept matchingControlSubConcept) throws KAONException,InterruptedException {
        // do a breadth first traversal of the parent tree
        List queue=new LinkedList();
        queue.addAll(matchingControlSubConcept.getSuperConcepts());
        while (!queue.isEmpty()) {
            Concept controlSuperConcept=(Concept)queue.remove(0);
            if (processLexicalEntries(subConcept,controlSuperConcept.getLexicalEntries(KAONVocabularyAdaptor.INSTANCE.getKAONLabel())))
                break;
            if (processLexicalEntries(subConcept,controlSuperConcept.getLexicalEntries(KAONVocabularyAdaptor.INSTANCE.getStem())))
                break;
            if (processLexicalEntries(subConcept,controlSuperConcept.getLexicalEntries(KAONVocabularyAdaptor.INSTANCE.getSynonym())))
                break;
            queue.addAll(controlSuperConcept.getSuperConcepts());
            checkInterrupted();
        }
    }
    /**
     * Processes the set of lexical entries.
     *
     * @param subConcept                        the subconcept being processed
     * @param lexicalEntries                    the set of lexical entries
     * @return                                  <code>true</code> if the stem was processed
     * @throws KAONException                    thrown if there is an error in accessing the ontology
     * @throws InterruptedException             thrown if the algorithm is interrupted
     */
    protected boolean processLexicalEntries(Concept subConcept,Set lexicalEntries) throws KAONException,InterruptedException {
        Iterator iterator=lexicalEntries.iterator();
        while (iterator.hasNext()) {
            LexicalEntry lexicalEntry=(LexicalEntry)iterator.next();
            if (m_languageURI.equals(lexicalEntry.getInLanguage())) {
                String value=lexicalEntry.getValue();
                String valueStem=getWordStem(value);
                if (processSuperConceptWord(subConcept,valueStem))
                    return true;
            }
        }
        return false;
    }
    /**
     * Processes one superconcept label.
     *
     * @param subConcept                        the subconcept being processed
     * @param superConceptWord                  one word of the superconcept in the original ontology
     * @return                                  <code>true</code> if the word was successfully matched
     * @throws KAONException                    thrown if there is an error
     */
    protected boolean processSuperConceptWord(Concept subConcept,String superConceptWord) throws KAONException {
        Object superObject=m_conceptMap.get(superConceptWord);
        if (superObject instanceof Concept)
            return createRule(subConcept,(Concept)superObject);
        else if (superObject instanceof Set) {
            boolean result=false;
            Iterator superConcepts=((Set)superObject).iterator();
            while (superConcepts.hasNext()) {
                Concept superConcept=(Concept)superConcepts.next();
                if (createRule(subConcept,superConcept))
                    result=true;
            }
            return result;
        }
        else
            return false;
    }
    /**
     * Creates a rule between two concepts if this is possibile.
     *
     * @param subConcept                        the subconcept
     * @param superConcept                      the superconcept
     * @return                                  <code>true</code> if the rule was created
     * @throws KAONException                    thrown if there is an error
     */
    protected boolean createRule(Concept subConcept,Concept superConcept) throws KAONException {
        if (!subConcept.equals(superConcept) && !subConcept.isSubConceptOf(superConcept)) {
            m_associationRules.getAssociationRule(subConcept,superConcept);
            return true;
        }
        else
            return false;
    }
}
