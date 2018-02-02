package edu.ucla.astro.irlab.mosfire.util;

import edu.ucla.astro.irlab.util.gui.ArrayListTableModel;

public class MechanicalSlitListTableModel extends ArrayListTableModel {

	boolean readOnly;
	public MechanicalSlitListTableModel() {
		this(false);
	}
	public MechanicalSlitListTableModel(boolean readOnly) {
		super();
		this.readOnly =readOnly; 
		setColumnNames(new String[] {"Row", "Center", "Width"});
	}
	public boolean isCellEditable(int row, int col) {
		return ((col == 2) && !readOnly);
	}
	public void setValueAt(Object aValue, int row, int col) {
		try {
			SlitPosition pos = (SlitPosition)(super.getData().get(row));
			double dValue = Double.parseDouble(aValue.toString());
			if (col == 1) {
				pos.setCenterPosition(dValue);
			} else if (col == 2) {
				pos.setSlitWidth(dValue);
			} else
				return;
			fireTableCellUpdated(row, col);
		} catch (ArrayIndexOutOfBoundsException aioobEx) {
		   //. ignore
			aioobEx.printStackTrace();
		}
		
	}
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return ((SlitPosition)super.getData().get(row)).getSlitNumber();
		} else if (col == 1) {
			return ((SlitPosition)super.getData().get(row)).getCenterPosition();
		} else if (col == 2) {
			return ((SlitPosition)super.getData().get(row)).getSlitWidth();			
		} else
			return null;
	}
	public int getSlitRow(int tableRow) {
		return ((SlitPosition)super.getData().get(tableRow)).getSlitNumber();
	}
	public int getTableRow(int slitRow) {
		int row=0;
		for (Object obj : getData()) {
			if (((SlitPosition)obj).getSlitNumber() == slitRow) {
				return row;
			}
			row++;
		}
		return -1;
	}
}
