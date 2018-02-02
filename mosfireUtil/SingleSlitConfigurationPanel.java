package edu.ucla.astro.irlab.mosfire.util;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SingleSlitConfigurationPanel extends JPanel {
	private double widthToHeightAspectRatio = MosfireParameters.CSU_WIDTH/MosfireParameters.CSU_ROW_HEIGHT;
	private double fullSpectralCoverageFractionOfWidth = MosfireParameters.CSU_SPEC_FULL_SPECTRAL_COVERAGE_WIDTH_ARCMIN/(MosfireParameters.CSU_WIDTH / 60.0);
	private double pixelsPerArcsec;
	private double slitHeightInPixels;
	private SlitPosition  slitPosition; 
	private ResizeListener resizeListener = new ResizeListener();
	private int drawHeight = -1;
	private int drawWidth = -1;
	private boolean isReadOnly;
	
	public SingleSlitConfigurationPanel() {
		this(false);
	}
	public SingleSlitConfigurationPanel(boolean readOnly) {
		isReadOnly = readOnly;
		init();
	}
	
	private void init() {
		addComponentListener(resizeListener);
		if (!isReadOnly) {
			this.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent mEv) {
					handleMouseClicked(mEv);
				}
			});
		}
	}
	public void handleMouseClicked(MouseEvent mEv) {
		if (slitPosition != null) {
			slitPosition.setCenterPosition(mEv.getX()/pixelsPerArcsec);
			repaint();
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
  	//g2d.setColor(Color.BLACK);
  	//g2d.setStroke(new BasicStroke(2.0f));
  	//g2d.draw(border);
  	
		
		//. close off and shade in lim spect coverage areas
//  	int leftHatchRightEdge = (int)Math.floor((this.getWidth() - (MosfireParameters.CSU_SPEC_FULL_SPECTRAL_COVERAGE_WIDTH_ARCMIN*pixelsPerArcmin))/2);
//  	java.awt.geom.Rectangle2D.Float leftHatch = new java.awt.geom.Rectangle2D.Float(0,0,leftHatchRightEdge, drawHeight);
//  	int rightHatchLeftEdge = (int)Math.floor((this.getWidth() + (MosfireParameters.CSU_SPEC_FULL_SPECTRAL_COVERAGE_WIDTH_ARCMIN*pixelsPerArcmin))/2);
//  	java.awt.geom.Rectangle2D.Float rightHatch = new java.awt.geom.Rectangle2D.Float(rightHatchLeftEdge, 0, drawWidth-rightHatchLeftEdge, drawHeight);

//  	g2d.setColor(MosfireParameters.COLOR_LIMITED_SPECTRAL_COVERAGE);
//  	g2d.fill(leftHatch);
//  	g2d.fill(rightHatch);

  	if (slitPosition != null) {
  		//. set color
  		g2d.setColor(MosfireParameters.COLOR_SLIT);
  		g2d.fill(getSlitShape(slitPosition));
  	}
	}
	public void setSlitPosition(SlitPosition slitPosition) {
		this.slitPosition = slitPosition;
	}
	public void processComponentResized(ComponentEvent cEv) {
		int width = this.getWidth();
		int height = this.getHeight();
		
		double scaleByHeight = height/MosfireParameters.CSU_ROW_HEIGHT;
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
	public Polygon getSlitShape(SlitPosition pos) {
		/*
		 *      ul ____w_____ ur
		 *        /         /|
		 *       /   c     / |
		 *      /    +    /\_| 
		 *     /    /|y  / a | h
		 *    /____/_|__/    |
		 *  ll     x    lr
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
		double y = slitHeightArcsec / 2.;
		double x = y * Math.tan(Math.toRadians(MosfireParameters.CSU_SLIT_TILT_ANGLE));
		double c = pos.getCenterPosition()+MosfireParameters.CSU_ZERO_PT*MosfireParameters.CSU_ARCSEC_PER_MM;
		double w = pos.getSlitWidth();
		if (w < MosfireParameters.CSU_SLIT_MINIMUM_DRAWN_WIDTH) {
			w = MosfireParameters.CSU_SLIT_MINIMUM_DRAWN_WIDTH;
		}
		
		int llx = (int)(Math.floor(pixelsPerArcsec*(c - w/2. - x)));
		int lrx = (int)(Math.floor(pixelsPerArcsec*(c + w/2. - x)));
		int ulx = (int)(Math.floor(pixelsPerArcsec*(c - w/2. + x)));
		int urx = (int)(Math.floor(pixelsPerArcsec*(c + w/2. + x)));
		
		int lly = drawHeight;
		int uly = 0;
		int lry = lly;
		int ury = uly;
		
		return new Polygon(new int[] {llx, lrx, urx, ulx}, new int[] {lly, lry, ury, uly}, 4);
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
			
			SingleSlitConfigurationPanel panel = new SingleSlitConfigurationPanel();
			panel.setSlitPosition(config.getSlitPosition(20));
			JFrame frame = new JFrame();
			panel.setWidthInPixels(400);
			
			

			frame.getContentPane().add(panel, java.awt.BorderLayout.CENTER);
			frame.getContentPane().add(new javax.swing.JLabel(" "), java.awt.BorderLayout.NORTH);
			frame.getContentPane().add(new javax.swing.JLabel(" "), java.awt.BorderLayout.SOUTH);
			frame.getContentPane().add(new javax.swing.JLabel(" "), java.awt.BorderLayout.EAST);
			frame.getContentPane().add(new javax.swing.JLabel(" "), java.awt.BorderLayout.WEST);
	
			frame.pack();
			frame.setVisible(true);
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
