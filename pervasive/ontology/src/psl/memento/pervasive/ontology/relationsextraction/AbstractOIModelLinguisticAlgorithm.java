package psl.memento.pervasive.ontology.relationsextraction;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import edu.unika.aifb.kaon.api.*;
import edu.unika.aifb.kaon.api.vocabulary.*;
import edu.unika.aifb.kaon.api.oimodel.*;

/**
 * Abstract linguistinc algorithm containing some methods that are commonly useful for manipulating
 * OI-models.
 */
public abstract class AbstractOIModelLinguisticAlgorithm extends AbstractLinguisticAlgorithm {

    /**
     * Constructs an instance of this class.
     *
     * @param language                          the language of texts
     * @throws IllegalArgumentException         thrown if there is an error in the arguments
     */
    public AbstractOIModelLinguisticAlgorithm(String language) throws IllegalArgumentException {
        super(language);
    }
    /**
     * Takes elements of a set, cuts them down into smaller chunks and then calls the consumer of the set to receive them.
     * This is typically used for building an index over an ontology, where it is necessary to load the lexical information
     * for all concepts. In this case it is better to load the information in chunks of 200.
     *
     * @param entityProcessor                   receives each entity
     * @param firstPhaseNumber                  the index of the first phase
     * @param totalNumberOfPhases               the total number of phases
     * @throws KAONException                    thrown if there is a problem with accessing the ontology
     * @throws InterruptedException             thrown the algorithm has been interrupted
     */
    protected void forAllEntities(EntityProcessor entityProcessor,int firstPhaseNumber,int totalNumberOfPhases) throws KAONException,InterruptedException {
        fireProgressReport(firstPhaseNumber,totalNumberOfPhases,0,1);
        Set entities=entityProcessor.getAllEntities();
        int processedEntities=0;
        fireProgressReport(firstPhaseNumber+1,totalNumberOfPhases,processedEntities,entities.size());
        Iterator iterator=entities.iterator();
        Set subSet=new HashSet();
        while (iterator.hasNext()) {
            Entity entity=(Entity)iterator.next();
            subSet.add(entity);
            if (subSet.size()==200) {
                processSubSet(subSet,entityProcessor);
                processedEntities+=subSet.size();
                subSet.clear();
                fireProgressReport(firstPhaseNumber+1,totalNumberOfPhases,processedEntities,entities.size());
            }
            checkInterrupted();
        }
        if (!subSet.isEmpty()) {
            processSubSet(subSet,entityProcessor);
            processedEntities+=subSet.size();
            fireProgressReport(firstPhaseNumber+1,totalNumberOfPhases,processedEntities,entities.size());
        }
    }
    /**
     * Processes the subset of the total entity set.
     *
     * @param subSet                    the subset of entities
     * @param entityProcessor           the entity processor
     * @throws KAONException            thrown in case of error
     * @throws InterruptedException     thrown if the thread of the algorithm has been interrupted
     */
    protected void processSubSet(Set subSet,EntityProcessor entityProcessor) throws KAONException,InterruptedException {
        entityProcessor.processSubSet(subSet);
        Iterator iterator=subSet.iterator();
        while (iterator.hasNext()) {
            Entity entity=(Entity)iterator.next();
            entityProcessor.processEntity(entity);
            checkInterrupted();
        }
    }

    /**
     * The predicate executed for all entities.
     */
    protected interface EntityProcessor {
        Set getAllEntities() throws KAONException;
        void processSubSet(Set subSet) throws KAONException;
        void processEntity(Entity entity) throws KAONException;
    }

    /**
     * The entity processor that builds an index over entities using the lexical layer
     */
    protected abstract class LexicalIndexBuilder implements EntityProcessor {
        protected OIModel m_oimodel;
        protected Map m_index;
        protected String m_languageURI;

        public LexicalIndexBuilder(OIModel oimodel,Map index) {
            m_oimodel=oimodel;
            m_index=index;
            m_languageURI=KAONVocabularyAdaptor.INSTANCE.getLanguageURI(getLanguage());
        }
        public void addStemmedLabel(Entity entity) throws KAONException {
            String label=entity.getLabel(m_languageURI);
            if (label!=null) {
                String labelStem=getWordStem(label);
                addToIndex(labelStem,entity);
            }
        }
        public void addStemmedSynonyms(Entity entity) throws KAONException {
            Iterator synonyms=entity.getLexicalEntries(KAONVocabularyAdaptor.INSTANCE.getSynonym()).iterator();
            while (synonyms.hasNext()) {
                LexicalEntry synonym=(LexicalEntry)synonyms.next();
                if (m_languageURI.equals(synonym.getInLanguage())) {
                    String value=synonym.getValue();
                    if (value!=null) {
                        String valueStem=getWordStem(value);
                        addToIndex(valueStem,entity);
                    }
                }
            }
        }
        public void addStem(Entity entity) throws KAONException {
            String stem=entity.getLexicalAttribute(KAONVocabularyAdaptor.INSTANCE.getStem(),m_languageURI,KAONVocabularyAdaptor.INSTANCE.getValue());
            if (stem!=null)
                addToIndex(stem,entity);
        }
        public void addToIndex(String word,Entity entity) {
            Object object=m_index.get(word);
            if (object instanceof Set)
                ((Set)object).add(entity);
            else if (object!=null) {
                Set set=new HashSet(5);
                set.add(object);
                set.add(entity);
                m_index.put(word,set);
            }
            else
                m_index.put(word,entity);
        }
        public void processSubSet(Set subSet) throws KAONException {
            m_oimodel.loadObjects(subSet,OIModel.LOAD_LEXICON);
        }
        public void processEntity(Entity entity) throws KAONException {
            addStemmedLabel(entity);
            addStemmedSynonyms(entity);
            addStem(entity);
        }
    }

    /**
     * The entity processor that builds a lexicon over the concepts in the ontology.
     */
    protected class ConceptLexicalIndexBuilder extends LexicalIndexBuilder implements EntityProcessor {

        public ConceptLexicalIndexBuilder(OIModel oimodel,Map index) {
            super(oimodel,index);
        }
        public Set getAllEntities() throws KAONException {
            return m_oimodel.getConcepts();
        }
    }
}
