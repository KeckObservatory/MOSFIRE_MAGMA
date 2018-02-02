package edu.ucla.astro.irlab.util.gui.builder;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComponent;

import edu.ucla.astro.irlab.util.InvalidValueException;
import edu.ucla.astro.irlab.util.NoSuchPropertyException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SetterGUIWidget extends GUIWidget {
	JButton setButton = new JButton();
	
	public SetterGUIWidget(String name, JComponent component) {
		super(name, component);
		setButton.setText("SET");
		setButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				setButton_actionPerformed();
			}
		});	
	}
	public void setButton_actionPerformed() {
		if (propertySetter != null) {
			String value;
			JComponent comp = super.getComponent(); 
			if (comp instanceof JTextField) {
				value = ((JTextField)comp).getText();
			} else if (comp instanceof JComboBox) {
					value = ((JComboBox)comp).getSelectedItem().toString();
			} else if (comp instanceof JLabel) {
				value = ((JLabel)comp).getText();
			} else {
				return;
			}
			//. validate value
			if (valueValidator != null) {
				if (!valueValidator.isValueValid(value)) {
					String[] message = {"Error setting property <"+super.getPropertyName()+">: ",
							"",
							valueValidator.getCriteria()
					};
					JOptionPane.showMessageDialog(super.getComponent(), message, "Error Setting Property", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			
			try {
				propertySetter.setNewPropertyValue(super.getPropertyName(), value);
			} catch (NoSuchPropertyException nspEx) {
				JOptionPane.showMessageDialog(super.getComponent(), "Error setting property <"+super.getPropertyName()+">: "+nspEx.getMessage(), "Error Setting Property", JOptionPane.ERROR_MESSAGE);
			} catch (InvalidValueException ivEx) {
				JOptionPane.showMessageDialog(super.getComponent(), "Error setting property <"+super.getPropertyName()+">: "+ivEx.getMessage(), "Error Setting Property", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	public JButton getSetButton() {
		return setButton;
	}
	
}
