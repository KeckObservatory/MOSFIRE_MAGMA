package edu.ucla.astro.irlab.util.gui;

import javax.swing.JCheckBox;

public class OptionCheckBox extends JCheckBox {
	private int defaultAnswer;
	private JCheckBox optionDialogCheckBox;
	public OptionCheckBox(String checkBoxText) {
		this(checkBoxText, checkBoxText);
	}
	public OptionCheckBox(String checkBoxText, String optionDialogText) {
		super(checkBoxText);
		optionDialogCheckBox = new JCheckBox(optionDialogText);
		optionDialogCheckBox.setModel(getModel());
	}
	public void setDefaultAnswer(int answer) {
		defaultAnswer = answer;
	}
	public int getDefaultAnswer() {
		return defaultAnswer;
	}
	public JCheckBox getOptionDialogCheckBox() {
		return optionDialogCheckBox;
	}
}
