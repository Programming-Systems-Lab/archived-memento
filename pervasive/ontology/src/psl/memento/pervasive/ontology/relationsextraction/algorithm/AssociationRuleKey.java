package psl.memento.pervasive.ontology.relationsextraction.algorithm;

//CONVERTED FROM KAON TO PSL ONTOLOGY

/**
 * Represents the key of the association rule.
 */
public class AssociationRuleKey {
    /** The object that signals the presence of another object. */
    protected Object m_premise;
    /** The object whose presence is correlated with the premise object. */
    protected Object m_conclusion;
    /** The hash-code of this object. */
    protected int m_hashCode;

    public AssociationRuleKey(Object premise,Object conclusion) {
        m_premise=premise;
        m_conclusion=conclusion;
        m_hashCode=m_premise.hashCode()*7+m_conclusion.hashCode();
    }
    public Object getPremise() {
        return m_premise;
    }
    public Object getConclusion() {
        return m_conclusion;
    }
    public boolean equals(Object that) {
        if (this==that)
            return true;
        if (!(that instanceof AssociationRuleKey))
            return false;
        AssociationRuleKey associationRuleKey=(AssociationRuleKey)that;
        return m_premise.equals(associationRuleKey.m_premise) && m_conclusion.equals(associationRuleKey.m_conclusion);
    }
    public int hashCode() {
        return m_hashCode;
    }
}
