package edu.ucla.astro.irlab.util.gui.builder;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Dimension;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import javax.swing.text.JTextComponent;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JProgressBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jdom.*;

import edu.ucla.astro.irlab.util.Property;
import edu.ucla.astro.irlab.util.PropertyList;
import edu.ucla.astro.irlab.util.PropertySetter;
import edu.ucla.astro.irlab.util.ValueFormatter;
import edu.ucla.astro.irlab.util.ValueValidator;
import edu.ucla.astro.irlab.util.gui.MechButton;
import edu.ucla.astro.irlab.util.gui.StatusLightPanel;

//. note: for grid layout, add null or blank components in holes in arraylist

public class GUILayout {
	private GUIContainer mainContainer;
	private Point screenLocation;
	private Dimension screenSize;
	private String name = "";
	private String title = "";
	public static String WIDGET_LABEL = "label";
	public static String WIDGET_FIELD = "field";
	public static String WIDGET_MECH_BUTTON = "mechButton";
	public static String WIDGET_PROGRESS_BAR = "progressBar";
	public static String WIDGET_STATUS_LIGHT = "statusLight";
	public static String WIDGET_WARNING_LIGHT = "warningLight";
	public static String WIDGET_COMBO_BOX = "combo";
	public static String WIDGET_DO_BUTTON = "doButton";
	public static String COMPONENT_TYPE_STATUS = "status";
	public static String COMPONENT_TYPE_CONTROL = "control";
	public static String COMPONENT_TYPE_SET_CONTROL = "setcontrol";
	public static String COMPONENT_FONT_STYLE_BOLD = "bold";
	public static String COMPONENT_FONT_STYLE_ITALICS = "italics";
	public static String COMPONENT_FONT_STYLE_BOLD_ITALICS = "bold+italics";
	public static String BORDER_TYPE_RAISED_ETCHED = "raised_etched";
	public static String BORDER_TYPE_LOWERED_ETCHED = "lowered_etched";
	public static String BORDER_TYPE_LINE = "line";
	public static String BORDER_TYPE_RAISED_BEVEL = "raised_bevel";
	public static String BORDER_TYPE_LOWERED_BEVEL = "lowered_bevel";
	public static String BORDER_TYPE_EMPTY ="empty";
	public static String ALIGNMENT_NORTHWEST = "northwest";
	public static String ALIGNMENT_NORTH = "north";
	public static String ALIGNMENT_NORTHEAST = "northeast";
	public static String ALIGNMENT_WEST = "west";
	public static String ALIGNMENT_CENTER = "center";
	public static String ALIGNMENT_EAST = "east";
	public static String ALIGNMENT_SOUTHWEST = "southwest";
	public static String ALIGNMENT_SOUTH = "south";
	public static String ALIGNMENT_SOUTHEAST = "southeast";
	public static String ALIGNMENT_LEFT = "left";
	public static String ALIGNMENT_RIGHT = "right";
	public static String ALIGNMENT_TOP = "top";
	public static String ALIGNMENT_BOTTOM = "bottom";
	public static String ALIGNMENT_MIDDLE = "middle";
	
	public static String XML_ROOT_ATTRIBUTE_MANAGER = "manager";
	public static String XML_ROOT_ATTRIBUTE_PACKAGE = "package";
	public static String XML_ROOT_ATTRIBUTE_TITLE = "title";
	public static String XML_ROOT_ATTRIBUTE_X_POSITION = "xpos";
	public static String XML_ROOT_ATTRIBUTE_Y_POSITION = "ypos";
	public static String XML_ROOT_ATTRIBUTE_X_SIZE = "xsize";
	public static String XML_ROOT_ATTRIBUTE_Y_SIZE = "ysize";
	public static String XML_ROOT = "guilayout";
	
	public static String XML_ELEMENT_CONTAINER = "container";
	public static String XML_CONTAINER_ATTRIBUTE_NAME = "name";
	public static String XML_CONTAINER_ATTRIBUTE_TYPE = "type";
	public static String XML_CONTAINER_ATTRIBUTE_GRID_X = "x";
	public static String XML_CONTAINER_ATTRIBUTE_GRID_Y = "y";
	public static String XML_CONTAINER_ATTRIBUTE_PAD_X = "xPad";
	public static String XML_CONTAINER_ATTRIBUTE_PAD_Y = "yPad";
	public static String XML_CONTAINER_ATTRIBUTE_X_SIZE = "xsize";
	public static String XML_CONTAINER_ATTRIBUTE_Y_SIZE = "ysize";
	public static String XML_CONTAINER_ATTRIBUTE_WIDTH = "width";
	public static String XML_CONTAINER_ATTRIBUTE_HEIGHT = "height";
	public static String XML_CONTAINER_ATTRIBUTE_ALIGNMENT = "align";
	public static String XML_CONTAINER_ATTRIBUTE_MANAGER = "manager";
	public static String XML_CONTAINER_ATTRIBUTE_BORDER = "border";
	public static String XML_CONTAINER_ATTRIBUTE_BORDER_TITLE = "bordertitle";
	
