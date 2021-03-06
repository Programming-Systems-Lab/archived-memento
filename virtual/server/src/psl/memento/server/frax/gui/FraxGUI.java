package psl.memento.server.frax.gui;

// jdk imports
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.Logger;
import javax.swing.*;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.common.prettywriter.*;
import psl.memento.server.frax.*;
import psl.memento.server.frax.util.MiscUtils;

public class FraxGUI implements ActionListener {
  static {
    MiscUtils.configureLogging();
  }
  
  private static Font kProgramFont = new Font("Monospaced", Font.PLAIN, 12);
  private static int kMaxHistorySize = 20;
  
  private static Logger sLog = Logger.getLogger("psl.memento.server.frax");
  
  private static final String kWarningCouldNotSetDefaultConfig =
    "Could not set the default configuration: ";
  
  private Frax mFrax;
  private JFrame mFrame;
  private JTextArea mResultsText;
  private JWindow mSplashWindow;
  private AddressPanel mAddressPanel;
  private JLabel mStatusLabel;
  private JMenu mMenuToolsAddressBuilder;
  private JMenuItem mMenuBuilderLocalFile;
  private JMenuItem mMenuBuilderFTP;
  private JMenuItem mMenuBuilderDB;
  private JMenuItem mOptionsMenuItem;
  private JFileChooser mExtractFileChooser;
  private OptionsDialog mOptionsDialog;
  private FTPAddressBuilderDialog mFTPAddressBuilderDialog;
  private DBAddressBuilderDialog mDBAddressBuilderDialog;
  
  public static void main(String[] args) {
    new FraxGUI();
  }
  
