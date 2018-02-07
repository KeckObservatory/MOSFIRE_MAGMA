/* Copyright (c) 2012, Regents of the University of California
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for educational, research and non-profit purposes, without 
 * fee, and without a written agreement is hereby granted, provided that the 
 * above copyright notice, this paragraph and the following three paragraphs 
 * appear in all copies.
 * 
 * Permission to incorporate this software into commercial products may be 
 * obtained by contacting the University of California.
 * 
 *  Thomas J. Trappler, ASM
 *  Director, UCLA Software Licensing
 *  UCLA Office of Information Technology
 *  5611 Math Sciences
 *  Los Angeles, CA 90095-1557
 *  (310) 825-7516
 *  trappler@ats.ucla.edu
 *  
 *  This software program and documentation are copyrighted by The Regents of 
 *  the University of California. The software program and documentation are 
 *  supplied "as is", without any accompanying services from The Regents. The 
 *  Regents does not warrant that the operation of the program will be 
 *  uninterrupted or error-free. The end-user understands that the program was 
 *  developed for research purposes and is advised not to rely exclusively on 
 *  the program for any reason.
 *  
 *  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR 
 *  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING 
 *  LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS 
 *  DOCUMENTATION, EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE 
 *  POSSIBILITY OF SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY 
 *  DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE 
 *  SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF 
 *  CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 *  ENHANCEMENTS, OR MODIFICATIONS.
 */
package edu.ucla.astro.irlab.mosfire.mscgui;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
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
import javax.swing.LookAndFeel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.xml.transform.TransformerException;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
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
import java.awt.event.FocusListener;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import edu.hawaii.keck.kjava.KJavaException;
import edu.ucla.astro.irlab.util.FileUtilities;
import edu.ucla.astro.irlab.util.InvalidValueException;
import edu.ucla.astro.irlab.util.NoSuchPropertyException;
import edu.ucla.astro.irlab.util.NumberFormatters;
import edu.ucla.astro.irlab.util.ServerStatusPanel;
import edu.ucla.astro.irlab.util.gui.CellEditorsAndRenderers;
import edu.ucla.astro.irlab.util.gui.GradientButton;
import edu.ucla.astro.irlab.util.gui.OptionCheckBox;
import edu.ucla.astro.irlab.util.gui.GenericController;
import edu.ucla.astro.irlab.mosfire.util.*;

import nom.tam.fits.FitsException;

import org.apache.log4j.*;
import org.jdom.JDOMException;

//. DONE copying mask config should change mask name to new name in mascgen panel
//. DONE some thought needed about directories, re: users saving at home, and opening at keck.
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
/**
 * This is the view (GUI) class for MAGMA (MSCGUI). It contains code for the
 * 
 * @author Jason L. Weiss
 */
public class MSCGUIView extends JFrame implements ChangeListener {
	// . serialVersionUID created using serialver command 2006/07/11
	static final long serialVersionUID = -4737530193991680291L;

	private static final Logger logger = Logger.getLogger(MSCGUIView.class);

	private MSCGUIModel myModel;

	private JTabbedPane mascgenTabbedPane = new JTabbedPane();
	private MascgenOutputsPanel maskConfigOutputPanel = new MascgenOutputsPanel();
	private JPanel mascgenStatusPanel = new JPanel();

	private MascgenOutputsPanel mascgenOutputPanel = new MascgenOutputsPanel();

	/** The status bar. */
	private JLabel statusBar = new JLabel();

	/** The current mask name value label. */
	private JLabel currentMaskNameValueLabel = new JLabel("");
	/** The center value label. */
	private JLabel centerValueLabel = new JLabel("");
	/** The pa value label. */
	private JLabel paValueLabel = new JLabel("");
	/** The total priority value label. */
	private JLabel totalPriorityValueLabel = new JLabel("");
	/** The current slit width spinner. */
	private JSpinner currentSlitWidthSpinner = new JSpinner();

	// . opened configs panel
	// . these can get modified (shown or hidden) according to options
	private JScrollPane openedConfigsScrollPane = new JScrollPane();
	private JTable openedConfigsTable = new JTable();
	private JPanel openedConfigsPanel = new JPanel();
	private JPanel topConfigPanel = new JPanel(new GridLayout(1, 0));
	private JPanel bottomConfigPanel = new JPanel(new GridLayout(1, 0));
	private JLabel maskConfigurationsLabel = new JLabel("MASK CONFIGURATIONS");

	private JButton openConfigButton = new JButton("Open...");
	private JButton copyConfigButton = new JButton("Copy");
	private JButton saveConfigButton = new JButton("Save MSC...");
	private JButton saveAllConfigButton = new JButton("Save All...");
	private JButton closeConfigButton = new JButton("Close");

	private SlitConfigurationTableModel openedConfigsTableModel = new SlitConfigurationTableModel();

	// . spinners for defining a long slit
	private JSpinner longSlitWidthSpinner = new JSpinner();
	private JSpinner longSlitLengthSpinner = new JSpinner();

	private GradientButton setupAlignmentButton = new GradientButton(
			"Setup Alignment Mask");
	private GradientButton setupScienceButton = new GradientButton(
			"Setup Science Mask");
	private GradientButton executeMaskButton = new GradientButton(
			"Execute Mask");

	/** The loaded mask label. */
	private JLabel loadedMaskLabel = new JLabel();
	/** The csu ready value label. */
	private JLabel csuReadyValueLabel = new JLabel();
	/** The csu status value label. */
	private JLabel csuStatusValueLabel = new JLabel();

	/** The mira check box. */
	private JCheckBox miraCheckBox = new JCheckBox("MIRA?");

	/** The input object list value label. */
	private JLabel inputObjectListValueLabel = new JLabel("none");

	/** The use center of priority check box. */
	private JCheckBox useCenterOfPriorityCheckBox = new JCheckBox(
			"Use Center of Priority");

	/** MASCGEN Inputs Fields */
	private JTextField xRangeField = new JTextField();
	private JTextField xCenterField = new JTextField();
	private JTextField slitWidthField = new JTextField();
	private JTextField ditherSpaceField = new JTextField();
	private JTextField nodAmpField = new JTextField();
	private JTextField centerRaDecField = new JTextField();
	private JTextField xStepsField = new JTextField();
	private JTextField xStepSizeField = new JTextField();
	private JTextField yStepsField = new JTextField();
	private JTextField yStepSizeField = new JTextField();
	private JTextField centerPAField = new JTextField();
	private JTextField paStepsField = new JTextField();
	private JTextField paStepSizeField = new JTextField();
	private JTextField alignmentStarsField = new JTextField();
	private JTextField alignmentStarEdgeField = new JTextField();
	private JTextField maskNameField = new JTextField();

	// . mascgen status panel

	/** The mascgen status text area. */
	private JTextArea mascgenStatusTextArea = new JTextArea();

	/** The mascgen run number label. */
	private JLabel mascgenRunNumberLabel = new JLabel("");

	/** The mascgen total runs label. */
	private JLabel mascgenTotalRunsLabel = new JLabel("");

	/** The mascgen optimal run number label. */
	private JLabel mascgenOptimalRunNumberLabel = new JLabel("");

	/** The mascgen total priority label. */
	private JLabel mascgenTotalPriorityLabel = new JLabel("");

	/** The mascgen run number title. */
	private JLabel mascgenRunNumberTitle = new JLabel("Run number: ");

	/** The mascgen total runs title. */
	private JLabel mascgenTotalRunsTitle = new JLabel("Total number of runs: ");

	/** The mascgen total priority title. */
	private JLabel mascgenTotalPriorityTitle = new JLabel(
			"Highest Total priority: ");

	/** The mascgen optimal run number title. */
	private JLabel mascgenOptimalRunNumberTitle = new JLabel("Found on run: ");

	/** The mascgen abort button. */
	private JButton mascgenAbortButton = new JButton("ABORT");

	// . table
	/** The slit list table scroll pane. */
	private JScrollPane slitListTableScrollPane = new JScrollPane();

	/** The slit list table. */
	private JTable slitListTable = new JTable();

	/** The slit list table model. */
	private MechanicalSlitListTableModel slitListTableModel = new MechanicalSlitListTableModel(
			false);

	// . configuration panels
	/** The slit configuration panel. */
	private MaskVisualizationPanel slitConfigurationPanel = new MaskVisualizationPanel();

	// . target list panel
	/** The target list panel. */
	private JPanel targetListPanel = new JPanel();

	/** The target list table scroll pane. */
	private JScrollPane targetListTableScrollPane = new JScrollPane();

	/** The target list table. */
	private JTable targetListTable = new JTable();

	/** The target list table model. */
	private TargetListTableModel targetListTableModel = new TargetListTableModel();

	/** The target table sorter. */
	private TableRowSorter<TargetListTableModel> targetTableSorter;

	/** The status panel. */
	private JPanel statusPanel = new JPanel();

	/** The mosfire status panel. */
	private ServerStatusPanel mosfireStatusPanel;

	/** The mcsus status panel. */
	private ServerStatusPanel mcsusStatusPanel;

	/** The mds status panel. */
	private ServerStatusPanel mdsStatusPanel;

	/** The mascgen params fc. */
	private JFileChooser mascgenParamsFC = new JFileChooser();

	/** The object list fc. */
	private JFileChooser objectListFC = new JFileChooser();

	/** The open slit configuration fc. */
	private JFileChooser openSlitConfigurationFC = new JFileChooser();

	/** The save slit configuration fc. */
	private JFileChooser saveSlitConfigurationFC = new JFileChooser();

	/** The executed mask dir fc. */
	private JFileChooser executedMaskDirFC = new JFileChooser();

	/** The msc list fc. */
	private JFileChooser mscListFC = new JFileChooser();

	/** The warning list text area. */
	private JTextArea warningListTextArea = new JTextArea();

	/** The warning list scroll pane. */
	private JScrollPane warningListScrollPane = new JScrollPane();

	/** The calibration frame. */
	private CalibrationScriptFrame calibrationFrame;

	/** The align with above menu item. */
	private JMenuItem alignWithAboveMenuItem = new JMenuItem(
			"Align with slit above");

	/** The align with below menu item. */
	private JMenuItem alignWithBelowMenuItem = new JMenuItem(
			"Align with slit below");

	/** The move slit on target menu item. */
	private JMenuItem moveSlitOnTargetMenuItem = new JMenuItem(
			"Move slit(s) onto target");

	///////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M1 by Ji Man Sohn, UCLA 2016-2017  //
	/** Whole Field Offset in X direction */
	private JMenuItem slitNudgeMenuItem = new JMenuItem("Nudge the slit");
	///////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M3 by Ji Man Sohn, UCLA 2016-2017  //
	/** Whole Field Offset in X direction */
	private JMenuItem wholeFieldOffsetInXMenuItem = new JMenuItem("Offset whole field in x-direction");
	///////////////////////////////////////////////////////////////////

	/** The target info menu. */
	private JMenu targetInfoMenu = new JMenu("Target Info");

	/** The target info panel. */
	private AstroObjInfoPanel targetInfoPanel = new AstroObjInfoPanel();

	/** The mask configs popup. */
	private JPopupMenu maskConfigsPopup = new JPopupMenu();

	/** The popup open msc. */
	private JMenuItem popupOpenMSC = new JMenuItem("Open MSC...");

	/** The popup copy msc. */
	private JMenuItem popupCopyMSC = new JMenuItem("Copy MSC");

	/** The popup save msc. */
	private JMenuItem popupSaveMSC = new JMenuItem("Save MSC...");

	/** The popup save all. */
	private JMenuItem popupSaveAll = new JMenuItem(
			"Save All Configuration Products...");

	/** The popup close msc. */
	private JMenuItem popupCloseMSC = new JMenuItem("Close MSC");

	/** The open mask config popup. */
	private JPopupMenu openMaskConfigPopup = new JPopupMenu();

	/** The popup only open msc. */
	private JMenuItem popupOnlyOpenMSC = new JMenuItem("Open MSC...");

	/** The input object list full path. */
	private String inputObjectListFullPath = "";

	/** The duplicate mask name option check box. */
	private OptionCheckBox duplicateMaskNameOptionCheckBox;

	/** The min align stars option check box. */
	private OptionCheckBox minAlignStarsOptionCheckBox;

	/** The setup mask option check box. */
	private OptionCheckBox setupMaskOptionCheckBox;

	/** The warn mira option check box. */
	private OptionCheckBox warnMiraOptionCheckBox;

	/** The execute mask option check box. */
	private OptionCheckBox executeMaskOptionCheckBox;

	/** The execute different mask option check box. */
	private OptionCheckBox executeDifferentMaskOptionCheckBox;

	/** The write slit configuration html option check box. */
	private OptionCheckBox writeSlitConfigurationHTMLOptionCheckBox;

	/** The unused slits option check box. */
	private OptionCheckBox unusedSlitsOptionCheckBox;

	/** The invalid slit warning option check box. */
	private OptionCheckBox invalidSlitWarningOptionCheckBox;

	/** The reassign unused slits check box. */
	private JCheckBox reassignUnusedSlitsCheckBox = new JCheckBox(
			"Have MASCGEN join unassigned slits to highest priority neighbor?");

	/** The show mask config buttons check box. */
	private JCheckBox showMaskConfigButtonsCheckBox = new JCheckBox(
			"Show Mask Configuration Buttons?");

	// . panel for unused bar options
	/** The unused bar options panel. */
	private JPanel unusedBarOptionsPanel = new JPanel();

	/** The reassign method label. */
	private JLabel reassignMethodLabel = new JLabel(
			"Move unassigned slits as follows: ");

	/** The unused bar do nothing button. */
	private JRadioButton unusedBarDoNothingButton = new JRadioButton(
			"Do nothing.");

	/** The unused bar reduce width button. */
	private JRadioButton unusedBarReduceWidthButton = new JRadioButton(
			"Reduce Width.");

	/** The unused bar close off button. */
	private JRadioButton unusedBarCloseOffButton = new JRadioButton(
			"Move slit out of field of view.");

	/** The minimum reassign slit width label. */
	private JLabel minimumReassignSlitWidthLabel = new JLabel(
			"Apply to unassigned slits with widths greater than (arcsec): ");

	/** The minimum reassign slit width field. */
	private JSpinner minimumReassignSlitWidthField = new JSpinner();

	/** The reduced slit width label. */
	private JLabel reducedSlitWidthLabel = new JLabel(
			"Reduce slit width to (arcsec): ");

	/** The reduced slit width field. */
	private JSpinner reducedSlitWidthField = new JSpinner();

	/** The reassign method button group. */
	private ButtonGroup reassignMethodButtonGroup = new ButtonGroup();

	/** The default insets. */
	private Insets defaultInsets = new Insets(
			MSCGUIParameters.GUI_INSET_VERTICAL_GAP, 2,
			MSCGUIParameters.GUI_INSET_VERTICAL_GAP, 2);

	/** The show mask configuration buttons. */
	private boolean showMaskConfigurationButtons = true;

	/** The generate fits extensions panel. */
	private GenerateFitsExtensionsPanel generateFitsExtensionsPanel = new GenerateFitsExtensionsPanel();

	/** The my controller. */
	private MSCGUIController myController;

	/** The active target. */
	private AstroObj activeTarget;

	// Construct the frame
	/**
	 * Instantiates a new mSCGUI view.
	 * 
	 * @param newModel
	 *            the new model
	 * @throws Exception
	 *             the exception
	 */
	public MSCGUIView(MSCGUIModel newModel) throws Exception {
		myModel = newModel;
		showMaskConfigurationButtons = MSCGUIParameters.SHOW_MASK_CONFIGURATION_BUTTONS;
		if (MSCGUIParameters.ONLINE_MODE) {
			mosfireStatusPanel = new ServerStatusPanel("MOSFIRE", myModel
					.getMosfireLastAliveProperty(), true);
			mdsStatusPanel = new ServerStatusPanel("MDS", myModel
					.getMDSLastAliveProperty(), true);
			mcsusStatusPanel = new ServerStatusPanel("MCSUS", myModel
					.getMCSUSLastAliveProperty(), true);
		}
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		jbInit();
		myController = new MSCGUIController(myModel);
		updateView();
	}

