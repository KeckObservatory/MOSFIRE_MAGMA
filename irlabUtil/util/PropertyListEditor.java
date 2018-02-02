package edu.ucla.astro.irlab.util;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;


public class PropertyListEditor extends JFrame {

	PropertyList list;
	JLabel statusBar = new JLabel(" ");
	public PropertyListEditor(PropertyList list) {
		this.list = list;
		init();
	}
	private void init() {
		this.setTitle("Property List Editor");
		JScrollPane scrollPane = new JScrollPane();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0,1));
		
		Enumeration<String> e = list.keys();
		while (e.hasMoreElements()) {
			mainPanel.add(new PropertyPanel(list.get(e.nextElement())));
		}
		
		scrollPane.getViewport().add(mainPanel);
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);
		this.getContentPane().add(statusBar, BorderLayout.SOUTH);
		
	}
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }
	
	public class PropertyPanel extends JPanel {
		JLabel propName = new JLabel();
		JLabel colon = new JLabel(": ");
		JTextField field = new JTextField();
		JComboBox combo;
		JButton setButton = new JButton("SET");
		JLabel currentValueTitle = new JLabel("current value: ");
		JLabel currentValueLabel = new JLabel(" ");
		
		Property prop;
		public PropertyPanel(Property prop) {
			this.prop = prop;
			this.setBorder(BorderFactory.createEtchedBorder());
			
			propName.setText(prop.getName());
			propName.setToolTipText("["+prop.getDatatype()+"] "+prop.getDescription());
		
			if (prop.getAllowedValues() != null) {
				if (prop.getAllowedValues().size() > 0)
					combo = new JComboBox(prop.getAllowedValues().toArray());
			}
			
			Font currentFont = new Font("Default", Font.ITALIC, 11);
			currentValueLabel.setText(prop.getValue().toString());
			currentValueLabel.setFont(currentFont);
			currentValueTitle.setFont(currentFont);
			
			prop.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent pcEv) {
					updateValue(pcEv.getNewValue().toString());
				}
			});
			
			setButton.setActionCommand(prop.getName());
			setButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						setButton_actionPerformed(ev);
					}
				});

			this.setLayout(new GridBagLayout());
			this.add(propName, new GridBagConstraints(0,0,1,1,0.0,0.0,
					GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
			this.add(colon, new GridBagConstraints(1,0,1,1,0.0,0.0,
					GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));

			if (combo != null)
				this.add(combo, new GridBagConstraints(2,0,1,1,1.0,0.0,
						GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
			else
				this.add(field, new GridBagConstraints(2,0,1,1,1.0,0.0,
						GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
			this.add(setButton, new GridBagConstraints(3,0,1,1,0.0,0.0,
					GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
			this.add(currentValueTitle, new GridBagConstraints(0,1,2,1,0.0,0.0,
					GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
			this.add(currentValueLabel, new GridBagConstraints(2,1,2,1,1.0,0.0,
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));

		}
		public void updateValue(String newValue) {
			currentValueLabel.setText(newValue);
		}
		public void setButton_actionPerformed(ActionEvent aEv) {
			statusBar.setText("Setting value.");
			String value;
			
			if (combo != null) 
				value = combo.getSelectedItem().toString();
			else
				value = field.getText();

			try {
				list.setNewPropertyValue(prop.getName(), value);
			} catch (NoSuchPropertyException nspEx) {
				JOptionPane.showMessageDialog(this, "Error setting property <"+prop.getName()+">: "+nspEx.getMessage(), "Error Setting Property", JOptionPane.ERROR_MESSAGE);
			} catch (InvalidValueException ivEx) {
				JOptionPane.showMessageDialog(this, "Error setting property <"+prop.getName()+">: "+ivEx.getMessage(), "Error Setting Property", JOptionPane.ERROR_MESSAGE);
			}

		}	
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			PropertyList list = new PropertyList();
			list.readXML(new java.io.File("/u/mosdev/kroot/kss/mosfire/gui/mdesktop/mosfireProperties.xml"));
			list.fillWithTestValues();
			PropertyListEditor ed = new PropertyListEditor(list);
			ed.setSize(400,800);
			ed.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
