/* ------------------------------------------------------------------
 * BitColumn.java
 * 
 * Created 2009-03-17 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.data;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;

public class BitSetColumn extends BasicColumn {
	
	private ArrayList<BitSet> rows = new ArrayList<BitSet>();
	private int numBits;
	
	public BitSetColumn(String name, int numBits) { 
		super(name, Column.Type.BitSet);
		this.numBits = numBits;
		//TODO: should we default setMeta() here?
	}

	public BitSetColumn(String name, int numBits, boolean isMeta) { 
		this(name, numBits);
		setMeta(isMeta);
	}

	public void addValue(Object value) {
		boolean boolValue = parseValue(value);
		BitSet row = new BitSet(numBits);
		if (boolValue) row.set(0, numBits);
		rows.add(row);
	}
	
	private boolean parseValue(Object value) { 
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		else {
			try {
				boolean boolValue = Boolean.parseBoolean(value.toString());
				return boolValue;
			} 
			catch (NumberFormatException e) {
				return false;
			}
		}
	}

	public void clear() {
		rows.clear();
	}

	public void ensureCapacity(int minRows) {
		while (rows.size() < minRows) { 
			rows.add(null);
		}
	}
	
	public BitSet getBitSetValueAt(int row) { 
		return rows.get(row);
	}
	
	public boolean getBooleanValue(int row) {
		BitSet bset = rows.get(row);
		int nextClearIndex = bset.nextClearBit(0);
		return nextClearIndex >= numBits || nextClearIndex < 0;
	}

	public int getIntValueAt(int row) {
		return getBooleanValue(row) ? 1 : 0;
	}

	public double getRealValueAt(int row) {
		return getBooleanValue(row) ? 1.0 : 0.0;
	}

	public int getRowCount() {
		return rows.size();
	}

	public String getStringValueAt(int row) {
		return "" + getBooleanValue(row);
	}

	public Object getValueAt(int row) {
		return rows.get(row);
	}

	public void reorder(Collection<Integer> rowOrder) {
		ArrayList<BitSet> newRows = new ArrayList<BitSet>();
		reorder(rowOrder, rows, newRows);
		rows = newRows;
	}

	public void setValueAt(int row, Object value) {
		ensureCapacity(row + 1);
		boolean boolValue = parseValue(value);
		BitSet bset = new BitSet(numBits);
		if (boolValue) bset.set(0, numBits - 1);
		rows.set(row, bset);
	}
}
