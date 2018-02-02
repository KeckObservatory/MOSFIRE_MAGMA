package edu.ucla.astro.irlab.mosfire.mscgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.ucla.astro.irlab.util.gui.ColorScaleChooserPanel;

public class PlotTargetsOptionsPanel extends JPanel {
	
	private JPanel targetSizePanel = new JPanel();
	private JRadioButton scaleTargetSizeRadioButton = new JRadioButton("Scale target size with priority");
	private ButtonGroup scaleTargetSizeButtonGroup = new ButtonGroup();
	private JPanel constantSizePanel = new JPanel();
	private JRadioButton constantSizeRadioButton = new JRadioButton("Use constant size");
	private JLabel targetSizeLabel = new JLabel("Base target size in percentage of row height:");
	private JSpinner constantSizeSpinner = new JSpinner();

	private JPanel targetColorPanel = new JPanel();
	private JRadioButton scaleTargetColorRadioButton = new JRadioButton("Scale target color with priority");
	private ColorScaleChooserPanel colorScaleChooser = new ColorScaleChooserPanel();
	private ButtonGroup scaleTargetColorButtonGroup = new ButtonGroup();
	private JRadioButton constantColorRadioButton = new JRadioButton("Use color: ");
	private JLabel constantColorPreviewLabel = new JLabel(" ");
	private JColorChooser targetColorChooser = new JColorChooser(MSCGUIParameters.DEFAULT_COLOR_TARGET);
	
	public PlotTargetsOptionsPanel() {
		constantColorPreviewLabel.setEnabled(false);
		targetColorChooser.setEnabled(false);
		constantColorPreviewLabel.setPreferredSize(new Dimension(100, 20));
		constantColorPreviewLabel.setOpaque(true);
		constantColorPreviewLabel.setBackground(MSCGUIParameters.DEFAULT_COLOR_TARGET);
		constantColorPreviewLabel.setForeground(MSCGUIParameters.DEFAULT_COLOR_TARGET);
		targetColorChooser.setPreviewPanel(new JPanel());
		targetColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				targetColorChooser_stateChanged(e);
			}
		});
		scaleTargetColorButtonGroup.add(scaleTargetColorRadioButton);
		scaleTargetColorButtonGroup.add(constantColorRadioButton);
		scaleTargetColorRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scaleTargetColor_actionPerformed(e);
			}
		});
		constantColorRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scaleTargetColor_actionPerformed(e);
			}
		});
		
		scaleTargetSizeButtonGroup.add(scaleTargetSizeRadioButton);
		scaleTargetSizeButtonGroup.add(constantSizeRadioButton);
		constantSizeSpinner.setModel(new SpinnerNumberModel(MSCGUIParameters.DEFAULT_TARGET_SIZE_PERCENTAGE_OF_ROW, 0, Integer.MAX_VALUE, 1));
		
		
		constantSizePanel.setLayout(new BorderLayout(5,0));
		constantSizePanel.add(targetSizeLabel, BorderLayout.WEST);
		constantSizePanel.add(constantSizeSpinner, BorderLayout.CENTER);
		
		targetSizePanel.setBorder(BorderFactory.createTitledBorder("Target Size"));
		targetSizePanel.setLayout(new GridBagLayout());
		targetSizePanel.add(scaleTargetSizeRadioButton, new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		targetSizePanel.add(constantSizeRadioButton, new GridBagConstraints(0,1,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		targetSizePanel.add(constantSizePanel, new GridBagConstraints(0,2,1,1,1.0,0.0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		
		targetColorPanel.setBorder(BorderFactory.createTitledBorder("Target Color"));
		targetColorPanel.setLayout(new GridBagLayout());
		targetColorPanel.add(scaleTargetColorRadioButton, new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		targetColorPanel.add(colorScaleChooser, new GridBagConstraints(0,1,1,1,1.0,0.0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		targetColorPanel.add(constantColorRadioButton, new GridBagConstraints(0,2,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		targetColorPanel.add(constantColorPreviewLabel, new GridBagConstraints(0,3,1,1,1.0,0.0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		targetColorPanel.add(targetColorChooser, new GridBagConstraints(0,4,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		setLayout(new BorderLayout());
		add(targetSizePanel, BorderLayout.NORTH);
		add(targetColorPanel, BorderLayout.SOUTH);
		
	}
	protected void targetColorChooser_stateChanged(ChangeEvent e) {
		Color selectedColor = targetColorChooser.getSelectionModel().getSelectedColor();
		constantColorPreviewLabel.setBackground(selectedColor);
		constantColorPreviewLabel.setForeground(selectedColor);
		constantColorPreviewLabel.repaint();
	}
	protected void scaleTargetColor_actionPerformed(ActionEvent e) {
		setScaleTargetColorWithPriority(scaleTargetColorRadioButton.isSelected());
	}
	public void setScaleTargetSizeWithPriority(boolean scaleTargetSizeWithPriority) {
		scaleTargetSizeRadioButton.setSelected(scaleTargetSizeWithPriority);
		constantSizeRadioButton.setSelected(!scaleTargetSizeWithPriority);
	}
	public boolean getScaleTargetSizeWithPriority() {
		return scaleTargetSizeRadioButton.isSelected();
	}
	public void setScaleTargetColorWithPriority(boolean scaleTargetColorWithPriority) {
		colorScaleChooser.setEnabled(scaleTargetColorWithPriority);
		targetColorChooser.setEnabled(!scaleTargetColorWithPriority);
		constantColorPreviewLabel.setEnabled(!scaleTargetColorWithPriority);
		scaleTargetColorRadioButton.setSelected(scaleTargetColorWithPriority);
		constantColorRadioButton.setSelected(!scaleTargetColorWithPriority);
	}
	public boolean getScaleTargetColorWithPriority() {
		return scaleTargetColorRadioButton.isSelected();
	}
	public void setColorScaleMode(int mode) {
		colorScaleChooser.setScaleMode(mode);
	}
	public int getColorScaleMode() {
		return colorScaleChooser.getScaleMode();
	}
	public void setConstantColor(Color c) {
		targetColorChooser.setColor(c);
	}
	public Color getConstantColor() {
		return targetColorChooser.getColor();
	}
	public void setTargetSize(int targetSize) {
		constantSizeSpinner.getModel().setValue(new Integer(targetSize));
	}
	public int getTargetSize() {
		return (Integer)(((SpinnerNumberModel)constantSizeSpinner.getModel()).getNumber()).intValue();
	}
	
}
