import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Properties;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.JFrame;
import org.w3c.dom.*;
import org.openxml.parser.HTMLParser;
import org.xml.sax.InputSource;
import org.apache.xml.serialize.*;
import java.nio.charset.*;
import java.nio.ByteBuffer;

/** This class uses a settings file to determine portions of a
 * web site to remove, thus extracting the true content of a
 * site based on the user's preferences.
 * @author David Neistadt
 */
public class ContentExtractor implements ProxyFilter {
 
    //Instance variables
    private String mSettingsFile;  //the settings file path    
    private Properties mSettings;  //the settings properties file
    private InputStream mIn;       //the inputstream to filter
    private Document mTree;        //the DOM tree for HTML
    private Hashtable mAdServers;  //hashtable of ad servers
    private LinkedList mLinksSource; //hashtable of all the removed link sources
    private LinkedList mLinksText;  //hashtable of all the removed link texts
    private LinkedList mLinksSourceAll; //hashtable of ALL link sources
    private LinkedList mLinksTextAll; //hashtable of ALL link texts
    private LinkedList mImagesSource; //hashtable of ALL image sources
    private ProxyFilterSettings mSettingsGUI;   //the settings JPanel
    private boolean mCheckChildren; //boolean to see if children nodes should be checked
    private String textPrintBuffer; //the line to print when text printing
    private int numberBlankLines;   //the number of consecutive blank lines
    private int lengthForTableRemover;  //the cumulative length of text in a table
    private Node mBodyNode;         //the BODY tag node for the link enqueuer
        
    //Settings variables
    public static final String IGNORE_TEXT_LINKS = "Ignore Text Links";
    private final String IGNORE_TEXT_LINKS_DEF = "true";
    private boolean ignoreTextLinks;   
    
    public static final String IGNORE_IMAGES = "Ignore Images";
    private final String IGNORE_IMAGES_DEF = "true";
    private boolean ignoreImages;
    
    public static final String IGNORE_SCRIPTS = "Ignore Scripts";
    private final String IGNORE_SCRIPTS_DEF = "true";
    private boolean ignoreScripts;
    
    public static final String IGNORE_STYLES = "Ignore Styles";
    private final String IGNORE_STYLES_DEF = "false";
    private boolean ignoreStyles;
    
    public static final String IGNORE_FORMS = "Ignore Forms";    
    private final String IGNORE_FORMS_DEF = "true";
    private boolean ignoreForms;
    
    public static final String IGNORE_META = "Ignore Meta Tags";
    private final String IGNORE_META_DEF = "true";
    private boolean ignoreMeta;
    
    //private final String MINIMUM_TEXT_LENGTH = "Minimum Text Length";
    //private final String MINIMUM_TEXT_LENGTH_DEF = "0";
    //private int minTextLength;
    
    //================================================================
    //All the settings for link lists - or link cells
    public static final String IGNORE_LINK_CELLS = "Ignore Link Lists";
    private final String IGNORE_LINK_CELLS_DEF = "true";
    private boolean ignoreLinkCells;
   
    //LC stands for Link Cells
    public static final String LC_IGNORE_IMAGE_LINKS = "Ignore Image Links in Link Lists";
    private final String LC_IGNORE_IMAGE_LINKS_DEF = "true";
    private boolean ignoreLCImageLinks;
    
    public static final String LC_IGNORE_TEXT_LINKS = "Ignore Text Links in Link Lists";
    private final String LC_IGNORE_TEXT_LINKS_DEF = "true";
    private boolean ignoreLCTextLinks;
    
    public static final String LC_ONLY_LINKS_AND_TEXT = "Ignore Only Links and Text in Link Lists";
    private final String LC_ONLY_LINKS_AND_TEXT_DEF = "true";
    private boolean ignoreLCOnlyLinksAndText;
    
    //End of settings for link lists - or link cells
    //=================================================================
    
    public static final String IGNORE_IMAGE_LINKS = "Ignore Image Links";
    private final String IGNORE_IMAGE_LINKS_DEF = "true";
    private boolean ignoreImageLinks;
    
    public static final String IGNORE_INPUT_TAGS = "Ignore <INPUT> Tags";
    private final String IGNORE_INPUT_TAGS_DEF = "true"; 
    private boolean ignoreInputTags;
    
    public static final String IGNORE_BUTTON_TAGS = "Ignore <BUTTON> Tags";
    private final String IGNORE_BUTTON_TAGS_DEF = "true";
    private boolean ignoreButtonTags;
    
    public static final String IGNORE_SELECT_TAGS = "Ignore <SELECT> Tags";
    private final String IGNORE_SELECT_TAGS_DEF = "true";
    private boolean ignoreSelectTags;
    
    public static final String IGNORE_NOSCRIPT_TAGS = "Ignore <NOSCRIPT> Tags";
    private final String IGNORE_NOSCRIPT_TAGS_DEF = "true";
    private boolean ignoreNoscriptTags;
    
    public static final String IGNORE_CELL_WIDTH = "Ignore Table Cell Widths";
    private final String IGNORE_CELL_WIDTH_DEF = "false";
    private boolean ignoreCellWidth;
    
    public static final String IGNORE_ADS = "Ignore All Advertisements";
    private final String IGNORE_ADS_DEF = "true";
    private boolean ignoreAds;
    
    public static final String ONLY_TEXT = "Print Only Text";
    private final String ONLY_TEXT_DEF = "false";
    private boolean onlyText;
    
    public static final String IGNORE_DIV_STYLES = "Ignore Style Attribute in <DIV> Tags";
    private final String IGNORE_DIV_STYLES_DEF = "false";
    private boolean ignoreDivStyles;
    
    public static final String IGNORE_IFRAME_TAGS = "Ignore <IFRAME> Tags";
    private final String IGNORE_IFRAME_TAGS_DEF = "false";
    private boolean ignoreIFrameTags;
    
    public static final String DISPLAY_IMAGE_ALTS = "Display Image ALTs";
    private final String DISPLAY_IMAGE_ALTS_DEF = "false";
    private boolean displayImageAlts;
    
    public static final String DISPLAY_IMAGE_LINK_ALTS = "Display Image Link ALTs";
    private final String DISPLAY_IMAGE_LINK_ALTS_DEF = "false";
    private boolean displayImageLinkAlts;
    
    ////////////////////////// Empty Table Settings ////////////////////////////
    public static final String REMOVE_EMPTY_TABLES = "Remove Empty Tables";
    private final String REMOVE_EMPTY_TABLES_DEF = "true";
    private boolean removeEmptyTables;
    
    public static final String SUBSTANCE_IMAGE = "<IMG> tags are substance";
    private final String SUBSTANCE_IMAGE_DEF = "true";
    private boolean substanceImage;
    
    public static final String SUBSTANCE_LINKS = "<A> tags are substance";
    private final String SUBSTANCE_LINKS_DEF = "true";
    private boolean substanceLinks;
    
    public static final String SUBSTANCE_IFRAME = "<IFRAME> tags are substance";
    private final String SUBSTANCE_IFRAME_DEF = "true";
    private boolean substanceIFrame;
    
    public static final String SUBSTANCE_INPUT = "<INPUT> tags are substance";
    private final String SUBSTANCE_INPUT_DEF = "true";
    private boolean substanceInput;
    
    public static final String SUBSTANCE_BUTTON = "<BUTTON> tags are substance";
    private final String SUBSTANCE_BUTTON_DEF = "true";
    private boolean substanceButton;
    
    public static final String SUBSTANCE_TEXTAREA = "<TEXTAREA> tags are substance";
    private final String SUBSTANCE_TEXTAREA_DEF = "true";
    private boolean substanceTextarea;
    
    public static final String SUBSTANCE_SELECT = "<SELECT> tags are substance";
    private final String SUBSTANCE_SELECT_DEF = "true";
    private boolean substanceSelect;
    
    public static final String SUBSTANCE_FORM = "<FORM> tags are substance";
    private final String SUBSTANCE_FORM_DEF = "false";
    private boolean substanceForm;
    
    public static final String SUBSTANCE_MIN_TEXT_LENGTH = "Minimum text length as substance";
    private final String SUBSTANCE_MIN_TEXT_LENGTH_DEF = "1";
    private int substanceMinTextLength;
    
    ////////////////////////////////////////////////////////////////////////////
    
    public static final String LIMIT_LINEBREAKS = "Limit Number of Line Breaks";
    private final String LIMIT_LINEBREAKS_DEF = "true";
    private boolean limitLinebreaks;
    
    public static final String MAX_LINEBREAKS = "Maximum Number of Line Breaks";
    private final String MAX_LINEBREAKS_DEF = "2";
    private int maxLinebreaks;
    
