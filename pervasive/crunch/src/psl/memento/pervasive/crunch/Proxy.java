import java.util.LinkedList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Peter Grimm
 **/
public class Proxy extends Thread implements ActionListener{
    
    private LinkedList filters = new LinkedList();
    
    private JFrame proxyWindow;
    private JTabbedPane settingsTabs;
    private JPanel buttonPanel;
    private JButton cancelButton;
    private JButton commitButton;
    
    public static void main(String[] args){
	new Proxy().start();
    }
    
    public void run(){
	System.out.println("Started...");
	
	filters.add(new ContentExtractor());
	
	drawGUI();
	
	new ProxyListener(filters, 4000).start();
    }
    
    public void drawGUI(){
        proxyWindow = new JFrame("Not Totally Hacked Proxy 0.0a");
	proxyWindow.setDefaultCloseOperation(proxyWindow.EXIT_ON_CLOSE);
	
	settingsTabs = new JTabbedPane();
	Iterator filterIter = filters.listIterator(0);
	while(filterIter.hasNext()){
	    ProxyFilter current = (ProxyFilter)(filterIter.next());
	    settingsTabs.add(current.getSettingsGUI().getTabName(), new JScrollPane(current.getSettingsGUI()));
	}
	
	proxyWindow.getContentPane().add(settingsTabs, BorderLayout.CENTER);
	
	buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
	buttonPanel.add(Box.createHorizontalGlue());
	commitButton = new JButton("Commit Changes");
	commitButton.addActionListener(this);
	buttonPanel.add(commitButton);
	cancelButton = new JButton("Cancel Changes");
	cancelButton.addActionListener(this);
	buttonPanel.add(cancelButton);

	proxyWindow.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	proxyWindow.pack();
	proxyWindow.show();
    }
    
    public void actionPerformed(ActionEvent e){
	Object command = e.getSource();
	if(command==commitButton){
	    int index = settingsTabs.getSelectedIndex();
	    ProxyFilter filter = (ProxyFilter)(filters.get(index));
	    filter.getSettingsGUI().commitSettings();
	    System.out.println("Settings committed.");
	}else if(command==cancelButton){
	    int index = settingsTabs.getSelectedIndex();
	    ProxyFilter filter = (ProxyFilter)(filters.get(index));
	    filter.getSettingsGUI().revertSettings();
	}else{
	    System.out.println("Error: unknown command");
	}
    }
}


