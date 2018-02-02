package edu.ucla.astro.irlab.mosfire.mscgui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import edu.ucla.astro.irlab.mosfire.util.SlitConfiguration;
import edu.ucla.astro.irlab.util.process.ProcessControl;
import edu.ucla.astro.irlab.util.process.ProcessInfo;
import edu.ucla.astro.irlab.util.process.ProcessListener;
import edu.ucla.astro.irlab.util.process.ProcessListenerDialog;

public class CalibrationScriptFrame extends JFrame implements ProcessListener {
	private static final Logger logger = Logger.getLogger(CalibrationScriptFrame.class);
	
  protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  private JPanel contentPane;
  private JPanel mainPanel = new JPanel();
  private JPanel arcsPanel = new JPanel();
  private JPanel flatsPanel = new JPanel();
  private JPanel slitMasksPanel = new JPanel();
  private JPanel optionsPanel = new JPanel();
	
  private Insets defaultInsets = new Insets(2,2,2,2);
  private Insets largeInsets = new Insets(5,5,5,5);
	
  private JLabel arcsLabel = new JLabel("ARCS");
  private QuantityPanel arcsQuantityPanel = new QuantityPanel(MSCGUIParameters.DEFAULT_ITIME_SEC_ARCS);
  private JLabel selectLampsLabel = new JLabel("Select lamps: ");
  private JCheckBox neonCheckBox = new JCheckBox("Neon");
  private JCheckBox argonCheckBox = new JCheckBox("Argon");
	
  private JLabel flatsLabel = new JLabel("FLATS");
  private QuantityPanel flatsQuantityPanel = new QuantityPanel(MSCGUIParameters.DEFAULT_ITIME_SEC_FLATS);
	
  private JLabel slitMasksLabel = new JLabel("SLITMASKS");
  private JScrollPane slitMasksScrollPane = new JScrollPane();
//  private CheckBoxList slitMasksList = new CheckBoxList();
  private JTable slitMasksTable = new JTable();
  
  private JLabel optionsLabel = new JLabel("OPTIONS");
  private JCheckBox endOfNightShutdownCheckBox = new JCheckBox("Do end-of-night shutdown when done?");
	
  private JButton goButton = new JButton("GO");
  private JButton quitButton = new JButton("QUIT");

//  private Object[][] dummySlitMaskTableData = new Object[][] {{"", new Boolean(true), new Boolean(true), new Boolean(true), new Boolean(true)}};
  private DefaultTableModel slitMasksTableModel;
//  private DefaultListModel slitMasksListModel = new DefaultListModel();
  private ProcessControl myProcessControl = new ProcessControl();

  private ArrayList<SlitConfiguration> slitMasks = new ArrayList<SlitConfiguration>();
	private boolean scriptRunning;
	private long currentCommandProcessID;

