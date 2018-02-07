package edu.ucla.astro.irlab.mosfire.mscgui;

import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.CSU_FP_RADIUS;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.CSU_HEIGHT;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.CSU_WIDTH;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.STAR_EDGE_DISTANCE;
import static edu.ucla.astro.irlab.mosfire.util.MosfireParameters.CSU_NUMBER_OF_BAR_PAIRS;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ucla.astro.irlab.mosfire.util.AstroObj;
import edu.ucla.astro.irlab.mosfire.util.MascgenArgumentException;
import edu.ucla.astro.irlab.mosfire.util.MascgenArguments;
import edu.ucla.astro.irlab.mosfire.util.MascgenResult;
import edu.ucla.astro.irlab.mosfire.util.MascgenTransforms;
import edu.ucla.astro.irlab.mosfire.util.MosfireParameters;
import edu.ucla.astro.irlab.mosfire.util.RaDec;
import edu.ucla.astro.irlab.mosfire.util.TargetListFormatException;
import edu.ucla.astro.irlab.mosfire.util.TargetListParser;
import edu.ucla.astro.irlab.util.TopPriorityList;

public class MascgenCore {
	private static final Logger logger = Logger.getLogger(MascgenCore.class);
	//. TODO make constant for CSU_WIDTH/2, CSU_HEIGHT/2
	private double circleOriginX = CSU_WIDTH / 2;
	private double circleOriginY = CSU_HEIGHT / 2;

	private double minLegalX;
	private double maxLegalX;
	private double xCenter;
	private double ditherSpace;
	private double alignmentStarEdgeBuffer;
	
	private String mascgenStatus="";
	private int mascgenRunNumber=0;
	private int mascgenTotalRuns=0;
	private double mascgenTotalPriority=0.0;
	private int mascgenOptimalRunNumber=0;
	private volatile boolean abort=false;
	
	private ArrayList<Node> bestNodes = new ArrayList<Node>(CSU_NUMBER_OF_BAR_PAIRS);
	private ArrayList<ArrayList<Node>> allNodes = new ArrayList<ArrayList<Node>>(CSU_NUMBER_OF_BAR_PAIRS);

	//////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M6 by Ji Man Sohn, UCLA 2016-2017 //
	private int numTopConfigs = 0;
	//////////////////////////////////////////////////////////////////

  /** Additional Functions 
   * @throws TargetListFormatException 
   * @throws IOException **/
	//Read in file and return array of AstroObjs.
	private static ArrayList<AstroObj> readInFile(String fileName) throws NumberFormatException, IOException, TargetListFormatException {
		ArrayList<AstroObj> astroObjArrayList = TargetListParser.parseFile(fileName);
		
		// remove a center line
		// must be first line in the file, name = CENTER, Priority=9999
		if (astroObjArrayList.get(0).getObjName() == "CENTER" && 
				astroObjArrayList.get(0).getObjPriority() == 9999){
			astroObjArrayList.remove(0);
		}

		return astroObjArrayList;
	}

