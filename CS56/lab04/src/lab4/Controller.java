package lab4;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;

public class Controller extends JPanel{
		
	Controller(){
		JButton button;
		JPanel pane;
		JLabel text;
		JTextField textField;
		JCheckBox checkbox;
		JRadioButton radioButton;
		JComboBox dropdown;
		JTextArea textArea;
		
		this.setLayout(new BorderLayout());
		
		pane = new JPanel();
		pane.setLayout(new GridLayout(4,3));
		add(pane, BorderLayout.CENTER);
		text = new JLabel("Printer:");
		pane.add(text);
		textField = new JTextField();
		text.setHorizontalAlignment(JLabel.RIGHT);
		pane.add(textField);
		button = new JButton("OK");
		pane.add(button);
		checkbox = new JCheckBox("Print to File");
		pane.add(checkbox);
		radioButton = new JRadioButton("Selection");
		pane.add(radioButton);
		button = new JButton("Cancel");
		pane.add(button);
		checkbox = new JCheckBox("2-Sided");
		pane.add(checkbox);
		radioButton = new JRadioButton("All");
		pane.add(radioButton);
		button = new JButton("Setup");
		pane.add(button);
		text = new JLabel("Print Quality:");
		text.setHorizontalAlignment(JLabel.RIGHT);
		pane.add(text);
		dropdown = new JComboBox(new String[]{"","high", "low"});
		pane.add(dropdown);
		button = new JButton("Help");
		pane.add(button);
		
		pane = new JPanel();
		add(pane, BorderLayout.SOUTH);
		pane.setLayout(new BorderLayout());
		text = new JLabel("Console:");
		pane.add(text, BorderLayout.WEST);
		textArea = new JTextArea(2, 80);
		pane.add(textArea);
		setVisible(true);
	}
	
}
