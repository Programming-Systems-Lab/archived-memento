package psl.memento.pervasive.ontology.relationsextraction;

import java.util.EventListener;
import javax.swing.event.EventListenerList;
import javax.swing.SwingUtilities;

//CONVERTED FROM KAON TO PSL ONTOLOGY

/**
 * Base class for all algorithms providing convenient ways to provide feedback on the algorithm progress.
 */
public abstract class AbstractAlgorithm {
    /** Set to <code>true</code> if various files (e.g. stopword list) should be reloaded every time the algorithm is restarted. */
    protected static final boolean s_developmentMode="true".equalsIgnoreCase(System.getProperty("texttoonto.developmentMode"));

    /** The list of event listeners. */
    protected EventListenerList m_listenerList;

    /**
     * Creates an instance of this class.
     */
    public AbstractAlgorithm() {
        m_listenerList=new EventListenerList();
    }
    /**
     * Adds a progress listener to this algorithm.
     *
     * @param listener                      the progress listener
     */
    public void addAlgorithmProgressListener(AlgorithmProgressListener listener) {
        m_listenerList.add(AlgorithmProgressListener.class,listener);
    }
    /**
     * Removes a progress listener from this algorithm.
     *
     * @param listener                      the progress listener
     */
    public void removeAlgorithmProgressListener(AlgorithmProgressListener listener) {
        EventListener[] eventListeners=m_listenerList.getListeners(AlgorithmProgressListener.class);
        for (int i=0;i<eventListeners.length;i++) {
            if (eventListeners[i]==listener) {
                m_listenerList.remove(AlgorithmProgressListener.class,eventListeners[i]);
                break;
            }
            if ((eventListeners[i] instanceof DispatchThreadAlgorithmProgressListener) && ((DispatchThreadAlgorithmProgressListener)eventListeners[i]).getListener()==listener) {
                m_listenerList.remove(AlgorithmProgressListener.class,eventListeners[i]);
                break;
            }
        }
    }
    /**
     * Adds a progress listener that executes on the Swing's event dispatch thread.
     *
     * @param listener                      the progress listener
     */
    public void addDispatchThreadAlgorithmProgressListener(AlgorithmProgressListener listener) {
        addAlgorithmProgressListener(new DispatchThreadAlgorithmProgressListener(listener));
    }
    /**
     * Checks whether the algorithm has been interrupted, and if so, throws an exception. This
     * method doesn't clear the thread's interruption flag.
     *
     * @throws InterruptedException         thrown if the algorithm has been interrupted
     */
    protected void checkInterrupted() throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
    }
    /**
     * Notifies listeners that the algorithm has started.
     *
     * @param numberOfPhases                the number of phases of the algorithm
     */
    protected void fireAlgorithmStarted(int numberOfPhases) {
        EventListener[] eventListeners=m_listenerList.getListeners(AlgorithmProgressListener.class);
        for (int i=0;i<eventListeners.length;i++)
            ((AlgorithmProgressListener)eventListeners[i]).algorithmStarted(this,numberOfPhases);
    }
    /**
     * Notifies listeners that the algorithm has finished.
     *
     * @param currentPhase                      the index of the current phase
     * @param numberOfPhases                    the number of phases of the algorithm
     * @param currentStep                       the index of the current step
     * @param numberOfSteps                     the number of steps in the current phase
     */
    protected void fireProgressReport(int currentPhase,int numberOfPhases,long currentStep,long numberOfSteps) {
        EventListener[] eventListeners=m_listenerList.getListeners(AlgorithmProgressListener.class);
        for (int i=0;i<eventListeners.length;i++)
            ((AlgorithmProgressListener)eventListeners[i]).progressReport(this,currentPhase,numberOfPhases,currentStep,numberOfSteps);
    }
    /**
     * Notifies listeners that the algorithm has finished.
     */
    protected void fireAlgorithmFinished() {
        EventListener[] eventListeners=m_listenerList.getListeners(AlgorithmProgressListener.class);
        for (int i=0;i<eventListeners.length;i++)
            ((AlgorithmProgressListener)eventListeners[i]).algorithmFinished(this);
    }

    /**
     * The wrapper that forwards all progress events to the Swing's event dispatch thread.
     */
    protected static class DispatchThreadAlgorithmProgressListener implements AlgorithmProgressListener {
        /** The original listener. */
        protected AlgorithmProgressListener m_listener;

        /**
         * Creates an instance of this class.
         *
         * @param listener                  the original progress listener
         */
        public DispatchThreadAlgorithmProgressListener(AlgorithmProgressListener listener) {
            m_listener=listener;
        }
        /**
         * Returns the original listener.
         *
         * @return                          the original listener
         */
        public AlgorithmProgressListener getListener() {
            return m_listener;
        }
        /**
         * Called when the algorithm is started.
         *
         * @param algorithm                         the algorithm
         * @param numberOfPhases                    the number of phases of the algorithm
         */
        public void algorithmStarted(final AbstractAlgorithm algorithm,final int numberOfPhases) {
            if (SwingUtilities.isEventDispatchThread())
                m_listener.algorithmStarted(algorithm,numberOfPhases);
            else
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        m_listener.algorithmStarted(algorithm,numberOfPhases);
                    }
                });
        }
        /**
         * Called to report the progress of the algorithm.
         *
         * @param algorithm                         the algorithm
         * @param currentPhase                      the index of the current phase
         * @param numberOfPhases                    the number of phases of the algorithm
         * @param currentStep                       the index of the current step
         * @param numberOfSteps                     the number of steps in the current phase
         */
        public void progressReport(final AbstractAlgorithm algorithm,final int currentPhase,final int numberOfPhases,final long currentStep,final long numberOfSteps) {
            if (SwingUtilities.isEventDispatchThread())
                m_listener.progressReport(algorithm,currentPhase,numberOfPhases,currentStep,numberOfSteps);
            else
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        m_listener.progressReport(algorithm,currentPhase,numberOfPhases,currentStep,numberOfSteps);
                    }
                });
        }
        /**
         * Called when the algorithm is finished.
         *
         * @param algorithm                         the algorithm
         */
        public void algorithmFinished(final AbstractAlgorithm algorithm) {
            if (SwingUtilities.isEventDispatchThread())
                m_listener.algorithmFinished(algorithm);
            else
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        m_listener.algorithmFinished(algorithm);
                    }
                });
        }
    }
}