	public static String XML_ELEMENT_WIDGET = "widget";
	public static String XML_WIDGET_ATTRIBUTE_NAME = "name";
	public static String XML_WIDGET_ATTRIBUTE_TYPE = "type";
	public static String XML_WIDGET_ATTRIBUTE_FONT_NAME = "fontName";
	public static String XML_WIDGET_ATTRIBUTE_FONT_SIZE = "fontSize";
	public static String XML_WIDGET_ATTRIBUTE_FONT_STYLE = "fontStyle";
	public static String XML_WIDGET_ATTRIBUTE_PROPERTY = "property";
	public static String XML_WIDGET_ATTRIBUTE_PROPERTY_SET_VALUE = "propertySetValue";
	public static String XML_WIDGET_ATTRIBUTE_GRID_X = "x";
	public static String XML_WIDGET_ATTRIBUTE_GRID_Y = "y";
	public static String XML_WIDGET_ATTRIBUTE_WIDTH = "width";
	public static String XML_WIDGET_ATTRIBUTE_HEIGHT = "height";
	public static String XML_WIDGET_ATTRIBUTE_ALIGNMENT = "align";
	public static String XML_WIDGET_ATTRIBUTE_UNITS = "units";
	public static String XML_WIDGET_ATTRIBUTE_WIDGET = "widget";
	public static String XML_WIDGET_ATTRIBUTE_VALUE = "value";
	public static String XML_WIDGET_ATTRIBUTE_ICON = "icon";
	public static String XML_WIDGET_ATTRIBUTE_VALUE_VALIDATOR = "valueValidator";
	public static String XML_WIDGET_ATTRIBUTE_VALUE_FORMATTER = "valueFormatter";
	public static String XML_WIDGET_ATTRIBUTE_X_SIZE = "xsize";
	public static String XML_WIDGET_ATTRIBUTE_Y_SIZE = "ysize";
	public static String XML_WIDGET_ATTRIBUTE_BACKGROUND_COLOR = "bgcolor";
	public static String XML_WIDGET_ATTRIBUTE_FOREGROUND_COLOR = "fgcolor";
	
	public GUILayout (File layoutConfigFile) throws JDOMException, IOException, InvalidGUILayoutException {
		screenLocation = new Point(0,0);
		screenSize = new Dimension(1,1);
		mainContainer = new GUIContainer("contentPane",new JPanel());
		openConfigFile(layoutConfigFile);
	}
	
