/* ------------------------------------------------------------------
 * Column.java
 * 
 * Created 2008-10-30 by Niklas Elmqvist <elm@purdue.edu>.
 * ------------------------------------------------------------------
 */
package edu.purdue.timematrix.data;


import java.util.Collection;

public interface Column {
	public enum Type { Integer, Real, String, BitSet };
	public String getName();
	public Type getType();
	public int getIntValueAt(int row);
	public double getRealValueAt(int row);
	public String getStringValueAt(int row);
	public Object getValueAt(int row);
	public void addValue(Object value);
	public int getRowCount();
	public void setValueAt(int row, Object value);
	public void reorder(Collection<Integer> rowOrder);
	public void clear();
	public void ensureCapacity(int minRows);
	public double getMin();
	public double getMax();
	public boolean isMeta();
}
