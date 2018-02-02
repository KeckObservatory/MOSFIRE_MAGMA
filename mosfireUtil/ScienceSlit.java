package edu.ucla.astro.irlab.mosfire.util;


public class ScienceSlit extends SlitPosition implements Cloneable {
	private RaDec slitRaDec;
	private double slitLength;
	
	public ScienceSlit(int number, RaDec raDec, double width, double length, AstroObj obj, double centerDist) {
		super(number, 0.0, width, obj, centerDist);
		slitRaDec = raDec;
		slitLength = length;
	}
	
	public ScienceSlit(int number, MechanicalSlit pos) {
		this(number, new RaDec(), pos.getSlitWidth(), 
				pos.getSlitRows() * MosfireParameters.CSU_ROW_HEIGHT - MosfireParameters.OVERLAP,
				pos.getTarget(), 0.0);
	}
	public ScienceSlit(int number) {
		this(number, new RaDec(), 0.0, 0.0, new AstroObj(), 0.0);
	}
	public ScienceSlit() {
		this(-1);
	}
	
	public RaDec getSlitRaDec() {
		return slitRaDec;
	}
	public void setSlitRaDec(RaDec slitRaDec) {
		this.slitRaDec = slitRaDec;
	}
	public double getSlitLength() {
		return slitLength;
	}
	public void setSlitLength(double slitLength) {
		this.slitLength = slitLength;
	}
	public int getSlitRows() {
		return getSlitRowsFromLength(slitLength);
	}
	public static int getSlitRowsFromLength(double length) {
		double verticalSlitLength = length * Math.cos(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
		//. l = slitLength, r = slitRows, h = SINGLE_SLIT_HEIGHT, o = OVERLAP
		//. l = rh + o(r-1) = rh + ro -o
		//. r(h+o) = l + o
		return (int)Math.round((verticalSlitLength + MosfireParameters.OVERLAP)/MosfireParameters.CSU_ROW_HEIGHT);
	}
	public static double getSlitLengthFromRows(int rows) {
		double verticalSlitLength = rows * MosfireParameters.SINGLE_SLIT_HEIGHT + (rows - 1) * MosfireParameters.OVERLAP;
		return verticalSlitLength / Math.cos(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
	}
	public ScienceSlit clone() {
		try {
			return (ScienceSlit)super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new AssertionError();
		}
	}
}
