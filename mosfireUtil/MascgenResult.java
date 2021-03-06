package edu.ucla.astro.irlab.mosfire.util;


public class MascgenResult {

	private RaDec center;
	private double positionAngle;
	private double totalPriority;
	private AstroObj[] astroObjects;
	private AstroObj[] legalAlignmentStars;
	private boolean coordWrap = false;
	public MascgenResult() {
		this(new RaDec(), 0.0, 0, new AstroObj[0], new AstroObj[0]);
	}
	public MascgenResult(RaDec center, double positionAngle, long totalPriority,
			AstroObj[] astroObjects, AstroObj[] legalAlignmentStars) {
		this.center = center;
		this.positionAngle = positionAngle;
		this.totalPriority = totalPriority;
		this.astroObjects = astroObjects;
		this.legalAlignmentStars = legalAlignmentStars;
	}
	public RaDec getCenter() {
		return center;
	}
	public void setCenter(RaDec center) {
		this.center = center;
	}
	public double getPositionAngle() {
		return positionAngle;
	}
	public void setPositionAngle(double positionAngle) {
		this.positionAngle = positionAngle;
	}
	public double getTotalPriority() {
		return totalPriority;
	}
	public void setTotalPriority(double totalPriority) {
		this.totalPriority = totalPriority;
	}
	public AstroObj[] getAstroObjects() {
		return astroObjects;
	}
	public void setAstroObjects(AstroObj[] astroObjects) {
		this.astroObjects = astroObjects;
	}
	public AstroObj[] getLegalAlignmentStars() {
		return legalAlignmentStars;
	}
	public void setLegalAlignmentStars(AstroObj[] legalAlignmentStars) {
		this.legalAlignmentStars = legalAlignmentStars;
	}
	public void setCoordWrap(boolean coordWrap) {
		this.coordWrap = coordWrap;
	}
	public boolean isCoordWrap() {
		return coordWrap;
	}

}
