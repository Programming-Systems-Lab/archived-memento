package psl.memento.server.frax.gui;

// jdk imports
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

// non-jdk imports
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.common.prettywriter.*;
import psl.memento.server.frax.*;

public class FraxGUI implements ActionListener {
  private static Font kProgramFont = new Font("Monospaced", Font.PLAIN, 12);
  private static int kMaxHistorySize = 20;
  
  private JFrame mFrame;
  private JTextArea mResultsText;
  private AddressPanel mAddressPanel;
  private JLabel mStatusLabel;
  private JMenuItem mInsertAddressLocalFile;
  private JMenuItem mOptionsMenuItem;
  private JFileChooser mExtractFileChooser;
  
  public static void main(String[] args) {
    new FraxGUI();
  }
  
  private FraxGUI() {
    // make sure Frax is initialized
    Frax.getInstance();
    
    mFrame = new JFrame("Frax Metadata Extractor");
    
    mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    buildMenus();
    
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
    
    mInsertAddressLocalFile =
      new JMenuItem("Extract from Local File...", KeyEvent.VK_E);
    mInsertAddressLocalFile.addActionListener(this);
    menu.add(mInsertAddressLocalFile);
    
    menu.addSeparator();
    
    mOptionsMenuItem = new JMenuItem("Options", KeyEvent.VK_O);
    mOptionsMenuItem.addActionListener(this);
    menu.add(mOptionsMenuItem);
  }
  
  public void actionPerformed(ActionEvent iEvent) {
    Object source = iEvent.getSource();
    
    if (mInsertAddressLocalFile.equals(source)) {
      int value = mExtractFileChooser.showDialog(mFrame, null);
      if (value == JFileChooser.APPROVE_OPTION) {
        mAddressPanel.mAddressText.setSelectedItem(mExtractFileChooser.
          getSelectedFile().toURI().toString());
      }
    } else if (mOptionsMenuItem.equals(source)) {
      // display options window
    }
  }
  
  private static class AddressPanel extends JPanel implements ActionListener {
    private FraxGUI mFraxGUI;
    private JLabel mAddressLabel;    
    private JComboBox mAddressText;
    private JButton mExtractButton;
    private JButton mStopButton;    
    
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
      
      mExtractButton = new JButton("Extract", new ImageIcon("etc/frax/Export24.gif"));
      mExtractButton.addActionListener(this);
      mExtractButton.setToolTipText("Extract metadata.");      
      buttonPanel.add(mExtractButton, BorderLayout.WEST);
      
      mStopButton = new JButton("Stop", new ImageIcon("etc/frax/Stop24.gif"));
      mStopButton.addActionListener(this);
      mStopButton.setToolTipText("Stop the extraction.");      
      mStopButton.setEnabled(false);
      buttonPanel.add(mStopButton, BorderLayout.EAST);
    }
    
    public void actionPerformed(ActionEvent iEvent) {
      Object source = iEvent.getSource();
      String command = iEvent.getActionCommand();      
      
      if (mExtractButton.equals(source)) {
        execExtract();
      } else if (mAddressText.equals(source)) {
        if ("comboBoxChanged".equals(command)) {
          execExtract();
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
    
    private void execExtract() {
      String uriString = (String) mAddressText.getSelectedItem();
      if (uriString == null || uriString.trim().equals("")) {        
        return;
      }      
      
      mExtractButton.setEnabled(false);
      mStopButton.setEnabled(true);
      mFraxGUI.mResultsText.setText("");      
      
      new ExtractionThread(uriString, this).start();
    }
    
    private void execStop() {
      mStopButton.setEnabled(false);
      mExtractButton.setEnabled(true);
      
      // FIXME: calling this method doesn't actually
      // interrupt the extraction thread
    }
  }
  
  private static class ExtractionThread extends Thread {
    private String mURI;
    private AddressPanel mP;
    private boolean mRunning;
    
    public ExtractionThread(String iURI, AddressPanel iP) {
      super();
      
      mURI = iURI;
      mP = iP;      
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
        
        Resource r = Frax.getInstance().extractMetadata(uri);

        if (!mRunning) {
          return;
        }
        
        PrettyWriter pw = new PrettyWriter();
        StringWriter sw = new StringWriter();
        pw.write(r.getModel(), sw, null);
        
        if (!mRunning) {
          return;
        }
        
        long timeTotal = System.currentTimeMillis() - timeStarted;
        
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