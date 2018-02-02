package edu.ucla.astro.irlab.util;

import java.awt.Color;

public class ColorUtilities {
	public static final float MAXIMUM_VISIBLE_WAVELENGTH_NM = 780;
	public static final float MINIMUM_VISIBLE_WAVELENGTH_NM = 380;
	public static final float MAXIMUM_FULL_INTENSITY_WAVELENGTH_NM = 700;
	public static final float MINIMUM_FULL_INTENSITY_WAVELENGTH_NM = 420;
	public static final int GRAYSCALE = 0;
	public static final int GRAYSCALE_LOG = 1;
	public static final int FULL_VISIBLE_SPECTRUM = 2;
	public static final int FULL_INTENSITY_VISIBLE_SPECTRUM = 3;
	public static final int BLUE_RED = 4;
	public static final int BLUE_RED_LOG = 5;
	public static final int YELLOW_RED = 6;
	public static final int YELLOW_RED_LOG = 7;
	public static final int ICE_FIRE = 8;
	public static final int YELLOW_MAGENTA_RED_GRADIENT = 9;
	public static final int BLUE_CYAN_YELLOW_RED = 10;
	public static final int BLUE_GREEN_YELLOW_RED = 11;
	public static final int BLUE_YELLOW_RED = 12;
	public static final int RAINBOW = 13;

	public static final String[] AVAILABLE_SCALES = {"Grayscale",
																									 "Grayscale (Log)",
																									 "Full Visible Spectrum",
																									 "Full Intensity Visible Spectrum",
																									 "Blue-Red",
																									 "Blue-Red (Log)",
																									 "Yellow-Red",
																									 "Yellow-Red (Log)",
																									 "Ice-Fire",
																									 "Yellow-Magenta-Red",
																									 "Blue-Cyan-Yellow-Red",
																									 "Blue-Green-Yellow-Red",
																									 "Blue-Yellow-Red",
																									 "Rainbow"
	};
	
	public static final int FUNCTION_CONSTANT = 0;
	public static final int FUNCTION_LINEAR = 1;
	public static final int FUNCTION_QUAD = 2;
	public static final int FUNCTION_NEG_QUAD = 3;
	public static final int FUNCTION_LOG = 4;
	public static final int FUNCTION_NEG_LOG = 5;
	public static final int FUNCTION_EXP = 6;
	public static final int FUNCTION_NEG_EXP = 7;
	public static final int FUNCTION_TOP_QUARTER_SIN = 8;
	public static final int FUNCTION_BOTTOM_QUARTER_SIN = 9;
	public static final int FUNCTION_HALF_SIN = 10;
	public static final int FUNCTION_ARCSIN = 11;
	
	public static final String[] AVAIABLE_SCALE_FUNCTIONS = {"Constant",
																													 "Linear",
																													 "Quadratic",
																													 "Negative Quadratic",
																													 "Logarithmic",
																													 "Negative Logarithmic",
																													 "Exponential",
																													 "Negative Exponential",
																													 "Top Quarter Sin",
																													 "Bottom Quarter Sin",
																													 "Half Sin",
																													 "Arcsin"
	};

