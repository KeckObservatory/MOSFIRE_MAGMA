package edu.ucla.astro.irlab.util.gui;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

/**
 * <p>Title: IRLab Java Utilities</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCLA Infrared Laboratory</p>
 * @author Jason L. Weiss
 * @version 1.0
 */

public class CellEditorsAndRenderers {
	private CellEditorsAndRenderers() {
	}
	
  //. CenteredTextTableCellRenderer Inner Class
  //. center aligns string value
  public static class CenteredTextTableCellRenderer implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
    	String text;
    	if (value == null) {
    		text="";
    	} else {
    		text = value.toString();
    	}
      JLabel returnedLabel = new JLabel(text, SwingConstants.CENTER);
      returnedLabel.setFont(UIManager.getFont("TableCell.font"));
      returnedLabel.setOpaque(true);
      if (table.isRowSelected(row)) {
        returnedLabel.setBackground(table.getSelectionBackground());
        returnedLabel.setForeground(table.getSelectionForeground());
      } else {
        returnedLabel.setBackground(table.getBackground());
        returnedLabel.setForeground(table.getForeground());
      }
      return returnedLabel;
    }
  }
  //. DoubleValueTableCellRenderer Inner Class
  public static class DoubleValueTableCellRenderer implements TableCellRenderer, java.io.Serializable {
    private int precision;
    public DoubleValueTableCellRenderer(int decimalPrecision) {
      precision=decimalPrecision;
    }
    public DoubleValueTableCellRenderer() {
      this(-1);
    }
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
      JLabel returnedLabel = new JLabel("", SwingConstants.CENTER);
      returnedLabel.setOpaque(true);
      if (table.isRowSelected(row)) {
        returnedLabel.setBackground(table.getSelectionBackground());
        returnedLabel.setForeground(table.getSelectionForeground());
      } else {
        returnedLabel.setBackground(table.getBackground());
        returnedLabel.setForeground(table.getForeground());
      }
      try {
      	if (value == null) {
      		return returnedLabel;
      	}
        Double doubleValue = (Double)value;
        if (precision < 1) {
          returnedLabel.setText(doubleValue.toString());
        } else {
          java.text.NumberFormat format = java.text.NumberFormat.getInstance();
          format.setMinimumFractionDigits(precision);
          format.setMaximumFractionDigits(precision);
          returnedLabel.setText(format.format(doubleValue));
        }
      } catch (Exception e) {
        returnedLabel.setText(value.toString());
      }
      return returnedLabel;
    }
  }
  public static class AutoSelectTableCellEditor extends JTextField implements FocusListener {
    public AutoSelectTableCellEditor() {
      setBorder(new LineBorder(Color.BLACK));
      addFocusListener(this);
    }
    public void setText(String text) {
      super.setText(text);
    }
    public void focusGained(FocusEvent e) {
      selectAll();
    }
    public void focusLost(FocusEvent e) {
    }
  }
  //. AlignedTextTableHeaderCellRenderer Inner Class
  //. center aligns string value
  public static class AlignedTextTableHeaderCellRenderer implements TableCellRenderer {
  	private int align;  //. use Alignment Constants for JLabels
  	public AlignedTextTableHeaderCellRenderer(int alignment) {
  		align=alignment;
  	}
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
    	String text;
    	if (value == null) {
    		text="";
    	} else {
    		text = value.toString();
    	}
      JLabel returnedLabel = new JLabel(text, align);
      returnedLabel.setOpaque(true);
      returnedLabel.setFont(UIManager.getFont("TableHeader.font"));
      returnedLabel.setBackground(UIManager.getColor("TableHeader.background"));
      returnedLabel.setForeground(UIManager.getColor("TableHeader.foreground"));
      returnedLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
      
      return returnedLabel;
    }
  }

}