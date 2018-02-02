package edu.ucla.astro.irlab.mosfire.util;

import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.CSU_HEIGHT;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.OVERLAP;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.SINGLE_SLIT_HEIGHT;

import java.awt.geom.Point2D;

public class MascgenTransforms {
	// Convert the coordinates of an RaDec point from Ra/Dec to x/y.
	public static void raDecToXY(RaDec p) {

		if (p.getDecDeg() == 0){
			// figure out if it is negative or not
			String dec = Double.toString(p.getDecDeg());
			if (dec.equals("-0.0")){
				p.setYCoordinate((-1)*(3600 * Math.abs(p.getDecDeg()) + 60 * p.getDecMin() + 
						p.getDecSec()));
			}
			else{
				p.setYCoordinate((3600 * Math.abs(p.getDecDeg()) + 60 * p.getDecMin() + 
						p.getDecSec()));
			}

		}
		else{
			p.setYCoordinate(p.getDecDeg() / Math.abs(p.getDecDeg()) * 
					(3600 * Math.abs(p.getDecDeg()) + 60 * p.getDecMin() + 
							p.getDecSec()));
		}

		p.setXCoordinate(Math.cos(p.getYCoordinate() * Math.PI / 180 / 3600) * 
				15 * 
				(p.getRaHour() * 3600 + 
						p.getRaMin() * 60 +  
						p.getRaSec()));
	}

	// Convert the coordinates of an RaDec point from Ra/Dec to x/y.
	//. same as raDecToXY but returns a point
	public static Point2D.Double getCSUXYFromRaDec(RaDec p) {
		Point2D.Double result = new Point2D.Double();
		if (p.getDecDeg() == 0){
			// figure out if it is negative or not
			String dec = Double.toString(p.getDecDeg());
			if (dec.equals("-0.0")){
				result.y = ((-1)*(3600 * Math.abs(p.getDecDeg()) + 60 * p.getDecMin() + 
						p.getDecSec()));
			}
			else{
				result.y = ((3600 * Math.abs(p.getDecDeg()) + 60 * p.getDecMin() + 
						p.getDecSec()));
			}

		}
		else{
			result.y = (p.getDecDeg() / Math.abs(p.getDecDeg()) * 
					(3600 * Math.abs(p.getDecDeg()) + 60 * p.getDecMin() + 
							p.getDecSec()));
		}

		result.x = (Math.cos(result.y * Math.PI / 180 / 3600) * 
				15 * 
				(p.getRaHour() * 3600 + 
						p.getRaMin() * 60 +  
						p.getRaSec()));
		
		return result;
	}


	// Convert the coordinates of an RaDec point from x/y to Ra/Dec.
	public static void xyToRaDec(RaDec p) {

		p.setDecDeg((Math.abs(p.getYCoordinate()) / p.getYCoordinate()) * 
				Math.floor(Math.abs(p.getYCoordinate() / 3600)));		
		p.setDecMin((int) Math.floor((Math.abs(p.getYCoordinate() / 3600) - 
				Math.floor(Math.abs(p.getYCoordinate() / 3600))) * 60));
		p.setDecSec(60 * ((Math.abs(p.getYCoordinate() / 3600) - 
				Math.floor(Math.abs(p.getYCoordinate() / 3600))) * 
				60 - p.getDecMin()));
		
		double decrad = (p.getYCoordinate() * Math.PI / 180 / 3600);
		
		p.setRaHour((int) Math.floor(p.getXCoordinate() / 3600 / 15 / Math.cos(decrad)));
		p.setRaMin((int) Math.floor(60 * ((p.getXCoordinate() / 3600 / 15 / Math.cos(decrad)) - p.getRaHour())));
		p.setRaSec(60 * (((p.getXCoordinate() / 3600 / 15 / Math.cos(decrad)) - p.getRaHour()) * 60 - p.getRaMin()));
		

	}

	// Convert the coordinates of an AstroObj from Ra/Dec to x/y.
	public static void astroObjRaDecToXY(AstroObj obj, RaDec cp) {

		if (obj.getDecDeg() == 0){
			// figure out if it is negative or not
			String dec = Double.toString(obj.getDecDeg());
			if (dec.equals("-0.0")){
				obj.setWcsY((-1)*(3600 * Math.abs(obj.getDecDeg()) + 60 * obj.getDecMin() + 
						obj.getDecSec()));
			}
			else{
				obj.setWcsY((3600 * Math.abs(obj.getDecDeg()) + 60 * obj.getDecMin() + 
						obj.getDecSec()));
			}

		}
		else{
			obj.setWcsY(obj.getDecDeg() / Math.abs(obj.getDecDeg()) * 
					(3600 * Math.abs(obj.getDecDeg()) + 60 * obj.getDecMin() + 
							obj.getDecSec()));
		}


		obj.setWcsX(Math.cos(cp.getYCoordinate() * Math.PI / 180 / 3600) * 
				15 * 
				(obj.getRaHour() * 3600 + 
						obj.getRaMin() * 60 +  
						obj.getRaSec()));
	}

//Convert the coordinates of an AstroObj from Ra/Dec to x/y.
	//. same as astroObjRaDecToXY but returns a point
	public static Point2D.Double getWcsFromRaDec(RaDec obj, double centerPointY) {
		Point2D.Double wcs = new Point2D.Double();
		
		if (obj.getDecDeg() == 0){
			// figure out if it is negative or not
			String dec = Double.toString(obj.getDecDeg());
			if (dec.equals("-0.0")){
				wcs.y = ((-1)*(3600 * Math.abs(obj.getDecDeg()) + 60 * obj.getDecMin() + 
						obj.getDecSec()));
			}
			else{
				wcs.y = ((3600 * Math.abs(obj.getDecDeg()) + 60 * obj.getDecMin() + 
						obj.getDecSec()));
			}

		}
		else{
			wcs.y = (obj.getDecDeg() / Math.abs(obj.getDecDeg()) * 
					(3600 * Math.abs(obj.getDecDeg()) + 60 * obj.getDecMin() + 
							obj.getDecSec()));
		}


		wcs.x = (Math.cos(centerPointY * Math.PI / 180 / 3600) * 
				15 * 
				(obj.getRaHour() * 3600 + 
						obj.getRaMin() * 60 +  
						obj.getRaSec()));
		
		return wcs;
	}

