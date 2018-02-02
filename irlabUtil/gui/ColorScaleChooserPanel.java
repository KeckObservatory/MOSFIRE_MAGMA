package edu.ucla.astro.irlab.util.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import edu.ucla.astro.irlab.util.ColorUtilities;

public class ColorScaleChooserPanel extends JPanel {
	private ColorScalePanel panel = new ColorScalePanel();
	private JComboBox scaleCombo;
	private int scaleMode = 0;
	public ColorScaleChooserPanel() {
		scaleCombo = new JComboBox(ColorUtilities.AVAILABLE_SCALES);
		scaleCombo.setSelectedIndex(scaleMode);
		scaleCombo.setRenderer(new ColorScaleComboRenderer());
		scaleCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scaleCombo_actionPerformed(e);
			}
		});
	  setLayout(new BorderLayout());
	  add(scaleCombo, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
	}
	protected void scaleCombo_actionPerformed(ActionEvent e) {
		scaleMode = scaleCombo.getSelectedIndex();
		panel.setMode(scaleMode);
		repaint();
	}
	public int getScaleMode() {
		return scaleMode;
	}
	public void setScaleMode(int mode) {
		scaleMode = mode;
		scaleCombo.setSelectedIndex(mode);
	}
	public class ColorScalePanel extends JPanel {
		private int mode;
		public void setMode(int scaleMode) {
			mode = scaleMode;
		}
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			Dimension d = getSize();
			g2.setStroke(new BasicStroke(1));
			double scaleBottomY = d.getHeight();
			double scaleTopY = 0;
			int pixelsWide = (int)Math.round(d.getWidth());
			for (int ii=0; ii<pixelsWide; ii++) {
				Color scaleColor = ColorUtilities.getColorFromScale(mode, (float)ii/(float)d.getWidth(), 1.0f);
				g2.setColor(scaleColor);
				Line2D.Double line = new Line2D.Double(ii, scaleBottomY, ii, scaleTopY);
				g2.draw(line);
			}
		}
	}
	public class ColorScaleComboRenderer extends JPanel implements ListCellRenderer {
		JLabel label = new JLabel();
		ColorScalePanel panel = new ColorScalePanel();
		public ColorScaleComboRenderer() {
			JPanel topBuffer = new JPanel();
			JPanel bottomBuffer = new JPanel();
			topBuffer.setPreferredSize(new Dimension(10,2));
			bottomBuffer.setPreferredSize(new Dimension(10,2));
			label.setPreferredSize(new Dimension(200, 16));
			setLayout(new BorderLayout());
			add(label, BorderLayout.WEST);
			add(panel, BorderLayout.CENTER);
			add(topBuffer, BorderLayout.NORTH);
			add(bottomBuffer, BorderLayout.SOUTH);
		}
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			setBackground(label.getBackground());

			List<String> names = Arrays.asList(ColorUtilities.AVAILABLE_SCALES);			
			int idx = names.indexOf(value.toString());
			label.setText(value.toString());
			panel.setMode(idx);
			
			return this;
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		scaleCombo.setEnabled(enabled);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		ColorScaleChooserPanel f = new ColorScaleChooserPanel();
		frame.getContentPane().add(f);
		frame.setSize(600,300);
		frame.validate();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