	public static Color getColorFromCustomScale(float intensity, float gamma, float rStart, float rEnd, int rFunction, float gStart, float gEnd, int gFunction, float bStart, float bEnd, int bFunction) {
		float r, g, b;

		intensity = constrain(intensity);
		gamma = constrain(gamma);
		
		if (rStart < rEnd) {
			r = (rEnd - rStart) * getFactor(intensity, rStart, rEnd, rFunction)+ rStart;
		} else {
			r = (rStart - rEnd) * getFactor(intensity, rStart, rEnd, rFunction) + rEnd;
		}
		if (gStart < gEnd) {
			g = (gEnd - gStart) * getFactor(intensity, gStart, gEnd, gFunction)+ gStart;
		} else {
			g = (gStart - gEnd) * getFactor(intensity, gStart, gEnd, gFunction)+ gEnd;
		}
		if (bStart < bEnd) {
			b = (bEnd - bStart) * getFactor(intensity, bStart, bEnd, bFunction)+ bStart;
		} else {
			b = (bStart - bEnd) * getFactor(intensity, bStart, bEnd, bFunction)+ bEnd;
		}
		
		return new Color(constrain(r), constrain(g), constrain(b));
		
	}
	private static float getFactor(float intensity, float start, float end, int function) {
		float factor;
		switch (function) {
		case FUNCTION_LINEAR:
			factor = (start < end) ? scaleUpLinear(intensity) : scaleDownLinear(intensity);
			break;
		case FUNCTION_QUAD:
			factor = (start < end) ? scaleUpByQuad(intensity) : scaleDownByQuad(intensity);
			break;			
		case FUNCTION_NEG_QUAD:
			factor = (start < end) ? scaleUpByNegQuad(intensity) : scaleDownByNegQuad(intensity);
			break;			
		case FUNCTION_LOG:
			factor = (start < end) ? scaleUpByLog(intensity) : scaleDownByLog(intensity);
			break;			
		case FUNCTION_NEG_LOG:
			factor = (start < end) ? scaleUpByNegLog(intensity) : scaleDownByNegLog(intensity);
			break;			
		case FUNCTION_EXP:
			factor = (start < end) ? scaleUpByExp(intensity) : scaleDownByExp(intensity);
			break;			
		case FUNCTION_NEG_EXP:
			factor = (start < end) ? scaleUpByNegExp(intensity) : scaleDownByNegExp(intensity);
			break;			
		case FUNCTION_TOP_QUARTER_SIN:
			factor = (start < end) ? scaleUpByTopQuarterSin(intensity) : scaleDownByTopQuarterSin(intensity);
			break;			
		case FUNCTION_BOTTOM_QUARTER_SIN:
			factor = (start < end) ? scaleUpByBottomQuarterSin(intensity) : scaleDownByBottomQuarterSin(intensity);
			break;			
		case FUNCTION_HALF_SIN:
			factor = (start < end) ? scaleUpByHalfSin(intensity) : scaleDownByHalfSin(intensity);
			break;			
		case FUNCTION_ARCSIN:
			factor = (start < end) ? scaleUpByArcsin(intensity) : scaleDownByArcsin(intensity);
			break;			
		default:
			factor=1;
			break;
		}
		return factor;
	}
	
