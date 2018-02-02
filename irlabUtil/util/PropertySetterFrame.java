package edu.ucla.astro.irlab.util;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.util.Enumeration;

import javax.swing.JLabel;

//. todo: change to use PropertySetter interface of PropertyList
public class PropertySetterFrame extends JFrame {
	PropertyList list;
	JTextField propNameField = new JTextField();
	JTextField valueField = new JTextField();
	JLabel statusLabel = new JLabel(" ");
	JComboBox combo;
	JComboBox valueCombo = new JComboBox();
	JPanel valuePanel = new JPanel();
	final static String VALUE_COMBO = "valueCombo";
	final static String VALUE_FIELD = "valueField";
	boolean usingCombo = false;
	public PropertySetterFrame(PropertyList propList) {
		list = propList;
		
		jbInit();
	}
	private void jbInit() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,0));
		
		
		String[] stringPropList = new String[list.size()];
		int ii=0;
		Enumeration<String> e = list.keys();
		while (e.hasMoreElements()) {
			stringPropList[ii] = list.get(e.nextElement()).getName();
			ii++;
		}
		combo = new JComboBox(stringPropList);
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				combo_actionPerformed();
			}
		});
		
		valuePanel.setLayout(new CardLayout());
		
		
		JButton setButton = new JButton("SET");
		
		setButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				setButton_actionPerformed();
			}
		});

		valuePanel.add(valueField, VALUE_FIELD);
		valuePanel.add(valueCombo, VALUE_COMBO);
		
		panel.add(propNameField);
		panel.add(valuePanel);
				
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(combo, BorderLayout.NORTH);
		this.getContentPane().add(panel, BorderLayout.CENTER);
		this.getContentPane().add(setButton, BorderLayout.EAST);
		this.getContentPane().add(statusLabel, BorderLayout.SOUTH);
		
	}
	public void combo_actionPerformed() {
		Property prop = list.getProperty(combo.getSelectedItem().toString());
		propNameField.setText(combo.getSelectedItem().toString());

		CardLayout cl = (CardLayout)(valuePanel.getLayout());

		if (prop.getAllowedValues() != null) {
			if (prop.getAllowedValues().size() > 0) {
				DefaultComboBoxModel model = new DefaultComboBoxModel(prop.getAllowedValues().toArray());
				valueCombo.setModel(model);
				valueCombo.setSelectedItem(prop.getValue());
				cl.show(valuePanel, VALUE_COMBO);
				usingCombo=true;
				return;
			}
		}
		valueField.setText(prop.getValue().toString());
		cl.show(valuePanel, VALUE_FIELD);
		usingCombo=false;
	}
	public void setButton_actionPerformed() {
		statusLabel.setText("Setting value.");
		
		String propName = propNameField.getText();
		Property prop = list.getProperty(propName);
		if (prop == null) {
			statusLabel.setText("Property of that name not found.");
			return;
		}
		try {
			if (usingCombo) {
				list.setNewPropertyValue(propName, valueCombo.getSelectedItem().toString());
			} else {
				list.setNewPropertyValue(propName, valueField.getText());
			}
		} catch (NoSuchPropertyException nspEx) {
			JOptionPane.showMessageDialog(this, "Error setting property <"+propName+">: "+nspEx.getMessage(), "Error Setting Property", JOptionPane.ERROR_MESSAGE);
		} catch (InvalidValueException ivEx) {
			JOptionPane.showMessageDialog(this, "Error setting property <"+propName+">: "+ivEx.getMessage(), "Error Setting Property", JOptionPane.ERROR_MESSAGE);
		}

	}
}
