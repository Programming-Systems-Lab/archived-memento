/*
 * OptionsDialog.java
 *
 * Created on January 3, 2003, 7:12 AM
 */

package psl.memento.server.frax.gui;

import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import psl.memento.server.frax.*;

/**
 *
 * @author  Mark Ayzenshtat
 */
public class OptionsDialog extends javax.swing.JDialog {

  /** Creates new form OptionsDialog */
  public OptionsDialog(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  private void initComponents() {//GEN-BEGIN:initComponents
    jTabbedPane1 = new javax.swing.JTabbedPane();
    genOptionsPanel = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();
    chkUseMDCache = new javax.swing.JCheckBox();
    chkExtractContentMD = new javax.swing.JCheckBox();
    chkUseOracleForExtractors = new javax.swing.JCheckBox();
    chkUseOracleForPlugs = new javax.swing.JCheckBox();
    extractorsPanel = new javax.swing.JPanel();
    schemesPanel = new javax.swing.JPanel();
    schemesLabel = new javax.swing.JLabel();
    schemesListScrollPane = new javax.swing.JScrollPane();
    schemesList = new javax.swing.JList();
    infoPanel = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    extractorInfoTA = new javax.swing.JTextArea();
    plugsPanel = new javax.swing.JPanel();
    contentTypesPanel = new javax.swing.JPanel();
    contentTypesLabel = new javax.swing.JLabel();
    contentTypesScrollPane = new javax.swing.JScrollPane();
    contentTypesList = new javax.swing.JList();
    contentTypeInfoPanel = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    plugInfoTA = new javax.swing.JTextArea();
    jPanel1 = new javax.swing.JPanel();
    buttonOK = new javax.swing.JButton();

    setTitle("Options");
    setLocationRelativeTo(null);
    setModal(true);
    setResizable(false);
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        closeDialog(evt);
      }
    });

    jTabbedPane1.setToolTipText("");
    jTabbedPane1.setPreferredSize(new java.awt.Dimension(640, 480));
    jTabbedPane1.addContainerListener(new java.awt.event.ContainerAdapter() {
      public void componentAdded(java.awt.event.ContainerEvent evt) {
        jTabbedPane1ComponentAdded(evt);
      }
    });

    jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

    chkUseMDCache.setSelected(Frax.getInstance().getConfiguration().getUseMetadataCache());
    chkUseMDCache.setText("Use metadata cache");
    chkUseMDCache.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        chkUseMDCacheActionPerformed(evt);
      }
    });

    jPanel2.add(chkUseMDCache);

    chkExtractContentMD.setSelected(Frax.getInstance().getConfiguration().getExtractContentMetadata());
    chkExtractContentMD.setText("Extract content metadata");
    jPanel2.add(chkExtractContentMD);

    chkUseOracleForExtractors.setSelected(Frax.getInstance().getConfiguration().getUseOracleForExtractors());
    chkUseOracleForExtractors.setText("Use oracle for unrecognized schemes");
    jPanel2.add(chkUseOracleForExtractors);

    chkUseOracleForPlugs.setSelected(Frax.getInstance().getConfiguration().getUseOracleForPlugs());
    chkUseOracleForPlugs.setText("Use oracle for unrecognized content types");
    jPanel2.add(chkUseOracleForPlugs);

    genOptionsPanel.add(jPanel2);

    jTabbedPane1.addTab("General Options", genOptionsPanel);

    extractorsPanel.setLayout(new java.awt.BorderLayout());

    extractorsPanel.setToolTipText("");
    schemesPanel.setLayout(new javax.swing.BoxLayout(schemesPanel, javax.swing.BoxLayout.Y_AXIS));

    schemesLabel.setText("Schemes");
    schemesLabel.setToolTipText("S");
    schemesPanel.add(schemesLabel);

    schemesList.setBorder(new javax.swing.border.EtchedBorder());
    schemesList.setFont(new java.awt.Font("Dialog", 0, 12));
    schemesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    schemesList.setToolTipText("");
    schemesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        schemesListValueChanged(evt);
      }
    });

    schemesListScrollPane.setViewportView(schemesList);

    schemesPanel.add(schemesListScrollPane);

    extractorsPanel.add(schemesPanel, java.awt.BorderLayout.WEST);

    infoPanel.setLayout(new java.awt.BorderLayout());

    extractorInfoTA.setEditable(false);
    extractorInfoTA.setToolTipText("");
    jScrollPane1.setViewportView(extractorInfoTA);

    infoPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    extractorsPanel.add(infoPanel, java.awt.BorderLayout.CENTER);

    jTabbedPane1.addTab("Extractors", extractorsPanel);

    plugsPanel.setLayout(new java.awt.BorderLayout());

    contentTypesPanel.setLayout(new javax.swing.BoxLayout(contentTypesPanel, javax.swing.BoxLayout.Y_AXIS));

    contentTypesLabel.setText("MIME Types");
    contentTypesLabel.setToolTipText("S");
    contentTypesPanel.add(contentTypesLabel);

    contentTypesList.setBorder(new javax.swing.border.EtchedBorder());
    contentTypesList.setFont(new java.awt.Font("Dialog", 0, 12));
    contentTypesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    contentTypesList.setToolTipText("");
    contentTypesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        contentTypesListValueChanged(evt);
      }
    });

    contentTypesScrollPane.setViewportView(contentTypesList);

    contentTypesPanel.add(contentTypesScrollPane);

    plugsPanel.add(contentTypesPanel, java.awt.BorderLayout.WEST);

    contentTypeInfoPanel.setLayout(new java.awt.BorderLayout());

    plugInfoTA.setEditable(false);
    plugInfoTA.setToolTipText("");
    jScrollPane2.setViewportView(plugInfoTA);

    contentTypeInfoPanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

    plugsPanel.add(contentTypeInfoPanel, java.awt.BorderLayout.CENTER);

    jTabbedPane1.addTab("Plugs", plugsPanel);

    getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

    buttonOK.setText("OK");
    buttonOK.setToolTipText("");
    buttonOK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonOKActionPerformed(evt);
      }
    });

    jPanel1.add(buttonOK);

    getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

    pack();
  }//GEN-END:initComponents

  private void contentTypesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_contentTypesListValueChanged
    if (evt.getValueIsAdjusting()) {
      return;
    }

    if (((JList) evt.getSource()).getSelectedIndex() < 0) {
      return;
    }
    
    StringBuffer labelText = new StringBuffer();
    
    labelText.append("PLUG CLASS:\n");
    String plugClassName = Frax.getInstance().getConfiguration()
      .getPlugClass((String) ((JList) evt.getSource()).getSelectedValue())
      .getName();    
    labelText.append(plugClassName);
    
    labelText.append("\n\nDEPENDENCIES:\n");    
    List deps = Frax.getInstance().getConfiguration()
      .getDependencies(plugClassName);
    
    if (deps.isEmpty()) {
      labelText.append("(NONE)\n");
    } else {    
      for (int i = 0, n = deps.size(); i < n; i++) {
        labelText.append(deps.get(i)).append('\n');
      }
    }
    
    plugInfoTA.setText(labelText.toString());
  }//GEN-LAST:event_contentTypesListValueChanged

  public void reloadSchemesList() {
    String[] schemes = Frax.getInstance().getConfiguration().getLocalSchemes();
    Arrays.sort(schemes);
    schemesList.setListData(schemes);
  }
  
  public void reloadTypesList() {
    String[] contentTypes = Frax.getInstance().getConfiguration().getLocalTypes();
    Arrays.sort(contentTypes);
    contentTypesList.setListData(contentTypes);
  }
  
  private void schemesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_schemesListValueChanged
    if (evt.getValueIsAdjusting()) {
      return;
    }
    
    if (((JList) evt.getSource()).getSelectedIndex() < 0) {
      return;
    }
    
    StringBuffer labelText = new StringBuffer();
    
    labelText.append("EXTRACTOR CLASS:\n");
    
    FraxConfiguration config = Frax.getInstance().getConfiguration();
    JList source = (JList) evt.getSource();
    String selectedValue = (String) source.getSelectedValue();
    Class c = config.getExtractorClass(selectedValue);
    String extractorClassName = c.getName();
    labelText.append(extractorClassName);
    
    labelText.append("\n\nDEPENDENCIES:\n");    
    List deps = Frax.getInstance().getConfiguration()
      .getDependencies(extractorClassName);
    
    if (deps.isEmpty()) {
      labelText.append("(NONE)\n");
    } else {    
      for (int i = 0, n = deps.size(); i < n; i++) {
        labelText.append(deps.get(i)).append('\n');
      }
    }
    
    extractorInfoTA.setText(labelText.toString());
  }//GEN-LAST:event_schemesListValueChanged

  private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
    saveOptionsData();

    setVisible(false);
    dispose();
  }//GEN-LAST:event_buttonOKActionPerformed

  private void saveOptionsData() {
    FraxConfiguration config = Frax.getInstance().getConfiguration();

    config.setUseMetadataCache(chkUseMDCache.isSelected());
    config.setExtractContentMetadata(chkExtractContentMD.isSelected());
    config.setUseOracleForExtractors(chkUseOracleForExtractors.isSelected());
    config.setUseOracleForPlugs(chkUseOracleForPlugs.isSelected());
  }

  private void jTabbedPane1ComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentAdded
    // Add your handling code here:
  }//GEN-LAST:event_jTabbedPane1ComponentAdded

  private void chkUseMDCacheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseMDCacheActionPerformed
    // Add your handling code here:
  }//GEN-LAST:event_chkUseMDCacheActionPerformed

  /** Closes the dialog */
  private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
    setVisible(false);
    dispose();
  }//GEN-LAST:event_closeDialog

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JList contentTypesList;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel infoPanel;
  private javax.swing.JPanel schemesPanel;
  private javax.swing.JPanel genOptionsPanel;
  private javax.swing.JScrollPane contentTypesScrollPane;
  private javax.swing.JPanel extractorsPanel;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JList schemesList;
  private javax.swing.JLabel schemesLabel;
  private javax.swing.JPanel contentTypesPanel;
  private javax.swing.JPanel contentTypeInfoPanel;
  private javax.swing.JScrollPane schemesListScrollPane;
  private javax.swing.JPanel plugsPanel;
  private javax.swing.JTextArea extractorInfoTA;
  private javax.swing.JCheckBox chkUseOracleForPlugs;
  private javax.swing.JCheckBox chkUseOracleForExtractors;
  private javax.swing.JCheckBox chkExtractContentMD;
  private javax.swing.JLabel contentTypesLabel;
  private javax.swing.JTabbedPane jTabbedPane1;
  private javax.swing.JButton buttonOK;
  private javax.swing.JCheckBox chkUseMDCache;
  private javax.swing.JTextArea plugInfoTA;
  // End of variables declaration//GEN-END:variables

}
