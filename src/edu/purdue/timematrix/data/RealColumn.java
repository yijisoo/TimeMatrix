/* ------------------------------------------------------------------
 * RealColumn.java
 * 
 * Created 2008-10-30 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.data;

import java.util.ArrayList;
import java.util.Collection;

public class RealColumn extends BasicColumn {

	private double min = Double.NEGATIVE_INFINITY;
	private double max = Double.POSITIVE_INFINITY;
	private ArrayList<Double> rows = new ArrayList<Double>();

	public RealColumn(String name) {
		super(name, Column.Type.Real);
	}

	public Object getValueAt(int row) {
		return rows.get(row);
	}

	public int getIntValueAt(int row) {
		return rows.get(row).intValue();
	}

	public double getRealValueAt(int row) {
		return rows.get(row);
	}

	public String getStringValueAt(int row) {
		return "" + rows.get(row);
	}
	
	private double parseValue(Object value) {
		if (value instanceof Double) {
			return (Double) value;
		}
		else {
			try {
				double doubleValue = Double.parseDouble(value.toString());
				return doubleValue;
			} 
			catch (NumberFormatException e) {
				return 0.0;
			}
		}
	}
	
	public void addValue(Object value) {
		double doubleValue = parseValue(value);
		rows.add(doubleValue);
		if (doubleValue < min) min = doubleValue;
		if (doubleValue > max) max = doubleValue;
	} 
	
	public int getRowCount() {
		return rows.size();
	}

	public void reorder(Collection<Integer> rowOrder) {
		ArrayList<Double> newRows = new ArrayList<Double>();
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
		double doubleValue = parseValue(value);
		rows.set(row,(Double) value);
		if (doubleValue < min) min = doubleValue;
		if (doubleValue > max) max = doubleValue;
	}

	public void clear() {
		rows.clear();
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}		
}
