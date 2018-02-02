package edu.ucla.astro.irlab.util.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.ucla.astro.irlab.util.ColorUtilities;

public class ColorScaleDesignerPanel extends JPanel {
	float rStart;
	float rEnd;
	float gStart;
	float gEnd;
	float bStart;
	float bEnd;
	int rFunction;
	int gFunction;
	int bFunction;
	
	JTextField rStartField = new JTextField();
	JLabel rStartLabel = new JLabel("R Start: ");
	JTextField rEndField = new JTextField();
	JLabel rEndLabel = new JLabel("R End: ");
	JTextField gStartField = new JTextField();
	JLabel gStartLabel = new JLabel("G Start: ");
	JTextField gEndField = new JTextField();
	JLabel gEndLabel = new JLabel("G End: ");
	JTextField bStartField = new JTextField();
	JLabel bStartLabel = new JLabel("B Start: ");
	JTextField bEndField = new JTextField();
	JLabel bEndLabel = new JLabel("B End: ");
	JComboBox rFunctionCombo = new JComboBox(ColorUtilities.AVAIABLE_SCALE_FUNCTIONS);
	JComboBox gFunctionCombo = new JComboBox(ColorUtilities.AVAIABLE_SCALE_FUNCTIONS);
	JComboBox bFunctionCombo = new JComboBox(ColorUtilities.AVAIABLE_SCALE_FUNCTIONS);
	JPanel controlPanel = new JPanel();
	ColorScalePanel previewPanel = new ColorScalePanel();
	JButton updateButton = new JButton("Update");
	public ColorScaleDesignerPanel() {
		init();
	}

	public void init() {
		
		controlPanel.setLayout(new GridLayout(3,5));
		controlPanel.add(rStartLabel);
		controlPanel.add(rStartField);
		controlPanel.add(rFunctionCombo);
		controlPanel.add(rEndLabel);
		controlPanel.add(rEndField);
		controlPanel.add(gStartLabel);
		controlPanel.add(gStartField);
		controlPanel.add(gFunctionCombo);
		controlPanel.add(gEndLabel);
		controlPanel.add(gEndField);
		controlPanel.add(bStartLabel);
		controlPanel.add(bStartField);
		controlPanel.add(bFunctionCombo);
		controlPanel.add(bEndLabel);
		controlPanel.add(bEndField);
		
		updateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateButton_actionPerformed();
			}
		});
		
		setLayout(new BorderLayout());
		add(controlPanel, BorderLayout.NORTH);
		add(previewPanel, BorderLayout.CENTER);
		add(updateButton, BorderLayout.SOUTH);
		
	}
	private float validateInput(JTextField field) {
		String entry = field.getText();
		try {
			float value = Float.parseFloat(entry);
			return ColorUtilities.constrain(value);
		} catch (NumberFormatException ex) {
			field.setText(Float.toString(0f));
			return 0f;
		}
	}
	public void updateButton_actionPerformed() {
		rStart = validateInput(rStartField);
		gStart = validateInput(gStartField);
		bStart = validateInput(bStartField);
		rEnd = validateInput(rEndField);
		gEnd = validateInput(gEndField);
		bEnd = validateInput(bEndField);
		rFunction = rFunctionCombo.getSelectedIndex();
		gFunction = gFunctionCombo.getSelectedIndex();
		bFunction = bFunctionCombo.getSelectedIndex();
		repaint();
	}
	public class ColorScalePanel extends JPanel {
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			Dimension d = getSize();
			g2.setStroke(new BasicStroke(1));
			double scaleBottomY = d.getHeight();
			double scaleTopY = 0;
			int pixelsWide = (int)Math.round(d.getWidth());
			for (int ii=0; ii<pixelsWide; ii++) {
				Color scaleColor = ColorUtilities.getColorFromCustomScale((float)ii/(float)d.getWidth(), 1.0f,
						rStart, rEnd, rFunction, gStart, gEnd, gFunction, bStart, bEnd, bFunction);
				g2.setColor(scaleColor);
				Line2D.Double line = new Line2D.Double(ii, scaleBottomY, ii, scaleTopY);
				g2.draw(line);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		ColorScaleDesignerPanel f = new ColorScaleDesignerPanel();
		frame.getContentPane().add(f);
		frame.setSize(600,300);
		frame.validate();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
