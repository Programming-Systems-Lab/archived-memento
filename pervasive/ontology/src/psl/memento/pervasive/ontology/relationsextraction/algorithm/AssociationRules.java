package psl.memento.pervasive.ontology.relationsextraction.algorithm;

import java.util.Map;
import java.util.HashMap;

//CONVERTED FROM KAON TO PSL ONTOLOGY

/**
 * Represents the list of association rules.
 */
public class AssociationRules {
    /** The map of all the rules indexed by the key. */
    protected Map m_rules;

    /**
     * Creates an instance of this class.
     */
    public AssociationRules() {
        m_rules=new HashMap();
    }
    /**
     * Returns the association rule with given premise and conclusion. If the
     * rule doesn't exist, it is created.
     *
     * @param premise                           the premise of the rule
     * @param conclusion                        the conclusion of the rule
     */
    public AssociationRule getAssociationRule(Object premise,Object conclusion) {
        AssociationRuleKey associationRuleKey=new AssociationRuleKey(premise,conclusion);
        AssociationRule associationRule=(AssociationRule)m_rules.get(associationRuleKey);
        if (associationRule==null) {
            associationRule=new AssociationRule(associationRuleKey);
            m_rules.put(associationRuleKey,associationRule);
        }
        return associationRule;
    }
    /**
     * Returns the array of rules.
     *
     * @return                                  the array of rules
     */
    public AssociationRule[] getAssociationRules() {
        AssociationRule[] rules=new AssociationRule[m_rules.size()];
        m_rules.values().toArray(rules);
        return rules;
    }
}
