package psl.memento.pervasive.ontology.relationsextraction.algorithm;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

//CONVERTED FROM KAON TO PSL ONTOLOGY

/**
 * Represents one association derived by the association rules algorithm.
 */
public class AssociationRule {
    /** The key of this association rule. */
    protected AssociationRuleKey m_associationRuleKey;
    /** The faction representing what fraction of transactions support this rule. */
    protected double m_support;
    /** Confidence factor of this rule. */
    protected double m_confidence;
    /** How many transactions support this rule at all. */
    protected int m_absoluteFrequency;
    /** The list of pattern names that created this pattern. */
    protected List m_patternNames;

    public AssociationRule(AssociationRuleKey associationRuleKey) {
        m_associationRuleKey=associationRuleKey;
        m_patternNames=null;
    }
    public Object getPremise() {
        return m_associationRuleKey.getPremise();
    }
    public Object getConclusion() {
        return m_associationRuleKey.getConclusion();
    }
    public double getSupport() {
       return m_support;
    }
    public void setSupport(double support) {
       m_support=support;
    }
    public double getConfidence() {
       return m_confidence;
    }
    public void setConfidence(double confidence) {
       m_confidence=confidence;
    }
    public int getAbsoluteFrequency() {
       return m_absoluteFrequency;
    }
    public void setAbsoluteFrequency(int absoluteFrequency) {
       m_absoluteFrequency=absoluteFrequency;
    }
    public void addPatternName(String patternName) {
        if (m_patternNames==null)
            m_patternNames=new LinkedList();
        m_patternNames.add(patternName);
    }
    public List getPatternNames() {
        if (m_patternNames==null)
            return Collections.EMPTY_LIST;
        else
            return m_patternNames;
    }
}
