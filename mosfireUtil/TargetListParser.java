package edu.ucla.astro.irlab.mosfire.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import edu.ucla.astro.irlab.util.NumberFormatters;

public class TargetListParser {
	public static String   SEPARATOR   = "  ";
	public static String   TERMINATOR  = "\n";
	private static DecimalFormat threeDigitFormatter = NumberFormatters.StandardFloatFormatter(2);
	private static DecimalFormat twoDigitFormatter = NumberFormatters.StandardFloatFormatter(2);
	private static DecimalFormat oneDigitFormatter = NumberFormatters.StandardFloatFormatter(1);
	private static DecimalFormat zeroDigitFormatter = NumberFormatters.StandardFloatFormatter(0);

	private static AstroObj parseLine(String newLine, int lineNumber) throws NumberFormatException, TargetListFormatException {
		Scanner scanner = new Scanner(newLine);
		AstroObj currentTargetDefinitionObject = new AstroObj();

		currentTargetDefinitionObject.setObjName(scanner.next());
		
		if (scanner.hasNext()) {
			if (scanner.hasNextDouble()) {
				currentTargetDefinitionObject.setObjPriority(scanner.nextDouble());
			} else {
				throw new TargetListFormatException("Error parsing line "+lineNumber+": Second column must be a number for priority.");
			}
		} else {
			throw new TargetListFormatException("Error parsing line "+lineNumber+": Target List entry must contain an object name, priority, magnitude, RA, Dec, epoch, and equinox.");
		}
		
		if (scanner.hasNext()) {
			if (scanner.hasNextDouble()) {
				currentTargetDefinitionObject.setObjMag(scanner.nextDouble());
			} else {
				String value = scanner.next();
				if (value.equalsIgnoreCase("nan")) {
					currentTargetDefinitionObject.setObjMag(Double.NaN);
				} else {
					System.err.println("Error parsing line "+lineNumber+": Third column must be a number for magnitude.");
					currentTargetDefinitionObject.setObjMag(99.9);
				}
			}
		} else {
			throw new TargetListFormatException("Error parsing line "+lineNumber+": Target List entry must contain an object name, priority, magnitude, RA, Dec, epoch, and equinox.");
		}

		double raH, raM, raS, decD, decM, decS;

		if (scanner.hasNext()) {
			if (scanner.hasNextInt()) {
				//. RA expected to be delimited by spaces
				raH = scanner.nextInt();
				if (scanner.hasNext()) {
					if (scanner.hasNextInt()) {
						raM = scanner.nextInt();
						if (scanner.hasNextDouble()) {
							raS = scanner.nextDouble();
						} else {
							throw new TargetListFormatException("Error parsing line "+lineNumber+": RA specification must be in one of the following formats: \"raH raM raS\", \"raH:raM:raS\", \"raH:raM\", \"ra\"");							
						}
					} else if (scanner.hasNextDouble()) {
							double raMDouble = scanner.nextDouble();
							raM = Math.floor(raMDouble);
							raS = (raMDouble - raM) * 60.0;	
					} else {
						throw new TargetListFormatException("Error parsing line "+lineNumber+": RA specification must be in one of the following formats: \"raH raM raS\", \"raH:raM:raS\", \"raH:raM\", \"ra\"");
					}
				} else {
					throw new TargetListFormatException("Error parsing line "+lineNumber+": Target List entry must contain an object name, priority, magnitude, RA, Dec, epoch, and equinox.");
				}
//				currentTargetDefinitionObject.setObjMag(scanner.nextDouble());
			} else if (scanner.hasNextDouble()) {
				//. RA expected to be single value in decimal form
				double raDouble = scanner.nextDouble();
				raH = Math.floor(raDouble);
				raM = Math.floor((raDouble - raH) * 60.0);
				raS = (((raDouble - raH) * 60.0) - raM) * 60.0;
			} else {
				//. RA expected to be delimited using ":"
				StringTokenizer coordinateTokenizer = new StringTokenizer(scanner.next(), ":");
				if (coordinateTokenizer.countTokens() == 1) {
					double raDouble = Double.parseDouble(coordinateTokenizer.nextToken());
					raH = Math.floor(raDouble);
					raM = Math.floor((raDouble - raH) * 60.0);
					raS = (((raDouble - raH) * 60.0) - raM) * 60.0;
				} else if (coordinateTokenizer.countTokens() == 2) {
					raH = Double.parseDouble(coordinateTokenizer.nextToken());
					double raMDouble = Double.parseDouble(coordinateTokenizer.nextToken());
					raM = Math.floor(raMDouble);
					raS = (raMDouble - raM) * 60.0;	
				} else if (coordinateTokenizer.countTokens() == 3) {
					raH = Double.parseDouble(coordinateTokenizer.nextToken());
					raM = Double.parseDouble(coordinateTokenizer.nextToken());
					raS = Double.parseDouble(coordinateTokenizer.nextToken());
				} else {
					throw new TargetListFormatException("Error parsing line "+lineNumber+": RA specification must be in one of the following formats: \"raH raM raS\", \"raH:raM:raS\", \"raH:raM\", \"ra\"");
				}
						
			}
		} else {
			throw new TargetListFormatException("Error parsing line "+lineNumber+": Target List entry must contain an object name, priority, magnitude, RA, Dec, epoch, and equinox.");
		}

		if (scanner.hasNext()) {
			if (scanner.hasNextInt()) {
				String decDString = scanner.next();
				if (decDString.startsWith("+")) {
					decDString = decDString.substring(1);
				}
				decD = Double.parseDouble(decDString);
				//. DEC expected to be delimited by spaces
				if (scanner.hasNext()) {
					if (scanner.hasNextInt()) {
						decM = (double)scanner.nextInt();
						if (scanner.hasNextDouble()) {
							decS = scanner.nextDouble();
						} else {
							throw new TargetListFormatException("Error parsing line "+lineNumber+": Dec specification must be in one of the following formats: \"decD decM decS\", \"decD:decM:decS\", \"decD:decM\", \"dec\"");							
						}
					} else if (scanner.hasNextDouble()) {
							double decMDouble = scanner.nextDouble();
							decM = Math.floor(decMDouble);
							decS = (decMDouble - decM) * 60.0;	
					} else {
						throw new TargetListFormatException("Error parsing line "+lineNumber+": Dec specification must be in one of the following formats: \"decD decM decS\", \"decD:decM:decS\", \"decD:decM\", \"dec\"");
					}
				} else {
					throw new TargetListFormatException("Error parsing line "+lineNumber+": Target List entry must contain an object name, priority, magnitude, RA, Dec, epoch, and equinox.");
				}
//				currentTargetDefinitionObject.setObjMag(scanner.nextDouble());
			} else if (scanner.hasNextDouble()) {
				//. if this token starts with a +, it may or may not
				//. be decimal degrees.
				//. check for this case
				String nextValue = scanner.next();
				if (nextValue.startsWith("+") && !nextValue.contains(".")) {
					//. its really an integer with a plus in front
					//. parse like done above for integer degrees case
					//. (expecting decimal minutes or integer minutes and decimal seconds)
					decD = Double.parseDouble(nextValue);
					//. DEC expected to be delimited by spaces
					if (scanner.hasNext()) {
						if (scanner.hasNextInt()) {
							decM = (double)scanner.nextInt();
							if (scanner.hasNextDouble()) {
								decS = scanner.nextDouble();
							} else {
								throw new TargetListFormatException("Error parsing line "+lineNumber+": Dec specification must be in one of the following formats: \"decD decM decS\", \"decD:decM:decS\", \"decD:decM\", \"dec\"");							
							}
						} else if (scanner.hasNextDouble()) {
								double decMDouble = scanner.nextDouble();
								decM = Math.floor(decMDouble);
								decS = (decMDouble - decM) * 60.0;	
						} else {
							throw new TargetListFormatException("Error parsing line "+lineNumber+": Dec specification must be in one of the following formats: \"decD decM decS\", \"decD:decM:decS\", \"decD:decM\", \"dec\"");
						}
					} else {
						throw new TargetListFormatException("Error parsing line "+lineNumber+": Target List entry must contain an object name, priority, magnitude, RA, Dec, epoch, and equinox.");
					}
				} else {
				
					//. RA expected to be single value in decimal form
					double decDouble = Double.parseDouble(nextValue);
					decD = (double)((int)(decDouble));
					if ((decD == 0) && (decDouble < 0)) {
						decD = -0.0;
					}
					decM = Math.floor(Math.abs(decDouble - decD) * 60.0);
					decS = ((Math.abs(decDouble - decD) * 60.0) - decM) * 60.0;
				}
			} else {
				String nextString = scanner.next();
				//. RA expected to be delimited using ":"
				StringTokenizer coordinateTokenizer = new StringTokenizer(nextString, ":");
				if (coordinateTokenizer.countTokens() == 1) {
					throw new TargetListFormatException("Error parsing line "+lineNumber+": Dec specification must be in one of the following formats: \"decD decM decS\", \"decD:decM:decS\", \"decD:decM\", \"dec\"");
				} else if (coordinateTokenizer.countTokens() == 2) {
					decD = Double.parseDouble(coordinateTokenizer.nextToken());
					double decMDouble = Double.parseDouble(coordinateTokenizer.nextToken());
					decM = Math.floor(decMDouble);
					decS = (decMDouble - decM) * 60.0;	
				} else if (coordinateTokenizer.countTokens() == 3) {
					decD = Double.parseDouble(coordinateTokenizer.nextToken());
					decM = Double.parseDouble(coordinateTokenizer.nextToken());
					decS = Double.parseDouble(coordinateTokenizer.nextToken());
				} else {
					throw new TargetListFormatException("Error parsing line "+lineNumber+": Dec specification must be in one of the following formats: \"decD decM decS\", \"decD:decM:decS\", \"decD:decM\", \"dec\"");
				}
						
			}
		} else {
			throw new TargetListFormatException("Error parsing line "+lineNumber+": Target List entry must contain an object name, priority, magnitude, RA, Dec, epoch, and equinox.");
		}
		currentTargetDefinitionObject.setRaHour(raH);
		currentTargetDefinitionObject.setRaMin(raM);
		currentTargetDefinitionObject.setRaSec(raS);
		currentTargetDefinitionObject.setDecDeg(decD);
		currentTargetDefinitionObject.setDecMin(decM);
		currentTargetDefinitionObject.setDecSec(decS);
		
		if (scanner.hasNext()) {
			if (scanner.hasNextDouble()) {
				currentTargetDefinitionObject.setEpoch(scanner.nextDouble());
			} else {
				throw new TargetListFormatException("Error parsing line "+lineNumber+": Epoch must be a number.");
			}
		} else {
			throw new TargetListFormatException("Error parsing line "+lineNumber+": Target List entry must contain an object name, priority, magnitude, RA, Dec, epoch, and equinox.");
		}


		if (scanner.hasNext()) {
			if (scanner.hasNextDouble()) {
				currentTargetDefinitionObject.setEquinox(scanner.nextDouble());
			} else {
				throw new TargetListFormatException("Error parsing line "+lineNumber+": Equinox must be a number.");
			}
		} else {
			throw new TargetListFormatException("Error parsing line "+lineNumber+": Target List entry must contain an object name, priority, magnitude, RA, Dec, epoch, and equinox.");
		}

		return currentTargetDefinitionObject;
	}
	
