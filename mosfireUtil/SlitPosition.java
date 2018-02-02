package edu.ucla.astro.irlab.mosfire.util;


public class SlitPosition {
	private int slitNumber;
	private double slitWidth;       //. in arcsecs
	private AstroObj target;
	private double centerDistance;  //. for alignment slits
	private double centerPosition;  //. in arcsecs, from center of CSU

	public SlitPosition(int number, double center, double width, AstroObj target, double targetCenterDistance) {
		this.slitNumber = number;
		this.centerPosition = center;
		this.slitWidth = width;
		this.target = target;
		this.centerDistance = targetCenterDistance;
	}
	public SlitPosition(int number, double center, double width, AstroObj target) {
		this(number, center, width, target, 0.0);
	}
	public SlitPosition(int number, double center, double width) {
		this(number, center, width, new AstroObj());
	}
	public SlitPosition(double center, double width) {
		this(-1, center, width);
	}
	public SlitPosition(int number) {
		this(number, 0.0, 0.0);
	}
	public SlitPosition() {
		this(-1);
	}
	public double getSlitWidth() {
		return slitWidth;
	}
	public void setSlitWidth(double slitWidth) {
		this.slitWidth = slitWidth;
	}
	
	public int getSlitNumber() {
		return slitNumber;
	}
	public void  setSlitNumber(int number) {
		slitNumber = number;
	}
	public void setTarget(AstroObj target) {
		this.target = target;
	}
	public AstroObj getTarget() {
		return target;
	}
	public double getCenterDistance() {
		return centerDistance;
	}
	public void setCenterDistance(double targetCenterDistance) {
		this.centerDistance = targetCenterDistance;
	}
	public String getTargetName() {
		return target.getObjName();
	}
	public void setTargetName(String targetName) {
		target.setObjName(targetName);
	}
	public double getCenterPosition() {
		return centerPosition;
	}
	public void setCenterPosition(double centerPosition) {
		this.centerPosition = centerPosition;
	}  

}
