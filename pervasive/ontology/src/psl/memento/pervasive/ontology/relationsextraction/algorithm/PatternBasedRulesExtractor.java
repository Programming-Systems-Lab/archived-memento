package psl.memento.pervasive.ontology.relationsextraction.algorithm;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import psl.memento.pervasive.ontology.*;
import psl.memento.pervasive.ontology.relationsextraction.*;

//CONVERTED FROM KAON TO PSL ONTOLOGY

/**
 * Implements a rule extraction algorithm based on linguistic patterns.
 */
public class PatternBasedRulesExtractor extends AbstractLinguisticAlgorithm {
    /** The map of all loaded lists of patterns indexed by the language code. */
    protected static final Map s_patterns=new HashMap();

    /** The map of concepts indexed by their stem. */
    protected Map m_conceptMap;
    /** The rules being extracted. */
    protected AssociationRules m_associationRules;
    /** The list of patterns. */
    protected List m_patterns;

    /**
     * Constructs an instance of this class.
     *
     * @param language                          the language of texts
     * @param associationRules                  the list of rules being filled in
     * @throws IllegalArgumentException         thrown if the language is not supported
     */
    public PatternBasedRulesExtractor(String language,AssociationRules associationRules) throws IllegalArgumentException {
        super(language);
        m_associationRules=associationRules;
        m_patterns=(List)getPatterns(language);
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
     * Applies all known patterns and extracts obtained rules.
     *
     * @param text                              the text
     */
    public void extractPatternsFromRules(String text) {
        Iterator sentences=new SentenceIterator(text);
        while (sentences.hasNext()) {
            String sentence=(String)sentences.next();
            for (int i=0;i<m_patterns.size();i++) {
                PatternInfo patternInfo=(PatternInfo)m_patterns.get(i);
                patternInfo.matchPattern(sentence,this);
            }
        }
    }
    /**
     * Returns the nodes for given term.
     *
     * @param unstemmedTerm                     the term
     * @return                                  the ontology node (or <code>null</code> if there is no such node)
     */
    protected Set getOntologyNodesForTerm(String unstemmedTerm) {
        String wordStem=getWordStem(unstemmedTerm);
        Object object=m_conceptMap.get(wordStem);
        if (object instanceof OntologyNode) {
            Set set=new HashSet();
            set.add(object);
            return set;
        }
        else if (object instanceof Set)
            return (Set)object;
        else
            return null;
    }
    /**
     * Creates association rules between supplied terms.
     *
     * @param unstemmedPremiseTerm              the unstemmed premise term
     * @param unstemmedConclusionTerm           the unstemmed conclusion term
     * @param patternInfo                       the pattern to apply
     */
    protected void createRules(String unstemmedPremiseTerm,String unstemmedConclusionTerm,PatternInfo patternInfo) {
        Set premises=getOntologyNodesForTerm(unstemmedPremiseTerm);
        Set conclusions=getOntologyNodesForTerm(unstemmedConclusionTerm);
        if (premises!=null && conclusions!=null) {
            Iterator premisesIterator=premises.iterator();
            while (premisesIterator.hasNext()) {
                OntologyNode premise=(OntologyNode)premisesIterator.next();
                Iterator conclusionsIterator=conclusions.iterator();
                while (conclusionsIterator.hasNext()) {
                    OntologyNode conclusion=(OntologyNode)conclusionsIterator.next();
                    m_associationRules.getAssociationRule(premise,conclusion).addPatternName(patternInfo.getPatternName());
                }
            }
        }
    }
    /**
     * Returns the list of patterns for given language.
     *
     * @param language                          the language for which the patterns are loaded
     * @return                                  the list of patterns for given language
     * @throws IllegalArgumentException         thrown if the pattenrs for given language cannot be loaded
     */
    protected static List getPatterns(String language) throws IllegalArgumentException {
        List patterns=(List)s_patterns.get(language);
        if (patterns==null) {
            patterns=new ArrayList();
            URL url=PatternBasedRulesExtractor.class.getResource("res/patterns_"+language+".txt");
            if (url==null)
                throw new IllegalArgumentException("The list of patterns list for language '"+language+"' is missing.");
            BufferedReader in=null;
            try {
                in=new BufferedReader(new InputStreamReader(url.openStream()));
                String line=in.readLine();
                while (line!=null) {
                    if (line.length()!=0) {
                        int nameEnd=line.indexOf(':');
                        if (nameEnd==-1)
                            throw new IllegalArgumentException("Missing pattern name for line '"+line+"'");
                        String patternName=line.substring(0,nameEnd).trim();
                        int typeEnd=line.indexOf(':',nameEnd+1);
                        if (typeEnd==-1)
                            throw new IllegalArgumentException("Missing pattern type for line '"+line+"'");
                        String patternType=line.substring(nameEnd+1,typeEnd).trim();
                        String patternText=line.substring(typeEnd+1).trim();
                        PatternInfo patternInfo=createPatternInfo(patternType);
                        try {
                            patternInfo.initialize(patternName,patternText);
                        }
                        catch (PatternSyntaxException e) {
                            IllegalArgumentException error=new IllegalArgumentException("Invalid pattern '"+patternText+"': "+e.getMessage());
                            error.initCause(e);
                            throw error;
                        }
                        patterns.add(patternInfo);
                    }
                    line=in.readLine();
                }
            }
            catch (IOException e) {
                IllegalArgumentException exception=new IllegalArgumentException("Error reading patterns list for language '"+language+"': "+e.getMessage());
                exception.initCause(e);
                throw exception;
            }
            if (in!=null)
                try {
                    in.close();
                }
                catch (IOException ignored) {
                }
            if (!s_developmentMode)
                s_patterns.put(language,patterns);
        }
        return patterns;
    }
    /**
     * Creates the pattern of given type.
     *
     * @param patternType                       the type for the pattern
     * @return                                  the new pattern
     * @throws IllegalArgumentException         thrown if the pattern type is invalid
     */
    protected static PatternInfo createPatternInfo(String patternType) throws IllegalArgumentException {
        if ("pc".equalsIgnoreCase(patternType))
            return new PremiseConclusionPattern();
        else if ("cp".equalsIgnoreCase(patternType))
            return new ConclusionPremisePattern();
        else
            throw new IllegalArgumentException("Illegal pattern type '"+patternType+"'.");
    }

    /**
     * The class that stores the pattern information.
     */
    protected static abstract class PatternInfo {
        protected String m_patternName;
        protected Pattern m_pattern;

        public PatternInfo()  {
        }
        public void initialize(String patternName,String patternText) throws PatternSyntaxException {
            m_patternName=patternName;
            m_pattern=Pattern.compile(patternText);
        }
        public Pattern getPattern() {
            return m_pattern;
        }
        public String getPatternName() {
            return m_patternName;
        }
        public abstract void matchPattern(String text,PatternBasedRulesExtractor extractor);
    }

    /**
     * A pattern matching conclusion and then premise.
     */
    protected static class ConclusionPremisePattern extends PatternInfo {
        public void matchPattern(String text,PatternBasedRulesExtractor extractor) {
            Matcher matcher=getPattern().matcher(text);
            while (matcher.find()) {
                String conclusionTerm=matcher.group(1);
                String premiseTerm=matcher.group(2);
                extractor.createRules(premiseTerm,conclusionTerm,this);
            }
        }
    }

    /**
     * A pattern matching the premise and then the conclusion.
     */
    protected static class PremiseConclusionPattern extends PatternInfo {
        public void matchPattern(String text,PatternBasedRulesExtractor extractor) {
            Matcher matcher=getPattern().matcher(text);
            while (matcher.find()) {
                String premiseTerm=matcher.group(1);
                String conclusionTerm=matcher.group(2);
                extractor.createRules(premiseTerm,conclusionTerm,this);
            }
        }
    }
}
