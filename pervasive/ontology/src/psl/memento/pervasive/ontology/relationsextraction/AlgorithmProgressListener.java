package psl.memento.pervasive.ontology.relationsextraction;

import java.util.EventListener;

//CONVERTED FROM KAON TO PSL ONTOLOGY

/**
 * Listener for algorithm progress. Progress of an algorithm is specified in the current phase of the algorithm,
 * the current step in the current phase of the algorithm and the maximal number of steps.
 */
public interface AlgorithmProgressListener extends EventListener {
    /**
     * Called when the algorithm is started.
     *
     * @param algorithm                         the algorithm
     * @param numberOfPhases                    the number of phases of the algorithm
     */
    void algorithmStarted(AbstractAlgorithm algorithm,int numberOfPhases);
    /**
     * Called to report the progress of the algorithm.
     *
     * @param algorithm                         the algorithm
     * @param currentPhase                      the index of the current phase
     * @param numberOfPhases                    the number of phases of the algorithm
     * @param currentStep                       the index of the current step
     * @param numberOfSteps                     the number of steps in the current phase
     */
    void progressReport(AbstractAlgorithm algorithm,int currentPhase,int numberOfPhases,long currentStep,long numberOfSteps);
    /**
     * Called when the algorithm is finished.
     *
     * @param algorithm                         the algorithm
     */
    void algorithmFinished(AbstractAlgorithm algorithm);
}
