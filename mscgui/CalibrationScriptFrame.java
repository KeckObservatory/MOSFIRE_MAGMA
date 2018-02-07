package edu.ucla.astro.irlab.mosfire.mscgui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

import edu.ucla.astro.irlab.mosfire.util.MosfireParameters;
import edu.ucla.astro.irlab.mosfire.util.SlitConfiguration;
import edu.ucla.astro.irlab.util.process.ProcessControl;
import edu.ucla.astro.irlab.util.process.ProcessInfo;
import edu.ucla.astro.irlab.util.process.ProcessListener;
import edu.ucla.astro.irlab.util.process.ProcessListenerDialog;

public class CalibrationScriptFrame extends JDialog implements ProcessListener {
	private static final Logger logger = Logger.getLogger(CalibrationScriptFrame.class);
	
  protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  private Insets defaultInsets = new Insets(2,2,2,2);

  private QuantityPanel arcsQuantityPanel = new QuantityPanel(MSCGUIParameters.DEFAULT_ITIME_SEC_ARCS, MSCGUIParameters.DEFAULT_QUANTITY_ARCS);
  private ArcsTable arcsTable = new ArcsTable();
  private DefaultTableModel arcsTableModel;
  
  private QuantityPanel flatsYQuantityPanel = new QuantityPanel(MSCGUIParameters.DEFAULT_ITIME_SEC_FLATS_Y, MSCGUIParameters.DEFAULT_QUANTITY_FLATS_Y, MSCGUIParameters.DEFAULT_LAMPS_OFF_FLATS_Y);
  private QuantityPanel flatsJQuantityPanel = new QuantityPanel(MSCGUIParameters.DEFAULT_ITIME_SEC_FLATS_J, MSCGUIParameters.DEFAULT_QUANTITY_FLATS_J, MSCGUIParameters.DEFAULT_LAMPS_OFF_FLATS_J);
  private QuantityPanel flatsHQuantityPanel = new QuantityPanel(MSCGUIParameters.DEFAULT_ITIME_SEC_FLATS_H, MSCGUIParameters.DEFAULT_QUANTITY_FLATS_H, MSCGUIParameters.DEFAULT_LAMPS_OFF_FLATS_H);
  private QuantityPanel flatsKQuantityPanel = new QuantityPanel(MSCGUIParameters.DEFAULT_ITIME_SEC_FLATS_K, MSCGUIParameters.DEFAULT_QUANTITY_FLATS_K, MSCGUIParameters.DEFAULT_LAMPS_OFF_FLATS_K);
  private JTable slitMasksTable = new JTable();
  
  private JCheckBox endOfNightShutdownCheckBox = new JCheckBox("Do end-of-night shutdown when done?");
	
  private JButton goButton = new JButton("GO");
  private JButton abortButton = new JButton("ABORT");
  private JButton quitButton = new JButton("QUIT");

  private DefaultTableModel slitMasksTableModel;
  private ProcessControl myProcessControl = new ProcessControl();

  private ArrayList<SlitConfiguration> slitMasks = new ArrayList<SlitConfiguration>();
	private boolean scriptRunning;
	private long currentCommandProcessID;

	public CalibrationScriptFrame(Frame parent) {
		super(parent, MSCGUIParameters.CALIBRATION_GUI_TITLE);
		initGUI();	
//		validate();
	}

	public void setSlitMasks(ArrayList<SlitConfiguration> newSlitMasks) {
		slitMasks = newSlitMasks;
		slitMasksTableModel.setRowCount(0);
		for (SlitConfiguration mask : slitMasks) {
			slitMasksTableModel.addRow(new Object[] {mask.getMaskName(), new Boolean(true), new Boolean(true), new Boolean(true), new Boolean(true)});
		}
	}

