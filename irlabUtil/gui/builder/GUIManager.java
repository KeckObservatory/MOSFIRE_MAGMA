package edu.ucla.astro.irlab.util.gui.builder;

import java.awt.Point;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameListener;


import org.jdom.*;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import edu.ucla.astro.irlab.util.InvalidEnvironmentVariableException;
import edu.ucla.astro.irlab.util.PropertyList;
import edu.ucla.astro.irlab.util.gui.ConvertibleFrame;
import edu.ucla.astro.irlab.util.process.ProcessController;
import edu.ucla.astro.irlab.util.process.ProcessListener;

//. TODO custom gui's
//. TODO change ifListener to ArrayList?
public class GUIManager {
	
	public static final String XML_ROOT = "guiList";
	public static final String XML_ELEMENT_GUI = "gui";
	public static final String XML_GUI_ATTRIBUTE_TITLE = "title";
	public static final String XML_GUI_ATTRIBUTE_ACRONYM = "acronym";
	public static final String XML_GUI_ATTRIBUTE_NAME = "name";
	public static final String XML_GUI_ATTRIBUTE_ICON = "icon";
	public static final String XML_GUI_ATTRIBUTE_LAYOUT = "layout";
	public static final String XML_GUI_ATTRIBUTE_XLOC = "xloc";
	public static final String XML_GUI_ATTRIBUTE_YLOC = "yloc";
	public static final String XML_GUI_ATTRIBUTE_XSIZE = "xs";
	public static final String XML_GUI_ATTRIBUTE_YSIZE = "ys";
	public static final String XML_GUI_ATTRIBUTE_VISIBLE = "visible";
	public static final String XML_GUI_ATTRIBUTE_PROCESS_CONTROLLER = "processController";
	
	ArrayList<GUISpecification> guiList;
	ArrayList<ProcessListener> processListenerList;
	ArrayList<ComponentListener> componentListenerList;
	private ArrayList<JInternalFrame> frameList;
	private JDesktopPane desktop;
	PropertyList propList;
	InternalFrameListener ifListener;
	public GUIManager() {
		guiList = new ArrayList<GUISpecification>();
		frameList = new ArrayList<JInternalFrame>();	
		processListenerList = new ArrayList<ProcessListener>();
		componentListenerList = new ArrayList<ComponentListener>();
	}
	public GUIManager(JDesktopPane desktopPane) {
		this();
		desktop =desktopPane;
	}
	public GUIManager(JDesktopPane desktopPane, PropertyList list) {
		this(desktopPane);
		propList = list;
	}
	public GUIManager(PropertyList list) {
		this();
		propList = list;
	}
	public void setDesktop(JDesktopPane desktop) {
		this.desktop = desktop;
	}
	