	public CalibrationScriptFrame() throws HeadlessException {
		super(MSCGUIParameters.CALIBRATION_GUI_TITLE);
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
	/*
	private SlitConfiguration[] getSelectedSlitMasks() {
		ArrayList<SlitConfiguration> list = new ArrayList<SlitConfiguration>();
		int itemCount=0;
		//. go throw check boxes in slitMasksModel and look for ones that are checked
		for (int ii=0; ii<slitMasksListModel.getSize(); ii++) {
			JCheckBox item = (JCheckBox)(slitMasksListModel.get(ii));
			if (item.isSelected()) {
				//. if checked, add to output list
				list.add(slitMasks.get(ii));
				itemCount++;
			}
		}
		//. convert ArrayList to array
		SlitConfiguration[] strArray = new SlitConfiguration[itemCount];
		return list.toArray(strArray);
	}
	*/
	private void initGUI() {
		slitMasksTableModel = new DefaultTableModel(new String[] {"Mask Name", "Y", "J", "H", "K"}, 0) {
			public Class getColumnClass(int columnIndex) {
				if (columnIndex == 0) {
					return String.class;
				} else {
					return Boolean.class;
				}
			}
		};
		slitMasksTable.setModel(slitMasksTableModel);

		//. set widths for filter columns
		slitMasksTable.getColumnModel().getColumn(1).setMaxWidth(MSCGUIParameters.WIDTH_SPECTRAL_CALIBRATION_FILTER_COLUMN);
		slitMasksTable.getColumnModel().getColumn(2).setMaxWidth(MSCGUIParameters.WIDTH_SPECTRAL_CALIBRATION_FILTER_COLUMN);
		slitMasksTable.getColumnModel().getColumn(3).setMaxWidth(MSCGUIParameters.WIDTH_SPECTRAL_CALIBRATION_FILTER_COLUMN);
		slitMasksTable.getColumnModel().getColumn(4).setMaxWidth(MSCGUIParameters.WIDTH_SPECTRAL_CALIBRATION_FILTER_COLUMN);
		
		//. set default value of lamps to on
		neonCheckBox.setSelected(true);
		argonCheckBox.setSelected(true);
		
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
		
		quitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				quitButton_actionPerformed(e);
			}
		});
		
		//. construct components
		arcsPanel.setLayout(new GridBagLayout());
		arcsPanel.add(arcsLabel, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 0, 5, 0), 0, 0));
		arcsPanel.add(arcsQuantityPanel, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
		arcsPanel.add(selectLampsLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
		arcsPanel.add(neonCheckBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
		arcsPanel.add(argonCheckBox, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));

		flatsPanel.setLayout(new GridBagLayout());
		flatsPanel.add(flatsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 0, 5, 0), 0, 0));
		flatsPanel.add(flatsQuantityPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));

		slitMasksScrollPane.setViewportView(slitMasksTable);
		slitMasksPanel.setLayout(new BorderLayout(0,10));
		slitMasksPanel.add(slitMasksLabel, BorderLayout.NORTH);
		slitMasksPanel.add(slitMasksScrollPane, BorderLayout.CENTER);
		
		optionsPanel.setLayout(new BorderLayout());
		optionsPanel.add(optionsLabel, BorderLayout.NORTH);
		optionsPanel.add(endOfNightShutdownCheckBox, BorderLayout.CENTER);
		
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.add(arcsPanel,  new GridBagConstraints(0, 1, 2, 1, 10.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, largeInsets, 0, 0));
		mainPanel.add(flatsPanel,  new GridBagConstraints(0, 2, 2, 1, 10.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, largeInsets, 0, 0));
		mainPanel.add(slitMasksPanel,  new GridBagConstraints(0, 3, 2, 1, 10.0, 10.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, largeInsets, 0, 0));
		mainPanel.add(optionsPanel,  new GridBagConstraints(0, 4, 2, 1, 10.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, largeInsets, 0, 0));
		mainPanel.add(goButton,  new GridBagConstraints(0, 5, 1, 1, 10.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 20, 5, 20), 0, 0));
		mainPanel.add(quitButton,  new GridBagConstraints(1, 5, 1, 1, 10.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 20, 5, 20), 0, 0));
		
		contentPane = (JPanel)getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(mainPanel, BorderLayout.CENTER);
	}

	protected void quitButton_actionPerformed(ActionEvent e) {
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
	private void executeScript() throws IOException, InterruptedException {
		//. construct command from gui
		String[] command = constructCommand();
		//. create new process info
		ProcessInfo pi = new ProcessInfo(command, System.currentTimeMillis());
		//. create new dialog to show process output
		ProcessListenerDialog processDialog = new ProcessListenerDialog(this, pi, true);
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
		
		String[] command = new String[9+numMasks*5];
		int ii=0;
		//. put gui info at start of script
		command[ii++] = MSCGUIParameters.SCRIPT_CALIBRATE_MASKS.getAbsolutePath();
		command[ii++] = Integer.toString(arcsQuantityPanel.getQuantity());
		command[ii++] = Integer.toString(arcsQuantityPanel.getExposureTime());
		command[ii++] = neonCheckBox.isSelected() ? "1" : "0";
		command[ii++] = argonCheckBox.isSelected() ? "1" : "0";
		command[ii++] = Integer.toString(flatsQuantityPanel.getQuantity());
		command[ii++] = Integer.toString(flatsQuantityPanel.getExposureTime());
		command[ii++] = endOfNightShutdownCheckBox.isSelected() ? "1" : "0";
		command[ii++] = Integer.toString(numMasks);
		//. for now, just add mask names.  might want mask script filenames here		
		for (int jj=0; jj<numMasks; jj++) {
			command[ii++] = slitMasksTable.getValueAt(jj, 0).toString();
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
		//. TODO need abort ability?
		goButton.setEnabled(!scriptRunning);
		quitButton.setEnabled(!scriptRunning);
	}

	private class CheckBoxList extends JList {
		//. code from http://www.devx.com/tips/Tip/5342 -- Trevor Harmon
		public CheckBoxList() {
			setCellRenderer(new CellRenderer());

			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					int index = locationToIndex(e.getPoint());

					if (index != -1) {
						JCheckBox checkbox = (JCheckBox)getModel().getElementAt(index);
						checkbox.setSelected(!checkbox.isSelected());
						repaint();
					}
				}
			});

			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}

		protected class CellRenderer implements ListCellRenderer
		{
			public Component getListCellRendererComponent(
					JList list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				JCheckBox checkbox = (JCheckBox) value;
				checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
				checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
				checkbox.setEnabled(isEnabled());
				checkbox.setFont(getFont());
				checkbox.setFocusPainted(false);
				checkbox.setBorderPainted(true);
				checkbox.setBorder(isSelected ?	UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
				return checkbox;
			}
		}
	}
	private class QuantityPanel extends JPanel {
		JLabel quantityLabel = new JLabel("Quantity: ");
		JSpinner quantitySpinner = new JSpinner();
		JLabel exposureTimeLabel = new JLabel("Exposure time: ");
		JTextField exposureTimeField = new JTextField();
		JLabel secondsLabel = new JLabel("sec");
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 0, 9, 1);
		public QuantityPanel(int defaultItime) {
			super();
			quantitySpinner.setModel(spinnerModel);
			exposureTimeField.setText(Integer.toString(defaultItime));
			exposureTimeField.setPreferredSize(new Dimension(30, 20));
			exposureTimeField.setHorizontalAlignment(JTextField.RIGHT);
			
			setLayout(new GridBagLayout());
			add(quantityLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
			add(quantitySpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2,2,2,15), 0, 0));
			add(exposureTimeLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
			add(exposureTimeField, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
			add(secondsLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, defaultInsets, 0, 0));
		}
		public int getQuantity() {
			return ((Integer)(spinnerModel.getNextValue())).intValue();
		}
		public int getExposureTime() {
			return Integer.parseInt(exposureTimeField.getText());
		}
	}
	
	public static void main(String[] args) {
		//. main for testing
		CalibrationScriptFrame f = new CalibrationScriptFrame();
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
