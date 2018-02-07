package edu.ucla.astro.irlab.mosfire.mscgui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.ucla.astro.irlab.mosfire.util.MascgenArguments;

public class MascgenOutputsPanel extends JPanel {
	//. mascgen output panel
	private JPanel mascgenOutputParamsPanel = new JPanel();
	private JLabel outputRootDirLabel = new JLabel("Output directory:");
	private JLabel outputRootDirValueLabel = new JLabel("");
	private JButton outputRootDirBrowseButton = new JButton("Browse...");
	private JCheckBox automaticOutputButton = new JCheckBox("Auto-name output files?");
	private JCheckBox autoOutputDirButton = new JCheckBox("Output to subdirectory with mask name?");
	private JLabel outputDirLabel = new JLabel("Output subdirectory:");
	private JTextField outputDirField = new JTextField("");
	private JButton outputDirBrowseButton = new JButton("Browse...");
	private JLabel mascgenParamsLabel = new JLabel("Mascgen Params:");
	private JTextField mascgenParamsField = new JTextField();
	private JLabel allTargetsLabel = new JLabel("All Targets:");
	private JTextField allTargetsField = new JTextField();
	private JLabel maskTargetsLabel = new JLabel("Mask Targets:");
	private JTextField maskTargetsField = new JTextField();
	private JLabel mscFileLabel = new JLabel("MSC:");
	private JTextField mscFileField = new JTextField();
	private JLabel maskScriptLabel = new JLabel("Mask Script:");
	private JTextField maskScriptField = new JTextField();
	private JLabel alignMaskScriptLabel = new JLabel("Alignment Mask Script:");
	private JTextField alignMaskScriptField = new JTextField();
	private JLabel slitListLabel = new JLabel("Slit List:");
	private JTextField slitListField = new JTextField();
	private JLabel ds9RegionsLabel = new JLabel("DS9 Regions:");
	private JTextField ds9RegionsField = new JTextField();
	private JLabel starListLabel = new JLabel("Keck Star List:");
	private JTextField starListField = new JTextField();
	private String maskName = MSCGUIParameters.DEFAULT_MASK_NAME;
	//////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M4 by Ji Man Sohn, UCLA 2016-2017 //
	private JLabel excessTargetsLabel = new JLabel("Excess Targets:");
	private JTextField excessTargetsField = new JTextField();
	//////////////////////////////////////////////////////////////////

	private JFileChooser outputRootDirFC = new JFileChooser();