    public static final String ADD_LINKS_TO_BOTTOM = "Add removed links to bottom of the page";
    private final String ADD_LINKS_TO_BOTTOM_DEF = "false";
    private boolean addLinksToBottom;
    
    public static final String IGNORE_EMBED_TAGS = "Ignore <EMBED> tags";
    private final String IGNORE_EMBED_TAGS_DEF = "false";
    private boolean ignoreEmbedTags;
    
    /*
     * Link/Text ratio is determined by the amount of text words to single links
     * A word is considered 4 letters long.
     */
    public final static String LINK_TEXT_REMOVAL_RATIO = "Link/Text Removal Ratio";
    private final String LINK_TEXT_REMOVAL_RATIO_DEF = ".25";
    private double linkTextRatio;
    
    //Normal final variables not associated with settings
    public static final int ALL = 0;
    public static final int TEXT = 1;
    public static final int IMAGE = 2;
    public static final int LETTERS_PER_WORD = 5;
    public static final String SETTINGS_FILE_DEF = "settings.txt";
    public static final String AD_FILE = "serverlist.txt";
    public static final String CONTENT_TEXT = "text/plain";
    public static final String CONTENT_HTML = "text/html";
    
    /**
     * Creates a new instance without any input stream and the
     * default settings file.
     */
    public ContentExtractor() {
        this(ContentExtractor.SETTINGS_FILE_DEF, null);
    }
    
    /**
     * Creates a new instance without any input stream and with a settings file
     * @param iSettings the settings file path
     */
    public ContentExtractor(String iSettings) {
        this(iSettings, null);
    }
    
    /**
     * Creates a new instance of ContentExtractor with the default settings file
     * @param iIn the input stream of the HTML file
     */
    public ContentExtractor(InputStream iIn) {
        this(ContentExtractor.SETTINGS_FILE_DEF, iIn); 
    }
    
    /** Creates a new instance of ContentExtractor
     * @param iSettings the name of the settings file
     * @param iIn the input stream of the HTML file
     */
    public ContentExtractor(String iSettings, InputStream iIn) {
        mSettingsFile = iSettings;
        mSettings = new Properties();
        mIn = iIn;
        
        //Load settings and ad server lists
        loadSettingsProperties();
        loadAdsServerList();
        loadSettings();
        
        mSettingsGUI = new SettingsEditor(this);
        textPrintBuffer = "";
        numberBlankLines = 0;
        mLinksSource = new LinkedList();
        mLinksText = new LinkedList();
        mLinksSourceAll = new LinkedList();
        mLinksTextAll = new LinkedList();
        mImagesSource = new LinkedList();
    }

