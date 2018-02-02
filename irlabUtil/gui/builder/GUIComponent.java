package edu.ucla.astro.irlab.util.gui.builder;

import java.awt.Color;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

//. note: add font, color
//. may need more grid bag constraints: fill, anchor
//. may need insets (could be in layout)

public class GUIComponent {
	public static final int ALIGNMENT_NORTHWEST = 1;
	public static final int ALIGNMENT_NORTH     = 2;
	public static final int ALIGNMENT_NORTHEAST = 3;
	public static final int ALIGNMENT_WEST      = 4;
	public static final int ALIGNMENT_CENTER    = 5;
	public static final int ALIGNMENT_EAST      = 6;
	public static final int ALIGNMENT_SOUTHWEST = 7;
	public static final int ALIGNMENT_SOUTH     = 8;
	public static final int ALIGNMENT_SOUTHEAST = 9;

	private JComponent myComponent;
	private String componentType;
	private String componentName;
	private String fontName;
	private String fontStyle;
	private String fontSize;
	private Color color;  //. color for font, border, etc.
	private int xGrid=0;
	private int yGrid=0;
	private int width = 1;
	private int height = 1;
	private int alignment;
	
	public GUIComponent(String name, String type) {
		
	}
	public GUIComponent(String name, JComponent component) {
		componentName=name;
		myComponent=component;
		alignment=ALIGNMENT_CENTER;
	}

	public int getAlignment() {
		return alignment;
	}
	public void setAlignment(int alignment) {
		if ((alignment > 0) &&  (alignment < 10))
			this.alignment = alignment;
	}
	public JComponent getComponent() {
		return myComponent;
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public int getXGrid() {
		return xGrid;
	}
	public void setXGrid(int grid) {
		xGrid = grid;
	}
	public int getYGrid() {
		return yGrid;
	}
	public void setYGrid(int grid) {
		yGrid = grid;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public String getFontName() {
		return fontName;
	}
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	public String getFontSize() {
		return fontSize;
	}
	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}
	public String getFontStyle() {
		return fontStyle;
	}
	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}
}