	// Component initialization
	/**
	 * Jb init.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private void jbInit() throws Exception {

		SpinnerNumberModel minReassignSlitWidthSpinnerModel = new SpinnerNumberModel();
		minReassignSlitWidthSpinnerModel.setStepSize(0.1);
		minReassignSlitWidthSpinnerModel
				.setMinimum(MosfireParameters.MINIMUM_SLIT_WIDTH);
		minReassignSlitWidthSpinnerModel
				.setMaximum(MosfireParameters.MAXIMUM_SLIT_WIDTH);
		minReassignSlitWidthSpinnerModel.setValue(myModel
				.getMinimumCloseOffSlitWidth());
		minimumReassignSlitWidthField
				.setModel(minReassignSlitWidthSpinnerModel);

		SpinnerNumberModel reducedSlitWidthSpinnerModel = new SpinnerNumberModel();
		reducedSlitWidthSpinnerModel.setStepSize(0.1);
		reducedSlitWidthSpinnerModel
				.setMinimum(MosfireParameters.MINIMUM_SLIT_WIDTH);
		reducedSlitWidthSpinnerModel
				.setMaximum(MosfireParameters.MAXIMUM_SLIT_WIDTH);
		reducedSlitWidthSpinnerModel.setValue(myModel.getClosedOffSlitWidth());
		reducedSlitWidthField.setModel(reducedSlitWidthSpinnerModel);

		unusedBarOptionsPanel.setBorder(BorderFactory
				.createTitledBorder("Unassigned Bar Options"));
		unusedBarOptionsPanel.setLayout(new GridBagLayout());
		// unusedBarOptionsPanel.add(maximumSlitLengthLabel, new
		// GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
		// GridBagConstraints.NONE, defaultInsets, 0, 0));
		// unusedBarOptionsPanel.add(maximumSlitLengthField, new
		// GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
		// GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(reassignMethodLabel, new GridBagConstraints(
				0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(unusedBarDoNothingButton,
				new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(unusedBarReduceWidthButton,
				new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(unusedBarCloseOffButton,
				new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(minimumReassignSlitWidthLabel,
				new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(minimumReassignSlitWidthField,
				new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(reducedSlitWidthLabel,
				new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						defaultInsets, 0, 0));
		unusedBarOptionsPanel.add(reducedSlitWidthField,
				new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						defaultInsets, 0, 0));
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

		duplicateMaskNameOptionCheckBox = new OptionCheckBox(
				"Continue to show this warning?",
				"Warn before replacing existing mask with duplicate name?");
		duplicateMaskNameOptionCheckBox
				.setSelected(MSCGUIParameters.SHOW_WARNING_DUPLICATE_MASK_NAME);
		duplicateMaskNameOptionCheckBox
				.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_DUPLICATE_MASK_NAME_WARNING);

		minAlignStarsOptionCheckBox = new OptionCheckBox(
				"Continue to show this warning?",
				"Warn if number of alignment stars is less than recommended minimum?");
		minAlignStarsOptionCheckBox
				.setSelected(MSCGUIParameters.SHOW_WARNING_MINIMUM_ALIGN_STARS);
		minAlignStarsOptionCheckBox
				.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_MINIMUM_ALIGN_STARS);

		invalidSlitWarningOptionCheckBox = new OptionCheckBox(
				"Continue to warn about saving masks with invalid slits?",
				"Warn about saving masks with invalid slits?");
		invalidSlitWarningOptionCheckBox
				.setSelected(MSCGUIParameters.SHOW_WARNING_SAVE_INVALID_SLITS);
		invalidSlitWarningOptionCheckBox
				.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_SAVE_INVALID_SLITS);

		setupMaskOptionCheckBox = new OptionCheckBox(
				"Continue to confirm mask setup?", "Confirm mask setup?");
		setupMaskOptionCheckBox
				.setSelected(MSCGUIParameters.SHOW_WARNING_SETUP_MASK);
		setupMaskOptionCheckBox
				.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_SETUP_MASK);

		warnMiraOptionCheckBox = new OptionCheckBox(
				"Continue to warn if MIRA is selected?",
				"Warn if MIRA is selected?");
		warnMiraOptionCheckBox.setSelected(MSCGUIParameters.SHOW_WARNING_MIRA);
		warnMiraOptionCheckBox
				.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_WARN_MIRA);

		executeMaskOptionCheckBox = new OptionCheckBox(
				"Continue to confirm mask execution?",
				"Confirm mask execution?");
		executeMaskOptionCheckBox
				.setSelected(MSCGUIParameters.SHOW_WARNING_EXECUTE_MASK);
		executeMaskOptionCheckBox
				.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_EXECUTE_MASK);

		executeDifferentMaskOptionCheckBox = new OptionCheckBox(
				"Continue to show this warning?",
				"Warn before executing mask set up mask is different than displayed mask?");
		executeDifferentMaskOptionCheckBox
				.setSelected(MSCGUIParameters.SHOW_WARNING_EXECUTE_DIFFERENT_MASK);
		executeDifferentMaskOptionCheckBox
				.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_EXECUTE_DIFFERENT_MASK);

		writeSlitConfigurationHTMLOptionCheckBox = new OptionCheckBox(
				"Continue to try to write HTML files?",
				"Write HTML file when saving slit configuration?");
		writeSlitConfigurationHTMLOptionCheckBox
				.setSelected(MSCGUIParameters.SHOW_WARNING_WRITE_MSC_HTML);
		writeSlitConfigurationHTMLOptionCheckBox
				.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_WRITE_MSC_HTML);

		unusedSlitsOptionCheckBox = new OptionCheckBox(
				"Continue to warn about unassigned slits?",
				"Warn about unassigned slits before mask setup?");
		unusedSlitsOptionCheckBox
				.setSelected(MSCGUIParameters.SHOW_WARNING_UNUSED_SLITS);
		unusedSlitsOptionCheckBox
				.setDefaultAnswer(MSCGUIParameters.DEFAULT_ANSWER_UNUSED_SLITS);

		showMaskConfigButtonsCheckBox
				.setSelected(MSCGUIParameters.SHOW_MASK_CONFIGURATION_BUTTONS);
		reassignUnusedSlitsCheckBox.setSelected(myModel
				.isMascgenReassignUnusedSlits());

		warningListScrollPane.getViewport().add(warningListTextArea);
		warningListScrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		warningListScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		warningListScrollPane.setPreferredSize(new Dimension(300, 300));
		warningListTextArea.setLineWrap(true);
		warningListTextArea.setWrapStyleWord(true);
		warningListTextArea.setEditable(false);

		JPanel contentPane;
		// setIconImage(Toolkit.getDefaultToolkit().createImage(MSCGUIApplication.class.getResource("[Your Icon]")));
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		this.setSize(MSCGUIParameters.DIM_MAINFRAME);
		this.setTitle(MSCGUIParameters.GUI_TITLE
				+ (MSCGUIParameters.ENGINEERING_MODE ? " (Engineering Mode)"
						: "") + " " + MSCGUIParameters.MSC_VERSION);
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
		moveSlitOnTargetMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveSlitOnTarget_actionPerformed();
			}
		});
		
		///////////////////////////////////////////////////////////////////
		// Part of MAGMA UPGRADE item M1 by Ji Man Sohn, UCLA 2016-2017  //
		slitNudgeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				slitNudge_actionPerformed();
			}
		});
		///////////////////////////////////////////////////////////////////
		
		///////////////////////////////////////////////////////////////////
		// Part of MAGMA UPGRADE item M3 by Ji Man Sohn, UCLA 2016-2017  //
		wholeFieldOffsetInXMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				wholeFieldOffsetInX_actionPerformed();
			}
		});
		///////////////////////////////////////////////////////////////////


		targetInfoMenu.add(targetInfoPanel);
		popupOpenMSC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileOpenMSC_actionPerformed(e);
			}
		});
		popupOnlyOpenMSC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileOpenMSC_actionPerformed(e);
			}
		});
		popupCopyMSC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileCopyMSC_actionPerformed(e);
			}
		});
		popupSaveMSC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSaveMSC_actionPerformed(e);
			}
		});
		popupSaveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSaveAll_actionPerformed(e);
			}
		});
		popupCloseMSC.addActionListener(new ActionListener() {
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

		// . Menu
		JMenuBar mainMenuBar = new JMenuBar();
		JMenu jMenuFile = new JMenu("File");
		JMenuItem jMenuFileOpenTargetList = new JMenuItem("Open Target List...");
		JMenuItem jMenuFileOpenMSC = new JMenuItem("Open MSC...");
		JMenuItem jMenuFileCopyMSC = new JMenuItem("Copy MSC");
		JMenuItem jMenuFileSaveMSC = new JMenuItem("Save MSC...");
		JMenuItem jMenuFileSaveAll = new JMenuItem(
				"Save All Configuration Products...");
		JMenuItem jMenuFileGenerateExtensions = new JMenuItem(
				"Generate FITS Extensions...");
		JMenuItem jMenuFileCloseMSC = new JMenuItem("Close MSC");
		JMenuItem jMenuFileSaveMSCList = new JMenuItem("Save MSC List...");
		JMenuItem jMenuFileOpenMSCList = new JMenuItem("Open MSC List...");
		JMenuItem jMenuFileSetExecutedMaskDir = new JMenuItem(
				"Set Executed Mask Directory...");
		JMenuItem jMenuFileExit = new JMenuItem("Exit");
		JMenu jMenuTools = new JMenu("Tools");
		JMenuItem jMenuToolsCalibration = new JMenuItem("Calibration Tool...");
		JMenuItem jMenuToolsCustomize = new JMenuItem("Customize...");
		JMenuItem jMenuToolsOptions = new JMenuItem("Options...");
		JMenu jMenuHelp = new JMenu("Help");
		JMenuItem jMenuHelpOnline = new JMenuItem("Online Help...");
		JMenuItem jMenuHelpAbout = new JMenuItem("About...");

		// . Find HelpSet file can create HelpSet object
		boolean helpSetAvailable = false;
		ClassLoader cl = MSCGUIView.class.getClassLoader();
		URL hsURL = HelpSet.findHelpSet(cl,
				MSCGUIParameters.MSCGUI_HELPSET_NAME);
		try {
			HelpSet hs = new HelpSet(null, hsURL);
			HelpBroker hb = hs.createHelpBroker();
			jMenuHelpOnline
					.addActionListener(new CSH.DisplayHelpFromSource(hb));
			helpSetAvailable = true;
		} catch (HelpSetException ex) {
			JOptionPane
					.showMessageDialog(
							this,
							"Warning: Help files not found. Online help not available.",
							"Error Loading Help Set",
							JOptionPane.WARNING_MESSAGE);
			ex.printStackTrace();
		}

		jMenuFileOpenTargetList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileOpenTargetList_actionPerformed(e);
			}
		});
		jMenuFileOpenMSC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileOpenMSC_actionPerformed(e);
			}
		});
		jMenuFileCopyMSC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileCopyMSC_actionPerformed(e);
			}
		});
		jMenuFileSaveMSC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSaveMSC_actionPerformed(e);
			}
		});
		jMenuFileSaveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSaveAll_actionPerformed(e);
			}
		});
		jMenuFileGenerateExtensions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileGenerateExtensions_actionPerformed(e);
			}
		});
		jMenuFileCloseMSC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileCloseMSC_actionPerformed(e);
			}
		});
		jMenuFileOpenMSCList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileOpenMSCList_actionPerformed(e);
			}
		});
		jMenuFileSaveMSCList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSaveMSCList_actionPerformed(e);
			}
		});
		jMenuFileSetExecutedMaskDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSetExecutedMaskDir_actionPerformed(e);
			}
		});
		jMenuFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileExit_actionPerformed(e);
			}
		});
		jMenuHelpAbout.addActionListener(new ActionListener() {
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

		// . Assemble Main Base and Tabs
		// jMenuFile.add(jMenuFileOpenTargetList);
		jMenuFile.add(jMenuFileOpenMSC);
		jMenuFile.add(jMenuFileCopyMSC);
		jMenuFile.add(jMenuFileSaveMSC);
		jMenuFile.add(jMenuFileSaveAll);
		jMenuFile.add(jMenuFileGenerateExtensions);
		jMenuFile.add(jMenuFileCloseMSC);
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileOpenMSCList);
		jMenuFile.add(jMenuFileSaveMSCList);
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

		calibrationFrame = new CalibrationScriptFrame(this);
		calibrationFrame.setSize(MSCGUIParameters.DIM_CALIBRATION_GUI);

		mascgenParamsFC
				.setCurrentDirectory(MSCGUIParameters.DEFAULT_MASCGEN_PARAMS_DIRECTORY);
		mascgenParamsFC.setFileFilter(new FileUtilities.StandardFileFilter(
				new String[] { "param", "params" }, "MASCGEN Parameter files"));

		openSlitConfigurationFC
				.setCurrentDirectory(MSCGUIParameters.DEFAULT_MASK_CONFIGURATION_ROOT_DIRECTORY);
		openSlitConfigurationFC
				.setFileFilter(new FileUtilities.StandardFileFilter("xml",
						"Slit Configuration Files"));
		saveSlitConfigurationFC
				.setCurrentDirectory(MSCGUIParameters.DEFAULT_MASK_CONFIGURATION_ROOT_DIRECTORY);
		saveSlitConfigurationFC
				.setFileFilter(new FileUtilities.StandardFileFilter("xml",
						"Slit Configuration Files"));
		executedMaskDirFC.setCurrentDirectory(myModel.getScriptDirectory());
		executedMaskDirFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		mscListFC
				.setCurrentDirectory(MSCGUIParameters.DEFAULT_MASK_CONFIGURATION_ROOT_DIRECTORY);
		mscListFC.setSelectedFile(new File("myMasks."
				+ MSCGUIParameters.MSC_LIST_EXTENSTION));
		mscListFC.setFileFilter(new FileUtilities.StandardFileFilter(
				MSCGUIParameters.MSC_LIST_EXTENSTION,
				"Slit Configuration List Files"));

		objectListFC.setDialogTitle("Open Target List");
		objectListFC.setDialogType(JFileChooser.OPEN_DIALOG);
		logger.trace("Default target list directory = "
				+ MSCGUIParameters.DEFAULT_TARGET_LIST_DIRECTORY.toString());
		objectListFC
				.setCurrentDirectory(MSCGUIParameters.DEFAULT_TARGET_LIST_DIRECTORY);
		objectListFC.setFileFilter(new FileUtilities.StandardFileFilter(
				new String[] { "coords", "txt" }, "Target Lists"));

		JButton loadMascgenParamsButton = new JButton("Load Parameters...");
		loadMascgenParamsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadMascgenParamsButton_actionPerformed(e);
			}
		});
		JButton saveMascgenParamsButton = new JButton("Save Parameters...");
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

		// inputObjectListValueLabel.setPreferredSize(new Dimension(150,20));
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
		mascgenOutputPanel
				.updateOutputParams(MSCGUIParameters.DEFAULT_MASK_NAME);
		String outputRootDirString = MSCGUIParameters.DEFAULT_MASCGEN_OUTPUT_ROOT_DIRECTORY
				.toString();
		logger.trace("Output Root Dir = " + outputRootDirString);
		outputRootDirString = MSCGUIParameters.DEFAULT_MASCGEN_OUTPUT_ROOT_DIRECTORY
				.getCanonicalPath();
		logger.trace("Output Root Dir Canonical = " + outputRootDirString);
		mascgenOutputPanel.setOutputRootDir(outputRootDirString);

		JButton runMascgenButton = new JButton("Run");
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
		// targetListTableScrollPane.setPreferredSize(MSCGUIParameters.DIM_TABLE_TARGET_LIST);
		targetListTableModel.setData(myModel.getTargetList());
		targetListTable.setModel(targetListTableModel);
		targetListTable.getColumnModel().getColumn(0).setCellRenderer(
				new TargetListTableCellRenderer());
		targetListTable.getColumnModel().getColumn(0).setMaxWidth(40);
		targetListTable.getColumnModel().getColumn(1).setCellRenderer(
				new TargetListTableCellRenderer());
		targetListTable.getColumnModel().getColumn(2).setCellRenderer(
				new TargetListTableCellRenderer());
		targetListTable.getColumnModel().getColumn(3).setCellRenderer(
				new TargetListTableCellRenderer());
		targetListTable.getColumnModel().getColumn(4).setCellRenderer(
				new TargetListTableCellRenderer());
		targetListTable.getColumnModel().getColumn(5).setCellRenderer(
				new TargetListTableCellRenderer());
		targetListTable.getColumnModel().getColumn(6).setCellRenderer(
				new TargetListTableCellRenderer());

		targetListTable.getModel().addTableModelListener(
				new TableModelListener() {
					public void tableChanged(TableModelEvent tmEv) {
						targetListTableModelChanged(tmEv);
					}
				});
		targetListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		targetListTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent lsEv) {
						targetList_tableSelectionChanged(lsEv);
					}
				});
		targetTableSorter = new TableRowSorter<TargetListTableModel>(
				targetListTableModel);
		targetListTable.setRowSorter(targetTableSorter);

		JLabel currentMaskNameLabel = new JLabel("Mask Name:");
		JLabel centerLabel = new JLabel("Center:");
		JLabel paLabel = new JLabel("PA:");
		JLabel paUnitsLabel = new JLabel("degrees");
		JLabel totalPriorityLabel = new JLabel("Total Priority:");
		currentMaskNameLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_LABEL);
		currentMaskNameValueLabel
				.setFont(MSCGUIParameters.FONT_MASK_CONFIG_VALUE_NAME);
		currentMaskNameValueLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		centerLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_LABEL);
		centerValueLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_VALUE);
		paLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_LABEL);
		paValueLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_VALUE);
		paUnitsLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_VALUE);
		totalPriorityLabel.setFont(MSCGUIParameters.FONT_MASK_CONFIG_LABEL);
		totalPriorityValueLabel
				.setFont(MSCGUIParameters.FONT_MASK_CONFIG_VALUE);
		paValueLabel.setPreferredSize(new Dimension(50, 20));
		// centerValueLabel.setPreferredSize(new Dimension(250, 20));

		JPanel slitWidthPanel = new JPanel();
		slitWidthPanel.setBorder(BorderFactory.createEtchedBorder());
		SpinnerNumberModel currentSpinnerModel = new SpinnerNumberModel();
		currentSpinnerModel.setStepSize(0.01);
		currentSpinnerModel.setMinimum(0.01);
		currentSpinnerModel.setMaximum(10.0);
		currentSpinnerModel.setValue(0.1);
		currentSlitWidthSpinner.setModel(currentSpinnerModel);
		LookAndFeel lf = UIManager.getLookAndFeel();
		logger.info("look and feel id=" + lf.getID() + ", name=" + lf.getName()
				+ ", desc=" + lf.getDescription() + ".");
		JButton currentSlitWidthIncrementButton = new JButton("+");
		JButton currentSlitWidthDecrementButton = new JButton("-");
		if (lf.getID().equals("Aqua")) {
			currentSlitWidthIncrementButton.putClientProperty(
					"JButton.buttonType", "square");
			currentSlitWidthDecrementButton.putClientProperty(
					"JButton.buttonType", "square");
		}
		currentSlitWidthIncrementButton.setMargin(new Insets(0, 0, 0, 0));
		currentSlitWidthDecrementButton.setMargin(new Insets(0, 0, 0, 0));

		slitWidthPanel.setLayout(new GridBagLayout());
		currentSlitWidthSpinner.setPreferredSize(new Dimension(70, 20));

		JLabel currentSlitWidthLabel = new JLabel("Width: ");
		slitWidthPanel.add(currentSlitWidthLabel, new GridBagConstraints(0, 0,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		slitWidthPanel.add(currentSlitWidthSpinner, new GridBagConstraints(1,
				0, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		slitWidthPanel.add(currentSlitWidthIncrementButton,
				new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						defaultInsets, 0, 0));
		currentSlitWidthIncrementButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentSlitWidthSpinner_actionPerformed(e, 1);
			}
		});
		slitWidthPanel.add(currentSlitWidthDecrementButton,
				new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						defaultInsets, 0, 0));
		currentSlitWidthDecrementButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentSlitWidthSpinner_actionPerformed(e, -1);
			}
		});

		int row;

		JPanel maskConfigPanel = new JPanel();
		maskConfigPanel.setLayout(new GridBagLayout());
		maskConfigPanel.add(currentMaskNameLabel, new GridBagConstraints(0, 0,
				1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		maskConfigPanel.add(currentMaskNameValueLabel, new GridBagConstraints(
				1, 0, 1, 1, 30.0, 0.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		maskConfigPanel.add(centerLabel, new GridBagConstraints(0, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		maskConfigPanel.add(centerValueLabel, new GridBagConstraints(1, 1, 1,
				1, 30.0, 0.0, GridBagConstraints.SOUTHWEST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		maskConfigPanel.add(paLabel, new GridBagConstraints(2, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		maskConfigPanel.add(paValueLabel, new GridBagConstraints(3, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		maskConfigPanel.add(paUnitsLabel, new GridBagConstraints(4, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.SOUTHWEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		maskConfigPanel.add(totalPriorityLabel, new GridBagConstraints(2, 0, 2,
				1, 0.0, 0.0, GridBagConstraints.SOUTHEAST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		maskConfigPanel.add(totalPriorityValueLabel, new GridBagConstraints(4,
				0, 1, 1, 10.0, 0.0, GridBagConstraints.SOUTHWEST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));

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
		openedConfigsTable
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
		openedConfigsTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent lsEv) {
						openedConfigsTable_tableSelectionChanged(lsEv);
					}
				});
		openedConfigsTable.getModel().addTableModelListener(
				new TableModelListener() {
					public void tableChanged(TableModelEvent tmEv) {
						slitConfigurationTableModelChanged(tmEv);
					}
				});
		openedConfigsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		TableColumn statusColumn = openedConfigsTable.getColumnModel()
				.getColumn(0);
		statusColumn.setMaxWidth(MSCGUIParameters.WIDTH_MASK_CONFIG_STATUS);
		statusColumn
				.setCellRenderer(new CellEditorsAndRenderers.CenteredTextTableCellRenderer());
		openedConfigsScrollPane.getViewport().add(openedConfigsTable);

		openedConfigsPanel
				.setBackground(MSCGUIParameters.COLOR_OPENED_MASKS_PANEL);
		openedConfigsPanel.setBorder(BorderFactory.createEtchedBorder());
		maskConfigurationsLabel.setFont(MSCGUIParameters.FONT_MASCGEN_TITLE);
		maskConfigurationsLabel.setHorizontalAlignment(SwingConstants.CENTER);

		openedConfigsPanel.setLayout(new GridBagLayout());
		topConfigPanel.setBackground(MSCGUIParameters.COLOR_OPENED_MASKS_PANEL);
		bottomConfigPanel
				.setBackground(MSCGUIParameters.COLOR_OPENED_MASKS_PANEL);
		topConfigPanel.add(openConfigButton);
		topConfigPanel.add(copyConfigButton);
		topConfigPanel.add(closeConfigButton);
		bottomConfigPanel.add(saveConfigButton);
		bottomConfigPanel.add(saveAllConfigButton);
		row = 0;
		openedConfigsPanel.add(maskConfigurationsLabel, new GridBagConstraints(
				0, row, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		if (showMaskConfigurationButtons) {
			row++;
			openedConfigsPanel.add(topConfigPanel, new GridBagConstraints(0,
					row, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		}
		row++;
		openedConfigsPanel.add(openedConfigsScrollPane, new GridBagConstraints(
				0, row, 1, 1, 10.0, 10.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, defaultInsets, 0, 0));
		if (showMaskConfigurationButtons) {
			row++;
			openedConfigsPanel.add(bottomConfigPanel, new GridBagConstraints(0,
					row, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		}

		JLabel longSlitLabel = new JLabel("LONGSLIT");
		JLabel longSlitWidthLabel = new JLabel("Slit width: ");
		JLabel longSlitWidthUnits = new JLabel("arcsec");
		JLabel longSlitLengthLabel = new JLabel("Slit length: ");
		JLabel longSlitLengthUnits = new JLabel("arcsec");
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
		spinnerModel.setStepSize(0.1);
		spinnerModel.setMinimum(MosfireParameters.MINIMUM_SLIT_WIDTH);
		spinnerModel.setMaximum(MosfireParameters.MAXIMUM_SLIT_WIDTH);
		spinnerModel.setValue(MosfireParameters.DEFAULT_SLIT_WIDTH);
		longSlitWidthSpinner.setModel(spinnerModel);
		SpinnerNumberModel lengthSpinnerModel = new SpinnerNumberModel();
		lengthSpinnerModel.setStepSize(MosfireParameters.CSU_ROW_HEIGHT);
		lengthSpinnerModel.setMinimum(MosfireParameters.SINGLE_SLIT_HEIGHT);
		lengthSpinnerModel.setMaximum(MosfireParameters.CSU_HEIGHT);
		lengthSpinnerModel
				.setValue(MosfireParameters.CSU_DEFAULT_MAXIMUM_SLIT_LENGTH_IN_ROWS
						* MosfireParameters.CSU_ROW_HEIGHT);
		longSlitLengthSpinner.setModel(lengthSpinnerModel);
		longSlitLabel.setHorizontalAlignment(JLabel.CENTER);
		longSlitLabel.setFont(MSCGUIParameters.FONT_MASCGEN_TITLE);

		JButton openLongSlitButton = new JButton("Create Longslit");
		openLongSlitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openLongSlitButton_actionPerformed(e);
			}
		});

		JPanel longSlitPanel = new JPanel();
		JPanel longSlitTopPanel = new JPanel();
		JPanel longSlitCenterPanel = new JPanel();
		JPanel longSlitBottomPanel = new JPanel();
		longSlitPanel.setBackground(MSCGUIParameters.COLOR_LONG_SLIT_PANEL);
		longSlitTopPanel.setBackground(MSCGUIParameters.COLOR_LONG_SLIT_PANEL);
		longSlitCenterPanel
				.setBackground(MSCGUIParameters.COLOR_LONG_SLIT_PANEL);
		longSlitBottomPanel
				.setBackground(MSCGUIParameters.COLOR_LONG_SLIT_PANEL);
		longSlitPanel.setBorder(BorderFactory.createEtchedBorder());
		longSlitPanel.setLayout(new BorderLayout(5, 5));
		longSlitTopPanel.setLayout(new BorderLayout());
		longSlitCenterPanel.setLayout(new BorderLayout(5, 5));
		longSlitBottomPanel.setLayout(new BorderLayout());
		longSlitTopPanel.add(longSlitLengthLabel, BorderLayout.WEST);
		longSlitTopPanel.add(longSlitLengthSpinner, BorderLayout.CENTER);
		longSlitTopPanel.add(longSlitLengthUnits, BorderLayout.EAST);
		longSlitBottomPanel.add(longSlitWidthLabel, BorderLayout.WEST);
		longSlitBottomPanel.add(longSlitWidthSpinner, BorderLayout.CENTER);
		longSlitBottomPanel.add(longSlitWidthUnits, BorderLayout.EAST);
		longSlitCenterPanel.add(longSlitTopPanel, BorderLayout.NORTH);
		longSlitCenterPanel.add(longSlitBottomPanel, BorderLayout.SOUTH);
		longSlitPanel.add(longSlitLabel, BorderLayout.NORTH);
		longSlitPanel.add(longSlitCenterPanel, BorderLayout.CENTER);
		longSlitPanel.add(openLongSlitButton, BorderLayout.SOUTH);

		JLabel openMaskLabel = new JLabel("OPEN MASK");
		JButton openMaskButton = new JButton("Open Mask");
		openMaskLabel.setHorizontalAlignment(JLabel.CENTER);
		openMaskLabel.setFont(MSCGUIParameters.FONT_MASCGEN_TITLE);
		openMaskButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openMaskButton_actionPerformed(e);
			}
		});

		JPanel openMaskPanel = new JPanel();
		openMaskPanel.setBackground(MSCGUIParameters.COLOR_OPEN_MASK_PANEL);
		openMaskPanel.setBorder(BorderFactory.createEtchedBorder());
		openMaskPanel.setLayout(new BorderLayout(5, 5));
		openMaskPanel.add(openMaskLabel, BorderLayout.NORTH);
		openMaskPanel.add(openMaskButton, BorderLayout.SOUTH);

		JSplitPane topSplitPane = new JSplitPane();
		topSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		// topSplitPane.setContinuousLayout(true);
		topSplitPane.setDividerLocation(MSCGUIParameters.DIM_MAINFRAME.width
				- MSCGUIParameters.WIDTH_MASCGEN_PANEL);
		// topSplitPane.setDividerLocation(topSplitPane.getWidth() -
		// topSplitPane.getInsets().right - topSplitPane.getDividerSize() -
		// MSCGUIParameters.WIDTH_MASCGEN_PANEL);

		JSplitPane objectsSplitPane = new JSplitPane();
		objectsSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		objectsSplitPane
				.setDividerLocation(MSCGUIParameters.DIM_MAINFRAME.height
						- MSCGUIParameters.DIM_TABLE_TARGET_LIST.height);

		JPanel rightPanel = new JPanel();
		rightPanel.setMinimumSize(new Dimension(0, 0));
		objectsSplitPane.setMinimumSize(new Dimension(0, 0));
		slitConfigurationPanel.setMinimumSize(new Dimension(0, 0));

		JPanel mascgenInputPanel = new JPanel();
		mascgenTabbedPane.add("Inputs", mascgenInputPanel);
		mascgenTabbedPane.add("Outputs", mascgenOutputPanel);
		mascgenTabbedPane.add("Status", mascgenStatusPanel);

		JPanel mascgenParamsButtonPanel = new JPanel();
		mascgenParamsButtonPanel.setLayout(new GridLayout(1, 0));
		mascgenParamsButtonPanel.add(loadMascgenParamsButton);
		mascgenParamsButtonPanel.add(saveMascgenParamsButton);

		row = 0;

		JLabel inputObjectListLabel = new JLabel("Input Object List: ");
		JButton inputObjectListBrowseButton = new JButton(
				"Select Target List...");
		inputObjectListBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inputObjectListBrowseButton_actionPerformed();
			}
		});

		ditherSpaceField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				updateNodAmpField();

			}

			@Override
			public void focusGained(FocusEvent e) {
				// . no action.
			}
		});
		nodAmpField.setEditable(false);
		nodAmpField.setToolTipText(MSCGUIParameters.NOD_AMP_FIELD_TOOLTIP);

		JLabel xRangeLabel = new JLabel("X Range:");
		JLabel xRangeUnitsLabel = new JLabel("arcmin");
		JLabel xCenterLabel = new JLabel("X Center:");
		JLabel xCenterUnitsLabel = new JLabel("arcmin");
		JLabel slitWidthLabel = new JLabel("Slit Width:");
		JLabel slitWidthUnitsLabel = new JLabel("arcsec");
		JLabel ditherSpaceLabel = new JLabel("Dither Space:");
		JLabel ditherSpaceUnitsLabel = new JLabel("arcsec");
		JLabel nodAmpLabel = new JLabel("Nod Amp:");
		JLabel centerRaDecLabel = new JLabel("Center Ra/Dec:");
		JLabel centerRaDecUnitsLabel = new JLabel("h m s \u00b0 \' \"");
		JLabel xStepsLabel = new JLabel("X Steps:");
		JLabel xStepsUnitsLabel = new JLabel("");
		JLabel xStepSizeLabel = new JLabel("X Step Size:");
		JLabel xStepSizeUnitsLabel = new JLabel("arcsec");
		JLabel yStepsLabel = new JLabel("Y Steps:");
		JLabel yStepsUnitsLabel = new JLabel("");
		JLabel yStepSizeLabel = new JLabel("Y Step Size:");
		JLabel yStepSizeUnitsLabel = new JLabel("arcsec");
		JLabel centerPALabel = new JLabel("Center PA:");
		JLabel centerPAUnitsLabel = new JLabel("degrees");
		JLabel paStepsLabel = new JLabel("PA Steps:");
		JLabel paStepsUnitsLabel = new JLabel("");
		JLabel paStepSizeLabel = new JLabel("PA Step Size:");
		JLabel paStepSizeUnitsLabel = new JLabel("degrees");
		JLabel alignmentStarsLabel = new JLabel("Alignment Stars:");
		JLabel alignmentStarsUnitsLabel = new JLabel("");
		JLabel alignmentStarEdgeLabel = new JLabel("Star Edge Buffer:");
		JLabel alignmentStarEdgeUnitsLabel = new JLabel("arcsec");
		JLabel maskNameLabel = new JLabel("Mask Name:");
		JLabel maskNameUnitsLabel = new JLabel("");
		mascgenInputPanel.setLayout(new GridBagLayout());

		mascgenInputPanel.add(inputObjectListBrowseButton,
				new GridBagConstraints(0, row, 5, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(inputObjectListLabel, new GridBagConstraints(0,
				row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		mascgenInputPanel.add(inputObjectListValueLabel,
				new GridBagConstraints(1, row, 4, 1, 1.0, 0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(xRangeLabel, new GridBagConstraints(0, row, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		mascgenInputPanel.add(xRangeField, new GridBagConstraints(1, row, 3, 1,
				1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(xRangeUnitsLabel, new GridBagConstraints(4, row,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(xCenterLabel, new GridBagConstraints(0, row, 1,
				1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		mascgenInputPanel.add(xCenterField, new GridBagConstraints(1, row, 3,
				1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(xCenterUnitsLabel, new GridBagConstraints(4, row,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(slitWidthLabel, new GridBagConstraints(0, row, 1,
				1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		mascgenInputPanel.add(slitWidthField, new GridBagConstraints(1, row, 3,
				1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(slitWidthUnitsLabel, new GridBagConstraints(4,
				row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(ditherSpaceLabel, new GridBagConstraints(0, row,
				1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		mascgenInputPanel.add(ditherSpaceField, new GridBagConstraints(1, row,
				1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		// mascgenInputPanel.add(ditherSpaceUnitsLabel, new
		// GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
		// GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenInputPanel.add(nodAmpLabel, new GridBagConstraints(2, row, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(MSCGUIParameters.GUI_INSET_VERTICAL_GAP, 10,
						MSCGUIParameters.GUI_INSET_VERTICAL_GAP, 1), 0, 0));
		mascgenInputPanel.add(nodAmpField, new GridBagConstraints(3, row, 1, 1,
				1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(ditherSpaceUnitsLabel, new GridBagConstraints(4,
				row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(alignmentStarsLabel, new GridBagConstraints(0,
				row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		mascgenInputPanel.add(alignmentStarsField, new GridBagConstraints(1,
				row, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(alignmentStarsUnitsLabel, new GridBagConstraints(
				4, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(alignmentStarEdgeLabel, new GridBagConstraints(0,
				row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		mascgenInputPanel.add(alignmentStarEdgeField, new GridBagConstraints(1,
				row, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(alignmentStarEdgeUnitsLabel,
				new GridBagConstraints(4, row, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(useCenterOfPriorityCheckBox,
				new GridBagConstraints(0, row, 5, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(centerRaDecLabel, new GridBagConstraints(0, row,
				1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		mascgenInputPanel.add(centerRaDecField, new GridBagConstraints(1, row,
				3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(centerRaDecUnitsLabel, new GridBagConstraints(4,
				row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(xStepsLabel, new GridBagConstraints(0, row, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		mascgenInputPanel.add(xStepsField, new GridBagConstraints(1, row, 3, 1,
				1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(xStepsUnitsLabel, new GridBagConstraints(4, row,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(xStepSizeLabel, new GridBagConstraints(0, row, 1,
				1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		mascgenInputPanel.add(xStepSizeField, new GridBagConstraints(1, row, 3,
				1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(xStepSizeUnitsLabel, new GridBagConstraints(4,
				row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(yStepsLabel, new GridBagConstraints(0, row, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		mascgenInputPanel.add(yStepsField, new GridBagConstraints(1, row, 3, 1,
				1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(yStepsUnitsLabel, new GridBagConstraints(4, row,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(yStepSizeLabel, new GridBagConstraints(0, row, 1,
				1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		mascgenInputPanel.add(yStepSizeField, new GridBagConstraints(1, row, 3,
				1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(yStepSizeUnitsLabel, new GridBagConstraints(4,
				row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(centerPALabel, new GridBagConstraints(0, row, 1,
				1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		mascgenInputPanel.add(centerPAField, new GridBagConstraints(1, row, 3,
				1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(centerPAUnitsLabel, new GridBagConstraints(4,
				row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(paStepsLabel, new GridBagConstraints(0, row, 1,
				1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		mascgenInputPanel.add(paStepsField, new GridBagConstraints(1, row, 3,
				1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(paStepsUnitsLabel, new GridBagConstraints(4, row,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(paStepSizeLabel, new GridBagConstraints(0, row,
				1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		mascgenInputPanel.add(paStepSizeField, new GridBagConstraints(1, row,
				3, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenInputPanel.add(paStepSizeUnitsLabel, new GridBagConstraints(4,
				row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		row++;
		mascgenInputPanel.add(runMascgenButton, new GridBagConstraints(0, row,
				5, 1, 1.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));

		JPanel scrollPanel = new JPanel();
		JScrollPane mascgenStatusScrollPane = new JScrollPane();
		scrollPanel.setLayout(new BorderLayout());
		scrollPanel.add(mascgenStatusScrollPane, BorderLayout.CENTER);
		mascgenStatusScrollPane.setBorder(BorderFactory
				.createTitledBorder("Status"));
		mascgenStatusScrollPane.setViewportView(mascgenStatusTextArea);
		mascgenStatusScrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mascgenStatusScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		mascgenStatusTextArea.setLineWrap(true);
		mascgenStatusTextArea.setWrapStyleWord(true);
		mascgenStatusTextArea.setEditable(false);
		row = 0;
		mascgenStatusPanel.setLayout(new GridBagLayout());
		mascgenStatusPanel.add(mascgenAbortButton, new GridBagConstraints(0,
				row, 2, 1, 10.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		row++;
		mascgenStatusPanel.add(mascgenRunNumberTitle, new GridBagConstraints(0,
				row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		mascgenStatusPanel.add(mascgenRunNumberLabel, new GridBagConstraints(1,
				row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		row++;
		mascgenStatusPanel.add(mascgenTotalRunsTitle, new GridBagConstraints(0,
				row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		mascgenStatusPanel.add(mascgenTotalRunsLabel, new GridBagConstraints(1,
				row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		row++;
		mascgenStatusPanel.add(mascgenTotalPriorityTitle,
				new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						defaultInsets, 0, 0));
		mascgenStatusPanel.add(mascgenTotalPriorityLabel,
				new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						defaultInsets, 0, 0));
		row++;
		mascgenStatusPanel.add(mascgenOptimalRunNumberTitle,
				new GridBagConstraints(0, row, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						defaultInsets, 0, 0));
		mascgenStatusPanel.add(mascgenOptimalRunNumberLabel,
				new GridBagConstraints(1, row, 1, 1, 1.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						defaultInsets, 0, 0));
		row++;
		mascgenStatusPanel.add(scrollPanel, new GridBagConstraints(0, row, 2,
				1, 10.0, 10.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, defaultInsets, 0, 0));

		JPanel mascgenPanel = new JPanel();
		mascgenPanel.setBorder(BorderFactory.createEtchedBorder());
		mascgenPanel.setBackground(MSCGUIParameters.COLOR_MASCGEN_PANEL);
		JLabel mascgenPanelLabel = new JLabel("MASCGEN");
		mascgenPanelLabel.setHorizontalAlignment(JLabel.CENTER);
		mascgenPanelLabel.setFont(MSCGUIParameters.FONT_MASCGEN_TITLE);
		mascgenParamsButtonPanel
				.setBackground(MSCGUIParameters.COLOR_MASCGEN_PANEL);

		mascgenPanel.setLayout(new GridBagLayout());
		row = 0;
		mascgenPanel.add(mascgenPanelLabel, new GridBagConstraints(0, row, 3,
				1, 1.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		row++;
		mascgenPanel.add(mascgenParamsButtonPanel, new GridBagConstraints(0,
				row, 3, 1, 1.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		row++;
		mascgenPanel.add(maskNameLabel, new GridBagConstraints(0, row, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		mascgenPanel.add(maskNameField, new GridBagConstraints(1, row, 1, 1,
				1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		mascgenPanel.add(maskNameUnitsLabel, new GridBagConstraints(2, row, 1,
				1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		row++;
		mascgenPanel.add(mascgenTabbedPane, new GridBagConstraints(0, row, 3,
				1, 1.0, 1.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));

		targetListPanel.add(targetListTableScrollPane, BorderLayout.CENTER);
		targetListPanel
				.setPreferredSize(MSCGUIParameters.DIM_TABLE_TARGET_LIST);

		JPanel specialMaskPanel = new JPanel();
		specialMaskPanel.setLayout(new GridLayout(1, 0));
		specialMaskPanel.add(openMaskPanel);
		specialMaskPanel.add(longSlitPanel);

		rightPanel.setLayout(new GridBagLayout());
		row = 0;
		rightPanel.add(openedConfigsPanel, new GridBagConstraints(0, row, 1, 1,
				10.0, 2.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				defaultInsets, 0, 0));
		if (MSCGUIParameters.ONLINE_MODE) {
			row++;
			rightPanel.add(specialMaskPanel, new GridBagConstraints(0, row, 1,
					1, 10.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		}
		// row++;
		// rightPanel.add(longSlitPanel, new
		// GridBagConstraints(0,row,1,1,10.0,0.0, GridBagConstraints.CENTER,
		// GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		rightPanel.add(mascgenPanel, new GridBagConstraints(0, row, 1, 1, 10.0,
				0.0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
				defaultInsets, 0, 0));

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

		JPanel loadedMaskPanel = new JPanel();
		loadedMaskPanel.setBorder(BorderFactory.createEtchedBorder());
		loadedMaskPanel.setLayout(new BorderLayout());
		loadedMaskPanel.add(loadedMaskLabel, BorderLayout.CENTER);
		loadedMaskLabel.setHorizontalAlignment(JLabel.CENTER);
		miraCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);

		csuReadyValueLabel.setText("-2: System Stopped");
		JPanel csuStatusPanel = new JPanel();
		csuStatusPanel.setLayout(new GridBagLayout());
		JLabel csuReadyLabel = new JLabel("CSU State:");
		JLabel csuStatusLabel = new JLabel("Status:");
		csuStatusPanel.add(csuReadyLabel, new GridBagConstraints(0, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		csuStatusPanel.add(csuReadyValueLabel, new GridBagConstraints(1, 0, 1,
				1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));
		csuStatusPanel.add(csuStatusLabel, new GridBagConstraints(2, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(2, 20, 2, 2), 0, 0));
		csuStatusPanel.add(csuStatusValueLabel, new GridBagConstraints(3, 0, 1,
				1, 10.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		csuStatusPanel.add(miraCheckBox, new GridBagConstraints(4, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				defaultInsets, 0, 0));

		JPanel scriptButtonPanel = new JPanel();
		scriptButtonPanel.setLayout(new GridBagLayout());
		scriptButtonPanel.add(csuStatusPanel, new GridBagConstraints(0, 0, 4,
				1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		scriptButtonPanel.add(setupAlignmentButton, new GridBagConstraints(0,
				1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		scriptButtonPanel.add(setupScienceButton, new GridBagConstraints(1, 1,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));
		scriptButtonPanel.add(loadedMaskPanel, new GridBagConstraints(2, 1, 1,
				1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		scriptButtonPanel.add(executeMaskButton, new GridBagConstraints(3, 1,
				1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0, 0));

		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new BorderLayout());
		middlePanel.add(slitConfigurationPanel, BorderLayout.CENTER);
		if (myModel.isOnline()) {
			middlePanel.add(scriptButtonPanel, BorderLayout.SOUTH);
		}
		middlePanel.add(maskConfigPanel, BorderLayout.NORTH);

		slitListTableModel.setData(myModel.getCurrentSlitConfiguration()
				.getMechanicalSlitList());
		slitListTable.setModel(slitListTableModel);
		TableColumn rowColumn = slitListTable.getColumnModel().getColumn(0);
		TableColumn centerColumn = slitListTable.getColumnModel().getColumn(1);
		TableColumn widthColumn = slitListTable.getColumnModel().getColumn(2);

		rowColumn
				.setCellRenderer(new CellEditorsAndRenderers.CenteredTextTableCellRenderer());
		centerColumn
				.setCellRenderer(new CellEditorsAndRenderers.DoubleValueTableCellRenderer(
						2));
		widthColumn
				.setCellRenderer(new CellEditorsAndRenderers.DoubleValueTableCellRenderer(
						2));

		rowColumn.setPreferredWidth(25);
		centerColumn.setPreferredWidth(75);
		widthColumn.setPreferredWidth(50);

		slitListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		slitListTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent lsEv) {
						slitList_tableSelectionChanged(lsEv);
					}
				});
		slitListTable.getModel().addTableModelListener(
				new TableModelListener() {
					public void tableChanged(TableModelEvent tmEv) {
						slitListTableModelChanged(tmEv);
					}
				});

		slitListTableScrollPane.getViewport().add(slitListTable);
		slitListTableScrollPane
				.setPreferredSize(MSCGUIParameters.DIM_TABLE_SLIT_LIST);

		JPanel slitTablePanel = new JPanel();
		slitTablePanel.setLayout(new BorderLayout(5, 5));
		slitTablePanel.add(slitWidthPanel, BorderLayout.NORTH);
		slitTablePanel.add(slitListTableScrollPane, BorderLayout.CENTER);

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(slitTablePanel, BorderLayout.WEST);
		leftPanel.add(middlePanel, BorderLayout.CENTER);

		objectsSplitPane.setTopComponent(leftPanel);
		objectsSplitPane.setBottomComponent(targetListPanel);

		statusPanel.setLayout(new GridBagLayout());
		statusPanel.add(statusBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
		if (MSCGUIParameters.ONLINE_MODE) {
			statusPanel.add(mosfireStatusPanel, new GridBagConstraints(1, 0, 1,
					1, 0.0, 1.0, GridBagConstraints.WEST,
					GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
			statusPanel.add(mcsusStatusPanel, new GridBagConstraints(2, 0, 1,
					1, 0.0, 1.0, GridBagConstraints.WEST,
					GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
			statusPanel.add(mdsStatusPanel, new GridBagConstraints(3, 0, 1, 1,
					0.0, 1.0, GridBagConstraints.WEST,
					GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
		}
		contentPane.add(topSplitPane, BorderLayout.CENTER);
		contentPane.add(statusPanel, BorderLayout.SOUTH);

	}
	
	///////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M1 by Ji Man Sohn, UCLA 2016-2017  //
	protected void slitNudge_actionPerformed() {
		System.out.println("Slit Nudge Perform attempted");
		try{
			String input = (String) JOptionPane.showInputDialog(this,
			        "How much offset in x-direction?(in arcsec)", "Slit Nudge in X-direction", JOptionPane.QUESTION_MESSAGE, null,
			        null, null);
			if(input == null){
				return;
			}else {
				myModel.slitNudge(Double.parseDouble(input));
			}
		}catch(NumberFormatException e){
			JOptionPane.showMessageDialog(this, "Please enter a valid integer greater than 0.", "Non-integer input", JOptionPane.ERROR_MESSAGE);
			slitNudge_actionPerformed();
		}
	}
	///////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M3 by Ji Man Sohn, UCLA 2016-2017  //
	protected void wholeFieldOffsetInX_actionPerformed() {
		double offset = 0;
		try{
			String input = (String) JOptionPane.showInputDialog(this,
			        "How much offset in x-direction?(in arcsec)", "Whole Field Offset in X-direction", JOptionPane.QUESTION_MESSAGE, null,
			        null, null);
			if(input == null){
				return;
			}else {
				offset = Double.parseDouble(input);
				wholeFieldOffsetInX(offset, false);
			}
		}catch(NumberFormatException e){
			JOptionPane.showMessageDialog(this, "Please enter a valid integer greater than 0.", "Non-integer input", JOptionPane.ERROR_MESSAGE);
			wholeFieldOffsetInX_actionPerformed();
		} 
	}
	
	private void wholeFieldOffsetInX(double offset, boolean realign){
		try {
			myModel.wholeFieldOffsetInX(offset, realign);
		} catch (OffsetException e) {
			if(e.getMessage().contains("Ok")){
				int answer = JOptionPane.showConfirmDialog(this, e.getMessage(), "Offset Conflict", JOptionPane.YES_NO_OPTION);
				if(answer == JOptionPane.YES_OPTION){
					wholeFieldOffsetInX(offset,true);
				}
			}else {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Offset Conflict", JOptionPane.ERROR_MESSAGE, null);
			}
		}
	}
	///////////////////////////////////////////////////////////////////

	protected void updateNodAmpField() {
		nodAmpField.setText(Double.toString(Double.parseDouble(ditherSpaceField
				.getText()) / 2.0));
	}

	/**
	 * Handles setting of radio buttons for specifying what to do with
	 * unassigned slits
	 * 
	 * @param type
	 *            method of handling unassinged slits. Must be one of
	 *            CLOSE_OFF_TYPE_DO_NOTHING, CLOSE_OFF_TYPE_REDUCE_IN_PLACE, or
	 *            CLOSE_OFF_TYPE_CLOSE_OFF.
	 */
	protected void reassignMethod_actionPerformed(int type) {
		myModel.setCloseOffType(type);
	}