	public ArrayList<GUISpecification> getGUIList() {
		return guiList;
	}
	public void setGUIList(ArrayList<GUISpecification> guiList) {
		guiList.clear();
		this.guiList = guiList;
		frameList.clear();
		frameList = new ArrayList<JInternalFrame>();
		while(frameList.size() < guiList.size())
			frameList.add(null);
	}
	public void addGUI(GUISpecification spec) {
		guiList.add(spec);
		frameList.add(null);
	}
	public void addProcessListener(ProcessListener l) {
		processListenerList.add(l);
	}
	public void addComponentListener(ComponentListener l) {
		componentListenerList.add(l);
	}
	public JInternalFrame getGUI(String guiName) throws InvalidGUISpecificationException, JDOMException, IOException {
		int index=0;
		for (GUISpecification spec : guiList) {
			if (guiName.compareTo(spec.getName()) == 0)
				return getGUI(index, spec);
			else
				index++;
		}
		return null;
	}
	public JInternalFrame getGUI(int index) throws InvalidGUISpecificationException, JDOMException, IOException {
		try {
			return getGUI(index, guiList.get(index));
		} catch (ArrayIndexOutOfBoundsException aioobEx) {
			return null;
		}
	}
	public JInternalFrame getGUI(int index, GUISpecification spec) throws InvalidGUISpecificationException, JDOMException, IOException {
		//. if gui exists, return.
		if (frameList.get(index) != null) {
			return frameList.get(index);
		} else {
			JInternalFrame frame;
			
			//. if layout tag starts with "class", it must be a full program, and not just a GUI Builder GUI
			//. format is class:package.class [args]
			//. anything after the first whitespace are arguments. args is optional, no brackets
			if (spec.getLayoutFilename().startsWith(GUISpecification.LAYOUT_CLASS_PREFIX)) {

				//. create from that can be put in a desktop
				frame = new JInternalFrame();
				
				//. tokenize string
				String[] layoutParts = spec.getLayoutFilename().split(" ");
				
				//. get class name
				String className = layoutParts[0].substring(GUISpecification.LAYOUT_CLASS_PREFIX.length());

				String[] args = new String[0];
				if (layoutParts.length > 1) {
					args = new String[layoutParts.length-1];
					System.arraycopy(layoutParts, 1, args, 0, layoutParts.length-1);
				}
				
				try {
					//. get class
					Class clazz = GUIManager.class.getClassLoader().loadClass(className);
					Object frameObj;
					
					//. if no args
					if (args.length == 0) {
						//. look for constructor that takes a PropertyList object
						Constructor constructor = clazz.getConstructor(new Class[] {PropertyList.class});
						
						frameObj = constructor.newInstance(new Object[] {propList});
					} else {
						//. look for constructor that takes a String array for arguments, and a PropertyList object
						Constructor constructor = clazz.getConstructor(new Class[] {String[].class, JDesktopPane.class, PropertyList.class});
						
						frameObj = constructor.newInstance(new Object[] {args, desktop, propList});

					}
					//. check to see if listed as process controller
					if (spec.isProcessController()) {
						if (frameObj instanceof ProcessController) {
							for (ProcessListener l : processListenerList) {
								((ProcessController)frameObj).addProcessListener(l);
							}
						} else {
							throw new InvalidGUISpecificationException("Classes specified as processController must implement ProcessController interface.");
						}
					}
					
					if (frameObj instanceof JFrame) {
						frame.setContentPane(((JFrame)frameObj).getContentPane());
						if (spec.getTitle().isEmpty()) {
							frame.setTitle(spec.getAcronym());
						} else {
							frame.setTitle(spec.getTitle());
						}
					} else if (frameObj instanceof ConvertibleFrame) {
						ConvertibleFrame cframe = (ConvertibleFrame)frameObj;
						
						cframe.setName(spec.getName());
						
						if (!spec.getTitle().isEmpty()) {
							cframe.setTitle(spec.getTitle());
						}
						
						//. if it has an icon, use it
						if (spec.getIcon() != null) {
							cframe.setIcon(spec.getIcon());
						}
						//. allow frame to be resized.
						//. don't allow minimizing, because better to use toolbar than 
						//. minimized icons.
						cframe.setMaximizable(true);
						cframe.setResizable(true);

						//. allow closing of frames, but hide the gui rather than destroy
						cframe.setClosable(true);
						cframe.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
						
						//. if both sizeX and sizeY are specified (> 0), set size of GUI
						int sizeX, sizeY;
						sizeX=spec.getSizeX();
						if (sizeX > 0) {
							sizeY = spec.getSizeY();
							if (sizeY > 0) {
								cframe.setSize(sizeX, sizeY);
							}
						}
						
						//. if both locX and locY are specified, set location of GUI
						int locX, locY;
						locX=spec.getScreenLocationX();
						if (locX != GUISpecification.NULL_SCREEN_LOCATION_VALUE) {
							locY = spec.getScreenLocationY();
							if (locY != GUISpecification.NULL_SCREEN_LOCATION_VALUE) {
								cframe.setLocation(locX, locY);
							}
						}
						
						//. add internal frame listener if specified
						if (ifListener != null) {
							cframe.addInternalFrameListener(ifListener);
						}
						
						//. add component listener
						for (ComponentListener l : componentListenerList) {
							cframe.addComponentListener(l);
						}

												
						//. add frame to framelist
						frameList.set(index, cframe.getInternalFrame());

						//. return frame
						return cframe.getInternalFrame();
					} else {
						throw new InvalidGUISpecificationException("Invalid class. Must be a JFrame or ConvertibleFrame.");
					}
					


				} catch (NoSuchMethodException nsmEx) {
					throw new InvalidGUISpecificationException("Layout indicates a class that does not have the proper constructor.");
				} catch (Exception ex) {
					//. TODO: improve
					ex.printStackTrace();
					throw new InvalidGUISpecificationException("Invalid GUI Specification: "+ex.getMessage());
				}
			} else {
			
				try {
					GUILayout layout = new GUILayout(spec.getLayoutFile());

					//. register components with properties
					if (propList != null) {
				  	layout.populateListComponents(propList);
						layout.registerComponentsWithProperties(propList);
						layout.registerComponentsWithSetter(propList);
					}
		
					//. otherwise, create, and then return
					frame = GUIBuilder.createInternalGUI(layout);

				} catch (InvalidGUILayoutException iglEx) {
					throw new InvalidGUISpecificationException("Error with layout <"+spec.getLayoutFilename()+">. "+iglEx.getMessage());
				}

			}
			frame.setName(spec.getName());
			
			//. if it has an icon, use it
			if (spec.getIcon() != null) {
				frame.setFrameIcon(spec.getIcon());
			}
			//. allow frame to be resized.
			//. don't allow minimizing, because better to use toolbar than 
			//. minimized icons.
			frame.setMaximizable(true);
			frame.setResizable(true);

			//. allow closing of frames, but hide the gui rather than destroy
			frame.setClosable(true);
			frame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
			
			//. if both sizeX and sizeY are specified (> 0), set size of GUI
			int sizeX, sizeY;
			sizeX=spec.getSizeX();
			if (sizeX > 0) {
				sizeY = spec.getSizeY();
				if (sizeY > 0) {
					frame.setSize(sizeX, sizeY);
				}
			}
			
			//. if both locX and locY are specified, set location of GUI
			int locX, locY;
			locX=spec.getScreenLocationX();
			if (locX != GUISpecification.NULL_SCREEN_LOCATION_VALUE) {
				locY = spec.getScreenLocationY();
				if (locY != GUISpecification.NULL_SCREEN_LOCATION_VALUE) {
					frame.setLocation(locX, locY);
				}
			}
			
			//. add internal frame listener if specified
			if (ifListener != null) {
				frame.addInternalFrameListener(ifListener);
			}
			
			//. add component listeners 
			for (ComponentListener l : componentListenerList) {
				frame.addComponentListener(l);
			}


			//. add to desktop if specified
			if (desktop != null) 
				desktop.add(frame);

			//. add frame to framelist
			frameList.set(index, frame);

			//. return frame
			return frame;
		}
	}
	public void setInternalFrameListener(InternalFrameListener listener) {
		ifListener = listener;
	}
	public void killGUIs() {
		for (JInternalFrame frame : frameList) {
			if (frame != null)
				frame.dispose();
		}
	}
	public void readGUIList(File guiListFile) throws JDOMException, IOException, InvalidEnvironmentVariableException {
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();
		org.jdom.Document myDoc = builder.build(guiListFile);
		
		//. destroy current GUIs
		killGUIs();
		
		//. clear guiList and frameList
		guiList.clear();
		frameList.clear();
		//. do we want to do the bottom?
		//processListenerList.clear();
		//componentListenerList.clear();
		
		Attribute workingAtt;
		//. get root element.
		Element root=myDoc.getRootElement();
		//. check that it is a guilayout
		if (root.getName().compareTo(XML_ROOT) != 0)
			throw new JDOMException("Root element must be "+XML_ROOT);
		
    //. get children elements
    List<Element> elements=root.getChildren();
    //. loop through them
    for (Element current : elements) {
      //. check
      if (current.getName().compareTo(XML_ELEMENT_GUI) == 0) {
      	GUISpecification spec = new GUISpecification();
      	String name;

      	workingAtt = current.getAttribute(XML_GUI_ATTRIBUTE_TITLE);
      	if (workingAtt != null) {
      		spec.setTitle(workingAtt.getValue());
      	}
      	
      	workingAtt = current.getAttribute(XML_GUI_ATTRIBUTE_ACRONYM);
      	if (workingAtt != null) {
      		spec.setAcronym(workingAtt.getValue());
      	} else {
      		throw new JDOMException("GUI elements must have a "+XML_GUI_ATTRIBUTE_ACRONYM+" attribute");
      	}
      	
      	workingAtt = current.getAttribute(XML_GUI_ATTRIBUTE_NAME);
      	if (workingAtt != null) {
      		name = workingAtt.getValue();
      		spec.setName(name);
      	} else {
      		throw new JDOMException("GUI elements must have a "+XML_GUI_ATTRIBUTE_NAME+" attribute");
      	}
      	
      	workingAtt = current.getAttribute(XML_GUI_ATTRIBUTE_LAYOUT);
      	if (workingAtt != null) {
      		try {
      			spec.setLayoutFilename(workingAtt.getValue());
      		} catch (IOException ioEx) {
      			throw new JDOMException("Error loading layout for <"+name+">.  Check that file exists and is readable.");
      		}
      	} else {
      		throw new JDOMException("GUI elements must have a "+XML_GUI_ATTRIBUTE_LAYOUT+" attribute");
      	}
      	
      	workingAtt = current.getAttribute(XML_GUI_ATTRIBUTE_ICON);
      	if (workingAtt != null) {
      		spec.setIconFilename(workingAtt.getValue());
      	}

      	workingAtt = current.getAttribute(XML_GUI_ATTRIBUTE_XLOC);
      	if (workingAtt != null) {
      		try {
      			int intVal = workingAtt.getIntValue();
      			spec.setScreenLocationX(intVal);
      		} catch (DataConversionException ex) {
      			System.out.println("error reading xloc attribute for <"+name+">.");
      		}
      	}
      	workingAtt = current.getAttribute(XML_GUI_ATTRIBUTE_YLOC);
      	if (workingAtt != null) {
      		try {
      			int intVal = workingAtt.getIntValue();
      			spec.setScreenLocationY(intVal);
      		} catch (DataConversionException ex) {
      			System.out.println("error reading yloc attribute for <"+name+">.");
      		}
      	}
      	workingAtt = current.getAttribute(XML_GUI_ATTRIBUTE_XSIZE);
      	if (workingAtt != null) {
      		try {
      			int intVal = workingAtt.getIntValue();
      			spec.setSizeX(intVal);
      		} catch (DataConversionException ex) {
      			System.out.println("error reading xs attribute for <"+name+">.");
      		}
      	}
      	workingAtt = current.getAttribute(XML_GUI_ATTRIBUTE_YSIZE);
      	if (workingAtt != null) {
      		try {
      			int intVal = workingAtt.getIntValue();
      			spec.setSizeY(intVal);
      		} catch (DataConversionException ex) {
      			System.out.println("error reading ys attribute for <"+name+">.");
      		}
      	}

      	workingAtt = current.getAttribute(XML_GUI_ATTRIBUTE_VISIBLE);
     		if (workingAtt != null) {
     			try {
     				boolean boolVal = workingAtt.getBooleanValue();
     				spec.setVisible(boolVal);
     			} catch (DataConversionException dcEx) {
     				System.out.println("error reading visible attribute for <"+name+">.");
      		}     				
     		}
      	
      	workingAtt = current.getAttribute(XML_GUI_ATTRIBUTE_PROCESS_CONTROLLER);
     		if (workingAtt != null) {
     			try {
     				boolean boolVal = workingAtt.getBooleanValue();
     				spec.setProcessController(boolVal);
     			} catch (DataConversionException dcEx) {
     				System.out.println("error reading processController attribute for <"+name+">.");
      		}     				
     		}

     		//. add to guiList
        addGUI(spec);
      }
    }
	}
	public void writeGUIList(File guiListFile) throws JDOMException, IOException {
	  //. todo: versioning?    
		Element guiElement;
	  JInternalFrame frame;
	  
    //. root element 
    Element root = new Element(XML_ROOT);
    Document doc = new Document(root);
    
    int index=0;
    for (GUISpecification guiSpec : guiList) {
    	frame = frameList.get(index);
    	guiElement = new Element(XML_ELEMENT_GUI);
    	if (!guiSpec.getTitle().isEmpty()) {
    		guiElement.setAttribute(XML_GUI_ATTRIBUTE_TITLE, guiSpec.getTitle());
    	}
    	guiElement.setAttribute(XML_GUI_ATTRIBUTE_ACRONYM, guiSpec.getAcronym());
    	guiElement.setAttribute(XML_GUI_ATTRIBUTE_NAME, guiSpec.getName());
    	if (frame != null) {
    		Point loc = frame.getLocation();
    		guiElement.setAttribute(XML_GUI_ATTRIBUTE_XLOC, Integer.toString(new Double(Math.floor(loc.getX())).intValue()));
    		guiElement.setAttribute(XML_GUI_ATTRIBUTE_YLOC, Integer.toString(new Double(Math.floor(loc.getY())).intValue()));
    		guiElement.setAttribute(XML_GUI_ATTRIBUTE_XSIZE, Integer.toString(frame.getWidth()));
    		guiElement.setAttribute(XML_GUI_ATTRIBUTE_YSIZE, Integer.toString(frame.getHeight()));
    		guiElement.setAttribute(XML_GUI_ATTRIBUTE_VISIBLE, Boolean.toString(frame.isVisible()));
    	}
    	guiElement.setAttribute(XML_GUI_ATTRIBUTE_ICON, guiSpec.getIconFilename());
    	guiElement.setAttribute(XML_GUI_ATTRIBUTE_LAYOUT, guiSpec.getLayoutFilename());
    	guiElement.setAttribute(XML_GUI_ATTRIBUTE_PROCESS_CONTROLLER, Boolean.toString(guiSpec.isProcessController()));
    	root.addContent(guiElement);
    	
    	index++;
    }
    
    XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
    outputter.output(doc, new java.io.FileOutputStream(guiListFile));

	}
}