  private FraxGUI() {
    mSplashWindow = new JWindow();
    
    JLabel splashImageLabel = new JLabel(new ImageIcon(ClassLoader.getSystemResource("etc/frax/frax1.gif")));
    splashImageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));        
    mSplashWindow.getContentPane().add(splashImageLabel, BorderLayout.CENTER);
    
    JProgressBar loadProgress = new JProgressBar(1, 5);
    loadProgress.setStringPainted(true);    
    mSplashWindow.getContentPane().add(loadProgress, BorderLayout.SOUTH);
    mSplashWindow.pack();
    
    centerOnScreen(mSplashWindow);
    mSplashWindow.setVisible(true);
    
    // initialize Frax
    mFrax = Frax.getInstance();
    
    // load configuration data
    loadProgress.setValue(1);
    loadProgress.setString("Loading local extractors and plugs");
            
    try {
      mFrax.setConfiguration(new XMLFraxConfiguration());
    } catch (Exception ex) {            
      sLog.warning(kWarningCouldNotSetDefaultConfig + ex);
    }
    
    FraxConfiguration config = Frax.getInstance().getConfiguration();
    config.setUseMetadataCache(true);
    config.setExtractContentMetadata(true);
    
    // synch with the oracle
    loadProgress.setValue(2);
    loadProgress.setString("Synchronizing with oracle");
        
    try {
      mFrax.synchWithOracle();
    } catch (FraxException ex) {
      JOptionPane.showMessageDialog(mSplashWindow,
        ("Could not synchronize with the oracle server: " + ex), "Warning",
        JOptionPane.WARNING_MESSAGE);
    }
    
    // load persistent RDF model (metadata cache)
    loadProgress.setValue(3);
    loadProgress.setString("Initializing metadata cache");
            
    mFrax.loadPersistentModel();    
    
    // create the GUI
    loadProgress.setValue(4);
    loadProgress.setString("Creating GUI");
    createGUI();
    
    loadProgress.setValue(loadProgress.getMaximum());
    loadProgress.setString("Done");
    mSplashWindow.setVisible(false);
  }
  
  private void createGUI() {
    mFrame = new JFrame("Frax Metadata Extractor - Front End");
    
    mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    buildMenus();
    
    mOptionsDialog = new OptionsDialog(mFrame, true);
    mFTPAddressBuilderDialog = new FTPAddressBuilderDialog(mFrame, true, this);
    mDBAddressBuilderDialog = new DBAddressBuilderDialog(mFrame, true, this);
    centerOnScreen(mOptionsDialog);
    centerOnScreen(mFTPAddressBuilderDialog);
    centerOnScreen(mDBAddressBuilderDialog);
    
    java.awt.Container contentPane = mFrame.getContentPane();
    
    mAddressPanel = new AddressPanel(this);
    contentPane.add(mAddressPanel, BorderLayout.NORTH);
        
    mResultsText = new JTextArea();
    mResultsText.setEditable(false);
    mResultsText.setFont(kProgramFont);
    
    JScrollPane resultsPane = new JScrollPane(mResultsText);
    resultsPane.setPreferredSize(new Dimension(800, 600));
    contentPane.add(resultsPane, BorderLayout.CENTER);
    
    mStatusLabel = new JLabel(" ");
    mStatusLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
    contentPane.add(mStatusLabel, BorderLayout.SOUTH);
    
    // init extract file chooser
    mExtractFileChooser = new JFileChooser();
    mExtractFileChooser.
      setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    mExtractFileChooser.setApproveButtonText("Extract");
    
    mFrame.pack();
    centerOnScreen(mFrame);
    mFrame.setVisible(true);
  }
  
  private void buildMenus() {
    JMenu menu;
    JMenuItem menuItem;
    
    JMenuBar menuBar = new JMenuBar();
    mFrame.setJMenuBar(menuBar);
    
    menu = new JMenu("Tools");
    menu.setMnemonic(KeyEvent.VK_T);
    menuBar.add(menu);
    
    mMenuToolsAddressBuilder = new JMenu("Address Builder");
    mMenuToolsAddressBuilder.setMnemonic(KeyEvent.VK_A);
    menu.add(mMenuToolsAddressBuilder);
    
    mMenuBuilderLocalFile = new JMenuItem("Local File", KeyEvent.VK_L);
    mMenuBuilderLocalFile.addActionListener(this);
    mMenuToolsAddressBuilder.add(mMenuBuilderLocalFile);
    
    mMenuBuilderFTP = new JMenuItem("FTP", KeyEvent.VK_F);
    mMenuBuilderFTP.addActionListener(this);
    mMenuToolsAddressBuilder.add(mMenuBuilderFTP);
    
    mMenuBuilderDB = new JMenuItem("Database", KeyEvent.VK_D);
    mMenuBuilderDB.addActionListener(this);
    mMenuToolsAddressBuilder.add(mMenuBuilderDB);
    
    menu.addSeparator();
    
    mOptionsMenuItem = new JMenuItem("Options", KeyEvent.VK_O);
    mOptionsMenuItem.addActionListener(this);
    menu.add(mOptionsMenuItem);
  }
  
  public JComboBox getAddressTextCombo() {
    return mAddressPanel.mAddressText;
  }
  
  public void actionPerformed(ActionEvent iEvent) {
    Object source = iEvent.getSource();
    
    if (mMenuBuilderLocalFile.equals(source)) {
      int value = mExtractFileChooser.showDialog(mFrame, null);
      if (value == JFileChooser.APPROVE_OPTION) {
        mAddressPanel.mAddressText.setSelectedItem(mExtractFileChooser.
          getSelectedFile().toURI().toString());
      }
    } else if (mOptionsMenuItem.equals(source)) {
      mOptionsDialog.reloadSchemesList();
      mOptionsDialog.reloadTypesList();
      mOptionsDialog.setVisible(true);
    } else if (mMenuBuilderFTP.equals(source)) {
      mFTPAddressBuilderDialog.setVisible(true);
    } else if (mMenuBuilderDB.equals(source)) {
      mDBAddressBuilderDialog.setVisible(true);
    }
  }
  
  private static class AddressPanel extends JPanel implements ActionListener {
    private FraxGUI mFraxGUI;
    private JLabel mAddressLabel;
    private JComboBox mAddressText;
    private JButton mExtractButton;
    private JButton mStopButton;    
    private ExtractionThread mCurrentExtractionThread;
    
    public AddressPanel(FraxGUI iFraxGUI) {
      super();      
      
      mFraxGUI = iFraxGUI;    
      setLayout(new BorderLayout());      
      
      mAddressLabel = new JLabel("Address");
      mAddressLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
      add(mAddressLabel, BorderLayout.WEST);
      
      mAddressText = new JComboBox();      
      mAddressText.setEditable(true);
      mAddressText.setFont(kProgramFont);
      mAddressText.addActionListener(this);
      JPanel addressPanel = new JPanel();
      addressPanel.setLayout(new BorderLayout());
      addressPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
      addressPanel.add(mAddressText, BorderLayout.CENTER);
      add(addressPanel, BorderLayout.CENTER);
      
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BorderLayout());      
      add(buttonPanel, BorderLayout.EAST);
            
      mExtractButton = new JButton("Extract", new ImageIcon(ClassLoader.getSystemResource("etc/frax/Export24.gif")));
      mExtractButton.addActionListener(this);
      mExtractButton.setToolTipText("Extract metadata.");      
      buttonPanel.add(mExtractButton, BorderLayout.WEST);
      
      mStopButton = new JButton("Stop", new ImageIcon(ClassLoader.getSystemResource("etc/frax/Stop24.gif")));
      mStopButton.addActionListener(this);
      mStopButton.setToolTipText("Stop the extraction.");      
      mStopButton.setEnabled(false);
      buttonPanel.add(mStopButton, BorderLayout.EAST);
    }
    
    public void actionPerformed(ActionEvent iEvent) {
      Object source = iEvent.getSource();
      String command = iEvent.getActionCommand();      
      
      if (mExtractButton.equals(source)) {
        execExtract((iEvent.getModifiers() & ActionEvent.SHIFT_MASK) != 0);
      } else if (mAddressText.equals(source)) {
        if ("comboBoxChanged".equals(command)) {
          execExtract(false);
        } else if ("comboBoxEdited".equals(command)) {
          addAddressToHistory();
        }
      } else if (mStopButton.equals(source)) {
        execStop();
      }
    }
    
    private void addAddressToHistory() {
      String address = (String) mAddressText.getSelectedItem();
      if (address == null || address.trim().equals("")) {
        return;
      }
      
      int historySize = mAddressText.getItemCount();
      if (historySize >= kMaxHistorySize) {
        return;
      }
      
      for (int i = 0; i < historySize; i++) {
        if (address.equals(mAddressText.getItemAt(i))) {
          return;
        }
      }
      
      mAddressText.insertItemAt(address, 0);
    }
    
    private void execExtract(boolean iForceReload) {
      String uriString = (String) mAddressText.getSelectedItem();
      if (uriString == null || uriString.trim().equals("")) {        
        return;
      }      
      
      mExtractButton.setEnabled(false);
      mStopButton.setEnabled(true);
      mFraxGUI.mResultsText.setText("");      
      
      mCurrentExtractionThread = new ExtractionThread(uriString, this,
        iForceReload);
      mCurrentExtractionThread.start();
    }
    
    private void execStop() {
      mStopButton.setEnabled(false);
      mExtractButton.setEnabled(true);      
      
      if (mCurrentExtractionThread != null) {
        synchronized (mCurrentExtractionThread) {
          mCurrentExtractionThread.setRunning(false);
          mCurrentExtractionThread = null;
        }
      }
    }
  }
  
  static void centerOnScreen(Component iC) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension cSize = iC.getSize();
    
    iC.setLocation((screenSize.width - cSize.width) / 2,
      (screenSize.height - cSize.height) / 2);
  }
  
  private static class ExtractionThread extends Thread {
    private String mURI;
    private AddressPanel mP;    
    private boolean mRunning;
    private boolean mForceReload;
    
    public ExtractionThread(String iURI, AddressPanel iP,
        boolean iForceReload) {
      super();
      
      mURI = iURI;
      mP = iP;
      mForceReload = iForceReload;
    }
    
    public void run() {
      mRunning = true;
      long timeStarted = System.currentTimeMillis();
      
      try {
        if (!mRunning) {
          return;
        }
        
        URI uri = new URI(mURI);
        if (uri.getScheme() == null) {
          throw new URISyntaxException(mURI,
            "URI must be absolute (scheme element must be present)");
        }
        
        FraxConfiguration config = Frax.getInstance().getConfiguration();                
        
        Model m = Frax.getInstance().extractMetadata(uri,
          config.getExtractContentMetadata(),
          config.getUseOracleForExtractors(),
          config.getUseOracleForPlugs(),
          (mForceReload ? false : config.getUseMetadataCache()));
        
        long timeTotal = System.currentTimeMillis() - timeStarted;

        if (!mRunning) {
          return;
        }
        
        PrettyWriter pw = new PrettyWriter();
        StringWriter sw = new StringWriter();
        pw.write(m, sw, null);
        
        if (!mRunning) {
          return;
        }        
        
        mP.mFraxGUI.mResultsText.setText(sw.toString());        
        mP.mFraxGUI.mStatusLabel.setText("Done (" +
          ((float) timeTotal / 1000) + " seconds).");
      } catch (URISyntaxException ex) {
        JOptionPane.showMessageDialog(mP.mFraxGUI.mFrame,
          "Invalid absolute URI: " + ex.getInput(), "Error",
          JOptionPane.ERROR_MESSAGE);
        return;
      } catch (FraxException ex) {
        JOptionPane.showMessageDialog(mP.mFraxGUI.mFrame,
          ex.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
        return;
      } catch (RDFException ex) {
        JOptionPane.showMessageDialog(mP.mFraxGUI.mFrame,
          ex.getMessage(), "RDF Error",
          JOptionPane.ERROR_MESSAGE);
        return;
      } finally {
        mP.execStop();       
      }
    }
    
    public void setRunning(boolean iRunning) {
      mRunning = iRunning;
    }
  }
}