    /**
     * Loads the settings into the property file
     */
    private void loadSettingsProperties() {
        try {
            mSettings.load(new FileInputStream(mSettingsFile));
        }
        catch (FileNotFoundException e) {
            //Don't load the settings if the file doesn't exist
        }
        
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads the ad file into a hashtable
     */
    public void loadAdsServerList() {
        mAdServers = new Hashtable();

        try {
            FileReader fr = new FileReader(new File(AD_FILE));
            BufferedReader in = new BufferedReader(fr);
            String line = in.readLine();
            
            while (line != null) {
                mAdServers.put(line, line);
                line = in.readLine();
            }//while
        }
        catch (FileNotFoundException e) {
            //if the ad file is not there, don't do anything, just print
            //that the file isn't there
            System.out.println("WARNING: Server list for ad remover not found");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }//loadAdsServerList
    
    /**
     * Returns a setting based on the final variables
     * @param iSetting the name of the setting based on the final variables
     * @return the setting as a string. For boolean values, "true" and "false"
     * will be returned. Null will be returned if the setting doesn't exist
     */
    public String getSetting(String iSetting) {
        if (iSetting.equalsIgnoreCase(IGNORE_ADS))
            return Boolean.toString(ignoreAds);
        else if (iSetting.equalsIgnoreCase(IGNORE_BUTTON_TAGS))
            return Boolean.toString(ignoreButtonTags);
        else if (iSetting.equalsIgnoreCase(IGNORE_CELL_WIDTH))           
            return Boolean.toString(ignoreCellWidth);      
        else if (iSetting.equalsIgnoreCase(IGNORE_DIV_STYLES))           
            return Boolean.toString(ignoreDivStyles);      
        else if (iSetting.equalsIgnoreCase(IGNORE_FORMS))
            return Boolean.toString(ignoreForms);        
        else if (iSetting.equalsIgnoreCase(IGNORE_IFRAME_TAGS))         
            return Boolean.toString(ignoreIFrameTags);
        else if (iSetting.equalsIgnoreCase(IGNORE_IMAGE_LINKS))
            return Boolean.toString(ignoreImageLinks);
        else if (iSetting.equalsIgnoreCase(IGNORE_IMAGES))  
            return Boolean.toString(ignoreImages);       
        else if (iSetting.equalsIgnoreCase(IGNORE_INPUT_TAGS))           
            return Boolean.toString(ignoreInputTags);       
        else if (iSetting.equalsIgnoreCase(IGNORE_LINK_CELLS))           
            return Boolean.toString(ignoreLinkCells);       
        else if (iSetting.equalsIgnoreCase(IGNORE_META))           
            return Boolean.toString(ignoreMeta);        
        else if (iSetting.equalsIgnoreCase(IGNORE_NOSCRIPT_TAGS))          
            return Boolean.toString(ignoreNoscriptTags);      
        else if (iSetting.equalsIgnoreCase(IGNORE_SCRIPTS))          
            return Boolean.toString(ignoreScripts);     
        else if (iSetting.equalsIgnoreCase(IGNORE_SELECT_TAGS))        
            return Boolean.toString(ignoreSelectTags);   
        else if (iSetting.equalsIgnoreCase(IGNORE_STYLES))     
            return Boolean.toString(ignoreStyles); 
        else if (iSetting.equalsIgnoreCase(IGNORE_TEXT_LINKS))      
            return Boolean.toString(ignoreTextLinks); 
        else if (iSetting.equalsIgnoreCase(LC_IGNORE_IMAGE_LINKS))     
            return Boolean.toString(ignoreLCImageLinks);
        else if (iSetting.equalsIgnoreCase(LC_IGNORE_TEXT_LINKS))    
            return Boolean.toString(ignoreLCTextLinks);
        else if (iSetting.equalsIgnoreCase(LINK_TEXT_REMOVAL_RATIO))    
            return Double.toString(linkTextRatio);
        else if (iSetting.equalsIgnoreCase(ONLY_TEXT))  
            return Boolean.toString(onlyText);
        else if (iSetting.equalsIgnoreCase(LC_ONLY_LINKS_AND_TEXT))   
            return Boolean.toString(ignoreLCOnlyLinksAndText);
        else if (iSetting.equalsIgnoreCase(DISPLAY_IMAGE_ALTS))   
            return Boolean.toString(displayImageAlts);
        else if (iSetting.equalsIgnoreCase(DISPLAY_IMAGE_LINK_ALTS))  
            return Boolean.toString(displayImageLinkAlts);
        else if (iSetting.equalsIgnoreCase(REMOVE_EMPTY_TABLES)) 
            return Boolean.toString(removeEmptyTables);
        else if (iSetting.equalsIgnoreCase(LIMIT_LINEBREAKS))
            return Boolean.toString(limitLinebreaks);
        else if (iSetting.equalsIgnoreCase(MAX_LINEBREAKS)) 
            return Integer.toString(maxLinebreaks);
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_BUTTON))  
            return Boolean.toString(substanceButton);
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_FORM)) 
            return Boolean.toString(substanceForm);
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_IFRAME)) 
            return Boolean.toString(substanceIFrame);
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_IMAGE))
            return Boolean.toString(substanceImage);
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_INPUT))
            return Boolean.toString(substanceInput);
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_LINKS))
            return Boolean.toString(substanceLinks);
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_MIN_TEXT_LENGTH))
            return Integer.toString(substanceMinTextLength);
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_SELECT))
            
            return Boolean.toString(substanceSelect);
        
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_TEXTAREA))
            
            return Boolean.toString(substanceTextarea);
        
        else if (iSetting.equalsIgnoreCase(ADD_LINKS_TO_BOTTOM))
            
            return Boolean.toString(addLinksToBottom);
        
        else if (iSetting.equalsIgnoreCase(IGNORE_EMBED_TAGS))
            
            return Boolean.toString(ignoreEmbedTags);
        
        
        
        return null;
        
    }//getSettings
    
    
    
    /**
     *
     * Sets a setting based on the final variables
     *
     * @param iSetting the name of the setting based on the final variables
     *
     * @param iValue the desired value of the setting. For boolean values,
     *
     * "true" and "false" should be used
     *
     */
    
    public void changeSetting(String iSetting, String iValue) {
        
        if (iSetting.equalsIgnoreCase(IGNORE_ADS))
            
            ignoreAds = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_BUTTON_TAGS))
            
            ignoreButtonTags = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_CELL_WIDTH))
            
            ignoreCellWidth = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_DIV_STYLES))
            
            ignoreDivStyles = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_FORMS))
            
            ignoreForms = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_IFRAME_TAGS))
            
            ignoreIFrameTags = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_IMAGE_LINKS))
            
            ignoreImageLinks = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_IMAGES))
            
            ignoreImages = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_INPUT_TAGS))
            
            ignoreInputTags = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_LINK_CELLS))
            
            ignoreLinkCells = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_META))
            
            ignoreMeta = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_NOSCRIPT_TAGS))
            
            ignoreNoscriptTags = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_SCRIPTS))
            
            ignoreScripts = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_SELECT_TAGS))
            
            ignoreSelectTags = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_STYLES))
            
            ignoreStyles = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_TEXT_LINKS))
            
            ignoreTextLinks = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(LC_IGNORE_IMAGE_LINKS))
            
            ignoreLCImageLinks = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(LC_IGNORE_TEXT_LINKS))
            
            ignoreLCTextLinks = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(LINK_TEXT_REMOVAL_RATIO))
            
            linkTextRatio = Double.parseDouble(iValue);
        
        else if (iSetting.equalsIgnoreCase(ONLY_TEXT))
            
            onlyText = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(LC_ONLY_LINKS_AND_TEXT))
            
            ignoreLCOnlyLinksAndText = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(DISPLAY_IMAGE_ALTS))
            
            displayImageAlts = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(DISPLAY_IMAGE_LINK_ALTS))
            
            displayImageLinkAlts = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(REMOVE_EMPTY_TABLES))
            
            removeEmptyTables = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(LIMIT_LINEBREAKS))
            
            limitLinebreaks = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(MAX_LINEBREAKS))
            
            maxLinebreaks = Integer.parseInt(iValue);
        
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_BUTTON))
            
            substanceButton = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_FORM))
            
            substanceForm = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_IFRAME))
            
            substanceIFrame = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_IMAGE))
            
            substanceImage = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_INPUT))
            
            substanceInput = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_LINKS))
            
            substanceLinks = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_MIN_TEXT_LENGTH))
            
            substanceMinTextLength = Integer.parseInt(iValue);
        
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_SELECT))
            
            substanceSelect = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(SUBSTANCE_TEXTAREA))
            
            substanceTextarea = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(ADD_LINKS_TO_BOTTOM))
            
            addLinksToBottom = iValue.equals("true");
        
        else if (iSetting.equalsIgnoreCase(IGNORE_EMBED_TAGS))
            
            ignoreEmbedTags = iValue.equals("true");
        
    }//changeSetting
    
    
    
    /**
     *
     * Loads the settings file into the boolean values
     *
     */
    
    public void loadSettings() {
        
        ignoreTextLinks = (mSettings.getProperty(IGNORE_TEXT_LINKS, IGNORE_TEXT_LINKS_DEF)).equals("true");
        
        ignoreImageLinks = (mSettings.getProperty(IGNORE_IMAGE_LINKS, IGNORE_IMAGE_LINKS_DEF)).equals("true");
        
        ignoreImages = (mSettings.getProperty(IGNORE_IMAGES, IGNORE_IMAGES_DEF)).equals("true");
        
        ignoreScripts = (mSettings.getProperty(IGNORE_SCRIPTS, IGNORE_SCRIPTS_DEF)).equals("true");
        
        ignoreStyles = (mSettings.getProperty(IGNORE_STYLES, IGNORE_SCRIPTS_DEF)).equals("true");
        
        ignoreForms = (mSettings.getProperty(IGNORE_FORMS, IGNORE_FORMS_DEF)).equals("true");
        
        ignoreMeta = (mSettings.getProperty(IGNORE_META, IGNORE_META_DEF)).equals("true");
        
        ignoreLinkCells = (mSettings.getProperty(IGNORE_LINK_CELLS, IGNORE_LINK_CELLS_DEF)).equals("true");
        
        ignoreLCImageLinks = (mSettings.getProperty(LC_IGNORE_IMAGE_LINKS, LC_IGNORE_IMAGE_LINKS_DEF)).equals("true");
        
        ignoreLCTextLinks = (mSettings.getProperty(LC_IGNORE_TEXT_LINKS, LC_IGNORE_TEXT_LINKS_DEF)).equals("true");
        
        linkTextRatio = Double.parseDouble(mSettings.getProperty(LINK_TEXT_REMOVAL_RATIO, LINK_TEXT_REMOVAL_RATIO_DEF));
        
        ignoreButtonTags = (mSettings.getProperty(IGNORE_BUTTON_TAGS, IGNORE_BUTTON_TAGS_DEF)).equals("true");
        
        ignoreInputTags = (mSettings.getProperty(IGNORE_INPUT_TAGS, IGNORE_INPUT_TAGS_DEF)).equals("true");
        
        ignoreSelectTags = (mSettings.getProperty(IGNORE_SELECT_TAGS, IGNORE_SELECT_TAGS_DEF)).equals("true");
        
        ignoreNoscriptTags = (mSettings.getProperty(IGNORE_NOSCRIPT_TAGS, IGNORE_NOSCRIPT_TAGS_DEF)).equals("true");
        
        ignoreCellWidth = (mSettings.getProperty(IGNORE_CELL_WIDTH, IGNORE_CELL_WIDTH_DEF)).equals("true");
        
        ignoreAds = (mSettings.getProperty(IGNORE_ADS, IGNORE_ADS_DEF)).equals("true");
        
        onlyText = (mSettings.getProperty(ONLY_TEXT, ONLY_TEXT_DEF)).equals("true");
        
        ignoreIFrameTags = (mSettings.getProperty(IGNORE_IFRAME_TAGS, IGNORE_IFRAME_TAGS_DEF)).equals("true");
        
        ignoreDivStyles = (mSettings.getProperty(IGNORE_DIV_STYLES, IGNORE_DIV_STYLES_DEF)).equals("true");
        
        ignoreLCOnlyLinksAndText = (mSettings.getProperty(LC_ONLY_LINKS_AND_TEXT, LC_ONLY_LINKS_AND_TEXT_DEF)).equals("true");
        
        displayImageAlts = (mSettings.getProperty(DISPLAY_IMAGE_ALTS, DISPLAY_IMAGE_ALTS_DEF)).equals("true");
        
        displayImageLinkAlts = (mSettings.getProperty(DISPLAY_IMAGE_LINK_ALTS, DISPLAY_IMAGE_LINK_ALTS_DEF)).equals("true");
        
        removeEmptyTables = (mSettings.getProperty(REMOVE_EMPTY_TABLES, REMOVE_EMPTY_TABLES_DEF)).equals("true");
        
        limitLinebreaks = (mSettings.getProperty(LIMIT_LINEBREAKS, LIMIT_LINEBREAKS_DEF)).equals("true");
        
        maxLinebreaks = Integer.parseInt(mSettings.getProperty(MAX_LINEBREAKS, MAX_LINEBREAKS_DEF));
        
        substanceButton = (mSettings.getProperty(SUBSTANCE_BUTTON, SUBSTANCE_BUTTON_DEF)).equals("true");
        
        substanceForm = (mSettings.getProperty(SUBSTANCE_FORM, SUBSTANCE_FORM_DEF)).equals("true");
        
        substanceIFrame = (mSettings.getProperty(SUBSTANCE_IFRAME, SUBSTANCE_IFRAME_DEF)).equals("true");
        
        substanceImage = (mSettings.getProperty(SUBSTANCE_IMAGE, SUBSTANCE_IMAGE_DEF)).equals("true");
        
        substanceInput = (mSettings.getProperty(SUBSTANCE_INPUT, SUBSTANCE_INPUT_DEF)).equals("true");
        
        substanceLinks = (mSettings.getProperty(SUBSTANCE_LINKS, SUBSTANCE_LINKS_DEF)).equals("true");
        
        substanceMinTextLength = Integer.parseInt(mSettings.getProperty(SUBSTANCE_MIN_TEXT_LENGTH, SUBSTANCE_MIN_TEXT_LENGTH_DEF));
        
        substanceSelect = (mSettings.getProperty(SUBSTANCE_SELECT, SUBSTANCE_SELECT_DEF)).equals("true");
        
        substanceTextarea = (mSettings.getProperty(SUBSTANCE_TEXTAREA, SUBSTANCE_TEXTAREA_DEF)).equals("true");
        
        addLinksToBottom = (mSettings.getProperty(ADD_LINKS_TO_BOTTOM, ADD_LINKS_TO_BOTTOM_DEF)).equals("true");
        
        ignoreEmbedTags = (mSettings.getProperty(IGNORE_EMBED_TAGS, IGNORE_EMBED_TAGS_DEF)).equals("true");
        
    }//loadSettings
    
    
    
    private void saveProperties() {
        
        mSettings.setProperty(IGNORE_TEXT_LINKS, Boolean.toString(ignoreTextLinks));
        
        mSettings.setProperty(IGNORE_IMAGE_LINKS, Boolean.toString(ignoreImageLinks));
        
        mSettings.setProperty(IGNORE_IMAGES, Boolean.toString(ignoreImages));
        
        mSettings.setProperty(IGNORE_SCRIPTS, Boolean.toString(ignoreScripts));
        
        mSettings.setProperty(IGNORE_STYLES, Boolean.toString(ignoreStyles));
        
        mSettings.setProperty(IGNORE_FORMS, Boolean.toString(ignoreForms));
        
        mSettings.setProperty(IGNORE_META, Boolean.toString(ignoreMeta));
        
        mSettings.setProperty(IGNORE_LINK_CELLS, Boolean.toString(ignoreLinkCells));
        
        mSettings.setProperty(LC_IGNORE_IMAGE_LINKS, Boolean.toString(ignoreLCImageLinks));
        
        mSettings.setProperty(LC_IGNORE_TEXT_LINKS, Boolean.toString(ignoreLCTextLinks));
        
        mSettings.setProperty(LINK_TEXT_REMOVAL_RATIO, Double.toString(linkTextRatio));
        
        mSettings.setProperty(IGNORE_BUTTON_TAGS, Boolean.toString(ignoreButtonTags));
        
        mSettings.setProperty(IGNORE_INPUT_TAGS, Boolean.toString(ignoreInputTags));
        
        mSettings.setProperty(IGNORE_SELECT_TAGS, Boolean.toString(ignoreSelectTags));
        
        mSettings.setProperty(IGNORE_NOSCRIPT_TAGS, Boolean.toString(ignoreNoscriptTags));
        
        mSettings.setProperty(IGNORE_CELL_WIDTH, Boolean.toString(ignoreCellWidth));
        
        mSettings.setProperty(IGNORE_ADS, Boolean.toString(ignoreAds));
        
        mSettings.setProperty(ONLY_TEXT, Boolean.toString(onlyText));
        
        mSettings.setProperty(IGNORE_IFRAME_TAGS, Boolean.toString(ignoreIFrameTags));
        
        mSettings.setProperty(IGNORE_DIV_STYLES, Boolean.toString(ignoreDivStyles));
        
        mSettings.setProperty(LC_ONLY_LINKS_AND_TEXT, Boolean.toString(ignoreLCOnlyLinksAndText));
        
        mSettings.setProperty(DISPLAY_IMAGE_ALTS, Boolean.toString(displayImageAlts));
        
        mSettings.setProperty(DISPLAY_IMAGE_LINK_ALTS, Boolean.toString(displayImageLinkAlts));
        
        mSettings.setProperty(REMOVE_EMPTY_TABLES, Boolean.toString(removeEmptyTables));
        
        mSettings.setProperty(LIMIT_LINEBREAKS, Boolean.toString(limitLinebreaks));
        
        mSettings.setProperty(MAX_LINEBREAKS, Integer.toString(maxLinebreaks));
        
        mSettings.setProperty(SUBSTANCE_BUTTON, Boolean.toString(substanceButton));
        
        mSettings.setProperty(SUBSTANCE_FORM, Boolean.toString(substanceForm));
        
        mSettings.setProperty(SUBSTANCE_IFRAME, Boolean.toString(substanceIFrame));
        
        mSettings.setProperty(SUBSTANCE_IMAGE, Boolean.toString(substanceImage));
        
        mSettings.setProperty(SUBSTANCE_INPUT, Boolean.toString(substanceInput));
        
        mSettings.setProperty(SUBSTANCE_LINKS, Boolean.toString(substanceLinks));
        
        mSettings.setProperty(SUBSTANCE_MIN_TEXT_LENGTH, Integer.toString(substanceMinTextLength));
        
        mSettings.setProperty(SUBSTANCE_SELECT, Boolean.toString(substanceSelect));
        
        mSettings.setProperty(SUBSTANCE_TEXTAREA, Boolean.toString(substanceTextarea));
        
        mSettings.setProperty(ADD_LINKS_TO_BOTTOM, Boolean.toString(addLinksToBottom));
        
        mSettings.setProperty(IGNORE_EMBED_TAGS, Boolean.toString(ignoreEmbedTags));
        
    }
    
    
    
    /**
     *
     * Save the settings file
     *
     */
    
    public void saveSettings() {
        
        saveProperties();
        
        
        
        try {
            
            mSettings.store(new FileOutputStream(new File(mSettingsFile)), "Content Extractor Settings File");
            
        }
        
        catch (Exception e) {
            
            e.printStackTrace();
            
        }
        
    }
    
    
    
    /**
     *
     * Extracts the content of the html page based on the settings
     *
     */
    
    public void extractContent() {
        
        HTMLParser parser = new HTMLParser();
        
        try {
            
            //Create the input source using the ISO-8859-1 character set
            
            InputStreamReader reader = new InputStreamReader(mIn, "ISO-8859-1");
            
            parser.parse(new InputSource(reader));
            
            
            
            mTree = parser.getDocument();
            
            
            
            extract(mTree);
            
            
            
            //Appends the links to the bottom of the page
            
            if (addLinksToBottom) addEnqueuedLinks();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            
        }
        
    }
    
    /**
     * Extracts content and returns text only without changing settings
     */
    public void extractContentAsText() {
        String lastSetting = getSetting(ContentExtractor.ONLY_TEXT);
        extractContent();
        changeSetting(ONLY_TEXT, lastSetting);
    }
    
    
    /**
     *
     * A recursive algorithm that checks through a node's children and
     *
     * filters out what it wants
     *
     * @param iNode the node to start checking
     *
     */
    
    private void extract(Node iNode) {
        
        NodeList children = iNode.getChildNodes();
        
        if ( children != null ) {
            
            int len = children.getLength();
            
            for ( int i = 0; i < len; i++ ) {
                
                filterNode(children.item(i));
                
            }
            
        }
        
    }
    
    
    
    /**
     *
     * Examines a node and determines if it should be included in the
     *
     * extracted DOM tree
     *
     * @param iNode the node to filter
     *
     */
    
    private void filterNode(Node iNode) {
        
        //Boolean that determines if the the children of the node should be filtered
        
        mCheckChildren = true;
        
        
        
        //Put the node through the sequence of filters
        
        passThroughFilters(iNode);
        
        
        
        if (mCheckChildren) filterChildren(iNode);
        
    }//filterNode
    
    
    
    /**
     *
     * Passes a node through a set of filters
     *
     * @param iNode the node to filter
     *
     */
    
    private void passThroughFilters(Node iNode) {
        
        //Check to see if the node is a Text node or an element node and
        
        //act accordingly
        
        
        
        int type = iNode.getNodeType();
        
        Node parent = iNode.getParentNode();
        
        
        
        //Get the attributes of the node
        
        NamedNodeMap attr = iNode.getAttributes();
        
        
        
        //Element node
        
        if (type == Node.ELEMENT_NODE) {
            
            String name = iNode.getNodeName();
            
            //================================================================
            // Set of conditions that just check the nodes without editing or
            // deleting them
            //================================================================
            
            //Any type of link is encountered
            if (isLink(iNode))
                recordLink(iNode);
            if (isImage(iNode));
                recordImage(iNode);
            
            
            //================================================================
            
            // Set of conditions that edit the nodes but don't delete them
            
            //================================================================
            
            
            
            //<TD|TABLE width=*> removes widths
            
            if ((name.equalsIgnoreCase("TD") || name.equalsIgnoreCase("TABLE"))&& ignoreCellWidth) {
                
                if (hasAttribute(iNode, "width")) removeAttribute(iNode, "width");
                
            }//if
            
            
            
            //<DIV style=*> removes style
            
            if (name.equalsIgnoreCase("DIV") && ignoreDivStyles) {
                
                if (hasAttribute(iNode, "style")) removeAttribute(iNode, "style");
                
            }//if
            
            
            
            //================================================================
            
            //Set of conditionals determining what to ignore and not to ignore
            
            // (Conditions that DELETE nodes from the DOM tree)
            
            //================================================================
            
            
            
            if (isAdLink(iNode) && ignoreAds) {
                
                parent.removeChild(iNode);
                
                mCheckChildren = false;
                
            }
            
            //<TD> with Link/Text Ratio higher than threshold
            
            else if (name.equalsIgnoreCase("TD") && ignoreLinkCells) {
                
                testRemoveCell(iNode);
                
            }
            
            //<A HREF> with no Images
            
            else if (isTextLink(iNode) && ignoreTextLinks) {
                
                parent.removeChild(iNode);
                
                if (addLinksToBottom) enqueueLink(iNode);
                
                mCheckChildren = false;
                
            }
            
            //<A HREF> with Images
            
            else if (isImageLink(iNode) && ignoreImageLinks) {
                
                if (displayImageLinkAlts) {
                    
                    Node alt = null;
                    
                    boolean image = isImage(iNode);
                    
                    
                    
                    //Make sure the image link is the image
                    
                    if (image) alt = createImageLinkAltNode(iNode);
                    
                    if (alt != null) {
                        
                        Node replaced = parent.getParentNode().insertBefore(alt, iNode.getParentNode());
                        
                    }//if
                    
                    
                    
                    //Remove the image and the link
                    
                    if (image) {
                        
                        parent.removeChild(iNode);
                        
                        
                        
                        //Only remove the link if there are no more children
                        
                        //to prevent NullPointerExceptions
                        
                        if (!parent.hasChildNodes())
                            
                            parent.getParentNode().removeChild(parent);
                        
                    }
                    
                }//if
                
                else
                    
                    parent.removeChild(iNode);
                
            }
            
            //<IMG*>
            
            else if (name.equalsIgnoreCase("IMG") && ignoreImages && !isImageLink(iNode)) {
                
                if (displayImageAlts) {
                    
                    
                    
                    Node alt = createAltNode(iNode);
                    
                    if (alt != null) {
                        
                        Node replaced = parent.insertBefore(alt, iNode);
                        
                    }//if
                    
                }//if
                
                
                
                parent.removeChild(iNode);
                
            }
            
            //<SCRIPT>
            
            else if (name.equalsIgnoreCase("SCRIPT") && ignoreScripts) {
                
                parent.removeChild(iNode);
                
                mCheckChildren = false;
                
            }
            
            //<NOSCRIPT>
            
            else if (name.equalsIgnoreCase("NOSCRIPT") && ignoreNoscriptTags) {
                
                parent.removeChild(iNode);
                
                mCheckChildren = false;
                
            }
            
            //<NOSCRIPT> removal and save children
            
            else if (name.equalsIgnoreCase("NOSCRIPT") && ignoreScripts) {
                
                if (iNode.hasChildNodes()) {
                    
                    Node current = iNode.getFirstChild();
                    
                    while (current != null) {
                        
                        Node next = current.getNextSibling();
                        
                        //reinsert child before NOSCRIPT node
                        
                        parent.insertBefore(current, iNode);
                        
                        current = next;
                        
                    }//while
                    
                }//if
                
                
                
                parent.removeChild(iNode);
                
            }//else if
            
            //<STYLE>
            
            else if (name.equalsIgnoreCase("STYLE") && ignoreStyles) {
                
                parent.removeChild(iNode);
                
                mCheckChildren = false;
                
            }
            
            //<META>
            
            else if (name.equalsIgnoreCase("META") && ignoreMeta) {
                
                parent.removeChild(iNode);
                
                mCheckChildren = false;
                
            }
            
            //<FORM>
            
            else if (name.equalsIgnoreCase("FORM") && ignoreForms) {
                
                parent.removeChild(iNode);
                
                mCheckChildren = false;
                
            }
            
            //<INPUT>
            
            else if (name.equalsIgnoreCase("INPUT") && ignoreInputTags) {
                
                parent.removeChild(iNode);
                
                mCheckChildren = false;
                
            }
            
            //<BUTTON>
            
            else if (name.equalsIgnoreCase("BUTTON") && ignoreButtonTags) {
                
                parent.removeChild(iNode);
                
                mCheckChildren = false;
                
            }
            
            //<SELECT>
            
            else if (name.equalsIgnoreCase("SELECT") && ignoreSelectTags) {
                
                parent.removeChild(iNode);
                
                mCheckChildren = false;
                
            }
            
            //<IFRAME>
            
            else if (name.equalsIgnoreCase("IFRAME") && ignoreIFrameTags) {
                
                parent.removeChild(iNode);
                
                mCheckChildren = false;
                
            }
            
            //<TABLE>
            
            else if (name.equalsIgnoreCase("TABLE") && removeEmptyTables) {
                
                //Call method that removes empty tables
                
                removeEmptyTables(iNode);
                
                mCheckChildren = false;
                
            }//else if
            
            //<EMBED>
            
            else if (name.equalsIgnoreCase("EMBED") && ignoreEmbedTags) {
                
                parent.removeChild(iNode);
                
                mCheckChildren = false;
                
            }//else if
            
            //<BODY>
            
            else if (name.equalsIgnoreCase("BODY")) mBodyNode = iNode;
            
        }//if
        
        
        
        //Text node
        
        else if (type == Node.TEXT_NODE) {
            
            String value = iNode.getNodeValue();
            
            
            
            //================================================================
            
            //Set of conditions determining what text to ignore
            
            //================================================================
            
            
            
            //none so far
            
            
            
        }//else if
        
    }
    
    
    
    /**
     *
     * Filter child nodes
     *
     * @param iNode the node to filter the children
     *
     */
    
    private void filterChildren(Node iNode) {
        
        if (iNode.hasChildNodes()) {
            
            Node next = iNode.getFirstChild();
            
            
            
            while (next != null) {
                
                Node current = next;
                
                next = current.getNextSibling();
                
                filterNode(current);
                
            }
            
        }
        
    }//filterChildren
    
    
    
    /**
     *
     * Removes empty tables
     *
     * @param iNode the table node to examine
     *
     */
    
    private void removeEmptyTables(Node iNode) {
        
        //First filter the children but check for
        
        //undeleted nodes
        
        if (iNode.hasChildNodes()) {
            
            Node next = iNode.getFirstChild();
            
            
            
            while (next != null) {
                
                Node current = next;
                
                next = current.getNextSibling();
                
                filterNode(current);
                
            }//while
            
        }//if
        
        
        
        //Check to see if the table is actually empty
        
        //but reset length recognizer
        
        lengthForTableRemover = 0;
        
        boolean empty = processEmptyTable(iNode);
        
        
        
        if (empty) iNode.getParentNode().removeChild(iNode);
        
    }//removeEmptyTables
    
    
    
    /**
     *
     * Recursively check children nodes to see if the table is empty
     *
     * @param iNode the node to recursively check.
     *
     * @return true if the nodes are empty, false if they are not
     *
     */
    
    private boolean processEmptyTable(Node iNode) {
        
        //The variable that determines if the table is empty
        
        boolean empty = true;
        
        
        
        //Determine the type of the node
        
        int type = iNode.getNodeType();
        
        String name = iNode.getNodeName();
        
        
        
        //If it is an element
        
        if (type == Node.ELEMENT_NODE) {
            
            //Check to make sure if there are any elements that have
            
            //substance according to what settings are set
            
            if (name.equalsIgnoreCase("IMG") && substanceImage) empty = false;
            
            if (name.equalsIgnoreCase("A") && substanceLinks) empty = false;
            
            if (name.equalsIgnoreCase("BUTTON") && substanceButton) empty = false;
            
            if (name.equalsIgnoreCase("FORM") && substanceForm) empty = false;
            
            if (name.equalsIgnoreCase("IFRAME") && substanceIFrame) empty = false;
            
            if (name.equalsIgnoreCase("INPUT") && substanceInput) empty = false;
            
            if (name.equalsIgnoreCase("SELECT") && substanceSelect) empty = false;
            
            if (name.equalsIgnoreCase("TEXTAREA") && substanceTextarea) empty = false;
            
        }//if
        
        else if (type == Node.TEXT_NODE) {
            
            //Trim the text and make sure there is no more substance
            
            lengthForTableRemover += iNode.getNodeValue().trim().length();
            
            if (lengthForTableRemover >= substanceMinTextLength) empty = false;
            
        }//else if
        
        
        
        //Process the children
        
        if (iNode.hasChildNodes()) {
            
            Node next = iNode.getFirstChild();
            
            
            
            while (next != null && empty) {
                
                Node current = next;
                
                next = current.getNextSibling();
                
                empty = processEmptyTable(current);
                
            }//while
            
        }//if
        
        
        
        return empty;
        
    }//processEmptyTable
    
    
    
    /**
     *
     * Creates a new node from an image link node that creates a link to
     *
     * the image and the target of the image link.
     *
     * @param iNode the <IMG> node that is within the <A> tag
     *
     * @return the new node or null if something went wrong
     *
     */
    
    private Node createImageLinkAltNode(Node iNode) {
        
        //Make sure it is an image link and an image
        
        if (!isImage(iNode)) return null;
        
        if (!isImageLink(iNode)) return null;
        
        
        
        //Determine if there is an ALT tag
        
        String altTag = "";
        
        Node attr = iNode.getAttributes().getNamedItem("alt");
        
        if (attr == null) altTag = "-Link-";
        
        else if (attr.getNodeValue().trim() == "") altTag = "-Link-";
        
        else altTag = attr.getNodeValue();
        
        
        
        //Determine the source of the image
        
        String imageSource = "";
        
        Node attrSource = iNode.getAttributes().getNamedItem("src");
        
        if (attrSource == null) return null;
        
        else if (attrSource.getNodeValue().trim() == "") return null;
        
        else imageSource = attrSource.getNodeValue();
        
        
        
        //Determine the href of the link
        
        String linkHref = "";
        
        Node link = iNode.getParentNode();
        
        linkHref = link.getAttributes().getNamedItem("href").getNodeValue();
        
        if (linkHref == null) return null;
        
        else if (linkHref.trim() == "") return null;
        
        
        
        //CONSTRUCT REPLACEMENT NODE
        
        Element parent = mTree.createElement("B");
        
        Element italic = mTree.createElement("I");
        
        
        
        Element imageLink = mTree.createElement("A");
        
        imageLink.setAttribute("href", imageSource);
        
        
        
        Element altLink = mTree.createElement("A");
        
        altLink.setAttribute("href", linkHref);
        
        
        
        Node openBracket = mTree.createTextNode("[");
        
        Node closeBracket = mTree.createTextNode("]");
        
        Node seperator = mTree.createTextNode(" | ");
        
        Node imageLinkText = mTree.createTextNode("Image");
        
        Node altLinkText = mTree.createTextNode(altTag);
        
        
        
        //Link together nodes
        
        parent.appendChild(openBracket);
        
        parent.appendChild(imageLink);
        
        imageLink.appendChild(imageLinkText);
        
        parent.appendChild(seperator);
        
        parent.appendChild(italic);
        
        italic.appendChild(altLink);
        
        altLink.appendChild(altLinkText);
        
        parent.appendChild(closeBracket);
        
        
        
        //Return node
        
        return parent;
        
    }//createImageLinkAltNode
    
    
    
    /**
     *
     * Creates a new node that creates a link to an image node using ALT text
     *
     * @param iNode the image node
     *
     * @return the node to add to the DOM tree or null if the node isn't an image
     *
     * or doesn't have an ALT attribute.
     *
     */
    
    private Node createAltNode(Node iNode) {
        
        if (!isImage(iNode)) return null;
        
        
        
        //Determine if there is an ALT tag
        
        Node attr = iNode.getAttributes().getNamedItem("alt");
        
        if (attr == null) return null;
        
        if (attr.getNodeValue().trim() == "") return null;
        
        
        
        //Determine if there is a src
        
        Node attrLink = iNode.getAttributes().getNamedItem("src");
        
        if (attrLink == null) return null;
        
        
        
        //Create new link node
        
        Element altNode = mTree.createElement("A");
        
        
        
        //Add text
        
        altNode.setAttribute("href", attrLink.getNodeValue());
        
        //Bold Element
        
        Node bold = mTree.createElement("B");
        
        Node textNode = mTree.createTextNode("[" + attr.getNodeValue() + "]");
        
        bold.appendChild(textNode);
        
        altNode.appendChild(bold);
        
        altNode.setNodeValue("");
        
        
        
        return altNode;
        
    }//getAltNode
    
    
    
    /**
     *
     * Determines if a node has a link to an ad
     *
     * @param iNode the node to check for ads
     *
     * @return true if the node is a link to an ad, or false if it isn't
     *
     */
    
    private boolean isAdLink(Node iNode) {
        
        String attr = "";
        
        
        
        if (hasAttribute(iNode, "href")) attr = "href";
        
        else if (hasAttribute(iNode, "src")) attr = "src";
        
        
        
        //Doesn't had the required attributes
        
        if (attr.equals("")) return false;
        
        
        
        //Get the address of the potential ad
        
        Node attrNode = iNode.getAttributes().getNamedItem(attr);
        
        String address = attrNode.getNodeValue();
        
        
        
        try {
            
            URL addressURL = new URL(address);
            
            String host = addressURL.getHost();
            
            
            
            if (mAdServers.get(host) != null) return true;
            
        } catch (Exception e) {
            
            //Don't do anything because if the URL is malformed, it
            
            //probably doesn't point towards an advertisement domain
            
        }//catch
        
        
        
        return false;
        
    }//isAdLink
    
    
    
    /**
     *
     * Removes an attribute if the attrbiute exists from an Element node
     *
     * @param iNode the node
     *
     * @param iAttr the name of the attribute
     *
     */
    
    private void removeAttribute(Node iNode, String iAttr) {
        
        iNode.getAttributes().removeNamedItem(iAttr);
        
    }//removeAttribute
    
    
    
    /**
     *
     * Adds an attribute to an Element node
     *
     * @param iNode the node
     *
     * @param iName the name of the attribute
     *
     * @param iValue the value of the attribute
     *
     */
    
    private void addAttribute(Node iNode, String iName, String iValue) {
        
        Attr attr = mTree.createAttribute(iName);
        
        attr.setValue(iValue);
        
        iNode.getAttributes().setNamedItem(attr);
        
    }//addAttribute
    
    
    
    /**
     *
     * Checks to see if an attribute exists in an Element node
     *
     * @param iNode the node
     *
     * @param iAttr the name of the attribute to check for
     *
     * @return true if the attribute exists, false if it doesn't
     *
     */
    
    private boolean hasAttribute(Node iNode, String iAttr) {
        
        Node attr = iNode.getAttributes().getNamedItem(iAttr);
        
        if (attr == null) return false;
        
        else return true;
        
    }//hasAttribute
    
    
    
    /**
     *
     * Removes a table cell if the link ratio is appropriate
     *
     * @param iNode the table cell node
     *
     */
    
    public void testRemoveCell(Node iNode) {
        
        //Ignore if the cell has no children
        
        if (!iNode.hasChildNodes()) return;
        
        
        
        double links;
        
        double words;
        
        int type = ALL;
        
        
        
        if (ignoreLCImageLinks && ignoreLCTextLinks) type = ALL;
        
        else if (ignoreLCImageLinks) type = IMAGE;
        
        else if (ignoreLCTextLinks) type = TEXT;
        
        
        
        //Count up links and words
        
        links = getNumLinks(iNode, type);
        
        words = getNumWords(iNode);
        
        
        
        //Compute the ratio and check for divide by 0
        
        double ratio = 0;
        
        if (words == 0) ratio = linkTextRatio + 1;
        
        else ratio = links/words;
        
        
        
        if (ratio > linkTextRatio) {
            
            Node next = iNode.getFirstChild();
            
            while (next != null) {
                
                Node current = next;
                
                next = current.getNextSibling();
                
                
                
                //Check to see if only text and link nodes should be removed
                
                if (ignoreLCOnlyLinksAndText) {
                    
                    removeLinksAndText(current, type);
                    
                }
                
                else {
                    
                    Node next2 = iNode.getFirstChild();
                    
                    while (next2 != null) {
                        
                        Node current2 = next;
                        
                        next2 = current2.getNextSibling();
                        
                        removeAll(current2);
                        
                    }//while
                    
                    
                    
                    //Don't check the children because they are all removed
                    
                    mCheckChildren = false;
                    
                }//else
                
            }
            
        }
        
    }//testRemoveCell
    
    
    
    /**
     *
     * Recursive function that removes everything
     *
     * @param iNode the node to start removing children from
     *
     */
    
    private void removeAll(Node iNode) {
        
        if (isTextLink(iNode) && addLinksToBottom) {
            
            enqueueLink(iNode);
            
        }
        
        else {
            
            Node next = iNode.getFirstChild();
            
            while (next != null) {
                
                Node current = next;
                
                next = current.getNextSibling();
                
                removeAll(current);
                
            }//while
            
        }//while
        
        
        
        iNode.getParentNode().removeChild(iNode);
        
    }//removeChild
    
    
    
    /**
     *
     * Recursive function that removes links and text nodes
     *
     * @param iNode the node to edit
     *
     * @param iType the type of links to remove
     *
     */
    
    private void removeLinksAndText(Node iNode, int iType) {
        
        if (isLink(iNode) || iNode.getNodeType() == Node.TEXT_NODE) {
            
            if (iType == ALL) iNode.getParentNode().removeChild(iNode);
            
            if (iType == IMAGE && isImageLink(iNode)) iNode.getParentNode().removeChild(iNode);
            
            if (iType == TEXT && !isImageLink(iNode)) iNode.getParentNode().removeChild(iNode);
            
            
            
            if (isTextLink(iNode) && addLinksToBottom) enqueueLink(iNode);
            
        }
        
        else {
            
            Node next = iNode.getFirstChild();
            
            while (next != null) {
                
                Node current = next;
                
                next = current.getNextSibling();
                
                removeLinksAndText(current, iType);
                
            }//while
            
        }//else
        
    }//isDeeperLink
    
    
    
    /**
     *
     * Determines if a domain is an ad domain
     *
     * @param iDomain the the domain to check
     *
     * @return true if the domain is an ad domain, false if it is not.
     *
     */
    
    private boolean isAdDomain(String iDomain) {
        
        if (mAdServers.get(iDomain) == null) return false;
        
        return true;
        
    }//isAdDomain
    
    
    
    /**
     *
     * Counts the number of links from one node downward
     *
     * @param iNode the node to start counting from
     *
     * @param iType the type of links to count.
     *
     * @return the number of links
     *
     */
    
    private double getNumLinks(Node iNode, int iType) {
        
        double links = 0;
        
        
        
        if (iNode.hasChildNodes()) {
            
            Node next = iNode.getFirstChild();
            
            
            
            while (next != null) {
                
                Node current = next;
                
                next = current.getNextSibling();
                
                links += getNumLinks(current, iType);
                
            }
            
        }
        
        
        
        switch (iType) {
            
            case ALL:
                
                if (isLink(iNode)) links++;
                
                break;
                
            case TEXT:
                
                if (isTextLink(iNode)) links++;
                
                break;
                
            case IMAGE:
                
                if (isImageLink(iNode)) links++;
                
                break;
                
        }//switch
        
        
        
        return links;
        
    }//getNumLinks
    
    
    
    /**
     *
     * Checks to see if a node is a link
     *
     * @param iNode the node to check
     *
     * @return true if the node is a link, false if it is not
     *
     */
    
    private boolean isLink(Node iNode) {
        
        //Check to see if the node is a Text node or an element node
        
        int type = iNode.getNodeType();
        
        NamedNodeMap attr = iNode.getAttributes();
        
        
        
        //Element node
        
        if (type == Node.ELEMENT_NODE) {
            
            String name = iNode.getNodeName();
            
            
            
            //Check to see if it is a link
            
            if (name.equalsIgnoreCase("A")) {
                
                for (int i=0; i < attr.getLength(); i++ ) {
                    
                    if (attr.item(i).getNodeName().equalsIgnoreCase("HREF")) {
                        
                        return true;
                        
                    }//if
                    
                }//fot
                
            }//else if
            
        }//if
        
        
        
        return false;
        
    }
    
    
    
    /**
     *
     * Checks to see if a node is a link with an image as the link or if the
     *
     * node is an image, it checks if it is a link
     *
     * @param iNode the node to check
     *
     * @return true if the node is a link with an image, false if it is not
     *
     */
    
    private boolean isImageLink(Node iNode) {
        
        boolean imageLink = false;
        
        
        
        //Check to see if the node is a link
        
        if (isLink(iNode)) {
            
            
            
            //Check to see if the children have an image in it
            
            if (iNode.hasChildNodes()) {
                
                Node next = iNode.getFirstChild();
                
                
                
                while (next != null && !imageLink) {
                    
                    Node current = next;
                    
                    next = current.getNextSibling();
                    
                    if (isImage(current)) imageLink = true;
                    
                }//while
                
            }//if
            
        }//if
        
        //If the node is an image, check if its parent is a link
        
        else if (isImage(iNode)) {
            
            if (isLink(iNode.getParentNode())) imageLink = true;
            
        }//else if
        
        
        
        return imageLink;
        
    }//isImageLink
    
    
    
    /**
     *
     * Checks to see if a node is an image
     *
     * @param iNode the node to check
     *
     * @return true if the node is an image, false if it is not
     *
     */
    
    private boolean isImage(Node iNode) {
        
        boolean image = false;
        
        
        
        //Check to see if the node is an image
        
        int type = iNode.getNodeType();
        
        if (type == Node.ELEMENT_NODE) {
            
            if (iNode.getNodeName().equalsIgnoreCase("IMG")) image = true;
            
        }//if
        
        
        
        return image;
        
    }
    
    
    
    /**
     *
     * Determines if a link is a text link
     *
     * @param iNode the node to analyze
     *
     * @return true if the node is a text link and false if it is not.
     *
     */
    
    private boolean isTextLink(Node iNode) {
        
        return !isImageLink(iNode) && isLink(iNode);
        
    }//isTextLink
    
    
    
    /**
     *
     * Counts the number of links from one node downward
     *
     * @param iNode the node to start counting from
     *
     * @return the number of links
     *
     */
    
    private double getNumWords(Node iNode) {
        
        double words = 0;
        
        
        
        if (iNode.hasChildNodes()) {
            
            Node next = iNode.getFirstChild();
            
            
            
            while (next != null) {
                
                Node current = next;
                
                next = current.getNextSibling();
                
                
                
                //If it is a link, don't go any deeper into it
                
                if (!isLink(current)) words += getNumWords(current);
                
            }
            
        }
        
        
        
        //Check to see if the node is a Text node or an element node
        
        int type = iNode.getNodeType();
        
        
        
        //Text node
        
        if (type == Node.TEXT_NODE) {
            
            String content = iNode.getNodeValue();
            
            words += ((double)content.length())/LETTERS_PER_WORD;
            
        }//if
        
        
        
        return words;
        
    }//getNumLinks
    
    
    
    /**
     * Prepares a link node to be added to the bottom of the page by adding
     * it to the Hashtable
     * @param iLinkNode the link node to add o the bottom of the page
     */
    private void enqueueLink(Node iLinkNode) {
        //Make sure the node is a link
        if (!isTextLink(iLinkNode)) return;
        
        //Get the source of the text link
        String source = iLinkNode.getAttributes().getNamedItem("href").getNodeValue();
        String text = iLinkNode.getFirstChild().getNodeValue();
        
        if (source != null && text != null) {       
            mLinksSource.add(source);
            mLinksText.add(text);
        }  
    }//enqueueLink
    
    /**
     * Records the presence of a link
     * @param iLinkNode the link node 
     */
    private void recordLink(Node iLinkNode) {
        //Make sure the node is a link
        if (!isLink(iLinkNode)) return;
        
        //Get the source of the text link
        String source = iLinkNode.getAttributes().getNamedItem("href").getNodeValue();
        String text = iLinkNode.getFirstChild().getNodeValue();
        
        if (source != null) {       
            mLinksSource.add(source);
            mLinksText.add(text);
        }  
    }//recordLink
    
    /**
     * Records the presence of an image
     * @param iImageNode the image node
     */
    private void recordImage(Node iImageNode) {
        //Make sure the node is an image
        if (!isImage(iImageNode)) return;
        
        //get source of the image
        String source = iImageNode.getAttributes().getNamedItem("src").getNodeValue();
        
        if (source != null) {
            mImagesSource.add(source);
        }
    }
    
    /**
     * Returns a linked list containing all the image sources
     * @return a linked list containing all the image sources
     */
    public LinkedList getImageSources() {
        return mImagesSource;
    }
    
    /**
     * Returns a linked list containing all the link sources
     * @return a linked list containing all the link sources
     */
    public LinkedList getLinkSources() {
        return mLinksSourceAll;
    }
    
    /**
     * Returns a linked list containing all the link text
     * @return a linked list containing all the link text
     */
    public LinkedList getLinkText() {
        return mLinksTextAll;
    }
    
    /**
     *
     * Add enqueued links to bottom of page
     *
     */
    
    private void addEnqueuedLinks() {
        
        //Make sure the body node isn't null
        
        if (mBodyNode == null) return;
        
        
        
        //Make sure there are links enqueued
        
        if (mLinksSource.size() == 0) return;
        
        if (mLinksText.size() == 0) return;
        
        
        
        //Start adding formating
        
        Element center = mTree.createElement("CENTER");
        
        Element table = mTree.createElement("TABLE");
        
        table.setAttribute("cellpadding", "5");
        
        table.setAttribute("width", "100%");
        
        Element tablerow = mTree.createElement("TR");
        
        Element tablecell = mTree.createElement("TD");
        
        tablecell.setAttribute("bgcolor", "white");
        
        Element headerTag = mTree.createElement("H3");
        
        Node header = mTree.createTextNode("Removed Links:");
        
        
        
        //Append them
        
        mBodyNode.appendChild(table);
        
        table.appendChild(tablerow);
        
        tablerow.appendChild(tablecell);
        
        tablecell.appendChild(center);
        
        center.appendChild(headerTag);
        
        headerTag.appendChild(header);
        
        
        
        Iterator itrSource = mLinksSource.listIterator(0);
        
        Iterator itrText = mLinksText.listIterator(0);
        
        
        
        //Add links
        
        while (itrSource.hasNext() && itrText.hasNext()) {
            
            String source = (String)itrSource.next();
            
            String text = (String)itrText.next();
            
            
            
            Element link = mTree.createElement("A");
            
            link.setAttribute("href", source);
            
            link.setAttribute("style", "color: blue");
            
            link.appendChild(mTree.createTextNode(text));
            
            tablecell.appendChild(link);
            
            tablecell.appendChild(mTree.createElement("BR"));
            
        }//while
        
        
        
        //Purge the enqueued Links
        
        mLinksSource = new LinkedList();
        
        mLinksText = new LinkedList();
        
    }//addEnqueuedLinks
    
    
    
    /** Returns the Document object
     *
     * @return the Document object of the DOM tree representing
     *
     * the HTML file
     *
     */
    
    public Document getTree() {
        
        return mTree;
        
    }
    
    
    
    /**
     *
     * Prints only the text without any of the tags of the DOM tree
     *
     * @param iOutputStream the output stream
     *
     */
    
    public void textPrint(OutputStream iOutputStream) {
        
        PrintWriter output = new PrintWriter(iOutputStream);
        
        textPrint(mTree, output);
        
        output.close();
        
    }//textPrint
    
    
    
    /**
     *
     * Prints only the text without any of the tags of the DOM tree
     *
     * @param iDOMTree the DOM Document module to print without any tags
     *
     * @param iWriter the PrintWriter
     *
     */
    
    private void textPrint(Node iDOMTree, PrintWriter iWriter) {
        
        //Print child nodes first
        
        if (iDOMTree.hasChildNodes()) {
            
            Node next = iDOMTree.getFirstChild();
            
            
            
            while (next != null) {
                
                Node current = next;
                
                next = current.getNextSibling();
                
                
                
                //=====Filter out what is not really text=====//
                
                String name = current.getNodeName();
                
                boolean valid = true;
                
                
                
                //Styles should not be treated as text
                
                if (name.equalsIgnoreCase("STYLE")) valid = false;
                
                //Scripts should not be treated as text either
                
                else if (name.equalsIgnoreCase("SCRIPT")) valid = false;
                
                
                
                //============================================//
                
                
                
                //Perform recursive function
                
                if (valid) textPrint(current, iWriter);
                
            }//while
            
        }//if
        
        
        
        //Check to see if the node is a Text node or an element node
        
        int type = iDOMTree.getNodeType();
        
        
        
        //Element node
        
        if (type == Node.ELEMENT_NODE) {
            
            //if the node is <BR>, then print a line break
            
            if (iDOMTree.getNodeName().equalsIgnoreCase("BR")) {
                
                flush(iWriter);
                
                
                
            }
            
        }//else if
        
        
        
        //Text node
        
        else if (type == Node.TEXT_NODE) {
            
            //Print the text nodes to the output stream.
            
            if (!(iDOMTree.getNodeValue().trim().equals(""))) {
                
                textPrintBuffer += iDOMTree.getNodeValue();
                
                
                
            }
            
        }//if
        
    }//textPrint
    
    
    
    /**
     *
     * Flushs the buffered line and prints it out depending on
     *
     * the number of consecutive blank lines. This method also keeps track of
     *
     * the number of consecutive blank lines.
     *
     * @param iWriter the PrintWriter to flush the buffer to
     *
     */
    
    private void flush(PrintWriter iWriter) {
        
        boolean blank = false;
        
        
        
        //Check to see if the buffered line is blank
        
        if (textPrintBuffer.trim().length() == 0) blank = true;
        
        
        
        //Make sure there are not too many consecutive blank lines if necessary
        
        if (limitLinebreaks) {
            
            if (blank && numberBlankLines < maxLinebreaks) {
                
                iWriter.println(textPrintBuffer);
                
                numberBlankLines++;
                
            }//if
            
            else if (!blank)
                
                iWriter.println(textPrintBuffer);
            
        }//if
        
        else
            
            iWriter.println(textPrintBuffer);
        
        
        
        //Reset the numberBlankLines if the line is not blank
        
        if (!blank) numberBlankLines = 0;
        
        textPrintBuffer = "";
        
    }//flush
    
    
    
    /**
     *
     * Pretty prints the HTML to an OutputStream
     *
     * @param iNode the Document to start printing from
     *
     * @param iOut the output stream to print to.
     *
     */
    
    public void prettyPrint(Document iNode, OutputStream iOut) {
        
        //Create formating that will indent and print with the proper
        
        //method specified by the Document object.
        
        OutputFormat format = null;
	
	//according to the java documentation, all compliant JVM's should support the
	//ISO-8859-1 encoding.
	format = new OutputFormat(iNode, "ISO-8859-1", true);
        
        //Get the printer
        HTMLSerializer printer = new HTMLSerializer(iOut, format);
        
        try {
            
            printer.serialize(iNode);
            
	} catch (UnsupportedEncodingException uue) {
	    
	    System.out.println("Error: your system does not support the ISO-8859-1 encoding.");
	    uue.printStackTrace();
        
	} catch (Exception e) {
            
            e.printStackTrace();
            
        }//catch
        
        
        
    }//prettyPrint
    
    
    
    public static void main(String[] args) {
        
        if (args.length < 2) {
            
            System.out.println("Usage: java ContentExtractor [input file] [output file] {settings file}");
            
            return;
            
        }
        
        
        
        FileInputStream streamIn;
        
        try {
            
            streamIn = new FileInputStream(args[0]);
            
        }
        
        catch (FileNotFoundException e) {
            
            System.out.println("Input File Not Found");
            
            return;
            
        }
        
        catch (SecurityException e) {
            
            System.out.println("Read access denied to Input File");
            
            return;
            
        }
        
        
        
        
        
        ContentExtractor ce;
        
        if (args.length == 2) ce = new ContentExtractor(streamIn);
        
        else ce = new ContentExtractor(args[2], streamIn);
        
        ce.extractContent();
        
        
        
        try {
            
            File output = new File(args[1]);
            
            output.createNewFile();
            
            ce.processNoOverwrite(new File(args[0]),output);
            
        }
        
        catch ( IOException e ) {
            
            System.out.println("IO Exception");
            
            e.printStackTrace();
            
            return;
            
        }
        
    }
    
    
    
    /**
     *
     * This method returns a JPanel that edits the settings for the filter
     *
     * @return a JPanel to edit the settings from.
     *
     */
    
    public ProxyFilterSettings getSettingsGUI() {
        
        return mSettingsGUI;
        
    }
    
    
    
    /**
     *
     * Returns what the content type of the file is.
     *
     * @return the content type
     *
     */
    
    public String getContentType() {
        
        if (onlyText) return CONTENT_TEXT;
        
        else return CONTENT_HTML;
        
    }//getContentType
    
    
    
    /**
     *
     * This method processes a File and returns the processed file for the
     *
     * proxy to use.
     *
     * @return the processed file
     *
     */
    
    public File process(File in) throws IOException {
        
        FileInputStream streamIn = new FileInputStream(in);
        
        mIn = streamIn;
        
        extractContent();
        
        streamIn.close();
        
        
        
        if (!onlyText) {
            
            prettyPrint(mTree, new FileOutputStream(in));
            
        }
        
        else {
            
            textPrint(new FileOutputStream(in));
            
        }
        
        
        
        return in;
        
    }
    
    
    
    /**
     *
     * This method processes a File and returns a new file for the
     *
     * proxy to use. Note: the file is not overwritten
     *
     * @param in the file to process
     *
     * @param out the output file
     *
     */
    
    public File processNoOverwrite(File in, File out) throws IOException {
        
        FileInputStream streamIn = new FileInputStream(in);
        
        mIn = streamIn;
        
        extractContent();
        
        streamIn.close();
        
        
        
        if (!onlyText) {
            
            prettyPrint(mTree, new FileOutputStream(out));
            
        }
        
        else {
            
            textPrint(new FileOutputStream(out));
            
        }
        
        
        
        return out;
        
    }
    
}//ContentExtractor

