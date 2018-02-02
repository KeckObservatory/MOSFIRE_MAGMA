package edu.ucla.astro.irlab.mosfire.util;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SlitConfigurationPanel extends JPanel {
	private double widthToHeightAspectRatio = MosfireParameters.CSU_WIDTH/MosfireParameters.CSU_HEIGHT;
	private double fullSpectralCoverageFractionOfWidth = MosfireParameters.CSU_SPEC_FULL_SPECTRAL_COVERAGE_WIDTH_ARCMIN/(MosfireParameters.CSU_WIDTH / 60.0);
	private double pixelsPerArcsec;
	private double slitHeightInPixels;
	private ArrayList<? extends SlitPosition>  slitPositionList; 
	private ResizeListener resizeListener = new ResizeListener();
	private int drawHeight = -1;
	private int drawWidth = -1;
	private int selectedRow = -1;
	private ArrayList<String> barStatuses = new ArrayList<String>(MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS);
	public SlitConfigurationPanel() {
		init();
	}
	
	private void init() {
		addComponentListener(resizeListener);
		for (int ii=0; ii<MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS; ii++ ) {
			barStatuses.add("");
		}
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if ((drawWidth == -1) || (drawHeight == -1)) {
			processComponentResized(null);
		}
		
		//. flip horizontally
		g2d.scale(-1, 1);
		g2d.translate(-this.getWidth(), 0);
		
		//g2d.clearRect(0,0,this.getWidth(), this.getHeight());
		g2d.setColor(UIManager.getColor("Panel.background"));
		g2d.fillRect(0,0,this.getWidth(), this.getHeight());
		
		//. draw border around outside
  	java.awt.geom.Rectangle2D.Float border = new java.awt.geom.Rectangle2D.Float(0,0,drawWidth, drawHeight);
  	g2d.setColor(MosfireParameters.COLOR_MSC_FIELD_BG);
  	g2d.fill(border);

  	//. draw bar borders
  	g2d.setStroke(new BasicStroke(1.0f));
  	g2d.setColor(MosfireParameters.COLOR_MSC_BAR_OUTLINE);

  	for (int ii=0; ii<MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS; ii++) {
    	java.awt.geom.Rectangle2D.Double barBorder = new java.awt.geom.Rectangle2D.Double(0.0,slitHeightInPixels*ii,(double)drawWidth, slitHeightInPixels*(ii+1));  		
    	g2d.draw(barBorder);
  	}



		//g2d.setColor(Color.BLACK);
  	//g2d.setStroke(new BasicStroke(2.0f));
  	//g2d.draw(border);
  	
		
		//. close off and shade in lim spect coverage areas
//  	int leftHatchRightEdge = (int)Math.floor((drawWidth - (MosfireParameters.CSU_SPEC_FULL_SPECTRAL_COVERAGE_WIDTH_ARCMIN*pixelsPerArcmin))/2);
 // 	java.awt.geom.Rectangle2D.Float leftHatch = new java.awt.geom.Rectangle2D.Float(0,0,leftHatchRightEdge, drawHeight);
 // 	int rightHatchLeftEdge = (int)Math.floor((drawWidth + (MosfireParameters.CSU_SPEC_FULL_SPECTRAL_COVERAGE_WIDTH_ARCMIN*pixelsPerArcmin))/2);
  //	java.awt.geom.Rectangle2D.Float rightHatch = new java.awt.geom.Rectangle2D.Float(rightHatchLeftEdge, 0, drawWidth-rightHatchLeftEdge, drawHeight);

//  	g2d.setColor(MosfireParameters.COLOR_LIMITED_SPECTRAL_COVERAGE);
//  	g2d.fill(leftHatch);
//  	g2d.fill(rightHatch);


  	//. draw boxes for slits
		if (slitPositionList != null) {
			if (!slitPositionList.isEmpty()) {
				int slitNum = 0;
				//. set color
				g2d.setColor(MosfireParameters.COLOR_SLIT);
				for (SlitPosition pos : slitPositionList) {
					//. draw
					g2d.fill(getSlitShape(pos, slitNum));
					g2d.draw(getSlitShape(pos, slitNum));
					slitNum++;
				}
				if (selectedRow >= 0) {
					//. draw box around selected row.  0-based
					Rectangle2D.Double row = new Rectangle2D.Double(0,slitHeightInPixels*selectedRow-1, drawWidth-1, slitHeightInPixels+1);
			  	g2d.setStroke(new BasicStroke(4.0f));
					g2d.setColor(MosfireParameters.COLOR_SELECTED_SLIT);
					g2d.draw(row);
			  	g2d.setStroke(new BasicStroke(1.0f));
				}
			}
		}
		//. flip back horizontally for bar numbering
		g2d.scale(-1, 1);
		g2d.translate(-this.getWidth(), 0);
		DecimalFormat formatter = new DecimalFormat("00");
		for (int ii=1; ii<MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS+1; ii++) {
			g2d.setColor(MosfireParameters.COLOR_MSC_BAR_NUMBER);
    	g2d.setFont(MosfireParameters.FONT_MSC_BAR_NUMBER);
    	
    	g2d.drawString(formatter.format(ii*2-1), (float)(this.getWidth()-(16)), (float)(slitHeightInPixels*(ii-0.10)));
    	g2d.drawString(formatter.format(ii*2), (float)(this.getWidth()-drawWidth+(4)), (float)(slitHeightInPixels*(ii-0.10)));
    	String barStatus = barStatuses.get(ii*2-2);
    	if ((barStatus.compareToIgnoreCase(MosfireParameters.CSU_BAR_STATUS_DISABLED) == 0) ||
    			(barStatus.compareToIgnoreCase(MosfireParameters.CSU_BAR_STATUS_UNKNOWN) == 0) ||
    			(barStatus.compareToIgnoreCase(MosfireParameters.CSU_BAR_STATUS_ERROR) == 0)) {
    		g2d.setColor(MosfireParameters.COLOR_MSC_BAR_STATUS_ERROR);
    	} else {
    		g2d.setColor(MosfireParameters.COLOR_MSC_BAR_STATUS_OK);
    	}
    	g2d.drawString(barStatus, (float)(this.getWidth()-(6 * (barStatus.length()+5))), (float)(slitHeightInPixels*(ii-0.10)));
    	barStatus = barStatuses.get(ii*2-1);
    	if ((barStatus.compareToIgnoreCase(MosfireParameters.CSU_BAR_STATUS_DISABLED) == 0) ||
    			(barStatus.compareToIgnoreCase(MosfireParameters.CSU_BAR_STATUS_UNKNOWN) == 0) ||
    			(barStatus.compareToIgnoreCase(MosfireParameters.CSU_BAR_STATUS_ERROR) == 0)) {
    		g2d.setColor(MosfireParameters.COLOR_MSC_BAR_STATUS_ERROR);
    	} else {
    		g2d.setColor(MosfireParameters.COLOR_MSC_BAR_STATUS_OK);
    	}
    	g2d.drawString(barStatus, (float)(this.getWidth()-drawWidth+(26)), (float)(slitHeightInPixels*(ii-0.10)));
    	
    			
		}

	}
	public void setSlitPositionList(ArrayList<? extends SlitPosition> slitPositionList) {
		this.slitPositionList = slitPositionList;
	}
	public void setSlitPositionList(SlitConfiguration config) {
		setSlitPositionList(config.getMechanicalSlitList());
	}
	public void processComponentResized(ComponentEvent cEv) {
		int width = this.getWidth();
		int height = this.getHeight();
		
		double scaleByHeight = height/MosfireParameters.CSU_HEIGHT;
		double scaleByWidth = width/MosfireParameters.CSU_WIDTH;
		if (scaleByHeight < scaleByWidth) {
			setHeightInPixels(height);
		} else {
			setWidthInPixels(width);
		}
	}
	public void setWidthInPixels(int width) {
		drawWidth = width;
		drawHeight = (int)(Math.floor(width/widthToHeightAspectRatio));

		recalcScales();

		repaint();
	}
	public void setHeightInPixels(int height) {
		drawWidth = (int)(Math.floor(height*widthToHeightAspectRatio));
		drawHeight = height;
		
		recalcScales();
		
		repaint();
	}
	public void recalcScales() {
		pixelsPerArcsec = drawWidth/MosfireParameters.CSU_WIDTH;
		slitHeightInPixels = pixelsPerArcsec*MosfireParameters.CSU_ROW_HEIGHT;		
	}
public Polygon getSlitShape(SlitPosition pos, int numSlit) {
		/*
		 *      ul ____w_______ ur
		 *        /         /|
		 *       /   c     / |
		 *      /    +    /\_| h
		 *     /    /|y  / a | 
		 *    /____/_|__/   _|_
		 *  ll      x   lr
		 *   
		 *   h = slitHeight
		 *   w = slitWidth
		 *   c = centerPosition
		 *   a = tilt angle
		 *   n = slit number
		 *   
		 *   y = h / 2
		 *   x = y tan(a) 
		 *   llx = c - w/2 - x
		 *   lrx = c + w/2 - x
		 *   ulx = c - w/2 + x
		 *   urx = c + w/2 + x
		 *   
		 *   lly = (n+1) * h
		 *   lry = (n+1) * h
		 *   uly = n * h
		 *   ury = n * h
		 *    
		 * 
		 */
		double slitHeightArcsec = MosfireParameters.CSU_ROW_HEIGHT;
		double y = slitHeightArcsec/2.;
		double x = y * Math.tan(Math.toRadians(MosfireParameters.CSU_SLIT_TILT_ANGLE));
		double c = pos.getCenterPosition() + MosfireParameters.CSU_ZERO_PT*MosfireParameters.CSU_ARCSEC_PER_MM;
		double w = pos.getSlitWidth();
		if (w < MosfireParameters.CSU_SLIT_MINIMUM_DRAWN_WIDTH) {
			w = MosfireParameters.CSU_SLIT_MINIMUM_DRAWN_WIDTH;
		}
		
		int llx = (int)(Math.floor(pixelsPerArcsec*(c - w/2. - x)));
		int lrx = (int)(Math.floor(pixelsPerArcsec*(c + w/2. - x)));
		int ulx = (int)(Math.floor(pixelsPerArcsec*(c - w/2. + x)));
		int urx = (int)(Math.floor(pixelsPerArcsec*(c + w/2. + x)));
		
		int lly = (int)(Math.floor(((numSlit+1) * slitHeightInPixels)));
		int uly = (int)(Math.floor(((numSlit) * slitHeightInPixels)));
		int lry = lly;
		int ury = uly;
		
		return new Polygon(new int[] {llx, lrx, urx, ulx}, new int[] {lly, lry, ury, uly}, 4);
	}
	public int getRow(int verticalPixel) {
		//. 1-based
		if (verticalPixel > drawHeight)
			return MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS;
		else
			return (int)Math.floor(verticalPixel/slitHeightInPixels);
	}
	public void setSelectedRow(int row) {
		//. todo: throw error on invalid row?
		selectedRow = row;
		repaint();
	}
	public void clearSelectedRow() {
		selectedRow = -1;
		repaint();
	}
	public void setBarStatuses(ArrayList<String> barStatuses) {
		this.barStatuses = barStatuses;
		repaint();
	}

	public ArrayList<String> getBarStatuses() {
		return barStatuses;
	}
	public class ResizeListener extends ComponentAdapter {
		public void componentResized(ComponentEvent cEv) {
			processComponentResized(cEv);
		}		
	}
	public static void main(String[] args) {
		try {
			SlitConfiguration config = new SlitConfiguration();
			ArrayList<String> warningList = new ArrayList<String>();
			config.readSlitConfiguration(new java.io.File("/u/mosdev/kroot/kss/mosfire/gui/mscgui/mascgen_test_data/newbase/q0207_pa_0/q0207_pa_0.xml"), warningList);
			
			SlitConfigurationPanel panel = new SlitConfigurationPanel();
			panel.setSlitPositionList(config);
			JFrame frame = new JFrame();
			panel.setWidthInPixels(400);
			
			frame.getContentPane().add(panel, java.awt.BorderLayout.CENTER);
			frame.getContentPane().add(new javax.swing.JLabel(" "), java.awt.BorderLayout.NORTH);
			frame.getContentPane().add(new javax.swing.JLabel(" "), java.awt.BorderLayout.SOUTH);
			frame.getContentPane().add(new javax.swing.JLabel(" "), java.awt.BorderLayout.EAST);
			frame.getContentPane().add(new javax.swing.JLabel(" "), java.awt.BorderLayout.WEST);
	
			frame.pack();
			frame.setVisible(true);
		
			//edu.ucla.astro.irlab.test.UIManagerDefaults.showDefaults();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
