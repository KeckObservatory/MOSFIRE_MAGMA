package edu.ucla.astro.irlab.mosfire.mscgui;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.xml.transform.TransformerException;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import edu.hawaii.keck.kjava.KJavaException;
import edu.ucla.astro.irlab.util.FileUtilities;
import edu.ucla.astro.irlab.util.InvalidValueException;
import edu.ucla.astro.irlab.util.NoSuchPropertyException;
import edu.ucla.astro.irlab.util.NumberFormatters;
import edu.ucla.astro.irlab.util.ServerStatusPanel;
import edu.ucla.astro.irlab.util.gui.CellEditorsAndRenderers;
import edu.ucla.astro.irlab.util.gui.OptionCheckBox;
import edu.ucla.astro.irlab.util.gui.GenericController;
import edu.ucla.astro.irlab.mosfire.util.*;

import nom.tam.fits.FitsException;

import org.apache.log4j.*;
import org.jdom.JDOMException;

/**
 * <p>Title: MSCGUIView</p>
 * <p>Description: View Class for MOSFIRE CSU Control GUI</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: UCLA Infrared Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

//. DONE copying mask config should change mask name to new name in mascgen panel
//. TODO some thought needed about directories, re: users saving at home, and opening at keck.
//. DONE don't show warning on duplicate mask names
//. DONE adjust slit width
//. DONE script running
//. DONE open mask
//. DONE long slit
//. DONE mascgen status (new tab, brought forward on execution, disables tab switching, has abort button)
//. DONE center of priority
//. DONE execute buttons
//. DONE target table
//. DONE save configurations
//. DONE save configuration dialog
//. DONE handle unsaved configs
public class MSCGUIView extends JFrame implements ChangeListener {
	//. serialVersionUID created using serialver command 2006/07/11
	static final long serialVersionUID = -4737530193991680291L;

	private static final Logger logger = Logger.getLogger(MSCGUIView.class);
	
	private MSCGUIModel myModel;
	
	private JPanel contentPane;
	private JPanel mainPanel = new JPanel();
	private JPanel maskConfigPanel = new JPanel();
	private JPanel leftPanel = new JPanel();
	private JPanel rightPanel = new JPanel();
	private JPanel middlePanel = new JPanel();
	private JSplitPane topSplitPane = new JSplitPane();
	private JSplitPane objectsSplitPane = new JSplitPane();
	private JPanel mascgenPanel = new JPanel();
	private JTabbedPane mascgenTabbedPane = new JTabbedPane();
	private JPanel mascgenInputPanel = new JPanel();
	private MascgenOutputsPanel mascgenOutputPanel = new MascgenOutputsPanel();
	private MascgenOutputsPanel maskConfigOutputPanel = new MascgenOutputsPanel();
	private JPanel mascgenStatusPanel = new JPanel();
	private JPanel mascgenParamsButtonPanel = new JPanel();
	private JPanel longSlitPanel = new JPanel();
	private JPanel openMaskPanel = new JPanel();
	private JPanel specialMaskPanel = new JPanel();
	private JPanel slitWidthPanel = new JPanel();
	private JPanel slitTablePanel = new JPanel();
	
	//. MENU ITEMS
	private JMenuBar mainMenuBar = new JMenuBar();
	private JMenu jMenuFile            = new JMenu("File");
	private JMenuItem jMenuFileOpenTargetList          = new JMenuItem("Open Target List...");  
	private JMenuItem jMenuFileOpenMSC          = new JMenuItem("Open MSC...");  
	private JMenuItem jMenuFileCopyMSC          = new JMenuItem("Copy MSC");  
	private JMenuItem jMenuFileSaveMSC          = new JMenuItem("Save MSC...");  
	private JMenuItem jMenuFileSaveAll         = new JMenuItem("Save All Configuration Products...");  
	private JMenuItem jMenuFileCloseMSC          = new JMenuItem("Close MSC");  
	private JMenuItem jMenuFileSetExecutedMaskDir      = new JMenuItem("Set Executed Mask Directory...");  
	private JMenuItem jMenuFileExit                    = new JMenuItem("Exit");
	private JMenu jMenuTools            = new JMenu("Tools");
	private JMenuItem jMenuToolsCalibration            = new JMenuItem("Calibration Tool...");  
	private JMenuItem jMenuToolsCustomize              = new JMenuItem("Customize...");  
	private JMenuItem jMenuToolsOptions                = new JMenuItem("Options...");  
	private JMenu jMenuHelp            = new JMenu("Help");
	private JMenuItem jMenuHelpOnline                    = new JMenuItem("Online Help...");
	private JMenuItem jMenuHelpAbout                    = new JMenuItem("About...");
	private BorderLayout borderLayout1 = new BorderLayout();
	private JLabel statusBar = new JLabel();
	
	//. field panel
	private JLabel currentMaskNameLabel = new JLabel("Mask Name:");
	private JLabel currentMaskNameValueLabel = new JLabel("");
	private JLabel centerLabel = new JLabel("Center:");
	private JLabel centerValueLabel = new JLabel("");
	private JLabel paLabel = new JLabel("PA:");
	private JLabel paValueLabel = new JLabel("");
	private JLabel paUnitsLabel = new JLabel("degrees");
	private JLabel totalPriorityLabel = new JLabel("Total Priority:");
	private JLabel totalPriorityValueLabel = new JLabel("");
	
	//. slit width panel
	private JLabel currentSlitWidthLabel = new JLabel("Slit Width: ");
	private JSpinner currentSlitWidthSpinner = new JSpinner();
	private JCheckBox fixedSlitWidthCheckBox = new JCheckBox("Same slit width for all slits");
	private JLabel currentSlitWidthUnits = new JLabel("\"");
	private JButton currentSlitWidthSetButton = new JButton("SET");
	
	//. Opened Configs panel
	private JPanel openedConfigsPanel = new JPanel();
	private JPanel topConfigPanel = new JPanel(new GridLayout(1,0));
	private JPanel bottomConfigPanel = new JPanel(new GridLayout(1,0));
	private JLabel maskConfigurationsLabel = new JLabel("MASK CONFIGURATIONS");
	private JScrollPane openedConfigsScrollPane = new JScrollPane();
	private JTable openedConfigsTable = new JTable();
	private JButton openConfigButton = new JButton("Open...");
	private JButton copyConfigButton = new JButton("Copy");
	private JButton saveConfigButton = new JButton("Save MSC...");
	private JButton saveAllConfigButton = new JButton("Save All...");
	private JButton closeConfigButton = new JButton("Close");
	private SlitConfigurationTableModel openedConfigsTableModel = new SlitConfigurationTableModel();
	
	//. Long slit panel
	private JLabel longSlitLabel = new JLabel("LONGSLIT");
	private JLabel longSlitWidthLabel = new JLabel("Slit width: ");
	private JSpinner longSlitWidthSpinner = new JSpinner();
	private JLabel longSlitWidthUnits = new JLabel("arcsec");
	private JButton openLongSlitButton = new JButton("Create Longslit");
	
	//. open mask panel
	private JLabel openMaskLabel = new JLabel("OPEN MASK");
	private JButton openMaskButton = new JButton("Open Mask");
	
	//. script panel
	private JPanel scriptButtonPanel = new JPanel();
	private JButton setupAlignmentButton = new JButton("Setup Alignment Mask");
	private JButton setupScienceButton = new JButton("Setup Science Mask");
	private JButton executeMaskButton = new JButton("Execute Mask");
	private JPanel loadedMaskPanel = new JPanel();
	private JLabel loadedMaskLabel = new JLabel();
	private JPanel csuStatusPanel = new JPanel();
	private JLabel csuReadyLabel = new JLabel("CSU State:");
	private JLabel csuStatusLabel = new JLabel("Status:");
	private JLabel csuReadyValueLabel = new JLabel();
	private JLabel csuStatusValueLabel = new JLabel();

  //. mascgen Args panel
	private JLabel mascgenPanelLabel = new JLabel("MASCGEN");
	private JButton loadMascgenParamsButton = new JButton("Load Parameters...");
	private JButton saveMascgenParamsButton = new JButton("Save Parameters...");
	private JLabel inputObjectListLabel = new JLabel("Input Object List: ");
	private JLabel inputObjectListValueLabel = new JLabel("none");
	private JButton inputObjectListBrowseButton = new JButton("Select Target List...");
	private JCheckBox useCenterOfPriorityCheckBox = new JCheckBox("Use Center of Priority");
	private JLabel xRangeLabel = new JLabel("X Range:");
	private JTextField xRangeField = new JTextField();
	private JLabel xRangeUnitsLabel = new JLabel("arcmin");
	private JLabel xCenterLabel = new JLabel("X Center:");
	private JTextField xCenterField = new JTextField();
	private JLabel xCenterUnitsLabel = new JLabel("arcmin");
	private JLabel slitWidthLabel = new JLabel("Slit Width:");
	private JTextField slitWidthField = new JTextField();
	private JLabel slitWidthUnitsLabel = new JLabel("arcsec");
	private JLabel ditherSpaceLabel = new JLabel("Dither Space:");
	private JTextField ditherSpaceField = new JTextField();
	private JLabel ditherSpaceUnitsLabel = new JLabel("arcsec");
	private JLabel centerRaDecLabel = new JLabel("Center Ra/Dec:");
	private JTextField centerRaDecField = new JTextField();
	private JLabel centerRaDecUnitsLabel = new JLabel("h m s \u00b0 \' \"");
	private JLabel xStepsLabel = new JLabel("X Steps:");
	private JTextField xStepsField = new JTextField();
	private JLabel xStepsUnitsLabel = new JLabel("");
	private JLabel xStepSizeLabel = new JLabel("X Step Size:");
	private JTextField xStepSizeField = new JTextField();
	private JLabel xStepSizeUnitsLabel = new JLabel("arcsec");
	private JLabel yStepsLabel = new JLabel("Y Steps:");
	private JTextField yStepsField = new JTextField();
	private JLabel yStepsUnitsLabel = new JLabel("");
	private JLabel yStepSizeLabel = new JLabel("Y Step Size:");
	private JTextField yStepSizeField = new JTextField();
	private JLabel yStepSizeUnitsLabel = new JLabel("arcsec");
	private JLabel centerPALabel = new JLabel("Center PA:");
	private JTextField centerPAField = new JTextField();
	private JLabel centerPAUnitsLabel = new JLabel("degrees");
	private JLabel paStepsLabel = new JLabel("PA Steps:");
	private JTextField paStepsField = new JTextField();
	private JLabel paStepsUnitsLabel = new JLabel("");
	private JLabel paStepSizeLabel = new JLabel("PA Step Size:");
	private JTextField paStepSizeField = new JTextField();
	private JLabel paStepSizeUnitsLabel = new JLabel("degrees");
	private JLabel alignmentStarsLabel = new JLabel("Alignment Stars:");
	private JTextField alignmentStarsField = new JTextField();
	private JLabel alignmentStarsUnitsLabel = new JLabel("");
	private JLabel alignmentStarEdgeLabel = new JLabel("Star Edge Buffer:");
	private JTextField alignmentStarEdgeField = new JTextField();
	private JLabel alignmentStarEdgeUnitsLabel = new JLabel("arcsec");
	private JLabel maskNameLabel = new JLabel("Mask Name:");
	private JTextField maskNameField = new JTextField();
	private JLabel maskNameUnitsLabel = new JLabel("");
	private JButton runMascgenButton = new JButton("Run");

	//. mascgen status panel
	private JScrollPane mascgenStatusScrollPane = new JScrollPane();
	private JTextArea mascgenStatusTextArea = new JTextArea();
	private JLabel mascgenRunNumberLabel = new JLabel("");
	private JLabel mascgenTotalRunsLabel = new JLabel("");
	private JLabel mascgenOptimalRunNumberLabel = new JLabel("");
	private JLabel mascgenTotalPriorityLabel = new JLabel("");
	private JLabel mascgenRunNumberTitle = new JLabel("Run number: ");
	private JLabel mascgenTotalRunsTitle = new JLabel("Total number of runs: ");
	private JLabel mascgenTotalPriorityTitle = new JLabel("Highest Total priority: ");
	private JLabel mascgenOptimalRunNumberTitle = new JLabel("Found on run: ");
	private JButton mascgenAbortButton = new JButton("ABORT");

	//. table
	private JScrollPane slitListTableScrollPane = new JScrollPane();
	private JTable slitListTable = new JTable();
	private MechanicalSlitListTableModel slitListTableModel = new MechanicalSlitListTableModel(true);
	
	//. configuration panels
	private MaskVisualizationPanel slitConfigurationPanel = new MaskVisualizationPanel();

	//. target list panel
	private JPanel targetListPanel = new JPanel();
	private JScrollPane targetListTableScrollPane = new JScrollPane();
	private JTable targetListTable = new JTable();
	private TargetListTableModel targetListTableModel = new TargetListTableModel();
	
	private JPanel statusPanel = new JPanel();
	private ServerStatusPanel mosfireStatusPanel;
	private ServerStatusPanel mcsusStatusPanel;
	private ServerStatusPanel mdsStatusPanel;
	
	private JFileChooser mascgenParamsFC = new JFileChooser();
	private JFileChooser objectListFC = new JFileChooser();
	private JFileChooser slitConfigurationFC = new JFileChooser();
	private JFileChooser executedMaskDirFC = new JFileChooser();
	
	//. TODO: configure JFileChoosers (default dir, filename, filters, etc).

	private JTextArea warningListTextArea = new JTextArea();
	private JScrollPane warningListScrollPane = new JScrollPane();
	
	private CalibrationScriptFrame calibrationFrame = new CalibrationScriptFrame();
	
	private JMenuItem alignWithAboveMenuItem = new JMenuItem("Align with slit above");
	private JMenuItem alignWithBelowMenuItem = new JMenuItem("Align with slit below");
	private JMenu  slitMenu = new JMenu("Slit");
	private JMenu targetInfoMenu = new JMenu("Target Info");
	private AstroObjInfoPanel targetInfoPanel = new AstroObjInfoPanel();
	
	private JPopupMenu maskConfigsPopup = new JPopupMenu();
	private JMenuItem popupOpenMSC          = new JMenuItem("Open MSC...");  
	private JMenuItem popupCopyMSC          = new JMenuItem("Copy MSC");  
	private JMenuItem popupSaveMSC          = new JMenuItem("Save MSC...");  
	private JMenuItem popupSaveAll         = new JMenuItem("Save All Configuration Products...");  
	private JMenuItem popupCloseMSC          = new JMenuItem("Close MSC");  

	private JPopupMenu openMaskConfigPopup = new JPopupMenu();
	private JMenuItem popupOnlyOpenMSC          = new JMenuItem("Open MSC...");  

	
	private String inputObjectListFullPath = "";
	
	private OptionCheckBox duplicateMaskNameOptionCheckBox;
	private OptionCheckBox minAlignStarsOptionCheckBox;
	private OptionCheckBox setupMaskOptionCheckBox;
	private OptionCheckBox executeMaskOptionCheckBox;
	private OptionCheckBox executeDifferentMaskOptionCheckBox;
	private OptionCheckBox writeSlitConfigurationHTMLOptionCheckBox;
	private OptionCheckBox unusedSlitsOptionCheckBox;
	
	private JCheckBox reassignUnusedSlitsCheckBox = new JCheckBox("Have MASCGEN join unassigned slits to highest priority neighbor?");
	private JCheckBox showMaskConfigButtonsCheckBox = new JCheckBox("Show Mask Configuration Buttons?");
	
	//. panel for unused bar options
	private JPanel unusedBarOptionsPanel = new JPanel();
	private JLabel maximumSlitLengthLabel = new JLabel("Maximum slit length in rows: ");
	private JSpinner maximumSlitLengthField = new JSpinner();
	private JLabel reassignMethodLabel = new JLabel("Move unassigned slits as follows: ");
	private JRadioButton unusedBarDoNothingButton = new JRadioButton("Do nothing.");
	private JRadioButton unusedBarReduceWidthButton = new JRadioButton("Reduce Width.");
	private JRadioButton unusedBarCloseOffButton = new JRadioButton("Move slit out of field of view.");
	private JLabel minimumReassignSlitWidthLabel = new JLabel("Apply to unassigned slits with widths greater than (arcsec): ");
	private JSpinner minimumReassignSlitWidthField = new JSpinner();
	private JLabel reducedSlitWidthLabel = new JLabel("Reduce slit width to (arcsec): ");
	private JSpinner reducedSlitWidthField = new JSpinner();
	private ButtonGroup reassignMethodButtonGroup = new ButtonGroup();

	private Insets defaultInsets = new Insets(MSCGUIParameters.GUI_INSET_VERTICAL_GAP,2,MSCGUIParameters.GUI_INSET_VERTICAL_GAP,2);
	private boolean showMaskConfigurationButtons = true;
	
	private MSCGUIController myController;
	
	//Construct the frame
	public MSCGUIView(MSCGUIModel newModel)  throws Exception {
		myModel=newModel;
		showMaskConfigurationButtons = MSCGUIParameters.SHOW_MASK_CONFIGURATION_BUTTONS;
		if (MSCGUIParameters.ONLINE_MODE) {
			mosfireStatusPanel = new ServerStatusPanel("MOSFIRE", myModel.getMosfireLastAliveProperty(), true);
			mdsStatusPanel = new ServerStatusPanel("MDS", myModel.getMDSLastAliveProperty(), true);
			mcsusStatusPanel = new ServerStatusPanel("MCSUS", myModel.getMCSUSLastAliveProperty(), true);
		}
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		jbInit();
		myController = new MSCGUIController(myModel);
		updateView();
	}

	//Component initialization
	private void jbInit() throws Exception  {
		Insets wideInsets = new Insets(5,5,5,5);
		
		SpinnerNumberModel maxSlitLengthSpinnerModel = new SpinnerNumberModel();
		maxSlitLengthSpinnerModel.setMinimum(1);
		maxSlitLengthSpinnerModel.setMaximum(MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS);
		maxSlitLengthSpinnerModel.setValue(myModel.getMaximumSlitLength());
		maximumSlitLengthField.setModel(maxSlitLengthSpinnerModel);
		
		SpinnerNumberModel minReassignSlitWidthSpinnerModel = new SpinnerNumberModel();
		minReassignSlitWidthSpinnerModel.setStepSize(0.1);
		minReassignSlitWidthSpinnerModel.setMinimum(MosfireParameters.MINIMUM_SLIT_WIDTH);
		minReassignSlitWidthSpinnerModel.setMaximum(MosfireParameters.MAXIMUM_SLIT_WIDTH);
		minReassignSlitWidthSpinnerModel.setValue(myModel.getMinimumCloseOffSlitWidth());
		minimumReassignSlitWidthField.setModel(minReassignSlitWidthSpinnerModel);
		
		SpinnerNumberModel reducedSlitWidthSpinnerModel = new SpinnerNumberModel();
		reducedSlitWidthSpinnerModel.setStepSize(0.1);
		reducedSlitWidthSpinnerModel.setMinimum(MosfireParameters.MINIMUM_SLIT_WIDTH);
		reducedSlitWidthSpinnerModel.setMaximum(MosfireParameters.MAXIMUM_SLIT_WIDTH);
		reducedSlitWidthSpinnerModel.setValue(myModel.getClosedOffSlitWidth());
		reducedSlitWidthField.setModel(reducedSlitWidthSpinnerModel);
		
		unusedBarOptionsPanel.setBorder(BorderFactory.createTitledBorder("Unassigned Bar Options"));
		unusedBarOptionsPanel.setLayout(new GridBagLayout());
		unusedBarOptionsPanel.add(maximumSlitLengthLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(maximumSlitLengthField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(reassignMethodLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(unusedBarDoNothingButton, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(unusedBarReduceWidthButton, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(unusedBarCloseOffButton, new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(minimumReassignSlitWidthLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(minimumReassignSlitWidthField, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(reducedSlitWidthLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(reducedSlitWidthField, new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		reassignMethodButtonGroup.add(unusedBarDoNothingButton);
		reassignMethodButtonGroup.add(unusedBarReduceWidthButton);
		reassignMethodButtonGroup.add(unusedBarCloseOffButton);
		unusedBarDoNothingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reassignMethod_actionPerformed(MSCGUIModel.CLOSE_OFF_TYPE_DO_NOTHING);
			}
		});
		unusedBarReduceWidthButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reassignMethod_actionPerformed(MSCGUIModel.CLOSE_OFF_TYPE_REDUCE_IN_PLACE);
			}
		});
		unusedBarCloseOffButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reassignMethod_actionPerformed(MSCGUIModel.CLOSE_OFF_TYPE_CLOSE_OFF);
			}
		});
		
		
		
		duplicateMaskNameOptionCheckBox = new OptionCheckBox("Continue to show this warning?", "Warn before replacing existing mask with duplicate name?");
		duplicateMaskNameOptionCheckBox.setSelected(MSCGUIParameters.SHOW_WARNING_DUPLICATE_MASK_NAME);
		duplicateMaskNameOptionCheckBox.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_DUPLICATE_MASK_NAME_WARNING);
		
		minAlignStarsOptionCheckBox = new OptionCheckBox("Continue to show this warning?", "Warn if number of alignment stars is less than recommended minimum?");
		minAlignStarsOptionCheckBox.setSelected(MSCGUIParameters.SHOW_WARNING_MINIMUM_ALIGN_STARS);
		minAlignStarsOptionCheckBox.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_MINIMUM_ALIGN_STARS);

		setupMaskOptionCheckBox = new OptionCheckBox("Continue to confirm mask setup?", "Confirm mask setup?");
		setupMaskOptionCheckBox.setSelected(MSCGUIParameters.SHOW_WARNING_SETUP_MASK);
		setupMaskOptionCheckBox.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_SETUP_MASK);

		executeMaskOptionCheckBox = new OptionCheckBox("Continue to confirm mask execution?", "Confirm mask execution?");
		executeMaskOptionCheckBox.setSelected(MSCGUIParameters.SHOW_WARNING_EXECUTE_MASK);
		executeMaskOptionCheckBox.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_EXECUTE_MASK);

		executeDifferentMaskOptionCheckBox = new OptionCheckBox("Continue to show this warning?", "Warn before executing mask set up mask is different than displayed mask?");
		executeDifferentMaskOptionCheckBox.setSelected(MSCGUIParameters.SHOW_WARNING_EXECUTE_DIFFERENT_MASK);
		executeDifferentMaskOptionCheckBox.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_EXECUTE_DIFFERENT_MASK);
		
		writeSlitConfigurationHTMLOptionCheckBox = new OptionCheckBox("Continue to try to write HTML files?", "Write HTML file when saving slit configuration?");
		writeSlitConfigurationHTMLOptionCheckBox.setSelected(MSCGUIParameters.SHOW_WARNING_WRITE_MSC_HTML);
		writeSlitConfigurationHTMLOptionCheckBox.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_WRITE_MSC_HTML);

		unusedSlitsOptionCheckBox = new OptionCheckBox("Continue to warn about unassigned slits?", "Warn about unassigned slits before mask setup?");
		unusedSlitsOptionCheckBox.setSelected(MSCGUIParameters.SHOW_WARNING_UNUSED_SLITS);
		unusedSlitsOptionCheckBox.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_UNUSED_SLITS);

		showMaskConfigButtonsCheckBox.setSelected(MSCGUIParameters.SHOW_MASK_CONFIGURATION_BUTTONS);
		reassignUnusedSlitsCheckBox.setSelected(myModel.isMascgenReassignUnusedSlits());
		
		warningListScrollPane.getViewport().add(warningListTextArea);
		warningListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		warningListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		warningListScrollPane.setPreferredSize(new Dimension(300,300));
		warningListTextArea.setLineWrap(true);
		warningListTextArea.setWrapStyleWord(true);
		warningListTextArea.setEditable(false);
		
		//. Find HelpSet file can create HelpSet object
		boolean helpSetAvailable = false;
		ClassLoader cl = MSCGUIView.class.getClassLoader();
		URL hsURL = HelpSet.findHelpSet(cl, MSCGUIParameters.MSCGUI_HELPSET_NAME);
		try {
			HelpSet hs = new HelpSet(null, hsURL);
			HelpBroker hb = hs.createHelpBroker();
			jMenuHelpOnline.addActionListener(new CSH.DisplayHelpFromSource(hb));
			helpSetAvailable = true;
		} catch (HelpSetException ex) {
			JOptionPane.showMessageDialog(this, "Warning: Help files not found. Online help not available.", "Error Loading Help Set", JOptionPane.WARNING_MESSAGE);
			ex.printStackTrace();
		}
		//setIconImage(Toolkit.getDefaultToolkit().createImage(MSCGUIApplication.class.getResource("[Your Icon]")));
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(borderLayout1);
		this.setSize(MSCGUIParameters.DIM_MAINFRAME);
		this.setTitle(MSCGUIParameters.GUI_TITLE + (MSCGUIParameters.ENGINEERING_MODE ? " (Engineering Mode)" : ""));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		statusBar.setText(" ");
		
		alignWithAboveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				alignWithSlitAbove_actionPerformed();				
			}
		});
		alignWithBelowMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				alignWithSlitBelow_actionPerformed();				
			}
		});

		targetInfoMenu.add(targetInfoPanel);
		popupOpenMSC.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileOpenMSC_actionPerformed(e);
			}
		});
		popupOnlyOpenMSC.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileOpenMSC_actionPerformed(e);
			}
		});
		popupCopyMSC.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileCopyMSC_actionPerformed(e);
			}
		});
		popupSaveMSC.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSaveMSC_actionPerformed(e);
			}
		});
		popupSaveAll.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSaveAll_actionPerformed(e);
			}
		});
		popupCloseMSC.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileCloseMSC_actionPerformed(e);
			}
		});

		maskConfigsPopup.add(popupOpenMSC);
		maskConfigsPopup.add(popupCopyMSC);
		maskConfigsPopup.add(popupSaveMSC);
		maskConfigsPopup.add(popupSaveAll);
		maskConfigsPopup.add(popupCloseMSC);

		openMaskConfigPopup.add(popupOnlyOpenMSC);

		//. Menu
		jMenuFileOpenTargetList.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileOpenTargetList_actionPerformed(e);
			}
		});
		jMenuFileOpenMSC.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileOpenMSC_actionPerformed(e);
			}
		});
		jMenuFileCopyMSC.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileCopyMSC_actionPerformed(e);
			}
		});
		jMenuFileSaveMSC.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSaveMSC_actionPerformed(e);
			}
		});
		jMenuFileSaveAll.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSaveAll_actionPerformed(e);
			}
		});
		jMenuFileCloseMSC.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileCloseMSC_actionPerformed(e);
			}
		});
		jMenuFileSetExecutedMaskDir.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSetExecutedMaskDir_actionPerformed(e);
			}
		});
		jMenuFileExit.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuFileExit_actionPerformed(e);
			}
		});
		jMenuHelpAbout.addActionListener(new ActionListener()  {
			public void actionPerformed(ActionEvent e) {
				jMenuHelpAbout_actionPerformed(e);
			}
		});
		jMenuToolsCalibration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuToolsCalibration_actionPerformed(e);
			}
		});
		jMenuToolsCustomize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuToolsCustomize_actionPerformed(e);
			}
		});
		jMenuToolsOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuToolsOptions_actionPerformed(e);
			}
		});
		
		
		//.  Assemble Main Base and Tabs
//	jMenuFile.add(jMenuFileOpenTargetList);
		jMenuFile.add(jMenuFileOpenMSC);
		jMenuFile.add(jMenuFileCopyMSC);
		jMenuFile.add(jMenuFileSaveMSC);
		jMenuFile.add(jMenuFileSaveAll);
		jMenuFile.add(jMenuFileCloseMSC);
		jMenuFile.addSeparator();
		if (MSCGUIParameters.ONLINE_MODE) {
			jMenuFile.add(jMenuFileSetExecutedMaskDir);
			jMenuFile.addSeparator();
		}
		jMenuFile.add(jMenuFileExit);
		jMenuTools.add(jMenuToolsCalibration);
		jMenuTools.addSeparator();
		jMenuTools.add(jMenuToolsCustomize);
		jMenuTools.add(jMenuToolsOptions);
		if (helpSetAvailable) {
			jMenuHelp.add(jMenuHelpOnline);
		}
		jMenuHelp.add(jMenuHelpAbout);
		mainMenuBar.add(jMenuFile);
		mainMenuBar.add(jMenuTools);
		mainMenuBar.add(jMenuHelp);
		this.setJMenuBar(mainMenuBar);
		
		calibrationFrame.setSize(MSCGUIParameters.DIM_CALIBRATION_GUI);

		mascgenParamsFC.setCurrentDirectory(MSCGUIParameters.DEFAULT_MASCGEN_PARAMS_DIRECTORY);
		mascgenParamsFC.setFileFilter(new FileUtilities.StandardFileFilter(new String[] {"param", "params"}, "MASCGEN Parameter files"));
		
		slitConfigurationFC.setCurrentDirectory(MSCGUIParameters.DEFAULT_MASK_CONFIGURATION_ROOT_DIRECTORY);
		slitConfigurationFC.setFileFilter(new FileUtilities.StandardFileFilter("xml", "Slit Configuration Files"));
		executedMaskDirFC.setCurrentDirectory(myModel.getScriptDirectory());
		executedMaskDirFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		objectListFC.setDialogTitle("Open Target List");
		objectListFC.setDialogType(JFileChooser.OPEN_DIALOG);
		logger.trace("Default target list directory = "+MSCGUIParameters.DEFAULT_TARGET_LIST_DIRECTORY.toString());
		objectListFC.setCurrentDirectory(MSCGUIParameters.DEFAULT_TARGET_LIST_DIRECTORY);
		objectListFC.setFileFilter(new FileUtilities.StandardFileFilter(new String[] {"coords", "txt"}, "Target Lists"));
		

		inputObjectListBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inputObjectListBrowseButton_actionPerformed();
			}
		});
		
		loadMascgenParamsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadMascgenParamsButton_actionPerformed(e);
			}
		});		
		saveMascgenParamsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveMascgenParamsButton_actionPerformed(e);
			}
		});		

		
		slitConfigurationPanel.addMouseListener(new MouseAdapter() {			
			@Override
			public void mouseClicked(MouseEvent e) {
				slitConfigurationPanel_mouseClicked(e);
			}
		});
		
	//	inputObjectListValueLabel.setPreferredSize(new Dimension(150,20));
		inputObjectListValueLabel.setToolTipText(inputObjectListFullPath);
	
		useCenterOfPriorityCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				useCenterOfPriorityCheckBox_actionPerformed(e);
			}
		});

		maskNameField.setText(MSCGUIParameters.DEFAULT_MASK_NAME);
		maskNameField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				maskNameField_focusLost(e);
			}
		});
		mascgenOutputPanel.updateOutputParams(MSCGUIParameters.DEFAULT_MASK_NAME);
		String outputRootDirString = MSCGUIParameters.DEFAULT_MASCGEN_OUTPUT_ROOT_DIRECTORY.toString();
		logger.trace("Output Root Dir = "+outputRootDirString);
		outputRootDirString = MSCGUIParameters.DEFAULT_MASCGEN_OUTPUT_ROOT_DIRECTORY.getCanonicalPath();
		logger.trace("Output Root Dir Canonical = "+outputRootDirString);
		mascgenOutputPanel.setOutputRootDir(outputRootDirString);

		
		runMascgenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runMascgenButton_actionPerformed(e);
			}
		});
		mascgenAbortButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abortMascgenButton_actionPerformed(e);
			}
		});
		
		
		targetListPanel.setLayout(new BorderLayout());
		targetListTableScrollPane.getViewport().add(targetListTable);
		//targetListTableScrollPane.setPreferredSize(MSCGUIParameters.DIM_TABLE_TARGET_LIST);
		targetListTableModel.setData(myModel.getTargetList());
		targetListTable.setModel(targetListTableModel);
		targetListTable.getColumnModel().getColumn(0).setCellRenderer(new CellEditorsAndRenderers.CenteredTextTableCellRenderer());
		targetListTable.getColumnModel().getColumn(1).setCellRenderer(new CellEditorsAndRenderers.CenteredTextTableCellRenderer());
		targetListTable.getColumnModel().getColumn(2).setCellRenderer(new CellEditorsAndRenderers.CenteredTextTableCellRenderer());
		targetListTable.getColumnModel().getColumn(3).setCellRenderer(new CellEditorsAndRenderers.CenteredTextTableCellRenderer());
		targetListTable.getColumnModel().getColumn(4).setCellRenderer(new CellEditorsAndRenderers.CenteredTextTableCellRenderer());
		targetListTable.getColumnModel().getColumn(5).setCellRenderer(new CellEditorsAndRenderers.CenteredTextTableCellRenderer());
		targetListTable.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent tmEv) {
				targetListTableModelChanged(tmEv);
			}
		});
		targetListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		targetListTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lsEv) {
				targetList_tableSelectionChanged(lsEv);
			}
		});
		
		currentMaskNameLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_LABEL);
		currentMaskNameValueLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_VALUE_NAME);
		currentMaskNameValueLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		centerLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_LABEL);
		centerValueLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_VALUE);
		paLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_LABEL);
		paValueLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_VALUE);
		paUnitsLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_VALUE);
		totalPriorityLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_LABEL);
		totalPriorityValueLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_VALUE);
		paValueLabel.setPreferredSize(new Dimension(50, 20));
//		centerValueLabel.setPreferredSize(new Dimension(250, 20));
		
		slitWidthPanel.setBorder(BorderFactory.createEtchedBorder());
		SpinnerNumberModel currentSpinnerModel = new SpinnerNumberModel();
		currentSpinnerModel.setStepSize(0.1);
		currentSpinnerModel.setMinimum(MosfireParameters.MINIMUM_SLIT_WIDTH);
		currentSpinnerModel.setMaximum(MosfireParameters.MAXIMUM_SLIT_WIDTH);
		currentSpinnerModel.setValue(MosfireParameters.DEFAULT_SLIT_WIDTH);
		currentSlitWidthSpinner.setModel(currentSpinnerModel);
		slitWidthPanel.setLayout(new GridBagLayout());
//		slitWidthPanel.add(fixedSlitWidthCheckBox, BorderLayout.NORTH);
		currentSlitWidthSpinner.setPreferredSize(new Dimension(50, 20));
		
		slitWidthPanel.add(currentSlitWidthLabel, new GridBagConstraints(0,0,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		slitWidthPanel.add(currentSlitWidthSpinner, new GridBagConstraints(1,0,1,1,10.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
//		slitWidthPanel.add(currentSlitWidthUnits, new GridBagConstraints(2,0,1,1,0.0,0.0, GridBagConstraints.WEST,
//				GridBagConstraints.NONE, defaultInsets, 0,0));
		slitWidthPanel.add(currentSlitWidthSetButton, new GridBagConstraints(3,0,1,1,0.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		currentSlitWidthSetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentSlitWidthSpinner_actionPerformed(e);
			}
		});
		int row;
		
		maskConfigPanel.setLayout(new GridBagLayout());
		maskConfigPanel.add(currentMaskNameLabel, new GridBagConstraints(0,0,1,1,0.0,0.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		maskConfigPanel.add(currentMaskNameValueLabel, new GridBagConstraints(1,0,1,1,30.0,0.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		maskConfigPanel.add(centerLabel, new GridBagConstraints(0,1,1,1,0.0,0.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		maskConfigPanel.add(centerValueLabel, new GridBagConstraints(1,1,1,1,30.0,0.0, GridBagConstraints.SOUTHWEST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		maskConfigPanel.add(paLabel, new GridBagConstraints(2,1,1,1,0.0,0.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		maskConfigPanel.add(paValueLabel, new GridBagConstraints(3,1,1,1,0.0,0.0, GridBagConstraints.SOUTH,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		maskConfigPanel.add(paUnitsLabel, new GridBagConstraints(4,1,1,1,0.0,0.0, GridBagConstraints.SOUTHWEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		maskConfigPanel.add(totalPriorityLabel, new GridBagConstraints(2,0,2,1,0.0,0.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		maskConfigPanel.add(totalPriorityValueLabel, new GridBagConstraints(4,0,1,1,10.0,0.0, GridBagConstraints.SOUTHWEST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		
		openConfigButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openConfigButton_actionPerformed(e);
			}
		});
		copyConfigButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyConfigButton_actionPerformed(e);
			}
		});
		saveConfigButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveConfigButton_actionPerformed(e);
			}
		});
		saveAllConfigButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAllConfigButton_actionPerformed(e);
			}
		});
		closeConfigButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeConfigButton_actionPerformed(e);
			}
		});
		
		openedConfigsTableModel.setData(myModel.getOpenedSlitConfigurations());
		openedConfigsTable.setModel(openedConfigsTableModel);
		openedConfigsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		openedConfigsScrollPane.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				openedConfigsPanel_mouseReleased(e);
			}
		});
		openedConfigsTable.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				openedConfigsTable_mouseReleased(e);
			}
		});
		openedConfigsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lsEv) {
				openedConfigsTable_tableSelectionChanged(lsEv);
			}
		});
		openedConfigsTable.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent tmEv) {
				slitConfigurationTableModelChanged(tmEv);
			}
		});
		openedConfigsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		TableColumn statusColumn = openedConfigsTable.getColumnModel().getColumn(0);
		statusColumn.setMaxWidth(MSCGUIParameters.WIDTH_MASK_CONFIG_STATUS);
		statusColumn.setCellRenderer(new CellEditorsAndRenderers.CenteredTextTableCellRenderer());
		openedConfigsScrollPane.getViewport().add(openedConfigsTable);
		
		openedConfigsPanel.setBackground(MSCGUIParameters.COLOR_OPENED_MASKS_PANEL);
		openedConfigsPanel.setBorder(BorderFactory.createEtchedBorder());
		maskConfigurationsLabel.setFont(MSCGUIParameters.FONT_MASCGEN_TITLE);
		maskConfigurationsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		openedConfigsPanel.setLayout(new GridBagLayout());
		topConfigPanel.setBackground(MSCGUIParameters.COLOR_OPENED_MASKS_PANEL);
		bottomConfigPanel.setBackground(MSCGUIParameters.COLOR_OPENED_MASKS_PANEL);
		topConfigPanel.add(openConfigButton);
		topConfigPanel.add(copyConfigButton);
		topConfigPanel.add(closeConfigButton);
		bottomConfigPanel.add(saveConfigButton);
		bottomConfigPanel.add(saveAllConfigButton);
		row=0;
		openedConfigsPanel.add(maskConfigurationsLabel, new GridBagConstraints(0,row,1,1,10.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		if (showMaskConfigurationButtons) {
			row++;
			openedConfigsPanel.add(topConfigPanel, new GridBagConstraints(0,row,1,1,10.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		}
		row++;
		openedConfigsPanel.add(openedConfigsScrollPane, new GridBagConstraints(0,row,1,1,10.0,10.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, defaultInsets, 0,0));
		if (showMaskConfigurationButtons) {
			row++;
			openedConfigsPanel.add(bottomConfigPanel, new GridBagConstraints(0,row,1,1,10.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		}
		
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
		spinnerModel.setStepSize(0.1);
		spinnerModel.setMinimum(MosfireParameters.MINIMUM_SLIT_WIDTH);
		spinnerModel.setMaximum(MosfireParameters.MAXIMUM_SLIT_WIDTH);
		spinnerModel.setValue(MosfireParameters.DEFAULT_SLIT_WIDTH);
		longSlitWidthSpinner.setModel(spinnerModel);
		longSlitLabel.setHorizontalAlignment(JLabel.CENTER);
		longSlitLabel.setFont(MSCGUIParameters.FONT_MASCGEN_TITLE);
		openLongSlitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openLongSlitButton_actionPerformed(e);
			}
		});
		
		
		longSlitPanel.setBackground(MSCGUIParameters.COLOR_LONG_SLIT_PANEL);
		longSlitPanel.setBorder(BorderFactory.createEtchedBorder());		
		longSlitPanel.setLayout(new BorderLayout(5, 5));
		longSlitPanel.add(longSlitLabel, BorderLayout.NORTH);
		longSlitPanel.add(longSlitWidthLabel, BorderLayout.WEST);
		longSlitPanel.add(longSlitWidthSpinner, BorderLayout.CENTER);
		longSlitPanel.add(longSlitWidthUnits, BorderLayout.EAST);
		longSlitPanel.add(openLongSlitButton, BorderLayout.SOUTH);
		
		openMaskLabel.setHorizontalAlignment(JLabel.CENTER);
		openMaskLabel.setFont(MSCGUIParameters.FONT_MASCGEN_TITLE);
		openMaskButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openMaskButton_actionPerformed(e);
			}
		});
		openMaskPanel.setBackground(MSCGUIParameters.COLOR_OPEN_MASK_PANEL);
		openMaskPanel.setBorder(BorderFactory.createEtchedBorder());
		openMaskPanel.setLayout(new BorderLayout(5,5));
		openMaskPanel.add(openMaskLabel, BorderLayout.NORTH);
		openMaskPanel.add(openMaskButton, BorderLayout.SOUTH);
		
		topSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
//		topSplitPane.setContinuousLayout(true);
		topSplitPane.setDividerLocation(MSCGUIParameters.DIM_MAINFRAME.width  - MSCGUIParameters.WIDTH_MASCGEN_PANEL);
		//topSplitPane.setDividerLocation(topSplitPane.getWidth() - topSplitPane.getInsets().right - topSplitPane.getDividerSize() - MSCGUIParameters.WIDTH_MASCGEN_PANEL);

		objectsSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		objectsSplitPane.setDividerLocation(MSCGUIParameters.DIM_MAINFRAME.height - MSCGUIParameters.DIM_TABLE_TARGET_LIST.height);
		
		rightPanel.setMinimumSize(new Dimension(0,0));
		objectsSplitPane.setMinimumSize(new Dimension(0,0));
		slitConfigurationPanel.setMinimumSize(new Dimension(0,0));
		
		mascgenTabbedPane.add("Inputs", mascgenInputPanel);
		mascgenTabbedPane.add("Outputs", mascgenOutputPanel);
		mascgenTabbedPane.add("Status", mascgenStatusPanel);
		
		
		mascgenParamsButtonPanel.setLayout(new GridLayout(1,0));
		mascgenParamsButtonPanel.add(loadMascgenParamsButton);
		mascgenParamsButtonPanel.add(saveMascgenParamsButton);

		row=0;
		
		mascgenInputPanel.setLayout(new GridBagLayout());

		mascgenInputPanel.add(inputObjectListBrowseButton, new GridBagConstraints(0,row,3,1,1.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(inputObjectListLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(inputObjectListValueLabel, new GridBagConstraints(1,row,2,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(xRangeLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(xRangeField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(xRangeUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(xCenterLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(xCenterField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(xCenterUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(slitWidthLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(slitWidthField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(slitWidthUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(ditherSpaceLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(ditherSpaceField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(ditherSpaceUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(alignmentStarsLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(alignmentStarsField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(alignmentStarsUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(alignmentStarEdgeLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(alignmentStarEdgeField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(alignmentStarEdgeUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(useCenterOfPriorityCheckBox, new GridBagConstraints(0,row,3,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(centerRaDecLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(centerRaDecField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(centerRaDecUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(xStepsLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(xStepsField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(xStepsUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(xStepSizeLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(xStepSizeField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(xStepSizeUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(yStepsLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(yStepsField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(yStepsUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(yStepSizeLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(yStepSizeField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(yStepSizeUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(centerPALabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(centerPAField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(centerPAUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(paStepsLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(paStepsField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(paStepsUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(paStepSizeLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(paStepSizeField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenInputPanel.add(paStepSizeUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenInputPanel.add(runMascgenButton, new GridBagConstraints(0,row,3,1,1.0,0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));


		JPanel scrollPanel = new JPanel();
		scrollPanel.setLayout(new BorderLayout());
		scrollPanel.add(mascgenStatusScrollPane, BorderLayout.CENTER);
		mascgenStatusScrollPane.setBorder(BorderFactory.createTitledBorder("Status"));
		mascgenStatusScrollPane.setViewportView(mascgenStatusTextArea);
		mascgenStatusScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mascgenStatusScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		mascgenStatusTextArea.setLineWrap(true);
		mascgenStatusTextArea.setWrapStyleWord(true);
		mascgenStatusTextArea.setEditable(false);
		row=0;
		mascgenStatusPanel.setLayout(new GridBagLayout());
		mascgenStatusPanel.add(mascgenAbortButton, new GridBagConstraints(0,row,2,1,10.0,1.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenStatusPanel.add(mascgenRunNumberTitle, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenStatusPanel.add(mascgenRunNumberLabel, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenStatusPanel.add(mascgenTotalRunsTitle, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenStatusPanel.add(mascgenTotalRunsLabel, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenStatusPanel.add(mascgenTotalPriorityTitle, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenStatusPanel.add(mascgenTotalPriorityLabel, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenStatusPanel.add(mascgenOptimalRunNumberTitle, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenStatusPanel.add(mascgenOptimalRunNumberLabel, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenStatusPanel.add(scrollPanel, new GridBagConstraints(0,row,2,1,10.0,10.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, defaultInsets, 0,0));

		
		mascgenPanel.setBorder(BorderFactory.createEtchedBorder());
		mascgenPanel.setBackground(MSCGUIParameters.COLOR_MASCGEN_PANEL);
		mascgenPanelLabel.setHorizontalAlignment(JLabel.CENTER);
		mascgenPanelLabel.setFont(MSCGUIParameters.FONT_MASCGEN_TITLE);
		mascgenParamsButtonPanel.setBackground(MSCGUIParameters.COLOR_MASCGEN_PANEL);
		
		mascgenPanel.setLayout(new GridBagLayout());
		row=0;
		mascgenPanel.add(mascgenPanelLabel, new GridBagConstraints(0,row,3,1,1.0,0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenPanel.add(mascgenParamsButtonPanel, new GridBagConstraints(0,row,3,1,1.0,0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenPanel.add(maskNameLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenPanel.add(maskNameField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		mascgenPanel.add(maskNameUnitsLabel, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenPanel.add(mascgenTabbedPane, new GridBagConstraints(0,row,3,1,1.0,1.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));

		
		targetListPanel.add(targetListTableScrollPane, BorderLayout.CENTER);
		targetListPanel.setPreferredSize(MSCGUIParameters.DIM_TABLE_TARGET_LIST);
		
		
		specialMaskPanel.setLayout(new GridLayout(1,0));
		specialMaskPanel.add(openMaskPanel);
		specialMaskPanel.add(longSlitPanel);
		
		rightPanel.setLayout(new GridBagLayout());
		row=0;
		rightPanel.add(openedConfigsPanel,  new GridBagConstraints(0,row,1,1,10.0,2.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, defaultInsets, 0,0));
		if (MSCGUIParameters.ONLINE_MODE) {
			row++;
			rightPanel.add(specialMaskPanel,  new GridBagConstraints(0,row,1,1,10.0,0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		}
//		row++;
//		rightPanel.add(longSlitPanel,  new GridBagConstraints(0,row,1,1,10.0,0.0, GridBagConstraints.CENTER,
//				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		rightPanel.add(mascgenPanel,   new GridBagConstraints(0,row,1,1,10.0,0.0, GridBagConstraints.SOUTH,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		
		topSplitPane.setLeftComponent(objectsSplitPane);
		topSplitPane.setRightComponent(rightPanel);

		setupAlignmentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setupAlignmentButton_actionPerformed(e);
			}
		});
		setupScienceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setupScienceButton_actionPerformed(e);
			}
		});
		executeMaskButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executeMaskButton_actionPerformed(e);
			}
		});
		loadedMaskPanel.setBorder(BorderFactory.createEtchedBorder());
		loadedMaskPanel.setLayout(new BorderLayout());
		loadedMaskPanel.add(loadedMaskLabel, BorderLayout.CENTER);
		loadedMaskLabel.setHorizontalAlignment(JLabel.CENTER);
		
		csuReadyValueLabel.setText("-2: System Stopped");
		csuStatusPanel.setLayout(new GridBagLayout());
		csuStatusPanel.add(csuReadyLabel, new GridBagConstraints(0,0,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		csuStatusPanel.add(csuReadyValueLabel, new GridBagConstraints(1,0,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		csuStatusPanel.add(csuStatusLabel, new GridBagConstraints(2,0,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 20, 2, 2), 0,0));
		csuStatusPanel.add(csuStatusValueLabel, new GridBagConstraints(3,0,1,1,10.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		scriptButtonPanel.setLayout(new GridBagLayout());
		scriptButtonPanel.add(csuStatusPanel, new GridBagConstraints(0,0,4,1,10.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		scriptButtonPanel.add(setupAlignmentButton, new GridBagConstraints(0,1,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		scriptButtonPanel.add(setupScienceButton, new GridBagConstraints(1,1,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		scriptButtonPanel.add(loadedMaskPanel, new GridBagConstraints(2,1,1,1,10.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		scriptButtonPanel.add(executeMaskButton, new GridBagConstraints(3,1,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		
		middlePanel.setLayout(new BorderLayout());
		middlePanel.add(slitConfigurationPanel, BorderLayout.CENTER);
		if (myModel.isOnline()) {
			middlePanel.add(scriptButtonPanel, BorderLayout.SOUTH);
		}
		middlePanel.add(maskConfigPanel, BorderLayout.NORTH);

		slitListTableModel.setData(myModel.getCurrentSlitConfiguration().getMechanicalSlitList());
		slitListTable.setModel(slitListTableModel);
		TableColumn rowColumn = slitListTable.getColumnModel().getColumn(0);
		TableColumn centerColumn = slitListTable.getColumnModel().getColumn(1);
		TableColumn widthColumn = slitListTable.getColumnModel().getColumn(2);
		
		rowColumn.setCellRenderer(new CellEditorsAndRenderers.CenteredTextTableCellRenderer());
		centerColumn.setCellRenderer(new CellEditorsAndRenderers.DoubleValueTableCellRenderer(2));
		widthColumn.setCellRenderer(new CellEditorsAndRenderers.DoubleValueTableCellRenderer(2));
		
		rowColumn.setPreferredWidth(25);
		centerColumn.setPreferredWidth(75);
		widthColumn.setPreferredWidth(50);
		
		slitListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		slitListTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lsEv) {
				slitList_tableSelectionChanged(lsEv);
			}
		});
		/*
		slitListTable.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent tmEv) {
				slitListTableModelChanged(tmEv);
			}
		});
		*/
		slitListTableScrollPane.getViewport().add(slitListTable);
		slitListTableScrollPane.setPreferredSize(MSCGUIParameters.DIM_TABLE_SLIT_LIST);
		
		slitTablePanel.setLayout(new BorderLayout(5, 5));
		slitTablePanel.add(slitWidthPanel, BorderLayout.NORTH);
		slitTablePanel.add(slitListTableScrollPane, BorderLayout.CENTER);

		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(slitTablePanel, BorderLayout.WEST);
		leftPanel.add(middlePanel, BorderLayout.CENTER);
		
