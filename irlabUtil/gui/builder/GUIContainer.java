package edu.ucla.astro.irlab.util.gui.builder;

import java.util.ArrayList;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JComponent;

public class GUIContainer extends GUIComponent {
  //. support JPanel, 
	//.         JScrollPane
	//.           - horizontal/vertical/both
	//.         JSplitPane
	//.           - Horizonatal/Vertical
	//.           - % top/left split
	//.         JTabbedPane
	//.          - String name
	public static String CONTAINER_TYPE_PANEL = "panel";
	public static String CONTAINER_TYPE_TABBED_PANEL = "tabbedPanel";
	public static String CONTAINER_TYPE_SPLIT_PANEL = "splitPanel";
	public static String CONTAINER_TYPE_SCROLL_PANEL = "scrollPanel";
	public static String LAYOUT_GRID_BAG = "gridbag";
	public static String LAYOUT_GRID = "grid";
	//. TODO support border layout?
	private int gridXSize = 0;
	private int gridYSize = 0;
	private int padX = 0;
	private int padY = 0;
	private String layoutManager = "";
	
	private ArrayList<GUIComponent> components;
	public GUIContainer(String name, JComponent component) {
		super(name, component);
		components = new ArrayList<GUIComponent>(1);
	}
	public ArrayList<GUIComponent> getComponents() {
		return components;
	}
	public int getGridXSize() {
		return gridXSize;
	}
	public int getGridYSize() {
		return gridYSize;
	}
	public void setGridXSize(int gridXSize) {
		this.gridXSize = gridXSize;
	}
	public void setGridYSize(int gridYSize) {
		this.gridYSize = gridYSize;
	}
	public void setPadX(int padX) {
		this.padX = padX;
	}
	public int getPadX() {
		return padX;
	}
	public void setPadY(int padY) {
		this.padY = padY;
	}
	public int getPadY() {
		return padY;
	}
	public String getLayoutManager() {
		return layoutManager;
	}
	public void setLayoutManager(String layoutManager) {
		this.layoutManager = layoutManager;
	}
}