	public MascgenOutputsPanel() {
		init();
	}
	private void init() {
		Insets defaultInsets = new Insets(2,2,2,2);
		int row=0;
		
		outputRootDirFC.setDialogTitle("Select Output Root Directory");
		outputRootDirFC.setDialogType(JFileChooser.OPEN_DIALOG);
		outputRootDirFC.setCurrentDirectory(MSCGUIParameters.DEFAULT_MASCGEN_OUTPUT_ROOT_DIRECTORY);
		outputRootDirFC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		outputRootDirBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputRootDirBrowseButton_actionPerformed(e);
			}
		});
		
		automaticOutputButton.setSelected(MSCGUIParameters.AUTOMATIC_OUTPUT_FORMAT);
		automaticOutputButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				automaticOutputButton_itemStateChanged(e);				
			}
		});
		toggleAutomaticOutputFormatting(MSCGUIParameters.AUTOMATIC_OUTPUT_FORMAT);
		

		autoOutputDirButton.setSelected(MSCGUIParameters.AUTOMATIC_OUTPUT_DIR);
		autoOutputDirButton.addItemListener(new ItemListener() {	
			public void itemStateChanged(ItemEvent e) {
				autoOutputDirButton_itemStateChanged(e);
			}
		});
		toggleAutomaticOutputDir(MSCGUIParameters.AUTOMATIC_OUTPUT_DIR);

		mascgenOutputParamsPanel.setBorder(BorderFactory.createEtchedBorder());

		this.setLayout(new GridBagLayout());
		row=0;
		this.add(outputRootDirLabel, new GridBagConstraints(0,row,2,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		this.add(outputRootDirBrowseButton, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		this.add(outputRootDirValueLabel, new GridBagConstraints(0,row,3,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		this.add(autoOutputDirButton, new GridBagConstraints(0,row,3,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		this.add(outputDirLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		this.add(outputDirField, new GridBagConstraints(1,row,2,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
//		this.add(outputDirBrowseButton, new GridBagConstraints(2,row,1,1,0.0,0.0, GridBagConstraints.WEST,
//				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		this.add(mascgenOutputParamsPanel, new GridBagConstraints(0,row,3,1,1.0,1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, defaultInsets, 0,0));
		
		mascgenOutputParamsPanel.setLayout(new GridBagLayout());
		row=0;
		mascgenOutputParamsPanel.add(automaticOutputButton, new GridBagConstraints(0,row,3,1,0.0,0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		row++;
		mascgenOutputParamsPanel.add(mascgenParamsLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenOutputParamsPanel.add(mascgenParamsField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenOutputParamsPanel.add(allTargetsLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenOutputParamsPanel.add(allTargetsField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenOutputParamsPanel.add(maskTargetsLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenOutputParamsPanel.add(maskTargetsField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		//////////////////////////////////////////////////////////////////
		// Part of MAGMA UPGRADE item M4 by Ji Man Sohn, UCLA 2016-2017 //
		mascgenOutputParamsPanel.add(excessTargetsLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenOutputParamsPanel.add(excessTargetsField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		//////////////////////////////////////////////////////////////////
		mascgenOutputParamsPanel.add(mscFileLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenOutputParamsPanel.add(mscFileField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenOutputParamsPanel.add(maskScriptLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenOutputParamsPanel.add(maskScriptField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenOutputParamsPanel.add(alignMaskScriptLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenOutputParamsPanel.add(alignMaskScriptField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenOutputParamsPanel.add(slitListLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenOutputParamsPanel.add(slitListField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenOutputParamsPanel.add(ds9RegionsLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
		GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenOutputParamsPanel.add(ds9RegionsField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));
		row++;
		mascgenOutputParamsPanel.add(starListLabel, new GridBagConstraints(0,row,1,1,0.0,0.0, GridBagConstraints.EAST,
		GridBagConstraints.NONE, defaultInsets, 0,0));
		mascgenOutputParamsPanel.add(starListField, new GridBagConstraints(1,row,1,1,1.0,0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, defaultInsets, 0,0));

	}
	protected void outputRootDirBrowseButton_actionPerformed(ActionEvent e) {
		if  (outputRootDirFC.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			outputRootDirValueLabel.setText(outputRootDirFC.getSelectedFile().getPath());
			//. TODO: check to see if file exists... create if not (ask).
		}

	}
	private void toggleAutomaticOutputDir(boolean state) {
		outputDirField.setEditable(!state);
		if (state) {
			outputDirField.setText(maskName);
		}
	}
	public void updateOutputParams(String maskName) {
		if (autoOutputDirButton.isSelected()) {
			outputDirField.setText(maskName);
		}
		if (automaticOutputButton.isSelected()) {
			mascgenParamsField.setText(maskName+".param");
			allTargetsField.setText(maskName+"_orig.coords");
			maskTargetsField.setText(maskName+".coords");
			//////////////////////////////////////////////////////////////////
			// Part of MAGMA UPGRADE item M4 by Ji Man Sohn, UCLA 2016-2017 //
			excessTargetsField.setText(maskName+"_excess.coords");
			//////////////////////////////////////////////////////////////////
			mscFileField.setText(maskName+".xml");
			maskScriptField.setText(maskName+"_BarPositions.csh");
			alignMaskScriptField.setText(maskName+"_AlignmentBarPositions.csh");
			slitListField.setText(maskName+"_SlitList.txt");
			ds9RegionsField.setText(maskName+"_SlitRegions.reg");
			starListField.setText(maskName+"_StarList.txt");			
		}
	}
	public void updateOutputParams(MascgenArguments args) {
		maskName = args.getMaskName();
		autoOutputDirButton.setSelected(args.isOutputSubdirectoryMaskName());
		automaticOutputButton.setSelected(args.isAutonameOutputFiles());
		outputRootDirValueLabel.setText(args.getOutputDirectory());
		outputDirField.setText(args.getOutputSubdirectory());
		mascgenParamsField.setText(args.getOutputMascgenParams());
		allTargetsField.setText(args.getOutputAllTargets());
		maskTargetsField.setText(args.getOutputMaskTargets());
		//////////////////////////////////////////////////////////////////
		// Part of MAGMA UPGRADE item M4 by Ji Man Sohn, UCLA 2016-2017 //
		excessTargetsField.setText(args.getOutputExcessTargets());
		//////////////////////////////////////////////////////////////////
		mscFileField.setText(args.getOutputMSC());
		maskScriptField.setText(args.getOutputMaskScript());
		alignMaskScriptField.setText(args.getOutputAlignMaskScript());
		slitListField.setText(args.getOutputSlitList());
		ds9RegionsField.setText(args.getOutputDS9Regions());
		starListField.setText(args.getOutputStarList());			
	}

	private void toggleAutomaticOutputFormatting(boolean state) {
		mascgenParamsField.setEditable(!state);
		maskTargetsField.setEditable(!state);
		allTargetsField.setEditable(!state);
		//////////////////////////////////////////////////////////////////
		// Part of MAGMA UPGRADE item M4 by Ji Man Sohn, UCLA 2016-2017 //
		excessTargetsField.setEditable(!state);
		//////////////////////////////////////////////////////////////////
		mscFileField.setEditable(!state);
		maskScriptField.setEditable(!state);
		alignMaskScriptField.setEditable(!state);
		slitListField.setEditable(!state);
		ds9RegionsField.setEditable(!state);
		starListField.setEditable(!state);
		if (state) {
			updateOutputParams(maskName);
		}
	}
	protected void autoOutputDirButton_itemStateChanged(ItemEvent e) {
		toggleAutomaticOutputDir(e.getStateChange() == ItemEvent.SELECTED);
	}
	protected void automaticOutputButton_itemStateChanged(ItemEvent e) {
		toggleAutomaticOutputFormatting(e.getStateChange() == ItemEvent.SELECTED);
	}
	public void fillMascgenArgrumentsWithOutputs(MascgenArguments args) {
		
		args.setOutputDirectory(outputRootDirValueLabel.getText());
		args.setOutputSubdirectory(outputDirField.getText().trim());
		args.setOutputSubdirectoryMaskName(autoOutputDirButton.isSelected());
		args.setAutonameOutputFiles(automaticOutputButton.isSelected());
		args.setOutputMascgenParams(mascgenParamsField.getText().trim());
		args.setOutputAllTargets(allTargetsField.getText().trim());
		//////////////////////////////////////////////////////////////////
		// Part of MAGMA UPGRADE item M4 by Ji Man Sohn, UCLA 2016-2017 //
		args.setOutputExcessTargets(excessTargetsField.getText().trim());
		//////////////////////////////////////////////////////////////////
		args.setOutputMaskTargets(maskTargetsField.getText().trim());
		args.setOutputMSC(mscFileField.getText().trim());
		args.setOutputMaskScript(maskScriptField.getText().trim());
		args.setOutputAlignMaskScript(alignMaskScriptField.getText().trim());
		args.setOutputSlitList(slitListField.getText().trim());
		args.setOutputDS9Regions(ds9RegionsField.getText().trim());
		args.setOutputStarList(starListField.getText().trim());
	}
	public void setOutputRootDir(String path) {
		outputRootDirValueLabel.setText(path);
	}
	public void setMaskName(String name) {
		maskName = name;
	}
}
