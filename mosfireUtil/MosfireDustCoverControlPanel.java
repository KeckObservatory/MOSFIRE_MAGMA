package edu.ucla.astro.irlab.mosfire.util;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.ucla.astro.irlab.util.InvalidValueException;
import edu.ucla.astro.irlab.util.NoSuchPropertyException;
import edu.ucla.astro.irlab.util.PropertySetter;
import edu.ucla.astro.irlab.util.gui.builder.GUIBuilderCustomControl;

public class MosfireDustCoverControlPanel extends JPanel implements GUIBuilderCustomControl, ActionListener {

	private JButton openButton = new JButton("OPEN");
	private JButton closeButton = new JButton("CLOSE");
	private PropertySetter propSetter;
	public MosfireDustCoverControlPanel() {
		openButton.addActionListener(this);
		closeButton.addActionListener(this);
		setLayout(new GridLayout(1,2,5,0));
		add(closeButton);
		add(openButton);
	}
	public void setValue(String propName, Object newValue) {
		if (propName.equals(MosfireParameters.MOSFIRE_PROPERTY_MECH_DUST_COVER_POSITION)) {			
			String value = newValue.toString();
			closeButton.setEnabled(value.compareToIgnoreCase(MosfireParameters.DUST_COVER_POSITION_NAME_OPEN) == 0);
			openButton.setEnabled(value.compareToIgnoreCase(MosfireParameters.DUST_COVER_POSITION_NAME_CLOSED) == 0);
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String target;
		if (e.getSource() == openButton) {
			target = MosfireParameters.DUST_COVER_POSITION_NAME_OPEN;
		} else {
			target = MosfireParameters.DUST_COVER_POSITION_NAME_CLOSED;
		}
		
		if (propSetter != null) {
			try {
				propSetter.setNewPropertyValue(MosfireParameters.MOSFIRE_PROPERTY_MECH_DUST_COVER_TARGET_NAME, target);
			} catch (NoSuchPropertyException nspEx) {
				JOptionPane.showMessageDialog(this, "Error setting property <"+MosfireParameters.MOSFIRE_PROPERTY_MECH_DUST_COVER_TARGET_NAME+">: "+nspEx.getMessage(), "Error Setting Property", JOptionPane.ERROR_MESSAGE);
			} catch (InvalidValueException ivEx) {
				JOptionPane.showMessageDialog(this, "Error setting property <"+MosfireParameters.MOSFIRE_PROPERTY_MECH_DUST_COVER_TARGET_NAME+">: "+ivEx.getMessage(), "Error Setting Property", JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	@Override
	public void setPropertySetter(PropertySetter propertySetter) {
		propSetter = propertySetter;
	}
}
