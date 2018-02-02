package edu.ucla.astro.irlab.util.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LEDMeter extends JComponent {
	private int _numLEDs;
	private Color[] _colors;
	private double _ledHeight;
	private double _ledWidth;
	private double _ledSpacing;
	private double _value;
	private double _maxValue;
	private double _minValue;
	private double _cornerArc;
	private boolean _isVertical;
	private static final Color[] DEFAULT_COLORS =  {Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.RED};
	public LEDMeter(Color[] colors, double minValue, double maxValue, boolean isVertical) {
		_colors = colors;
		_numLEDs = colors.length;
		_ledHeight = 10;
		_ledWidth = 30;
		_ledSpacing = 4;
		_value = 0;
		_maxValue = maxValue;
		_minValue = minValue;
		_cornerArc = 10;
		_isVertical = isVertical;
	}
	public LEDMeter(Color[] colors, double minValue, double maxValue) {
		this(colors, minValue, maxValue, true);
	}
	public LEDMeter(Color[] colors, double maxValue) {
		this(colors, 0.0, maxValue);
	}
	public LEDMeter(Color[] colors, double maxValue, boolean isVertical) {
		this(colors, 0.0, maxValue, isVertical);
	}
	public LEDMeter(Color[] colors, boolean isVertical) {
		this(colors, 100.0, isVertical);
	}
	public LEDMeter(Color[] colors) {
		this(colors, 100.0);
	}
	public LEDMeter() {
		this(DEFAULT_COLORS);
	}
	public void setValue(double value) {
		_value = value;
		repaint();
	}
	public void setMaxValue(double maxValue) {
		_maxValue = maxValue;
		repaint();
	}
	public void setMinValue(double minValue) {
		_minValue = minValue;
		repaint();
	}
	public void setLEDWidth(double width) {
		_ledWidth = width;
		repaint();
	}
	public void setLEDSpacing(double ledSpacing) {
		_ledSpacing = ledSpacing;
		repaint();
	}
	public void paint(Graphics g) {
		int height = getSize().height;
		int width = getSize().width;

		double xcenter = width/2.;
		double ycenter = height/2.;
		double x1=0;
		double x2=0;
		
		
		if (_isVertical) {
			_ledHeight = (5.0*height) / (6.0*_numLEDs - 1);
		} else {
			_ledHeight = (5.0*width) / (6.0*_numLEDs - 1);
		}
		_ledSpacing = _ledHeight/5.0;
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		double y1, y2;
		RoundRectangle2D.Double ledShape;
		int onLEDs = (int)Math.ceil(_numLEDs * ((_value-_minValue)/(_maxValue-_minValue)));
		//. origin goes positive to the right and down
		for (int ii=0; ii<_numLEDs; ii++) {
			if (_isVertical) {
				x1 = xcenter - _ledWidth/2.;
				x2 = xcenter + _ledWidth/2.;
				y1 = height - ii * (_ledHeight + _ledSpacing);
				y2 = y1 - _ledHeight;
				ledShape = new RoundRectangle2D.Double(x1, y2, _ledWidth, _ledHeight, _cornerArc, _cornerArc);
			} else {
				y1 = ycenter - _ledWidth/2.;
				y2 = ycenter + _ledWidth/2.;
				x1 = ii * (_ledHeight + _ledSpacing);
				x2 = x1 + _ledHeight;				
				ledShape = new RoundRectangle2D.Double(x1, y1, _ledHeight, _ledWidth, _cornerArc, _cornerArc);
			}
			if (ii < onLEDs) {
				g2d.setColor(_colors[ii]);
			} else {
				g2d.setColor(_colors[ii].darker().darker().darker());
			}
			g2d.fill(ledShape);
			g2d.setColor(Color.WHITE);
			g2d.draw(ledShape);
		}		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel c = (JPanel)f.getContentPane();
		c.setLayout(new GridLayout(1,0));
		LEDMeter m1 = new LEDMeter();
		LEDMeter m2 = new LEDMeter();
		Color[] colors = {Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, 
				Color.GREEN, Color.GREEN, Color.YELLOW, Color.RED, Color.RED};
		LEDMeter m3 = new LEDMeter(colors, false);
		m1.setValue(56);
		m2.setValue(110);
		m3.setValue(75);
		c.add(m1);
		c.add(m2);
		c.add(m3);
		f.setSize(400,200);
		f.validate();
		f.setVisible(true);
	}

}