	public static Color getColorFromScale(int mode, float intensity, float gamma) {
		//. ref http://grass.osgeo.org/wiki/Color_tables
		float r, g, b;

		intensity = constrain(intensity);
		gamma = constrain(gamma);
		
		switch (mode) {
		case (BLUE_RED):
			r = scaleUpByLog(intensity);
			g = 0f;
			b = scaleDownByLog(intensity);
			break;
		case (BLUE_RED_LOG):
			r = scaleUpByLog(intensity);
			g = 0f;
			b = scaleDownByNegLog(intensity);;
			break;
		case (YELLOW_RED):
			r = 1f;
			g = scaleDownLinear(intensity);
			b = 0f;
			break;
		case (YELLOW_RED_LOG):
			r = 1f;
			g = scaleDownByNegLog(intensity);
			b = 0f;
			break;
		case (ICE_FIRE):
			r = scaleUpByTopQuarterSin(intensity);
			g = scaleDownByTopQuarterSin(intensity);
			b = scaleDownByBottomQuarterSin(intensity);
			break;
		case (YELLOW_MAGENTA_RED_GRADIENT):
			r = 1f;
			g = scaleDownByNegQuad(intensity);
			b = ( 1f - (float)Math.pow(2 * intensity - 1f, 2));
			break;
		case (BLUE_YELLOW_RED):
			//. 0% blue, 50% yellow, 100% red
			//. r    0     255       255
			//. g    0     255        0
			//. b   255     0         0
			if (intensity < 1/2.0) {
				r = intensity * 2f;
				g = intensity * 2f;
				b = (0.5f - intensity) *2f;
			} else {
				r = 1f;
				g = (1-intensity) * 2f;
				b = 0;
			}
			break;
		case (BLUE_CYAN_YELLOW_RED):
			//. 0% blue, 33% cyan, 66% yellow, 100% red
			//. r    0       0        255       255
			//. g    0      255       255        0
			//. b   255     255        0         0
			if (intensity < 1/3.0) {
				r = 0;
				g = intensity * 3;
				b = 1f;
			} else if (intensity > 2/3.0) {
				r = 1f;
				g = 3f * (1 - intensity);
				b = 0;
			} else {
				r = (intensity - 1/3.0f) * 3f;
				g = 1f;
				b = (2/3.0f - intensity) * 3f;
			}
			break;
		case (BLUE_GREEN_YELLOW_RED):
			//. 0% blue, 33% green, 66% yellow, 100% red
			//. r    0       0        255       255
			//. g    0      255       255        0
			//. b   255      0        0         0
			if (intensity < 1/3.0) {
				r = 0f;
				g = intensity * 3;
				b = (1f/3.0f - intensity) * 3f;
			} else if (intensity > 2/3.0) {
				r = 1f;
				g = 3f * (1 - intensity);
				b = 0f;
			} else {
				r = (intensity - 1/3.0f) * 3f;
				g = 1f;
				b = 0f;
			}
			break;
		case (RAINBOW):
			//. purple - blue - green - yellow - orange - red - magenta
			//.    0      1/6    1/3      1/2     2/3     5/6     1
			//. r 128      0      0       255     255     255    255
			//. g  0       0     255      255     127      0      0
			//. b 255     255     0       0       0       0     255
			if (intensity < 1f/6f) {
				r = 0.5f * (1f/6f - intensity) * 6f;
				g = 0f;
				b = 1f;
			} else if (intensity < 1f/3f) {
				r = 0f;
				g = (intensity - 1f/6f) * 6f;
				b = (1f/3f - intensity) * 6f;
			} else if (intensity < 1f/2f) {
				r = (intensity - 1f/3f) * 6f;
				g = 1f;
				b = 0f;
			} else if (intensity < 2f/3f) {
				r = 1f;
				g = 0.5f * (2f/3f - intensity) * 6f + 0.5f;
				b = 0f;
			} else if (intensity < 5f/6f) {
				r = 1f;
				g = 0.5f * (5f/6f - intensity) * 6f;
				b = 0f;
			} else {
				r = 1f;
				g = 0f;
				b = (intensity - 5f/6f) * 6f;
			}
			break;
		case (FULL_VISIBLE_SPECTRUM):
			float wl = intensity * (MAXIMUM_VISIBLE_WAVELENGTH_NM - MINIMUM_VISIBLE_WAVELENGTH_NM) + MINIMUM_VISIBLE_WAVELENGTH_NM;
			return wvColor(wl, gamma);
		case (FULL_INTENSITY_VISIBLE_SPECTRUM):
			float fwl = intensity * (MAXIMUM_FULL_INTENSITY_WAVELENGTH_NM - MINIMUM_FULL_INTENSITY_WAVELENGTH_NM) + MINIMUM_FULL_INTENSITY_WAVELENGTH_NM;
			return wvColor(fwl, gamma);
		case (GRAYSCALE_LOG):
			r = scaleUpByLog(intensity);
			g = r;
			b = r;
			break;
		case (GRAYSCALE):
		default:
			r = intensity;
			g = r;
			b = r;
			break;
		}

		
		r *= gamma;
		g *= gamma;
		b *= gamma;

		return new Color(constrain(r), constrain(g), constrain(b));
	}

