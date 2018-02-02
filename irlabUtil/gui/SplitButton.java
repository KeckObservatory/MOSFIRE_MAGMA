package edu.ucla.astro.irlab.util.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

public class SplitButton extends JComboBox {

	class SplitButtonCellRenderer extends GradientButton implements ListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			// TODO Auto-generated method stub
			setText(value.toString());
			return this;
		}
	}
	
	class SplitButtonComboBoxEditor extends GradientButton implements ComboBoxEditor {

		public SplitButtonComboBoxEditor() {
			super();
		}
		
		@Override
		public Component getEditorComponent() {
			return this;
		}

		@Override
		public Object getItem() {
			return this.getText();
		}

		@Override
		public void selectAll() {
			//. not implemented
		}

		@Override
		public void setItem(Object anObject) {
			setText(anObject.toString());
		}
	}
	
	SplitButtonComboBoxEditor myEditor;
	SplitButtonCellRenderer myRenderer;
	public SplitButton() {
		super();
		init();
	}
	public SplitButton(Object[] items) {
		super(items);
		init();
	}
	private void init() {
		myEditor = new SplitButtonComboBoxEditor();
		myRenderer = new SplitButtonCellRenderer();
		setRenderer(myRenderer);
		setEditable(true);
		setEditor(myEditor);
	}
	public void setBackground(Color c) {
		super.setBackground(c);
		if (myEditor != null)
			myEditor.setBackground(c);
		if (myRenderer != null) 
			myRenderer.setBackground(c);
	}
	public void setForeground(Color c) {
		super.setForeground(c);
		if (myEditor != null)
			myEditor.setForeground(c);
		if (myRenderer != null) 
			myRenderer.setForeground(c);
	}
	
	public void setEnabled(boolean s) {
		super.setEnabled(s);
		if (myEditor != null)
			myEditor.setEnabled(s);
		if (myRenderer != null) 
			myRenderer.setEnabled(s);
	}
	public void setFont(Font f) {
		super.setFont(f);
		if (myEditor != null)
			myEditor.setFont(f);
		if (myRenderer != null) 
			myRenderer.setFont(f);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] items = {"test1", "hello", "doh"};
		JFrame frame = new JFrame();
		JPanel panel = (JPanel)frame.getContentPane();
		panel.setLayout(new BorderLayout());
		SplitButton button = new SplitButton(items);
		button.setBackground(Color.red);
		button.setForeground(Color.white);
		button.setSelectedIndex(2);
		panel.add(button, BorderLayout.CENTER);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