	public void openConfigFile(File configFile) throws JDOMException, IOException, InvalidGUILayoutException {
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
		org.jdom.Document myDoc = builder.build(configFile);
		
		Attribute workingAtt;
		//. get root element.
		Element root=myDoc.getRootElement();
		//. check that it is a guilayout
		if (root.getName().compareTo(XML_ROOT) != 0)
			throw new JDOMException("Root element must be "+XML_ROOT);
		
		//. get root element attributes
		workingAtt = root.getAttribute(XML_ROOT_ATTRIBUTE_MANAGER);
		if (workingAtt != null)
			mainContainer.setLayoutManager(workingAtt.getValue());
		
		//. todo: confirm allowed layout
		
		int xsize=1;
		int ysize=1;
		//. get xsize and ysize
		workingAtt = root.getAttribute(XML_ROOT_ATTRIBUTE_X_SIZE);
		if (workingAtt != null)
			xsize = workingAtt.getIntValue();
		workingAtt = root.getAttribute(XML_ROOT_ATTRIBUTE_Y_SIZE);
		if (workingAtt != null)
			ysize = workingAtt.getIntValue();
		
		screenSize = new Dimension(xsize, ysize);
		
		int xpos=0;
		int ypos=0;
		//. get screen location
		workingAtt  = root.getAttribute(XML_ROOT_ATTRIBUTE_X_POSITION);
		if (workingAtt != null)
			xpos = workingAtt.getIntValue();
		workingAtt = root.getAttribute(XML_ROOT_ATTRIBUTE_Y_POSITION);
		if (workingAtt != null)
			ypos = workingAtt.getIntValue();
		
		screenLocation = new Point(xpos, ypos);
		
		workingAtt  = root.getAttribute(XML_ROOT_ATTRIBUTE_TITLE);
		if (workingAtt != null)
			title = workingAtt.getValue();
		
		//. get children elements
		List<Element> elements=root.getChildren();
		
		recursePanel(elements, mainContainer);
	}
	private void recursePanel(List<Element> elements, GUIContainer panel) throws InvalidGUILayoutException {
		Attribute workingAtt;
		int alignment=GUIComponent.ALIGNMENT_CENTER;
		String componentType = "";
		
		for (Element current : elements) {
			
			//. check
			if (current.getName().compareTo(XML_ELEMENT_WIDGET) == 0) {
				
				//. create a JLabel as a default component
				JComponent component = new JLabel();
				//. get type of widget
				workingAtt=current.getAttribute(XML_WIDGET_ATTRIBUTE_WIDGET);
				if (workingAtt == null) {
					//. error
				}	else {
					if (workingAtt.getValue().compareTo(WIDGET_LABEL) == 0) {
						component = new JLabel(" ");
					} else if (workingAtt.getValue().compareTo(WIDGET_FIELD) == 0) {
						component = new JTextField(" ");
					} else if (workingAtt.getValue().compareTo(WIDGET_COMBO_BOX) == 0) {
						component = new JComboBox();
					} else if (workingAtt.getValue().compareTo(WIDGET_DO_BUTTON) == 0) {
						component = new JButton();
					} else if (workingAtt.getValue().compareTo(WIDGET_MECH_BUTTON) == 0) {
						try {
							component = new MechButton();
						} catch (Exception ex) {
							ex.printStackTrace();
							throw new InvalidGUILayoutException("Error creating mech button: "+ex.getMessage());
						}
					} else if (workingAtt.getValue().compareTo(WIDGET_PROGRESS_BAR) == 0) {
						component = new JProgressBar(0,100);
						((JProgressBar)component).setStringPainted(true);
					} else if (workingAtt.getValue().compareTo(WIDGET_STATUS_LIGHT) == 0) {
						try {
							component = new StatusLightPanel(false, true);
							((StatusLightPanel)component).setOnText("ON");
							((StatusLightPanel)component).setOffText("OFF");
						} catch (Exception ex){
							ex.printStackTrace();
							throw new InvalidGUILayoutException("Error creating status light: "+ex.getMessage());
						}
					} else if (workingAtt.getValue().compareTo(WIDGET_WARNING_LIGHT) == 0) {
						try {
							component = new StatusLightPanel(false, true);
							((StatusLightPanel)component).setOnText("WARN");
							((StatusLightPanel)component).setOffText("OK");
							((StatusLightPanel)component).setOnColor(new Color(200,0,0));
							((StatusLightPanel)component).setOffColor(new Color(50,0,0));
						} catch (Exception ex){
							ex.printStackTrace();
							throw new InvalidGUILayoutException("Error creating warning light: "+ex.getMessage());
						}
					} else {
						//. some other generic class.  class must support a setValue(Object) method
						try {
							
							//. get class
							Class<?> clazz = GUILayout.class.getClassLoader().loadClass(workingAtt.getValue());
							
							//. load constructor
							Constructor<?> constructor = clazz.getConstructor(new Class[] {});
							
							
							Object object = constructor.newInstance(new Object[] {});
							
							if (object instanceof JComponent)
								component = (JComponent)object;
							else {
								throw new InvalidGUILayoutException("Invalid widget <"+workingAtt.getValue()+">.  Must be a subclass of JComponent.");
							}
						} catch (ClassNotFoundException  cnfEx) {
							cnfEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> not found.");
						} catch (NoSuchMethodException nsmEx) {
							nsmEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> does not have a parameterless constructor.");
						} catch (InstantiationException  iEx) {
							iEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> cannot be instantiated.");
						} catch (IllegalAccessException iaEx) {
							iaEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> constructor is inaccessible.");
						} catch (InvocationTargetException itEx) {
							itEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> is abstract.");
						}
						
					}
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_TYPE);
					if (workingAtt != null) {
						componentType = workingAtt.getValue();
						if (componentType.compareTo(COMPONENT_TYPE_STATUS) == 0) {
							if (component instanceof JTextComponent)
								((JTextComponent)component).setEditable(false);
							else if (component instanceof StatusLightPanel)
								((StatusLightPanel)component).setIsControllable(false);
						}
					}
					
					boolean setFont=false;
					String fontFace ="Default";
					int fontSize = 12;
					int fontStyle = Font.PLAIN;
					
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_FONT_NAME);
					if (workingAtt != null) {
						setFont=true;
						fontFace = workingAtt.getValue();
					}
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_FONT_SIZE);
					if (workingAtt != null) {
						try {
							fontSize = workingAtt.getIntValue();
							setFont=true;
						} catch (DataConversionException dcEx) {
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Font size <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_FONT_STYLE);
					if (workingAtt != null) {
						setFont=true;
						if (workingAtt.getValue().compareTo(COMPONENT_FONT_STYLE_BOLD) == 0)
							fontStyle = Font.BOLD;
						else if (workingAtt.getValue().compareTo(COMPONENT_FONT_STYLE_ITALICS) == 0)
							fontStyle = Font.ITALIC;
						else if (workingAtt.getValue().compareTo(COMPONENT_FONT_STYLE_BOLD_ITALICS) == 0)
							fontStyle = Font.BOLD+Font.ITALIC;
						else 
							throw new InvalidGUILayoutException("Unknown font style <"+workingAtt.getValue()+">.");
					}
					
					if (setFont) {
						component.setFont(new Font(fontFace, fontStyle, fontSize));
					}
					
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_BACKGROUND_COLOR);
					if (workingAtt != null) {
						//. color should be in FFFFFF format
						String value = workingAtt.getValue();
						try {
							int color = Integer.parseInt(value, 16);
							component.setBackground(new Color(color));
						} catch (NumberFormatException nfEx) {
							throw new InvalidGUILayoutException("Invalid background color <"+value+">.  Must be hex value in XXXXXX format.");
						}
					}
					
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_FOREGROUND_COLOR);
					if (workingAtt != null) {
						//. color should be in FFFFFF format
						String value = workingAtt.getValue();
						try {
							int color = Integer.parseInt(value, 16);
							component.setForeground(new Color(color));
						} catch (NumberFormatException nfEx) {
							throw new InvalidGUILayoutException("Invalid foreground color <"+value+">.  Must be hex value in XXXXXX format.");
						}
					}
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_ICON);
					if (workingAtt != null) {
						if (component instanceof JLabel) {
							URL iconURL = getClass().getResource(workingAtt.getValue());
							if (iconURL == null) {
								try {
									iconURL = new URL(workingAtt.getValue());
								} catch (MalformedURLException ex) {
									iconURL = null;
								}
							}
							if (iconURL != null) {
								((JLabel)component).setIcon(new ImageIcon(iconURL));
							} else {
								System.out.println("ICON attribute must be a valid class resource (/javaPackage/file) or URL (e.g. file:/path/file)");
							}
						}
					}
					
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_ALIGNMENT);
					if (workingAtt != null) {
						if (workingAtt.getValue().compareTo(ALIGNMENT_NORTHWEST) == 0) {
							alignment=GUIComponent.ALIGNMENT_NORTHWEST;
							if (component instanceof JLabel)
								((JLabel)component).setHorizontalAlignment(SwingConstants.LEFT);
						} else if ((workingAtt.getValue().compareTo(ALIGNMENT_NORTH) == 0) || 
								(workingAtt.getValue().compareTo(ALIGNMENT_TOP) == 0)) {
							alignment=GUIComponent.ALIGNMENT_NORTH;
							if (component instanceof JLabel)
								((JLabel)component).setHorizontalAlignment(SwingConstants.CENTER);
						} else if (workingAtt.getValue().compareTo(ALIGNMENT_NORTHEAST) == 0) {
							alignment=GUIComponent.ALIGNMENT_NORTHEAST;
							if (component instanceof JLabel)
								((JLabel)component).setHorizontalAlignment(SwingConstants.RIGHT);
						} else if ((workingAtt.getValue().compareTo(ALIGNMENT_WEST) == 0) || 
								(workingAtt.getValue().compareTo(ALIGNMENT_LEFT) == 0)) {
							alignment=GUIComponent.ALIGNMENT_WEST;
							if (component instanceof JLabel)
								((JLabel)component).setHorizontalAlignment(SwingConstants.LEFT);
						} else if ((workingAtt.getValue().compareTo(ALIGNMENT_CENTER) == 0) ||
							(workingAtt.getValue().compareTo(ALIGNMENT_MIDDLE) ==0)) {
							alignment=GUIComponent.ALIGNMENT_CENTER;
							if (component instanceof JLabel)
								((JLabel)component).setHorizontalAlignment(SwingConstants.CENTER);
						} else if ((workingAtt.getValue().compareTo(ALIGNMENT_EAST) == 0) || 
								(workingAtt.getValue().compareTo(ALIGNMENT_RIGHT) == 0)) {
							alignment=GUIComponent.ALIGNMENT_EAST;
							if (component instanceof JLabel)
								((JLabel)component).setHorizontalAlignment(SwingConstants.RIGHT);
						} else if (workingAtt.getValue().compareTo(ALIGNMENT_SOUTHWEST) == 0) {
							alignment=GUIComponent.ALIGNMENT_SOUTHWEST;
							if (component instanceof JLabel)
								((JLabel)component).setHorizontalAlignment(SwingConstants.LEFT);
						} else if ((workingAtt.getValue().compareTo(ALIGNMENT_SOUTH) == 0) || 
								(workingAtt.getValue().compareTo(ALIGNMENT_BOTTOM) == 0)) {
							alignment=GUIComponent.ALIGNMENT_SOUTH;
							if (component instanceof JLabel)
								((JLabel)component).setHorizontalAlignment(SwingConstants.CENTER);
						} else if (workingAtt.getValue().compareTo(ALIGNMENT_SOUTHEAST) == 0) {
							alignment=GUIComponent.ALIGNMENT_SOUTHEAST;							
							if (component instanceof JLabel)
								((JLabel)component).setHorizontalAlignment(SwingConstants.RIGHT);
						} else {
							throw new InvalidGUILayoutException("Alignment <"+workingAtt.getValue()+"> is invalid.");
						}
					}
					
					Dimension dim = component.getPreferredSize();
					boolean setDim = false;
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_X_SIZE);
					if (workingAtt != null) {
						try {
							dim.width = workingAtt.getIntValue();
							setDim = true;
						} catch (DataConversionException dcEx) {
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Widget X size <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_Y_SIZE);
					if (workingAtt != null) {
						try {
							dim.height = workingAtt.getIntValue();
							setDim=true;
						} catch (DataConversionException dcEx) {
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Widget Y size <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
					if (setDim) {
						component.setPreferredSize(dim);
						if (component instanceof JLabel) {
							component.setMinimumSize(dim);
						}
					}
					
					String name, propertyName, propertySetValue;
					
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_NAME);
					if (workingAtt == null)
						name = "";
					else
						name = workingAtt.getValue();
					
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_PROPERTY);
					if (workingAtt == null)
						propertyName = "";
					else
						propertyName = workingAtt.getValue();
					
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_PROPERTY_SET_VALUE);
					if (workingAtt == null)
						propertySetValue = "";
					else
						propertySetValue = workingAtt.getValue();
					
					GUIWidget guiComponent;
					if (componentType.compareTo(GUIWidget.WIDGET_TYPE_SET_CONTROL) == 0) {
						guiComponent = new SetterGUIWidget(name, component);				
					} else {
						guiComponent = new GUIWidget(name, component);
						if (componentType.compareTo(GUIWidget.WIDGET_TYPE_CONTROL) == 0) {
							guiComponent.implementControl();
						}
					}
					guiComponent.setWidgetType(componentType);
					guiComponent.setPropertyName(propertyName);
					guiComponent.setPropertySetValue(propertySetValue);
					
					workingAtt=current.getAttribute(XML_WIDGET_ATTRIBUTE_VALUE);
					if (workingAtt != null) {
						String label = workingAtt.getValue();
						guiComponent.setValue(label);
					}
					
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_VALUE_VALIDATOR);
					if (workingAtt != null) {
						try {
							
							//. get class
							Class<?> clazz = GUILayout.class.getClassLoader().loadClass(workingAtt.getValue());
							
							//. load constructor
							Constructor<?> constructor = clazz.getConstructor(new Class[] {});
							
							
							Object object = constructor.newInstance(new Object[] {});
							
							if (object instanceof ValueValidator) {
								guiComponent.setValueValidator((ValueValidator)object);
							} else {
								throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> is not an instance of ValueValidator.");
							}
						} catch (ClassNotFoundException  cnfEx) {
							cnfEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> not found.");
						} catch (NoSuchMethodException nsmEx) {
							nsmEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> does not have a parameterless constructor.");
						} catch (InstantiationException  iEx) {
							iEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> cannot be instantiated.");
						} catch (IllegalAccessException iaEx) {
							iaEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> constructor is inaccessible.");
						} catch (InvocationTargetException itEx) {
							itEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> is abstract.");
						}
					}

					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_VALUE_FORMATTER);
					if (workingAtt != null) {
						try {
							
							//. get class
							Class<?> clazz = GUILayout.class.getClassLoader().loadClass(workingAtt.getValue());
							
							//. load constructor
							Constructor<?> constructor = clazz.getConstructor(new Class[] {});
							
							
							Object object = constructor.newInstance(new Object[] {});
							
							if (object instanceof ValueFormatter) {
								guiComponent.setValueFormatter((ValueFormatter)object);
							} else {
								throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> is not an instance of ValueFormatter.");
							}
						} catch (ClassNotFoundException  cnfEx) {
							cnfEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> not found.");
						} catch (NoSuchMethodException nsmEx) {
							nsmEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> does not have a parameterless constructor.");
						} catch (InstantiationException  iEx) {
							iEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> cannot be instantiated.");
						} catch (IllegalAccessException iaEx) {
							iaEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> constructor is inaccessible.");
						} catch (InvocationTargetException itEx) {
							itEx.printStackTrace();
							throw new InvalidGUILayoutException("Class <"+workingAtt.getValue()+"> is abstract.");
						}
					}
					
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_WIDTH);
					if (workingAtt != null) {
						try {
							guiComponent.setWidth(workingAtt.getIntValue());
						} catch (DataConversionException dcEx) {
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Widget width <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_HEIGHT);
					if (workingAtt != null) {
						try {
							guiComponent.setHeight(workingAtt.getIntValue());
						} catch (DataConversionException dcEx) {
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Widget height <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
										
					int gridX=0;
					int gridY=0;
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_GRID_X);
					if (workingAtt != null) {
						try {
							gridX = workingAtt.getIntValue();
						} catch (DataConversionException dcEx) {
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Widget X location <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_GRID_Y);
					if (workingAtt != null) {
						try {
							gridY = workingAtt.getIntValue();
						} catch (DataConversionException dcEx) {
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Widget Y location <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
					addComponentToContainer(guiComponent, panel, gridX, gridY);
				} 
			} else if (current.getName().compareTo(XML_ELEMENT_CONTAINER) == 0) {
				JComponent component = new JPanel();
				
				//. get type of widget
				workingAtt=current.getAttribute(XML_CONTAINER_ATTRIBUTE_TYPE);
				if (workingAtt == null) {
					//. error
				}	else {
					if (workingAtt.getValue().compareTo(GUIContainer.CONTAINER_TYPE_PANEL) == 0) {
						component = new JPanel();
					} else if (workingAtt.getValue().compareTo(GUIContainer.CONTAINER_TYPE_SCROLL_PANEL) == 0) {
						component = new JScrollPane();
					} else if (workingAtt.getValue().compareTo(GUIContainer.CONTAINER_TYPE_SPLIT_PANEL) == 0) {
						component = new JSplitPane();
					} else if (workingAtt.getValue().compareTo(GUIContainer.CONTAINER_TYPE_TABBED_PANEL) == 0) {
						component = new JTabbedPane();
					} else {
						throw new InvalidGUILayoutException("Container type <"+workingAtt.getValue()+"> is invalid.");
					}
					workingAtt = current.getAttribute(XML_CONTAINER_ATTRIBUTE_BORDER);
					if (workingAtt != null){
						Border border;
						if (workingAtt.getValue().compareToIgnoreCase(BORDER_TYPE_LINE) == 0) {
							border = BorderFactory.createLineBorder(Color.BLACK);
						} else if (workingAtt.getValue().compareToIgnoreCase(BORDER_TYPE_LOWERED_BEVEL) == 0) {
							border = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						} else if (workingAtt.getValue().compareToIgnoreCase(BORDER_TYPE_RAISED_BEVEL) == 0) {
							border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						} else if (workingAtt.getValue().compareToIgnoreCase(BORDER_TYPE_LOWERED_ETCHED) == 0) {
							border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
						} else if (workingAtt.getValue().compareToIgnoreCase(BORDER_TYPE_RAISED_ETCHED) == 0) {
							border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
						} else {
							border = BorderFactory.createEmptyBorder();
						}
						
						workingAtt = current.getAttribute(XML_CONTAINER_ATTRIBUTE_BORDER_TITLE);
						if (workingAtt != null) {
							component.setBorder(new TitledBorder(border, workingAtt.getValue()));
						} else {
							component.setBorder(border);
						}
						
					}
					workingAtt = current.getAttribute(XML_WIDGET_ATTRIBUTE_ALIGNMENT);
					if (workingAtt != null) {
						if (workingAtt.getValue().compareTo(ALIGNMENT_NORTHWEST) == 0) {
							alignment=GUIComponent.ALIGNMENT_NORTHWEST;
						} else if ((workingAtt.getValue().compareTo(ALIGNMENT_NORTH) == 0) || 
								(workingAtt.getValue().compareTo(ALIGNMENT_TOP) == 0)) {
							alignment=GUIComponent.ALIGNMENT_NORTH;
						} else if (workingAtt.getValue().compareTo(ALIGNMENT_NORTHEAST) == 0) {
							alignment=GUIComponent.ALIGNMENT_NORTHEAST;
						} else if ((workingAtt.getValue().compareTo(ALIGNMENT_WEST) == 0) || 
								(workingAtt.getValue().compareTo(ALIGNMENT_LEFT) == 0)) {
							alignment=GUIComponent.ALIGNMENT_WEST;
						} else if (workingAtt.getValue().compareTo(ALIGNMENT_CENTER) == 0) {
							alignment=GUIComponent.ALIGNMENT_CENTER;
						} else if ((workingAtt.getValue().compareTo(ALIGNMENT_EAST) == 0) || 
								(workingAtt.getValue().compareTo(ALIGNMENT_RIGHT) == 0)) {
							alignment=GUIComponent.ALIGNMENT_EAST;
						} else if (workingAtt.getValue().compareTo(ALIGNMENT_SOUTHWEST) == 0) {
							alignment=GUIComponent.ALIGNMENT_SOUTHWEST;
						} else if ((workingAtt.getValue().compareTo(ALIGNMENT_SOUTH) == 0) || 
								(workingAtt.getValue().compareTo(ALIGNMENT_BOTTOM) == 0)) {
							alignment=GUIComponent.ALIGNMENT_SOUTH;
						} else if (workingAtt.getValue().compareTo(ALIGNMENT_SOUTHEAST) == 0) {
							alignment=GUIComponent.ALIGNMENT_SOUTHEAST;							
						} else {
							throw new InvalidGUILayoutException("Alignment <"+workingAtt.getValue()+"> is invalid.");
						}
					}
					
					String name = "";
					workingAtt=current.getAttribute(XML_CONTAINER_ATTRIBUTE_NAME);
					if (workingAtt != null) {
						name = workingAtt.getValue();
						component.setName(name);
					}
					
					Dimension dim = component.getPreferredSize();
					boolean setDim = false;
					workingAtt = current.getAttribute(XML_CONTAINER_ATTRIBUTE_X_SIZE);
					if (workingAtt != null) {
						try {
							dim.width = workingAtt.getIntValue();
							setDim = true;
						} catch (DataConversionException dcEx) {
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Container X size <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
					workingAtt = current.getAttribute(XML_CONTAINER_ATTRIBUTE_Y_SIZE);
					if (workingAtt != null) {
						try {
							dim.height = workingAtt.getIntValue();
							setDim=true;
						} catch (DataConversionException dcEx) {
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Container Y size <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
					if (setDim) {
						component.setPreferredSize(dim);
						if (component instanceof JLabel) {
							component.setMinimumSize(dim);
						}
					}

					GUIContainer guiComponent = new GUIContainer(name, component);
					
					guiComponent.setAlignment(alignment);
					
					workingAtt = current.getAttribute(XML_CONTAINER_ATTRIBUTE_MANAGER);
					if (workingAtt != null) {
						//. todo error handle
						guiComponent.setLayoutManager(workingAtt.getValue());
					}
					
					workingAtt = current.getAttribute(XML_CONTAINER_ATTRIBUTE_WIDTH);
					if (workingAtt != null) {
						try {
							guiComponent.setWidth(workingAtt.getIntValue());
						} catch (DataConversionException dcEx) {
							//. todo. handle
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Container width <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
					workingAtt = current.getAttribute(XML_CONTAINER_ATTRIBUTE_HEIGHT);
					if (workingAtt != null) {
						try {
							guiComponent.setHeight(workingAtt.getIntValue());
						} catch (DataConversionException dcEx) {
							//. todo. handle
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Container height <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
					
					workingAtt = current.getAttribute(XML_CONTAINER_ATTRIBUTE_PAD_X);
					if (workingAtt != null) {
						try {
							guiComponent.setPadX(workingAtt.getIntValue());
						} catch (DataConversionException dcEx) {
							//. todo. handle
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Container x padding <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
					workingAtt = current.getAttribute(XML_CONTAINER_ATTRIBUTE_PAD_Y);
					if (workingAtt != null) {
						try {
							guiComponent.setPadY(workingAtt.getIntValue());
						} catch (DataConversionException dcEx) {
							//. todo. handle
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Container x padding <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}

					int gridX=0;
					int gridY=0;
					workingAtt = current.getAttribute(XML_CONTAINER_ATTRIBUTE_GRID_X);
					if (workingAtt != null) {
						try {
							gridX = workingAtt.getIntValue();
						} catch (DataConversionException dcEx) {
							//. todo. handle
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Container x location <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
					workingAtt = current.getAttribute(XML_CONTAINER_ATTRIBUTE_GRID_Y);
					if (workingAtt != null) {
						try {
							gridY = workingAtt.getIntValue();
						} catch (DataConversionException dcEx) {
							//. todo. handle
							dcEx.printStackTrace();
							throw new InvalidGUILayoutException("Container y location <"+workingAtt.getValue()+"> cannot be parsed to an integer.");
						}
					}
					addComponentToContainer(guiComponent, panel, gridX, gridY);
					
					List<Element> newElements = current.getChildren();
					recursePanel(newElements, guiComponent);
				}
			}
		}
	}
	private void addComponentToContainer(GUIComponent guiComponent, GUIContainer panel, int gridX, int gridY) {
		
		int gridXSize = panel.getGridXSize();
		int gridYSize = panel.getGridYSize();
		
		guiComponent.setXGrid(gridX);
		
		if (gridX+1 > gridXSize)
			gridXSize = gridX+1;
		
		guiComponent.setYGrid(gridY);
		
		if (gridY+1 > gridYSize)
			gridYSize = gridY+1;
		
		
		panel.setGridXSize(gridXSize);
		panel.setGridYSize(gridYSize);
		
		ArrayList<GUIComponent> components = panel.getComponents();
		
		int componentListSize = (gridXSize)* (gridYSize);
		while (components.size() < componentListSize) {
			components.add(new GUIComponent("", new JPanel()));
		}
		components.set((gridXSize)*guiComponent.getYGrid()+guiComponent.getXGrid(), guiComponent);
	}
	
	public void setValueOfComponent(String propName, Object value) {
		setValueOfComponent(propName, value, mainContainer.getComponents());
	}
	public void setValueOfComponent(String propName, Object value, ArrayList<GUIComponent> components) {
		for (GUIComponent comp : components) {
			if (comp instanceof GUIContainer) {
				setValueOfComponent(propName, value, ((GUIContainer)comp).getComponents());
			} else if (comp instanceof GUIWidget) {
				//. todo: make setValue pass propName, so components can respond 
				//. to value changes of multiple properties
				if (((GUIWidget)comp).containsProperty(propName)) {
					if (((GUIWidget)comp).getWidgetType().compareTo(GUIWidget.WIDGET_TYPE_SET_CONTROL) != 0)
						((GUIWidget)comp).setValue(propName, value);
				}
			}
		}
	}
	public void registerComponentsWithSetter(PropertySetter setter) {
		registerComponentsWithSetter(setter, mainContainer.getComponents());
	}
	private void registerComponentsWithSetter(PropertySetter setter, ArrayList<GUIComponent> components) {
		for (GUIComponent comp : components) {
			if (comp instanceof GUIWidget) {
				((GUIWidget)comp).setPropertySetter(setter);
			} else if (comp instanceof GUIContainer) {
				registerComponentsWithSetter(setter, ((GUIContainer)comp).getComponents());
			}
		}
	}
	public void registerComponentsWithProperties(PropertyList list) {
		registerComponentsWithProperties(list, mainContainer.getComponents());
	}
	private void registerComponentsWithProperties(PropertyList list, ArrayList<GUIComponent> components) {
		for (GUIComponent comp : components) {
			if (comp instanceof GUIContainer) {
				//. if it is a container, recurse through container components
				registerComponentsWithProperties(list, ((GUIContainer)comp).getComponents());
			} else if (comp instanceof GUIWidget) {
				//. if it is a widget, get properties
				String[] widgetProperties = ((GUIWidget)comp).getPropertyNames();
				for (String propString : widgetProperties) {
					Property prop = list.getProperty(propString);
					if (prop != null) {
						//. set format, if specified
						if (prop.getFormat().length() > 0)
							((GUIWidget)comp).setFormat(propString, prop.getFormat());
						//. set default value
						((GUIWidget)comp).setValue(prop.getName(), prop.getValue());
						//. add property change handler
						prop.addPropertyChangeListener(new PropertyChangeListener(){
							public void propertyChange(PropertyChangeEvent evt) {
								setValueOfComponent(evt.getPropertyName(), evt.getNewValue());
							}
						});
					}
				}
			}
		}
	}
	public void populateListComponents(PropertyList list) {
		populateListComponents(list, mainContainer.getComponents());
	}
	public void populateListComponents(PropertyList list, ArrayList<GUIComponent> components) {
		for (GUIComponent comp: components) {
			if (comp instanceof GUIContainer) {
				//. if it is a containter, recurse through the container components
				populateListComponents(list, ((GUIContainer)comp).getComponents());
			} else if (comp instanceof GUIWidget) {
				//. if it is a widget component
				//. get property
				Property prop = list.getProperty(((GUIWidget)comp).getPropertyName());
				if (prop != null) {
					if (((GUIWidget)comp).getComponent() instanceof JComboBox) {
						if (!prop.getAllowedValues().isEmpty()) {
							((JComboBox)((GUIWidget)comp).getComponent()).setModel(
									new DefaultComboBoxModel(prop.getAllowedValues().toArray()));
						//	if (((GUIWidget)comp).getWidgetType().compareTo(GUIWidget.WIDGET_TYPE_CONTROL) == 0) {
						//		((GUIWidget)comp).implementControl();
						//	}
						}
					}
				}
			}
		}
	}
	public GUIComponent getComponent(String name) {
		return getComponent(name, mainContainer.getComponents());
	}
	private GUIComponent getComponent(String name, ArrayList<GUIComponent> components) {
		for (GUIComponent comp : components) {
			if (comp.getComponentName().compareTo(name) == 0) {
				return comp;
			} else {
				if (comp instanceof GUIContainer) {
					getComponent(name, ((GUIContainer)comp).getComponents());
				}
			}
		}
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	public Point getScreenLocation() {
		return screenLocation;
	}
	
	public void setScreenLocation(Point screenLocation) {
		this.screenLocation = screenLocation;
	}
	
	public Dimension getScreenSize() {
		return screenSize;
	}
	
	public void setScreenSize(Dimension screenSize) {
		this.screenSize = screenSize;
	}
	
	public GUIContainer getMainContainer() {
		return mainContainer;
	}
	
}
