package psl.memento.pervasive.roca.gui;

import psl.memento.pervasive.roca.room.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class NewObjectWindow implements ActionListener {

	private JPanel mainPanel, namePanel, imageLocationPanel, widthPanel, lengthPanel, heightPanel, texturePanel, buttonPanel;
	private JTextField name, imageLocation, width, length, height, texture;
	private JButton browse, add;
	private JComboBox comboBox;
	private final static int FIELD_LENGTH = 10, STRING_FIELD_LENGTH = 20;

	public NewObjectWindow(String id, JComboBox cb) {
		comboBox = cb;

		browse = new JButton("Browse...");
		add = new JButton("Add to Database");

		name = new JTextField(STRING_FIELD_LENGTH);
		imageLocation = new JTextField(STRING_FIELD_LENGTH);
		width = new JTextField(FIELD_LENGTH);
		length = new JTextField(FIELD_LENGTH);
		height = new JTextField(FIELD_LENGTH);
		texture = new JTextField(STRING_FIELD_LENGTH);

		namePanel = new JPanel();
		namePanel.setLayout(new FlowLayout());
		namePanel.add(new JLabel("Name: "));
		namePanel.add(name);

		//namePanel.add(Box.createRigidArea(new Dimension(100,0)));

		imageLocationPanel = new JPanel();
		imageLocationPanel.setLayout(new FlowLayout());
		imageLocationPanel.add(new JLabel("Image file: "));
		imageLocationPanel.add(imageLocation);
		imageLocationPanel.add(browse);
		//imageLocationPanel.add(Box.createRigidArea(new Dimension(110,0)));

/*
		sizePanel = new JPanel();
		sizePanel.setLayout(new FlowLayout());
		sizePanel.add(new JLabel("Width:     "));
		sizePanel.add(width);
		sizePanel.add(new JLabel(" Length: "));
		sizePanel.add(length);
		sizePanel.add(new JLabel(" Height: "));
		sizePanel.add(height);
*/

		widthPanel = new JPanel();
		widthPanel.setLayout(new FlowLayout());
		widthPanel.add(new JLabel("Width: "));
		widthPanel.add(width);
		//widthPanel.add(Box.createRigidArea(new Dimension(150,0)));

		lengthPanel = new JPanel();
		lengthPanel.setLayout(new FlowLayout());
		lengthPanel.add(new JLabel("Length: "));
		lengthPanel.add(length);
		//lengthPanel.add(Box.createRigidArea(new Dimension(150,0)));

		heightPanel = new JPanel();
		heightPanel.setLayout(new FlowLayout());
		heightPanel.add(new JLabel("Height: "));
		heightPanel.add(height);
		//namePanel.add(Box.createRigidArea(new Dimension(150,0)));

		texturePanel = new JPanel();
		texturePanel.setLayout(new FlowLayout());
		texturePanel.add(new JLabel("Texture: "));
		texturePanel.add(texture);
		//texturePanel.add(Box.createRigidArea(new Dimension(100,0)));

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(Box.createRigidArea(new Dimension(250,0)));
		buttonPanel.add(add);

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0,1));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		mainPanel.add(new JLabel("Add a new " + id.toLowerCase() + " object to the database."));
		mainPanel.add(namePanel);
		mainPanel.add(imageLocationPanel);
		//mainPanel.add(sizePanel);
		mainPanel.add(widthPanel);
		mainPanel.add(lengthPanel);
		mainPanel.add(heightPanel);
		mainPanel.add(texturePanel);
		mainPanel.add(buttonPanel);
	}

	public Component getMainPanel() {
		return mainPanel;
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == browse) {

		// Let user search for image

		}

		if (source == add) {

		// Add to server database and object selector combo box

			System.out.println("add to database and object selector combo box.");
		}
	}
}