package edu.ucla.astro.irlab.mosfire.util;

import edu.ucla.astro.irlab.util.gui.ArrayListTableModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TargetListTableModel extends ArrayListTableModel {

	String[] columnNameArray = new String[7];
	public static String columnName0 = "#";
  public static String columnName1  = "Target Name";
  public static String columnName2  = "Priority";
  public static String columnName3 =  "Magnitude";
  public static String columnName4 = "RA";
  public static String columnName5  = "Dec";
  public static String columnName6  = "Center Distance";
  private DecimalFormat formatter = new DecimalFormat("0.00");
  public TargetListTableModel() {
    columnNameArray[0]   = columnName0;
    columnNameArray[1]   = columnName1;
    columnNameArray[2]   = columnName2;
    columnNameArray[3]   = columnName3;
    columnNameArray[4]   = columnName4;
    columnNameArray[5]   = columnName5;
    columnNameArray[6]   = columnName6;
    
    setColumnNames(columnNameArray);
  }
  
	public Object getValueAt(int rowIndex, int columnIndex) {
		if  (rowIndex >= getData().size()) {
			return null;
		}
		AstroObj target = (AstroObj)(getData().get(rowIndex));
		switch (columnIndex) {
		case 0: return rowIndex+1;
		case 1: return target.getObjName();
		case 2: return target.getObjPriority();
		case 3: return target.getObjMag();
		case 4: return target.raToString();
		case 5: return target.decToString();
		case 6: return formatter.format(target.getCenterDistance());
		}
		return null;
	}

	public int getIndexOfTarget(AstroObj obj) {
		ArrayList<?> data = (ArrayList<?>)(getData());
		//. try to grab it directly 
		int index=data.indexOf(obj);
		//. if not, look for an object with the same name
		if (index == -1) {
			index=0;
			for (Object current : data) {
				if (current instanceof AstroObj) {
					if (((AstroObj)current).getObjName().equals(obj.getObjName())) {
						return index;
					}
					index++;
				}
			}
			return -1;
		} else {
			return index;
		}
	}
}