	//  The optimize method takes in an array of AstroObjs and the user-defined
	// CSU Parameters, center position (in right ascension and declination),
	// and position angle (in degrees). It returns an array of AstroObjs (of 
	// length less than or equal to 46) which compose the slit mask 
	// configuration with the highest total priority. If this array is passed to
	// the method slitConfigurationGenerator, it will return the corresponding
	// optimal slit configuration.
	private Node optimize(HashSet<AstroObj> allObjects, RaDec centerPosition, double pa) {
		//. variable to store highest score
		double currentMax=0;
		//. variable to store best top node, returned by method
		Node bestTopNode = new Node();

		//. clear node arrays
		resetNodes();

		//. convert PA to radians
		double theta = Math.toRadians(pa);

		double xOld, yOld;
		double objX, objY;

		for (AstroObj obj : allObjects) {
			// Transform the entire astroObjArray into the CSU plane by subtracting
			// the center coordinate from each AstroObj's xCoordinate and 
			// yCoordinate and putting these into the ObjX and ObjY.
			xOld = obj.getWcsX() - centerPosition.getXCoordinate();
			yOld = obj.getWcsY() - centerPosition.getYCoordinate();

			// Rotate the objects in the CSU plane by the Position Angle.
			/* Objects were read in with coordinate system origin at center of 
			 *  CSU field. The optimize method runs with the coordinate system 
			 *  origin in the lower left. So, simply add CSU_WIDTH / 2 to the x  
			 *  position and CSU_HEIGHT / 2 to the y position of each object. 
			 */
			objX = (xOld * Math.cos(theta) - yOld * Math.sin(theta));
			objY = (xOld * Math.sin(theta) + yOld * Math.cos(theta));

			/* Crop out all AstroObjs in the astroObjArray that 
			 * 
			 * a) lie outside the  focal plane circle, defined by CSU_FP_RADIUS 
			 *    centered at the origin (CSU_WDITH / 2, CSU_HEIGHT / 2). 
			 * b) have x coordinate positions outside of the legal range. 
			 * c) have x coordinate positions outside of the CSU Plane. 
			 */
			//. need to check object stays in slit during dither
			//.
			//. coordinate system origin is at center, and goes positive to the left, and up
			//. with slits tilted 4 degrees counter-clockwise
			double maxY = objY + ditherSpace * Math.cos(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
			double maxX = objX + ditherSpace * Math.sin(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
			double minY = objY - ditherSpace * Math.cos(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
			double minX = objX - ditherSpace * Math.sin(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
			if ((Point.distance(minX, minY, 0, 0) < CSU_FP_RADIUS) && 
					(Point.distance(maxX, maxY, 0, 0) < CSU_FP_RADIUS) && 
					(minX >= minLegalX) && (maxX <= maxLegalX) && (minX > -CSU_WIDTH/2.) && (maxX < CSU_WIDTH/2.) &&
					(minY > -CSU_HEIGHT/2.) && (maxY < CSU_HEIGHT/2.)) {

				obj.setObjX(objX);
				obj.setObjY(objY);

				//. determine what rows the object occupies during full dither
				int minRow = (int)Math.floor((minY + CSU_HEIGHT / 2. - MosfireParameters.OVERLAP)/ MosfireParameters.CSU_ROW_HEIGHT);
				int maxRow = (int)Math.floor((maxY + CSU_HEIGHT / 2. + MosfireParameters.OVERLAP)/ MosfireParameters.CSU_ROW_HEIGHT);
				obj.setMinRow(minRow);
				obj.setMaxRow(maxRow);
				
				//. make sure object stays within mask boundaries during dither
				//. this should be ensured by check above, but better to 
				//. include this just be sure, to avoid IndexOutOfBoundsException
				if ((minRow >= 0) &&  (maxRow < CSU_NUMBER_OF_BAR_PAIRS)) {
					//. if so, add to node list
					allNodes.get(maxRow).add(new Node(obj));
				}
			}
		}

		//. go down mask and find best node path
		//. tracking best nodes at each row as you go
		for (int rowNum=0; rowNum<CSU_NUMBER_OF_BAR_PAIRS; rowNum++) {
			//. initialize best node for row
			Node currentBestNode = new Node();
			
			//. for initial blank node, set next node as best node above it
			if (rowNum > 0) {
				currentBestNode.setNextNode(bestNodes.get(rowNum-1));
			}
			
			//. go through all nodes in row
			for (Node node : allNodes.get(rowNum)) {
				
				//. extract object from node
				AstroObj currentObj = node.getObj();

				//. if node doesn't have a score yet, give it a score of the priority of the object in it.
				if (node.getScore() == 0) {
					node.setScore(currentObj.getObjPriority());
				}
				
				//. get the index of the node before the extents of this one 
				int previousNodeNum = rowNum - (currentObj.getMaxRow() - currentObj.getMinRow()) - 1;
				
				//. make sure index is valid, and if so, set the best node at that index as the next node of this node
				if (previousNodeNum >= 0) {
					node.setNextNode(bestNodes.get(previousNodeNum));
				}
				
				double currentNodeScore = totalScore(node);
				double currentBestNodeScore = totalScore(currentBestNode);
				//. if this node gives the best total score so far, save it as the current best node for this row
				if (currentNodeScore > currentBestNodeScore) {
					currentBestNode = node;
				} else if (currentNodeScore == currentBestNodeScore) {
					//. if they are equal, use the node closest to xcenter
					double currentNodeX = currentObj.getObjX();
					//. now, currentBestNode might not have an object in it
					//. so, we'll use the current node if it is closer to xcenter
					//. than any of the objects in the current best node
					//. that this node would replace.
					//. therefore, follow the path of the best node until 
					//. we are beyond the minRow of this node.
					int currentRow = rowNum;
					Node checkNode = currentBestNode;
					AstroObj checkObj;
					while (currentRow >= node.getObj().getMinRow()) {
						checkObj = checkNode.getObj();
						if (checkObj.isNotBlank()) {
							
							if (Math.abs((currentNodeX) - xCenter * 60) < Math.abs((checkObj.getObjX()) - xCenter * 60)) {
								currentBestNode = node;
								break;
							}
							
						}
						checkNode = checkNode.getNextNode();
						if (checkNode == null) {
							break;
						}
						currentRow = checkNode.getObj().getMaxRow();
					}

				}
			}
			
			//. get the object for the best node of this row, after going through the all possible nodes
			AstroObj currentObj = currentBestNode.getObj();
			
			//. if there is an object there, we need to check to see if it is better without it
			if (currentObj.isNotBlank()) {
				
				//. go through all nodes this node would preclude
				for (int ii=rowNum-1; ii>=rowNum - (currentObj.getMaxRow() - currentObj.getMinRow()); ii--) {
					
					//. make sure we don't get an invalid index
					if (ii < 0) break;
					
					//. get the best node for this row
					Node priorBestNode = bestNodes.get(ii);
					
					//. if it improves the score, reset the current best node to no object, 
					//. and set its next node to this better one
					double priorBestNodeScore = totalScore(priorBestNode);
					double currentBestNodeScore = totalScore(currentBestNode);
					if (priorBestNodeScore > currentBestNodeScore) {
						currentBestNode = new Node();
						currentBestNode.setNextNode(priorBestNode);
					} else if (priorBestNodeScore == currentBestNodeScore) {
						//. TODO check for closeness?
						logger.trace("row <"+rowNum+"-"+ii+">, prior <"+priorBestNode+"> = current <"+currentBestNode+">");
					}
				}
			}
			
			//. set the best node for this row
			bestNodes.set(rowNum, currentBestNode);
			
			//. get the current score with this node at the top of the path
			double currentScore = totalScore(currentBestNode);
			//System.out.println("current score = " + currentScore);
			
			//. if it is the best yet, save it
			if (currentScore > currentMax) {
				currentMax = currentScore;
				bestTopNode = currentBestNode;
			}
		}
		
		//. having gone through all the nodes, we now have the best node to start the path
		return bestTopNode;
		
	}
	
	//  The optimize method takes in an array of AstroObjs and the user-defined
	// CSU Parameters, center position (in right ascension and declination),
	// and position angle (in degrees). It returns an array of AstroObjs (of 
	// length less than or equal to 46) which compose the slit mask 
	// configuration with the highest total priority. If this array is passed to
	// the method slitConfigurationGenerator, it will return the corresponding
	// optimal slit configuration.
	private Node optimizeOld(HashSet<AstroObj> allObjects, RaDec centerPosition, double pa) {
		//. variable to store highest score
		double currentMax=0;
		//. variable to store best top node, returned by method
		Node bestTopNode = new Node();

		//. clear node arrays
		resetNodes();

		//. convert PA to radians
		double theta = Math.toRadians(pa);

		double xOld, yOld;
		double objX, objY;

		for (AstroObj obj : allObjects) {
			// Transform the entire astroObjArray into the CSU plane by subtracting
			// the center coordinate from each AstroObj's xCoordinate and 
			// yCoordinate and putting these into the ObjX and ObjY.
			xOld = obj.getWcsX() - centerPosition.getXCoordinate();
			yOld = obj.getWcsY() - centerPosition.getYCoordinate();

			// Rotate the objects in the CSU plane by the Position Angle.
			/* Objects were read in with coordinate system origin at center of 
			 *  CSU field. The optimize method runs with the coordinate system 
			 *  origin in the lower left. So, simply add CSU_WIDTH / 2 to the x  
			 *  position and CSU_HEIGHT / 2 to the y position of each object. 
			 */
			objX = (xOld * Math.cos(theta) - yOld * Math.sin(theta));
			objY = (xOld * Math.sin(theta) + yOld * Math.cos(theta));

			/* Crop out all AstroObjs in the astroObjArray that 
			 * 
			 * a) lie outside the  focal plane circle, defined by CSU_FP_RADIUS 
			 *    centered at the origin (CSU_WDITH / 2, CSU_HEIGHT / 2). 
			 * b) have x coordinate positions outside of the legal range. 
			 * c) have x coordinate positions outside of the CSU Plane. 
			 */
			//. need to check object stays in slit during dither
			//.
			//. coordinate system origin is at center, and goes positive to the left, and up
			//. with slits tilted 4 degrees counter-clockwise
			double maxY = objY + ditherSpace * Math.cos(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
			double maxX = objX + ditherSpace * Math.sin(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
			double minY = objY - ditherSpace * Math.cos(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
			double minX = objX - ditherSpace * Math.sin(MosfireParameters.CSU_SLIT_TILT_ANGLE_RADIANS);
			if ((Point.distance(minX, minY, 0, 0) < CSU_FP_RADIUS) && 
					(Point.distance(maxX, maxY, 0, 0) < CSU_FP_RADIUS) && 
					(minX >= minLegalX) && (maxX <= maxLegalX) && (minX > -CSU_WIDTH/2.) && (maxX < CSU_WIDTH/2.) &&
					(minY > -CSU_HEIGHT/2.) && (maxY < CSU_HEIGHT/2.)) {

				// "Hard copy" the input AstroObj array so that subsequent optimize
				// calls are disrupted by any changes to the original 
				// astroObjArrayOriginal as read in.
				AstroObj newObj = obj.clone();

				newObj.setObjX(objX);
				newObj.setObjY(objY);

				//. determine what rows the object occupies during full dither
				int minRow = (int)Math.floor((minY + CSU_HEIGHT / 2. - MosfireParameters.OVERLAP)/ MosfireParameters.CSU_ROW_HEIGHT);
				int maxRow = (int)Math.floor((maxY + CSU_HEIGHT / 2. + MosfireParameters.OVERLAP)/ MosfireParameters.CSU_ROW_HEIGHT);
				newObj.setMinRow(minRow);
				newObj.setMaxRow(maxRow);
				
				//. make sure object stays within mask boundaries during dither
				//. this should be ensured by check above, but better to 
				//. include this just be sure, to avoid IndexOutOfBoundsException
				if ((minRow >= 0) &&  (maxRow < CSU_NUMBER_OF_BAR_PAIRS)) {
					//. if so, add to node list
					allNodes.get(maxRow).add(new Node(newObj));
				}
			}
		}

		//. go down mask and find best node path
		//. tracking best nodes at each row as you go
		for (int rowNum=0; rowNum<CSU_NUMBER_OF_BAR_PAIRS; rowNum++) {
			//. initialize best node for row
			Node currentBestNode = new Node();
			
			//. for initial blank node, set next node as best node above it
			if (rowNum > 0) {
				currentBestNode.setNextNode(bestNodes.get(rowNum-1));
			}
			
			//. go through all nodes in row
			for (Node node : allNodes.get(rowNum)) {
				
				//. extract object from node
				AstroObj currentObj = node.getObj();

				//. if node doesn't have a score yet, give it a score of the priority of the object in it.
				if (node.getScore() == 0) {
					node.setScore(currentObj.getObjPriority());
				}
				
				//. get the index of the node before the extents of this one 
				int previousNodeNum = rowNum - (currentObj.getMaxRow() - currentObj.getMinRow()) - 1;
				
				//. make sure index is valid, and if so, set the best node at that index as the next node of this node
				if (previousNodeNum >= 0) {
					node.setNextNode(bestNodes.get(previousNodeNum));
				}
				
				double currentNodeScore = totalScore(node);
				double currentBestNodeScore = totalScore(currentBestNode);
				//. if this node gives the best total score so far, save it as the current best node for this row
				if (currentNodeScore > currentBestNodeScore) {
					currentBestNode = node;
				} else if (currentNodeScore == currentBestNodeScore) {
					//. if they are equal, use the node closest to xcenter
					double currentNodeX = currentObj.getObjX();
					//. now, currentBestNode might not have an object in it
					//. so, we'll use the current node if it is closer to xcenter
					//. than any of the objects in the current best node
					//. that this node would replace.
					//. therefore, follow the path of the best node until 
					//. we are beyond the minRow of this node.
					int currentRow = rowNum;
					Node checkNode = currentBestNode;
					AstroObj checkObj;
					while (currentRow >= node.getObj().getMinRow()) {
						checkObj = checkNode.getObj();
						if (checkObj.isNotBlank()) {
							
							if (Math.abs((currentNodeX) - xCenter * 60) < Math.abs((checkObj.getObjX()) - xCenter * 60)) {
								currentBestNode = node;
								break;
							}
							
						}
						checkNode = checkNode.getNextNode();
						if (checkNode == null) {
							break;
						}
						currentRow = checkNode.getObj().getMaxRow();
					}

				}
			}
			
			//. get the object for the best node of this row, after going through the all possible nodes
			AstroObj currentObj = currentBestNode.getObj();
			
			//. if there is an object there, we need to check to see if it is better without it
			if (currentObj.isNotBlank()) {
				
				//. go through all nodes this node would preclude
				for (int ii=rowNum-1; ii>=rowNum - (currentObj.getMaxRow() - currentObj.getMinRow()); ii--) {
					
					//. make sure we don't get an invalid index
					if (ii < 0) break;
					
					//. get the best node for this row
					Node priorBestNode = bestNodes.get(ii);
					
					//. if it improves the score, reset the current best node to no object, 
					//. and set its next node to this better one
					double priorBestNodeScore = totalScore(priorBestNode);
					double currentBestNodeScore = totalScore(currentBestNode);
					if (priorBestNodeScore > currentBestNodeScore) {
						currentBestNode = new Node();
						currentBestNode.setNextNode(priorBestNode);
					} else if (priorBestNodeScore == currentBestNodeScore) {
						//. TODO check for closeness?
						logger.trace("row <"+rowNum+"-"+ii+">, prior <"+priorBestNode+"> = current <"+currentBestNode+">");
					}
				}
			}
			
			//. set the best node for this row
			bestNodes.set(rowNum, currentBestNode);
			
			//. get the current score with this node at the top of the path
			double currentScore = totalScore(currentBestNode);
			//System.out.println("current score = " + currentScore);
			
			//. if it is the best yet, save it
			if (currentScore > currentMax) {
				currentMax = currentScore;
				bestTopNode = currentBestNode;
			}
		}
		
		//. having gone through all the nodes, we now have the best node to start the path
		return bestTopNode;
		
	}
	
	private static double totalScore(Node node) {
		double total=0;
		Node nextNode = node;
		while(nextNode != null) {
			total += nextNode.getScore();
			nextNode = nextNode.getNextNode();
		}
		return total;
		
	}
	private void resetNodes() {
		bestNodes.clear();
		allNodes.clear();
		for (int ii=0; ii<CSU_NUMBER_OF_BAR_PAIRS; ii++) {
			allNodes.add(new ArrayList<Node>());
			bestNodes.add(new Node());
		}		
	}

	//  The findLegalStars method takes in an array of AstroObjs containing the alignment star
	// information and the user-defined
	// CSU Parameters, center position (in right ascension and declination),
	// and position angle (in degrees). It returns an array of AstroObjs
	// with the (4) stars to be used for the alignment.
	public AstroObj[] findLegalStars(HashSet<AstroObj> astroStarObjArrayOriginal, RaDec centerPosition, double pa) {

		double theta = Math.toRadians(pa);

		double xOld, yOld;
		double objX, objY;
		// "Hard copy" the input AstroObj array so that subsequent optimize
		// calls are disrupted by any changes to the original
		// astroObjArrayOriginal as read in.
		ArrayList<AstroObj> astroObjArrayList = new ArrayList<AstroObj>();
		RaDec objectRaDec;
		Point2D.Double objWcs;
		int row;
		for  (AstroObj obj : astroStarObjArrayOriginal) {
			
			// Transform the entire astroObjArray into the CSU plane by subtracting
			// the center coordinate from each AstroObj's xCoordinate and
			// yCoordinate and putting these into the ObjX and ObjY.
			objectRaDec = new RaDec((int)Math.floor(obj.getRaHour()), (int)Math.floor(obj.getRaMin()), obj.getRaSec(), obj.getDecDeg(), obj.getDecMin(), obj.getDecSec());
			objWcs = MascgenTransforms.getWcsFromRaDec(objectRaDec, centerPosition.getYCoordinate());
			
			xOld = objWcs.x - centerPosition.getXCoordinate();
			yOld = objWcs.y - centerPosition.getYCoordinate();
			
			// Rotate the objects in the CSU plane by the Position Angle.
			/** Objects were read in with coordinate system origin at center of
			 *  CSU field. The optimize method runs with the coordinate system
			 *  origin in the lower left. So, simply add CSU_WIDTH / 2 to the x
			 *  position and CSU_HEIGHT / 2 to the y position of each object. **/
			objX = xOld * Math.cos(theta) - yOld * Math.sin(theta) + CSU_WIDTH / 2;
			objY = xOld * Math.sin(theta) + yOld * Math.cos(theta) + CSU_HEIGHT / 2;
			
			/** Crop out all AstroObjs in the astroObjArray that lie outside the
			 * focal plane circle, defined by CSU_FP_RADIUS centered at the origin
			 * (CSU_WDITH / 2, CSU_HEIGHT / 2). **/
			/** Crop out all AstroObjs in the astroObjArray that have x coordinate
			 * positions outside of the CSU Plane. **/
			if (Point.distance(objX, objY, circleOriginX, circleOriginY) < CSU_FP_RADIUS) {

				if ((objX > STAR_EDGE_DISTANCE) && (objX < CSU_WIDTH-STAR_EDGE_DISTANCE)) {

					if ((objY > STAR_EDGE_DISTANCE) && (objY < CSU_HEIGHT-STAR_EDGE_DISTANCE)){ 

						
						/** Assign each Star AstroObj to its correct StarRowRegion.
						 * Then crop out the objects that do not fall into a RowRegion. **/
						row=AstroObj.getRow(objY, alignmentStarEdgeBuffer);

						if (row != -1) {
							obj.setObjRR(row);
/*
							obj.setObjX(objX - CSU_WIDTH / 2);
							obj.setObjY(objY - CSU_HEIGHT / 2);
							obj.setWcsX(objWcs.x);
							obj.setWcsY(objWcs.y);
*/
							astroObjArrayList.add(obj);
						}
					}
				}
			}                      
		}

		return astroObjArrayList.toArray(new AstroObj[astroObjArrayList.size()]);
	}

	//  The findLegalStars method takes in an array of AstroObjs containing the alignment star
	// information and the user-defined
	// CSU Parameters, center position (in right ascension and declination),
	// and position angle (in degrees). It returns an array of AstroObjs
	// with the (4) stars to be used for the alignment.
	public AstroObj[] findLegalStarsOld(HashSet<AstroObj> astroStarObjArrayOriginal, RaDec centerPosition, double pa) {

		double theta = Math.toRadians(pa);

		double xOld, yOld;
		double objX, objY;
		// "Hard copy" the input AstroObj array so that subsequent optimize
		// calls are disrupted by any changes to the original
		// astroObjArrayOriginal as read in.
		ArrayList<AstroObj> astroObjArrayList = new ArrayList<AstroObj>();
		RaDec objectRaDec;
		Point2D.Double objWcs;
		int row;
		for  (AstroObj obj : astroStarObjArrayOriginal) {
			
			// Transform the entire astroObjArray into the CSU plane by subtracting
			// the center coordinate from each AstroObj's xCoordinate and
			// yCoordinate and putting these into the ObjX and ObjY.
			objectRaDec = new RaDec((int)Math.floor(obj.getRaHour()), (int)Math.floor(obj.getRaMin()), obj.getRaSec(), obj.getDecDeg(), obj.getDecMin(), obj.getDecSec());
			objWcs = MascgenTransforms.getWcsFromRaDec(objectRaDec, centerPosition.getYCoordinate());
			
			xOld = objWcs.x - centerPosition.getXCoordinate();
			yOld = objWcs.y - centerPosition.getYCoordinate();
			
			// Rotate the objects in the CSU plane by the Position Angle.
			/** Objects were read in with coordinate system origin at center of
			 *  CSU field. The optimize method runs with the coordinate system
			 *  origin in the lower left. So, simply add CSU_WIDTH / 2 to the x
			 *  position and CSU_HEIGHT / 2 to the y position of each object. **/
			objX = xOld * Math.cos(theta) - yOld * Math.sin(theta) + CSU_WIDTH / 2;
			objY = xOld * Math.sin(theta) + yOld * Math.cos(theta) + CSU_HEIGHT / 2;
			
			/** Crop out all AstroObjs in the astroObjArray that lie outside the
			 * focal plane circle, defined by CSU_FP_RADIUS centered at the origin
			 * (CSU_WDITH / 2, CSU_HEIGHT / 2). **/
			/** Crop out all AstroObjs in the astroObjArray that have x coordinate
			 * positions outside of the CSU Plane. **/
			if (Point.distance(objX, objY, circleOriginX, circleOriginY) < CSU_FP_RADIUS) {

				if ((objX > STAR_EDGE_DISTANCE) && (objX < CSU_WIDTH-STAR_EDGE_DISTANCE)) {

					if ((objY > STAR_EDGE_DISTANCE) && (objY < CSU_HEIGHT-STAR_EDGE_DISTANCE)){ 

						
						/** Assign each Star AstroObj to its correct StarRowRegion.
						 * Then crop out the objects that do not fall into a RowRegion. **/
						row=AstroObj.getRow(objY, alignmentStarEdgeBuffer);

						if (row != -1) {
							AstroObj objClone = obj.clone();
							objClone.setObjRR(row);
							objClone.setObjX(objX - CSU_WIDTH / 2);
						  objClone.setObjY(objY - CSU_HEIGHT / 2);
						  objClone.setWcsX(objWcs.x);
						  objClone.setWcsY(objWcs.y);

							astroObjArrayList.add(objClone);
						}
					}
				}
			}                      
		}

		return astroObjArrayList.toArray(new AstroObj[astroObjArrayList.size()]);
	}


	//////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M6 by Ji Man Sohn, UCLA 2016-2017 //
	// * This method is heavily modified to keep set number of top  //
	//	 configurations. please refer to comment starts with JMS    //
	//////////////////////////////////////////////////////////////////
//	public MascgenResult run(List<AstroObj> targets, MascgenArguments args, PropertyChangeListener propertyChangeListener) throws MascgenArgumentException {
	public List<MascgenResult> run(List<AstroObj> targets, MascgenArguments args, PropertyChangeListener propertyChangeListener) throws MascgenArgumentException {

		HashSet<AstroObj> allObjects = new HashSet<AstroObj>();
		HashSet<AstroObj> allStars = new HashSet<AstroObj>();
		// JMS: Top Priority list to keep set number of configs with top priorities.
		TopPriorityList<MascgenResult> topResults = new TopPriorityList<MascgenResult>(numTopConfigs, new Comparator<MascgenResult>(){
			@Override
			public int compare(MascgenResult o1, MascgenResult o2) {
				if(o1.getTotalPriority()>o2.getTotalPriority()){
					return 1;
				}else if (o1.getTotalPriority()==o2.getTotalPriority()){
					return 0;
				}
				return -1;
			}
		});
		// JMS: two fields to keep track of temporary min priorities of
		//		results in the topResult list.
		double temporaryMinPriority = 0.0;
		double temporaryMaxPriority = 0.0;
		
		abort=false;
		
		setMascgenOptimalRunNumber(0, propertyChangeListener);
		setMascgenRunNumber(0, propertyChangeListener);
		setMascgenTotalPriority(0.0, propertyChangeListener);
		setMascgenTotalRuns(0, propertyChangeListener);
		setMascgenStatus("Validating arguments", propertyChangeListener);
		
		//. verify argument values

		if (args.getxSteps() < 0) {
			throw new MascgenArgumentException("Inavlid X Steps <"+args.getxSteps()+">. Must be non-negative");
		}
		if (args.getySteps() < 0) {
			throw new MascgenArgumentException("Inavlid Y Steps <"+args.getySteps()+">. Must be non-negative");
		}
		if (args.getPaSteps() < 0) {
			throw new MascgenArgumentException("Inavlid PA Steps <"+args.getPaSteps()+">. Must be non-negative");
		}

		java.text.DecimalFormat thirdPlace = new java.text.DecimalFormat("0.000");

		if ((args.getxRange() <= 0) || (args.getxRange() > CSU_WIDTH/60)) {
			throw new MascgenArgumentException("Invalid X Range <"+args.getxRange()+">. Must be between 0 and the CSU Width (" + thirdPlace.format(CSU_WIDTH / 60) + " arcmin).");
		}
		if ((args.getxCenter() <= -CSU_WIDTH / 120) || (args.getxCenter() >= CSU_WIDTH/120)) {
			throw new MascgenArgumentException("Invalid X Center <"+args.getxCenter()+">. Must be between +/- half of CSU Width (+/- " + thirdPlace.format(CSU_WIDTH / 120) + " arcmin).");
		}
		if ((args.getSlitWidth() <= 0) || (args.getSlitWidth() > CSU_WIDTH)) {
			throw new MascgenArgumentException("Invalid slit width <"+args.getSlitWidth()+">. Must be positive and less than CSU Width ("+thirdPlace.format(CSU_WIDTH) + " arcsec).");
		}
		if ((args.getDitherSpace() < 0) || (args.getDitherSpace() > CSU_HEIGHT/2)) {
			throw new MascgenArgumentException("Invalid dither space <"+args.getDitherSpace()+">. Must be non-negative and less than half of the CSU height ("+thirdPlace.format(CSU_HEIGHT / 2) + " arcsec).");
		}
		//. validate RA/DEC and PA center
		//. validate Star edge buffer

		//. minLegalX > 0? maxLegalX < CSU_WIDTH?  -> allow it, since objects not in CSU are cropped off later
		// Calculate some CSU parameters which are more useful.
//		minLegalX = 60 * (args.getxCenter() - args.getxRange() / 2) + CSU_WIDTH / 2;
//		maxLegalX = 60 * (args.getxCenter() + args.getxRange() / 2) + CSU_WIDTH / 2;
		minLegalX = 60 * (args.getxCenter() - args.getxRange() / 2);
		maxLegalX = 60 * (args.getxCenter() + args.getxRange() / 2);
		xCenter = args.getxCenter();
		ditherSpace = args.getDitherSpace();
		alignmentStarEdgeBuffer = args.getAlignmentStarEdgeBuffer();
		

		setMascgenTotalRuns((args.getxSteps()*2+1)*(args.getySteps()*2+1)*(args.getPaSteps()*2+1), propertyChangeListener);


		// Find the high and low coordinate extremes in the Input Object List.
		// Also create the astroObject that contains the information about the alignment stars
		// Alignment stars are designated by having a negative priority

		int highObjRaHour = (int) targets.get(0).getRaHour();
		int lowObjRaHour = highObjRaHour;
		int highObjDecDeg = (int) targets.get(0).getDecDeg();
		int lowObjDecDeg = highObjDecDeg;

		for (AstroObj currentObj : targets) {
			if ((int) currentObj.getRaHour() > highObjRaHour) {
				highObjRaHour = (int) (currentObj.getRaHour());
			}
			if ((int) currentObj.getRaHour() < lowObjRaHour) {
				lowObjRaHour = (int) (currentObj.getRaHour());
			}
			if ((int) currentObj.getDecDeg() > highObjDecDeg) {
				highObjDecDeg = (int) (currentObj.getDecDeg());
			}
			if ((int) currentObj.getDecDeg() < lowObjDecDeg) {
				lowObjDecDeg = (int) (currentObj.getDecDeg());
			}
			if (currentObj.getObjPriority() < 0.0) {
				allStars.add(currentObj);
			} else {
				allObjects.add(currentObj);
			}
		}
		
		// If the Input Object List covers more than one hour in RA or more
		// than one degree in Dec, throw exception
		if (highObjRaHour - lowObjRaHour > 1) {
			if ((highObjRaHour != 23) || (lowObjRaHour != 0)) {
				//. reject this, since if there is wrap, 
				//. it won't work right.
				throw new MascgenArgumentException("The input object list spans more than one hour in RA.  Reduce object list.");
			}
		}
		if (highObjDecDeg - lowObjDecDeg > 1) {
			throw new MascgenArgumentException("The input object list spans more than one degree in Dec.  Reduce object list.");
		}

		// Determine if the RA coordinates wrap around the zero line.
		boolean raCoordWrap = false;
		if ((highObjRaHour == 23) && (lowObjRaHour == 0)) {
			for (AstroObj obj : targets) {
				if (obj.getRaHour() == 0) {
					obj.setRaHour(12);
					raCoordWrap = true;
				}
				if (obj.getRaHour() == 23) {
					obj.setRaHour(11);
					raCoordWrap = true;
				}
			}
		}


		// Instantiate a new RaDec variable to store the input field center.
		RaDec fieldCenter, printedFieldCenter;
		if (args.usesCenterOfPriority()) {
			fieldCenter = calculateCenterOfPriority(allObjects);
			args.setCenterPosition(fieldCenter);
		} else {
			fieldCenter = args.getCenterPosition();
		}
		
		//. we want to print the field center in the mascgen status panel
		//. but it needs to be corrected for ra coord wrap if it was done.
		printedFieldCenter = fieldCenter.clone();
		
		if (raCoordWrap) {
			MascgenTransforms.fixRaCoordWrap(printedFieldCenter);
		}
		setMascgenStatus("Starting Center Position: "+printedFieldCenter.toStringWithUnits(), propertyChangeListener);
		
		// Compute the wcs x and y coordinates of the field center from Ra/Dec.
		MascgenTransforms.raDecToXY(fieldCenter);
		double totalPriority = 0;
		int runNum = 0; // Keep track of the number of optimization runs.
		RaDec tempFieldCenter = new RaDec();
		RaDec savedFieldCenter;
		double tempPA;

		// Now, run the three-level for loop over position angle, field center
		// y coordinate, and field center x coordinate. Count the total number
		// of loops (runNum).
		setMascgenStatus("Finding optimal mask configuration.", propertyChangeListener);

		//. run loop so that it starts at center, and works its way out.
		boolean configurationFound = false;
		// JMS: To avoid overwriting of MascgenResult, next two lines are moved down
		//		so it will be created right when a config with top priority is found.
		//MascgenResult result = new MascgenResult();
		//result.setCoordWrap(raCoordWrap);
		int xStepFactor = 0;
		int yStepFactor = 0;
		int paStepFactor = 0;
		for (int j = -args.getxSteps(); j < args.getxSteps() + 1; j++) {
			tempFieldCenter.setXCoordinate(fieldCenter.getXCoordinate() - xStepFactor * args.getxStepSize());
			xStepFactor = getNextFactor(xStepFactor);
			yStepFactor = 0;
			for (int k = -args.getySteps(); k < args.getySteps() + 1; k++){
				tempFieldCenter.setYCoordinate(fieldCenter.getYCoordinate() -	yStepFactor * args.getyStepSize());
				yStepFactor = getNextFactor(yStepFactor);
				for (AstroObj obj : allObjects) {
					MascgenTransforms.astroObjRaDecToXY(obj, tempFieldCenter);
				}
				paStepFactor = 0;
				for (int m = -args.getPaSteps(); m < args.getPaSteps() + 1; m++) {
					// JMS: To avoid overwriting of MascgenResult, next two lines are moved from above
					MascgenResult result = new MascgenResult();
					result.setCoordWrap(raCoordWrap);
					if (abort) {
						j=args.getxSteps();
						k=args.getySteps();
						m=args.getPaSteps();
					}
					setMascgenRunNumber(runNum+1, propertyChangeListener);

					double tempTotalPriority;
					tempPA = args.getCenterPA() + paStepFactor * args.getPaStepSize();
					paStepFactor = getNextFactor(paStepFactor);

					int legalNum=0;
					AstroObj[] tempStarAOArray = new AstroObj[0];
					if (args.getMinimumAlignmentStars() > 0) {
						tempStarAOArray = findLegalStars(allStars, 
								tempFieldCenter, tempPA); 
						// Now we use a hash set to find the number of unique legal stars
						// When you add an non-unique element to a hash set, nothing actually
						// gets added to the set
						HashSet<Integer> testHash = new HashSet<Integer>();

						for(AstroObj obj: tempStarAOArray){
							testHash.add(obj.getObjRR());
						}

						legalNum = testHash.size();
					}
					runNum++;

					if (legalNum >= args.getMinimumAlignmentStars()) {
						Node bestTopNode = optimize(allObjects, tempFieldCenter, tempPA);
						tempTotalPriority =  totalScore(bestTopNode);

						// JMS: this clause will be used to add new result with priority
						//		greater than minimum priority in the list.
						if (tempTotalPriority > temporaryMinPriority) {
							// JMS: If tempTotal is greater than max in the list, update temporaryMaxPriority
							if(tempTotalPriority>temporaryMaxPriority && topResults.peekFirst()!=null){
								temporaryMaxPriority = tempTotalPriority;
								setMascgenTotalPriority(temporaryMaxPriority, propertyChangeListener);
							}
							
							setMascgenOptimalRunNumber(runNum, propertyChangeListener);
							System.out.println(totalPriority + "\t" + tempTotalPriority + "\t"+ temporaryMaxPriority+ "\t"+ temporaryMinPriority);
							setMascgenStatus("-----------------------------------------------", propertyChangeListener);
							String status = "New optimum configuration " +
							"found on run number " + runNum + " with priority of " + tempTotalPriority +
							". \nThe best total priority so far is " + temporaryMaxPriority + ".";
							System.out.println(status);
							setMascgenStatus(status, propertyChangeListener);
							MascgenTransforms.xyToRaDec(tempFieldCenter);
							savedFieldCenter = tempFieldCenter.clone();
							if (raCoordWrap) {
								MascgenTransforms.fixRaCoordWrap(savedFieldCenter);
							}
							status = "Center = "+savedFieldCenter.toStringWithUnits()+", PA = "+tempPA+".";
							System.out.println(status);
							printNodePath(bestTopNode);
							setMascgenStatus(status, propertyChangeListener);
							configurationFound = true;
							result.setCenter(savedFieldCenter);
							result.setPositionAngle(tempPA);
							result.setTotalPriority(tempTotalPriority);
							result.setAstroObjects(createObjectArrayFromTopNode(bestTopNode));
							result.setLegalAlignmentStars(tempStarAOArray);
							// JMS: add the result to the list, and clean out the astro objects used.
							//		This cleaning is to not interfere with already established result.
							topResults.add(result);
							HashSet<AstroObj> tempAllObj = new HashSet<AstroObj>();
							for(AstroObj obj: allObjects){
								tempAllObj.add(obj.getCleanAstroObj());
							}
							HashSet<AstroObj> tempAllStar = new HashSet<AstroObj>();
							for(AstroObj obj: allStars){
								tempAllStar.add(obj.getCleanAstroObj());
							}
							allObjects = tempAllObj;
							allStars = tempAllStar;
							temporaryMinPriority = topResults.peekLast().getTotalPriority();
							
						} else if (tempTotalPriority == temporaryMinPriority) {
							setMascgenStatus("-----------------------------------------------", propertyChangeListener);
							String status = "Configuration with same priority " + temporaryMinPriority +
							" found on run " + runNum  + "." +
							"\nPrevious configuration being used.";
							System.out.println(status);
							setMascgenStatus(status, propertyChangeListener);
							MascgenTransforms.xyToRaDec(tempFieldCenter);
							savedFieldCenter = tempFieldCenter.clone();
							if (raCoordWrap) {
								MascgenTransforms.fixRaCoordWrap(savedFieldCenter);
							}
							status = "Center = "+savedFieldCenter.toStringWithUnits()+", PA = "+tempPA+".";
							System.out.println(status);
							if (result.getAstroObjects().length == 0) {
								result.setLegalAlignmentStars(tempStarAOArray);
								result.setCenter(savedFieldCenter);
								result.setPositionAngle(tempPA);
							}
							setMascgenStatus(status, propertyChangeListener);
						}
					} else {
						if (result.getAstroObjects().length == 0) {
							if (legalNum > result.getLegalAlignmentStars().length) {
								result.setLegalAlignmentStars(tempStarAOArray);
								MascgenTransforms.xyToRaDec(tempFieldCenter);
								savedFieldCenter = tempFieldCenter.clone();
								if (raCoordWrap) {
									MascgenTransforms.fixRaCoordWrap(savedFieldCenter);
								}
								result.setCenter(savedFieldCenter);
								result.setPositionAngle(tempPA);
							}
						}
					}
				}	
			}
		}

		setMascgenStatus("-----------------------------------------------", propertyChangeListener);
		setMascgenStatus(" --------------------------------------------- ", propertyChangeListener);
		setMascgenStatus("*** OPTIMIZATION COMPLETE. ***", propertyChangeListener);
		if (configurationFound) {
			setMascgenStatus("*** CONFIGURATION FOUND ***", propertyChangeListener);
			if (raCoordWrap) {
				
				for (AstroObj obj : targets) {
					MascgenTransforms.fixRaCoordWrap(obj);
				}
/*
 				for (AstroObj obj : result.getAstroObjects()) {
 
					fixRaCoordWrap(obj);
				}
				for (AstroObj obj : result.getLegalAlignmentStars()) {
					fixRaCoordWrap(obj);
				}
				*/
			}
		} else {
			setMascgenStatus("*** NO VALID CONFIGURATION FOUND. ***", propertyChangeListener);
		}
		setMascgenStatus(" --------------------------------------------- ", propertyChangeListener);
		setMascgenStatus("-----------------------------------------------", propertyChangeListener);
		// JMS : This method now returns list of mascgen result rather than single result.
		// return result;
		return topResults.asList();
	}
	//////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M6 by Ji Man Sohn, UCLA 2016-2017 //
	// * This method is the run method before the update.			//
	//////////////////////////////////////////////////////////////////
	public MascgenResult runOld(List<AstroObj> targets, MascgenArguments args, PropertyChangeListener propertyChangeListener) throws MascgenArgumentException {

		HashSet<AstroObj> allObjects = new HashSet<AstroObj>();
		HashSet<AstroObj> allStars = new HashSet<AstroObj>();
		
		abort=false;
		
		setMascgenOptimalRunNumber(0, propertyChangeListener);
		setMascgenRunNumber(0, propertyChangeListener);
		setMascgenTotalPriority(0.0, propertyChangeListener);
		setMascgenTotalRuns(0, propertyChangeListener);
		setMascgenStatus("Validating arguments", propertyChangeListener);
		
		//. verify argument values

		if (args.getxSteps() < 0) {
			throw new MascgenArgumentException("Inavlid X Steps <"+args.getxSteps()+">. Must be non-negative");
		}
		if (args.getySteps() < 0) {
			throw new MascgenArgumentException("Inavlid Y Steps <"+args.getySteps()+">. Must be non-negative");
		}
		if (args.getPaSteps() < 0) {
			throw new MascgenArgumentException("Inavlid PA Steps <"+args.getPaSteps()+">. Must be non-negative");
		}

		java.text.DecimalFormat thirdPlace = new java.text.DecimalFormat("0.000");

		if ((args.getxRange() <= 0) || (args.getxRange() > CSU_WIDTH/60)) {
			throw new MascgenArgumentException("Invalid X Range <"+args.getxRange()+">. Must be between 0 and the CSU Width (" + thirdPlace.format(CSU_WIDTH / 60) + " arcmin).");
		}
		if ((args.getxCenter() <= -CSU_WIDTH / 120) || (args.getxCenter() >= CSU_WIDTH/120)) {
			throw new MascgenArgumentException("Invalid X Center <"+args.getxCenter()+">. Must be between +/- half of CSU Width (+/- " + thirdPlace.format(CSU_WIDTH / 120) + " arcmin).");
		}
		if ((args.getSlitWidth() <= 0) || (args.getSlitWidth() > CSU_WIDTH)) {
			throw new MascgenArgumentException("Invalid slit width <"+args.getSlitWidth()+">. Must be positive and less than CSU Width ("+thirdPlace.format(CSU_WIDTH) + " arcsec).");
		}
		if ((args.getDitherSpace() < 0) || (args.getDitherSpace() > CSU_HEIGHT/2)) {
			throw new MascgenArgumentException("Invalid dither space <"+args.getDitherSpace()+">. Must be non-negative and less than half of the CSU height ("+thirdPlace.format(CSU_HEIGHT / 2) + " arcsec).");
		}
		//. validate RA/DEC and PA center
		//. validate Star edge buffer

		//. minLegalX > 0? maxLegalX < CSU_WIDTH?  -> allow it, since objects not in CSU are cropped off later
		// Calculate some CSU parameters which are more useful.
//		minLegalX = 60 * (args.getxCenter() - args.getxRange() / 2) + CSU_WIDTH / 2;
//		maxLegalX = 60 * (args.getxCenter() + args.getxRange() / 2) + CSU_WIDTH / 2;
		minLegalX = 60 * (args.getxCenter() - args.getxRange() / 2);
		maxLegalX = 60 * (args.getxCenter() + args.getxRange() / 2);
		xCenter = args.getxCenter();
		ditherSpace = args.getDitherSpace();
		alignmentStarEdgeBuffer = args.getAlignmentStarEdgeBuffer();
		

		setMascgenTotalRuns((args.getxSteps()*2+1)*(args.getySteps()*2+1)*(args.getPaSteps()*2+1), propertyChangeListener);


		// Find the high and low coordinate extremes in the Input Object List.
		// Also create the astroObject that contains the information about the alignment stars
		// Alignment stars are designated by having a negative priority

		int highObjRaHour = (int) targets.get(0).getRaHour();
		int lowObjRaHour = highObjRaHour;
		int highObjDecDeg = (int) targets.get(0).getDecDeg();
		int lowObjDecDeg = highObjDecDeg;

		for (AstroObj currentObj : targets) {
			if ((int) currentObj.getRaHour() > highObjRaHour) {
				highObjRaHour = (int) (currentObj.getRaHour());
			}
			if ((int) currentObj.getRaHour() < lowObjRaHour) {
				lowObjRaHour = (int) (currentObj.getRaHour());
			}
			if ((int) currentObj.getDecDeg() > highObjDecDeg) {
				highObjDecDeg = (int) (currentObj.getDecDeg());
			}
			if ((int) currentObj.getDecDeg() < lowObjDecDeg) {
				lowObjDecDeg = (int) (currentObj.getDecDeg());
			}
			if (currentObj.getObjPriority() < 0.0) {
				allStars.add(currentObj);
			} else {
				allObjects.add(currentObj);
			}
		}
		
		// If the Input Object List covers more than one hour in RA or more
		// than one degree in Dec, throw exception
		if (highObjRaHour - lowObjRaHour > 1) {
			if ((highObjRaHour != 23) || (lowObjRaHour != 0)) {
				//. reject this, since if there is wrap, 
				//. it won't work right.
				throw new MascgenArgumentException("The input object list spans more than one hour in RA.  Reduce object list.");
			}
		}
		if (highObjDecDeg - lowObjDecDeg > 1) {
			throw new MascgenArgumentException("The input object list spans more than one degree in Dec.  Reduce object list.");
		}

		// Determine if the RA coordinates wrap around the zero line.
		boolean raCoordWrap = false;
		if ((highObjRaHour == 23) && (lowObjRaHour == 0)) {
			for (AstroObj obj : targets) {
				if (obj.getRaHour() == 0) {
					obj.setRaHour(12);
					raCoordWrap = true;
				}
				if (obj.getRaHour() == 23) {
					obj.setRaHour(11);
					raCoordWrap = true;
				}
			}
		}


		// Instantiate a new RaDec variable to store the input field center.
		RaDec fieldCenter, printedFieldCenter;
		if (args.usesCenterOfPriority()) {
			fieldCenter = calculateCenterOfPriority(allObjects);
			args.setCenterPosition(fieldCenter);
		} else {
			fieldCenter = args.getCenterPosition();
		}
		
		//. we want to print the field center in the mascgen status panel
		//. but it needs to be corrected for ra coord wrap if it was done.
		printedFieldCenter = fieldCenter.clone();
		
		if (raCoordWrap) {
			MascgenTransforms.fixRaCoordWrap(printedFieldCenter);
		}
		setMascgenStatus("Starting Center Position: "+printedFieldCenter.toStringWithUnits(), propertyChangeListener);
		
		// Compute the wcs x and y coordinates of the field center from Ra/Dec.
		MascgenTransforms.raDecToXY(fieldCenter);
		double totalPriority = 0;
		int runNum = 0; // Keep track of the number of optimization runs.
		RaDec tempFieldCenter = new RaDec();
		RaDec savedFieldCenter;
		double tempPA;

		// Now, run the three-level for loop over position angle, field center
		// y coordinate, and field center x coordinate. Count the total number
		// of loops (runNum).
		setMascgenStatus("Finding optimal mask configuration.", propertyChangeListener);

		//. run loop so that it starts at center, and works its way out.
		boolean configurationFound = false;
		MascgenResult result = new MascgenResult();
		result.setCoordWrap(raCoordWrap);
		int xStepFactor = 0;
		int yStepFactor = 0;
		int paStepFactor = 0;
		for (int j = -args.getxSteps(); j < args.getxSteps() + 1; j++) {
			tempFieldCenter.setXCoordinate(fieldCenter.getXCoordinate() - xStepFactor * args.getxStepSize());
			xStepFactor = getNextFactor(xStepFactor);
			yStepFactor = 0;
			for (int k = -args.getySteps(); k < args.getySteps() + 1; k++){
				tempFieldCenter.setYCoordinate(fieldCenter.getYCoordinate() -	yStepFactor * args.getyStepSize());
				yStepFactor = getNextFactor(yStepFactor);
				for (AstroObj obj : allObjects) {
					MascgenTransforms.astroObjRaDecToXY(obj, tempFieldCenter);
				}
				paStepFactor = 0;
				for (int m = -args.getPaSteps(); m < args.getPaSteps() + 1; m++) {
					if (abort) {
						j=args.getxSteps();
						k=args.getySteps();
						m=args.getPaSteps();
					}
					setMascgenRunNumber(runNum+1, propertyChangeListener);

					double tempTotalPriority;
					tempPA = args.getCenterPA() + paStepFactor * args.getPaStepSize();
					paStepFactor = getNextFactor(paStepFactor);

					int legalNum=0;
					AstroObj[] tempStarAOArray = new AstroObj[0];
					if (args.getMinimumAlignmentStars() > 0) {
						tempStarAOArray = findLegalStars(allStars, 
								tempFieldCenter, tempPA); 
						// Now we use a hash set to find the number of unique legal stars
						// When you add an non-unique element to a hash set, nothing actually
						// gets added to the set
						HashSet<Integer> testHash = new HashSet<Integer>();

						for(AstroObj obj: tempStarAOArray){
							testHash.add(obj.getObjRR());
						}

						legalNum = testHash.size();
					}
					runNum++;

					if (legalNum >= args.getMinimumAlignmentStars()) {
						Node bestTopNode = optimize(allObjects, tempFieldCenter, tempPA);
						tempTotalPriority =  totalScore(bestTopNode);


						if (tempTotalPriority > totalPriority) {
							setMascgenTotalPriority(totalPriority, propertyChangeListener);
							setMascgenOptimalRunNumber(runNum, propertyChangeListener);
							setMascgenStatus("-----------------------------------------------", propertyChangeListener);
							String status = "New optimum configuration " +
							"found on run number " + runNum +
							". \nThe best total priority so far is " + totalPriority + ".";
							System.out.println(status);
							setMascgenStatus(status, propertyChangeListener);
							MascgenTransforms.xyToRaDec(tempFieldCenter);
							savedFieldCenter = tempFieldCenter.clone();
							if (raCoordWrap) {
								MascgenTransforms.fixRaCoordWrap(savedFieldCenter);
							}
							status = "Center = "+savedFieldCenter.toStringWithUnits()+", PA = "+tempPA+".";
							System.out.println(status);
							printNodePath(bestTopNode);
							setMascgenStatus(status, propertyChangeListener);
							configurationFound = true;
							result.setCenter(savedFieldCenter);
							result.setPositionAngle(tempPA);
							result.setTotalPriority(tempTotalPriority);
							result.setAstroObjects(createObjectArrayFromTopNode(bestTopNode));
							result.setLegalAlignmentStars(tempStarAOArray);
						} else if (tempTotalPriority == totalPriority) {
							setMascgenStatus("-----------------------------------------------", propertyChangeListener);
							String status = "Configuration with same priority " + totalPriority +
							" found on run " + runNum  + "." +
							"\nPrevious configuration being used.";
							System.out.println(status);
							setMascgenStatus(status, propertyChangeListener);
							MascgenTransforms.xyToRaDec(tempFieldCenter);
							savedFieldCenter = tempFieldCenter.clone();
							if (raCoordWrap) {
								MascgenTransforms.fixRaCoordWrap(savedFieldCenter);
							}
							status = "Center = "+savedFieldCenter.toStringWithUnits()+", PA = "+tempPA+".";
							System.out.println(status);
							if (result.getAstroObjects().length == 0) {
								result.setLegalAlignmentStars(tempStarAOArray);
								result.setCenter(savedFieldCenter);
								result.setPositionAngle(tempPA);
							}
							setMascgenStatus(status, propertyChangeListener);
						}
					} else {
						if (result.getAstroObjects().length == 0) {
							if (legalNum > result.getLegalAlignmentStars().length) {
								result.setLegalAlignmentStars(tempStarAOArray);
								MascgenTransforms.xyToRaDec(tempFieldCenter);
								savedFieldCenter = tempFieldCenter.clone();
								if (raCoordWrap) {
									MascgenTransforms.fixRaCoordWrap(savedFieldCenter);
								}
								result.setCenter(savedFieldCenter);
								result.setPositionAngle(tempPA);
							}
						}
					}
				}	
			}
		}

		setMascgenStatus("-----------------------------------------------", propertyChangeListener);
		setMascgenStatus(" --------------------------------------------- ", propertyChangeListener);
		setMascgenStatus("*** OPTIMIZATION COMPLETE. ***", propertyChangeListener);
		if (configurationFound) {
			setMascgenStatus("*** CONFIGURATION FOUND ***", propertyChangeListener);
			if (raCoordWrap) {
				
				for (AstroObj obj : targets) {
					MascgenTransforms.fixRaCoordWrap(obj);
				}
/*
 				for (AstroObj obj : result.getAstroObjects()) {
 
					fixRaCoordWrap(obj);
				}
				for (AstroObj obj : result.getLegalAlignmentStars()) {
					fixRaCoordWrap(obj);
				}
				*/
			}
		} else {
			setMascgenStatus("*** NO VALID CONFIGURATION FOUND. ***", propertyChangeListener);
		}
		setMascgenStatus(" --------------------------------------------- ", propertyChangeListener);
		setMascgenStatus("-----------------------------------------------", propertyChangeListener);

		return result;
	}
	public MascgenResult runOldOld(List<AstroObj> targets, MascgenArguments args, PropertyChangeListener propertyChangeListener) throws MascgenArgumentException {
		HashSet<AstroObj> allObjects = new HashSet<AstroObj>();
		HashSet<AstroObj> allStars = new HashSet<AstroObj>();

		abort=false;
		
		setMascgenOptimalRunNumber(0, propertyChangeListener);
		setMascgenRunNumber(0, propertyChangeListener);
		setMascgenTotalPriority(0.0, propertyChangeListener);
		setMascgenTotalRuns(0, propertyChangeListener);
		setMascgenStatus("Validating arguments", propertyChangeListener);
		
		//. verify argument values

		if (args.getxSteps() < 0) {
			throw new MascgenArgumentException("Inavlid X Steps <"+args.getxSteps()+">. Must be non-negative");
		}
		if (args.getySteps() < 0) {
			throw new MascgenArgumentException("Inavlid Y Steps <"+args.getySteps()+">. Must be non-negative");
		}
		if (args.getPaSteps() < 0) {
			throw new MascgenArgumentException("Inavlid PA Steps <"+args.getPaSteps()+">. Must be non-negative");
		}

		java.text.DecimalFormat thirdPlace = new java.text.DecimalFormat("0.000");

		if ((args.getxRange() <= 0) || (args.getxRange() > CSU_WIDTH/60)) {
			throw new MascgenArgumentException("Invalid X Range <"+args.getxRange()+">. Must be between 0 and the CSU Width (" + thirdPlace.format(CSU_WIDTH / 60) + " arcmin).");
		}
		if ((args.getxCenter() <= -CSU_WIDTH / 120) || (args.getxCenter() >= CSU_WIDTH/120)) {
			throw new MascgenArgumentException("Invalid X Center <"+args.getxCenter()+">. Must be between +/- half of CSU Width (+/- " + thirdPlace.format(CSU_WIDTH / 120) + " arcmin).");
		}
		if ((args.getSlitWidth() <= 0) || (args.getSlitWidth() > CSU_WIDTH)) {
			throw new MascgenArgumentException("Invalid slit width <"+args.getSlitWidth()+">. Must be positive and less than CSU Width ("+thirdPlace.format(CSU_WIDTH) + " arcsec).");
		}
		if ((args.getDitherSpace() < 0) || (args.getDitherSpace() > CSU_HEIGHT/2)) {
			throw new MascgenArgumentException("Invalid dither space <"+args.getDitherSpace()+">. Must be non-negative and less than half of the CSU height ("+thirdPlace.format(CSU_HEIGHT / 2) + " arcsec).");
		}
		//. validate RA/DEC and PA center
		//. validate Star edge buffer

		//. minLegalX > 0? maxLegalX < CSU_WIDTH?  -> allow it, since objects not in CSU are cropped off later
		// Calculate some CSU parameters which are more useful.
//		minLegalX = 60 * (args.getxCenter() - args.getxRange() / 2) + CSU_WIDTH / 2;
//		maxLegalX = 60 * (args.getxCenter() + args.getxRange() / 2) + CSU_WIDTH / 2;
		minLegalX = 60 * (args.getxCenter() - args.getxRange() / 2);
		maxLegalX = 60 * (args.getxCenter() + args.getxRange() / 2);
		xCenter = args.getxCenter();
		ditherSpace = args.getDitherSpace();
		alignmentStarEdgeBuffer = args.getAlignmentStarEdgeBuffer();
		

		setMascgenTotalRuns((args.getxSteps()*2+1)*(args.getySteps()*2+1)*(args.getPaSteps()*2+1), propertyChangeListener);


		// Find the high and low coordinate extremes in the Input Object List.
		// Also create the astroObject that contains the information about the alignment stars
		// Alignment stars are designated by having a negative priority

		int highObjRaHour = (int) targets.get(0).getRaHour();
		int lowObjRaHour = highObjRaHour;
		int highObjDecDeg = (int) targets.get(0).getDecDeg();
		int lowObjDecDeg = highObjDecDeg;

		for (AstroObj currentObj : targets) {
			if ((int) currentObj.getRaHour() > highObjRaHour) {
				highObjRaHour = (int) (currentObj.getRaHour());
			}
			if ((int) currentObj.getRaHour() < lowObjRaHour) {
				lowObjRaHour = (int) (currentObj.getRaHour());
			}
			if ((int) currentObj.getDecDeg() > highObjDecDeg) {
				highObjDecDeg = (int) (currentObj.getDecDeg());
			}
			if ((int) currentObj.getDecDeg() < lowObjDecDeg) {
				lowObjDecDeg = (int) (currentObj.getDecDeg());
			}
			if (currentObj.getObjPriority() < 0.0) {
				allStars.add(currentObj);
			} else {
				allObjects.add(currentObj);
			}
		}
		
		// If the Input Object List covers more than one hour in RA or more
		// than one degree in Dec, throw exception
		if (highObjRaHour - lowObjRaHour > 1) {
			if ((highObjRaHour != 23) || (lowObjRaHour != 0)) {
				//. reject this, since if there is wrap, 
				//. it won't work right.
				throw new MascgenArgumentException("The input object list spans more than one degree in RA.  Reduce object list.");
			}
		}
		if (highObjDecDeg - lowObjDecDeg > 1) {
			throw new MascgenArgumentException("The input object list spans more than one degree in Dec.  Reduce object list.");
		}

		// Determine if the RA coordinates wrap around the zero line.
		boolean raCoordWrap = false;
		if ((highObjRaHour == 23) && (lowObjRaHour == 0)) {
			for (AstroObj obj : targets) {
				if (obj.getRaHour() == 0) {
					obj.setRaHour(12);
					raCoordWrap = true;
				}
				if (obj.getRaHour() == 23) {
					obj.setRaHour(11);
					raCoordWrap = true;
				}
			}
		}

		// Instantiate a new RaDec variable to store the input field center.
		RaDec fieldCenter, printedFieldCenter;
		if (args.usesCenterOfPriority()) {
			fieldCenter = calculateCenterOfPriority(allObjects);
		} else {
			fieldCenter = args.getCenterPosition();
		}
		
		//. we want to print the field center in the mascgen status panel
		//. but it needs to be corrected for ra coord wrap if it was done.
		printedFieldCenter = fieldCenter.clone();
		
		if (raCoordWrap) {
			MascgenTransforms.fixRaCoordWrap(printedFieldCenter);
		}
		setMascgenStatus("Starting Center Position: "+printedFieldCenter.toStringWithUnits(), propertyChangeListener);
		
		// Compute the wcs x and y coordinates of the field center from Ra/Dec.
		MascgenTransforms.raDecToXY(fieldCenter);
		double totalPriority = 0;
		int runNum = 0; // Keep track of the number of optimization runs.
		RaDec tempFieldCenter = new RaDec();
		RaDec savedFieldCenter;
		double tempPA;

		// Now, run the three-level for loop over position angle, field center
		// y coordinate, and field center x coordinate. Count the total number
		// of loops (runNum).
		setMascgenStatus("Finding optimal mask configuration.", propertyChangeListener);

		//. run loop so that it starts at center, and works its way out.
		boolean configurationFound = false;
		MascgenResult result = new MascgenResult();
		int xStepFactor = 0;
		int yStepFactor = 0;
		int paStepFactor = 0;
		for (int j = -args.getxSteps(); j < args.getxSteps() + 1; j++) {
			tempFieldCenter.setXCoordinate(fieldCenter.getXCoordinate() - xStepFactor * args.getxStepSize());
			xStepFactor = getNextFactor(xStepFactor);
			yStepFactor = 0;
			for (int k = -args.getySteps(); k < args.getySteps() + 1; k++){
				tempFieldCenter.setYCoordinate(fieldCenter.getYCoordinate() -	yStepFactor * args.getyStepSize());
				yStepFactor = getNextFactor(yStepFactor);
				for (AstroObj obj : allObjects) {
					MascgenTransforms.astroObjRaDecToXY(obj, tempFieldCenter);
				}
				paStepFactor = 0;
				for (int m = -args.getPaSteps(); m < args.getPaSteps() + 1; m++) {
					if (abort) {
						j=args.getxSteps();
						k=args.getySteps();
						m=args.getPaSteps();
					}
					setMascgenRunNumber(runNum+1, propertyChangeListener);

					double tempTotalPriority;
					tempPA = args.getCenterPA() + paStepFactor * args.getPaStepSize();
					paStepFactor = getNextFactor(paStepFactor);
					
					Node bestTopNode = optimizeOld(allObjects, tempFieldCenter, tempPA);
					tempTotalPriority =  totalScore(bestTopNode);

					AstroObj[] tempStarAOArray = findLegalStarsOld(allStars, 
							tempFieldCenter, tempPA); 
					// Now we use a hash set to find the number of unique legal stars
					// When you add an non-unique element to a hash set, nothing actually
					// gets added to the set
					HashSet<Integer> testHash = new HashSet<Integer>();

					for(AstroObj obj: tempStarAOArray){
						testHash.add(obj.getObjRR());
					}

					int legalNum = testHash.size();

					runNum++;
					if (tempTotalPriority > totalPriority && 
							legalNum >= args.getMinimumAlignmentStars()) {
						totalPriority = tempTotalPriority;
						setMascgenTotalPriority(totalPriority, propertyChangeListener);
						setMascgenOptimalRunNumber(runNum, propertyChangeListener);
						setMascgenStatus("-----------------------------------------------", propertyChangeListener);
						String status = "New optimum configuration " +
						"found on run number " + runNum +
						". \nThe best total priority so far is " + totalPriority + ".";
						System.out.println(status);
						setMascgenStatus(status, propertyChangeListener);
						MascgenTransforms.xyToRaDec(tempFieldCenter);
						savedFieldCenter = tempFieldCenter.clone();
						if (raCoordWrap) {
							MascgenTransforms.fixRaCoordWrap(savedFieldCenter);
						}
						status = "Center = "+savedFieldCenter.toStringWithUnits()+", PA = "+tempPA+".";
						System.out.println(status);
						printNodePath(bestTopNode);
						setMascgenStatus(status, propertyChangeListener);
						configurationFound = true;
						result.setCenter(savedFieldCenter);
						result.setPositionAngle(tempPA);
						result.setTotalPriority(tempTotalPriority);
						result.setAstroObjects(createObjectArrayFromTopNode(bestTopNode));
						result.setLegalAlignmentStars(tempStarAOArray);
					} else if ((tempTotalPriority == totalPriority) && (legalNum >= args.getMinimumAlignmentStars())) {
						setMascgenStatus("-----------------------------------------------", propertyChangeListener);
						String status = "Configuration with same priority " + totalPriority +
						" found on run " + runNum  + "." +
						"\nPrevious configuration being used.";
						System.out.println(status);
						setMascgenStatus(status, propertyChangeListener);
						MascgenTransforms.xyToRaDec(tempFieldCenter);
						savedFieldCenter = tempFieldCenter.clone();
						if (raCoordWrap) {
							MascgenTransforms.fixRaCoordWrap(savedFieldCenter);
						}
						status = "Center = "+savedFieldCenter.toStringWithUnits()+", PA = "+tempPA+".";
						System.out.println(status);
						setMascgenStatus(status, propertyChangeListener);
					}
				}	
			}
		}

		setMascgenStatus("-----------------------------------------------", propertyChangeListener);
		setMascgenStatus(" --------------------------------------------- ", propertyChangeListener);
		setMascgenStatus("*** OPTIMIZATION COMPLETE. ***", propertyChangeListener);
		if (configurationFound) {
			setMascgenStatus("*** CONFIGURATION FOUND ***", propertyChangeListener);
			if (raCoordWrap) {
				for (AstroObj obj : result.getAstroObjects()) {
					MascgenTransforms.fixRaCoordWrap(obj);
				}
				for (AstroObj obj : result.getLegalAlignmentStars()) {
					MascgenTransforms.fixRaCoordWrap(obj);
				}
			}
		} else {
			setMascgenStatus("*** NO VALID CONFIGURATION FOUND. ***", propertyChangeListener);
		}
		setMascgenStatus(" --------------------------------------------- ", propertyChangeListener);
		setMascgenStatus("-----------------------------------------------", propertyChangeListener);

		return result;
	}

	private RaDec calculateCenterOfPriority(HashSet<AstroObj> objArray) {
		double centerRAsecs = raWeightedSum(objArray) /	prioritySum(objArray);
		double centerDECsecs = decWeightedSum(objArray) / prioritySum(objArray);
		int sign = ((centerDECsecs < 0) ? -1 : 1);
		centerDECsecs = Math.abs(centerDECsecs);
		int raH = (int)Math.floor(centerRAsecs / 3600);
		int raM = (int)Math.floor((centerRAsecs - 3600 * raH) / 60);
		double raS = centerRAsecs - 60 * raM - 3600 * raH;
		double decD = Math.floor(centerDECsecs / 3600);
		double decM = Math.floor((centerDECsecs - 3600 * decD) / 60);
		double decS = centerDECsecs - 60 * decM	- 3600 * decD;
		if ((decD == 0.0) && (sign == -1)) {
			decD = -0.0;
		} else {
			decD *= sign;
		}
		return new RaDec(raH, raM, raS, decD, decM, decS);
	}
	// Sum up the total priorty of objects in an astroObjArray.
	private static double prioritySum(HashSet<AstroObj> array) {
		long result = 0;
		for (AstroObj obj : array) {
			result += obj.getObjPriority();
		}
		return result;
	}
	// Sum up the total weighted RA coordinates of objects in an AstroObjArray.
	private static double raWeightedSum(HashSet<AstroObj> array) {
		double result = 0;
		for (AstroObj obj : array) {
			result += (obj.getRaHour() * 3600 +	obj.getRaMin() * 60 +
					obj.getRaSec()) * obj.getObjPriority();
		}
		return result;
	}

	// Sum up the total weighted DEC coordinates of objects in an AstroObjArray.
	private static double decWeightedSum(HashSet<AstroObj> array) {
		double result = 0.0;
		double sign = 1.0;
		for (AstroObj obj : array) {
			if (obj.getDecDeg() == 0.0) {
				if (Double.toString(obj.getDecDeg()).equals("-0.0")) {
					sign = -1.0;
				}
			} else {
				sign = (obj.getDecDeg() > 0.0) ? 1.0 : -1.0;
			}
			result += sign * (Math.abs(obj.getDecDeg()) * 3600 + obj.getDecMin() * 60 +
					obj.getDecSec()) * obj.getObjPriority();
		}
		return result;
	}


	private int getNextFactor(int lastFactor) {
		//. pattern is 0, 1, -1, 2, -2, 3, -3...
		if (lastFactor > 0) {
			return lastFactor * -1;
		} else {
			return lastFactor * -1 + 1;
		}
	}

	private AstroObj[] createObjectArrayFromTopNode(Node topNode) {
		ArrayList<AstroObj> array = new ArrayList<AstroObj>();
		Node nextNode = topNode;
		while (nextNode != null) {
			if (nextNode.getObj().isNotBlank()) {
				AstroObj obj = nextNode.getObj();
				obj.setInValidSlit(true);
				array.add(obj);
			}
			nextNode = nextNode.getNextNode();
		}
		return array.toArray(new AstroObj[array.size()]);
	}
	public void setMascgenStatus(String mascgenStatus, PropertyChangeListener l) {
		String oldValue = this.mascgenStatus;
		this.mascgenStatus = mascgenStatus;
		l.propertyChange(new PropertyChangeEvent(this, "mascgenStatus", oldValue, mascgenStatus));
	}
	public void setMascgenRunNumber(int mascgenRunNumber, PropertyChangeListener l) {
		int oldValue = this.mascgenRunNumber;
		this.mascgenRunNumber = mascgenRunNumber;
		l.propertyChange(new PropertyChangeEvent(this, "mascgenRunNumber", oldValue, mascgenRunNumber));
	}

	public void setMascgenTotalRuns(int mascgenTotalRuns, PropertyChangeListener l) {
		int oldValue = this.mascgenTotalRuns;
		this.mascgenTotalRuns = mascgenTotalRuns;
		l.propertyChange(new PropertyChangeEvent(this, "mascgenTotalRuns", oldValue, mascgenTotalRuns));
	}

	public void setMascgenTotalPriority(double mascgenTotalPriority, PropertyChangeListener l) {
		double oldValue = this.mascgenTotalPriority;
		this.mascgenTotalPriority = mascgenTotalPriority;
		l.propertyChange(new PropertyChangeEvent(this, "mascgenTotalPriority", oldValue, mascgenTotalPriority));
	}

	public void setMascgenOptimalRunNumber(int mascgenOptimalRunNumber, PropertyChangeListener l) {
		int oldValue = this.mascgenOptimalRunNumber;
		this.mascgenOptimalRunNumber = mascgenOptimalRunNumber;
		l.propertyChange(new PropertyChangeEvent(this, "mascgenOptimalRunNumber", oldValue, mascgenOptimalRunNumber));
	}
	public void abort() {
		abort=true;
	}
	private void printNodePath(Node node) {
		Node nextNode = node;
		while(nextNode != null) {
			logger.debug(nextNode.getObj().getMaxRow()+": "+nextNode);
			nextNode = nextNode.getNextNode();
		}

	}

	private class Node {
		private Node nextNode;
		private AstroObj obj;
		private double score;
		public Node() {
			this(new AstroObj());
		}
		public Node(AstroObj obj) {
			this.obj=obj;
			score=0;
		}
		public void setNextNode(Node nextNode) {
			this.nextNode = nextNode;
		}
		public Node getNextNode() {
			return nextNode;
		}
		public void setObj(AstroObj obj) {
			this.obj = obj;
		}
		public AstroObj getObj() {
			return obj;
		}
		public void setScore(double score) {
			this.score = score;
		}
		public double getScore() {
			return score;
		}
		public String toString() {
			if (nextNode == null) {
				return obj.toString();
			} else {
				return obj.toString() + " -> " + nextNode.getObj().toString(); 
			}
		}
	}
	//////////////////////////////////////////////////////////////////
	// Part of MAGMA UPGRADE item M6 by Ji Man Sohn, UCLA 2016-2017 //
	public void setNumTopConfigs(int num){
		numTopConfigs = num;
		System.out.println("Got it! : " + numTopConfigs);
	}
	//////////////////////////////////////////////////////////////////

}