	/*================================================================================================
	 /      parseFile()
	  /=================================================================================================*/
	public static ArrayList<AstroObj> parseFile(String targetListFilename) throws FileNotFoundException, IOException, NumberFormatException, TargetListFormatException {
		return parseFile(new File(targetListFilename));
	}
	public static ArrayList<AstroObj> parseFile(File targetListFile) throws IOException, NumberFormatException, TargetListFormatException {
		ArrayList<AstroObj> targetList = new ArrayList<AstroObj>();
		
		RandomAccessFile currentRandomAccessFile = new RandomAccessFile(targetListFile,"r");

		String currentLine = currentRandomAccessFile.readLine();
		for (int ii=1; currentLine != null; ii++) {
			if (!currentLine.startsWith("#") && (!currentLine.trim().isEmpty())) {
				targetList.add(parseLine(currentLine, ii));
			}
			currentLine = currentRandomAccessFile.readLine();
		} 

		return targetList;
	}
	
	/*================================================================================================
	 /       SaveFile()
	  /=================================================================================================*/
	public static void saveFile(String targetListFilename, ArrayList<AstroObj> targetList) {
		try{
			File             currentFile         = new File(targetListFilename);
			FileWriter       myFileWriter        = new FileWriter(currentFile);
			
			for(AstroObj currentTarget : targetList) {
				myFileWriter.write(constructTargetDefinitionRecord(currentTarget));
			}
			myFileWriter.flush();
			myFileWriter.close();
		} catch(Exception e) {
			System.out.println("An error occured while trying to save the Target Definition File. " + e.toString());
		}
		
	}
	/*================================================================================================
	 /       constructTargetDefinitionObjectRecord(TargetDefinitionObject  newTargetDefinitionObject)
	  /=================================================================================================*/
	private static String constructTargetDefinitionRecord(AstroObj  target){
		/*  The target list will have the same format as the DEIMOS target list, used by the slitassign and autoslit software. It will simply be:
		 Column 	Field 	Datatype 	Description
		 1 	ID 	String 	Name of target
		 2 	Priority 	Integer 	Ranking of importance of target. Can be any integer, but usually from 0-1000. Potential acquisition stars are given a -1 priority
		 3 	Magnitude 	Float 	        Brightness of target
		 4 	RA H    	Integer 	Target Right Ascension hours
		 5 	RA M 	        Integer 	Target Right Ascension minutes
		 6 	RA S 	        Float 	        Target Right Ascension seconds
		 7 	DEC D 	        Integer 	Target Declination degress
		 8 	DEC M 	        Integer 	Target Declination minutes
		 9 	DEC S    	Float 	        Target Declination seconds
		 10 	Epoch   	Float       	Epoch for RA and Dec coordinates
		 11 	Equinox         Float 	        ???
		 12 	Proper Motion in RA 	Float 	RA Proper motion of the target in arcsec/year
		 13 	Proper Motion in Dec 	Float 	Dec Proper motion of the target in arcsec/year
		 
		 EXAMPLE target definition statement
		 //     BX370f 500 25.36 17 00 44.616 64 08 14.69 2000.0 2000.0 0.0 0.0
		  * 
		  *  note: proper motion is ignored and set to 0
		  */
		return target.getObjName() + SEPARATOR + 
			twoDigitFormatter.format(target.getObjPriority()) + SEPARATOR + 
			twoDigitFormatter.format(target.getObjMag()) + SEPARATOR + 
			zeroDigitFormatter.format(target.getRaHour()) + SEPARATOR + 
			zeroDigitFormatter.format(target.getRaMin()) + SEPARATOR + 
			threeDigitFormatter.format(target.getRaSec()) +	SEPARATOR + 
			zeroDigitFormatter.format(target.getDecDeg()) + SEPARATOR + 
			zeroDigitFormatter.format(target.getDecMin()) + SEPARATOR + 
			twoDigitFormatter.format(target.getDecSec()) + SEPARATOR +
			oneDigitFormatter.format(target.getEpoch()) + SEPARATOR + 
			oneDigitFormatter.format(target.getEquinox()) + SEPARATOR + 
			oneDigitFormatter.format(0.0) + SEPARATOR + 
			oneDigitFormatter.format(0.0) + TERMINATOR;
	}
	public static void main(String args[]) {
		ArrayList<AstroObj> objs;
		try {
			objs = TargetListParser.parseFile("/home/mosdev/kroot/kss/mosfire/gui/mscgui/mascgen_test_data/newbase/q1700_pa_0/q1700_test_coords.txt");
//			TargetListParser.saveFile("/home/mosdev/kroot/kss/mosfire/gui/mscgui/mascgen_test_data/working/orionBD/Orion_out.txt", objs);
		} catch (NumberFormatException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (TargetListFormatException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
	}
}
