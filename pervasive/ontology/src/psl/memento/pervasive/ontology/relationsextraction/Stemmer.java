package psl.memento.pervasive.ontology.relationsextraction;

//CONVERTED FROM KAON TO PSL ONTOLOGY

/**
 * Interface for stemmers.
 */
public interface Stemmer {
    /**
     * Returns the stem of the word.
     *
     * @param word                          word to be stemmed
     * @return                              the stem of hte word
     */
    String getWordStem(String word);
}