	private static float scaleUpByLog(float intensity) {
		/*
		 *           ________
		 *      __---
		 *    _-
		 *   /
		 *  |
		 */
		return (float)Math.log10(1f + 9f*intensity);
	}
	private static float scaleDownByLog(float intensity) {
		/*
		 *  --------___
		 *             --_
		 *                -
		 *                 \
		 *                  |
		 */
		return (float)Math.log10(10f - 9f*intensity);
	}
	private static float scaleDownByNegLog(float intensity) {
		/* 
		 *  |
		 *   \
		 *    -_
		 *      --___
		 *           ---------
		 */
		return 1f - scaleUpByLog(intensity);
	}
	private static float scaleUpByNegLog(float intensity) {
		/*
		 *                    |
		 *                   /
		 *                 _-
		 *            ___--
		 *   ---------
		 */
		return 1f - scaleDownByLog(intensity);
	}
	private static float scaleUpByNegQuad(float intensity) {
		//. tangent: end
		/*
		 *           ________
		 *      __---
		 *    _-
		 *   /
		 *  |
		 */
		return 1f - scaleDownByQuad(intensity);
	}
	private static float scaleDownByNegQuad(float intensity) {
		//. tangent: start
		/*
		 *  --------___
		 *             --_
		 *                -
		 *                 \
		 *                  |
		 */
		return (1f - scaleUpByQuad(intensity));
	}
	private static float scaleDownByQuad(float intensity) {
		//. tangent: end
		/* 
		 *  |
		 *   \
		 *    -_
		 *      --___
		 *           ---------
		 */
		return (intensity - 1f)*(intensity - 1f);
	}
	private static float scaleUpByQuad(float intensity) {
		//. tangent: start
		/*
		 *                    |
		 *                   /
		 *                 _-
		 *            ___--
		 *   ---------
		 */
		return (intensity * intensity);
	}
	private static float scaleDownLinear(float intensity) {
		return 1f - scaleUpLinear(intensity);
	}
	private static float scaleUpLinear(float intensity) {
		return intensity;
	}
	private static float scaleUpByExp(float intensity) {
		return (float)((Math.exp(intensity) - 1)/(Math.E - 1));
	}
	private static float scaleDownByExp(float intensity) {
		return 1f - scaleUpByExp(intensity);
	}
	private static float scaleUpByNegExp(float intensity) {
		return (float)((Math.exp(-intensity) - 1)/(1/Math.E - 1));
	}
	private static float scaleDownByNegExp(float intensity) {
		return 1f - scaleUpByNegExp(intensity);
	}
	private static float scaleUpByTopQuarterSin(float intensity) {
		//. tangent: end
		return (float)(Math.sin(intensity*Math.PI/2.0));
	}
	private static float scaleDownByTopQuarterSin(float intensity) {
		//. tangent: start
		return (float)(Math.cos(intensity*Math.PI/2.0));
	}
	private static float scaleUpByBottomQuarterSin(float intensity) {
		//. tangent: start
		return 1f - scaleDownByTopQuarterSin(intensity);
	}
	private static float scaleDownByBottomQuarterSin(float intensity) {
		//. tangent: end
		return 1f - scaleUpByTopQuarterSin(intensity);
	}
	private static float scaleUpByHalfSin(float intensity) {
		//. tangent: both
		return 1f - scaleDownByHalfSin(intensity);
	}
	private static float scaleDownByHalfSin(float intensity) {
		//. tangent: both
		return (float)(0.5 * Math.cos(Math.PI*intensity) + 0.5);
	}
	private static float scaleUpByArcsin(float intensity) {
		return (float)(Math.acos(1-2*intensity)/Math.PI);
	}
	private static float scaleDownByArcsin(float intensity) {
		return (float)(Math.acos(2*intensity-1)/Math.PI);
	}
	
