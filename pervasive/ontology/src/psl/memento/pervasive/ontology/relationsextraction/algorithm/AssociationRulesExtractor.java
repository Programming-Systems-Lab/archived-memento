package psl.memento.pervasive.ontology.relationsextraction.algorithm;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Collections;

import psl.memento.pervasive.ontology.relationsextraction.AbstractAlgorithm;

//CONVERTED FROM KAON TO PSL ONTOLOGY

/**
 * This class is responsible for starting the overall association rule extraction process.
 */
public class AssociationRulesExtractor extends AbstractAlgorithm {
    /** The phase of extracting frequent objects. */
    public static final int PHASE_EXTRACT_FREQUENT_OBJECTS=1;
    /** The phase of generation of candidate pairs. */
    public static final int PHASE_GENERATE_CANDIDATES=2;
    /** The phase of rule generation. */
    public static final int PHASE_GENERATE_RULES=3;
    /** The number of phases. */
    public static final int PHASE_NUMBER=3;

    /** List of transactions - the key and the value is the same object. */
    protected Map m_transactions;
    /** The total number of transactions. */
    protected int m_transactionTotal;
    /** The minimum support of the algorithm. */
    protected double m_minimumSupport;
    /** The minimum confidence of the algorithm. */
    protected double m_minimumConfidence;
    /** The association rules being extracted. */
    protected AssociationRules m_associationRules;

