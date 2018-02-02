package edu.ucla.astro.irlab.util.gui;

import javax.swing.table.*;
import java.util.ArrayList;

/**
 * <p>Title: OSIRIS Observation Planning GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: UCLA IR Lab</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public abstract class ArrayListTableModel extends AbstractTableModel {
  private String[] columnNames;
  private ArrayList<?> data;

  public ArrayListTableModel() {
    data=new ArrayList<Object>();
  }
  public String getColumnName(int column) {
    return columnNames[column];
  }
  public int getColumnCount() {
    return columnNames.length;
  }
  public int getRowCount() {
  	if (data != null)
  		return data.size();
  	else
  		return 0;
  }
  public Class<?> getColumnClass(int c) {
    Object obj = getValueAt(0, c);
    if (obj == null) {
    	return Object.class;
    } else {
    	return  obj.getClass();
    }
   }
  public ArrayList<?> getData() {
    return data;
  }
  public void setData(ArrayList<?> newData) {
    data=newData;
    fireTableDataChanged();
  }
  public void setColumnNames(String[] names) {
   this.columnNames = names;
  }
  //. this method is a javax.swing.table.AbstractTableModel abstract method
  //. again, it is abstract here.  must be implemented by child class.
  public abstract Object getValueAt(int rowIndex, int columnIndex);

}