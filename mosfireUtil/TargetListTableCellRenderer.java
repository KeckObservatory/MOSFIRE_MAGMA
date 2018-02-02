package edu.ucla.astro.irlab.mosfire.util;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

public class TargetListTableCellRenderer implements TableCellRenderer {
	private final static Color starColor = new Color(0xd2691e);
	@Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
    	String text;
    	if (value == null) {
    		text="";
    	} else {
    		text = value.toString();
    	}
    	AstroObj obj = (AstroObj)((TargetListTableModel)table.getModel()).getData().get(table.convertRowIndexToModel(row));
    	Color fg = table.getForeground();
    	Color selectedFg = table.getSelectionForeground();
    	if (obj != null) {
    		if (obj.getObjPriority() < 0) {
    			fg = starColor;
    			selectedFg = starColor;
    		} else {
    			if (!obj.isInValidSlit()) {
    				fg = Color.red;
    				selectedFg = Color.red;
    			}
    		}
    	}
      JLabel returnedLabel = new JLabel(text, SwingConstants.CENTER);
      returnedLabel.setFont(UIManager.getFont("TableCell.font"));
      returnedLabel.setOpaque(true);
      if (table.isRowSelected(row)) {
        returnedLabel.setBackground(table.getSelectionBackground());
        returnedLabel.setForeground(selectedFg);
      } else {
        returnedLabel.setBackground(table.getBackground());
        returnedLabel.setForeground(fg);
      }
      return returnedLabel;
    }

}