//		mainPanel.setLayout(new GridBagLayout());
//		mainPanel.add(topSplitPane, new GridBagConstraints(0,0,2,1,10.0,10.0, GridBagConstraints.NORTHWEST,
//				GridBagConstraints.BOTH, defaultInsets, 0,0));
//		mainPanel.add(singleSlitConfigurationPanel, new GridBagConstraints(0,2,2,1,10.0,0.5, GridBagConstraints.CENTER,
//				GridBagConstraints.BOTH, defaultInsets, 0,0));
//		mainPanel.add(buttonPanel, new GridBagConstraints(0,3,2,1,10.0,0.0, GridBagConstraints.CENTER,
//				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
//		mainPanel.add(targetListPanel, new GridBagConstraints(0,4,2,1,10.0,0.0, GridBagConstraints.CENTER,
//				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		
		objectsSplitPane.setTopComponent(leftPanel);
		objectsSplitPane.setBottomComponent(targetListPanel);
		
		statusPanel.setLayout(new GridBagLayout());
		statusPanel.add(statusBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
	        GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		if (MSCGUIParameters.ONLINE_MODE) {
			statusPanel.add(mosfireStatusPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0,
	        GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0));
			statusPanel.add(mcsusStatusPanel, new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0,
	        GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0));
			statusPanel.add(mdsStatusPanel, new GridBagConstraints(3, 0, 1, 1, 0.0, 1.0,
	        GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0));
		}
		contentPane.add(topSplitPane, BorderLayout.CENTER);    
		contentPane.add(statusPanel, BorderLayout.SOUTH);
		

	}
	protected void reassignMethod_actionPerformed(int type) {
		myModel.setCloseOffType(type);
	}
	protected void alignWithSlitBelow_actionPerformed() {
		alignSlitWithBelow();
	}

	protected void alignWithSlitAbove_actionPerformed() {
		alignSlitWithAbove();
	}

	protected void jMenuToolsCustomize_actionPerformed(ActionEvent e) {
		showCustomizeOptions();
	}
	protected void jMenuToolsOptions_actionPerformed(ActionEvent e) {
		showOptions();
	}
	private void showOptions() {
		if (MSCGUIParameters.ONLINE_MODE) {
			Object[] message = {writeSlitConfigurationHTMLOptionCheckBox.getOptionDialogCheckBox(), "", 
					duplicateMaskNameOptionCheckBox.getOptionDialogCheckBox(), "", 
					minAlignStarsOptionCheckBox.getOptionDialogCheckBox(), "", 
					setupMaskOptionCheckBox.getOptionDialogCheckBox(), "", 
					executeMaskOptionCheckBox.getOptionDialogCheckBox(), "", 
					executeDifferentMaskOptionCheckBox.getOptionDialogCheckBox(), "",
					reassignUnusedSlitsCheckBox, "",
					unusedSlitsOptionCheckBox.getOptionDialogCheckBox(), "",
					unusedBarOptionsPanel, ""};
			JOptionPane.showMessageDialog(this, message, "MSCGUI Options", JOptionPane.INFORMATION_MESSAGE);
			myModel.setMaximumSlitLength(((SpinnerNumberModel)(maximumSlitLengthField.getModel())).getNumber().intValue());
			myModel.setClosedOffSlitWidth(((SpinnerNumberModel)(reducedSlitWidthField.getModel())).getNumber().doubleValue());
			myModel.setMinimumCloseOffSlitWidth(((SpinnerNumberModel)(minimumReassignSlitWidthField.getModel())).getNumber().doubleValue());

		} else {
			Object[] message = {writeSlitConfigurationHTMLOptionCheckBox.getOptionDialogCheckBox(), "", 
					duplicateMaskNameOptionCheckBox.getOptionDialogCheckBox(), "", 
					minAlignStarsOptionCheckBox.getOptionDialogCheckBox(), "", 
					reassignUnusedSlitsCheckBox, ""};
			JOptionPane.showMessageDialog(this, message, "MSCGUI Options", JOptionPane.INFORMATION_MESSAGE);
		}
		myModel.setMascgenReassignUnusedSlits(reassignUnusedSlitsCheckBox.isSelected());
	}
	private void showCustomizeOptions() {
		boolean oldValue = showMaskConfigButtonsCheckBox.isSelected();
		if (JOptionPane.showConfirmDialog(this, showMaskConfigButtonsCheckBox, "Customize MSCGUI", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			showMaskConfigurationButtons = showMaskConfigButtonsCheckBox.isSelected();
			updateGUI();
		} else {
			showMaskConfigButtonsCheckBox.setSelected(oldValue);
		}
	}
	
	private void updateGUI() {
		openedConfigsPanel.removeAll();
		int row=0;
		openedConfigsPanel.add(maskConfigurationsLabel, new GridBagConstraints(0,row,1,1,10.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		if (showMaskConfigurationButtons) {
			row++;
			openedConfigsPanel.add(topConfigPanel, new GridBagConstraints(0,row,1,1,10.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		}
		row++;
		openedConfigsPanel.add(openedConfigsScrollPane, new GridBagConstraints(0,row,1,1,10.0,10.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, defaultInsets, 0,0));
		if (showMaskConfigurationButtons) {
			row++;
			openedConfigsPanel.add(bottomConfigPanel, new GridBagConstraints(0,row,1,1,10.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		}
		validate();
		repaint();
	}
	
	protected void jMenuToolsCalibration_actionPerformed(ActionEvent e) {
		showCalibrationToolsDialog();
	}
	private void showCalibrationToolsDialog() {
		Point p = this.getLocation();
		calibrationFrame.setLocation(p.x+100, p.y+100);
		ArrayList<SlitConfiguration> masks = getCustomSlitMasks();
		if (masks.size() > 0) {
			calibrationFrame.setSlitMasks(masks);
			calibrationFrame.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this, "No opened saveable configurations.", "Calibration Tool Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	private ArrayList<SlitConfiguration> getCustomSlitMasks() {
		ArrayList<SlitConfiguration> openedConfigs = myModel.getOpenedSlitConfigurations();
		ArrayList<SlitConfiguration> openedCustomConfigs = new ArrayList<SlitConfiguration>();
		for (SlitConfiguration config : openedConfigs) {
			if (config.getStatus() != SlitConfiguration.STATUS_UNSAVEABLE) {
				openedCustomConfigs.add(config);
			}
		}
		return openedCustomConfigs;
	}
/*	protected void slitWidthSpinner_stateChanged(ChangeEvent e) {
		JSpinner spinner = (JSpinner)(e.getSource());
		SpinnerNumberModel model = (SpinnerNumberModel)(spinner.getModel());
		double newValue = (Double)(model.getNumber()).doubleValue();
		if (newValue > MosfireParameters.MAXIMUM_SLIT_WIDTH) {
			JOptionPane.showMessageDialog(this, "Slit width must be no more than "+MosfireParameters.MAXIMUM_SLIT_WIDTH+" arcesc.", "Error setting slit width", JOptionPane.ERROR_MESSAGE);
			model.setValue(MosfireParameters.MAXIMUM_SLIT_WIDTH);
		} else if (newValue < MosfireParameters.MINIMUM_SLIT_WIDTH) {
			JOptionPane.showMessageDialog(this, "Slit width must be at least "+MosfireParameters.MINIMUM_SLIT_WIDTH+" arcesc.", "Error setting slit width", JOptionPane.ERROR_MESSAGE);
			model.setValue(MosfireParameters.MINIMUM_SLIT_WIDTH);
		}
	}
*/
	
	protected void currentSlitWidthSpinner_actionPerformed(ActionEvent e) {
		myModel.setCurrentSlitWidth(((Double)(((SpinnerNumberModel)(currentSlitWidthSpinner.getModel())).getNumber())).doubleValue());
	}

	protected void openLongSlitButton_actionPerformed(ActionEvent e) {
		openLongSlit();
	}
	private void openLongSlit() {
		myModel.openLongSlitConfiguration(((Double)(((SpinnerNumberModel)(longSlitWidthSpinner.getModel())).getNumber())).doubleValue());
	}
	protected void openMaskButton_actionPerformed(ActionEvent e) {
		openMask();
	}
	private void openMask() {
		myModel.openOpenMaskSlitConfiguration();
	}
	protected void executeMaskButton_actionPerformed(ActionEvent e) {
		executeMask();
	}

	protected void setupScienceButton_actionPerformed(ActionEvent e) {
		doMaskSetup(false);
	}

	protected void setupAlignmentButton_actionPerformed(ActionEvent e) {
		doMaskSetup(true);
	}

	private void doMaskSetup(boolean isAlign) {
		try {
			int answer;
			if (setupMaskOptionCheckBox.isSelected()) {
				Object message[] = {"This will load the current setup into the CSU.",
													"This will NOT execute the mask.",
													" ",
													"Proceed?", 
													" ",
													setupMaskOptionCheckBox, 
													"(This can be changed in Tools->Options)"};
				answer = JOptionPane.showConfirmDialog(this, message, "Confirm Mask Setup", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				setupMaskOptionCheckBox.setDefaultAnswer(answer);
			} else {
				answer = setupMaskOptionCheckBox.getDefaultAnswer();
			} 
			if (answer == JOptionPane.YES_OPTION) {
				//. warn about unused slits
				boolean[] usedSlits = myModel.getSlitUsageArray(isAlign);
				//. construct list of unused slits
				StringBuffer unusedSlitList = new StringBuffer();
				for (int ii=0; ii<usedSlits.length; ii++) {
					if (!usedSlits[ii]) {
						unusedSlitList.append(ii+1);
						unusedSlitList.append(", ");
					}
				}
				if (unusedSlitList.length() > 0) {
					//. TODO move button out of here.
					JButton optionsButton = new JButton("Options");
					optionsButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							showOptions();
						}
					});
					//. remove last comma
					unusedSlitList.deleteCharAt(unusedSlitList.length()-1);
					if (unusedSlitsOptionCheckBox.isSelected()) {
						if (myModel.getCloseOffType() == MSCGUIModel.CLOSE_OFF_TYPE_DO_NOTHING) {
							Object message[] = {"The following slits have not been assigned:",
									unusedSlitList.toString(),
									" ",
									"The current settings indicate the slits will be left as is.",
									"Settings can be changed under Tools->Options.",
									" ",
									"Proceed?",
									" ",
									unusedSlitsOptionCheckBox, 
							"(This can be changed in Tools->Options)"};
							answer = JOptionPane.showConfirmDialog(this, message, "Unassigned Slits", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);							
						} else {
							Object message[] = {"The following slits have not been assigned:",
								unusedSlitList.toString(),
								" ",
								"The current settings indicate that slits larger than "+myModel.getMinimumCloseOffSlitWidth()+ " arcsec wide", 
								"will be closed down to "+myModel.getClosedOffSlitWidth()+" arcsec"+
								((myModel.getCloseOffType() == MSCGUIModel.CLOSE_OFF_TYPE_REDUCE_IN_PLACE) ? " in place." : " and moved off the field of view."),
								"Settings can be changed under Tools->Options.",
								" ",
								"Proceed?",
								" ",
								unusedSlitsOptionCheckBox, 
								"(This can be changed in Tools->Options)"};
							answer = JOptionPane.showConfirmDialog(this, message, "Unassigned Slits", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						}
					} else {
						answer = JOptionPane.YES_OPTION;
					} 
					if (answer == JOptionPane.NO_OPTION) {
						return;
					}
				}
				myModel.executeMaskSetup(isAlign);
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Error executing mask setup: "+ex.getMessage(), "Error Executing Mask Setup", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			JOptionPane.showMessageDialog(this, "Interrupt during mask setup: "+ex.getMessage(),  "Setup Mask Interrupt", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	private void executeMask() {
		String loadedMask = myModel.getLoadedMaskSetup();
		int answer;
		if (executeMaskOptionCheckBox.isSelected()) {
			Object[] message = {"This will cause the CSU to configure the currently loaded mask, which is:",
				                  " ",
				                  loadedMask,
				                  " ",
				                  "This could take several minutes.  Do you wish to proceed?",
				                  executeMaskOptionCheckBox, 
													"(This can be changed in Tools->Options)"};
			answer = JOptionPane.showConfirmDialog(this, message, "Confirm Mask Configuration", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			executeMaskOptionCheckBox.setDefaultAnswer(answer);
		} else {
			answer = executeMaskOptionCheckBox.getDefaultAnswer();
		}
		if (answer == JOptionPane.YES_OPTION) {
			//. check that current slit configuration matched last loaded mask
			String currentSlitMaskName = myModel.getCurrentSlitConfiguration().getMaskName();
			if (!loadedMask.equals(currentSlitMaskName) && !loadedMask.equals(currentSlitMaskName+" (align)")) {
				if (executeDifferentMaskOptionCheckBox.isSelected()) {
					Object warningMessage[] = {"Current mask loaded in CSU (" + loadedMask + ")",
																			"does not match current slit configuration (" + currentSlitMaskName + ")",
																			" ",
																			"Are you sure you want to continue?", 
																			" ",
																			executeDifferentMaskOptionCheckBox, 
																			"(This can be changed in Tools->Options)"};
					answer  = JOptionPane.showConfirmDialog(this, warningMessage, "Confirm Mask Configuration", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					executeDifferentMaskOptionCheckBox.setDefaultAnswer(answer);
				} else {
					answer = executeDifferentMaskOptionCheckBox.getDefaultAnswer();
				}
				if (answer == JOptionPane.NO_OPTION) {
					return;
				}
			}
			try {
				myModel.executeMask();
			} catch (InterruptedException ex) {
				JOptionPane.showMessageDialog(this, "Interrupt during mask execution: "+ex.getMessage(),  "Execute Mask Interrupt", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (FitsException ex) {
				JOptionPane.showMessageDialog(this, "Error writing FITS extension: "+ex.getMessage(),  "FITS Extension Error", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Error executing mask: "+ex.getMessage(), "Error Executing Mask", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (NoSuchPropertyException ex) {
				JOptionPane.showMessageDialog(this, "Error executing mask: "+ex.getMessage(), "Error Executing Mask", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (InvalidValueException ex) {
				JOptionPane.showMessageDialog(this, "Error executing mask: "+ex.getMessage(), "Error Executing Mask", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
	
	private void setInputObjectList(String fullPath) {
		inputObjectListFullPath = fullPath;
		inputObjectListValueLabel.setToolTipText(inputObjectListFullPath);
		inputObjectListValueLabel.setText(FileUtilities.getNameOfFile(inputObjectListFullPath));
		inputObjectListValueLabel.setName(inputObjectListFullPath);

	}
	
	protected void inputObjectListBrowseButton_actionPerformed() {
		chooseTargetList();
	}
	protected void maskNameField_focusLost(FocusEvent e) {
		String maskName = maskNameField.getText().trim();
		if (maskName.isEmpty()) {
			maskName=MSCGUIParameters.DEFAULT_MASK_NAME;
			maskNameField.setText(maskName);
		}
		mascgenOutputPanel.updateOutputParams(maskName);
	}
	protected void copyConfigButton_actionPerformed(ActionEvent e) {
		copyNewSlitConfiguration();
	}
	private void copyNewSlitConfiguration() {
		int index = openedConfigsTable.getSelectedRow();
		if (index >= 0) {
			if (myModel.getCurrentSlitConfiguration().getStatus().equals(SlitConfiguration.STATUS_UNSAVEABLE)) {
				JOptionPane.showMessageDialog(this, "Unsaveable configurations cannot be copied.", "Cannot Copy Configuration", JOptionPane.ERROR_MESSAGE);
				return;
			}
			myModel.copySlitConfiguration(index);
		}

	}
	protected void openConfigButton_actionPerformed(ActionEvent e) {
		openNewSlitConfiguration();
	}
	private void openNewSlitConfiguration() {
		if (slitConfigurationFC.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			ArrayList<String> warningList = new ArrayList<String>();
			try {
				myModel.openSlitConfiguration(slitConfigurationFC.getSelectedFile(), warningList);
				if (!warningList.isEmpty()) {
					JOptionPane.showMessageDialog(this, constructWarningListDialogMessage("The following warnings were found while opening slit configuration file:", warningList, ""), "Warnings Found Opening File", JOptionPane.WARNING_MESSAGE);
				}
			} catch (JDOMException ex) {
				JOptionPane.showMessageDialog(this, "Error parsing Slit Configuration file:\n\n"+ slitConfigurationFC.getSelectedFile().getPath()+"\n\n"+ex.getMessage(), "Error Parsing File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Error opening Slit Configuration file:\n\n"+ slitConfigurationFC.getSelectedFile().getPath()+"\n\n"+ex.getMessage(), "Error Opening File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
	protected void saveConfigButton_actionPerformed(ActionEvent e) {
		saveSlitConfiguration();
	}
	private void saveSlitConfiguration() {
		if (slitConfigurationFC.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = slitConfigurationFC.getSelectedFile();
			if (file.exists()) {
				int answer = JOptionPane.showConfirmDialog(this, "File exists.  Overwrite?");
				if (answer == JOptionPane.NO_OPTION) {
					saveSlitConfiguration();
					return;
				} else if (answer == JOptionPane.CANCEL_OPTION) {
					return;
				}				
			}
			try {
				myModel.writeMSCFile(file);
				//. status should have changed
				openedConfigsTable.repaint();
				if (writeSlitConfigurationHTMLOptionCheckBox.isSelected()) {
					try {
						myModel.writeMSCHtmlFile(file);
					} catch (MalformedURLException ex) {
						Object[] message = {"Error saving HTML version of Slit Configuration file:",
								" ",
								file.getPath(), 
								" ",
								ex.getMessage(),
								" ",
								"The XML was saved successfully.", 
								" ",
								writeSlitConfigurationHTMLOptionCheckBox, 
						"(This can be changed in Tools->Options)"};
						JOptionPane.showMessageDialog(this, message, "Error Saving File", JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					} catch (TransformerException ex) {
						Object[] message = {"Error saving HTML version of Slit Configuration file:",
								" ",
								file.getPath(), 
								" ",
								ex.getMessage(),
								" ",
								"The XML was saved successfully.", 
								" ",
								writeSlitConfigurationHTMLOptionCheckBox, 
						"(This can be changed in Tools->Options)"};
						JOptionPane.showMessageDialog(this, message, "Error Saving File", JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
				}
			} catch (JDOMException ex) {
				JOptionPane.showMessageDialog(this, "Error saving Slit Configuration file:\n\n"+ file.getPath()+"\n\n"+ex.getMessage(), "Error Saving File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Error saving Slit Configuration file:\n\n"+ file.getPath()+"\n\n"+ex.getMessage(), "Error Saving File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
	protected void saveAllConfigButton_actionPerformed(ActionEvent e) {
		saveAllSlitConfigurationProducts();
	}
	private void saveAllSlitConfigurationProducts() {
		if (JOptionPane.showConfirmDialog(this, maskConfigOutputPanel, "Save All MASCGEN Products", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			try {
				
				MascgenArguments tempArgs = new MascgenArguments();
				maskConfigOutputPanel.fillMascgenArgrumentsWithOutputs(tempArgs);
				
				//. validate output directory
				File outputDir = validateOutputDirectory(tempArgs);
				//. if no exception thrown, root dir exists and is writeable
				if (outputDir.exists()) {
					ArrayList<String> filenamesInUse = getMaskOutputFilenamesInUse(tempArgs);
					if (!filenamesInUse.isEmpty()) {
						
						filenamesInUse.add(0, "The following output files already exist:");
						filenamesInUse.add(1, " ");
						filenamesInUse.add(" ");
						filenamesInUse.add("Overwrite?");
						int answer = JOptionPane.showConfirmDialog(this, filenamesInUse.toArray(), "Warning: Filenames In Use", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
						if (answer == JOptionPane.CANCEL_OPTION) {
							return;
						} else if (answer == JOptionPane.NO_OPTION) {
							saveAllSlitConfigurationProducts();
							return;
						}
					}
				} else {
					if (outputDir.mkdir() == false) {
						JOptionPane.showMessageDialog(this, "Error creating output directory:\n\n"+outputDir.getAbsolutePath(), "Error Saving Files", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				maskConfigOutputPanel.fillMascgenArgrumentsWithOutputs(myModel.getCurrentSlitConfiguration().getMascgenArgs());
				myModel.writeCurrentSlitConfigurationOutputs(writeSlitConfigurationHTMLOptionCheckBox.isSelected());
				//. status should have changed
				openedConfigsTable.repaint();
			} catch (InvalidValueException ex) {
				JOptionPane.showMessageDialog(this, "Error saving Slit Configuration files.\n\n"+ex.getMessage(), "Error Saving Files", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (JDOMException ex) {
				JOptionPane.showMessageDialog(this, "Error saving Slit Configuration files.\n\n"+ex.getMessage(), "Error Saving Files", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Error saving Slit Configuration file.\n\n"+ex.getMessage(), "Error Saving Files", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (TransformerException ex) {
				JOptionPane.showMessageDialog(this, "Error saving Slit Configuration file.\n\n"+ex.getMessage(), "Error Saving Files", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
	private ArrayList<String> getMaskOutputFilenamesInUse(MascgenArguments args) {
		ArrayList<String> list = new ArrayList<String>();

		String[] filenameArray = {args.getFullPathOutputMSC(),
															args.getFullPathOutputMascgenParams(),
															args.getFullPathOutputMaskScript(),
															args.getFullPathOutputAlignMaskScript(), 
															args.getFullPathOutputMaskTargets(),
															args.getFullPathOutputSlitList(),
															args.getFullPathOutputDS9Regions(),
															args.getFullPathOutputStarList()
		};
		
		File currentFile;

		for (String filename : filenameArray) {
			currentFile = new File(filename);
			if (currentFile.exists()) {
				list.add(filename);
			}
		}
		return list;
	}
	protected void closeConfigButton_actionPerformed(ActionEvent e) {
		closeCurrentConfig();
	}
	private void closeCurrentConfig() {
		int index = openedConfigsTable.getSelectedRow();
		if (index >= 0) {
			if (!myModel.getCurrentSlitConfiguration().getStatus().equals(SlitConfiguration.STATUS_SAVED) &&
					!myModel.getCurrentSlitConfiguration().getStatus().equals(SlitConfiguration.STATUS_UNSAVEABLE)) {
				if (JOptionPane.showConfirmDialog(this, "Close without saving?", "File has not been saved.", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
					return;
				}
			}
			myModel.closeSlitConfiguration(index);
		}
}
	protected void saveMascgenParamsButton_actionPerformed(ActionEvent e) {
		saveMascgenParams();
	}
	protected void loadMascgenParamsButton_actionPerformed(ActionEvent e) {
		loadMascgenParams();
	}
	
	private void loadMascgenParams() {
		mascgenParamsFC.setDialogTitle("Load MASCGEN Parameters");
		if  (mascgenParamsFC.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				ArrayList<String> warningList = new ArrayList<String>();
				MascgenArguments args = MascgenArguments.readMascgenParamFile(mascgenParamsFC.getSelectedFile(), warningList);
				updateViewMascgenArguments(args);
				if (!warningList.isEmpty()) {
					JOptionPane.showMessageDialog(this, constructWarningListDialogMessage("The following warnings were found while opening MASCGEN configuration file:", warningList, ""), "Warnings Found Opening File", JOptionPane.WARNING_MESSAGE);
				}
			
			} catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(this, "Error opening parameter file:\n\n"+ mascgenParamsFC.getSelectedFile().getPath()+"\n\n"+ex.getMessage(), "Error Opening File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (JDOMException ex) {
				JOptionPane.showMessageDialog(this, "Error parsing parameter file:\n\n"+ mascgenParamsFC.getSelectedFile().getPath()+"\n\n"+ex.getMessage(), "Error Parsing File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Error opening parameter file:\n\n"+ mascgenParamsFC.getSelectedFile().getPath()+"\n\n"+ex.getMessage(), "Error Opening File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
	private void saveMascgenParams() {
		mascgenParamsFC.setDialogTitle("Save MASCGEN Parameters");
		if  (mascgenParamsFC.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File saveFile = mascgenParamsFC.getSelectedFile();
			if (saveFile.exists()) {
				int answer = JOptionPane.showConfirmDialog(this, "File exists.  Overwrite?");
				if (answer == JOptionPane.NO_OPTION) {
					saveMascgenParams();
					return;
				} else if (answer == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}
			try {
				MascgenArguments args = constructMascgenArgumentsFromFields();
				args.writeMascgenParamFile(saveFile);
			} catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(this, "Error saving parameter file:\n\n"+ saveFile.getPath()+"\n\n"+ex.getMessage(),
						"Error Saving File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (InvalidValueException ex) {
				//. TODO: ignore this error.  maybe separate method that doesn't validate dir?
				JOptionPane.showMessageDialog(this, "Error with parameters: " + ex.getMessage(), "Error starting MASCGEN", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (JDOMException ex) {
				JOptionPane.showMessageDialog(this, "XML error writing parameter file:\n\n"+ saveFile.getPath()+"\n\n"+ex.getMessage(),
						"Error Saving File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Error saving parameter file:\n\n"+ saveFile.getPath()+"\n\n"+ex.getMessage(),
						"Error Saving File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	protected void useCenterOfPriorityCheckBox_actionPerformed(ActionEvent e) {
		centerRaDecField.setEnabled(!useCenterOfPriorityCheckBox.isSelected());
	}


	private MascgenArguments constructMascgenArgumentsFromFields() throws NumberFormatException, InvalidValueException {		
		MascgenArguments args = new MascgenArguments(maskNameField.getText().trim(), inputObjectListValueLabel.getName(), 
				Double.parseDouble(xRangeField.getText()), Double.parseDouble(xCenterField.getText()), 
				Double.parseDouble(slitWidthField.getText()), Double.parseDouble(ditherSpaceField.getText()),
				getRaDecFromString(centerRaDecField.getText().trim()), useCenterOfPriorityCheckBox.isSelected(),
				Integer.parseInt(xStepsField.getText()), Double.parseDouble(xStepSizeField.getText()),
				Integer.parseInt(yStepsField.getText()), Double.parseDouble(yStepSizeField.getText()),
				Double.parseDouble(centerPAField.getText()), Integer.parseInt(paStepsField.getText()), Double.parseDouble(paStepSizeField.getText()),
				Integer.parseInt(alignmentStarsField.getText()), Double.parseDouble(alignmentStarEdgeField.getText()));
		mascgenOutputPanel.fillMascgenArgrumentsWithOutputs(args);
		validateOutputDirectory(args);
		return args;
	}
	private RaDec getRaDecFromString(String raDecString) throws NumberFormatException, InvalidValueException {
		String[] splits = raDecString.split(" ");
		if (splits.length < 6) {
			throw new InvalidValueException("Invalid number of components in Ra/Dec string");
		}
		return new RaDec(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]), Double.parseDouble(splits[2]), Double.parseDouble(splits[3]), Double.parseDouble(splits[4]), Double.parseDouble(splits[5]));

	}
	private File validateOutputDirectory(MascgenArguments args) throws InvalidValueException {
		String testDir = args.getOutputDirectory()+File.separator+args.getOutputSubdirectory();
		//. check to see if testDir exists
		File testFile = new File(testDir);
		if(testFile.exists()) {
			//. check to see if dir is writable
			if (!testFile.canWrite()) {
				throw new InvalidValueException("Output directory <"+testFile+"> is not writable.");
			}
			//. TODO validate all output products for writeability
		} else { 
			File parentDir = new File(args.getOutputDirectory());
			if (parentDir.exists()) {
				//. check to see if dir is writable
				if (!parentDir.canWrite()) {
					throw new InvalidValueException("Output root directory <"+parentDir+"> is not writable.");
				}				
			} else {
				//. check it's parent
				File rootParent = parentDir.getParentFile();
				if (rootParent == null) {
					throw new InvalidValueException("Output root directory <"+parentDir+"> cannot be created. Parent directory does not exist.");					
				} else {
					//. check to see if dir is writable
					if (!parentDir.canWrite()) {
						throw new InvalidValueException("Output root directory <"+parentDir+"> cannot be created. Parent directory is not writeable.");
					}									
				}
			}
		}		
		return testFile;
	}
	protected void abortMascgenButton_actionPerformed(ActionEvent e) {	
		myModel.abortMascgen();
	}
	protected void runMascgenButton_actionPerformed(ActionEvent e) {	
		String maskName = maskNameField.getText().trim();
		int index = myModel.getSlitConfigurationIndex(maskName);
		if (index >= 0) {
			int answer;
			if (duplicateMaskNameOptionCheckBox.isSelected()) {
				Object[] message = {"A Mask Configuration already is opened with the mask name", " ", maskName, " ", 
					"Do you wish to replace?  If not, please change mask name.", " ", duplicateMaskNameOptionCheckBox, 
					"(This can be changed in Tools->Options.)"}; 
				answer = JOptionPane.showConfirmDialog(this, message, "Mask Name in Use", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				duplicateMaskNameOptionCheckBox.setDefaultAnswer(answer);
			} else {
				answer = duplicateMaskNameOptionCheckBox.getDefaultAnswer();
			}
			if (answer  == JOptionPane.NO_OPTION) {	
				maskNameField.requestFocus();
				maskNameField.selectAll();
				return;
			}
		}
		runMascgen();
	}

	private void runMascgen() {
		try {
			MascgenArguments data = constructMascgenArgumentsFromFields();
			if (data.getMinimumAlignmentStars() < MosfireParameters.SUGGESTED_MINIMUM_ALIGNMENT_STARS) {
				int answer;
				if (minAlignStarsOptionCheckBox.isSelected()) {
					Object[] message = {"Specified number of alignment stars ("+data.getMinimumAlignmentStars()+")",
							              "is less than suggested minimum of "+MosfireParameters.SUGGESTED_MINIMUM_ALIGNMENT_STARS+".", 
							              " ", 
														"Do you wish to continue anyway?", 
														" ", 
														minAlignStarsOptionCheckBox, 
														"(This can be changed in Tools->Options)"}; 
					answer = JOptionPane.showConfirmDialog(this, message, "Check Alignment Star Count", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					minAlignStarsOptionCheckBox.setDefaultAnswer(answer);
				} else {
					answer = minAlignStarsOptionCheckBox.getDefaultAnswer();
				}
				if (answer == JOptionPane.NO_OPTION) {
					alignmentStarsField.requestFocus();
				  alignmentStarsField.selectAll();
				  return;
				}
			}
			mascgenStatusTextArea.setText("");
			mascgenTabbedPane.setSelectedComponent(mascgenStatusPanel);
			myModel.startMascgen(data);
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Error parsing parameters: " + ex.getMessage(), "Error starting MASCGEN", JOptionPane.ERROR_MESSAGE);
		} catch (InvalidValueException ex) {
			JOptionPane.showMessageDialog(this, "Error with parameters: " + ex.getMessage(), "Error starting MASCGEN", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
		
	}


	
	public void stateChanged(ChangeEvent cEv) {
		System.out.println("State Changed: "+cEv.toString());

	}

	//File | Open action performed
	public void jMenuFileOpenTargetList_actionPerformed(ActionEvent e) {
		chooseTargetList();
	}
	//File | Open MSC action performed

	public void jMenuFileOpenMSC_actionPerformed(ActionEvent e) {
		openNewSlitConfiguration();
	}
	//File | Copy MSC action performed
	public void jMenuFileCopyMSC_actionPerformed(ActionEvent e) {
		copyNewSlitConfiguration();
	}
	//File | Save MSC action performed
	public void jMenuFileSaveMSC_actionPerformed(ActionEvent e) {
		saveSlitConfiguration();
	}
	//File | Save All action performed
	public void jMenuFileSaveAll_actionPerformed(ActionEvent e) {
		saveAllSlitConfigurationProducts();
	}
	//File | Close MSC action performed
	public void jMenuFileCloseMSC_actionPerformed(ActionEvent e) {
		closeCurrentConfig();
	}
	//File | Set Executed Mask Directory action performed
	public void jMenuFileSetExecutedMaskDir_actionPerformed(ActionEvent e) {
		setExecutedMaskDir();
	}
	private void setExecutedMaskDir() {
		executedMaskDirFC.setSelectedFile(myModel.getScriptDirectory());
		int retval = executedMaskDirFC.showOpenDialog(this);
		if (retval == JFileChooser.APPROVE_OPTION) {
			File newExecutedMaskDir = executedMaskDirFC.getSelectedFile();
			try {
				if (FileUtilities.confirmOrCreateDirectory(newExecutedMaskDir, this)) {
					myModel.setScriptDirectory(newExecutedMaskDir);
					return;
				}
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Invalid directory: "+ex.getMessage(), "Invalid Directory", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
			setExecutedMaskDir();
			return;
		}
	}
	
	//File | Exit action performed
	public void jMenuFileExit_actionPerformed(ActionEvent e) {
		closeGUI();
	}
	private void closeGUI() {
		//. check for unsaved configs
		if (myModel.hasUnsavedSlitConfigurationsOpened()) {
			if (JOptionPane.showConfirmDialog(this, "There are unsaved slit configurations opened.  Discard?", "Unsaved Configurations Open", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
				return;
			}
		}
		try {
			myModel.stopCShow();
			System.exit(0);
		} catch (KJavaException ex) {
			ex.printStackTrace();
			Object[] message = {"Error stopping CShow:", " ", ex.getMessage(), " ", "Exit anyway?"};
			if (JOptionPane.showConfirmDialog(this, message, "Error stopping CShow", JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}
	//Help | About action performed
	public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
		MosfireAboutBox dlg = new MosfireAboutBox(this, MSCGUIParameters.GUI_TITLE);
		dlg.setVersion("Version 0.7");
		dlg.setReleased("8 August 2011");
		dlg.setLocationAtCenter(this);
		dlg.setModal(true);
		dlg.setVisible(true);
	}
	public void openedConfigsTable_mouseReleased(MouseEvent e) {
        int r = openedConfigsTable.rowAtPoint(e.getPoint());
        if (r >= 0 && r < openedConfigsTable.getRowCount()) {
        	openedConfigsTable.setRowSelectionInterval(r, r);
        } else {
        	openedConfigsTable.clearSelection();
        }

        int rowindex = openedConfigsTable.getSelectedRow();
        if (e.getButton() == getMouseButton(MSCGUIParameters.CONTEXT_MENU_MOUSE_BUTTON)) {
            if (rowindex < 0) {
            	return;
            }
           	maskConfigsPopup.show(e.getComponent(), e.getX(), e.getY());
        }	
	}
	public void openedConfigsPanel_mouseReleased(MouseEvent e) {
        if (e.getButton() == getMouseButton(MSCGUIParameters.CONTEXT_MENU_MOUSE_BUTTON)) {
           	openMaskConfigPopup.show(e.getComponent(), e.getX(), e.getY());
        }	
	}
	public void slitConfigurationPanel_mouseClicked(MouseEvent mEv) {
		int row = slitConfigurationPanel.getRow(mEv.getY());
		logger.debug("mouseClicked: "+mEv.getY()+" -> "+row);
		myModel.setActiveSlitRow(row+1);
		if (mEv.getButton() == getMouseButton(MSCGUIParameters.CONTEXT_MENU_MOUSE_BUTTON)) {
			logger.debug("Right click");
			if (!myModel.getCurrentSlitConfiguration().getStatus().equals(SlitConfiguration.STATUS_UNSAVEABLE)) {
				if (row >= 0 && row < MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS) {
					JPopupMenu menu = new JPopupMenu();
					if (row > 0) {
						menu.add(alignWithAboveMenuItem);
					} 
					if (row < MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS - 1) {
						menu.add(alignWithBelowMenuItem);
					}
					AstroObj target = slitConfigurationPanel.getCurrentTarget();
					if (target != null) {
						menu.addSeparator();
						menu.add(new JLabel("   "+target.getObjName()));
						menu.add(targetInfoMenu);
						targetInfoPanel.setAstroObj(target);
					}
					menu.show(mEv.getComponent(), mEv.getX(), mEv.getY());
				}
			}
		}
	}
	private int getMouseButton(int buttonNumber) {
		switch (buttonNumber) {
		case 1: return MouseEvent.BUTTON1;
		case 2: return MouseEvent.BUTTON2;
		case 3: return MouseEvent.BUTTON3;
		default: return 0;
		}
	}
	public void slitList_tableSelectionChanged(ListSelectionEvent lsEv) {
		myModel.setActiveRow(slitListTable.getSelectedRow());
	}
	public void targetList_tableSelectionChanged(ListSelectionEvent lsEv) {
		if (lsEv.getValueIsAdjusting()) {
			if (targetListTable.getSelectedRow() >= 0) {
				myModel.setActiveObject((AstroObj)(targetListTableModel.getData().get(targetListTable.getSelectedRow())));
			}
		}
	}
	public void openedConfigsTable_tableSelectionChanged(ListSelectionEvent lsEv) {
		myModel.setCurrentSlitConfigurationIndex(openedConfigsTable.getSelectedRow());
	}
	public void slitConfigurationTableModelChanged(TableModelEvent tmEv) {
		logger.debug("opened configs model changed: col="+tmEv.getColumn()+", first row="+tmEv.getFirstRow()+", last row="+tmEv.getLastRow());
		if ((tmEv.getColumn() == -2)) {
			if (openedConfigsTableModel.getNewMaskName().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Slit configuration name cannot be empty.", "Error setting mask name", JOptionPane.ERROR_MESSAGE);						
			} else {
				JOptionPane.showMessageDialog(this, "Slit configuration name "+openedConfigsTableModel.getNewMaskName()+" is taken.  Names must be unique.", "Error setting mask name", JOptionPane.ERROR_MESSAGE);		
			}
		} else {
			updateViewCurrentMaskName(myModel.getCurrentSlitConfiguration().getMaskName());
		}
	}
	public void targetListTableModelChanged(TableModelEvent tmEv) {
//		slitConfigurationPanel.repaint();
		//.repaint();
	}

	private void chooseTargetList() {
		//. browse for file
		int retval = objectListFC.showOpenDialog(this);

		//. if file is selected
		if (retval == JFileChooser.APPROVE_OPTION) {
			//. get file
			File file = objectListFC.getSelectedFile();
			setInputObjectList(objectListFC.getSelectedFile().getPath());
			openTargetList(file);
		}
	}

	public void nudgeSlit(double nudgeAmount) {
		myModel.moveActiveSlit(nudgeAmount);
		slitListTable.repaint();
	}
	public void alignSlitWithAbove() {
		myModel.alignActiveSlitWithAbove();
	}
	public void alignSlitWithBelow() {
		myModel.alignActiveSlitWithBelow();
	}
	//Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			closeGUI();
		} else if (e.getID() == WindowEvent.WINDOW_DEICONIFIED) {
			repaint();
		}
	}
	private void updateView() {
		updateViewCurrentMascgenResult(myModel.getCurrentMascgenResult());
		updateViewCurrentSlitConfiguration(myModel.getCurrentSlitConfiguration());
		updateViewCurrentSlitConfigurationIndex(-1);
		updateViewLoadedMaskSetup(myModel.getLoadedMaskSetup());
		updateViewCurrentSlitWidth(myModel.getCurrentSlitWidth());
		updateViewScriptRunning(myModel.isScriptRunning());
		updateViewCSUStatus(myModel.getCsuStatus());
		updateViewCSUReady(myModel.getCsuReady());
		updateViewUnusedBarOptions();
	}
	private void updateViewUnusedBarOptions() {
		
//		reducedSlitWidthField.setValue(Double.toString(myModel.getClosedOffSlitWidth()));
//		minimumReassignSlitWidthField.setValue(Double.toString(myModel.getMinimumCloseOffSlitWidth()));
//		maximumSlitLengthField.setValue(Integer.toString(myModel.getMaximumSlitLength()));
		updateViewCloseOffType(myModel.getCloseOffType());
	}
	
	private void updateViewActiveRow(int slitTableRow) {
		logger.debug("active slit table row = "+slitTableRow);
		try {
			if (slitTableRow < 0) {
				slitListTable.clearSelection();
				targetListTable.clearSelection();
				slitConfigurationPanel.setActiveRow(-1);
			} else {
				slitListTable.setRowSelectionInterval(slitTableRow, slitTableRow);


				//. scroll viewport of scrollpane to show selected row
				//. from Java Developers Almanac 1.4, item e947
				Rectangle rect = slitListTable.getCellRect(slitTableRow, 0, true);
				Point pt = slitListTableScrollPane.getViewport().getViewPosition();
				rect.setLocation(rect.x-pt.x, rect.y-pt.y);

				slitListTableScrollPane.getViewport().scrollRectToVisible(rect);

				int targetIndex = targetListTableModel.getIndexOfTarget(myModel.getCurrentSlitConfiguration().getMechanicalSlitList().get(slitTableRow).getTarget());
				if (targetIndex >= 0) {
					targetListTable.setRowSelectionInterval(targetIndex, targetIndex);

					rect = targetListTable.getCellRect(targetIndex, 0, true);
					pt = targetListTableScrollPane.getViewport().getViewPosition();
					rect.setLocation(rect.x-pt.x, rect.y-pt.y);

					targetListTableScrollPane.getViewport().scrollRectToVisible(rect);
				}
				slitConfigurationPanel.setActiveRow(myModel.getCurrentSlitConfiguration().getMechanicalSlitList().get(slitTableRow).getSlitNumber()-1);
			}
			slitConfigurationPanel.repaint();
		} catch (ArrayIndexOutOfBoundsException aioobEx) {
			JOptionPane.showMessageDialog(this, "Error selecting slit: "+aioobEx.getMessage(), "Slit Selection Error", JOptionPane.ERROR_MESSAGE);
			aioobEx.printStackTrace();
		}
	}
	private void updateViewCurrentMascgenResult(MascgenResult result) {
		centerValueLabel.setText(result.getCenter().toStringWithColons());
		paValueLabel.setText(Double.toString(result.getPositionAngle()));
		totalPriorityValueLabel.setText(Double.toString(result.getTotalPriority()));
	}
	private void updateViewMascgenArguments(MascgenArguments data) {
		inputObjectListFullPath = data.getTargetList();
		inputObjectListValueLabel.setToolTipText(inputObjectListFullPath);
		inputObjectListValueLabel.setText(FileUtilities.getNameOfFile(inputObjectListFullPath));
		inputObjectListValueLabel.setName(inputObjectListFullPath);
		useCenterOfPriorityCheckBox.setSelected(data.usesCenterOfPriority());
		centerRaDecField.setEnabled(!data.usesCenterOfPriority());
		xRangeField.setText(Double.toString(data.getxRange()));
		xCenterField.setText(Double.toString(data.getxCenter()));
		slitWidthField.setText(Double.toString(data.getSlitWidth()));
		ditherSpaceField.setText(Double.toString(data.getDitherSpace()));
		centerRaDecField.setText(data.getCenterPosition().toString());
		xStepsField.setText(Integer.toString(data.getxSteps()));
		xStepSizeField.setText(Double.toString(data.getxStepSize()));
		yStepsField.setText(Integer.toString(data.getySteps()));
		yStepSizeField.setText(Double.toString(data.getyStepSize()));
		centerPAField.setText(Double.toString(data.getCenterPA()));
		paStepsField.setText(Integer.toString(data.getPaSteps()));
		paStepSizeField.setText(Double.toString(data.getPaStepSize()));
		alignmentStarsField.setText(Integer.toString(data.getMinimumAlignmentStars()));
		alignmentStarEdgeField.setText(Double.toString(data.getAlignmentStarEdgeBuffer()));
		maskNameField.setText(data.getMaskName());
		mascgenOutputPanel.updateOutputParams(data);
		maskConfigOutputPanel.updateOutputParams(data);
//		if  (!inputObjectListFullPath.isEmpty()) {
//			openTargetList(new File(inputObjectListFullPath));
//		}
	}

	private void updateViewCurrentMaskName(String name) {
		currentMaskNameValueLabel.setText(name);
		maskConfigOutputPanel.updateOutputParams(name);
		openedConfigsTable.repaint();
	}
	private void updateViewCurrentSlitConfiguration(SlitConfiguration config) {
		currentMaskNameValueLabel.setText(config.getMaskName());
		updateViewMascgenArguments(config.getMascgenArgs());
		updateViewCurrentMascgenResult(config.getMascgenResult());
		slitListTableModel.setData(config.getMechanicalSlitList());
		targetListTableModel.setData(config.getAllTargets());
		updateViewSlitConfigurationStatus(config.getStatus());
		//setupAlignmentButton.setEnabled((config.getAlignmentStarCount() > 0));
		updateScriptButtons();
		File defaultMSCFilename;
		File defaultMSCOutputDir = new File(config.getMascgenArgs().getFullPathOutputSubdirectory());
		//. if output dir and subdirectory exist, use full path
		if (defaultMSCOutputDir.exists()) {
			defaultMSCFilename = new File(config.getMascgenArgs().getFullPathOutputMSC());
		} else {
			//. otherwise, try using output dir without subdir
			defaultMSCFilename = new File(config.getMascgenArgs().getOutputDirectory()+File.separator+config.getMascgenArgs().getOutputMSC());
		}
		slitConfigurationFC.setSelectedFile(defaultMSCFilename);
		try {
			slitConfigurationPanel.openNewConfiguration(config);
		} catch (InvalidValueException ex) {
			ex.printStackTrace();
		}
	}
	private void updateViewSlitConfigurationStatus(String status) {
		openedConfigsTable.repaint();
		currentSlitWidthSetButton.setEnabled(!status.equals(SlitConfiguration.STATUS_UNSAVEABLE));
		copyConfigButton.setEnabled(!status.equals(SlitConfiguration.STATUS_UNSAVEABLE));
		saveConfigButton.setEnabled(!status.equals(SlitConfiguration.STATUS_UNSAVEABLE));
		saveAllConfigButton.setEnabled(!status.equals(SlitConfiguration.STATUS_UNSAVEABLE));
	}
	private void updateViewCurrentSlitConfigurationIndex(int index) {
		logger.debug("updating view with current slit index " + index);
		if (index < 0) {
			openedConfigsTable.clearSelection();
			closeConfigButton.setEnabled(false);
			copyConfigButton.setEnabled(false);
			saveConfigButton.setEnabled(false);
			saveAllConfigButton.setEnabled(false);
		} else {
			try {
				openedConfigsTable.setRowSelectionInterval(index, index);

				//. scroll viewport of scrollpane to show selected row
				//. from Java Developers Almanac 1.4, item e947
				Rectangle rect = openedConfigsTable.getCellRect(index, 0, true);
				Point pt = openedConfigsScrollPane.getViewport().getViewPosition();
				rect.setLocation(rect.x-pt.x, rect.y-pt.y);

				slitListTableScrollPane.getViewport().scrollRectToVisible(rect);
				closeConfigButton.setEnabled(true);
				copyConfigButton.setEnabled(!myModel.getCurrentSlitConfiguration().getStatus().equals(SlitConfiguration.STATUS_UNSAVEABLE));
				saveConfigButton.setEnabled(!myModel.getCurrentSlitConfiguration().getStatus().equals(SlitConfiguration.STATUS_UNSAVEABLE));
				saveAllConfigButton.setEnabled(!myModel.getCurrentSlitConfiguration().getStatus().equals(SlitConfiguration.STATUS_UNSAVEABLE));

			} catch (ArrayIndexOutOfBoundsException aioobEx) {
				JOptionPane.showMessageDialog(this, "Error selecting mask configuration: "+aioobEx.getMessage(), "Mask Configuration Selection Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private void updateViewOpenedSlitConfigurations() {
		openedConfigsTableModel.fireTableDataChanged();
//		updateViewCurrentSlitConfigurationIndex(myModel.getCurrentSlitConfigurationIndex());
	}
	private void updateViewLoadedMaskSetup(String newValue) {
		loadedMaskLabel.setText(newValue);
	}
	private void updateViewMascgenTotalPriority(double doubleValue) {
		DecimalFormat f = NumberFormatters.StandardFloatFormatter(2);
		mascgenTotalPriorityLabel.setText(f.format(doubleValue));
	}
	private void updateViewMascgenOptimalRunNumber(int intValue) {
		mascgenOptimalRunNumberLabel.setText(Integer.toString(intValue));
	}
	private void updateViewMascgenTotalRuns(int intValue) {
		mascgenTotalRunsLabel.setText(Integer.toString(intValue));
	}
	private void updateViewMascgenRunNumber(int intValue) {
		mascgenRunNumberLabel.setText(Integer.toString(intValue));
	}
	private void updateViewMascgenRunStatus(String status) {
		mascgenStatusTextArea.append(status+"\n");
		mascgenStatusTextArea.setCaretPosition(mascgenStatusTextArea.getDocument().getLength());
	}
	private void updateViewMascgenArgumentsException(MascgenArgumentException ex) {
		updateViewMascgenRunStatus("Error running mascgen: "+ex.getMessage());
		JOptionPane.showMessageDialog(this, "Error with MASCGEN Arguments: " + ex.getMessage(), "Error running MASCGEN", JOptionPane.ERROR_MESSAGE);
		ex.printStackTrace();
	}
	private void updateViewScriptRunning(boolean status) {
		updateScriptButtons();
	}
	private void updateScriptButtons() {
		boolean okToSendTargets = Arrays.asList(MSCGUIParameters.CSU_READINESS_STATES_OK_TO_SEND_TARGETS).contains(myModel.getCsuReady());
		setupAlignmentButton.setEnabled(!myModel.isScriptRunning() && okToSendTargets &&
				(myModel.getCurrentSlitConfiguration().getAlignmentStarCount() > 0));
		setupScienceButton.setEnabled(!myModel.isScriptRunning() && okToSendTargets);
		executeMaskButton.setEnabled(!myModel.isScriptRunning() && (myModel.getCsuReady() == MSCGUIParameters.CSU_READINESS_STATE_READY_TO_MOVE));
	}
	private void updateViewCurrentSlitWidth(double width) {
		currentSlitWidthSpinner.setValue(width);
	}
	private void updateViewCSUStatus(String value) {
  	csuStatusValueLabel.setText(value);
  }
	private void updateViewCSUReady(int value) {
  	csuReadyValueLabel.setText(MSCGUIParameters.CSU_READINESS_STATES[value+MSCGUIParameters.CSU_READINESS_STATES_ARRAY_OFFSET]);
		updateScriptButtons();
  }
	private void updateViewCloseOffType(int type) {
		switch (type) {
		case MSCGUIModel.CLOSE_OFF_TYPE_DO_NOTHING: 
			unusedBarDoNothingButton.setSelected(true);
			break;
		case MSCGUIModel.CLOSE_OFF_TYPE_REDUCE_IN_PLACE:
			unusedBarReduceWidthButton.setSelected(true);
			break;
		case MSCGUIModel.CLOSE_OFF_TYPE_CLOSE_OFF:
			unusedBarCloseOffButton.setSelected(true);
			break;
		}
		minimumReassignSlitWidthLabel.setEnabled(type != MSCGUIModel.CLOSE_OFF_TYPE_DO_NOTHING);
		minimumReassignSlitWidthField.setEnabled(type != MSCGUIModel.CLOSE_OFF_TYPE_DO_NOTHING);
		reducedSlitWidthLabel.setEnabled(type != MSCGUIModel.CLOSE_OFF_TYPE_DO_NOTHING);
		reducedSlitWidthField.setEnabled(type != MSCGUIModel.CLOSE_OFF_TYPE_DO_NOTHING);
	}
	private void openTargetList(File file) {
		
		try {
			//. have model open file can put in target list
			myModel.openTargetList(file);
			//. target list has been updated, so notify table
			targetListTableModel.fireTableDataChanged();
		} catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(this, new String[] {"Error opening target list: File Not Found. ", " ", fnfEx.getMessage()}, "Error Opening Target List", JOptionPane.ERROR_MESSAGE);
			fnfEx.printStackTrace();
		} catch (NumberFormatException nfEx) {
			JOptionPane.showMessageDialog(this,  new String[] {"Error opening target list: Check target list format.", " ", nfEx.getMessage()}, "Error Opening Target List", JOptionPane.ERROR_MESSAGE);
			nfEx.printStackTrace();
		} catch (IOException ioEx) {
			JOptionPane.showMessageDialog(this,  new String[] {"Error opening target list: I/O error. ", " ", ioEx.getMessage()}, "Error Opening Target List", JOptionPane.ERROR_MESSAGE);
			ioEx.printStackTrace();
		} catch (TargetListFormatException tlfEx) {
			JOptionPane.showMessageDialog(this,  new String[] {"Error opening target list: Check target list format.", " ", tlfEx.getMessage()}, "Error Opening Target List", JOptionPane.ERROR_MESSAGE);
			tlfEx.printStackTrace();
		}
		
	}
	private Object[] constructWarningListDialogMessage(String headerMessage, ArrayList<String> warningList, String finalMessage) {
		warningListTextArea.setText("");
		for (String currentWarning : warningList) {
			warningListTextArea.append("  - "+currentWarning+"\n");
		}
		warningListTextArea.setCaretPosition(0);
		Object[] retval = {headerMessage, " " , warningListScrollPane, " ", finalMessage};
		return retval;
	}
	private void updateViewProcessErrorOutput(ArrayList<String> errorMessages) {
		if (!errorMessages.isEmpty()) {
			JOptionPane.showMessageDialog(this, constructWarningListDialogMessage("Error running script.  Output:", errorMessages, ""), "Error Executing Script", JOptionPane.ERROR_MESSAGE);
		}

	}
	//.//.//.//.//.//.//.//.//.//.//.//.//.//
	//. MSCGUI MVC Controller inner class  .//
	//.//.//.//.//.//.//.//.//.//.//.//.//.//
	public class MSCGUIController extends GenericController {
		
		public MSCGUIController(MSCGUIModel newMSCGUIModel) {
			super(newMSCGUIModel);
		}
		
		
		public void model_propertyChange(PropertyChangeEvent e) {
			if (e.getPropertyName().compareTo("activeRow") == 0) {
				updateViewActiveRow(((Integer)e.getNewValue()).intValue());
			}  else if (e.getPropertyName().compareTo("currentMascgenResult") == 0) {
					updateViewCurrentMascgenResult((MascgenResult)(e.getNewValue()));
			}  else if (e.getPropertyName().compareTo("loadedMaskSetup") == 0) {
				updateViewLoadedMaskSetup(e.getNewValue().toString());
			}  else if (e.getPropertyName().compareTo("currentSlitConfiguration") == 0) {
				updateViewCurrentSlitConfiguration((SlitConfiguration)(e.getNewValue()));
			}  else if (e.getPropertyName().compareTo("openedSlitConfigurations") == 0) {
				updateViewOpenedSlitConfigurations();
			}  else if (e.getPropertyName().compareTo("currentSlitConfigurationIndex") == 0) {
				updateViewCurrentSlitConfigurationIndex(((Integer)(e.getNewValue())).intValue());
			}  else if (e.getPropertyName().compareTo("mascgenStatus") == 0) {
				updateViewMascgenRunStatus(e.getNewValue().toString());
			}  else if (e.getPropertyName().compareTo("mascgenRunNumber") == 0) {
				updateViewMascgenRunNumber(((Integer)e.getNewValue()).intValue());
			}  else if (e.getPropertyName().compareTo("mascgenTotalRuns") == 0) {
				updateViewMascgenTotalRuns(((Integer)e.getNewValue()).intValue());
			}  else if (e.getPropertyName().compareTo("mascgenOptimalRunNumber") == 0) {
				updateViewMascgenOptimalRunNumber(((Integer)e.getNewValue()).intValue());
			}  else if (e.getPropertyName().compareTo("mascgenTotalPriority") == 0) {
				updateViewMascgenTotalPriority(((Double)e.getNewValue()).doubleValue());
			}  else if (e.getPropertyName().compareTo("mascgenArgumentException") == 0) {
				updateViewMascgenArgumentsException((MascgenArgumentException)e.getNewValue());
			}  else if (e.getPropertyName().compareTo("scriptRunning") == 0) {
				updateViewScriptRunning(((Boolean)e.getNewValue()).booleanValue());
			}  else if (e.getPropertyName().compareTo("currentSlitWidth") == 0) {
				updateViewCurrentSlitWidth(((Double)e.getNewValue()).doubleValue());
		  } else if (e.getPropertyName().compareTo("csuStatus") == 0) {
		  	updateViewCSUStatus(e.getNewValue().toString());
		  } else if (e.getPropertyName().compareTo("csuReady") == 0) {
		  	updateViewCSUReady(((Integer)(e.getNewValue())).intValue());
		  } else if (e.getPropertyName().compareTo("closeOffType") == 0) {
		  	updateViewCloseOffType(((Integer)(e.getNewValue())).intValue());
		  } else if (e.getPropertyName().compareTo("processErrorOutput") == 0) {
		  	updateViewProcessErrorOutput(((ArrayList<String>)(e.getNewValue())));

			}
		}


	} //. end controller inner class

	
}
