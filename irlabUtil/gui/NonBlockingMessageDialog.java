package edu.ucla.astro.irlab.util.gui;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class NonBlockingMessageDialog {

	public static JDialog showMessageDialog(Component parent, Object message) {
		return showMessageDialog(parent, message, "Message");
	}
	public static JDialog showMessageDialog(Component parent, Object message, String title) {
		return showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	public static JDialog showMessageDialog(Component parent, Object message, String title, int messageType) {
		final JOptionPane optionPane = new JOptionPane(message, messageType);
		final JDialog d = optionPane.createDialog(parent, title);
		d.setModal(false);
		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String prop = evt.getPropertyName();
				if (d.isVisible() && (evt.getSource() == optionPane) && prop.equals(JOptionPane.VALUE_PROPERTY)) {
					d.setVisible(false);
				}
			}
		});
		d.setContentPane(optionPane);
		d.pack();
		d.setVisible(true);
		return d;
	}
}