	public static RaDec getRaDecFromWcs(Point2D.Double wcs, RaDec cp) {
		RaDec result = new RaDec();
		result.setDecDeg((Math.abs(wcs.y) / wcs.y) * 
				Math.floor(Math.abs(wcs.y / 3600)));
		result.setDecMin((int) Math.floor((Math.abs(wcs.y / 3600) - 
				Math.floor(Math.abs(wcs.y / 3600))) * 60));
		result.setDecSec(60 * ((Math.abs(wcs.y / 3600) - 
				Math.floor(Math.abs(wcs.y / 3600))) * 
				60 - result.getDecMin()));
		double decrad = (cp.getYCoordinate() * Math.PI / 180 / 3600);
		result.setRaHour((int) Math.floor(wcs.x / 3600 / 15 / Math.cos(decrad)));
		result.setRaMin((int) Math.floor(60 * 
				((wcs.x / 3600 / 15 / Math.cos(decrad)) 
						- result.getRaHour())));
		result.setRaSec(60 * (((wcs.x / 3600 / 15 / Math.cos(decrad)) 
				- result.getRaHour()) * 60 - result.getRaMin()));
	
		return result;
	}

	public static Point2D.Double getWcsFromCSUCoords(Point2D.Double csuCoordsPoint, RaDec cp, double pa) {
		//. rotate back by theta
		double theta = -pa * Math.PI / 180;	
		return new Point2D.Double((csuCoordsPoint.x * Math.cos(theta) - csuCoordsPoint.y * Math.sin(theta)) + cp.getXCoordinate(), 
				(csuCoordsPoint.x * Math.sin(theta) + csuCoordsPoint.y * Math.cos(theta)) + cp.getYCoordinate());
	}
	
	public static Point2D.Double getSlitPositionInCsuCoords(int startRow, int lengthInRows, Point2D.Double targetCsuCoordsPoint) {
		//. for historical reasons, the row numbering is from the bottom, zero-based (real row 1 = 45, real row 46 = 0)  TODO: double check
		//. so for a real row r, pass in 46 - r
		Point2D.Double result = new Point2D.Double();
		
		//. first line in y is the Y coordinate of the middle of the row.  seconds line is an offset 
		//. applied due to length of slit.
		result.y = (lengthInRows/2.0 + startRow)*MosfireParameters.CSU_ROW_HEIGHT - CSU_HEIGHT / 2;
		
		//. find X offsets between target and slit (object will be centered in slit,
		//. so simple trig can give us the X offset) and subtract from object X position (in CSU coords)
		result.x = targetCsuCoordsPoint.x - ((targetCsuCoordsPoint.y - result.y)*Math.tan(CSU_SLIT_TILT_ANGLE_RADIANS));

		return result;
	}

	public static Point2D.Double getCSUCoordsFromWcs(Point2D.Double wcs, RaDec cp, double pa) {
		double theta = pa * Math.PI / 180;	
		return new Point2D.Double(((wcs.x - cp.getXCoordinate()) * Math.cos(theta) - (wcs.y - cp.getYCoordinate()) * Math.sin(theta)), 
				((wcs.x - cp.getXCoordinate()) * Math.sin(theta) + (wcs.y - cp.getYCoordinate()) * Math.cos(theta)));
	
	}
	public static int getRowFromRaDec(RaDec target, RaDec cp, double pa) {
		Point2D.Double wcs = getWcsFromRaDec(target, cp.getYCoordinate());
		Point2D.Double csuCoords = getCSUCoordsFromWcs(wcs, cp, pa);
		double yoffset = MosfireParameters.CSU_HEIGHT/2.0 - csuCoords.y;
		int row = (int) Math.floor((yoffset - MosfireParameters.OVERLAP/2.0) / (MosfireParameters.CSU_ROW_HEIGHT));
		//. above row is zero-based.  add 1 to make 1-based
		return row+1;
	}
	public static void fixRaCoordWrap(AstroObj obj) {
		double h = obj.getRaHour();
		h -= 12.0;
		if (h < 0) h+=24.0;
		obj.setRaHour(h);
		
	}
	public static void fixRaCoordWrap(RaDec coord) {
		int h = coord.getRaHour();
		h -= 12;
		if (h < 0) h+=24;
		coord.setRaHour(h);
		
	}
	public static void applyRaCoordWrap(AstroObj obj) {
		double h = obj.getRaHour();
		h += 12.0;
		if (h > 24.0) h-=24.0;
		obj.setRaHour(h);
	}

	public static void applyRaCoordWrap(RaDec coord) {
		int h = coord.getRaHour();
		h += 12;
		if (h >24) h-=24;
		coord.setRaHour(h);
	}

}
