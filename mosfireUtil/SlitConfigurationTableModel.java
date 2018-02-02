package edu.ucla.astro.irlab.mosfire.util;

import edu.ucla.astro.irlab.util.gui.ArrayListTableModel;
import java.util.ArrayList;

public class SlitConfigurationTableModel extends ArrayListTableModel {

	private String newMaskName="";
	public SlitConfigurationTableModel() {
		super();
		setColumnNames(new String[] {"Status", "Name"});
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return ((SlitConfiguration)super.getData().get(rowIndex)).getStatus();
		} else if (columnIndex == 1) {
			return ((SlitConfiguration)super.getData().get(rowIndex)).getMaskName();			
		} else
			return null;
	}
	public boolean isCellEditable(int row, int col) {
		return (col == 1);
	}
	public void setValueAt(Object aValue, int row, int col) {
		try {
			SlitConfiguration config = (SlitConfiguration)(super.getData().get(row));
			if (col == 1) {
				newMaskName = aValue.toString();
				int index = isNameTaken(newMaskName);
				//. if simply resetting the cell to the original value, just return
				if (index == row) return;
				if (!newMaskName.isEmpty() && (index < 0)) {
					config.setMaskName(newMaskName);
				} else {
					col=-2;
				}
			} else
				return;
			fireTableCellUpdated(row, col);
		} catch (ArrayIndexOutOfBoundsException aioobEx) {
		   //. ignore
			aioobEx.printStackTrace();
		}
	}
	public String getNewMaskName() {
		return newMaskName;
	}
	private int isNameTaken(String name) {
		int index=0;
		ArrayList<SlitConfiguration> configs = (ArrayList<SlitConfiguration>)super.getData();
		for (SlitConfiguration config : configs) {
			if (config.getMaskName().equals(name)) {
				return index;
			}
			index++;
		}
		return -1;
	}
}
