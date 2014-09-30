/* ------------------------------------------------------------------
 * StringColumn.java
 * 
 * Created 2008-10-30 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.data;

import java.util.ArrayList;
import java.util.Collection;

public class StringColumn extends BasicColumn {

	private ArrayList<String> rows = new ArrayList<String>();

	public StringColumn(String name) {
		super(name, Column.Type.String);
	}

	public StringColumn(String name, boolean isMeta) {
		super(name, Column.Type.String);
		setMeta(isMeta);
	}

	public Object getValueAt(int row) {
		return rows.get(row);
	}

	public int getIntValueAt(int row) {
		try { 
			return Integer.parseInt(rows.get(row));
		}
		catch (NumberFormatException e) { 
			return 0;
		}
	}

	public double getRealValueAt(int row) {
		try { 
			return Double.parseDouble(rows.get(row));
		}
		catch (NumberFormatException e) { 
//			return 0;
			return rows.get(row).charAt(0);
		}
	}

	public String getStringValueAt(int row) {
		return rows.get(row);
	}
	
	public void addValue(Object value) {
		if (value instanceof String) {
			rows.add((String) value);
		}
		else { 
			rows.add(new String(""));
		}
	}

	public int getRowCount() {
		return rows.size();
	}

	public void reorder(Collection<Integer> rowOrder) {
		ArrayList<String> newRows = new ArrayList<String>();
		reorder(rowOrder, rows, newRows);
		rows = newRows;
	}
	
	public void ensureCapacity(int minRows) {
		while (rows.size() < minRows) { 
			rows.add(null);
		}
	}

	public void setValueAt(int row, Object value) {
		ensureCapacity(row + 1);
		if (value instanceof String) {
			rows.set(row, (String) value);
		}
		else { 
			rows.set(row, new String(""));
		}
	}

	public void clear() {
		rows.clear();
	}
}