	public static float constrain(float value) {
		if (value < 0f) return 0f;
		if (value > 1f) return 1f;
		return value;
	}
	public static Color wvColor (float wl, float gamma ) {
		/**
		 * Create a Color object given the wavelength of the colour in nanometers.
		 * Version 1.0
		 *
		 * instead of:
		 * Color c = new Color(255, 0, 0);
		 * use the freqency in nanometers, and gamma 0.0. .. 1.0.
		 * Color c = Wavelength.wlColor( 400.0f, 0.80f );
		 *
		 * or using frequency in Terahertz and gamma 0.0. .. 1.0.
		 * Color c = Wavelength.fColor( 500.0f, 1.0f );
		 *
		 * You might use it to draw a realistic rainbow, or to write
		 * educational Applets about the light spectrum.
		 *
		 * Based on a Fortran program by Dan Bruton (astro@tamu.edu)
		 * The original Fortran can be found at:
		 * http://www.isc.tamu.edu/~astro/color.html
		 * It uses linear interpolation on ten spectral bands.
		 *
		 * @author  copyright (c) 1998-2005 Roedy Green, Canadian Mind Products
		 * may be copied and used freely for any purpose but military.
		 *
		 * Roedy Green
		 * Canadian Mind Products
		 * #327 - 964 Heywood Avenue
		 * Victoria, BC Canada V8V 2Y5
		 * tel: (250) 361-9093
		 * mailto:roedyg@mindprod.com
		 * http://mindprod.com
		 *
		 */
		/**
		 * red, green, blue component in range 0.0 .. 1.0.
		 */
		float r = 0;
		float g = 0;
		float b = 0;

		/**
		 * intensity 0.0 .. 1.0
		 * based on drop off in vision at low/high wavelengths
		 */
		float s = 1;

		/**
		 * We use different linear interpolations on different bands.
		 * These numbers mark the upper bound of each band.
		 */
		final float [] bands = { 380, 420, 440, 490, 510, 580, 645, 700, 780, Float.MAX_VALUE};

		/**
		 * Figure out which band we fall in.  A point on the edge
		 * is considered part of the lower band.
		 */
		int band = bands.length - 1;
		for ( int i=0; i<bands.length; i++ )
		{
			if ( wl <= bands[i] )
			{
				band = i;
				break;
			}
		}
		switch ( band )
		{

		case 0:
			/* invisible below 380 */
			// The code is a little redundant for clarity.
			// A smart optimiser can remove any r=0, g=0, b=0.
			r = 0;
			g = 0;
			b = 0;
			s = 0;
			break;

		case 1:
			/* 380 .. 420, intensity drop off. */
			r = (440-wl)/(440-380);
			g = 0;
			b = 1;
			s = .3f + .7f*(wl-380)/(420-380);
			break;

		case 2:
			/* 420 .. 440 */
			r = (440-wl)/(440-380);
			g = 0;
			b = 1;
			break;

		case 3:
			/* 440 .. 490 */
			r = 0;
			g = (wl-440)/(490-440);
			b = 1;
			break;

		case 4:
			/* 490 .. 510 */
			r = 0;
			g = 1;
			b = (510-wl)/(510-490);
			break;

		case 5:
			/* 510 .. 580 */
			r = (wl-510)/(580-510);
			g = 1;
			b = 0;
			break;

		case 6:
			/* 580 .. 645 */
			r = 1;
			g = (645-wl)/(645-580);
			b = 0;
			break;

		case 7:
			/* 645 .. 700 */
			r = 1;
			g = 0;
			b = 0;
			break;

		case 8:
			/* 700 .. 780, intensity drop off */
			r = 1;
			g = 0;
			b = 0;
			s = .3f + .7f*(780-wl)/(780-700);
			break;

		case 9:
			/* invisible above 780 */
			r = 0;
			g = 0;
			b = 0;
			s = 0;
			break;

		} // end switch

		// apply intensity and gamma corrections.
		s *= gamma;
		r *= s;
		g *= s;
		b *= s;

		return new Color(r, g, b);

	} // end wvColor
}