	private void initGUI() {
	  Insets largeInsets = new Insets(5,5,5,5);
		
	  JPanel contentPane;
	  JPanel mainPanel = new JPanel();
	  JPanel arcsPanel = new JPanel();
	  JPanel flatsPanel = new JPanel();
	  JPanel slitMasksPanel = new JPanel();
	  JPanel optionsPanel = new JPanel();
	  JLabel arcsLabel = new JLabel("ARCS");
	  JLabel flatsLabel = new JLabel("FLATS");
		JLabel flatsYLabel = new JLabel("Y:");
		JLabel flatsJLabel = new JLabel("J:");
		JLabel flatsHLabel = new JLabel("H:");
		JLabel flatsKLabel = new JLabel("K:");
		
	  JLabel slitMasksLabel = new JLabel("SLITMASKS");
	  JLabel optionsLabel = new JLabel("OPTIONS");
	  JScrollPane slitMasksScrollPane = new JScrollPane();
				
		slitMasksTableModel = new DefaultTableModel(new String[] {"Mask Name", "Y", "J", "H", "K"}, 0) {
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0) {
					return String.class;
				} else {
					return Boolean.class;
				}
			}
		};
		
		slitMasksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		slitMasksTable.setModel(slitMasksTableModel);
		slitMasksTableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				slitMasksTableModel_tableChanged(e);
				
			}
		});

		slitMasksTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				slitMasksTable_selectionChanged(e);
			}
		});
		
		//. set widths for filter columns
		slitMasksTable.getColumnModel().getColumn(1).setMaxWidth(MSCGUIParameters.WIDTH_SPECTRAL_CALIBRATION_FILTER_COLUMN);
		slitMasksTable.getColumnModel().getColumn(2).setMaxWidth(MSCGUIParameters.WIDTH_SPECTRAL_CALIBRATION_FILTER_COLUMN);
		slitMasksTable.getColumnModel().getColumn(3).setMaxWidth(MSCGUIParameters.WIDTH_SPECTRAL_CALIBRATION_FILTER_COLUMN);
		slitMasksTable.getColumnModel().getColumn(4).setMaxWidth(MSCGUIParameters.WIDTH_SPECTRAL_CALIBRATION_FILTER_COLUMN);
				
		//. create borders for subsections
		arcsPanel.setBorder(BorderFactory.createEtchedBorder());
		flatsPanel.setBorder(BorderFactory.createEtchedBorder());
		slitMasksPanel.setBorder(BorderFactory.createEtchedBorder());
		optionsPanel.setBorder(BorderFactory.createEtchedBorder());
		
		//. put header labels in center
		arcsLabel.setHorizontalAlignment(JLabel.CENTER);
		flatsLabel.setHorizontalAlignment(JLabel.CENTER);
		slitMasksLabel.setHorizontalAlignment(JLabel.CENTER);
		optionsLabel.setHorizontalAlignment(JLabel.CENTER);
		
		//. action handlers for buttons
		goButton.setEnabled(MSCGUIParameters.ONLINE_MODE);
		goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goButton_actionPerformed(e);
			}
		});
		abortButton.setEnabled(false);
		abortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				abortButton_actionPerformed(e);
			}
		});
		
		quitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				quitButton_actionPerformed(e);
			}
		});
		
		//. construct components
		arcsPanel.setLayout(new GridBagLayout());
		arcsPanel.add(arcsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 0, 5, 0), 0, 0));
		arcsPanel.add(arcsQuantityPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
		arcsPanel.add(arcsTable.getTableHeader(), new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));
		arcsPanel.add(arcsTable, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, defaultInsets, 0, 0));

		flatsPanel.setLayout(new GridBagLayout());
		flatsPanel.add(flatsLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 0, 5, 0), 0, 0));

		flatsPanel.add(flatsYLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
		flatsPanel.add(flatsYQuantityPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
		flatsPanel.add(flatsJLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
		flatsPanel.add(flatsJQuantityPanel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
		flatsPanel.add(flatsHLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
		flatsPanel.add(flatsHQuantityPanel, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
		flatsPanel.add(flatsKLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
		flatsPanel.add(flatsKQuantityPanel, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));

		slitMasksScrollPane.setViewportView(slitMasksTable);
		slitMasksPanel.setLayout(new BorderLayout(0,10));
		slitMasksPanel.add(slitMasksLabel, BorderLayout.NORTH);
		slitMasksPanel.add(slitMasksScrollPane, BorderLayout.CENTER);
		
		optionsPanel.setLayout(new BorderLayout());
		optionsPanel.add(optionsLabel, BorderLayout.NORTH);
		optionsPanel.add(endOfNightShutdownCheckBox, BorderLayout.CENTER);
		
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.add(arcsPanel,  new GridBagConstraints(0, 1, 3, 1, 10.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, largeInsets, 0, 0));
		mainPanel.add(flatsPanel,  new GridBagConstraints(0, 2, 3, 1, 10.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, largeInsets, 0, 0));
		mainPanel.add(slitMasksPanel,  new GridBagConstraints(0, 3, 3, 1, 10.0, 10.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, largeInsets, 0, 0));
		mainPanel.add(optionsPanel,  new GridBagConstraints(0, 4, 3, 1, 10.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, largeInsets, 0, 0));
		mainPanel.add(goButton,  new GridBagConstraints(0, 5, 1, 1, 10.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 20, 5, 20), 0, 0));
		mainPanel.add(abortButton,  new GridBagConstraints(1, 5, 1, 1, 10.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 20, 5, 20), 0, 0));
		mainPanel.add(quitButton,  new GridBagConstraints(2, 5, 1, 1, 10.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 20, 5, 20), 0, 0));
		
		contentPane = (JPanel)getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(mainPanel, BorderLayout.CENTER);
	}

	protected void slitMasksTableModel_tableChanged(TableModelEvent e) {
		updateSensitivity();
	}

	protected void slitMasksTable_selectionChanged(ListSelectionEvent e) {
		updateSensitivity();
	}

	private void updateSensitivity() {
		int selectedRow = slitMasksTable.getSelectedRow();
		if (selectedRow > -1) {
			boolean yOn = ((Boolean)(slitMasksTable.getValueAt(selectedRow, 1))).booleanValue();
			boolean jOn = ((Boolean)(slitMasksTable.getValueAt(selectedRow, 2))).booleanValue();
			boolean hOn = ((Boolean)(slitMasksTable.getValueAt(selectedRow, 3))).booleanValue();
			boolean kOn = ((Boolean)(slitMasksTable.getValueAt(selectedRow, 4))).booleanValue();
			flatsYQuantityPanel.setEnabled(yOn);
			flatsJQuantityPanel.setEnabled(jOn);
			flatsHQuantityPanel.setEnabled(hOn);
			flatsKQuantityPanel.setEnabled(kOn);
			arcsTable.yOn = yOn;
			arcsTable.jOn = jOn;
			arcsTable.hOn = hOn;
			arcsTable.kOn = kOn;
			arcsTable.repaint();
		
		}
		
	}
	
	protected void quitButton_actionPerformed(ActionEvent e) {
		//. unselect a row
		slitMasksTable.getSelectionModel().clearSelection();
		//. hide gui
		setVisible(false);
	}

	protected void goButton_actionPerformed(ActionEvent e) {
		try {
			//. run script
			executeScript();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Error running script: "+ex.getMessage(), "Error Running Script", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			JOptionPane.showMessageDialog(this, "Script interrupted: "+ex.getMessage(), "Error Running Script", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	protected void abortButton_actionPerformed(ActionEvent e) {
		try {
			//. run script
			long id = System.currentTimeMillis();
			String[] command = new String[4];
			int argnum=0;
			command[argnum++]=MSCGUIParameters.SCRIPT_ABORT.getAbsolutePath();
			command[argnum++]="-t";
			command[argnum++]=Long.toString(id);
			//. use abort after frame setting so that mosfire.aborting is set to 1
			//. but mds does not receive an abort
			command[argnum++]=Integer.toString(MosfireParameters.ABORT_TYPE_AFTER_FRAME_VALUE);
			ProcessInfo pi = new ProcessInfo(command, id);
			myProcessControl.execute(pi);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Error running script: "+ex.getMessage(), "Error Running Script", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			JOptionPane.showMessageDialog(this, "Script interrupted: "+ex.getMessage(), "Error Running Script", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	private void executeScript() throws IOException, InterruptedException {
		//. construct command from gui
		String[] command = constructCommand();
		//. create new process info
		ProcessInfo pi = new ProcessInfo(command, System.currentTimeMillis());
		//. create new dialog to show process output
		ProcessListenerDialog processDialog = new ProcessListenerDialog(this, pi, false);
		processDialog.setSize(MSCGUIParameters.DIM_CALIBRATION_PROCESS_DIALOG);
		Point location = this.getLocation();
		Dimension dim = this.getSize();
		//. put dialog next to this dialog
		processDialog.setLocation(location.x+dim.width, location.y);
		//. add listeners to the process, one for the dialog, one for this to know when script is done
		myProcessControl.addProcessListener(processDialog);
		myProcessControl.addProcessListener(this);
		//. run script
		myProcessControl.execute(pi);
		//. save process id
		currentCommandProcessID = pi.getIdNumber();
		setScriptRunning(true);
		//. show process monitoring dialog
		processDialog.setVisible(true);
	}
	private String[] constructCommand() {
		//. get selected masks
		//SlitConfiguration[] masks = getSelectedSlitMasks();
		int numMasks = slitMasksTable.getRowCount();
		
		String[] command = new String[34+numMasks*5];
		int ii=0;
		/*. 
		 * The command construct for calibrations changed drastically feb 28, 2013
		 * by wirth, lyke and other SAs.
		 * 
		 * Args:
    	 YNeonCount  = number of Ne exposures to acquire in Y band [int]
       YNeonTime   = exposures Time for Ne arcs in Y band [int]
       YArgonCount = number of Ar exposures to acquire in Y band [int]
       YArgonTime  = exposures Time for Ar arcs in Y band [int]
       YFlatCount  = number of Flats to acquire in Y band [int]
       YFlatLamp   = lamp to use for Y-band flats [string]
       YFlatTime   = exposure Time for Flats in Y band [int]
       YLampsOff   = flag indicating whether to acquire Lamps on/Lamps Off pair in Y band [bool]
       JNeonCount  = number of Ne exposures to acquire in J band [int]
       JNeonTime   = exposures Time for Ne arcs in J band [int]
       JArgonCount = number of Ar exposures to acquire in J band [int]
       JArgonTime  = exposures Time for Ar arcs in J band [int]
       JFlatCount  = number of Flats to acquire in J band [int]
       JFlatLamp   = lamp to use for J-band flats [string]
       JFlatTime   = exposure Time for Flats in J band [int]
       JLampsOff   = flag indicating whether to acquire Lamps on/Lamps Off pair in J band [bool]
       HNeonCount  = number of Ne exposures to acquire in H band [int]
       HNeonTime   = exposures Time for Ne arcs in H band [int]
       HArgonCount = number of Ar exposures to acquire in H band [int]
       HArgonTime  = exposures Time for Ar arcs in H band [int]
       HFlatCount  = number of Flats to acquire in H band [int]
       HFlatLamp   = lamp to use for H-band flats [string]
       HFlatTime   = exposure Time for Flats in H band [int]
       HLampsOff   = flag indicating whether to acquire Lamps on/Lamps Off pair in H band [bool]
       KNeonCount  = number of Ne exposures to acquire in K band [int]
       KNeonTime   = exposure Time for Ne arcs in K band [int]
       KArgonCount = number of Ar exposures to acquire in K band [int]
       KArgonTime  = exposures Time for Ar arc in K band [int]
       KFlatCount  = number of Flats to acquire in K band [int]
       KFlatLamp   = lamp to use for K-band flats [string]
       KFlatTime   = exposure Time for Flats in K band [int]
       KLampsOff   = flag indicating whether to acquire Lamps on/Lamps Off pair in K band [bool]
       Shutdown    = flag whether to shut down MOSFIRE after completion [bool]

       The following set of params is repeated N times (once per mask):
       MaskPathN   = path to mask N .xml file [string]
       YstatusN    = calibrate mask N in Y band? [bool]
       JstatusN    = calibrate mask N in J band? [bool]
       HstatusN    = calibrate mask N in H band? [bool]
       KstatusN    = calibrate mask N in K band? [bool]
      
     For now:

         [YJHK][Neon|Argon]Count will all have the same value
         [YJHK][Neon|Argon]Time will all have the same value (can be changed by user in GUI)
         [YJHK]FlatCount remain independent.  Default value should be 7 instead of 12
         [YJHK]FlatLamp can be ?spec?, ?im?, or ?mos?.  Currently should be ?mos?
         [YJHK]FlatTime remain independent.  New defaults for YJHK should be 28, 17, 12, 17 seconds respectively
         [YJHK]LampsOff are now parameters.  Defaults for YJHK should be 0, 0, 0, 1
		*/
		command[ii++] = MSCGUIParameters.SCRIPT_CALIBRATE_MASKS.getAbsolutePath();
		//. start Y: neon count, neon itime, argon count, argon itime, flats, lamp, itime, lampsoff
		command[ii++] = ((Boolean)(arcsTable.getValueAt(0, 1))).booleanValue() ? Integer.toString(arcsQuantityPanel.getQuantity()) : "0";
		command[ii++] = Integer.toString(arcsQuantityPanel.getExposureTime());
		command[ii++] = ((Boolean)(arcsTable.getValueAt(1, 1))).booleanValue() ? Integer.toString(arcsQuantityPanel.getQuantity()) : "0";
		command[ii++] = Integer.toString(arcsQuantityPanel.getExposureTime());
		command[ii++] = Integer.toString(flatsYQuantityPanel.getQuantity());
		command[ii++] = MSCGUIParameters.DEFAULT_FLATLAMP;
		command[ii++] = Integer.toString(flatsYQuantityPanel.getExposureTime());
		command[ii++] = ((Boolean)(flatsYQuantityPanel.getTakeLampsOff())).booleanValue() ? "1" : "0";
		
		//. start J: neon count, neon itime, argon count, argon itime, flats, lamp, itime, lampsoff
		command[ii++] = ((Boolean)(arcsTable.getValueAt(0, 2))).booleanValue() ? Integer.toString(arcsQuantityPanel.getQuantity()) : "0";
		command[ii++] = Integer.toString(arcsQuantityPanel.getExposureTime());
		command[ii++] = ((Boolean)(arcsTable.getValueAt(1, 2))).booleanValue() ? Integer.toString(arcsQuantityPanel.getQuantity()) : "0";
		command[ii++] = Integer.toString(arcsQuantityPanel.getExposureTime());
		command[ii++] = Integer.toString(flatsJQuantityPanel.getQuantity());
		command[ii++] = MSCGUIParameters.DEFAULT_FLATLAMP;
		command[ii++] = Integer.toString(flatsJQuantityPanel.getExposureTime());
		command[ii++] = ((Boolean)(flatsJQuantityPanel.getTakeLampsOff())).booleanValue() ? "1" : "0";
		
		
		//. start H: neon count, neon itime, argon count, argon itime, flats, lamp, itime, lampsoff
		command[ii++] = ((Boolean)(arcsTable.getValueAt(0, 3))).booleanValue() ? Integer.toString(arcsQuantityPanel.getQuantity()) : "0";
		command[ii++] = Integer.toString(arcsQuantityPanel.getExposureTime());
		command[ii++] = ((Boolean)(arcsTable.getValueAt(1, 3))).booleanValue() ? Integer.toString(arcsQuantityPanel.getQuantity()) : "0";
		command[ii++] = Integer.toString(arcsQuantityPanel.getExposureTime());
		command[ii++] = Integer.toString(flatsHQuantityPanel.getQuantity());
		command[ii++] = MSCGUIParameters.DEFAULT_FLATLAMP;
		command[ii++] = Integer.toString(flatsHQuantityPanel.getExposureTime());
		command[ii++] = ((Boolean)(flatsHQuantityPanel.getTakeLampsOff())).booleanValue() ? "1" : "0";

		//. start K: neon count, neon itime, argon count, argon itime, flats, lamp, itime, lampsoff
		command[ii++] = ((Boolean)(arcsTable.getValueAt(0, 4))).booleanValue() ? Integer.toString(arcsQuantityPanel.getQuantity()) : "0";
		command[ii++] = Integer.toString(arcsQuantityPanel.getExposureTime());
		command[ii++] = ((Boolean)(arcsTable.getValueAt(1, 4))).booleanValue() ? Integer.toString(arcsQuantityPanel.getQuantity()) : "0";
		command[ii++] = Integer.toString(arcsQuantityPanel.getExposureTime());
		command[ii++] = Integer.toString(flatsKQuantityPanel.getQuantity());
		command[ii++] = MSCGUIParameters.DEFAULT_FLATLAMP;
		command[ii++] = Integer.toString(flatsKQuantityPanel.getExposureTime());
		command[ii++] = ((Boolean)(flatsKQuantityPanel.getTakeLampsOff())).booleanValue() ? "1" : "0";
		
		command[ii++] = endOfNightShutdownCheckBox.isSelected() ? "1" : "0";
//		command[ii++] = Integer.toString(numMasks);
		//. for now, just add mask names.  might want mask script filenames here		
		for (int jj=0; jj<numMasks; jj++) {
			command[ii++] = slitMasks.get(jj).getOriginalFilename();
			command[ii++] = ((Boolean)(slitMasksTable.getValueAt(jj, 1))).booleanValue() ? "1" : "0";
			command[ii++] = ((Boolean)(slitMasksTable.getValueAt(jj, 2))).booleanValue() ? "1" : "0";
			command[ii++] = ((Boolean)(slitMasksTable.getValueAt(jj, 3))).booleanValue() ? "1" : "0";
			command[ii++] = ((Boolean)(slitMasksTable.getValueAt(jj, 4))).booleanValue() ? "1" : "0";
		}
		return command;
	}
	private String[] constructCommandPre2013Feb28() {
		//. 2013-Feb-28: major change to take calibration script
		//. by wirth, lyke and other SAs.  this was the command construct
		//. before that change.
		
		//. get selected masks
		//SlitConfiguration[] masks = getSelectedSlitMasks();
		int numMasks = slitMasksTable.getRowCount();
		
		String[] command = new String[20+numMasks*5];
		int ii=0;
		//. put gui info at start of script
		command[ii++] = MSCGUIParameters.SCRIPT_CALIBRATE_MASKS.getAbsolutePath();
		command[ii++] = Integer.toString(arcsQuantityPanel.getQuantity());
		command[ii++] = ((Boolean)(arcsTable.getValueAt(0, 1))).booleanValue() ? "1" : "0";
		command[ii++] = ((Boolean)(arcsTable.getValueAt(0, 2))).booleanValue() ? "1" : "0";
		command[ii++] = ((Boolean)(arcsTable.getValueAt(0, 3))).booleanValue() ? "1" : "0";
		command[ii++] = ((Boolean)(arcsTable.getValueAt(0, 4))).booleanValue() ? "1" : "0";
		command[ii++] = ((Boolean)(arcsTable.getValueAt(1, 1))).booleanValue() ? "1" : "0";
		command[ii++] = ((Boolean)(arcsTable.getValueAt(1, 2))).booleanValue() ? "1" : "0";
		command[ii++] = ((Boolean)(arcsTable.getValueAt(1, 3))).booleanValue() ? "1" : "0";
		command[ii++] = ((Boolean)(arcsTable.getValueAt(1, 4))).booleanValue() ? "1" : "0";
		command[ii++] = Integer.toString(flatsYQuantityPanel.getQuantity());
		command[ii++] = Integer.toString(flatsYQuantityPanel.getExposureTime());
		command[ii++] = Integer.toString(flatsJQuantityPanel.getQuantity());
		command[ii++] = Integer.toString(flatsJQuantityPanel.getExposureTime());
		command[ii++] = Integer.toString(flatsHQuantityPanel.getQuantity());
		command[ii++] = Integer.toString(flatsHQuantityPanel.getExposureTime());
		command[ii++] = Integer.toString(flatsKQuantityPanel.getQuantity());
		command[ii++] = Integer.toString(flatsKQuantityPanel.getExposureTime());
		command[ii++] = endOfNightShutdownCheckBox.isSelected() ? "1" : "0";
		command[ii++] = Integer.toString(numMasks);
		//. for now, just add mask names.  might want mask script filenames here		
		for (int jj=0; jj<numMasks; jj++) {
			command[ii++] = slitMasks.get(jj).getOriginalFilename();
			command[ii++] = ((Boolean)(slitMasksTable.getValueAt(jj, 1))).booleanValue() ? "1" : "0";
			command[ii++] = ((Boolean)(slitMasksTable.getValueAt(jj, 2))).booleanValue() ? "1" : "0";
			command[ii++] = ((Boolean)(slitMasksTable.getValueAt(jj, 3))).booleanValue() ? "1" : "0";
			command[ii++] = ((Boolean)(slitMasksTable.getValueAt(jj, 4))).booleanValue() ? "1" : "0";
		}
		return command;
	}
	public void processErrMessage(ProcessInfo process, String message) {
		logger.error(message);
	}
	public void processOutMessage(ProcessInfo process, String message) {
		logger.debug(message);
	}

	public void processExitCode(ProcessInfo process, int code) {
		logger.debug("process "+process.getIdNumber()+" ("+process.getCommandString()+") exited with code "+code);
		processExited(process, code);
	}
	protected void processExited(ProcessInfo process, int code) {
		if (process.getIdNumber() == currentCommandProcessID) {
			setScriptRunning(false);
		}
	}
	public boolean isScriptRunning() {
		return scriptRunning;
	}
	private void setScriptRunning(boolean scriptRunning) {
		this.scriptRunning = scriptRunning;
		//. disable go and quit button is script is running
		goButton.setEnabled(!scriptRunning);
		abortButton.setEnabled(scriptRunning);
		quitButton.setEnabled(!scriptRunning);
	}


	private class QuantityPanel extends JPanel {
		JLabel quantityLabel = new JLabel("Quantity: ");
		JSpinner quantitySpinner = new JSpinner();
		JLabel exposureTimeLabel = new JLabel("Exposure time: ");
		JTextField exposureTimeField = new JTextField();
		JLabel secondsLabel = new JLabel("sec");
		JCheckBox lampsOffCheckBox = new JCheckBox("Take Lamps Off?");
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(12, 0, 99, 1);
		boolean addLampsOff = false;
		boolean defaultLampsOff = false;
		public QuantityPanel(int defaultItime, int defaultQuantity, boolean lampsOffCheck) {
			super();
			addLampsOff = true;
			defaultLampsOff = lampsOffCheck;
			construct(defaultItime, defaultQuantity);
		}
		public QuantityPanel(int defaultItime, int defaultQuantity) {
			super();
			construct(defaultItime, defaultQuantity);
		}
		private void construct(int defaultItime, int defaultQuantity) {
			spinnerModel.setValue(new Integer(defaultQuantity));
			quantitySpinner.setModel(spinnerModel);
			exposureTimeField.setText(Integer.toString(defaultItime));
			exposureTimeField.setPreferredSize(new Dimension(30, 20));
			exposureTimeField.setMinimumSize(new Dimension(30, 20));
			exposureTimeField.setHorizontalAlignment(JTextField.RIGHT);
			
			setLayout(new GridBagLayout());
			add(quantityLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
			add(quantitySpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2,2,2,15), 0, 0));
			if (defaultItime > 0) {
				add(exposureTimeLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
				add(exposureTimeField, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
				add(secondsLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
			}
			if (addLampsOff) {
				lampsOffCheckBox.setSelected(defaultLampsOff);
				add(lampsOffCheckBox, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
			}
		}
		public void setEnabled(boolean enabled) {
			quantityLabel.setEnabled(enabled);
			quantitySpinner.setEnabled(enabled);
			exposureTimeLabel.setEnabled(enabled);
			exposureTimeField.setEnabled(enabled);
			secondsLabel.setEnabled(enabled);
			lampsOffCheckBox.setEnabled(enabled);
		}
		public int getQuantity() {
			return ((Integer)(spinnerModel.getValue())).intValue();
		}
		public int getExposureTime() {
			return Integer.parseInt(exposureTimeField.getText());
		}
		public boolean getTakeLampsOff() {
			return lampsOffCheckBox.isSelected();
		}
	}
	
	public class ArcsTable extends JTable {
		public boolean yOn = true;
		public boolean jOn = true;
		public boolean hOn = true;
		public boolean kOn = true;
		private DefaultTableModel arcsTableModel;
		public ArcsTable() {
			super();
			arcsTableModel = new DefaultTableModel(new String[] {"Lamp", "Y", "J", "H", "K"}, 0){
				public Class<?> getColumnClass(int columnIndex) {
					if (columnIndex == 0) {
						return String.class;
					} else {
						return Boolean.class;
					}
				}				
			};
			setDefaultRenderer(Boolean.class, new ArcsTableCellRenderer());
			setDefaultEditor(Boolean.class, new ArcsTableCellEditor());
			arcsTableModel.addRow(new Object[] {"Neon", false, false, false, true});
			arcsTableModel.addRow(new Object[] {"Argon", false, false, false, true});
			setModel(arcsTableModel);

		}
		public class ArcsTableCellRenderer extends DefaultTableCellRenderer {
			public ArcsTableCellRenderer() {
				super();
				setHorizontalAlignment(SwingConstants.CENTER);
			}
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JCheckBox c = new JCheckBox();
				c.setHorizontalAlignment(SwingConstants.CENTER);
				c.setSelected(((Boolean)(table.getModel().getValueAt(row, column))).booleanValue());
				switch (column) {
				case 1: c.setEnabled(yOn);
				break;
				case 2: c.setEnabled(jOn);
				break;
				case 3: c.setEnabled(hOn);
				break;
				case 4: c.setEnabled(kOn);
				break;
				default: c.setEnabled(true);
				}
				return c;
			}
		}
		public class ArcsTableCellEditor extends DefaultCellEditor {
			public ArcsTableCellEditor() {
				super(new JCheckBox());
			}
			public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
				Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
				((JCheckBox)c).setHorizontalAlignment(SwingConstants.CENTER);
//				System.out.println(row+","+column+"-"+yOn);
				switch (column) {
				case 1: c.setEnabled(yOn);
				break;
				case 2: c.setEnabled(jOn);
				break;
				case 3: c.setEnabled(hOn);
				break;
				case 4: c.setEnabled(kOn);
				break;
				default: c.setEnabled(true);
				break;
				}
				return c;
			}
		}
	}
	
	public static void main(String[] args) {
		//. main for testing
		CalibrationScriptFrame f = new CalibrationScriptFrame(null);
		f.setSize(MSCGUIParameters.DIM_CALIBRATION_GUI);
		f.validate();
		f.setVisible(true);
		ArrayList<SlitConfiguration> testMasks = new ArrayList<SlitConfiguration>();
		for (int ii=0; ii<20; ii++) {
			testMasks.add(new SlitConfiguration("testMask "+Integer.toString(ii)));
		}
		f.setSlitMasks(testMasks);
	}

}
