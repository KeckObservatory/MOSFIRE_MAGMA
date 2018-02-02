package edu.ucla.astro.irlab.mosfire.util;


public class MechanicalSlit extends SlitPosition implements Cloneable {
	private int slitRows;

	public MechanicalSlit(int number, double center, double width,
			AstroObj target, double targetCenterDistance) {
		super(number, center, width, target, targetCenterDistance);
		slitRows = -1;
	}

	public MechanicalSlit(int number, double center, double width, AstroObj target) {
		this(number, center, width, target, 0.0);
	}

	public MechanicalSlit(int number, double center, double width) {
		this(number, center, width, new AstroObj());
	}

	public MechanicalSlit(double center, double width) {
		this(-1, center, width);
	}

	public MechanicalSlit(int number) {
		this(number, 0.0, 0.0);
	}
	public double getRightBarPositionInMM() {
		return ((getCenterPosition() - getSlitWidth()/2)/MosfireParameters.CSU_ARCSEC_PER_MM) + MosfireParameters.CSU_ZERO_PT;
	}
	public double getLeftBarPositionInMM() {
		return ((getCenterPosition() + getSlitWidth()/2)/MosfireParameters.CSU_ARCSEC_PER_MM) + MosfireParameters.CSU_ZERO_PT;
	}
	
	public int getRightBarNumber() {
		return (getSlitNumber() * 2) - 1;
	}
	public int getLeftBarNumber() {
		return getSlitNumber() * 2;
	}
	public int getSlitRows() {
		return slitRows;
	}
	public void setSlitRows(int slitRows) {
		this.slitRows = slitRows;
	}

	public String toString() {
		return "MechanicalSlit ("+getSlitNumber()+"): center:"+getCenterPosition()+", width:"+getSlitWidth();
	}

  public MechanicalSlit clone() {
  	try {
			return (MechanicalSlit) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new AssertionError();
		}
  }

}