    /**
     * Creates an instance of this class.
     *
     * @param associationRules              the association rules
     */
    public AssociationRulesExtractor(AssociationRules associationRules) {
        m_associationRules=associationRules;
        m_transactions=new HashMap();
        m_transactionTotal=0;
        m_minimumSupport=0.0;
        m_minimumConfidence=0.0;
    }
    /**
     * Returns the current minimum support.
     *
     * @return                              the current minimum support
     */
    public double getMinimumSupport() {
        return m_minimumSupport;
    }
    /**
     * Sets the desired minimum support.
     *
     * @param minimumSupport                desired minimum support
     */
    public void setMinimumSupport(double minimumSupport) {
        m_minimumSupport=minimumSupport;
    }
    /**
     * Returns the current minimum confidence.
     *
     * @return                              the current minimum confidence
     */
    public double getMinimumConfidence() {
        return m_minimumConfidence;
    }
    /**
     * Sets the desired minimum confidence.
     *
     * @param minimumConfidence             desired minimum confidence
     */
    public void setMinimumConfidence(double minimumConfidence) {
        m_minimumConfidence=minimumConfidence;
    }
    /**
     * Adds a transaction to the database.
     *
     * @param object0                       the first object of the transaction
     * @param object1                       the second object of the transaction
     */
    public void addTransaction(Object object0,Object object1) {
        if (!object0.equals(object1)) {
            Transaction transactionKey=new Transaction(object0,object1);
            Transaction existingTransaction=(Transaction)m_transactions.get(transactionKey);
            if (existingTransaction==null && object0.hashCode()==object1.hashCode()) {
                transactionKey=new Transaction(object1,object0);
                existingTransaction=(Transaction)m_transactions.get(transactionKey);
            }
            if (existingTransaction==null) {
                existingTransaction=transactionKey;
                m_transactions.put(existingTransaction,existingTransaction);
            }
            existingTransaction.m_occurs++;
            m_transactionTotal++;
        }
    }
    /**
     * Returns an existing transaction for given objects.
     *
     * @return                              existing transaction or <code>null</code>
     */
    protected Transaction getTransaction(Object object0,Object object1) {
        Transaction transactionKey=new Transaction(object0,object1);
        Transaction existingTransaction=(Transaction)m_transactions.get(transactionKey);
        if (existingTransaction==null && object0.hashCode()==object1.hashCode()) {
            transactionKey=new Transaction(object1,object0);
            existingTransaction=(Transaction)m_transactions.get(transactionKey);
        }
        return existingTransaction;
    }
    /**
     * Extracts the association rules from the database.
     *
     * @throws InterruptedException         thrown if the algorithm has been interrupted
     */
    public void computeAssociationRules() throws InterruptedException {
        fireAlgorithmStarted(PHASE_NUMBER);
        try {
            Map singleObjectFrequency=computeSingleObjectFrequency();
            List frequentObjects=computeFrequentObjects(singleObjectFrequency);
            List candidateTransactions=generateCandidateTransactions(frequentObjects);
            generateAssociationRules(candidateTransactions,singleObjectFrequency);
        }
        finally {
            fireAlgorithmFinished();
        }
    }
    /**
     * Increments the index in the frequency map for given object.
     *
     * @param object                        object for which the index is incremented
     * @param map                           the map of IntegerWrapper objects indexed by the objects
     * @param amount                        by how much is the frequency incremented
     * @throws InterruptedException         thrown if the algorithm has been interrupted
     */
    protected void addFrequency(Object object,Map map,int amount) {
        IntegerWrapper frequency=(IntegerWrapper)map.get(object);
        if (frequency==null) {
            frequency=new IntegerWrapper();
            map.put(object,frequency);
        }
        frequency.m_value+=amount;
    }
    /**
     * Computes the frequency of each object in the transaction separately.
     *
     * @return                              the map indexed by the objects containing IntegerWrapper objects
     * @throws InterruptedException         thrown if the algorithm has been interrupted
     */
    protected Map computeSingleObjectFrequency() throws InterruptedException {
        Map result=new HashMap();
        Set transactions=m_transactions.keySet();
        int processedObjects=0;
        fireProgressReport(PHASE_EXTRACT_FREQUENT_OBJECTS,PHASE_NUMBER,processedObjects,transactions.size());
        Iterator iterator=transactions.iterator();
        while (iterator.hasNext()) {
            Transaction transaction=(Transaction)iterator.next();
            addFrequency(transaction.m_object0,result,transaction.m_occurs);
            addFrequency(transaction.m_object1,result,transaction.m_occurs);
            checkInterrupted();
            processedObjects++;
            if ((processedObjects % 10)==0)
                fireProgressReport(PHASE_EXTRACT_FREQUENT_OBJECTS,PHASE_NUMBER,processedObjects,transactions.size());
        }
        fireProgressReport(PHASE_EXTRACT_FREQUENT_OBJECTS,PHASE_NUMBER,processedObjects,transactions.size());
        return result;
    }
    /**
     * Generate the list of frequent objects, that is, objects that occur at least minimumSupport times.
     *
     * @param singleObjectFrequency         the map of frequencies of single objects
     * @return                              the list of candidates
     * @throws InterruptedException         thrown if the algorithm has been interrupted
     */
    protected List computeFrequentObjects(Map singleObjectFrequency) throws InterruptedException {
        List result=new ArrayList();
        Iterator keys=singleObjectFrequency.keySet().iterator();
        while (keys.hasNext()) {
            Object object=keys.next();
            IntegerWrapper frequency=(IntegerWrapper)singleObjectFrequency.get(object);
            double objectSupport=frequency.m_value/((double)m_transactionTotal);
            if (objectSupport>=m_minimumSupport)
                result.add(object);
            checkInterrupted();
        }
        return result;
    }
    /**
     * Generates the list of potential association rules. The result is a list containing Transaction objects.
     *
     * @param frequentObjects               the list of frequent objects
     * @return                              the list containing the Transaction objects
     * @throws InterruptedException         thrown if the algorithm has been interrupted
     */
    protected List generateCandidateTransactions(List frequentObjects) throws InterruptedException {
        List result=new ArrayList();
        long size=frequentObjects.size();
        long totalSteps=size*(size-1)/2;
        long processedSteps=0;
        fireProgressReport(PHASE_GENERATE_CANDIDATES,PHASE_NUMBER,processedSteps,totalSteps);
        for (int i=0;i<frequentObjects.size();i++) {
            Object candidate0=frequentObjects.get(i);
            for (int j=i+1;j<frequentObjects.size();j++) {
                Object candidate1=frequentObjects.get(j);
                Transaction transaction=getTransaction(candidate0,candidate1);
                if (transaction!=null) {
                    transaction.updateSupport(m_transactionTotal);
                    if (transaction.m_support>=m_minimumSupport)
                        result.add(transaction);
                }
                checkInterrupted();
                processedSteps++;
                if ((processedSteps % 100)==0)
                    fireProgressReport(PHASE_GENERATE_CANDIDATES,PHASE_NUMBER,processedSteps,totalSteps);
            }
        }
        fireProgressReport(PHASE_GENERATE_CANDIDATES,PHASE_NUMBER,processedSteps,totalSteps);
        return result;
    }
    /**
     * Generates association rules from the list of candidate pairs.
     *
     * @param candidateTransactions         the list of candidate transactions
     * @param singleObjectFrequency         the map of frequencies of objects
     * @throws InterruptedException         thrown if the algorithm has been interrupted
     */
    protected void generateAssociationRules(List candidateTransactions,Map singleObjectFrequency) throws InterruptedException {
        int transactionsProcessed=0;
        fireProgressReport(PHASE_GENERATE_RULES,PHASE_NUMBER,transactionsProcessed,candidateTransactions.size());
        Iterator transactions=candidateTransactions.iterator();
        while (transactions.hasNext()) {
            Transaction transaction=(Transaction)transactions.next();
            int candidate0Frequency=((IntegerWrapper)singleObjectFrequency.get(transaction.m_object0)).m_value;
            int candidate1Frequency=((IntegerWrapper)singleObjectFrequency.get(transaction.m_object1)).m_value;
            double confidence0=transaction.m_occurs/(double)candidate0Frequency;
            if (confidence0>=m_minimumConfidence) {
                AssociationRule associationRule=m_associationRules.getAssociationRule(transaction.m_object0,transaction.m_object1);
                associationRule.setSupport(transaction.m_support);
                associationRule.setConfidence(confidence0);
                associationRule.setAbsoluteFrequency(transaction.m_occurs);
            }
            double confidence1=transaction.m_occurs/(double)candidate1Frequency;
            if (confidence1>=m_minimumConfidence) {
                AssociationRule associationRule=m_associationRules.getAssociationRule(transaction.m_object1,transaction.m_object0);
                associationRule.setSupport(transaction.m_support);
                associationRule.setConfidence(confidence1);
                associationRule.setAbsoluteFrequency(transaction.m_occurs);
            }
            checkInterrupted();
            transactionsProcessed++;
            if ((transactionsProcessed % 10)==0)
                fireProgressReport(PHASE_GENERATE_RULES,PHASE_NUMBER,transactionsProcessed,candidateTransactions.size());
        }
        fireProgressReport(PHASE_GENERATE_RULES,PHASE_NUMBER,transactionsProcessed,candidateTransactions.size());
    }

    /**
     * A transaction in the list.
     */
    protected static class Transaction {
        public Object m_object0;
        public Object m_object1;
        public int m_occurs;
        public double m_support;
        protected int m_hashCode;

        public Transaction(Object object0,Object object1) {
            if (object0.hashCode()<object1.hashCode()) {
                m_object0=object0;
                m_object1=object1;
            }
            else {
                m_object0=object1;
                m_object1=object0;
            }
            m_occurs=0;
            m_hashCode=m_object0.hashCode()*7+m_object1.hashCode();
        }
        public void updateSupport(int totalTransactions) {
            m_support=m_occurs/((double)totalTransactions);
        }
        public int hashCode() {
            return m_hashCode;
        }
        public boolean equals(Object that) {
            if (this==that)
                return true;
            if (!(that instanceof Transaction))
                return false;
            Transaction thatTransaction=(Transaction)that;
            return m_object0.equals(thatTransaction.m_object0) && m_object1.equals(thatTransaction.m_object1);
        }
    }

    /**
     * Wraps an int, but for the difference of Integer class, allows incrementing the integer.
     */
    protected static class IntegerWrapper {
        public int m_value;
    }
}