	/**
	 * Handles clicking of align current slit with slit below.
	 */
	protected void alignWithSlitBelow_actionPerformed() {
		alignSlitWithBelow();
	}

	/**
	 * Handles clicking of align current slit with slit above.
	 */
	protected void alignWithSlitAbove_actionPerformed() {
		alignSlitWithAbove();
	}

	/**
	 * Handles clicking of moving slit on selected target.
	 */
	protected void moveSlitOnTarget_actionPerformed() {
		if (!myModel.moveSlitOntoTarget(activeTarget)) {
			JOptionPane
					.showMessageDialog(
							this,
							"Cannot create a valid slit on target.  Target is too near the edge of the field of view.",
							"Cannot Move Slit", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Handles setting of Tools->Customize menu option
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	protected void jMenuToolsCustomize_actionPerformed(ActionEvent e) {
		showCustomizeOptions();
	}

	/**
	 * Handles setting of Tools->Options menu option
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	protected void jMenuToolsOptions_actionPerformed(ActionEvent e) {
		showOptions();
	}

	/**
	 * Show options dialog.
	 */
	private void showOptions() {
		if (MSCGUIParameters.ONLINE_MODE) {
			Object[] message = {
					writeSlitConfigurationHTMLOptionCheckBox
							.getOptionDialogCheckBox(),
					"",
					duplicateMaskNameOptionCheckBox.getOptionDialogCheckBox(),
					"",
					minAlignStarsOptionCheckBox.getOptionDialogCheckBox(),
					"",
					invalidSlitWarningOptionCheckBox.getOptionDialogCheckBox(),
					"",
					setupMaskOptionCheckBox.getOptionDialogCheckBox(),
					"",
					executeMaskOptionCheckBox.getOptionDialogCheckBox(),
					"",
					executeDifferentMaskOptionCheckBox
							.getOptionDialogCheckBox(), "",
					warnMiraOptionCheckBox.getOptionDialogCheckBox(), "",
					reassignUnusedSlitsCheckBox, "",
					unusedSlitsOptionCheckBox.getOptionDialogCheckBox(), "",
					unusedBarOptionsPanel, "" };
			JOptionPane.showMessageDialog(this, message, "MSCGUI Options",
					JOptionPane.INFORMATION_MESSAGE);
			myModel
					.setClosedOffSlitWidth(((SpinnerNumberModel) (reducedSlitWidthField
							.getModel())).getNumber().doubleValue());
			myModel
					.setMinimumCloseOffSlitWidth(((SpinnerNumberModel) (minimumReassignSlitWidthField
							.getModel())).getNumber().doubleValue());

		} else {
			Object[] message = {
					writeSlitConfigurationHTMLOptionCheckBox
							.getOptionDialogCheckBox(), "",
					duplicateMaskNameOptionCheckBox.getOptionDialogCheckBox(),
					"",
					invalidSlitWarningOptionCheckBox.getOptionDialogCheckBox(),
					"", minAlignStarsOptionCheckBox.getOptionDialogCheckBox(),
					"", reassignUnusedSlitsCheckBox, "" };
			JOptionPane.showMessageDialog(this, message, "MSCGUI Options",
					JOptionPane.INFORMATION_MESSAGE);
		}
		myModel.setMascgenReassignUnusedSlits(reassignUnusedSlitsCheckBox
				.isSelected());
	}

	/**
	 * Show customize options dialog.
	 */
	private void showCustomizeOptions() {
		boolean oldValue = showMaskConfigButtonsCheckBox.isSelected();
		if (JOptionPane.showConfirmDialog(this, showMaskConfigButtonsCheckBox,
				"Customize MSCGUI", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			showMaskConfigurationButtons = showMaskConfigButtonsCheckBox
					.isSelected();
			updateGUI();
		} else {
			showMaskConfigButtonsCheckBox.setSelected(oldValue);
		}
	}

	/**
	 * Update GUI. Redraws opened configurations panel, for when GUI is
	 * customized.
	 */
	private void updateGUI() {
		openedConfigsPanel.removeAll();
		int row = 0;
		openedConfigsPanel.add(maskConfigurationsLabel, new GridBagConstraints(
				0, row, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		if (showMaskConfigurationButtons) {
			row++;
			openedConfigsPanel.add(topConfigPanel, new GridBagConstraints(0,
					row, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		}
		row++;
		openedConfigsPanel.add(openedConfigsScrollPane, new GridBagConstraints(
				0, row, 1, 1, 10.0, 10.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, defaultInsets, 0, 0));
		if (showMaskConfigurationButtons) {
			row++;
			openedConfigsPanel.add(bottomConfigPanel, new GridBagConstraints(0,
					row, 1, 1, 10.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		}
		validate();
		repaint();
	}

	/**
	 * Handle selecting of Tools->Calibration menu option
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	protected void jMenuToolsCalibration_actionPerformed(ActionEvent e) {
		showCalibrationToolsDialog();
	}

	/**
	 * Show calibration tool dialog.
	 */
	private void showCalibrationToolsDialog() {
		Point p = this.getLocation();
		calibrationFrame.setLocation(p.x + 100, p.y + 100);
		ArrayList<SlitConfiguration> masks;
		try {
			masks = getCustomSlitMasks();
			if (masks.size() > 0) {
				calibrationFrame.setSlitMasks(masks);
				calibrationFrame.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(this,
						"No opened saveable configurations.",
						"Calibration Tool Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (FileNotFoundException e) {
			JOptionPane
					.showMessageDialog(
							this,
							"Error saving Long Slit Configuration.  Could not write to default location.",
							"Error Saving LongSlit", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

	}

	/**
	 * Determines which of the opened configuration are custom masks for which
	 * calibrations could be taken.
	 * 
	 * @return ArrayList of custom slit masks
	 * @throws FileNotFoundException
	 *             on error writing long slit configuraiton to disk, if
	 *             necessary
	 */
	private ArrayList<SlitConfiguration> getCustomSlitMasks()
			throws FileNotFoundException {
		ArrayList<SlitConfiguration> openedConfigs = myModel
				.getOpenedSlitConfigurations();
		ArrayList<SlitConfiguration> openedCustomConfigs = new ArrayList<SlitConfiguration>();
		for (SlitConfiguration config : openedConfigs) {
			if (config.getStatus() == SlitConfiguration.STATUS_SAVED) {
				openedCustomConfigs.add(config);
			} else if ((config.getStatus() == SlitConfiguration.STATUS_UNSAVEABLE)
					&& (config.getMaskName().startsWith("LONGSLIT-"))) {
				// . add to list a long slit that is 46 rows but the width of
				// whatever they are using
				// . get width
				String width = config.getMaskName().substring(
						config.getMaskName().indexOf("x") + 1);
				String name = "LONGSLIT-"
						+ MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS + "x"
						+ width;
				// . check to see if mask is already added
				boolean alreadyAdded = false;
				for (SlitConfiguration c : openedCustomConfigs) {
					if (c.getMaskName().equals(name)) {
						alreadyAdded = true;
						break;
					}
				}
				if (!alreadyAdded) {
					SlitConfiguration longSlit = SlitConfiguration
							.createLongSlitConfiguration(
									MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS,
									Double.parseDouble(width));
					if (myModel.isOnline()) {
						myModel.writeLongSlitConfigurationScript(longSlit);
					}
					openedCustomConfigs.add(longSlit);
				}
			}
		}
		return openedCustomConfigs;
	}

	/**
	 * Handles increment or decrement of slit width
	 * 
	 * @param e
	 *            ActionEvent for set button press
	 * @param direction
	 *            int specifying whether it is in increment (1) or decrement
	 *            (-1)
	 */
	protected void currentSlitWidthSpinner_actionPerformed(ActionEvent e,
			int direction) {
		if (!myModel
				.incrementSlitWidth(((Double) (((SpinnerNumberModel) (currentSlitWidthSpinner
						.getModel())).getNumber())).doubleValue()
						* direction)) {
			JOptionPane
					.showMessageDialog(
							this,
							"Cannot adjust bar slit.  Change would make one or more slits invalid",
							"Error adjusting slit width",
							JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Handles opening of long slit button
	 * 
	 * @param e
	 *            ActionEvent for open long slit button
	 */
	protected void openLongSlitButton_actionPerformed(ActionEvent e) {
		openLongSlit();
	}

	/**
	 * Open a new long slit configuration with length specified by
	 * longSlitLengthSpinner and width specified by longSlitWidthSpinner.
	 */
	private void openLongSlit() {
		myModel.openLongSlitConfiguration(
				((Double) (((SpinnerNumberModel) (longSlitLengthSpinner
						.getModel())).getNumber())).doubleValue(),
				((Double) (((SpinnerNumberModel) (longSlitWidthSpinner
						.getModel())).getNumber())).doubleValue());
	}

	/**
	 * Handles open mask button
	 * 
	 * @param e
	 *            ActionEvent for open mask button
	 */
	protected void openMaskButton_actionPerformed(ActionEvent e) {
		openMask();
	}

	/**
	 * Add a new open mask configuration.
	 */
	private void openMask() {
		myModel.openOpenMaskSlitConfiguration();
	}

	/**
	 * Handles press of execute mask button
	 * 
	 * @param e
	 *            ActionEvent for execute mask button
	 */
	protected void executeMaskButton_actionPerformed(ActionEvent e) {
		executeMask();
	}

	/**
	 * Handles press of setup science mask button.
	 * 
	 * @param e
	 *            ActionEvent for setup science mask button
	 */
	protected void setupScienceButton_actionPerformed(ActionEvent e) {
		doMaskSetup(false);
	}

	/**
	 * Handles press of setup alignment mask button.
	 * 
	 * @param e
	 *            ActionEvent for setup alignment mask button
	 */
	protected void setupAlignmentButton_actionPerformed(ActionEvent e) {
		doMaskSetup(true);
	}

	/**
	 * Do mask setup. First warns if MIRA box is selected. Then warns to make
	 * sure user wants to do setup. Also warns about unused slits.
	 * 
	 * @param isAlign
	 *            flag for whether this is an alignment mask
	 */
	private void doMaskSetup(boolean isAlign) {
		try {
			disableScriptButtons();
			int answer;
			if (miraCheckBox.isSelected()) {
				if (warnMiraOptionCheckBox.isSelected()) {
					Object message[] = {
							"The MIRA check box is selected.  You should only",
							"do this if you plan to do a MIRA next.  Otherwise",
							"uncheck the MIRA box (above the Execute button)",
							"and setup your mask again.", " ",
							"Do you wish to proceed?" };
					answer = JOptionPane.showConfirmDialog(this, message,
							"Confirm MIRA", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
				} else {
					answer = JOptionPane.YES_OPTION;
				}
				if (answer == JOptionPane.NO_OPTION) {
					updateScriptButtons();
					return;
				}

			}
			if (setupMaskOptionCheckBox.isSelected()) {
				Object message[] = {
						"This will load the current setup into the CSU.",
						"This will NOT execute the mask.", " ", "Proceed?",
						" ", setupMaskOptionCheckBox,
						"(This can be changed in Tools->Options)" };
				answer = JOptionPane.showConfirmDialog(this, message,
						"Confirm Mask Setup", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				setupMaskOptionCheckBox.setDefaultAnswer(answer);
			} else {
				answer = setupMaskOptionCheckBox.getDefaultAnswer();
			}
			if (answer == JOptionPane.YES_OPTION) {
				// . warn about unused slits
				boolean[] usedSlits = myModel.getSlitUsageArray(isAlign);
				// . construct list of unused slits
				StringBuffer unusedSlitList = new StringBuffer();
				for (int ii = 0; ii < usedSlits.length; ii++) {
					if (!usedSlits[ii]) {
						unusedSlitList.append(ii + 1);
						unusedSlitList.append(", ");
					}
				}
				if (unusedSlitList.length() > 0) {
					// . TODO move button out of here.
					JButton optionsButton = new JButton("Options");
					optionsButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							showOptions();
						}
					});
					// . remove last comma
					unusedSlitList.deleteCharAt(unusedSlitList.length() - 1);
					if (unusedSlitsOptionCheckBox.isSelected()) {
						if (myModel.getCloseOffType() == MSCGUIModel.CLOSE_OFF_TYPE_DO_NOTHING) {
							Object message[] = {
									"The following slits have not been assigned:",
									unusedSlitList.toString(),
									" ",
									"The current settings indicate the slits will be left as is.",
									"Settings can be changed under Tools->Options.",
									" ", "Proceed?", " ",
									unusedSlitsOptionCheckBox,
									"(This can be changed in Tools->Options)" };
							answer = JOptionPane.showConfirmDialog(this,
									message, "Unassigned Slits",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);
						} else {
							Object message[] = {
									"The following slits have not been assigned:",
									unusedSlitList.toString(),
									" ",
									"The current settings indicate that slits larger than "
											+ myModel
													.getMinimumCloseOffSlitWidth()
											+ " arcsec wide",
									"will be closed down to "
											+ myModel.getClosedOffSlitWidth()
											+ " arcsec"
											+ ((myModel.getCloseOffType() == MSCGUIModel.CLOSE_OFF_TYPE_REDUCE_IN_PLACE) ? " in place."
													: " and moved off the field of view."),
									"Settings can be changed under Tools->Options.",
									" ", "Proceed?", " ",
									unusedSlitsOptionCheckBox,
									"(This can be changed in Tools->Options)" };
							answer = JOptionPane.showConfirmDialog(this,
									message, "Unassigned Slits",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);
						}
					} else {
						answer = JOptionPane.YES_OPTION;
					}
					if (answer == JOptionPane.NO_OPTION) {
						updateScriptButtons();
						return;
					}
				}
				myModel.executeMaskSetup(isAlign, miraCheckBox.isSelected());
				if (miraCheckBox.isSelected()) {
					miraCheckBox.setSelected(false);
				}
			} else {
				updateScriptButtons();
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Error executing mask setup: "
					+ ex.getMessage(), "Error Executing Mask Setup",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			updateScriptButtons();
		} catch (InterruptedException ex) {
			JOptionPane.showMessageDialog(this, "Interrupt during mask setup: "
					+ ex.getMessage(), "Setup Mask Interrupt",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			updateScriptButtons();
		}
	}

	/**
	 * Execute mask. First warns if dark filter is not in place, then warns
	 * about executing mask.
	 */
	private void executeMask() {
		if (!myModel.isFilterDark()) {
			if (JOptionPane
					.showConfirmDialog(
							this,
							"Dark Filter should be put in before executing masks.  Proceed?",
							"Dark Filter Not In", JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
				return;
			}
		}
		disableScriptButtons();
		String loadedMask = myModel.getLoadedMaskSetup();
		int answer;
		if (executeMaskOptionCheckBox.isSelected()) {
			Object[] message = {
					"This will cause the CSU to configure the currently loaded mask, which is:",
					" ",
					loadedMask,
					" ",
					"This could take several minutes.  Do you wish to proceed?",
					executeMaskOptionCheckBox,
					"(This can be changed in Tools->Options)" };
			answer = JOptionPane.showConfirmDialog(this, message,
					"Confirm Mask Configuration", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			executeMaskOptionCheckBox.setDefaultAnswer(answer);
		} else {
			answer = executeMaskOptionCheckBox.getDefaultAnswer();
		}
		if (answer == JOptionPane.YES_OPTION) {
			// . check that current slit configuration matched last loaded mask
			String currentSlitMaskName = myModel.getCurrentSlitConfiguration()
					.getMaskName();
			if (!loadedMask.equals(currentSlitMaskName)
					&& !loadedMask.equals(currentSlitMaskName + " (align)")) {
				if (executeDifferentMaskOptionCheckBox.isSelected()) {
					Object warningMessage[] = {
							"Current mask loaded in CSU (" + loadedMask + ")",
							"does not match current slit configuration ("
									+ currentSlitMaskName + ")", " ",
							"Are you sure you want to continue?", " ",
							executeDifferentMaskOptionCheckBox,
							"(This can be changed in Tools->Options)" };
					answer = JOptionPane.showConfirmDialog(this,
							warningMessage, "Confirm Mask Configuration",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
					executeDifferentMaskOptionCheckBox.setDefaultAnswer(answer);
				} else {
					answer = executeDifferentMaskOptionCheckBox
							.getDefaultAnswer();
				}
				if (answer == JOptionPane.NO_OPTION) {
					updateScriptButtons();
					return;
				}
			}
			try {
				myModel.executeMask();
			} catch (InterruptedException ex) {
				JOptionPane.showMessageDialog(this,
						"Interrupt during mask execution: " + ex.getMessage(),
						"Execute Mask Interrupt", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				updateScriptButtons();
			} catch (FitsException ex) {
				JOptionPane.showMessageDialog(this,
						"Error writing FITS extension: " + ex.getMessage(),
						"FITS Extension Error", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				updateScriptButtons();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Error executing mask: "
						+ ex.getMessage(), "Error Executing Mask",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				updateScriptButtons();
			} catch (NoSuchPropertyException ex) {
				JOptionPane.showMessageDialog(this, "Error executing mask: "
						+ ex.getMessage(), "Error Executing Mask",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				updateScriptButtons();
			} catch (InvalidValueException ex) {
				JOptionPane.showMessageDialog(this, "Error executing mask: "
						+ ex.getMessage(), "Error Executing Mask",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				updateScriptButtons();
			}
		} else {
			updateScriptButtons();
		}
	}

	/**
	 * Sets the input object list.
	 * 
	 * @param fullPath
	 *            String to path of input object list
	 */
	private void setInputObjectList(String fullPath) {
		inputObjectListFullPath = fullPath;
		inputObjectListValueLabel.setToolTipText(inputObjectListFullPath);
		inputObjectListValueLabel.setText(FileUtilities
				.getNameOfFile(inputObjectListFullPath));
		inputObjectListValueLabel.setName(inputObjectListFullPath);

	}

	/**
	 * Handles browse button for input object list.
	 */
	protected void inputObjectListBrowseButton_actionPerformed() {
		chooseTargetList();
	}

	/**
	 * Handles if focus is lost in mask name field.
	 * 
	 * @param e
	 *            FocusEvent for losing focus on mask name field
	 */
	protected void maskNameField_focusLost(FocusEvent e) {
		String maskName = maskNameField.getText().trim();
		if (maskName.isEmpty()) {
			maskName = MSCGUIParameters.DEFAULT_MASK_NAME;
			maskNameField.setText(maskName);
		}
		mascgenOutputPanel.updateOutputParams(maskName);
	}

	/**
	 * Handles copy configuration button press.
	 * 
	 * @param e
	 *            ActionEvent for copy configuration button
	 */
	protected void copyConfigButton_actionPerformed(ActionEvent e) {
		copyNewSlitConfiguration();
	}

	/**
	 * Copy new slit configuration.
	 */
	private void copyNewSlitConfiguration() {
		int index = openedConfigsTable.getSelectedRow();
		if (index >= 0) {
			if (myModel.getCurrentSlitConfiguration().getStatus().equals(
					SlitConfiguration.STATUS_UNSAVEABLE)) {
				JOptionPane.showMessageDialog(this,
						"Unsaveable configurations cannot be copied.",
						"Cannot Copy Configuration", JOptionPane.ERROR_MESSAGE);
				return;
			}
			myModel.copySlitConfiguration(index);
		}

	}

	/**
	 * Handles press of open configuration button.
	 * 
	 * @param e
	 *            ActionEvent for open configuration button
	 */
	protected void openConfigButton_actionPerformed(ActionEvent e) {
		openNewSlitConfiguration();
	}

	/**
	 * Open new slit configuration. Displays file chooser for selecting
	 * configuration.
	 */
	private void openNewSlitConfiguration() {
		if (openSlitConfigurationFC.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			ArrayList<String> warningList = new ArrayList<String>();
			try {
				myModel.openSlitConfiguration(openSlitConfigurationFC
						.getSelectedFile(), warningList);
				if (!warningList.isEmpty()) {
					JOptionPane
							.showMessageDialog(
									this,
									constructWarningListDialogMessage(
											"The following warnings were found while opening slit configuration file:",
											warningList, ""),
									"Warnings Found Opening File",
									JOptionPane.WARNING_MESSAGE);
				}
			} catch (JDOMException ex) {
				JOptionPane.showMessageDialog(this,
						"Error parsing Slit Configuration file:\n\n"
								+ openSlitConfigurationFC.getSelectedFile()
										.getPath() + "\n\n" + ex.getMessage(),
						"Error Parsing File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this,
						"Error opening Slit Configuration file:\n\n"
								+ openSlitConfigurationFC.getSelectedFile()
										.getPath() + "\n\n" + ex.getMessage(),
						"Error Opening File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Handles save configuration button press.
	 * 
	 * @param e
	 *            ActionEvent for save configuration button
	 */
	protected void saveConfigButton_actionPerformed(ActionEvent e) {
		saveSlitConfiguration();
	}

	/**
	 * Save slit configuration. Warns if invalid slits exist in current
	 * configuration. File chooser shown for file selection.
	 */
	private void saveSlitConfiguration() {
		int answer;
		if (myModel.currentSlitConfigurationHasInvalidSlits()) {
			if (invalidSlitWarningOptionCheckBox.isSelected()) {
				Object message[] = {
						"The current slit configuration contains invalid slits.",
						" ", "Save anyway?", " ",
						invalidSlitWarningOptionCheckBox,
						"(This can be changed in Tools->Options)" };
				answer = JOptionPane.showConfirmDialog(this, message,
						"Confirm Mask Setup", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
			} else {
				answer = invalidSlitWarningOptionCheckBox.getDefaultAnswer();
			}
		} else {
			answer = JOptionPane.YES_OPTION;
		}
		if (answer == JOptionPane.YES_OPTION) {
			if (saveSlitConfigurationFC.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = saveSlitConfigurationFC.getSelectedFile();
				// ////////////////////////////////////////////////////////////////
				// Part of MAGMA UPGRADE item m1 by Ji Man Sohn, UCLA 2016-2017////
				int pos = file.getName().lastIndexOf(".xml");
				if (pos > 16 || (pos < 0 && file.getName().length() > 16)) {
					JOptionPane.showMessageDialog(this,
							"Please limit the mask name to 16 character");
					saveSlitConfiguration();
					return;
				}
				// ////////////////////////////////////////////////////////////////
				if (file.exists()) {
					answer = JOptionPane.showConfirmDialog(this,
							"File exists.  Overwrite?");
					if (answer == JOptionPane.NO_OPTION) {
						saveSlitConfiguration();
						return;
					} else if (answer == JOptionPane.CANCEL_OPTION) {
						return;
					}
				}
				try {
					myModel.writeMSCFile(file);
					// . status should have changed
					openedConfigsTable.repaint();
					if (writeSlitConfigurationHTMLOptionCheckBox.isSelected()) {
						try {
							myModel.writeMSCHtmlFile(file);
						} catch (MalformedURLException ex) {
							Object[] message = {
									"Error saving HTML version of Slit Configuration file:",
									" ", file.getPath(), " ", ex.getMessage(),
									" ", "The XML was saved successfully.",
									" ",
									writeSlitConfigurationHTMLOptionCheckBox,
									"(This can be changed in Tools->Options)" };
							JOptionPane.showMessageDialog(this, message,
									"Error Saving File",
									JOptionPane.ERROR_MESSAGE);
							ex.printStackTrace();
						} catch (TransformerException ex) {
							Object[] message = {
									"Error saving HTML version of Slit Configuration file:",
									" ", file.getPath(), " ", ex.getMessage(),
									" ", "The XML was saved successfully.",
									" ",
									writeSlitConfigurationHTMLOptionCheckBox,
									"(This can be changed in Tools->Options)" };
							JOptionPane.showMessageDialog(this, message,
									"Error Saving File",
									JOptionPane.ERROR_MESSAGE);
							ex.printStackTrace();
						}
					}
				} catch (JDOMException ex) {
					JOptionPane
							.showMessageDialog(this,
									"Error saving Slit Configuration file:\n\n"
											+ file.getPath() + "\n\n"
											+ ex.getMessage(),
									"Error Saving File",
									JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				} catch (IOException ex) {
					JOptionPane
							.showMessageDialog(this,
									"Error saving Slit Configuration file:\n\n"
											+ file.getPath() + "\n\n"
											+ ex.getMessage(),
									"Error Saving File",
									JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Generate fits extensions.
	 */
	private void generateFitsExtensions() {
		generateFitsExtensionsPanel.setMaskName(myModel
				.getCurrentSlitConfiguration().getMaskName());
		generateFitsExtensionsPanel.setDefaultDirectory(new File(myModel
				.getCurrentSlitConfiguration().getMascgenArgs()
				.getOutputDirectory()));
		generateFitsExtensionsPanel.setHasAlign(myModel
				.getCurrentSlitConfiguration().getAlignmentStarCount() > 0);
		if (JOptionPane.showConfirmDialog(this, generateFitsExtensionsPanel,
				"Specify Filenames", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE) == JOptionPane.OK_OPTION) {
			try {
				if (generateFitsExtensionsPanel.isScienceSelected()) {
					myModel.getCurrentSlitConfiguration().writeFITSExtension(
							generateFitsExtensionsPanel.getScienceFilename(),
							false);
				}
				if (generateFitsExtensionsPanel.isAlignSelected()) {
					myModel.getCurrentSlitConfiguration().writeFITSExtension(
							generateFitsExtensionsPanel.getAlignFilename(),
							true);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FitsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Save all config button_action performed.
	 * 
	 * @param e
	 *            the e
	 */
	protected void saveAllConfigButton_actionPerformed(ActionEvent e) {
		// ////////////////////////////////////////////////////////////////
		// Part of MAGMA UPGRADE item m1 by Ji Man Sohn, UCLA 2016-2017 //
		if (myModel.getCurrentSlitConfiguration().getMaskName().length() < 17) {
			saveAllSlitConfigurationProducts();
		} else {
			JOptionPane.showMessageDialog(this,
					"Please limit the mask name to 16 character");
		}
		// saveAllSlitConfigurationProducts();
		// ////////////////////////////////////////////////////////////////
	}

	/**
	 * Save all slit configuration products.
	 */
	private void saveAllSlitConfigurationProducts() {
		int answer;
		if (myModel.currentSlitConfigurationHasInvalidSlits()) {
			if (invalidSlitWarningOptionCheckBox.isSelected()) {
				Object message[] = {
						"The current slit configuration contains invalid slits.",
						" ", "Save anyway?", " ",
						invalidSlitWarningOptionCheckBox,
						"(This can be changed in Tools->Options)" };
				answer = JOptionPane.showConfirmDialog(this, message,
						"Confirm Mask Setup", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
			} else {
				answer = invalidSlitWarningOptionCheckBox.getDefaultAnswer();
			}
		} else {
			answer = JOptionPane.YES_OPTION;
		}
		if (answer == JOptionPane.YES_OPTION) {

			if (JOptionPane.showConfirmDialog(this, maskConfigOutputPanel,
					"Save All MASCGEN Products", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				try {

					MascgenArguments tempArgs = new MascgenArguments();
					maskConfigOutputPanel
							.fillMascgenArgrumentsWithOutputs(tempArgs);

					// . validate output directory
					File outputDir = validateOutputDirectory(tempArgs);
					// . if no exception thrown, root dir exists and is
					// writeable
					if (outputDir.exists()) {
						ArrayList<String> filenamesInUse = getMaskOutputFilenamesInUse(tempArgs);
						if (!filenamesInUse.isEmpty()) {

							filenamesInUse
									.add(0,
											"The following output files already exist:");
							filenamesInUse.add(1, " ");
							filenamesInUse.add(" ");
							filenamesInUse.add("Overwrite?");
							answer = JOptionPane.showConfirmDialog(this,
									filenamesInUse.toArray(),
									"Warning: Filenames In Use",
									JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.WARNING_MESSAGE);
							if (answer == JOptionPane.CANCEL_OPTION) {
								return;
							} else if (answer == JOptionPane.NO_OPTION) {
								saveAllSlitConfigurationProducts();
								return;
							}
						}
					} else {
						if (outputDir.mkdir() == false) {
							JOptionPane.showMessageDialog(this,
									"Error creating output directory:\n\n"
											+ outputDir.getAbsolutePath(),
									"Error Saving Files",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					}

					maskConfigOutputPanel
							.fillMascgenArgrumentsWithOutputs(myModel
									.getCurrentSlitConfiguration()
									.getMascgenArgs());
					myModel
							.writeCurrentSlitConfigurationOutputs(writeSlitConfigurationHTMLOptionCheckBox
									.isSelected());
					// . status should have changed
					openedConfigsTable.repaint();
				} catch (InvalidValueException ex) {
					JOptionPane.showMessageDialog(this,
							"Error saving Slit Configuration files.\n\n"
									+ ex.getMessage(), "Error Saving Files",
							JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				} catch (JDOMException ex) {
					JOptionPane.showMessageDialog(this,
							"Error saving Slit Configuration files.\n\n"
									+ ex.getMessage(), "Error Saving Files",
							JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(this,
							"Error saving Slit Configuration file.\n\n"
									+ ex.getMessage(), "Error Saving Files",
							JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				} catch (TransformerException ex) {
					JOptionPane.showMessageDialog(this,
							"Error saving Slit Configuration file.\n\n"
									+ ex.getMessage(), "Error Saving Files",
							JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Gets the mask output filenames in use.
	 * 
	 * @param args
	 *            the args
	 * @return the mask output filenames in use
	 */
	private ArrayList<String> getMaskOutputFilenamesInUse(MascgenArguments args) {
		ArrayList<String> list = new ArrayList<String>();

		String[] filenameArray = { args.getFullPathOutputMSC(),
				args.getFullPathOutputMascgenParams(),
				args.getFullPathOutputMaskScript(),
				args.getFullPathOutputAlignMaskScript(),
				args.getFullPathOutputMaskTargets(),
				args.getFullPathOutputAllTargets(),
				args.getFullPathOutputSlitList(),
				args.getFullPathOutputDS9Regions(),
				args.getFullPathOutputStarList() };

		File currentFile;

		for (String filename : filenameArray) {
			currentFile = new File(filename);
			if (currentFile.exists()) {
				list.add(filename);
			}
		}
		return list;
	}

	/**
	 * Close config button_action performed.
	 * 
	 * @param e
	 *            the e
	 */
	protected void closeConfigButton_actionPerformed(ActionEvent e) {
		closeCurrentConfig();
	}

	/**
	 * Close current config.
	 */
	private void closeCurrentConfig() {
		int index = openedConfigsTable.getSelectedRow();
		if (index >= 0) {
			if (!myModel.getCurrentSlitConfiguration().getStatus().equals(
					SlitConfiguration.STATUS_SAVED)
					&& !myModel.getCurrentSlitConfiguration().getStatus()
							.equals(SlitConfiguration.STATUS_UNSAVEABLE)) {
				if (JOptionPane
						.showConfirmDialog(this, "Close without saving?",
								"File has not been saved.",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
					return;
				}
			}
			myModel.closeSlitConfiguration(index);
		}
	}

	/**
	 * Save mascgen params button_action performed.
	 * 
	 * @param e
	 *            the e
	 */
	protected void saveMascgenParamsButton_actionPerformed(ActionEvent e) {
		saveMascgenParams();
	}

	/**
	 * Load mascgen params button_action performed.
	 * 
	 * @param e
	 *            the e
	 */
	protected void loadMascgenParamsButton_actionPerformed(ActionEvent e) {
		loadMascgenParams();
	}

	/**
	 * Load mascgen params.
	 */
	private void loadMascgenParams() {
		mascgenParamsFC.setDialogTitle("Load MASCGEN Parameters");
		if (mascgenParamsFC.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				ArrayList<String> warningList = new ArrayList<String>();
				MascgenArguments args = MascgenArguments.readMascgenParamFile(
						mascgenParamsFC.getSelectedFile(), warningList);
				updateViewMascgenArguments(args);
				if (!warningList.isEmpty()) {
					JOptionPane
							.showMessageDialog(
									this,
									constructWarningListDialogMessage(
											"The following warnings were found while opening MASCGEN configuration file:",
											warningList, ""),
									"Warnings Found Opening File",
									JOptionPane.WARNING_MESSAGE);
				}

			} catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(this,
						"Error opening parameter file:\n\n"
								+ mascgenParamsFC.getSelectedFile().getPath()
								+ "\n\n" + ex.getMessage(),
						"Error Opening File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (JDOMException ex) {
				JOptionPane.showMessageDialog(this,
						"Error parsing parameter file:\n\n"
								+ mascgenParamsFC.getSelectedFile().getPath()
								+ "\n\n" + ex.getMessage(),
						"Error Parsing File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this,
						"Error opening parameter file:\n\n"
								+ mascgenParamsFC.getSelectedFile().getPath()
								+ "\n\n" + ex.getMessage(),
						"Error Opening File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Save mascgen params.
	 */
	private void saveMascgenParams() {
		mascgenParamsFC.setDialogTitle("Save MASCGEN Parameters");
		if (mascgenParamsFC.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File saveFile = mascgenParamsFC.getSelectedFile();
			if (saveFile.exists()) {
				int answer = JOptionPane.showConfirmDialog(this,
						"File exists.  Overwrite?");
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
				JOptionPane.showMessageDialog(this,
						"Error saving parameter file:\n\n" + saveFile.getPath()
								+ "\n\n" + ex.getMessage(),
						"Error Saving File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (InvalidValueException ex) {
				// . TODO: ignore this error. maybe separate method that doesn't
				// validate dir?
				JOptionPane.showMessageDialog(this, "Error with parameters: "
						+ ex.getMessage(), "Error starting MASCGEN",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (JDOMException ex) {
				JOptionPane
						.showMessageDialog(this,
								"XML error writing parameter file:\n\n"
										+ saveFile.getPath() + "\n\n"
										+ ex.getMessage(), "Error Saving File",
								JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this,
						"Error saving parameter file:\n\n" + saveFile.getPath()
								+ "\n\n" + ex.getMessage(),
						"Error Saving File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Use center of priority check box_action performed.
	 * 
	 * @param e
	 *            the e
	 */
	protected void useCenterOfPriorityCheckBox_actionPerformed(ActionEvent e) {
		centerRaDecField.setEnabled(!useCenterOfPriorityCheckBox.isSelected());
	}

	/**
	 * Construct mascgen arguments from fields.
	 * 
	 * @return the mascgen arguments
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws InvalidValueException
	 *             the invalid value exception
	 */
	private MascgenArguments constructMascgenArgumentsFromFields()
			throws NumberFormatException, InvalidValueException {
		RaDec center;
		// . if using center of priority, allow center to be invalid. replace
		// with default ra/dec
		try {
			center = getRaDecFromString(centerRaDecField.getText().trim());
		} catch (NumberFormatException ex) {
			if (useCenterOfPriorityCheckBox.isSelected()) {
				center = new RaDec();
			} else {
				throw ex;
			}
		} catch (InvalidValueException ex) {
			if (useCenterOfPriorityCheckBox.isSelected()) {
				center = new RaDec();
			} else {
				throw ex;
			}
		}
		MascgenArguments args = new MascgenArguments(maskNameField.getText()
				.trim(), inputObjectListValueLabel.getName(), Double
				.parseDouble(xRangeField.getText()), Double
				.parseDouble(xCenterField.getText()), Double
				.parseDouble(slitWidthField.getText()), Double
				.parseDouble(ditherSpaceField.getText()), center,
				useCenterOfPriorityCheckBox.isSelected(), Integer
						.parseInt(xStepsField.getText()), Double
						.parseDouble(xStepSizeField.getText()), Integer
						.parseInt(yStepsField.getText()), Double
						.parseDouble(yStepSizeField.getText()), Double
						.parseDouble(centerPAField.getText()), Integer
						.parseInt(paStepsField.getText()), Double
						.parseDouble(paStepSizeField.getText()), Integer
						.parseInt(alignmentStarsField.getText()), Double
						.parseDouble(alignmentStarEdgeField.getText()));
		mascgenOutputPanel.fillMascgenArgrumentsWithOutputs(args);
		validateOutputDirectory(args);
		return args;
	}

	/**
	 * Gets the ra dec from string.
	 * 
	 * @param raDecString
	 *            the ra dec string
	 * @return the ra dec from string
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws InvalidValueException
	 *             the invalid value exception
	 */
	private RaDec getRaDecFromString(String raDecString)
			throws NumberFormatException, InvalidValueException {
		String[] splits = raDecString.split(" ");
		ArrayList<String> splitsTrimmed = new ArrayList<String>();
		// . throw away empty splits, to allow arbitrary whitespace between
		// fields
		for (String s : splits) {
			if (!s.isEmpty()) {
				splitsTrimmed.add(s);
			}
		}
		// . make sure there are enough fields
		if (splitsTrimmed.size() < 6) {
			throw new InvalidValueException(
					"Invalid number of components in Ra/Dec string");
		}
		return new RaDec(Integer.parseInt(splitsTrimmed.get(0)), Integer
				.parseInt(splitsTrimmed.get(1)), Double
				.parseDouble(splitsTrimmed.get(2)), Double
				.parseDouble(splitsTrimmed.get(3)), Double
				.parseDouble(splitsTrimmed.get(4)), Double
				.parseDouble(splitsTrimmed.get(5)));

	}

	/**
	 * Validate output directory.
	 * 
	 * @param args
	 *            the args
	 * @return the file
	 * @throws InvalidValueException
	 *             the invalid value exception
	 */
	private File validateOutputDirectory(MascgenArguments args)
			throws InvalidValueException {
		String testDir = args.getOutputDirectory() + File.separator
				+ args.getOutputSubdirectory();
		// . check to see if testDir exists
		File testFile = new File(testDir);
		if (testFile.exists()) {
			// . check to see if dir is writable
			if (!testFile.canWrite()) {
				throw new InvalidValueException("Output directory <" + testFile
						+ "> is not writable.");
			}
			// . TODO validate all output products for writeability
		} else {
			File parentDir = new File(args.getOutputDirectory());
			if (parentDir.exists()) {
				// . check to see if dir is writable
				if (!parentDir.canWrite()) {
					throw new InvalidValueException("Output root directory <"
							+ parentDir + "> is not writable.");
				}
			} else {
				// . check it's parent
				File rootParent = parentDir.getParentFile();
				if (rootParent == null) {
					throw new InvalidValueException(
							"Output root directory <"
									+ parentDir
									+ "> cannot be created. Parent directory does not exist.");
				} else {
					// . check to see if dir is writable
					if (!parentDir.canWrite()) {
						throw new InvalidValueException(
								"Output root directory <"
										+ parentDir
										+ "> cannot be created. Parent directory is not writeable.");
					}
				}
			}
		}
		return testFile;
	}

	/**
	 * Abort mascgen button_action performed.
	 * 
	 * @param e
	 *            the e
	 */
	protected void abortMascgenButton_actionPerformed(ActionEvent e) {
		myModel.abortMascgen();
	}

	/**
	 * Run mascgen button_action performed.
	 * 
	 * @param e
	 *            the e
	 */
	protected void runMascgenButton_actionPerformed(ActionEvent e) {
		String maskName = maskNameField.getText().trim();
		int index = myModel.getSlitConfigurationIndex(maskName);
		if (index >= 0) {
			int answer;
			if (duplicateMaskNameOptionCheckBox.isSelected()) {
				Object[] message = {
						"A Mask Configuration already is opened with the mask name",
						" ",
						maskName,
						" ",
						"Do you wish to replace?  If not, please change mask name.",
						" ", duplicateMaskNameOptionCheckBox,
						"(This can be changed in Tools->Options.)" };
				answer = JOptionPane.showConfirmDialog(this, message,
						"Mask Name in Use", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				duplicateMaskNameOptionCheckBox.setDefaultAnswer(answer);
			} else {
				answer = duplicateMaskNameOptionCheckBox.getDefaultAnswer();
			}
			if (answer == JOptionPane.NO_OPTION) {
				maskNameField.requestFocus();
				maskNameField.selectAll();
				return;
			}
		}
		if(requestNumTopConfigs()){
			runMascgen();
		}
	}

	/**
	 * Run mascgen.
	 */
	private void runMascgen() {
		try {
			MascgenArguments data = constructMascgenArgumentsFromFields();
			if (data.getMinimumAlignmentStars() < MosfireParameters.SUGGESTED_MINIMUM_ALIGNMENT_STARS) {
				int answer;
				if (minAlignStarsOptionCheckBox.isSelected()) {
					Object[] message = {
							"Specified number of alignment stars ("
									+ data.getMinimumAlignmentStars() + ")",
							"is less than suggested minimum of "
									+ MosfireParameters.SUGGESTED_MINIMUM_ALIGNMENT_STARS
									+ ".", " ",
							"Do you wish to continue anyway?", " ",
							minAlignStarsOptionCheckBox,
							"(This can be changed in Tools->Options)" };
					answer = JOptionPane.showConfirmDialog(this, message,
							"Check Alignment Star Count",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
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
			try {
				myModel.startMascgen(data);
			} catch (FileNotFoundException ex) {
				Object[] message = {
						"Target list " + data.getTargetList() + " not found.",
						" ",
						"Try using " + data.getFullPathOutputAllTargets() + "?" };
				int answer = JOptionPane.showConfirmDialog(this, message,
						"Target List Not Found", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (answer == JOptionPane.YES_OPTION) {
					String origTargetList = data.getTargetList();
					data.setTargetList(data.getFullPathOutputAllTargets());
					try {
						myModel.startMascgen(data);
					} catch (FileNotFoundException e) {
						data.setTargetList(origTargetList);
						JOptionPane.showMessageDialog(this,
								"Error with target list: " + e.getMessage(),
								"Error starting MASCGEN",
								JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Error parsing parameters: "
					+ ex.getMessage(), "Error starting MASCGEN",
					JOptionPane.ERROR_MESSAGE);
		} catch (InvalidValueException ex) {
			JOptionPane.showMessageDialog(this, "Error with parameters: "
					+ ex.getMessage(), "Error starting MASCGEN",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}

	}

	public void setScriptQuestion(String message) {
		logger.debug(message);
		if (message.trim().length() > 0) {
			int answer = JOptionPane.showConfirmDialog(this, message,
					"Script Question", JOptionPane.YES_NO_OPTION);
			try {
				myModel.answerScriptQuestion(answer == JOptionPane.YES_OPTION);
			} catch (NoSuchPropertyException ex) {
				JOptionPane
						.showMessageDialog(
								this,
								"Error setting script question reply.  Script reply property not found.",
								"Error Setting Property",
								JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (InvalidValueException ex) {
				JOptionPane.showMessageDialog(this,
						"Error setting script question reply: "
								+ ex.getMessage(), "Error Setting Property",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
	 * )
	 */
	public void stateChanged(ChangeEvent cEv) {
		System.out.println("State Changed: " + cEv.toString());

	}

	// File | Open action performed
	/**
	 * J menu file open target list_action performed.
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	public void jMenuFileOpenTargetList_actionPerformed(ActionEvent e) {
		chooseTargetList();
	}

	// File | Open MSC action performed
	/**
	 * J menu file open ms c_action performed.
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	public void jMenuFileOpenMSC_actionPerformed(ActionEvent e) {
		openNewSlitConfiguration();
	}

	// File | Copy MSC action performed
	/**
	 * J menu file copy ms c_action performed.
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	public void jMenuFileCopyMSC_actionPerformed(ActionEvent e) {
		copyNewSlitConfiguration();
	}

	// File | Save MSC action performed
	/**
	 * J menu file save ms c_action performed.
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	public void jMenuFileSaveMSC_actionPerformed(ActionEvent e) {
		saveSlitConfiguration();
	}

	// File | Save All action performed
	/**
	 * J menu file save all_action performed.
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	public void jMenuFileSaveAll_actionPerformed(ActionEvent e) {
		saveAllSlitConfigurationProducts();
	}

	// File | Save All action performed
	/**
	 * J menu file generate extensions_action performed.
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	public void jMenuFileGenerateExtensions_actionPerformed(ActionEvent e) {
		generateFitsExtensions();
	}

	// File | Close MSC action performed
	/**
	 * J menu file close ms c_action performed.
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	public void jMenuFileCloseMSC_actionPerformed(ActionEvent e) {
		closeCurrentConfig();
	}

	// File | Open MSC action performed
	/**
	 * J menu file open msc list_action performed.
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	public void jMenuFileOpenMSCList_actionPerformed(ActionEvent e) {
		openMSCList();
	}

	// File | Save MSC action performed
	/**
	 * J menu file save msc list_action performed.
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	public void jMenuFileSaveMSCList_actionPerformed(ActionEvent e) {
		saveMSCList();
	}

	/**
	 * Open msc list.
	 */
	private void openMSCList() {
		int retval = mscListFC.showOpenDialog(this);
		if (retval == JFileChooser.APPROVE_OPTION) {
			File file = mscListFC.getSelectedFile();
			ArrayList<String> warningList = new ArrayList<String>();
			try {
				myModel.openMSCList(file, warningList);
				if (!warningList.isEmpty()) {
					JOptionPane
							.showMessageDialog(
									this,
									constructWarningListDialogMessage(
											"The following warnings were found while opening slit configuration list:",
											warningList, ""),
									"Warnings Found Opening List",
									JOptionPane.WARNING_MESSAGE);
				}
			} catch (JDOMException ex) {
				JOptionPane.showMessageDialog(this,
						"Error parsing Slit Configuration List:\n\n"
								+ mscListFC.getSelectedFile().getPath()
								+ "\n\n" + ex.getMessage(),
						"Error Parsing File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this,
						"Error opening Slit Configuration file:\n\n"
								+ mscListFC.getSelectedFile().getPath()
								+ "\n\n" + ex.getMessage(),
						"Error Opening File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Save msc list.
	 */
	private void saveMSCList() {
		if (mscListFC.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = FileUtilities.addExtensionIfNone(mscListFC
					.getSelectedFile(), MSCGUIParameters.MSC_LIST_EXTENSTION);
			if (file.exists()) {
				int answer = JOptionPane.showConfirmDialog(this,
						"File exists.  Overwrite?");
				if (answer == JOptionPane.NO_OPTION) {
					saveMSCList();
					return;
				} else if (answer == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}
			try {
				myModel.writeMSCList(file);
			} catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(this,
						"Error saving Slit Configuration List:\n\n"
								+ file.getPath() + "\n\n" + ex.getMessage(),
						"Error Saving File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

	// File | Set Executed Mask Directory action performed
	/**
	 * J menu file set executed mask dir_action performed.
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	public void jMenuFileSetExecutedMaskDir_actionPerformed(ActionEvent e) {
		setExecutedMaskDir();
	}

	/**
	 * Sets the executed mask dir.
	 */
	private void setExecutedMaskDir() {
		executedMaskDirFC.setSelectedFile(myModel.getScriptDirectory());
		int retval = executedMaskDirFC.showOpenDialog(this);
		if (retval == JFileChooser.APPROVE_OPTION) {
			File newExecutedMaskDir = executedMaskDirFC.getSelectedFile();
			try {
				if (FileUtilities.confirmOrCreateDirectory(newExecutedMaskDir,
						this)) {
					myModel.setScriptDirectory(newExecutedMaskDir);
					return;
				}
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Invalid directory: "
						+ ex.getMessage(), "Invalid Directory",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
			setExecutedMaskDir();
			return;
		}
	}

	// File | Exit action performed
	/**
	 * J menu file exit_action performed.
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	public void jMenuFileExit_actionPerformed(ActionEvent e) {
		closeGUI();
	}

	/**
	 * Close gui.
	 */
	private void closeGUI() {
		if (myModel.isScriptRunning() || calibrationFrame.isScriptRunning()) {
			if (JOptionPane
					.showConfirmDialog(
							this,
							"A script is currently running. Are you sure you want to exit?",
							"Script Running", JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
				return;
			}
		}
		// . check for unsaved configs
		if (myModel.hasUnsavedSlitConfigurationsOpened()) {
			if (JOptionPane.showConfirmDialog(this,
					"There are unsaved slit configurations opened.  Discard?",
					"Unsaved Configurations Open", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
				return;
			}
		}
		try {
			myModel.stopCShow();
			System.exit(0);
		} catch (KJavaException ex) {
			ex.printStackTrace();
			Object[] message = { "Error stopping CShow:", " ", ex.getMessage(),
					" ", "Exit anyway?" };
			if (JOptionPane.showConfirmDialog(this, message,
					"Error stopping CShow", JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}

	// Help | About action performed
	/**
	 * J menu help about_action performed.
	 * 
	 * @param e
	 *            ActionEvent for menu button press
	 */
	public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
		MosfireAboutBox dlg = new MosfireAboutBox(this,
				MSCGUIParameters.GUI_TITLE);
		dlg.setVersion("Version " + MSCGUIParameters.MSC_VERSION);
		dlg.setReleased("11 April 2013");
		dlg.setLocationAtCenter(this);
		dlg.setModal(true);
		dlg.setVisible(true);
	}

	/**
	 * Opened configs table_mouse released.
	 * 
	 * @param e
	 *            the e
	 */
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

	/**
	 * Opened configs panel_mouse released.
	 * 
	 * @param e
	 *            the e
	 */
	public void openedConfigsPanel_mouseReleased(MouseEvent e) {
		if (e.getButton() == getMouseButton(MSCGUIParameters.CONTEXT_MENU_MOUSE_BUTTON)) {
			openMaskConfigPopup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	/**
	 * Slit configuration panel_mouse clicked.
	 * 
	 * @param mEv
	 *            the m ev
	 */
	public void slitConfigurationPanel_mouseClicked(MouseEvent mEv) {
		int row = slitConfigurationPanel.getRow(mEv.getY());
		logger.debug("mouseClicked: " + mEv.getY() + " -> " + row);
		myModel.setActiveSlitRow(row + 1);
		// if (mEv.getButton() ==
		// getMouseButton(MSCGUIParameters.CONTEXT_MENU_MOUSE_BUTTON)) {
		if (SwingUtilities.isRightMouseButton(mEv)) {
			logger.debug("Right click");
			if (!myModel.getCurrentSlitConfiguration().getStatus().equals(
					SlitConfiguration.STATUS_UNSAVEABLE)) {
				if (row >= 0 && row < MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS) {
					JPopupMenu menu = new JPopupMenu();
					///////////////////////////////////////////////////////////////////
					// Part of MAGMA UPGRADE item M3 by Ji Man Sohn, UCLA 2016-2017  //
					menu.add(slitNudgeMenuItem);
					///////////////////////////////////////////////////////////////////
					
					///////////////////////////////////////////////////////////////////
					// Part of MAGMA UPGRADE item M3 by Ji Man Sohn, UCLA 2016-2017  //
					menu.add(wholeFieldOffsetInXMenuItem);
					///////////////////////////////////////////////////////////////////
					
					if (row > 0) {
						menu.add(alignWithAboveMenuItem);
					}
					if (row < MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS - 1) {
						menu.add(alignWithBelowMenuItem);
					}
					activeTarget = slitConfigurationPanel.getCurrentTarget();
					if (activeTarget != null) {
						menu.add(moveSlitOnTargetMenuItem);
						menu.addSeparator();
						menu.add(new JLabel("   " + activeTarget.getObjName()));
						menu.add(targetInfoMenu);
						targetInfoPanel.setAstroObj(activeTarget);
					}
					menu.show(mEv.getComponent(), mEv.getX(), mEv.getY());
				}
			}
		}
	}

	/**
	 * Gets the mouse button.
	 * 
	 * @param buttonNumber
	 *            the button number
	 * @return the mouse button
	 */
	private int getMouseButton(int buttonNumber) {
		switch (buttonNumber) {
		case 1:
			return MouseEvent.BUTTON1;
		case 2:
			return MouseEvent.BUTTON2;
		case 3:
			return MouseEvent.BUTTON3;
		default:
			return 0;
		}
	}

	/**
	 * Slit list_table selection changed.
	 * 
	 * @param lsEv
	 *            the ls ev
	 */
	public void slitList_tableSelectionChanged(ListSelectionEvent lsEv) {
		myModel.setActiveRow(slitListTable.getSelectedRow());
	}

	/**
	 * Target list_table selection changed.
	 * 
	 * @param lsEv
	 *            the ls ev
	 */
	public void targetList_tableSelectionChanged(ListSelectionEvent lsEv) {
		///////////////////////////////////////////////////////////////////
		// Part of MAGMA UPGRADE item m3 by Ji Man Sohn, UCLA 2016-2017////
		// * Added index comparison as trigger of updating selection     //
		//   to respond to keyboard navigation                           //
		// if (lsEv.getValueIsAdjusting()) {                        	 //
		if (lsEv.getFirstIndex()!=lsEv.getLastIndex()|| lsEv.getValueIsAdjusting()) {
		///////////////////////////////////////////////////////////////////
			if (targetListTable.getSelectedRow() >= 0) {
				myModel.setActiveObject((AstroObj) (targetListTableModel
						.getData().get(targetListTable
						.convertRowIndexToModel(targetListTable
								.getSelectedRow()))));
			}
		}
	}

	/**
	 * Opened configs table_table selection changed.
	 * 
	 * @param lsEv
	 *            the ls ev
	 */
	public void openedConfigsTable_tableSelectionChanged(ListSelectionEvent lsEv) {
		myModel.setCurrentSlitConfigurationIndex(openedConfigsTable
				.getSelectedRow());
	}

	/**
	 * Slit configuration table model changed.
	 * 
	 * @param tmEv
	 *            the tm ev
	 */
	public void slitConfigurationTableModelChanged(TableModelEvent tmEv) {
		logger.debug("opened configs model changed: col=" + tmEv.getColumn()
				+ ", first row=" + tmEv.getFirstRow() + ", last row="
				+ tmEv.getLastRow());
		if ((tmEv.getColumn() == -2)) {
			if (openedConfigsTableModel.getNewMaskName().isEmpty()) {
				JOptionPane.showMessageDialog(this,
						"Slit configuration name cannot be empty.",
						"Error setting mask name", JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Slit configuration name "
						+ openedConfigsTableModel.getNewMaskName()
						+ " is taken.  Names must be unique.",
						"Error setting mask name", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			updateViewCurrentMaskName(myModel.getCurrentSlitConfiguration()
					.getMaskName());
		}
	}

	/**
	 * Target list table model changed.
	 * 
	 * @param tmEv
	 *            the tm ev
	 */
	public void targetListTableModelChanged(TableModelEvent tmEv) {
		// slitConfigurationPanel.repaint();
		// .repaint();
	}

	/**
	 * Slit list table model changed.
	 * 
	 * @param tmEv
	 *            the tm ev
	 */
	public void slitListTableModelChanged(TableModelEvent tmEv) {
		// . this gets called whenever the slit list table model changes,
		// . which can be every time the current configuration changes.
		// . we really only want this handler to do something if someone
		// . changes the slit width for an individual row. in this case
		// . the start row and end row will be the same row.
		int row = tmEv.getFirstRow();
		int lastRow = tmEv.getLastRow();
		logger.debug("slitTableModelChanged row=" + row + " - "
				+ tmEv.getLastRow());
		if (row == lastRow) {
			if (slitListTableModel.getData().size() > row) {
				SlitPosition slit = (SlitPosition) (slitListTableModel
						.getData().get(row));
				if (slit != null) {
					myModel.setSlitWidth(row, slit.getSlitWidth());
				}
			}
		}
	}

	/**
	 * Choose target list.
	 */
	private void chooseTargetList() {
		// . browse for file
		int retval = objectListFC.showOpenDialog(this);

		// . if file is selected
		if (retval == JFileChooser.APPROVE_OPTION) {
			// . get file
			File file = objectListFC.getSelectedFile();
			setInputObjectList(objectListFC.getSelectedFile().getPath());
			openTargetList(file);
		}
	}

	/**
	 * Align slit with above.
	 */
	public void alignSlitWithAbove() {
		myModel.alignActiveSlitWithAbove();
	}

	/**
	 * Align slit with below.
	 */
	public void alignSlitWithBelow() {
		myModel.alignActiveSlitWithBelow();
	}

	// Overridden so we can exit when window is closed
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JFrame#processWindowEvent(java.awt.event.WindowEvent)
	 */
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			closeGUI();
		} else if (e.getID() == WindowEvent.WINDOW_DEICONIFIED) {
			repaint();
		}
	}

	/**
	 * Update view.
	 */
	private void updateView() {
		updateViewCurrentMascgenResult(myModel.getCurrentMascgenResult());
		updateViewCurrentSlitConfiguration(myModel
				.getCurrentSlitConfiguration());
		updateViewCurrentSlitConfigurationIndex(-1);
		updateViewLoadedMaskSetup(myModel.getLoadedMaskSetup());
		updateViewScriptRunning(myModel.isScriptRunning());
		updateViewCSUStatus(myModel.getCsuStatus());
		updateViewCSUReady(myModel.getCsuReady());
		updateViewUnusedBarOptions();
	}

	/**
	 * Update view unused bar options.
	 */
	private void updateViewUnusedBarOptions() {

		// reducedSlitWidthField.setValue(Double.toString(myModel.getClosedOffSlitWidth()));
		// minimumReassignSlitWidthField.setValue(Double.toString(myModel.getMinimumCloseOffSlitWidth()));
		// maximumSlitLengthField.setValue(Integer.toString(myModel.getMaximumSlitLength()));
		updateViewCloseOffType(myModel.getCloseOffType());
	}

	/**
	 * Update view active row.
	 * 
	 * @param slitTableRow
	 *            the slit table row
	 */
	private void updateViewActiveRow(int slitTableRow) {
		logger.debug("active slit table row = " + slitTableRow);
		try {
			if (slitTableRow < 0) {
				slitListTable.clearSelection();
				targetListTable.clearSelection();
				slitConfigurationPanel.setActiveRow(-1);
			} else {
				slitListTable.setRowSelectionInterval(slitTableRow,
						slitTableRow);

				// . scroll viewport of scrollpane to show selected row
				// . from Java Developers Almanac 1.4, item e947
				Rectangle rect = slitListTable.getCellRect(slitTableRow, 0,
						true);
				Point pt = slitListTableScrollPane.getViewport()
						.getViewPosition();
				rect.setLocation(rect.x - pt.x, rect.y - pt.y);

				slitListTableScrollPane.getViewport().scrollRectToVisible(rect);

				int targetIndex = targetListTableModel.getIndexOfTarget(myModel
						.getCurrentSlitConfiguration().getMechanicalSlitList()
						.get(slitTableRow).getTarget());
				if (targetIndex >= 0) {
					// . convert to view index
					int viewIndex = targetListTable
							.convertRowIndexToView(targetIndex);
					targetListTable.setRowSelectionInterval(viewIndex,
							viewIndex);

					rect = targetListTable.getCellRect(viewIndex, 0, true);
					pt = targetListTableScrollPane.getViewport()
							.getViewPosition();
					rect.setLocation(rect.x - pt.x, rect.y - pt.y);

					targetListTableScrollPane.getViewport()
							.scrollRectToVisible(rect);
				}
				slitConfigurationPanel.setActiveRow(myModel
						.getCurrentSlitConfiguration().getMechanicalSlitList()
						.get(slitTableRow).getSlitNumber() - 1);
			}
			slitConfigurationPanel.repaint();
		} catch (ArrayIndexOutOfBoundsException aioobEx) {
			JOptionPane.showMessageDialog(this, "Error selecting slit: "
					+ aioobEx.getMessage(), "Slit Selection Error",
					JOptionPane.ERROR_MESSAGE);
			aioobEx.printStackTrace();
		}
	}

	/**
	 * Update view current mascgen result.
	 * 
	 * @param result
	 *            the result
	 */
	private void updateViewCurrentMascgenResult(MascgenResult result) {
		centerValueLabel.setText(result.getCenter().toStringWithColons());
		paValueLabel.setText(Double.toString(result.getPositionAngle()));
		totalPriorityValueLabel.setText(Double.toString(result
				.getTotalPriority()));
	}

	/**
	 * Update view mascgen arguments.
	 * 
	 * @param data
	 *            the data
	 */
	private void updateViewMascgenArguments(MascgenArguments data) {
		inputObjectListFullPath = data.getTargetList();
		inputObjectListValueLabel.setToolTipText(inputObjectListFullPath);
		inputObjectListValueLabel.setText(FileUtilities
				.getNameOfFile(inputObjectListFullPath));
		inputObjectListValueLabel.setName(inputObjectListFullPath);
		useCenterOfPriorityCheckBox.setSelected(data.usesCenterOfPriority());
		centerRaDecField.setEnabled(!data.usesCenterOfPriority());
		xRangeField.setText(Double.toString(data.getxRange()));
		xCenterField.setText(Double.toString(data.getxCenter()));
		slitWidthField.setText(Double.toString(data.getSlitWidth()));
		ditherSpaceField.setText(Double.toString(data.getDitherSpace()));
		nodAmpField.setText(Double.toString(data.getDitherSpace() / 2.0));
		centerRaDecField.setText(data.getCenterPosition().toString());
		xStepsField.setText(Integer.toString(data.getxSteps()));
		xStepSizeField.setText(Double.toString(data.getxStepSize()));
		yStepsField.setText(Integer.toString(data.getySteps()));
		yStepSizeField.setText(Double.toString(data.getyStepSize()));
		centerPAField.setText(Double.toString(data.getCenterPA()));
		paStepsField.setText(Integer.toString(data.getPaSteps()));
		paStepSizeField.setText(Double.toString(data.getPaStepSize()));
		alignmentStarsField.setText(Integer.toString(data
				.getMinimumAlignmentStars()));
		alignmentStarEdgeField.setText(Double.toString(data
				.getAlignmentStarEdgeBuffer()));
		maskNameField.setText(data.getMaskName());
		mascgenOutputPanel.updateOutputParams(data);
		maskConfigOutputPanel.updateOutputParams(data);
		// if (!inputObjectListFullPath.isEmpty()) {
		// openTargetList(new File(inputObjectListFullPath));
		// }
	}

	/**
	 * Update view current mask name.
	 * 
	 * @param name
	 *            the name
	 */
	private void updateViewCurrentMaskName(String name) {
		currentMaskNameValueLabel.setText(name);
		maskConfigOutputPanel.updateOutputParams(name);
		openedConfigsTable.repaint();
	}

	/**
	 * Update view current slit configuration.
	 * 
	 * @param config
	 *            the config
	 */
	private void updateViewCurrentSlitConfiguration(SlitConfiguration config) {
		currentMaskNameValueLabel.setText(config.getMaskName());
		updateViewMascgenArguments(config.getMascgenArgs());
		updateViewCurrentMascgenResult(config.getMascgenResult());
		slitListTableModel.setData(config.getMechanicalSlitList());
		targetListTableModel.setData(config.getAllTargets());
		updateViewSlitConfigurationStatus(config.getStatus());
		// setupAlignmentButton.setEnabled((config.getAlignmentStarCount() >
		// 0));
		updateScriptButtons();
		File defaultMSCFilename;
		File defaultMSCOutputDir = new File(config.getMascgenArgs()
				.getFullPathOutputSubdirectory());
		// . if output dir and subdirectory exist, use full path
		if (defaultMSCOutputDir.exists()) {
			defaultMSCFilename = new File(config.getMascgenArgs()
					.getFullPathOutputMSC());
		} else {
			// . otherwise, try using output dir without subdir
			defaultMSCFilename = new File(config.getMascgenArgs()
					.getOutputDirectory()
					+ File.separator + config.getMascgenArgs().getOutputMSC());
		}
		saveSlitConfigurationFC.setSelectedFile(defaultMSCFilename);
		try {
			slitConfigurationPanel.openNewConfiguration(config);
		} catch (InvalidValueException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Update view slit configuration status.
	 * 
	 * @param status
	 *            the status
	 */
	private void updateViewSlitConfigurationStatus(String status) {
		openedConfigsTable.repaint();
		copyConfigButton.setEnabled(!status
				.equals(SlitConfiguration.STATUS_UNSAVEABLE));
		saveConfigButton.setEnabled(!status
				.equals(SlitConfiguration.STATUS_UNSAVEABLE));
		saveAllConfigButton.setEnabled(!status
				.equals(SlitConfiguration.STATUS_UNSAVEABLE));
	}

	/**
	 * Update view current slit configuration index.
	 * 
	 * @param index
	 *            the index
	 */
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

				// . scroll viewport of scrollpane to show selected row
				// . from Java Developers Almanac 1.4, item e947
				Rectangle rect = openedConfigsTable.getCellRect(index, 0, true);
				Point pt = openedConfigsScrollPane.getViewport()
						.getViewPosition();
				rect.setLocation(rect.x - pt.x, rect.y - pt.y);

				slitListTableScrollPane.getViewport().scrollRectToVisible(rect);
				closeConfigButton.setEnabled(true);
				copyConfigButton.setEnabled(!myModel
						.getCurrentSlitConfiguration().getStatus().equals(
								SlitConfiguration.STATUS_UNSAVEABLE));
				saveConfigButton.setEnabled(!myModel
						.getCurrentSlitConfiguration().getStatus().equals(
								SlitConfiguration.STATUS_UNSAVEABLE));
				saveAllConfigButton.setEnabled(!myModel
						.getCurrentSlitConfiguration().getStatus().equals(
								SlitConfiguration.STATUS_UNSAVEABLE));

			} catch (ArrayIndexOutOfBoundsException aioobEx) {
				JOptionPane.showMessageDialog(this,
						"Error selecting mask configuration: "
								+ aioobEx.getMessage(),
						"Mask Configuration Selection Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Update view opened slit configurations.
	 */
	private void updateViewOpenedSlitConfigurations() {
		openedConfigsTableModel.fireTableDataChanged();
		// updateViewCurrentSlitConfigurationIndex(myModel.getCurrentSlitConfigurationIndex());
	}

	/**
	 * Update view loaded mask setup.
	 * 
	 * @param newValue
	 *            the new value
	 */
	private void updateViewLoadedMaskSetup(String newValue) {
		loadedMaskLabel.setText(newValue);
	}

	/**
	 * Update view mascgen total priority.
	 * 
	 * @param doubleValue
	 *            the double value
	 */
	private void updateViewMascgenTotalPriority(double doubleValue) {
		DecimalFormat f = NumberFormatters.StandardFloatFormatter(2);
		mascgenTotalPriorityLabel.setText(f.format(doubleValue));
	}

	/**
	 * Update view mascgen optimal run number.
	 * 
	 * @param intValue
	 *            the int value
	 */
	private void updateViewMascgenOptimalRunNumber(int intValue) {
		mascgenOptimalRunNumberLabel.setText(Integer.toString(intValue));
	}

	/**
	 * Update view mascgen total runs.
	 * 
	 * @param intValue
	 *            the int value
	 */
	private void updateViewMascgenTotalRuns(int intValue) {
		mascgenTotalRunsLabel.setText(Integer.toString(intValue));
	}

	/**
	 * Update view mascgen run number.
	 * 
	 * @param intValue
	 *            the int value
	 */
	private void updateViewMascgenRunNumber(int intValue) {
		mascgenRunNumberLabel.setText(Integer.toString(intValue));
	}

	/**
	 * Update view mascgen run status.
	 * 
	 * @param status
	 *            the status
	 */
	private void updateViewMascgenRunStatus(String status) {
		mascgenStatusTextArea.append(status + "\n");
		mascgenStatusTextArea.setCaretPosition(mascgenStatusTextArea
				.getDocument().getLength());
	}

	/**
	 * Update view mascgen arguments exception.
	 * 
	 * @param ex
	 *            the ex
	 */
	private void updateViewMascgenArgumentsException(MascgenArgumentException ex) {
		updateViewMascgenRunStatus("Error running mascgen: " + ex.getMessage());
		JOptionPane.showMessageDialog(this, "Error with MASCGEN Arguments: "
				+ ex.getMessage(), "Error running MASCGEN",
				JOptionPane.ERROR_MESSAGE);
		ex.printStackTrace();
	}

	/**
	 * Update view script running.
	 * 
	 * @param status
	 *            the status
	 */
	private void updateViewScriptRunning(boolean status) {
		updateScriptButtons();
	}

	/**
	 * Disable script buttons.
	 */
	private void disableScriptButtons() {
		setupAlignmentButton.setEnabled(false);
		setupScienceButton.setEnabled(false);
		executeMaskButton.setEnabled(false);
	}

	/**
	 * Update script buttons.
	 */
	private void updateScriptButtons() {
		boolean okToSendTargets = Arrays.asList(
				MSCGUIParameters.CSU_READINESS_STATES_OK_TO_SEND_TARGETS)
				.contains(myModel.getCsuReady());
		logger.trace("updating script buttons... isScriptRunning="
				+ myModel.isScriptRunning() + ", okToSendTargets="
				+ okToSendTargets);
		setupAlignmentButton.setEnabled(!myModel.isScriptRunning()
				&& okToSendTargets
				&& (myModel.getCurrentSlitConfiguration()
						.getAlignmentStarCount() > 0));
		setupScienceButton.setEnabled(!myModel.isScriptRunning()
				&& okToSendTargets);
		executeMaskButton
				.setEnabled(!myModel.isScriptRunning()
						&& (myModel.getCsuReady() == MSCGUIParameters.CSU_READINESS_STATE_READY_TO_MOVE));

		boolean highlightExecute = false;
		boolean highlightAlign = false;
		boolean highlightScience = false;
		int current = 0;
		if (myModel.getCurrentMaskName().equals(
				myModel.getCurrentSlitConfiguration().getMaskName())) {
			// . current mask name matches active config mask name
			current = 1;
		} else if (myModel.getCurrentMaskName().equals(
				myModel.getCurrentSlitConfiguration().getMaskName()
						+ " (align)")) {
			// . current mask name matches active config alignment mask name
			current = 2;
		}
		int setup = 0;
		if (myModel.getLoadedMaskSetup().equals(
				myModel.getCurrentSlitConfiguration().getMaskName())) {
			// . setup mask name matches active config mask name
			setup = 1;
		} else if (myModel.getLoadedMaskSetup().equals(
				myModel.getCurrentSlitConfiguration().getMaskName()
						+ " (align)")) {
			// . setup mask name matches active config alignment mask name
			setup = 2;
		}

		if (setup == 1) {
			if (current != 1) {
				highlightExecute = true;
			}
		} else if (setup == 2) {
			if (current != 2) {
				highlightExecute = true;
			} else {
				highlightScience = true;
			}
		} else {
			if (current != 1) {
				highlightScience = true;
			}
			if (current != 2) {
				highlightAlign = true;
			}
		}

		executeMaskButton
				.setBackground((highlightExecute ? MSCGUIParameters.COLOR_HIGHLIGHTED_BUTTON
						: executeMaskButton.getDefaultColor()));
		setupAlignmentButton
				.setBackground((highlightAlign ? MSCGUIParameters.COLOR_HIGHLIGHTED_BUTTON
						: setupAlignmentButton.getDefaultColor()));
		setupScienceButton
				.setBackground((highlightScience ? MSCGUIParameters.COLOR_HIGHLIGHTED_BUTTON
						: setupScienceButton.getDefaultColor()));
	}

	/**
	 * Update view csu status.
	 * 
	 * @param value
	 *            the value
	 */
	private void updateViewCSUStatus(String value) {
		csuStatusValueLabel.setText(value);
	}

	/**
	 * Update view csu ready.
	 * 
	 * @param value
	 *            the value
	 */
	private void updateViewCSUReady(int value) {
		csuReadyValueLabel.setText(MSCGUIParameters.CSU_READINESS_STATES[value
				+ MSCGUIParameters.CSU_READINESS_STATES_ARRAY_OFFSET]);
		updateScriptButtons();
	}

	/**
	 * Update view close off type.
	 * 
	 * @param type
	 *            the type
	 */
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
		minimumReassignSlitWidthLabel
				.setEnabled(type != MSCGUIModel.CLOSE_OFF_TYPE_DO_NOTHING);
		minimumReassignSlitWidthField
				.setEnabled(type != MSCGUIModel.CLOSE_OFF_TYPE_DO_NOTHING);
		reducedSlitWidthLabel
				.setEnabled(type != MSCGUIModel.CLOSE_OFF_TYPE_DO_NOTHING);
		reducedSlitWidthField
				.setEnabled(type != MSCGUIModel.CLOSE_OFF_TYPE_DO_NOTHING);
	}

	/**
	 * Open target list.
	 * 
	 * @param file
	 *            the file
	 */
	private void openTargetList(File file) {

		try {
			// . have model open file can put in target list
			myModel.openTargetList(file);
			// . target list has been updated, so notify table
			targetListTableModel.fireTableDataChanged();
		} catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(this, new String[] {
					"Error opening target list: File Not Found. ", " ",
					fnfEx.getMessage() }, "Error Opening Target List",
					JOptionPane.ERROR_MESSAGE);
			fnfEx.printStackTrace();
		} catch (NumberFormatException nfEx) {
			JOptionPane.showMessageDialog(this, new String[] {
					"Error opening target list: Check target list format.",
					" ", nfEx.getMessage() }, "Error Opening Target List",
					JOptionPane.ERROR_MESSAGE);
			nfEx.printStackTrace();
		} catch (IOException ioEx) {
			JOptionPane.showMessageDialog(this, new String[] {
					"Error opening target list: I/O error. ", " ",
					ioEx.getMessage() }, "Error Opening Target List",
					JOptionPane.ERROR_MESSAGE);
			ioEx.printStackTrace();
		} catch (TargetListFormatException tlfEx) {
			JOptionPane.showMessageDialog(this, new String[] {
					"Error opening target list: Check target list format.",
					" ", tlfEx.getMessage() }, "Error Opening Target List",
					JOptionPane.ERROR_MESSAGE);
			tlfEx.printStackTrace();
		}

	}

	/**
	 * Construct warning list dialog message.
	 * 
	 * @param headerMessage
	 *            the header message
	 * @param warningList
	 *            the warning list
	 * @param finalMessage
	 *            the final message
	 * @return the object[]
	 */
	private Object[] constructWarningListDialogMessage(String headerMessage,
			ArrayList<String> warningList, String finalMessage) {
		warningListTextArea.setText("");
		for (String currentWarning : warningList) {
			warningListTextArea.append("  - " + currentWarning + "\n");
		}
		warningListTextArea.setCaretPosition(0);
		Object[] retval = { headerMessage, " ", warningListScrollPane, " ",
				finalMessage };
		return retval;
	}

	/**
	 * Update view process error output.
	 * 
	 * @param errorMessages
	 *            the error messages
	 */
	private void updateViewProcessErrorOutput(ArrayList<String> errorMessages) {
		if (!errorMessages.isEmpty()) {
			JOptionPane
					.showMessageDialog(this,
							constructWarningListDialogMessage(
									"Error running script.  Output:",
									errorMessages, ""),
							"Error Executing Script", JOptionPane.ERROR_MESSAGE);
		}

	}

	// .//.//.//.//.//.//.//.//.//.//.//.//.//
	// . MSCGUI MVC Controller inner class .//
	// .//.//.//.//.//.//.//.//.//.//.//.//.//
	/**
	 * The Class MSCGUIController.
	 */
	public class MSCGUIController extends GenericController {

		/**
		 * Instantiates a new mSCGUI controller.
		 * 
		 * @param newMSCGUIModel
		 *            the new mscgui model
		 */
		public MSCGUIController(MSCGUIModel newMSCGUIModel) {
			super(newMSCGUIModel);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * edu.ucla.astro.irlab.util.gui.GenericController#model_propertyChange
		 * (java.beans.PropertyChangeEvent)
		 */
		public void model_propertyChange(PropertyChangeEvent e) {
			if (e.getPropertyName().compareTo("activeRow") == 0) {
				updateViewActiveRow(((Integer) e.getNewValue()).intValue());
			} else if (e.getPropertyName().compareTo("currentMascgenResult") == 0) {
				updateViewCurrentMascgenResult((MascgenResult) (e.getNewValue()));
			} else if (e.getPropertyName().compareTo("loadedMaskSetup") == 0) {
				updateViewLoadedMaskSetup(e.getNewValue().toString());
			} else if (e.getPropertyName()
					.compareTo("currentSlitConfiguration") == 0) {
				updateViewCurrentSlitConfiguration((SlitConfiguration) (e
						.getNewValue()));
			} else if (e.getPropertyName()
					.compareTo("openedSlitConfigurations") == 0) {
				updateViewOpenedSlitConfigurations();
			} else if (e.getPropertyName().compareTo(
					"currentSlitConfigurationIndex") == 0) {
				updateViewCurrentSlitConfigurationIndex(((Integer) (e
						.getNewValue())).intValue());
			} else if (e.getPropertyName().compareTo("mascgenStatus") == 0) {
				updateViewMascgenRunStatus(e.getNewValue().toString());
			} else if (e.getPropertyName().compareTo("mascgenRunNumber") == 0) {
				updateViewMascgenRunNumber(((Integer) e.getNewValue())
						.intValue());
			} else if (e.getPropertyName().compareTo("mascgenTotalRuns") == 0) {
				updateViewMascgenTotalRuns(((Integer) e.getNewValue())
						.intValue());
			} else if (e.getPropertyName().compareTo("mascgenOptimalRunNumber") == 0) {
				updateViewMascgenOptimalRunNumber(((Integer) e.getNewValue())
						.intValue());
			} else if (e.getPropertyName().compareTo("mascgenTotalPriority") == 0) {
				updateViewMascgenTotalPriority(((Double) e.getNewValue())
						.doubleValue());
			} else if (e.getPropertyName()
					.compareTo("mascgenArgumentException") == 0) {
				updateViewMascgenArgumentsException((MascgenArgumentException) e
						.getNewValue());
			} else if (e.getPropertyName().compareTo("scriptRunning") == 0) {
				updateViewScriptRunning(((Boolean) e.getNewValue())
						.booleanValue());
			} else if (e.getPropertyName().compareTo("csuStatus") == 0) {
				updateViewCSUStatus(e.getNewValue().toString());
			} else if (e.getPropertyName().compareTo("csuReady") == 0) {
				updateViewCSUReady(((Integer) (e.getNewValue())).intValue());
			} else if (e.getPropertyName().compareTo("closeOffType") == 0) {
				updateViewCloseOffType(((Integer) (e.getNewValue())).intValue());
			} else if (e.getPropertyName().compareTo("processErrorOutput") == 0) {
				updateViewProcessErrorOutput(((ArrayList<String>) (e
						.getNewValue())));
			} else if (e.getPropertyName().compareTo(
					MosfireParameters.MOSFIRE_PROPERTY_MAGMA_SCRIPT_QUESTION) == 0) {
				setScriptQuestion(e.getNewValue().toString());
			}
		}

	} // . end controller inner class

	/**
	 * The Class GenerateFitsExtensionsPanel.
	 */
	private class GenerateFitsExtensionsPanel extends JPanel {

		/** The dir field. */
		private JTextField dirField = new JTextField();

		/** The mask field. */
		private JTextField maskField = new JTextField();

		/** The align field. */
		private JTextField alignField = new JTextField();

		/** The dir label. */
		private JLabel dirLabel = new JLabel("Directory:");

		/** The mask label. */
		private JCheckBox maskLabel = new JCheckBox(
				"Science Extensions Filename:");

		/** The align label. */
		private JCheckBox alignLabel = new JCheckBox(
				"Alignment Extensions Filename:");

		/** The dir browse button. */
		private JButton dirBrowseButton = new JButton("Browse");

		/** The script dat formatter. */
		private SimpleDateFormat scriptDatFormatter = new SimpleDateFormat(
				"yyMMdd_HHmmss_");

		/** The chooser. */
		private JFileChooser chooser = new JFileChooser();

		/**
		 * Instantiates a new generate fits extensions panel.
		 */
		public GenerateFitsExtensionsPanel() {
			setPreferredSize(new Dimension(600, 100));
			setLayout(new GridBagLayout());
			add(dirLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.EAST, GridBagConstraints.NONE,
					new Insets(2, 2, 2, 2), 0, 0));
			add(dirField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(2, 2, 2, 2), 0, 0));
			add(dirBrowseButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(2, 2, 2, 2), 0, 0));
			add(maskLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.EAST, GridBagConstraints.NONE,
					new Insets(2, 2, 2, 2), 0, 0));
			add(maskField, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(2, 2, 2, 2), 0, 0));
			add(alignLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.EAST, GridBagConstraints.NONE,
					new Insets(2, 2, 2, 2), 0, 0));
			add(alignField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(2, 2, 2, 2), 0, 0));
			dirField.setEditable(false);
			dirBrowseButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					dirBrowse();
				}
			});
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			maskLabel.setSelected(true);
			alignLabel.setSelected(true);
			maskLabel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					maskField.setEnabled(maskLabel.isSelected());
				}
			});
			alignLabel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					alignField.setEnabled(alignLabel.isSelected());
				}
			});
		}

		/**
		 * Sets the default directory.
		 * 
		 * @param dir
		 *            the new default directory
		 */
		public void setDefaultDirectory(File dir) {
			dirField.setText(dir.getAbsolutePath());
			chooser.setCurrentDirectory(dir);
		}

		/**
		 * Dir browse.
		 */
		private void dirBrowse() {
			if (chooser.showOpenDialog(dirBrowseButton) == JFileChooser.APPROVE_OPTION) {
				dirField.setText(chooser.getSelectedFile().toString());
			}
		}

		/**
		 * Sets the checks for align.
		 * 
		 * @param hasAlign
		 *            the new checks for align
		 */
		public void setHasAlign(boolean hasAlign) {
			alignLabel.setEnabled(hasAlign);
			alignLabel.setSelected(hasAlign);
			alignField.setEnabled(hasAlign);
		}

		/**
		 * Checks if is align selected.
		 * 
		 * @return true, if is align selected
		 */
		public boolean isAlignSelected() {
			return alignLabel.isSelected();
		}

		/**
		 * Checks if is science selected.
		 * 
		 * @return true, if is science selected
		 */
		public boolean isScienceSelected() {
			return maskLabel.isSelected();
		}

		/**
		 * Sets the mask name.
		 * 
		 * @param name
		 *            the new mask name
		 */
		public void setMaskName(String name) {
			String date = scriptDatFormatter.format(Calendar.getInstance()
					.getTime());
			String modifiedMaskName = name.replaceAll("[\\(\\)\\[\\]/]", "_");
			maskField.setText(date + modifiedMaskName + ".fits");
			alignField.setText(date + modifiedMaskName + "_align.fits");
		}

		/**
		 * Gets the science filename.
		 * 
		 * @return the science filename
		 */
		public String getScienceFilename() {
			return dirField.getText() + File.separator + maskField.getText();
		}

		/**
		 * Gets the align filename.
		 * 
		 * @return the align filename
		 */
		public String getAlignFilename() {
			return dirField.getText() + File.separator + alignField.getText();
		}
	}
	//////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M6 by Ji Man Sohn, UCLA 2016-2017 //
	private boolean requestNumTopConfigs(){
		try{
			String input = (String) JOptionPane.showInputDialog(this,
			        "How many top configuration would you like to see?", "Number of Top Configs", JOptionPane.QUESTION_MESSAGE, null,
			        null, "1");
			if(input == null){
				return false;
			}else if (input == "0"){
				throw new NumberFormatException("0 is not a valid input.");
			}else {
				myModel.setNumTopConfigs(Integer.parseInt(input));
				return true;
			}
		}catch(NumberFormatException e){
			JOptionPane.showMessageDialog(this, "Please enter a valid integer greater than 0.", "Non-integer input", JOptionPane.ERROR_MESSAGE);
			requestNumTopConfigs();
			return false;
		}
	}
	//////////////////////////////////////////////////////////////////
	
}
