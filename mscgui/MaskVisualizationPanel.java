package edu.ucla.astro.irlab.mosfire.mscgui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.ucla.astro.irlab.mosfire.util.AstroObj;
import edu.ucla.astro.irlab.mosfire.util.MascgenArguments;
import edu.ucla.astro.irlab.mosfire.util.MascgenTransforms;
import edu.ucla.astro.irlab.mosfire.util.MechanicalSlit;
import edu.ucla.astro.irlab.mosfire.util.MosfireParameters;
import edu.ucla.astro.irlab.mosfire.util.RaDec;
import edu.ucla.astro.irlab.mosfire.util.ScienceSlit;
import edu.ucla.astro.irlab.mosfire.util.SlitConfiguration;
import edu.ucla.astro.irlab.util.ColorUtilities;
import edu.ucla.astro.irlab.util.InvalidValueException;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.*;

//. TODO compass rose

class MaskVisualizationPanel extends JPanel {

	private boolean showTargets = true;
	private boolean scaleTargetColorWithPriority = true;
	private boolean scaleTargetSizeWithPriority = true;
	private boolean showAllTargets = false;
	private boolean showColorScale = true;
	private int targetSizePercentageOfRow = MSCGUIParameters.DEFAULT_TARGET_SIZE_PERCENTAGE_OF_ROW;
	private JTabbedPane tabbedPane = new JTabbedPane();
	private JPanel maskPanel = new JPanel();
	private JPanel spectraPanel = new JPanel();
	private JPanel pointerInfoPanel = new JPanel();
	private JLabel rowLabel = new JLabel("Row: ");
	private JLabel rowValueLabel = new JLabel(" ");
	private JLabel xLabel = new JLabel("X: ");
	private JLabel xValueLabel = new JLabel(" ");
	private JLabel objectLabel = new JLabel("Target: ");
	private JLabel objectValueLabel = new JLabel(" ");
	private JLabel priorityLabel = new JLabel("Priority: ");
	private JLabel priorityValueLabel = new JLabel(" ");
	private JPanel showTargetsPanel = new JPanel();
	private JLabel showTargetsLabel = new JLabel("Plot Targets: ");
	private JButton showTargetsOptionsButton = new JButton("Options");
	private JRadioButton showNoTargetsButton = new JRadioButton("None");
	private JRadioButton showMaskTargetsButton = new JRadioButton("Mask");
	private JRadioButton showAllTargetsButton = new JRadioButton("All");
	private ButtonGroup showTargetsButtonGroup = new ButtonGroup();
	private JTextField wavelengthPlotField = new JTextField();
	private SpectraDrawComponent spectraDraw = new SpectraDrawComponent();
	private MaskDrawComponent maskDraw = new MaskDrawComponent();
	private JButton addBtn;
	private JButton printBtn;
	private JComboBox filterComboBox;
	private JFileChooser printFileChooser = new JFileChooser();
	private double xLimit;
	private double xCenter;
	private int activeRow=-1;
	private String filter = "K";
	private String maskName;
	private String pa;
	private Font scaleFont = MSCGUIParameters.FONT_MASK_COLOR_SCALE;
	private SlitConfiguration config;
	private Collection<AstroObj> allObjects = new ArrayList<AstroObj>();
	private int colorScaleMode = MSCGUIParameters.DEFAULT_COLOR_SCALE_MODE;
	private Color targetColor = MSCGUIParameters.DEFAULT_COLOR_TARGET;
	private PlotTargetsOptionsPanel targetsOptionsPanel = new PlotTargetsOptionsPanel();
	
	private BufferedImage zoomImage = new BufferedImage(100,100, BufferedImage.TYPE_INT_RGB);
	private JPanel zoomPanel = new JPanel();
	
	public MaskVisualizationPanel() {
		xLimit=0.0;
		xCenter=0.0;
		
		config = new SlitConfiguration();
		jbInit();
	}
	private void jbInit() {
		printFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		
		// Add a panel for printing the current screen
		printBtn = new JButton("Print to PNG");
		printBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printButton_actionPerformed(e);
			}
		});


		rowValueLabel.setPreferredSize(new Dimension(25, 12));
		xValueLabel.setPreferredSize(new Dimension(60, 12));
		xValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		objectValueLabel.setPreferredSize(new Dimension(80, 12));
		priorityValueLabel.setPreferredSize(new Dimension(80, 12));
		showTargetsButtonGroup.add(showNoTargetsButton);
		showTargetsButtonGroup.add(showMaskTargetsButton);
		showTargetsButtonGroup.add(showAllTargetsButton);
		if (showAllTargets) {
			showAllTargetsButton.setSelected(true);
		} else if (showTargets) {
			showMaskTargetsButton.setSelected(true);
		} else {
			showNoTargetsButton.setSelected(true);
		}
		showTargetsOptionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showTargetsOptionsButton_actionPerformed(e);
			}
		});
		Insets showTargetsInsets = new Insets(2,5,2,5);
		showTargetsPanel.setLayout(new GridBagLayout());
		showTargetsPanel.add(showTargetsLabel, new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, showTargetsInsets, 0, 0));
		showTargetsPanel.add(showNoTargetsButton, new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, showTargetsInsets, 0, 0));
		showTargetsPanel.add(showMaskTargetsButton, new GridBagConstraints(2,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, showTargetsInsets, 0, 0));
		showTargetsPanel.add(showAllTargetsButton, new GridBagConstraints(3,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, showTargetsInsets, 0, 0));
		showTargetsPanel.add(showTargetsOptionsButton, new GridBagConstraints(4,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, showTargetsInsets, 0, 0));
		
		pointerInfoPanel.setLayout(new GridBagLayout());
		pointerInfoPanel.add(rowLabel, new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, showTargetsInsets, 0, 0));
		pointerInfoPanel.add(rowValueLabel, new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, showTargetsInsets, 0, 0));
		pointerInfoPanel.add(xLabel, new GridBagConstraints(2,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, showTargetsInsets, 0, 0));
		pointerInfoPanel.add(xValueLabel, new GridBagConstraints(3,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, showTargetsInsets, 0, 0));
		pointerInfoPanel.add(objectLabel, new GridBagConstraints(4,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, showTargetsInsets, 0, 0));
		pointerInfoPanel.add(objectValueLabel, new GridBagConstraints(5,0,1,1,5.0,0.0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, showTargetsInsets, 0, 0));
		pointerInfoPanel.add(priorityLabel, new GridBagConstraints(6,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, showTargetsInsets, 0, 0));
		pointerInfoPanel.add(priorityValueLabel, new GridBagConstraints(7,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, showTargetsInsets, 0, 0));
