package psl.memento.pervasive.ontology.relationsextraction;

import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import de.fzi.wim.kaoncorpus.api.*;

//CONVERTED FROM KAON TO PSL ONTOLOGY

/**
 * Base class for algorithms requiring a list of stop-words and a stemmer.
 */
public abstract class AbstractLinguisticAlgorithm extends AbstractAlgorithm {
    /** Sets of stop-words for indexed by the language. */
    protected static final Map s_stopwordSetsByLanguage=new HashMap();
    /** Returns the set of supported languages. */
    protected static final Set s_supportedLanguages=new TreeSet();
    static {
        s_supportedLanguages.add("en");
    }

    /** Language for which this algorithm operates. */
    protected String m_language;
    /** Set of stemmed stoppwords. */
    protected Set m_stopwords;
    /** Stemmer to use. */
    protected Stemmer m_stemmer;

    /**
     * Constructs an instance of this class for given language.
     *
     * @param language                      language for which this algorithm operates
     * @throws IllegalArgumentException     thrown if language is not supported
     */
    public AbstractLinguisticAlgorithm(String language) throws IllegalArgumentException {
        m_language=language;
        m_stopwords=getStopwords(m_language);
        m_stemmer=getStemmer(m_language);
    }
    /**
     * Returns the language of this algorithm.
     *
     * @return                              language of the current algorithm
     */
    public String getLanguage() {
        return m_language;
    }
    /**
     * Reads the stopwords from a given file. Each line in the Document is interpreted as one stop word. The stop word needn't to be stemmed.
     *
     * @param language                      language for which stopwords are read
     * @return                              set of stopwords
     * @throws IllegalArgumentException     thrown if language is not supported
     */
    protected static synchronized Set getStopwords(String language) throws IllegalArgumentException {
        Set stopwords=(Set)s_stopwordSetsByLanguage.get(language);
        if (stopwords==null) {
            Stemmer stemmer=getStemmer(language);
            stopwords=new HashSet();
            URL url=AbstractLinguisticAlgorithm.class.getResource("res/stopwords_"+language+".txt");
            if (url==null)
                throw new IllegalArgumentException("Stopword list for language '"+language+"' is missing.");
            BufferedReader in=null;
            try {
                in=new BufferedReader(new InputStreamReader(url.openStream()));
                String word=in.readLine();
                while (word!=null ) {
                    stopwords.add(stemmer.getWordStem(word));
                    word=in.readLine();
                }
            }
            catch (IOException e) {
                IllegalArgumentException exception=new IllegalArgumentException("Error reading stopword list '"+url+"'.");
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
                s_stopwordSetsByLanguage.put(language,stopwords);
        }
        return stopwords;
    }
    /**
     * Returns the stemmer for given language.
     *
     * @param language                      language for which the stemmer is required
     * @return                              stemmer instance for given language
     * @throws IllegalArgumentException     thrown if language is not supported
     */
    protected static Stemmer getStemmer(String language) throws IllegalArgumentException {
        if ("en".equals(language))
            return new StemmerEN();
        else
            throw new IllegalArgumentException("Cannot create stemmer for language '"+language+"'");
    }
    /**
     * Utility method returning the stem of the word currently in use.
     *
     * @param word                          word to be stemmed
     * @return                              the stem of the word
     */
    protected String getWordStem(String word) {
        return m_stemmer.getWordStem(word);
    }
    /**
     * Returns the content as string of given document.
     *
     * @param document                      the document
     * @return                              the text (or <code>null</code> if text cannot be read)
     * @throws IOException                  thrown if there is a problem with reading document's content
     */
    public String getDocumentText(Document document) throws IOException {
        Content content=document.getDefaultContent();
        if (content==null)
            return "";
        else
            return content.getContentAsText();
    }
    /**
     * Returns the unstemmed list of words extracted from the text of given document.
     *
     * @param document                      document whose list of words is examined
     * @return                              list of words contained in given document
     * @throws IOException                  thrown if there is a problem with extracting the list of words
     */
    protected String[] getUnstemmedWords(Document document) throws IOException {
        String documentText=getDocumentText(document);
        return getUnstemmedWords(documentText);
    }
    /**
     * Returns the list of unstemmed words extracted from a given string.
     *
     * @param text                          the string to be parsed
     * @return                              list of unstemmed words contained in given string
     */
    protected String[] getUnstemmedWords(String text) {
        String[] result;
        List words=new ArrayList(text.length()/4);
        StringTokenizer tokens=new StringTokenizer(text," \n\t\r\f.,;:*'\"?!()´`-");
        while (tokens.hasMoreTokens()) {
            String word=(String)tokens.nextElement();
            if (!wordContainsSpecialCharacters(word) && word.length()>1)
                words.add(word.toLowerCase());
        }
        result=new String[words.size()];
        words.toArray(result);
        return result;
    }
    /**
     * Tests whether given word contains a special character (anything other than a letter or a space.
     *
     * @param word                          the word being tested
     * @return                              <code>true</code> if the word contains a special character
     */
    protected static boolean wordContainsSpecialCharacters(String word) {
        for (int i=0;i<word.length();i++) {
            char c=word.charAt(i);
            if (!Character.isLetter(c) && c!='_')
                return true;
        }
        return false;
    }
    /**
     * Returns the set of supported languages.
     *
     * @return                              the set of supported language
     */
    public static Set getSupportedLanguages() {
        return s_supportedLanguages;
    }
    /**
     * Returns <code>true</code> if supplied stemmed word is a stopword.
     *
     * @param stemmedWord                   stemmed word being tested
     * @return                              <code>true</code> if given stemmed word is a stop-word
     */
    protected boolean isStopWord(String stemmedWord) {
        return m_stopwords.contains(stemmedWord);
    }

    /**
     * Iterator that returns sentences from a text.
     */
    protected static class SentenceIterator implements Iterator {
        /** The original text. */
        protected String m_text;
        /** The end position of the last match. */
        protected int m_lastDotPlusOne;
        /** Current sentence. */
        protected String m_sentence;

        /**
         * Creates an instance of this class.
         *
         * @param text                      the text
         */
        public SentenceIterator(String text) {
            m_text=text;
            m_lastDotPlusOne=0;
            advance();
        }
        public boolean hasNext() {
            return m_sentence!=null;
        }
        public Object next() {
            if (m_sentence==null)
                throw new NoSuchElementException();
            String sentence=m_sentence;
            advance();
            return sentence;
        }
        public void remove() {
            throw new UnsupportedOperationException("remove() is not supported on SentenceIterator.");
        }
        protected void advance() {
            if (m_lastDotPlusOne==-1)
                m_sentence=null;
            else {
                int dotPosition=m_text.indexOf('.',m_lastDotPlusOne);
                if (dotPosition>=0) {
                    m_sentence=m_text.substring(m_lastDotPlusOne,dotPosition+1);
                    m_lastDotPlusOne=dotPosition+1;
                }
                else {
                    m_sentence=m_text.substring(m_lastDotPlusOne);
                    m_lastDotPlusOne=-1;
                }
                if (m_sentence.length()==0) {
                    m_lastDotPlusOne=-1;
                    m_sentence=null;
                }
            }
        }
    }
}
