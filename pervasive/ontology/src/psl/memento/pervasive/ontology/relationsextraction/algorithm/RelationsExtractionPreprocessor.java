package psl.memento.pervasive.ontology.relationsextraction.algorithm;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;

import edu.unika.aifb.kaon.api.*;

import de.fzi.wim.kaoncorpus.api.*;
import psl.memento.pervasive.ontology.*;
import psl.memento.pervasive.ontology.relationsextraction.*;

//CONVERTED FROM KAON TO PSL ONTOLOGY

/**
 * This class provides a facade around the association rules extractor class for the case when association rules
 * are extracted from text.
 */
public class RelationsExtractionPreprocessor extends AbstractOIModelLinguisticAlgorithm {
    /** The selecting concepts phase. */
    public static final int PHASE_SELECTING_CONCEPTS=1;
    /** The loading concepts phase. */
    public static final int PHASE_LOADING_CONCEPTS=2;
    /** The processing documents phase. */
    public static final int PHASE_PROCESSING_DOCUMENTS=3;
    /** The number of phases. */
    public static final int PHASE_NUMBER=3;

    /** Map of concepts (or sets of concepts) indexed by the stem of thir label. */
    protected Map m_conceptMap;

    /**
     * Constructs an instance of this class.
     *
     * @param language                          the language of texts
     * @throws IllegalArgumentException         thrown if there is an error in the arguments
     */
    public RelationsExtractionPreprocessor(String language) throws IllegalArgumentException {
        super(language);
        m_conceptMap=new HashMap();
    }

    /**
     * Provides a map interface to all the nodes indexed by the label stems for the given ontology.
     * The key in the map is the label stem, and the value is either an Ontology node or a set of Ontology nodes.
     *
     * @param ontology                          the Ontology
     */
    public void fillConceptMap(Ontology ontology) {
		m_conceptMap = new OntologyNodeMap(ontology);
        //forAllEntities(new ConceptLexicalIndexBuilder(oimodel,m_conceptMap),PHASE_SELECTING_CONCEPTS,PHASE_NUMBER);
    }
    /**
     * Returns the map of concepts.
     *
     * @return                                  the map of concepts
     */
    public Map getConceptMap() {
        return m_conceptMap;
    }
    /**
     * Processes all pairs of given ontology nodes and a conclution object.
     *
     * @param associationRulesExctractor        the extractor receiving transactions
     * @param object0                           the first object
     * @param object1                           the second object (either an ontology node or a set)
     */
    protected void processPair1(AssociationRulesExtractor associationRulesExctractor,OntologyNode object0,Object object1) {
        if (object1 instanceof Set) {
            Iterator objects=((Set)object1).iterator();
            while (objects.hasNext()) {
                OntologyNode concept=(OntologyNode)objects.next();
                associationRulesExctractor.addTransaction(object0,concept);
            }
        }
        else
            associationRulesExctractor.addTransaction(object0,(OntologyNode)object1);
    }
    /**
     * Adds all pairs of ontology nodes from two sets to the algorithm.
     *
     * @param associationRulesExctractor        the extractor receiving transactions
     * @param object0                           the first object (either a concept of a set)
     * @param object1                           the second object (either a concept of a set)
     */
    protected void processPair(AssociationRulesExtractor associationRulesExctractor,Object object0,Object object1) {
        if (object0 instanceof Set) {
            Iterator objects=((Set)object0).iterator();
            while (objects.hasNext()) {
                OntologyNode concept=(OntologyNode)objects.next();
                processPair1(associationRulesExctractor,concept,object1);
            }
        }
        else
            processPair1(associationRulesExctractor,(OntologyNode)object0,object1);
    }
    /**
     * For all texts in the corpus resolves ontology nodes from given ontology and extracts the
     * list of potential ontology node association pairs.
     *
     * @param patternBasedRulesExtractor        the pattern-based rules extractor
     * @param associationRulesExctractor        the extractor receiving transactions
     * @param corpus                            the corpus being processed
     * @throws IOException                      thrown if there is a problem with reading the documents
     * @throws InterruptedException             thrown if the algorithm is interrpted
     */
    public void addCorpusTexts(PatternBasedRulesExtractor patternBasedRulesExtractor,AssociationRulesExtractor associationRulesExctractor,Corpus corpus) throws IOException,InterruptedException {
        if (patternBasedRulesExtractor!=null)
            patternBasedRulesExtractor.initialize(m_conceptMap);
        List documents=corpus.getDocuments();
        int processedDocuments=0;
        fireProgressReport(PHASE_PROCESSING_DOCUMENTS,PHASE_NUMBER,processedDocuments,documents.size());
        Iterator iterator=documents.iterator();
        while (iterator.hasNext()) {
            Document document=(Document)iterator.next();
            String documentText=getDocumentText(document);
            if (patternBasedRulesExtractor!=null)
                patternBasedRulesExtractor.extractPatternsFromRules(documentText);
            if (associationRulesExctractor!=null) {
                String[] unstemmedWords=getUnstemmedWords(documentText);
                Object previousElement=null;
                for (int index=0;index<unstemmedWords.length;index++) {
                    String wordStem=getWordStem(unstemmedWords[index]);
                    if (!isStopWord(wordStem)) {
                        Object mappedElement=m_conceptMap.get(wordStem);
                        if (mappedElement!=null) {
                            if (previousElement==null)
                                previousElement=mappedElement;
                            else {
                                processPair(associationRulesExctractor,previousElement,mappedElement);
                                previousElement=null;
                            }
                        }
                    }
                    checkInterrupted();
                }
            }
            checkInterrupted();
            processedDocuments++;
            if ((processedDocuments % 10)==0)
                fireProgressReport(PHASE_PROCESSING_DOCUMENTS,PHASE_NUMBER,processedDocuments,documents.size());
        }
        fireProgressReport(PHASE_PROCESSING_DOCUMENTS,PHASE_NUMBER,processedDocuments,documents.size());
    }
}