//		pointerInfoPanel.add(showTargetsPanel, new GridBagConstraints(0,1,8,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, showTargetsInsets, 0, 0));
/*
		FlowLayout pointerInfoPanelLayout = new FlowLayout();
		pointerInfoPanelLayout.setAlignOnBaseline(true);
		pointerInfoPanel.setLayout(pointerInfoPanelLayout);
		pointerInfoPanel.add(rowLabel);
		pointerInfoPanel.add(rowValueLabel);
		pointerInfoPanel.add(xLabel);
		pointerInfoPanel.add(xValueLabel);
		pointerInfoPanel.add(objectLabel);
		pointerInfoPanel.add(objectValueLabel);
		pointerInfoPanel.add(priorityLabel);
		pointerInfoPanel.add(priorityValueLabel);
		pointerInfoPanel.add(showTargetsPanel);
	*/	
		maskPanel.setLayout(new BorderLayout());
		maskPanel.add(maskDraw, BorderLayout.CENTER);
		maskPanel.add(pointerInfoPanel, BorderLayout.NORTH);
		maskPanel.add(showTargetsPanel, BorderLayout.SOUTH);
		//
		// Add a combo box to select the filter.
		filterComboBox = new JComboBox();
		filterComboBox.setEditable(false);
		filterComboBox.addItem("K");
		filterComboBox.addItem("Ks");
		filterComboBox.addItem("H");
		filterComboBox.addItem("J");
		filterComboBox.addItem("Y");

		filterComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filterComboBox_actionPerformed();
			}
		}
		);

		wavelengthPlotField.setText("#####");


		// panel for wavelength plotting
		JPanel waveLengthPlotPanel = new JPanel();
		waveLengthPlotPanel.setLayout(new GridBagLayout());

		// Add a panel for adding a wavelength marker
		addBtn = new JButton("Add Wavelength Marker");
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addMarkerBtn_actionPerformed(e);
			}
		});

		waveLengthPlotPanel.add(new Label("Wavelength to plot:"), 
				new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		waveLengthPlotPanel.add(wavelengthPlotField, 
				new GridBagConstraints(1,0,1,1,1.0,0.0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		waveLengthPlotPanel.add(new Label("Angstroms"), 
				new GridBagConstraints(2,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		waveLengthPlotPanel.add(addBtn, 
				new GridBagConstraints(3,0,1,1,0.0,0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));




		spectraPanel.setLayout(new BorderLayout());
		spectraPanel.add(filterComboBox, BorderLayout.NORTH);
		spectraPanel.add(spectraDraw, BorderLayout.CENTER);
		spectraPanel.add(waveLengthPlotPanel, BorderLayout.SOUTH);


		tabbedPane.add("Slit Mask", maskPanel);
		tabbedPane.add("Spectral Format", spectraPanel);

		
		setLayout(new BorderLayout());
		add(printBtn, BorderLayout.NORTH);
		add(tabbedPane, BorderLayout.CENTER);
		
		/* test window for zooming 
		JFrame frame = new JFrame();
		zoomPanel.setPreferredSize(new Dimension(100,100));
		JPanel frameContent = (JPanel)frame.getContentPane();
		frameContent.setLayout(new BorderLayout());
		frameContent.add(zoomPanel, BorderLayout.CENTER);
		frame.validate();
		frame.setVisible(true);
		*/
		
	}
	protected void showTargetsOptionsButton_actionPerformed(ActionEvent e) {
		targetsOptionsPanel.setScaleTargetColorWithPriority(scaleTargetColorWithPriority);
		targetsOptionsPanel.setScaleTargetSizeWithPriority(scaleTargetSizeWithPriority);
		targetsOptionsPanel.setColorScaleMode(colorScaleMode);
		targetsOptionsPanel.setTargetSize(targetSizePercentageOfRow);
		int answer = JOptionPane.showConfirmDialog(this, targetsOptionsPanel, "Plot Targets Options", JOptionPane.OK_CANCEL_OPTION);
		if (answer == JOptionPane.OK_OPTION) {
			targetSizePercentageOfRow = targetsOptionsPanel.getTargetSize();
			scaleTargetSizeWithPriority = targetsOptionsPanel.getScaleTargetSizeWithPriority();
			scaleTargetColorWithPriority = targetsOptionsPanel.getScaleTargetColorWithPriority();
			if (!scaleTargetColorWithPriority) {
				targetColor = targetsOptionsPanel.getConstantColor();
			} else {
				colorScaleMode = targetsOptionsPanel.getColorScaleMode();
			}
		}
		repaint();
	}
	public void addMouseListener(MouseListener l) {
		maskDraw.addMouseListener(l);
		spectraDraw.addMouseListener(l);
	}
	protected void printButton_actionPerformed(ActionEvent e) {
		printImage();
	}
	private void printImage() {
	//. get current selected view
		if (tabbedPane.getSelectedComponent() == maskPanel) {
			printFileChooser.setSelectedFile(new File(config.getMaskName()+"_maskImage.png"));
		} else {
			printFileChooser.setSelectedFile(new File(config.getMaskName()+"_specImage_"+filter+".png"));			
		}
		if (printFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = printFileChooser.getSelectedFile();
			if (file.exists()) {
				int answer = JOptionPane.showConfirmDialog(this, "File exists.  Overwrite?", "File Exists", JOptionPane.YES_NO_CANCEL_OPTION);
				if (answer == JOptionPane.CANCEL_OPTION) {
					return;
				} else if (answer == JOptionPane.NO_OPTION) {
					printImage();
					return;
				}
			}
			try {
				if (tabbedPane.getSelectedComponent() == maskPanel) {
					printMaskImage(file);
				} else {
					printSpectraImage(file);
				}
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "I/O Error saving to PNG: "+ex.getMessage(), "Error Saving PNG", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	public void openNewConfiguration(SlitConfiguration newConfig) throws InvalidValueException {

		config = newConfig;
		MascgenArguments data = newConfig.getMascgenArgs();
		List<AstroObj> allTargets = config.getOriginalTargetList();
		allObjects.clear();
		if (allTargets != null) {
			setAllObjects(allTargets);
		}
		
		maskName = data.getMaskName();

		xLimit = data.getxRange()*60/CSU_ARCSEC_PER_MM;
		xCenter = data.getxCenter()*60/CSU_ARCSEC_PER_MM;

		// Some formatters
		java.text.DecimalFormat secondPlace = 
			new java.text.DecimalFormat("0.00");

		pa = secondPlace.format(newConfig.getMascgenResult().getPositionAngle());

		spectraDraw.setFilter(filter);
		spectraDraw.repaint();
		maskDraw.repaint();
		
		printFileChooser.setCurrentDirectory(new File(config.getMascgenArgs().getFullPathOutputSubdirectory()));
	}

	public void setActiveRow(int activeRow) {
		this.activeRow = activeRow;
	}
	public int getActiveRow() {
		return activeRow;
	}
	public void setShowTargets(boolean showTargets) {
		this.showTargets = showTargets;
	}
	public boolean doesShowTargets() {
		return showTargets;
	}
	public void setShowAllTargets(boolean showAllTargets) {
		this.showAllTargets = showAllTargets;
	}
	public boolean doesShowAllTargets() {
		return showAllTargets;
	}
	public void setScaleTargetColorWithPriority(boolean scaleTargetColorWithPriority) {
		this.scaleTargetColorWithPriority = scaleTargetColorWithPriority;
	}
	public boolean doesScaleTargetColorWithPriority() {
		return scaleTargetColorWithPriority;
	}
	public void setScaleTargetSizeWithPriority(boolean scaleTargetSizeWithPriority) {
		this.scaleTargetSizeWithPriority = scaleTargetSizeWithPriority;
	}
	public boolean doesScaleTargetSizeWithPriority() {
		return scaleTargetSizeWithPriority;
	}
	public int getRow(int verticalPixel) {
		if (tabbedPane.getSelectedComponent() == maskPanel) {
			return maskDraw.getRow(verticalPixel);
		} else {
			return spectraDraw.getRow(verticalPixel);
		}
	}
	private void filterComboBox_actionPerformed() {
		String oldFilter = spectraDraw.getFilter();

		//change the filter
		filter = (String) filterComboBox.getSelectedItem();

		try {
			spectraDraw.setFilter(filter);
			//update the drawing
			spectraDraw.repaint();
		} catch (InvalidValueException ex) {
			JOptionPane.showMessageDialog(this, "Error setting filter to "+filter+". Filter is invalid.", "Invalid Filter", JOptionPane.ERROR_MESSAGE);;
			ex.printStackTrace();
			filterComboBox.setSelectedItem(oldFilter);
		}

		//. TODO move?
		wavelengthPlotField.setText("#####");



	}


	//The method which is performed when someone clicks on the "Add Wavelength Marker" button
	private void addMarkerBtn_actionPerformed(ActionEvent e) {
		//read in the value in the text box
		String text = wavelengthPlotField.getText();

		Scanner fillIn = new Scanner(text);   
		ArrayList<Double> waves = new ArrayList<Double>();
		while (fillIn.hasNext()) {
			String stringJunk = fillIn.next();

			//convert it to a number
			try{
				double wave=Double.parseDouble(stringJunk);

				waves.add(wave);
			} catch (NumberFormatException e2){
				JOptionPane.showMessageDialog(this, "Error parsing wavelength <"+stringJunk+">.  Must be a space separated list of numbers (wavelength in Angstroms)", "Error Parsing Wavelength", JOptionPane.ERROR_MESSAGE);
			}	    	

		}

		double[] wave = new double[waves.size()];
		int ii=0;
		for (Double currentWave: waves) {
			wave[ii] = currentWave.doubleValue();
			ii++;
		}

		try {
			spectraDraw.setMarkerWavelength(wave);
			spectraDraw.setMarkedWavelenthString(text);
		} catch (InvalidValueException ex) {
			JOptionPane.showMessageDialog(this, "Error setting wavelength(s).  "+ex.getMessage(), "Error Parsing Wavelength", JOptionPane.ERROR_MESSAGE);
		} 


		//update the drawing
		spectraDraw.repaint();

	}

	public void printMaskImage(File file) throws IOException {
		// Create an image to save

		RenderedImage maskPrint = maskDraw.makeMaskImage(); 
		// Write generated image to a file 
		// Save as PNG 
		ImageIO.write(maskPrint, "png", file); 
	}
	public void printSpectraImage(File file) throws IOException {
		// Create an image to save
		RenderedImage specPrint = spectraDraw.makeSpecImage(); 
		// Write generated image to a file 
		// Save as PNG 
		ImageIO.write(specPrint, "png", file); 
	}
	public void setAllObjects(Collection<AstroObj> objects) {
		allObjects = new ArrayList<AstroObj>(objects);
	}

	class SpectraDrawComponent extends JComponent {
		private String filter;
		private double[] markerWavelength;
		private double[] markedWaveFraction;
		private double guiSize;
		private double guiSpecLength;
		private double guiRatioPix;
		private double guiDetectorSize;
		private double guiCenterX;
		private double guiSpecCenterY;
		private double guiSlitHeightPix;
		private double guiOverlapPix;
		private String markedWavelengthString = "";
		
		// local variables to save the minimum and maximum expected wavelegths
		//given the filter choice
		private double minLam;
		private double maxLam;
		private double shift;
		private double minBand;
		private double maxBand;
		// The length of a spectrum in pixels
		private double specLength;

		public SpectraDrawComponent() {
			super();
			markedWaveFraction = new double[0];
			markerWavelength = new double[0];
		}
		public void setMarkedWavelenthString(String text) {
			markedWavelengthString = text;
		}
		
		// Returns a JPEG-ready spectral image 
		public RenderedImage makeSpecImage() { 
			double oldGuiSize = this.guiSize;
			setSize(2000);

			int width = (int) Math.floor(guiSize); 
			int height = (int) Math.floor(guiSize); 
			// Create a buffered image in which to draw 
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
			// Create a graphics contents on the buffered image 
			Graphics2D g2d = bufferedImage.createGraphics(); 
			// Draw graphics

			// make lines not pixelized
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			Color backgroundColor = Color.white;

			Font font = new Font("Serif", Font.BOLD, 30);
			
			drawSpecView(g2d, backgroundColor, font);

			g2d.setColor(Color.BLACK);

			font = new Font("Serif", Font.BOLD, 50);
			g2d.setFont(font);
			
			String label = maskName+"    PA = "+pa;

			FontMetrics fm = g2d.getFontMetrics(font);
			Rectangle2D labelExtents = fm.getStringBounds(label, g2d);


			String markedWaves = markedWavelengthString.trim();
			double labelY;
			if (markedWaves.isEmpty()) {
				labelY = guiSize - (guiSize - guiDetectorSize)/4.;
			} else {
				labelY = guiSize - (guiSize - guiDetectorSize)/3.;
			}
			
			g2d.drawString(label, (float)(guiSize - labelExtents.getWidth())/2, (float)labelY);
			
			if (!markedWaves.isEmpty()) {
				font = new Font("Serif", 0, 40);
				g2d.setFont(font);
				label = "Marked Wavelengths: " + markedWaves;
				fm = g2d.getFontMetrics(font);
				labelExtents = fm.getStringBounds(label, g2d);
				labelY = guiSize - (guiSize - guiDetectorSize)/6.;
				g2d.drawString(label, (float)(guiSize - labelExtents.getWidth())/2, (float)labelY);
			}
				
			
			
			setSize(oldGuiSize);

			return bufferedImage;
		}

		public String getFilter() {
			return filter;
		}

		public void drawSpecView(Graphics2D g2, Color backgroundColor, 	Font f){
			java.text.DecimalFormat wholeNum = new java.text.DecimalFormat("0");
			//make the background white
			Rectangle2D fullGUI = new Rectangle2D.Double(0,0,guiSize,guiSize);
			g2.setPaint(backgroundColor);
			g2.draw(fullGUI);
			g2.fill(fullGUI);
			g2.setFont(f);

			// store the length of the spectrum
			guiSpecLength=specLength/guiRatioPix;

			//set the line color to green

			g2.setPaint(Color.blue.darker().darker());

			//Draw a large green rectangle to represent the detector

			// positionArray is feed the width, height, centerX, and centerY coordinates
			// and returns the lower left hand corner x and y, and the width and height
			// the values needed to generate a Graphics2D shape
			double[] loc = positionArray(guiDetectorSize, guiDetectorSize, guiCenterX, guiSpecCenterY);
			Rectangle2D csuBorder = new Rectangle2D.Double(loc[0],loc[1],loc[2],loc[3]);
			//g2.draw(csuBorder);
			g2.fill(csuBorder);

			
			
			//Draw some spectra
			g2.setStroke( new BasicStroke( 2.0f ) );
			int row;
			for (MechanicalSlit slitPos : config.getMechanicalSlitList()) {
				row=slitPos.getSlitNumber()-1;
				// set the values of the minimum, maximum, and central wavelength
				double minWave = ((slitPos.getLeftBarPositionInMM()+slitPos.getRightBarPositionInMM())/2 - CSU_ZERO_PT)*shift+minLam;
				double maxWave = ((slitPos.getLeftBarPositionInMM()+slitPos.getRightBarPositionInMM())/2 - CSU_ZERO_PT)*shift+maxLam;
				double centerWave = (minWave+maxWave)/2;
				if (minWave < minBand) {
					minWave = minBand;
				}
				if (maxWave > maxBand){
					maxWave = maxBand;
				}

				double centerFraction = (centerWave-minBand)/(maxBand-minBand);	


				// spectrumFractionMinX is fed the length of the spectrum and the centerFraction of the 
				// length where the line falls and returns the min x position of the rectangle in gui coordinates
				double xPos = spectrumFractionMinX(guiSpecLength, centerFraction);
				Rectangle2D slit = new Rectangle2D.Double(xPos,
						guiSpecCenterY+(row-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeightPix+guiOverlapPix)
						,guiSpecLength,guiSlitHeightPix+guiOverlapPix);

				//Fill the spectra with a color gradient in gray

				//Gradient needs lower x,y and higher x,y to work - give it the bounds of the spectrum
				GradientPaint gradient = new GradientPaint((int)xPos,
						(int) (guiSpecCenterY+(row-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeightPix+guiOverlapPix)),
						Color.DARK_GRAY,(int) (xPos+guiSpecLength),
						(int)(guiSpecCenterY+(row+1-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeightPix+guiOverlapPix))
						,Color.LIGHT_GRAY,true);
				g2.setPaint(gradient);
				g2.fill(slit);

				if (row == activeRow) {
					g2.setPaint(MosfireParameters.COLOR_SELECTED_SLIT);
					Rectangle2D box = new Rectangle2D.Double(xPos,
							guiSpecCenterY+(row-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeightPix+guiOverlapPix),
							guiSpecLength,
							guiSlitHeightPix+guiOverlapPix);
					g2.fill(box);
				}
				
				//outline in black
				g2.setPaint(Color.BLACK);
				g2.draw(slit);

				// And add the minimum and maximum wavelength covered
				//double xDouble = guiCenterX - guiDetectorSize/6;
				double xDouble = xPos + guiSpecLength/20; 
				if (xDouble < guiCenterX - guiDetectorSize/2) {
					xDouble = guiCenterX - guiDetectorSize/2 +5;
				}
				float xFloat = (float) xDouble;

				double yDouble = guiSpecCenterY+(row+0.8-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeightPix+guiOverlapPix);
				float yFloat = (float) yDouble;

				// Write out the minimum and maximum wavelengths
				g2.setPaint(Color.WHITE);
				g2.drawString(wholeNum.format(minWave), xFloat, yFloat);

				//xDouble = guiCenterX + guiDetectorSize/6;
				//	if (xDouble > xPos+guiSpecLength) {xDouble = xPos + guiSpecLength/10*9;} 
				xDouble = xPos + guiSpecLength/10*9;
				if (xDouble > guiCenterX + guiDetectorSize/2-guiSize/20) {
					xDouble = guiCenterX + guiDetectorSize/2-guiSize/20;
				}
				xFloat = (float) xDouble;
				g2.setPaint(Color.BLACK);
				g2.drawString(wholeNum.format(maxWave), xFloat, yFloat);

				xFloat = (float) guiSize/2;
				g2.drawString(slitPos.getTargetName(), xFloat, yFloat);



				// Draw the marked wavelength if there is one
				for (int ij=0; ij < markedWaveFraction.length; ij++){
					Rectangle2D marker = new Rectangle2D.Double(
							xPos+guiSpecLength*markedWaveFraction[ij],
							guiSpecCenterY+(row-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeightPix+guiOverlapPix)
							,4,guiSlitHeightPix+guiOverlapPix);

					if (xPos+guiSpecLength*markedWaveFraction[ij] < guiCenterX-guiDetectorSize/2 ||
							xPos+guiSpecLength*markedWaveFraction[ij] > guiCenterX+guiDetectorSize/2){

						g2.setPaint(Color.RED);
					}
					else{
						g2.setPaint(Color.GREEN);	
					}

					g2.fill(marker);	
				}
			}

		}		

		public void paint(Graphics g){
			Graphics2D g2 = (Graphics2D)g;

			//Set the line thickness to quite thick
			g2.setStroke( new BasicStroke( 5.0f ) );

			// make lines not pixelized
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			Dimension dim = getSize();

			if (dim.height < dim.width) {
				setSize((double)dim.height);
			} else {
				setSize((double)dim.width);
			}

			Color backgroundColor = Color.black;

			Font font = new Font("Serif", Font.BOLD, 12);

			drawSpecView(g2, backgroundColor, font);
		}
		private void setSize(double size) {
			this.guiSize = size;

			// make variables scaled into GUI coordinates where the scaling is done such that
			// the focal place circle is just enclosed by the GUI window
			this.guiCenterX = guiSize / 2;

			// a second scale used for the Spectral view format
			this.guiRatioPix = DETECTOR_USABLE_PIXELS * 15/12 / guiSize;
			this.guiDetectorSize = DETECTOR_USABLE_PIXELS / guiRatioPix;


			this.guiSpecCenterY = guiSize / 2; // - guiSize/25;

			double slitPlusOverlap = DETECTOR_USABLE_PIXELS/CSU_NUMBER_OF_BAR_PAIRS / guiRatioPix;
			double slitFraction = SINGLE_SLIT_HEIGHT_MM/(SINGLE_SLIT_HEIGHT_MM+OVERLAP_MM);
			double overlapFraction = OVERLAP_MM/(SINGLE_SLIT_HEIGHT_MM+OVERLAP_MM);

			this.guiSlitHeightPix = slitFraction * slitPlusOverlap;
			this.guiOverlapPix = overlapFraction * slitPlusOverlap;



		}

		/** Return the bar left position in arcsec. **/
		public void setFilter(String filter) throws InvalidValueException {
			this.filter = filter;
			setMarkerWavelength(new double[0]);

			// decide which wavelength and shift values should be used
			// based off which filter is selected
			if (this.filter.compareToIgnoreCase("K") == 0){
				minLam = MIN_LAM_K;
				maxLam = MAX_LAM_K;
				shift = SHIFT_K;
				minBand = MIN_BAND_WIDTH_K;
				maxBand = MAX_BAND_WIDTH_K;
				specLength = (maxBand - minBand)/DISPERSION_K_KS;
			}
			else if (this.filter.compareToIgnoreCase("Ks") == 0){
				minLam = MIN_LAM_KS;
				maxLam = MAX_LAM_KS;
				shift = SHIFT_KS;
				minBand = MIN_BAND_WIDTH_KS;
				maxBand = MAX_BAND_WIDTH_KS;
				specLength = (maxBand - minBand)/DISPERSION_K_KS;
			}
			else if (this.filter.compareToIgnoreCase("H") == 0){
				minLam = MIN_LAM_H;
				maxLam = MAX_LAM_H;
				shift = SHIFT_H;
				minBand = MIN_BAND_WIDTH_H;
				maxBand = MAX_BAND_WIDTH_H;
				specLength = (maxBand - minBand)/DISPERSION_H;
			}
			else if (this.filter.compareToIgnoreCase("J") == 0){
				minLam = MIN_LAM_J;
				maxLam = MAX_LAM_J;
				shift = SHIFT_J;
				minBand = MIN_BAND_WIDTH_J;
				maxBand = MAX_BAND_WIDTH_J;
				specLength = (maxBand - minBand)/DISPERSION_J;
			}
			else if (this.filter.compareToIgnoreCase("Y") == 0){
				minLam = MIN_LAM_Y;
				maxLam = MAX_LAM_Y;
				shift = SHIFT_Y;
				minBand = MIN_BAND_WIDTH_Y;
				maxBand = MAX_BAND_WIDTH_Y;
				specLength = (maxBand - minBand)/DISPERSION_Y;
			} else {
				throw new InvalidValueException("Invalid filter.  Must be K, Ks, H, J, or Y");
			}
		}

		public void setMarkerWavelength(double[] wave) throws InvalidValueException{


			for (int i=0; i < wave.length; i++){
				if ((wave[i] < minBand) || (wave[i] > maxBand)) {
					throw new InvalidValueException("Wavelength "+wave[i]+" is not within range of filter "+filter+" ("+minBand+" to "+maxBand+" \u212b).");
				}
			}

			markerWavelength = wave;

			//the xposition of the marker is the location of the left 
			// edge of the spectrum (band), xPos, plus, the bandLength
			// times the bandFraction of the marked wavelength
			markedWaveFraction = new double[markerWavelength.length];

			for (int ii=0; ii < markedWaveFraction.length; ii++){
				markedWaveFraction[ii] = (this.markerWavelength[ii] - minBand)/(maxBand-minBand);
			}



		}
		//produces the minimum x location of the bar from width of a spectrum
		//and the fraction of the bandwidth that falls at the zero line of the detector
		public double spectrumFractionMinX(double width, double bandFraction){
			return guiCenterX-width*bandFraction;

		}
		
		public int getRow(int verticalPixel) {
			//. (guiSpecCenterY+(row-CSU_NUM_BAR_PAIRS/2)*(guiSlitHeightPix+guiOverlapPix))
			//. 0-based
			double topBorder = guiSpecCenterY - (CSU_NUMBER_OF_BAR_PAIRS/2 * (guiSlitHeightPix+guiOverlapPix));
			double bottomBorder = guiSpecCenterY + (CSU_NUMBER_OF_BAR_PAIRS/2 * (guiSlitHeightPix+guiOverlapPix));
			if (verticalPixel < topBorder) {
				return -1;
			}
			if (verticalPixel > bottomBorder) {
				return -1;
			}
			return (int)Math.floor((verticalPixel-topBorder)/((guiSlitHeightPix+guiOverlapPix)));
		}


	}  //. end SpectraDrawComponent inner class



	class MaskDrawComponent extends JComponent implements MouseMotionListener  {
		private double guiSize;
		private double guiCSUHeight;
		private double guiCenterX;
		private double guiCenterY;
		private double guiFocalPlane;
		private double guiXLimit;
		private double guiXCenter;
		private double guiRatio;
		private double guiSlitHeight;
		private double guiOverlap;
		private DecimalFormat csuXFormatter = new DecimalFormat("0.00\"");
		private HashSet<TargetShape> targetShapes = new HashSet<TargetShape>();
		private AstroObj currentTarget;
		public MaskDrawComponent() {
			super();
			addMouseMotionListener(this);
			showNoTargetsButton.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					handleShowTargetsButtons(e);
				}
			});
			showMaskTargetsButton.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					handleShowTargetsButtons(e);
				}
			});
			showAllTargetsButton.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					handleShowTargetsButtons(e);
				}
			});
		}
		public void paint(Graphics g){
			Graphics2D g2 = (Graphics2D)g;

			//Set the line thickness to quite thick
			g2.setStroke( new BasicStroke( 5.0f ) );

			// make lines not pixelized
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			Dimension dim = getSize();

			if (dim.height < dim.width) {
				setSize((double)dim.height);
			} else {
				setSize((double)dim.width);
			}

//			Color backgroundColor = new Color(100,100,125);
			Color backgroundColor = new Color(150,150,175);
			Color csuEdgeColor = Color.black;
			Color barColor = Color.white;
			Color legalColor = Color.red;
			Color focalCircleColor = Color.lightGray;
			Font font = new Font("Serif", Font.BOLD, 12);

			drawMaskView(g2, backgroundColor, csuEdgeColor, barColor, legalColor, focalCircleColor, font);		

		}
		private void setSize(double size) {
			this.guiSize = size;
			// make variables scaled into GUI coordinates where the scaling is done such that
			// the focal place circle is just enclosed by the GUI window
			this.guiRatio = CSU_FP_RADIUS_MM*2 / guiSize;
			this.guiCSUHeight = CSU_HEIGHT_MM / guiRatio;
			this.guiSlitHeight = SINGLE_SLIT_HEIGHT_MM/ guiRatio;
			this.guiOverlap = OVERLAP_MM / guiRatio;
			this.guiFocalPlane = CSU_FP_RADIUS_MM*2 / guiRatio;
			this.guiCenterX = guiSize / 2;
			this.guiCenterY = guiSize / 2;
			//  this.guiBarLength = BAR_LENGTH / guiRatio;
			this.guiXLimit = xLimit / guiRatio;
			this.guiXCenter = xCenter / guiRatio;
		}
		public void drawMaskView(Graphics2D g2, Color backgroundColor, Color csuEdgeColor,
				Color barColor, Color legalColor, Color focalCircleColor, Font font){

			if (showTargets || showAllTargets) {
				targetShapes.clear();
			}
			
			// make the background Black
			Rectangle2D fullGUI = new Rectangle2D.Double(0,0,guiSize,guiSize);
			g2.setPaint(backgroundColor);
			g2.draw(fullGUI);
			g2.fill(fullGUI);
			g2.setFont(font);


			g2.setColor(barColor);

			//Draw a large white rectangle to represent the outside of the CSU

			// positionArray is feed the width, height, centerX, and centerY coordinates
			// and returns the lower left hand corner x and y, and the width and height
			// the values needed to generate a Graphics2D shape
			double[] loc = positionArray(guiCSUHeight, guiCSUHeight, guiCenterX, guiCenterY);
			Rectangle2D csuBorder = new Rectangle2D.Double(loc[0],loc[1],loc[2],loc[3]);
			//g2.draw(csuBorder);
			g2.fill(csuBorder);

			//g2.setPaint(Color.black);
			g2.setColor(csuEdgeColor);
			g2.draw(csuBorder);


			//Draw a gray circle representing the Keck Focal Plane
			//g2.setPaint(Color.LIGHT_GRAY);
			g2.setColor(focalCircleColor);

			loc = positionArray(guiFocalPlane, guiFocalPlane, guiCenterX, guiCenterY);
			Ellipse2D focalPlane = new Ellipse2D.Double(loc[0],loc[1],loc[2],loc[3]);
			g2.draw(focalPlane);

			// draw in a rectangle that is the bounding box for the legal range
			//g2.setPaint(Color.RED);
			g2.setColor(legalColor);
			double ulx = guiCenterX - guiXCenter - guiXLimit/2.;
			double uly = guiCenterY - guiCSUHeight/2.0;
			double urx = guiCenterX - guiXCenter + guiXLimit/2.0;
			double minX = guiCenterX - guiCSUHeight/2.0;
			double maxX = guiCenterX + guiCSUHeight/2.0;
			if (ulx < minX) {
				ulx = minX;
			}
			if (urx > maxX) {
				urx = maxX;
			}
//			loc = positionArray(guiXLimit, guiCSUHeight, guiCenterX-guiXCenter, guiCenterY);
//			Rectangle2D legalBorder = new Rectangle2D.Double(loc[0],loc[1],loc[2],loc[3]);
			Rectangle2D legalBorder = new Rectangle2D.Double(ulx, uly, (urx-ulx), guiCSUHeight);
			g2.draw(legalBorder);

			//. set stroke to 1 pixel wide
			g2.setStroke( new BasicStroke( 1.0f ) );

			double leftBarLocationFromCenter;
			double rightBarLocationFromCenter;
			int row=0;
			//hasAlignmentStars=false;
			//Draw the alignment boxes first, if alignment stars are available
			for (MechanicalSlit pos : config.getAlignSlitList()) {
					
					//. alignment boxes are distinguished from regular slits because they have
					//. a different width than the science slit on the same row
					GeneralPath slit = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
					
					//. draw coordinate system goes positive left to right, but our coordinate 
					//. system goes positive to the left.  so these values must be subtracted from guiCenterX
					leftBarLocationFromCenter = pos.getLeftBarPositionInMM() - MosfireParameters.CSU_ZERO_PT;
					rightBarLocationFromCenter = pos.getRightBarPositionInMM() - MosfireParameters.CSU_ZERO_PT;
					row = pos.getSlitNumber() - 1;
					
						// determine the x and y coordinates of the box, in this order: lowerLeft, upperLeft, upperRight, lowerRight						
						float[] x = {(float) (guiCenterX-leftBarLocationFromCenter/guiRatio-guiSlitHeight/2*Math.tan(CSU_SLIT_TILT_ANGLE_RADIANS)),
												 (float) (guiCenterX-leftBarLocationFromCenter/guiRatio+guiSlitHeight/2*Math.tan(CSU_SLIT_TILT_ANGLE_RADIANS)),
												 (float) (guiCenterX-rightBarLocationFromCenter/guiRatio+guiSlitHeight/2*Math.tan(CSU_SLIT_TILT_ANGLE_RADIANS)),
												 (float) (guiCenterX-rightBarLocationFromCenter/guiRatio-guiSlitHeight/2*Math.tan(CSU_SLIT_TILT_ANGLE_RADIANS))};

						float[] y = {(float) (guiCenterY+(row-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeight+guiOverlap)+guiOverlap/2),
								(float) (guiCenterY+(row-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeight+guiOverlap)+guiSlitHeight+guiOverlap),
								(float) (guiCenterY+(row-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeight+guiOverlap)+guiSlitHeight+guiOverlap),
								(float) (guiCenterY+(row-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeight+guiOverlap)+guiOverlap/2)};



						slit.moveTo(x[0], y[0]);
						for (int index = 1; index < x.length; index++) {
							slit.lineTo(x[index], y[index]);
						}
						slit.closePath();
						g2.setColor(Color.ORANGE);
						g2.fill(slit);
						if (showTargets || showAllTargets) {
							targetShapes.add(new TargetShape(slit, pos.getTarget()));
						}
						
						// And add the target names
						float xFloat = (float)(guiCenterX-leftBarLocationFromCenter/guiRatio);
						float yFloat = (float)(guiCenterY+(row+0.9-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeight+guiOverlap));

						// Write out the target names
						g2.setColor(Color.BLACK);
						g2.drawString(pos.getTargetName(), xFloat, yFloat);
					}

			// Now draw science slits
			for (MechanicalSlit pos : config.getMechanicalSlitList()) {
				
				//. draw coordinate system goes positive left to right, but our coordinate 
				//. system goes positive to the left.  so these values must be subtracted from guiCenterX
				leftBarLocationFromCenter = pos.getLeftBarPositionInMM() - MosfireParameters.CSU_ZERO_PT;
				rightBarLocationFromCenter = pos.getRightBarPositionInMM() - MosfireParameters.CSU_ZERO_PT;
				row = pos.getSlitNumber() - 1;

				// determine the x and y coordinates of the box, in this order: lowerLeft, upperLeft, upperRight, lowerRight						
				float[] x = {(float) (guiCenterX-leftBarLocationFromCenter/guiRatio-guiSlitHeight/2*Math.tan(CSU_SLIT_TILT_ANGLE_RADIANS)),
										 (float) (guiCenterX-leftBarLocationFromCenter/guiRatio+guiSlitHeight/2*Math.tan(CSU_SLIT_TILT_ANGLE_RADIANS)),
										 (float) (guiCenterX-rightBarLocationFromCenter/guiRatio+guiSlitHeight/2*Math.tan(CSU_SLIT_TILT_ANGLE_RADIANS)),
										 (float) (guiCenterX-rightBarLocationFromCenter/guiRatio-guiSlitHeight/2*Math.tan(CSU_SLIT_TILT_ANGLE_RADIANS))};

				float[] y = {(float) (guiCenterY+(row-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeight+guiOverlap)+guiOverlap/2),
										 (float) (guiCenterY+(row-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeight+guiOverlap)+guiSlitHeight+guiOverlap),
										 (float) (guiCenterY+(row-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeight+guiOverlap)+guiSlitHeight+guiOverlap),
										 (float) (guiCenterY+(row-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeight+guiOverlap)+guiOverlap/2)};

				GeneralPath slit = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);

				//. draw slits
				slit.moveTo(x[0], y[0]);
				for (int index = 1; index < x.length; index++) {
					slit.lineTo(x[index], y[index]);
				}
				slit.closePath();
				if (config.getMaskName().equals("OPEN")) {
					g2.setColor(MosfireParameters.COLOR_SLIT);
				} else {
					if (pos.getTarget().isInValidSlit()) {
						g2.setColor(Color.BLACK);
					} else {
						g2.setColor(Color.RED);
					}
				}
				g2.fill(slit);

				// And add the target names
				float xFloat = (float)(guiCenterX-rightBarLocationFromCenter/guiRatio+ guiSize/40); 
				float yFloat = (float)(guiCenterY+(row+0.9-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeight+guiOverlap));

				// Write out the target names
				g2.drawString(pos.getTargetName(), xFloat, yFloat);
			}

			g2.setColor(Color.BLACK);
			for (int ii=0; ii<MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS; ii++) {
				//Draw a line in the overlap region
				Rectangle2D overlap = new Rectangle2D.Double(guiCenterX-guiCSUHeight/2,
						guiCenterY+(ii-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeight+guiOverlap),
						guiCSUHeight, guiOverlap);

				g2.fill(overlap);
				
				if (ii == activeRow) {
					g2.setColor(MosfireParameters.COLOR_SELECTED_SLIT);
					Rectangle2D activeRowBox = new Rectangle2D.Double(guiCenterX - guiCSUHeight/2,
							guiCenterY+(ii-CSU_NUMBER_OF_BAR_PAIRS/2)*(guiSlitHeight+guiOverlap)+guiOverlap, guiCSUHeight, guiSlitHeight);
					g2.fill(activeRowBox);
					g2.setColor(Color.BLACK);
				}
			}
			
			double maxPriority=0;
			if (showAllTargets) {
				//. draw targets
				Color objectColor = targetColor;
				if (scaleTargetColorWithPriority) {
					//. get max priority
					for (AstroObj obj : allObjects) {
						double currentPriority = obj.getObjPriority(); 
						if  (currentPriority > maxPriority) {
							maxPriority = currentPriority;
						}
					}
				} else {
					g2.setColor(objectColor);
				}
				for (AstroObj obj : allObjects) {
					double centerX = guiCenterX - obj.getObjX() / CSU_ARCSEC_PER_MM / guiRatio;
					double centerY = guiCenterY - obj.getObjY() / CSU_ARCSEC_PER_MM / guiRatio;
					double diam = guiSlitHeight * targetSizePercentageOfRow / 100.0;
					if (scaleTargetSizeWithPriority) {
						diam *= Math.log10(obj.getObjPriority()); // config.getMascgenResult().getTotalPriority();
					}
					if (scaleTargetColorWithPriority) {
						objectColor = ColorUtilities.getColorFromScale(colorScaleMode, (float)(obj.getObjPriority()/maxPriority), 1.0f);
						g2.setColor(objectColor);
					}
					Ellipse2D.Double targetCircle = new Ellipse2D.Double(centerX - diam/2.0, centerY - diam/2., diam, diam);
					g2.fill(targetCircle);
					targetShapes.add(new TargetShape(targetCircle, obj));
				}				
			} 
			if (showTargets) {
				//. draw targets
				Color objectColor = targetColor;
				if (scaleTargetColorWithPriority) {
					//. get max priority
					for (ScienceSlit slit : config.getScienceSlitList()) {
						double currentPriority = slit.getTarget().getObjPriority(); 
						if  (currentPriority > maxPriority) {
							maxPriority = currentPriority;
						}
					}
				} else {
					g2.setColor(objectColor);
				}
				for (ScienceSlit slit : config.getScienceSlitList()) {
					AstroObj obj = slit.getTarget();
					double centerX = guiCenterX - obj.getObjX() / CSU_ARCSEC_PER_MM / guiRatio;
					double centerY = guiCenterY - obj.getObjY() / CSU_ARCSEC_PER_MM / guiRatio;
					double diam = guiSlitHeight * targetSizePercentageOfRow / 100.0;
					if (scaleTargetSizeWithPriority) {
						diam *= Math.log10(obj.getObjPriority()); // config.getMascgenResult().getTotalPriority();
					}
					if (scaleTargetColorWithPriority) {
						objectColor = ColorUtilities.getColorFromScale(colorScaleMode, (float)(obj.getObjPriority()/maxPriority), 1.0f);
						g2.setColor(objectColor);
					}
					Ellipse2D.Double targetCircle = new Ellipse2D.Double(centerX - diam/2.0, centerY - diam/2., diam, diam);
					g2.fill(targetCircle);
					targetShapes.add(new TargetShape(targetCircle, obj));
				}
			}
			if (showTargets || showAllTargets) {
				if (showColorScale && scaleTargetColorWithPriority) {
					g2.setStroke(new BasicStroke(1));
					double scaleBottomY = guiSize;
					double scaleTopY = guiSize - (guiSize - guiCSUHeight)/6.0;
					for (int ii=0; ii<(int)Math.round(guiSize); ii++) {
						Color scaleColor = ColorUtilities.getColorFromScale(colorScaleMode, (float)(ii/guiSize), 1.0f);
						g2.setColor(scaleColor);
						Line2D.Double line = new Line2D.Double(ii, scaleBottomY, ii, scaleTopY);
						g2.draw(line);
					}
					g2.setColor(Color.black);
					g2.drawString("0", 5.0f, (float)(scaleTopY - 10));
					String maxPriorityString = Double.toString(maxPriority); 
					double width = g2.getFontMetrics().getStringBounds(maxPriorityString, g2).getWidth();
					g2.drawString(maxPriorityString, (float)(guiSize - width), (float)(scaleTopY - 10));
				}
			}
		}
				
		// Returns a JPEG-ready mask image 		
		public RenderedImage makeMaskImage() { 
			double oldGuiSize = this.guiSize;
			setSize(2000);

			int width = (int) Math.floor(guiSize); 
			int height = (int) Math.floor(guiSize); 
			// Create a buffered image in which to draw 
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
			// Create a graphics contents on the buffered image 
			Graphics2D g2d = bufferedImage.createGraphics(); 
			// Draw graphics

			// make lines not pixelized
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			Color backgroundColor = Color.white;
			Color csuEdgeColor = Color.black;
			Color barColor = Color.lightGray;//.brighter();
			Color legalColor = Color.red;
			Color focalCircleColor = Color.blue;

			Font font = new Font("Serif", Font.BOLD, 30);
			
			int oldActiveRow = activeRow;
			
			activeRow = -1;
			
			drawMaskView(g2d, backgroundColor, csuEdgeColor, barColor, legalColor, focalCircleColor, font);

			g2d.setColor(Color.BLACK);

			font = new Font("Serif", Font.BOLD, 50);
			g2d.setFont(font);
			String label = maskName+"    PA = "+pa;

			FontMetrics fm = g2d.getFontMetrics(font);
			Rectangle2D labelExtents = fm.getStringBounds(label, g2d);
			
			double labelY = (guiSize - guiCSUHeight)/2. - 20;

			g2d.drawString(label, (float)(guiSize - labelExtents.getWidth())/2, (float)labelY);

			/**
			 * // And add the target names
					double xDouble = guiCenterX+alignmentBarPositions[i].getBarLeft()/guiRatio;//+ guiSize/40; 
					float xFloat = (float) xDouble;

					double yDouble = guiCenterY+(i+0.9-BAR_NUMBER/2)*(guiSlitHeight+guiOverlap);
					float yFloat = (float) yDouble;

					// Write out the target names
					g2.setPaint(Color.BLACK);
					g2.drawString(alignmentBarPositions[i].getObjName(), xFloat, yFloat);

			 */
			activeRow = oldActiveRow;
			setSize(oldGuiSize);

			return bufferedImage;
		}
		public int getRow(int verticalPixel) {
			//. guiCenterY+(ii-CSU_NUM_BAR_PAIRS/2)*(guiSlitHeight+guiOverlap)+guiOverlap
			//. 0-based
			double topBorder = guiCenterY - (CSU_NUMBER_OF_BAR_PAIRS/2 * (guiSlitHeight+guiOverlap));
			double bottomBorder = guiCenterY + (CSU_NUMBER_OF_BAR_PAIRS/2 * (guiSlitHeight+guiOverlap));
			if (verticalPixel < topBorder) {
				return -1;
			}
			if (verticalPixel > bottomBorder) {
				return -1;
			}
			return (int)Math.floor((verticalPixel-topBorder)/((guiSlitHeight+guiOverlap)));
		}
		private TargetShape getTargetShape(double x, double y) {
			TargetShape returnedShape = null;
			for (TargetShape shape : targetShapes) {
				if (shape.contains(x, y)) {
					returnedShape = shape;
					break;
				}
			}
			return returnedShape;
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			double x = (guiCenterX - e.getX()) * guiRatio * CSU_ARCSEC_PER_MM;
			xValueLabel.setText(csuXFormatter.format(x));
			rowValueLabel.setText(Integer.toString(getRow(e.getY())+1));
			if (showTargets) {
				TargetShape shape = getTargetShape(e.getX(), e.getY());
				if (shape == null) {
					objectValueLabel.setText(" ");
					objectValueLabel.setToolTipText(" ");
					priorityValueLabel.setText(" ");
					priorityValueLabel.setToolTipText(" ");
					currentTarget = null;
				} else {
					objectValueLabel.setText(shape.getTargetName());
					objectValueLabel.setToolTipText(shape.getTargetName());
					priorityValueLabel.setText(Double.toString(shape.getPriority()));
					priorityValueLabel.setToolTipText(Double.toString(shape.getPriority()));
					currentTarget = shape.getTarget();
				}
			}
			/* zoom window
			if (e.getX() > 50 && e.getX() < maskDraw.getWidth() - 50 && e.getY() > 50 && e.getY() < maskDraw.getHeight() - 50) {
			BufferedImage tempImage = new BufferedImage(maskDraw.getWidth()*4, maskDraw.getHeight()*4, BufferedImage.TYPE_INT_RGB);
			Graphics g = tempImage.getGraphics();
			maskDraw.paint(g);
			zoomImage = tempImage.getSubimage(e.getX()-50, e.getY()-50, 100, 100);
			Graphics2D g2 = (Graphics2D) zoomPanel.getGraphics();
			g2.scale(4.0, 4.0);
			g2.drawImage(zoomImage, null, 0, 0);
			}
			*/
		}
		public void handleShowTargetsButtons(ActionEvent ev) {
			if (ev.getSource().equals(showNoTargetsButton)) {
				showTargets = false;
				showAllTargets = false;
			} else if (ev.getSource().equals(showMaskTargetsButton)) {
					showTargets = true;
					showAllTargets = false;
			} else if (ev.getSource().equals(showAllTargetsButton)) {
				showTargets = true;
				showAllTargets = true;
			}
			repaint();
		}
		
		public AstroObj getCurrentTarget() {
			return currentTarget;
		}

		private class TargetShape {
			private Shape targetShape;
			private AstroObj target;
			public TargetShape(Shape shape, AstroObj obj) {
				targetShape = shape;
				target = obj;
			}
			public double getPriority() {
				return target.getObjPriority();
			}
			public String getTargetName() {
				return target.getObjName();
			}
			public AstroObj getTarget() {
				return target;
			}
			public boolean contains(double x, double y) {
				return targetShape.contains(new Point2D.Double(x, y));
			}
		}
	}  //. end MaskDrawComponent inner class

	//useful for producing the numbers required for 2D graphics from center positions, height and width
	public double[] positionArray(double width, double height, double centerX, double centerY){
		double[] array={centerX-width/2,centerY-height/2,width,height};
		return array;
	}
	public AstroObj getCurrentTarget() {
		return maskDraw.getCurrentTarget();
	}

	//====================================================== method main
	public static void main(String[] args) {
		// Or args[1]
		// Pass the filter choice
		//String filter = args[0];

		MaskVisualizationPanel window = new MaskVisualizationPanel();
		window.setVisible(true);
	}
}
