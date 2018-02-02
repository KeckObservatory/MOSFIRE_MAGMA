package edu.ucla.astro.irlab.mosfire.util;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class AstroObjInfoPanel extends JPanel {

	private JLabel nameLabel = new JLabel(" ");
	private JLabel priorityLabel = new JLabel(" ");
	private JLabel raLabel = new JLabel(" ");
	private JLabel decLabel = new JLabel(" ");
	private JLabel epochLabel = new JLabel(" ");
	private JLabel equinoxLabel = new JLabel(" ");
	private JLabel magnitudeLabel = new JLabel(" ");
	private JLabel csuXLabel = new JLabel(" ");
	private JLabel csuYLabel = new JLabel(" ");
	private JLabel centerDistanceLabel = new JLabel(" ");
	private JLabel ditherMinRowLabel = new JLabel(" ");
	private JLabel ditherMaxRowLabel = new JLabel(" ");

	private Insets defaultInsets = new Insets(2,2,2,2);
	private DecimalFormat twoDigitFormatter = new DecimalFormat("0.00");
	private Font nameFont = new Font("Dialog", Font.BOLD, 16);
	private Font valueFont = new Font("Dialog", Font.ITALIC, 16);
	public AstroObjInfoPanel() {
		init();
	}
	public AstroObjInfoPanel(AstroObj obj) {
		init();
		setAstroObj(obj);
	}

	public void addRow(int index, String name, JLabel label) {
		JLabel tagLabel = new JLabel(name);
		tagLabel.setFont(nameFont);
		label.setFont(valueFont);
		add(tagLabel, new GridBagConstraints(0, index, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, defaultInsets, 0,0));
		add(label, new GridBagConstraints(1, index, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
	}
	public void init() {
		int ii=0;
		setLayout(new GridBagLayout());
		addRow(ii++, "Name: ", nameLabel);
		addRow(ii++, "Priority: ", priorityLabel);
		addRow(ii++, "Magnitude: ", magnitudeLabel);
		addRow(ii++, "RA: ", raLabel);
		addRow(ii++, "Dec: ", decLabel);
		addRow(ii++, "Epoch: ", epochLabel);
		addRow(ii++, "Equinox: ", equinoxLabel);
		addRow(ii++, "CSU X Location: ", csuXLabel);
		addRow(ii++, "CSU Y Location: ", csuYLabel);
		addRow(ii++, "Dither Min Row: ", ditherMinRowLabel);
		addRow(ii++, "Dither Max Row: ", ditherMaxRowLabel);
		addRow(ii++, "Center Distance: ", centerDistanceLabel);
	}
	public void setAstroObj(AstroObj obj) {
		nameLabel.setText(obj.getObjName());
		priorityLabel.setText(twoDigitFormatter.format(obj.getObjPriority()));
		raLabel.setText(obj.raToString());
		decLabel.setText(obj.decToString());
		epochLabel.setText(twoDigitFormatter.format(obj.getEpoch()));
		equinoxLabel.setText(twoDigitFormatter.format(obj.getEquinox()));
		magnitudeLabel.setText(twoDigitFormatter.format(obj.getObjMag()));
		csuXLabel.setText(twoDigitFormatter.format(obj.getObjX())+"\"");
		csuYLabel.setText(twoDigitFormatter.format(obj.getObjY())+"\"");
		//. min/max row in astroObj is numbered from bottom, so subtract from number of bars
		ditherMinRowLabel.setText(Integer.toString(MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS - obj.getMaxRow()));
		ditherMaxRowLabel.setText(Integer.toString(MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS - obj.getMinRow()));
		if (obj.isInValidSlit()) {
			centerDistanceLabel.setText(twoDigitFormatter.format(obj.getCenterDistance()));
		} else {
			centerDistanceLabel.setText("N/A");
		}
	}
}
