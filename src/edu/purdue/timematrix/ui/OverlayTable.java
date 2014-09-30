/* ------------------------------------------------------------------
 * OverlayTable.java
 * 
 * Created 2009-02-26 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.ui;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import edu.purdue.timematrix.overlay.Overlay;

public class OverlayTable extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private static String columnNames[] = { "V", "C", "Title", "Alpha" };
	private ArrayList<Overlay> overlays = new ArrayList<Overlay>();
	
	public OverlayTable() {
		// empty
	}
	
	public void addOverlay(Overlay overlay) { 
		overlays.add(overlay);
	}

	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0: return Boolean.class;
			case 1: return Color.class;
			case 2: return String.class;
			case 3: return Double.class;
		}
		return Object.class;
	}

	public int getColumnCount() {
		return 4;
	}

	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public int getRowCount() {
		return overlays.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Overlay overlay = overlays.get(rowIndex);
		switch (columnIndex) {
			case 0: return overlay.isVisible();
			case 1: return overlay.getColor();
			case 2: return overlay.getName();
			case 3: return overlay.getAlpha();
			default: break;
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		Overlay overlay = overlays.get(rowIndex);
		switch (columnIndex) {
			case 0: overlay.setVisible((Boolean) value); break;
			case 1: overlay.setColor((Color) value); break;
			case 2: overlay.setName((String) value); break;
			case 3: overlay.setAlpha((Double) value); break;
			default: return;
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}
}
