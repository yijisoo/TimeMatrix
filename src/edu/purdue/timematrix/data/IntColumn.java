/* ------------------------------------------------------------------
 * IntColumn.java
 * 
 * Created 2008-10-30 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.data;

import java.util.ArrayList;
import java.util.Collection;

public class IntColumn extends BasicColumn {
	
	private int min = Integer.MAX_VALUE;
	private int max = Integer.MIN_VALUE;
	private ArrayList<Integer> rows = new ArrayList<Integer>();

	public IntColumn(String name) {
		super(name, Column.Type.Integer);
	}

	public IntColumn(String name, boolean isMeta) {
		super(name, Column.Type.Integer);
		setMeta(isMeta);
	}

	public Object getValueAt(int row) {
		return rows.get(row);
	}

	public int getIntValueAt(int row) {
		return rows.get(row);
	}

	public double getRealValueAt(int row) {
		return rows.get(row);
	}

	public String getStringValueAt(int row) {
		return "" + rows.get(row);
	}
	
	private int parseValue(Object value) {
		if (value instanceof Integer) {
			return (Integer) value;
		}
		else {
			try {
				int intValue = Integer.parseInt(value.toString());
				return intValue;
			} 
			catch (NumberFormatException e) {
				return 0;
			}
		}
	}
	
	public void addValue(Object value) {
		int intValue = parseValue(value);
		rows.add(intValue);
		if (intValue < min) min = intValue;
		if (intValue > max) max = intValue;
	}

	public int getRowCount() {
		return rows.size();
	}

	public void reorder(Collection<Integer> rowOrder) {
		ArrayList<Integer> newRows = new ArrayList<Integer>();
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
		int intValue = parseValue(value);
		rows.set(row, (Integer) intValue);
		if (intValue < min) min = intValue;
		if (intValue > max) max = intValue;
	}

	public void clear() {
		rows.clear();
		min = Integer.MAX_VALUE;
		max = Integer.MIN_VALUE;
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}	
}
