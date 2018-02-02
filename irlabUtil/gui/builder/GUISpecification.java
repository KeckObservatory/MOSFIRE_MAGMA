package edu.ucla.astro.irlab.util.gui.builder;

import java.awt.MediaTracker;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import edu.ucla.astro.irlab.util.FileUtilities;
import edu.ucla.astro.irlab.util.InvalidEnvironmentVariableException;

//. notes: probably should store screen size and location here, for serialization

public class GUISpecification {
	public static final int NULL_SCREEN_LOCATION_VALUE = -999999;
	private String name;
	private String title = "";
	private String acronym;
	private String layoutFilename;
	private String iconFilename;
	private ImageIcon icon;
	private File layoutFile;
	private int screenLocationX = NULL_SCREEN_LOCATION_VALUE;
	private int screenLocationY = NULL_SCREEN_LOCATION_VALUE;
	private int sizeX = 0;
	private int sizeY = 0;
	private boolean visible=false;
	private boolean processController=false;
	public static String LAYOUT_CLASS_PREFIX = "class:";

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAcronym() {
		return acronym;
	}
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}
	public ImageIcon getIcon() {
		return icon;
	}
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
		this.iconFilename = icon.getDescription();
	}
	public String getIconFilename() {
		return iconFilename;
	}
	public void setIconFilename(String iconFilename) throws InvalidEnvironmentVariableException {
		this.iconFilename = iconFilename;
	  
		this.icon = new ImageIcon(FileUtilities.replaceEnvironmentVariables(iconFilename));
		if (icon.getImageLoadStatus() != MediaTracker.COMPLETE)
			this.icon = null;
	}
	public File getLayoutFile() {
		return layoutFile;
	}
	public void setLayoutFile(File layoutFile) {
		this.layoutFile = layoutFile;
	}
	public String getLayoutFilename() {
		return layoutFilename;
	}
	public void setLayoutFilename(String layoutFilename) throws IOException, InvalidEnvironmentVariableException {
		this.layoutFilename = layoutFilename;
		if (!layoutFilename.startsWith(LAYOUT_CLASS_PREFIX)) {
			this.layoutFile = new File(FileUtilities.replaceEnvironmentVariables(layoutFilename));
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScreenLocationX() {
		return screenLocationX;
	}
	public void setScreenLocationX(int screenLocationX) {
		this.screenLocationX = screenLocationX;
	}
	public int getScreenLocationY() {
		return screenLocationY;
	}
	public void setScreenLocationY(int screenLocationY) {
		this.screenLocationY = screenLocationY;
	}
	public int getSizeX() {
		return sizeX;
	}
	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}
	public int getSizeY() {
		return sizeY;
	}
	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public boolean isProcessController() {
		return processController;
	}
	public void setProcessController(boolean processController) {
		this.processController = processController;
	}


}
